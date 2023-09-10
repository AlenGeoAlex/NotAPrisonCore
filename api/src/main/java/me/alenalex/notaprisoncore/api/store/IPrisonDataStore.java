package me.alenalex.notaprisoncore.api.store;

import me.alenalex.notaprisoncore.api.store.redis.IRedisMineStore;

public interface IPrisonDataStore {
    IWorldStore worldStore();
    IMineMetaStore mineMetaStore();
    IMineStore mineStore();
    IRedisMineStore redisMineStore();
}


