package me.alenalex.notaprisoncore.paper.store;

import me.alenalex.notaprisoncore.api.store.*;
import me.alenalex.notaprisoncore.api.store.redis.IRedisMineStore;
import me.alenalex.notaprisoncore.api.store.redis.IRedisUserProfileStore;
import me.alenalex.notaprisoncore.api.store.redis.IRedisUserSocialStore;
import me.alenalex.notaprisoncore.paper.NotAPrisonCore;
import me.alenalex.notaprisoncore.paper.bootstrap.Bootstrap;
import me.alenalex.notaprisoncore.paper.store.redis.RedisMineStore;
import me.alenalex.notaprisoncore.paper.store.redis.RedisUserProfileStore;
import me.alenalex.notaprisoncore.paper.store.redis.RedisUserSocialStore;

import java.io.File;

public class PrisonDataStore implements IPrisonDataStore {
    private final NotAPrisonCore pluginInstance;
    private final WorldStore worldStore;
    private final MineMetaStore mineMetaStore;
    private final MineStore mineStore;
    private final RedisMineStore redisMineStore;
    private final UserSocialStore userSocialStore;
    private final RedisUserProfileStore redisUserProfileStore;
    private final UserProfileStore userProfileStore;
    private final RedisUserSocialStore redisUserSocialStore;
    private final File storeParentDirectory;
    public PrisonDataStore(NotAPrisonCore pluginInstance) {
        this.pluginInstance = pluginInstance;
        this.storeParentDirectory = new File(Bootstrap.getJavaPlugin().getDataFolder(), "store");
        this.worldStore = new WorldStore(this);
        this.mineMetaStore = new MineMetaStore(this);
        this.mineStore = new MineStore(this);
        this.redisMineStore = new RedisMineStore(this);
        this.userSocialStore = new UserSocialStore(this);
        this.redisUserProfileStore = new RedisUserProfileStore(this);
        this.userProfileStore = new UserProfileStore(this);
        this.redisUserSocialStore = new RedisUserSocialStore(this);
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
    public IWorldStore getWorldStore() {
        return worldStore;
    }

    @Override
    public IMineMetaStore getMineMetaStore() {
        return mineMetaStore;
    }

    @Override
    public IMineStore getMineStore() {
        return mineStore;
    }

    @Override
    public IRedisMineStore getRedisMineStore() {
        return redisMineStore;
    }

    @Override
    public IUserSocialStore getUserSocialStore() {
        return userSocialStore;
    }

    @Override
    public IUserProfileStore getUserProfileStore() {
        return userProfileStore;
    }

    @Override
    public IRedisUserProfileStore getRedisUserProfileStore() {
        return redisUserProfileStore;
    }

    @Override
    public IRedisUserSocialStore getRedisUserSocialStore(){
        return this.redisUserSocialStore;
    }

    public NotAPrisonCore getPluginInstance() {
        return pluginInstance;
    }

    public File getStoreParentDirectory() {
        return storeParentDirectory;
    }
}
