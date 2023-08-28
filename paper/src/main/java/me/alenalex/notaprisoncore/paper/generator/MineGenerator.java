package me.alenalex.notaprisoncore.paper.generator;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.object.schematic.Schematic;
import com.boydti.fawe.object.visitor.FastIterator;
import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import me.alenalex.notaprisoncore.api.config.entry.MinePositionalKeys;
import me.alenalex.notaprisoncore.api.entity.mine.IMineMeta;
import me.alenalex.notaprisoncore.api.entity.mine.MinePositionalKey;
import me.alenalex.notaprisoncore.api.exceptions.FailedMineGenerationException;
import me.alenalex.notaprisoncore.api.exceptions.NoSchematicFound;
import me.alenalex.notaprisoncore.api.generator.IMineGenerator;
import me.alenalex.notaprisoncore.api.managers.ISchematicFileManager;
import me.alenalex.notaprisoncore.paper.entity.mine.MineMeta;
import me.alenalex.notaprisoncore.paper.manager.mine.MineManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.CommandSender;
import org.bukkit.material.Directional;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class MineGenerator implements IMineGenerator {

    private final MineManager mineManager;

    public MineGenerator(MineManager mineManager) {
        this.mineManager = mineManager;
    }

    @Override
    public Optional<IMineMeta> generateMine(CommandSender requester, String schematicName) throws NoSchematicFound {
        ISchematicFileManager schematicFileManager = this.mineManager.getPlugin().getPrisonManagers().schematicManager().getSchematicFileManager();
        Optional<File> optionalSchematicFile = schematicFileManager.getSchematicFileOfName(schematicName);
        if(!optionalSchematicFile.isPresent()){
            throw new NoSchematicFound("The schematic with the name "+schematicName+" was not found in the plugin cache");
        }
        File schematicFile = optionalSchematicFile.get();
        @NotNull Optional<?> schematicOptional = this.mineManager.getPlugin().getPrisonManagers().schematicManager().getHookedSchematicProvider().getSchematic(schematicFile);
        if(!schematicOptional.isPresent()){
            throw new NoSchematicFound("Failed to load schematic with the name "+schematicName);
        }
        Schematic schematic = (Schematic) schematicOptional.get();
        Clipboard clipboard = schematic.getClipboard();
        if(clipboard == null){
            throw new NoSchematicFound("Failed to get the schematic with the name "+schematicName+" from world edit");
        }
        MinePositionalKeys mineIdentifiers = this.mineManager.getManagers().configurationManager().getMineIdentifierConfiguration()
                .ofMine(schematicName)
                .orElse(null);

        if(mineIdentifiers == null){
            throw new FailedMineGenerationException("Mine Identifiers are missing for the mine. Strict fields like spawn-point is missing. Aborting");
        }
        Location location = this.mineManager.getPlugin().getPrisonDataStore().worldStore().nextFreeLocation();
        World mineWorld = this.mineManager.getPlugin().getPrisonManagers().worldManager().getMineWorld();
        location.setY(clipboard.getOrigin().getY());
        Vector centerVector = BukkitUtil.toVector(location);
        com.sk89q.worldedit.world.World worldEditWorldWrapper = FaweAPI.getWorld(mineWorld.getName());
        final EditSession editSession = new EditSessionBuilder(worldEditWorldWrapper).fastmode(true).checkMemory(true).limitUnlimited().build();

        MineMeta meta = null;
        Location spawnPoint = null;
        Location lowerMiningPoint = null;
        Location upperMiningPoint = null;
        HashMap<String, Location> locationalKeyMap = new HashMap<>();
        try {
            schematic.paste(editSession, centerVector, true, false, null);
            mineManager.getPlugin().getLogger().info("Successfully pasted mine at ["+centerVector.getX()+", "+centerVector.getY()+", "+centerVector.getZ()+"]");

            Region region = clipboard.getRegion();
            region.setWorld(worldEditWorldWrapper);
            try {
                region.shift(centerVector.subtract(clipboard.getOrigin()));
            }
            catch (RegionOperationException e) {
                e.printStackTrace();
                return Optional.empty();
            }
            this.mineManager.getManagers().worldManager().loadChunkAt(new Location(mineWorld, centerVector.getX(), centerVector.getY(), centerVector.getZ()), 30);
            mineManager.getPlugin().getLogger().info("Starting to gather meta-data for mines");
            for (Vector next : new FastIterator(region, editSession)) {
                BaseBlock block = worldEditWorldWrapper.getBlock(next);
                int type = block.getType();
                if (type <= 0) continue;
                Material materialAt = Material.getMaterial(type);
                MinePositionalKey positionalKey = matchByMaterial(mineIdentifiers, materialAt, schematicName);
                if (positionalKey == null)
                    continue;

                Location positionalLocation = readFromVector(positionalKey, next, mineWorld);
                switch (positionalKey.getKey()) {
                    case "spawn-point":
                        spawnPoint = positionalLocation;
                        break;
                    case "lower-mine-corner":
                        lowerMiningPoint = positionalLocation;
                        break;
                    case "upper-mine-corner":
                        upperMiningPoint = positionalLocation;
                        break;
                    default: {
                        locationalKeyMap.put(positionalKey.getKey(), positionalLocation);
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            EditSession undoSession = new EditSessionBuilder(worldEditWorldWrapper).fastmode(true).checkMemory(true).limitUnlimited().build();
            try {
                editSession.undo(undoSession);
            }catch (Exception undoException){
                e.printStackTrace();
            }finally {
                undoSession.flushQueue();
            }
        }finally {
            editSession.flushQueue();
        }

        if(spawnPoint == null || upperMiningPoint == null || lowerMiningPoint == null){
            throw new FailedMineGenerationException("Failed to identify spawn-point or lower-mine-corner or upper-mine-corner");
        }

        List<MinePositionalKey> requiredKeys = mineIdentifiers.stream().filter(MinePositionalKey::isRequired).collect(Collectors.toList());
        boolean hasAllRequired = true;
        String missingKey = null;
        for (MinePositionalKey requiredKey : requiredKeys) {
            String key = requiredKey.getKey();
            if(key.equals("upper-mine-corner") || key.equals("lower-mine-corner") || key.equals("spawn-point"))
               continue;

            if(!locationalKeyMap.containsKey(key)){
                hasAllRequired = false;
                missingKey = key;
                break;
            }
        }

        if(!hasAllRequired){
            throw new FailedMineGenerationException("Failed to find the required block identifier "+missingKey+" while pasting schematic "+schematicName);
        }

        meta = new MineMeta(clipboard.getRegion(), lowerMiningPoint, upperMiningPoint, spawnPoint, locationalKeyMap);
        return Optional.of(meta);
    }

    private Location readFromVector(MinePositionalKey key, Vector vector, World world){
        Location location = new Location(world, vector.getX(), vector.getY(), vector.getZ());
        if(key.isReadDirection()){
            Block block = location.getBlock();
            if(block.getState().getData() instanceof Directional){
                location.setYaw(getYaw(((Directional) block.getState().getData()).getFacing()));
            }else{
                location.setYaw(key.getDefaultYaw());
            }
        }
        return location;
    }

    private Float getYaw(BlockFace face) {
        switch (face) {
            case WEST:
                return Float.valueOf(90.0F);
            case NORTH:
                return Float.valueOf(180.0F);
            case EAST:
                return Float.valueOf(-90.0F);
            case SOUTH:
                return Float.valueOf(-180.0F);
        }
        return Float.valueOf(0.0F);
    }
    private MinePositionalKey matchByMaterial(MinePositionalKeys minePositionalKeys, Material material, String schematicName){
        return minePositionalKeys.stream().filter(x -> x.getIdentifier() == material).findAny().orElse(null);
    }

    @Override
    public CompletableFuture<Collection<IMineMeta>> generateMines(CommandSender requester, String schematicName, int generationCount, long coolDownInterval) {
        CompletableFuture<Collection<IMineMeta>> future = new CompletableFuture<>();
        List<IMineMeta> mineMetas = new ArrayList<>();
        AtomicInteger count = new AtomicInteger(0);
        BukkitRunnable runnable = new BukkitRunnable() {
            @Override
            public void run() {
                if(count.get() >= generationCount){
                    future.complete(mineMetas);
                    if(!this.isCancelled())
                        this.cancel();
                    return;
                }
                try {
                    Optional<IMineMeta> metaOptional = generateMine(requester, schematicName);
                    IMineMeta meta = metaOptional.orElse(null);
                    if(meta == null){
                        future.completeExceptionally(new FailedMineGenerationException());
                        if(!this.isCancelled())
                            this.cancel();
                        return;
                    }

                    mineMetas.add(meta);
                } catch (Exception e) {
                    future.completeExceptionally(e);
                    if(!this.isCancelled())
                        this.cancel();
                    return;
                }
                count.incrementAndGet();
            }
        };
        runnable.runTaskTimer(this.mineManager.getPlugin().getBukkitPlugin(), 0L, coolDownInterval);
        return future;
    }
}
