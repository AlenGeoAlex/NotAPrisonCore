package me.alenalex.notaprisoncore.paper.data;

import me.alenalex.notaprisoncore.api.data.IDataHolder;
import me.alenalex.notaprisoncore.api.data.IMineMetaDataHolder;
import me.alenalex.notaprisoncore.paper.NotAPrisonCore;

public class DataHolder implements IDataHolder {

    private final NotAPrisonCore plugin;
    private final MineMetaDataHolder mineMetaDataHolder;

    public DataHolder(NotAPrisonCore plugin) {
        this.plugin = plugin;
        this.mineMetaDataHolder = new MineMetaDataHolder(this);
    }

    public void onEnable(){
        this.mineMetaDataHolder.onEnable();
    }

    public void onDisable(){
        this.mineMetaDataHolder.onDisable();
    }

    @Override
    public IMineMetaDataHolder mineMetaDataHolder() {
        return mineMetaDataHolder;
    }

    public NotAPrisonCore getPlugin() {
        return plugin;
    }
}
