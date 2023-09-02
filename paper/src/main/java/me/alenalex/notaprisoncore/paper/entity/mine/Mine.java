package me.alenalex.notaprisoncore.paper.entity.mine;

import me.alenalex.notaprisoncore.api.config.entry.BlockEntry;
import me.alenalex.notaprisoncore.api.entity.IEntityDataHolder;
import me.alenalex.notaprisoncore.api.entity.mine.IBlockChoices;
import me.alenalex.notaprisoncore.api.entity.mine.IMine;
import me.alenalex.notaprisoncore.api.entity.mine.IMineVault;
import me.alenalex.notaprisoncore.api.entity.mine.IMineMeta;
import me.alenalex.notaprisoncore.api.enums.MineAccess;
import me.alenalex.notaprisoncore.paper.manager.PrisonManagers;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public class Mine implements IMine {
    private UUID mineId;
    private final UUID ownerId;
    private final UUID metaId;
    private final MineMeta meta;
    private MineAccess mineAccess;
    private final IMineVault mineVault;
    private final BlockChoices blockChoices;

    //TODO Call defaults after initializing this constructor
    public Mine(UUID ownerId, MineMeta meta){
        this.ownerId = ownerId;
        this.meta = meta;
        this.metaId = meta.getMetaId();
        this.mineAccess = MineAccess.CLOSED;
        this.blockChoices = new BlockChoices();
        this.mineVault = new ThreadSafeMineVault();
    }

    public Mine(UUID ownerId, UUID mineId, MineMeta meta) {
        this.ownerId = ownerId;
        this.metaId = meta.getMetaId();
        this.meta = meta;
        this.mineId = mineId;
        this.blockChoices = new BlockChoices();
        this.mineVault = new ThreadSafeMineVault();
    }

    public Mine(UUID ownerId, UUID mineId, MineMeta meta, List<BlockEntry> blockEntryList, BigDecimal account) {
        this.ownerId = ownerId;
        this.metaId = meta.getMetaId();
        this.meta = meta;
        this.mineId = mineId;
        this.blockChoices = new BlockChoices();
        this.blockChoices.addChoices(blockEntryList);
        this.mineVault = new ThreadSafeMineVault(account);
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
    public @NotNull IEntityDataHolder getSharedDataHolder() {
        return null;
    }
    @Override
    public @NotNull IEntityDataHolder getLocalDataHolder() {
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

    public void setDefaults(PrisonManagers prisonManagers){
        this.mineAccess = prisonManagers.configurationManager().getPluginConfiguration().defaultMineConfiguration().getDefaultMineAccess();
        this.blockChoices.clearAndSetDefault();
        this.mineVault.setBalance(new BigDecimal(prisonManagers.configurationManager().getPluginConfiguration().defaultMineConfiguration().getDefaultVaultBalance().toString()));
    }

    private void saveLocalDataHolder(){

    }
}
