package me.alenalex.notaprisoncore.api.store.redis;

import me.alenalex.notaprisoncore.api.entity.user.IPrisonUserProfile;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IRedisUserProfileStore {
    CompletableFuture<Void> setUserOnSwitch(IPrisonUserProfile userProfile);
    CompletableFuture<Boolean> isUserOnSwitch(UUID uuid);
}
