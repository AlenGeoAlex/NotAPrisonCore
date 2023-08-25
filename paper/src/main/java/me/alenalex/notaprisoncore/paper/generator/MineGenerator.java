package me.alenalex.notaprisoncore.paper.generator;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.object.schematic.Schematic;
import com.boydti.fawe.util.EditSessionBuilder;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.regions.RegionOperationException;
import me.alenalex.notaprisoncore.api.entity.mine.IMineMeta;
import me.alenalex.notaprisoncore.api.exceptions.FailedMineGenerationException;
import me.alenalex.notaprisoncore.api.exceptions.NoSchematicFound;
import me.alenalex.notaprisoncore.api.generator.IMineGenerator;
import me.alenalex.notaprisoncore.api.managers.ISchematicFileManager;
import me.alenalex.notaprisoncore.paper.manager.MineManager;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

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
        Location location = this.mineManager.getPlugin().getPrisonDataStore().worldStore().nextFreeLocation();
        World mineWorld = this.mineManager.getPlugin().getPrisonManagers().worldManager().getMineWorld();
        location.setY(clipboard.getOrigin().getY());
        Vector centerVector = BukkitUtil.toVector(location);
        com.sk89q.worldedit.world.World worldEditWorldWrapper = FaweAPI.getWorld(mineWorld.getName());
        final EditSession editSession = new EditSessionBuilder(worldEditWorldWrapper).fastmode(true).checkMemory(true).limitUnlimited().build();

        try {
            schematic.paste(editSession, centerVector, true, false, null);
            mineManager.getPlugin().getLogger().info("Successfully pasted mine at ["+centerVector.getX()+", "+centerVector.getY()+", "+centerVector.getZ()+"]");
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

        Region region = clipboard.getRegion();
        region.setWorld(worldEditWorldWrapper);
        try {
            region.shift(centerVector.subtract(clipboard.getOrigin()));
        }
        catch (RegionOperationException e) {
            e.printStackTrace();
            return Optional.empty();
        }
        mineManager.getPlugin().getLogger().info("Starting to gather meta-data for mines");
        return null;
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
                    this.cancel();
                    future.complete(mineMetas);
                    return;
                }
                try {
                    Optional<IMineMeta> metaOptional = generateMine(requester, schematicName);
                    IMineMeta meta = metaOptional.orElse(null);
                    if(meta == null){
                        this.cancel();
                        future.completeExceptionally(new FailedMineGenerationException());
                        return;
                    }

                    mineMetas.add(meta);
                } catch (NoSchematicFound e) {
                    this.cancel();
                    future.completeExceptionally(e);
                    return;
                }
                count.incrementAndGet();
            }
        };

        this.mineManager.getPlugin().getBukkitPlugin().getServer().getScheduler().runTaskTimer(this.mineManager.getPlugin().getBukkitPlugin(), runnable, 0L, coolDownInterval);
        return future;
    }
}
