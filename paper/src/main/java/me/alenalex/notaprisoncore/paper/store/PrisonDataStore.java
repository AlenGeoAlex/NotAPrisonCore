package me.alenalex.notaprisoncore.paper.store;

import me.alenalex.notaprisoncore.api.store.IPrisonDataStore;
import me.alenalex.notaprisoncore.api.store.IWorldStore;
import me.alenalex.notaprisoncore.paper.NotAPrisonCore;

public class PrisonDataStore implements IPrisonDataStore {
    private final NotAPrisonCore pluginInstance;
    private final WorldStore worldStore;
    public PrisonDataStore(NotAPrisonCore pluginInstance) {
        this.pluginInstance = pluginInstance;
        this.worldStore = new WorldStore(this);
    }

    public void init(){
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
