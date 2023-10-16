package me.alenalex.notaprisoncore.api.store.redis;

import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IRedisUserSocialStore {

    void setName(@NotNull UUID uuid, @NotNull String name);
    CompletableFuture<Optional<String>> getName(@NotNull UUID uuid);

}
