package me.alenalex.notaprisoncore.paper.store;

import me.alenalex.notaprisoncore.api.store.IPrisonDataStore;
import me.alenalex.notaprisoncore.api.store.IWorldStore;
import me.alenalex.notaprisoncore.paper.NotAPrisonCore;
import me.alenalex.notaprisoncore.paper.bootstrap.Bootstrap;

import java.io.File;

public class PrisonDataStore implements IPrisonDataStore {
    private final NotAPrisonCore pluginInstance;
    private final WorldStore worldStore;
    private final File storeParentDirectory;
    public PrisonDataStore(NotAPrisonCore pluginInstance) {
        this.pluginInstance = pluginInstance;
        this.worldStore = new WorldStore(this);
        this.storeParentDirectory = new File(Bootstrap.getJavaPlugin().getDataFolder(), "store");
    }

    public void init(){
        if(!storeParentDirectory.exists()){
            storeParentDirectory.mkdirs();
        }
        this.worldStore.initStore();
    }

    public void load(){
        this.worldStore.load();
    }

    public void disable(){

    }

    @Override
    public IWorldStore worldStore() {
        return worldStore;
    }

    public NotAPrisonCore getPluginInstance() {
        return pluginInstance;
    }
}
