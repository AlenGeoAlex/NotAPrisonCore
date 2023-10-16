package me.alenalex.notaprisoncore.api.store;

import me.alenalex.notaprisoncore.api.store.redis.IRedisMineStore;
import me.alenalex.notaprisoncore.api.store.redis.IRedisUserProfileStore;
import me.alenalex.notaprisoncore.api.store.redis.IRedisUserSocialStore;

public interface IPrisonDataStore {
    IWorldStore getWorldStore();
    IMineMetaStore getMineMetaStore();
    IMineStore getMineStore();
    IRedisMineStore getRedisMineStore();
    IUserSocialStore getUserSocialStore();
    IUserProfileStore getUserProfileStore();
    IRedisUserProfileStore getRedisUserProfileStore();

    IRedisUserSocialStore getRedisUserSocialStore();
}


