package me.alenalex.notaprisoncore.paper.entity.mine;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.config.entry.BlockEntry;
import me.alenalex.notaprisoncore.api.entity.IEntityMetaDataHolder;
import me.alenalex.notaprisoncore.api.entity.mine.*;
import me.alenalex.notaprisoncore.api.enums.MineAccess;
import me.alenalex.notaprisoncore.api.events.mine.MineExpandEvent;
import me.alenalex.notaprisoncore.api.events.mine.PreMineExpandEvent;
import me.alenalex.notaprisoncore.api.exceptions.mine.InvalidMineException;
import me.alenalex.notaprisoncore.paper.bootstrap.Bootstrap;
import me.alenalex.notaprisoncore.paper.constants.MineConstants;
import me.alenalex.notaprisoncore.paper.entity.dataholder.LocalEntityMetaDataHolder;
import me.alenalex.notaprisoncore.paper.entity.dataholder.SharedEntityMetaDataHolder;
import me.alenalex.notaprisoncore.paper.manager.PrisonManagers;
import me.alenalex.notaprisoncore.paper.store.MineStore;
import me.alenalex.notaprisoncore.paper.wrapper.GsonWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ToString
@EqualsAndHashCode
public class Mine implements IMine {

    private UUID mineId;
    private final UUID ownerId;
    private final UUID metaId;
    private final MineMeta meta;
    private MineAccess mineAccess;
    private final ThreadSafeMineVault mineVault;
    private final BlockChoices blockChoices;
    private final MineResetter mineResetter;
    private final LocalEntityMetaDataHolder localEntityMetaDataHolder;
    private final SharedEntityMetaDataHolder sharedEntityMetaDataHolder;

    //TODO Call defaults after initializing this constructor
    public Mine(UUID ownerId, MineMeta meta){
        this.ownerId = ownerId;
        this.meta = meta;
        this.metaId = meta.getMetaId();
        this.mineAccess = MineAccess.CLOSED;
        this.blockChoices = new BlockChoices();
        this.mineVault = new ThreadSafeMineVault();
        this.mineResetter = new MineResetter(this.blockChoices, this.meta);
        this.localEntityMetaDataHolder = new LocalEntityMetaDataHolder();
        this.sharedEntityMetaDataHolder = new SharedEntityMetaDataHolder();
    }

    public Mine(UUID ownerId, UUID mineId, MineMeta meta) {
        this.ownerId = ownerId;
        this.metaId = meta.getMetaId();
        this.meta = meta;
        this.mineId = mineId;
        this.blockChoices = new BlockChoices();
        this.mineVault = new ThreadSafeMineVault();
        this.mineResetter = new MineResetter(this.blockChoices, this.meta);
        this.localEntityMetaDataHolder = new LocalEntityMetaDataHolder();
        this.sharedEntityMetaDataHolder = new SharedEntityMetaDataHolder();
    }

    public Mine(UUID ownerId, UUID mineId, MineMeta meta, BigDecimal amount, LocalEntityMetaDataHolder localMeta, SharedEntityMetaDataHolder sharedMeta) {
        this.ownerId = ownerId;
        this.metaId = meta.getMetaId();
        this.meta = meta;
        this.mineId = mineId;
        this.blockChoices = new BlockChoices();
        this.mineVault = new ThreadSafeMineVault(amount);
        this.mineResetter = new MineResetter(this.blockChoices, this.meta);
        this.localEntityMetaDataHolder = localMeta;
        this.sharedEntityMetaDataHolder = sharedMeta;
    }

    //From Gson Deserializer
    public Mine(UUID ownerId, UUID mineId, MineMeta meta, List<BlockEntry> blockEntryList, BigDecimal account, SharedEntityMetaDataHolder sharedMeta) {
        this.ownerId = ownerId;
        this.metaId = meta.getMetaId();
        this.meta = meta;
        this.mineId = mineId;
        this.blockChoices = new BlockChoices();
        this.blockChoices.addChoices(blockEntryList);
        this.mineVault = new ThreadSafeMineVault(account);
        this.mineResetter = new MineResetter(this.blockChoices, this.meta);
        this.localEntityMetaDataHolder = new LocalEntityMetaDataHolder();
        this.sharedEntityMetaDataHolder = sharedMeta;
    }

    @Override
    public @Nullable UUID getId() {
        return mineId;
    }
    @Override
    public @NotNull UUID getMetaId() {
        return metaId;
    }
    @Override
    public @NotNull IMineMeta getMeta() {
        return meta;
    }
    @Override
    public @NotNull IEntityMetaDataHolder getSharedMetaDataHolder() {
        return this.sharedEntityMetaDataHolder;
    }
    @Override
    public @NotNull IEntityMetaDataHolder getLocalMetaDataHolder() {
        return this.localEntityMetaDataHolder;
    }
    @Override
    public @NotNull MineAccess access() {
        return this.mineAccess;
    }
    @Override
    public @NotNull UUID getOwnerId() {
        return ownerId;
    }
    @Override
    public @NotNull IBlockChoices getBlockChoices() {
        return blockChoices;
    }

    @Override
    public @NotNull IMineVault getVault() {
        return this.mineVault;
    }

    @Override
    public IMineResetter getMineResetter() {
        return this.mineResetter;
    }

    @Override
    @NotNull
    public MineAccess access(MineAccess access) {
        this.mineAccess = access;
        return access;
    }

    @Override
    public void teleport(Player player) {
        this.teleport(player, false);
    }

    @Override
    public void teleport(Player player, boolean overrideAccess) {
        if(!overrideAccess){

        }
        player.teleport(this.meta.getSpawnPoint());
    }

    @Override
    public boolean teleport(Player player, String identifierKey){
        return teleport(player, identifierKey, false);
    }

    @Override
    public boolean teleport(Player player, String identifierKey, boolean overrideAccess){
        if(!overrideAccess){

        }
        Location location = null;

        switch (identifierKey){
            case "spawn-point":
                location = this.meta.getSpawnPoint();
                break;
            case "lower-mine-corner":
                location = this.meta.getLowerMiningPoint();
                break;
            case "upper-mine-corner":
                location = this.meta.getUpperMiningPoint();
                break;
            default:
                location = this.meta.getLocationOfIdentifier(identifierKey).orElse(null);
        }

        if(location == null)
            return false;

        player.teleport(location);
        return true;
    }



    @Override
    public void save() {

    }

    public void setMineId(UUID id){
        this.mineId = id;
    }

    @Override
    public boolean isValid(){
        return mineId != null;
    }

    @Override
    public boolean isInvalid(){
        return mineId == null;
    }

    @Override
    public CompletableFuture<Boolean> loadLocalMetaDataAsync() {
        if(this.isInvalid())
            throw new InvalidMineException("Tried to access local data holder for an invalid mine. Meta Id "+metaId.toString()+" owner id "+ownerId.toString() );
        return CompletableFuture.supplyAsync(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                Bootstrap plugin = (Bootstrap) Bootstrap.getJavaPlugin();
                MineStore mineStore = (MineStore) plugin.getPluginInstance().getPrisonDataStore().mineStore();
                File file = null;
                try {
                    file = mineStore.getOrCreate(mineId.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }

                if(file == null)
                    return false;
                String base64String = null;
                try (Stream<String> lines = Files.lines(file.toPath())) {
                    base64String = lines.collect(Collectors.joining(System.lineSeparator()));
                }catch (Exception e){
                    e.printStackTrace();
                    return false;
                }

                ((LocalEntityMetaDataHolder) localEntityMetaDataHolder).setHolderData(base64String);
                return true;
            }
        });
    }

    @Override
    public CompletableFuture<Boolean> saveLocalMetaDataAsync() {
        if(this.isInvalid())
            throw new InvalidMineException("Tried to access local data holder for an invalid mine. Meta Id "+metaId.toString()+" owner id "+ownerId.toString() );

        return CompletableFuture.supplyAsync(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                Bootstrap plugin = (Bootstrap) Bootstrap.getJavaPlugin();
                MineStore mineStore = (MineStore) plugin.getPluginInstance().getPrisonDataStore().mineStore();
                File file = null;
                try {
                    file = mineStore.getOrCreate(mineId.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                try (final FileWriter fileWriter = new FileWriter(file, false)) {
                    fileWriter.write(((LocalEntityMetaDataHolder) getLocalMetaDataHolder()).encode());
                } catch (IOException e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
                return true;
            }
        });
    }

    @Override
    public void sendPluginNotification(String message) {
        Player player = Bukkit.getPlayer(ownerId);
        if(player == null || !player.isOnline())
            return;

        player.sendMessage(message);
    }

    @Override
    public CompletableFuture<Void> expandMiningRegion(Vector min, Vector max) {
        PreMineExpandEvent mineExpandEvent = new PreMineExpandEvent(false, this);
        if(mineExpandEvent.isCancelled()){
            return CompletableFuture.completedFuture(null);
        }

        CompletableFuture<Void> future = new CompletableFuture<>();
        Location lowerMiningPoint = this.meta.getLowerMiningPoint();
        Vector lowerVector = new Vector(lowerMiningPoint.getBlockX(), lowerMiningPoint.getBlockY(), lowerMiningPoint.getBlockZ());
        this.localEntityMetaDataHolder.set(MineConstants.KEY_LOCAL_LOWER_MINING_VECTOR, GsonWrapper.singleton().gson().toJson(lowerVector));
        Location upperMiningPoint = this.meta.getLowerMiningPoint();
        Vector upperVector = new Vector(upperMiningPoint.getBlockX(), upperMiningPoint.getBlockY(), upperMiningPoint.getBlockZ());
        this.localEntityMetaDataHolder.set(MineConstants.KEY_LOCAL_UPPER_MINING_VECTOR, GsonWrapper.singleton().gson().toJson(upperVector));
        CompletableFuture<Boolean> saveFuture = saveLocalMetaDataAsync();
        //TODO Send message to others

        IMine me = this;
        CompletableFuture.allOf(saveFuture).thenRun(new Runnable() {
            @Override
            public void run() {
                MineExpandEvent mineExpandEvent = new MineExpandEvent(true, me);
                Bukkit.getServer().getPluginManager().callEvent(mineExpandEvent);
            }
        });

        return future;
    }


    public void setDefaults(PrisonManagers prisonManagers){
        this.mineAccess = prisonManagers.configurationManager().getPluginConfiguration().defaultMineConfiguration().getDefaultMineAccess();
        this.blockChoices.clearAndSetDefault();
        this.mineVault.setBalance(new BigDecimal(prisonManagers.configurationManager().getPluginConfiguration().defaultMineConfiguration().getDefaultVaultBalance().toString()));
    }

}
