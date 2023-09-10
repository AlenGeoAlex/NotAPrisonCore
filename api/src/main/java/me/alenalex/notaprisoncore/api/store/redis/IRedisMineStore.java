package me.alenalex.notaprisoncore.api.store.redis;

import me.alenalex.notaprisoncore.api.entity.mine.IMine;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IRedisMineStore {
    CompletableFuture<Boolean> set(IMine iMine);

    CompletableFuture<IMine> get(UUID mineId);
}
