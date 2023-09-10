package me.alenalex.notaprisoncore.paper.store;

import me.alenalex.notaprisoncore.api.store.IMineMetaStore;
import me.alenalex.notaprisoncore.api.store.IMineStore;
import me.alenalex.notaprisoncore.api.store.IPrisonDataStore;
import me.alenalex.notaprisoncore.api.store.IWorldStore;
import me.alenalex.notaprisoncore.api.store.redis.IRedisMineStore;
import me.alenalex.notaprisoncore.paper.NotAPrisonCore;
import me.alenalex.notaprisoncore.paper.bootstrap.Bootstrap;
import me.alenalex.notaprisoncore.paper.store.redis.RedisMineStore;

import java.io.File;

public class PrisonDataStore implements IPrisonDataStore {
    private final NotAPrisonCore pluginInstance;
    private final WorldStore worldStore;
    private final MineMetaStore mineMetaStore;
    private final MineStore mineStore;
    private final RedisMineStore redisMineStore;
    private final File storeParentDirectory;
    public PrisonDataStore(NotAPrisonCore pluginInstance) {
        this.pluginInstance = pluginInstance;
        this.storeParentDirectory = new File(Bootstrap.getJavaPlugin().getDataFolder(), "store");
        this.worldStore = new WorldStore(this);
        this.mineMetaStore = new MineMetaStore(this);
        this.mineStore = new MineStore(this);
        this.redisMineStore = new RedisMineStore(this);

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
        this.mineMetaStore.save();
    }

    @Override
    public IWorldStore worldStore() {
        return worldStore;
    }

    @Override
    public IMineMetaStore mineMetaStore() {
        return mineMetaStore;
    }

    @Override
    public IMineStore mineStore() {
        return mineStore;
    }

    @Override
    public IRedisMineStore redisMineStore() {
        return redisMineStore;
    }

    public NotAPrisonCore getPluginInstance() {
        return pluginInstance;
    }

    public File getStoreParentDirectory() {
        return storeParentDirectory;
    }
}
