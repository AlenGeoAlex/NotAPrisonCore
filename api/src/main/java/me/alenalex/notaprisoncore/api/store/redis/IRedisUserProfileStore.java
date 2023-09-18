package me.alenalex.notaprisoncore.api.store.redis;

import me.alenalex.notaprisoncore.api.common.Pair;
import me.alenalex.notaprisoncore.api.entity.mine.IMine;
import me.alenalex.notaprisoncore.api.entity.user.IPrisonUserProfile;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IRedisUserProfileStore {
    CompletableFuture<Void> setUserOnSwitch(IPrisonUserProfile userProfile);
    CompletableFuture<Boolean> isUserOnSwitch(UUID uuid);
    CompletableFuture<Void> setUserData(IPrisonUserProfile userProfile, IMine iMine);
    CompletableFuture<Pair<IPrisonUserProfile, IMine>> getUserData(UUID playerId);
}
