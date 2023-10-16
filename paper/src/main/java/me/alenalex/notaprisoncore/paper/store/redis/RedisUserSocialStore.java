package me.alenalex.notaprisoncore.paper.store.redis;

import me.alenalex.notaprisoncore.api.abstracts.store.AbstractRedisStore;
import me.alenalex.notaprisoncore.api.common.json.IJsonWrapper;
import me.alenalex.notaprisoncore.api.database.redis.IRedisDatabase;
import me.alenalex.notaprisoncore.api.store.redis.IRedisUserSocialStore;
import me.alenalex.notaprisoncore.paper.store.PrisonDataStore;
import me.alenalex.notaprisoncore.paper.wrapper.GsonWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class RedisUserSocialStore extends AbstractRedisStore<String> implements IRedisUserSocialStore {

    private static final String HASH_NAME = "npc-global-player-name-cache";

    public RedisUserSocialStore(PrisonDataStore prisonDataStore) {
        super(prisonDataStore.getPluginInstance().getDatabaseProvider().getRedisDatabase(), GsonWrapper.singleton());
    }

    @Override
    protected Class<String> entityType() {
        return String.class;
    }

    @Override
    public void setName(@NotNull UUID uuid, @NotNull String name) {
        this.mapSet(HASH_NAME, uuid.toString(), name)
                .exceptionally((err) -> {
                   err.printStackTrace();
                   return null;
                });
    }

    @Override
    public CompletableFuture<Optional<String>> getName(@NotNull UUID uuid) {
        return this.mapGet(HASH_NAME, uuid.toString());
    }
}
