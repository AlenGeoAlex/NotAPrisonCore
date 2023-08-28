package me.alenalex.notaprisoncore.paper.entity.mine;

import me.alenalex.notaprisoncore.api.config.entry.BlockEntry;
import me.alenalex.notaprisoncore.api.entity.IEntityDataHolder;
import me.alenalex.notaprisoncore.api.entity.mine.IBlockChoices;
import me.alenalex.notaprisoncore.api.entity.mine.IMine;
import me.alenalex.notaprisoncore.api.entity.mine.IMineMeta;
import me.alenalex.notaprisoncore.api.enums.MineAccess;
import me.alenalex.notaprisoncore.paper.manager.PrisonManagers;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class Mine implements IMine {

    private final UUID ownerId;
    private final UUID metaId;
    private final UUID mineId;
    private final MineMeta meta;
    private MineAccess mineAccess;
    private final BlockChoices blockChoices;

    public Mine(UUID ownerId, UUID mineId, MineMeta meta) {
        this.ownerId = ownerId;
        this.metaId = meta.getMetaId();
        this.meta = meta;
        this.mineId = mineId;
        this.blockChoices = new BlockChoices();
    }

    public Mine(UUID ownerId, UUID mineId, MineMeta meta, List<BlockEntry> blockEntryList) {
        this.ownerId = ownerId;
        this.metaId = meta.getMetaId();
        this.meta = meta;
        this.mineId = mineId;
        this.blockChoices = new BlockChoices();
        this.blockChoices.addChoices(blockEntryList);
    }

    @Override
    public UUID getId() {
        return mineId;
    }
    @Override
    public UUID getMetaId() {
        return metaId;
    }
    @Override
    public IMineMeta getMeta() {
        return meta;
    }
    @Override
    public IEntityDataHolder getSharedDataHolder() {
        return null;
    }
    @Override
    public IEntityDataHolder getLocalDataHolder() {
        return null;
    }
    @Override
    public MineAccess access() {
        return this.mineAccess;
    }
    @Override
    public UUID getOwnerId() {
        return ownerId;
    }

    @Override
    public IBlockChoices getBlockChoices() {
        return blockChoices;
    }

    @Override
    public MineAccess access(MineAccess access) {
        this.mineAccess = access;
        return access;
    }

    @Override
    public void teleport(Player player) {
        Location spawnPoint = this.meta.getSpawnPoint();
        player.teleport(spawnPoint);
    }
    public void setDefaults(PrisonManagers prisonManagers){
        this.mineAccess = prisonManagers.configurationManager().getPluginConfiguration().defaultMineConfiguration().getDefaultMineAccess();
        this.blockChoices.clearAndSetDefault();
    }
}
