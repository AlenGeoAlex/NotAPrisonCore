package me.alenalex.notaprisoncore.paper.data;

import me.alenalex.notaprisoncore.api.data.IMineDataHolder;
import me.alenalex.notaprisoncore.api.entity.mine.IMine;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class MineDataHolder implements IMineDataHolder {
    private final DataHolder dataHolder;
    private final ConcurrentHashMap<UUID, IMine> mineMap;
    public MineDataHolder(DataHolder dataHolder) {
        this.dataHolder = dataHolder;
        this.mineMap = new ConcurrentHashMap<>();
    }

    public void load(@NotNull IMine mine){
        this.mineMap.put(mine.getId(), mine);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }
}
