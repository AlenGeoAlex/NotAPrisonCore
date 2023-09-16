package me.alenalex.notaprisoncore.api.store;

import me.alenalex.notaprisoncore.api.store.redis.IRedisMineStore;
import me.alenalex.notaprisoncore.api.store.redis.IRedisUserProfileStore;

public interface IPrisonDataStore {
    IWorldStore worldStore();
    IMineMetaStore mineMetaStore();
    IMineStore mineStore();
    IRedisMineStore redisMineStore();
    IUserSocialStore userSocialStore();
    IUserProfileStore userProfileStore();
    IRedisUserProfileStore redisUserProfileStore();
}


