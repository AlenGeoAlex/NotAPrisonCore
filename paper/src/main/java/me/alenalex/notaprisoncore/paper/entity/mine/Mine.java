package me.alenalex.notaprisoncore.paper.entity.mine;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.config.entry.BlockEntry;
import me.alenalex.notaprisoncore.api.entity.IEntityMetaDataHolder;
import me.alenalex.notaprisoncore.api.entity.mine.*;
import me.alenalex.notaprisoncore.api.enums.MineAccess;
import me.alenalex.notaprisoncore.paper.entity.dataholder.LocalEntityMetaDataHolder;
import me.alenalex.notaprisoncore.paper.entity.dataholder.SharedEntityMetaDataHolder;
import me.alenalex.notaprisoncore.paper.manager.PrisonManagers;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

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

    public Mine(UUID ownerId, UUID mineId, MineMeta meta, List<BlockEntry> blockEntryList, BigDecimal account) {
        this.ownerId = ownerId;
        this.metaId = meta.getMetaId();
        this.meta = meta;
        this.mineId = mineId;
        this.blockChoices = new BlockChoices();
        this.blockChoices.addChoices(blockEntryList);
        this.mineVault = new ThreadSafeMineVault(account);
        this.mineResetter = new MineResetter(this.blockChoices, this.meta);
        this.localEntityMetaDataHolder = new LocalEntityMetaDataHolder();
        this.sharedEntityMetaDataHolder = new SharedEntityMetaDataHolder();
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
        return null;
    }
    @Override
    public @NotNull IEntityMetaDataHolder getLocalMetaDataHolder() {
        return null;
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
        player.teleport(this.meta.getSpawnPoint());
    }

    @Override
    public void save() {

    }

    public boolean isValid(){
        return mineId != null;
    }

    @Override
    public IMine refresh() {
        return null;
    }

    @Override
    public void sendPluginNotification(String message) {
        Player player = Bukkit.getPlayer(ownerId);
        if(player == null || !player.isOnline())
            return;

        player.sendMessage(message);
    }


    public void setDefaults(PrisonManagers prisonManagers){
        this.mineAccess = prisonManagers.configurationManager().getPluginConfiguration().defaultMineConfiguration().getDefaultMineAccess();
        this.blockChoices.clearAndSetDefault();
        this.mineVault.setBalance(new BigDecimal(prisonManagers.configurationManager().getPluginConfiguration().defaultMineConfiguration().getDefaultVaultBalance().toString()));
    }

    private void saveLocalDataHolder(){

    }
}
