package me.alenalex.notaprisoncore.paper.store.redis;

import me.alenalex.notaprisoncore.api.abstracts.store.AbstractRedisStore;
import me.alenalex.notaprisoncore.api.common.Pair;
import me.alenalex.notaprisoncore.api.enums.RedisKey;
import me.alenalex.notaprisoncore.api.entity.mine.IMine;
import me.alenalex.notaprisoncore.api.entity.user.IPrisonUserProfile;
import me.alenalex.notaprisoncore.api.exceptions.database.redis.RedisDatabaseNotAvailableException;
import me.alenalex.notaprisoncore.api.store.redis.IRedisUserProfileStore;
import me.alenalex.notaprisoncore.paper.bootstrap.Bootstrap;
import me.alenalex.notaprisoncore.paper.store.PrisonDataStore;
import me.alenalex.notaprisoncore.paper.wrapper.GsonWrapper;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class RedisUserProfileStore extends AbstractRedisStore<IPrisonUserProfile> implements IRedisUserProfileStore {
    private final PrisonDataStore prisonDataStore;
    public RedisUserProfileStore(PrisonDataStore prisonDataStore) {
        super(prisonDataStore.getPluginInstance().getDatabaseProvider().getRedisDatabase(), GsonWrapper.singleton());
        this.prisonDataStore = prisonDataStore;
    }

    @Override
    protected Class<IPrisonUserProfile> entityType() {
        return IPrisonUserProfile.class;
    }

    @Override
    public CompletableFuture<Void> setUserOnSwitch(IPrisonUserProfile userProfile) {
        if(!redisDatabase.isConnected()){
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(new RedisDatabaseNotAvailableException());
            return future;
        }

        return CompletableFuture.supplyAsync(new Supplier<Void>() {
            @Override
            public Void get() {
                setUserOnSwitchInternal(userProfile);
                return null;
            }
        });
    }

    @Override
    public CompletableFuture<Pair<Boolean, String>> isUserOnSwitch(UUID uuid) {
        if(!redisDatabase.isConnected()){
            CompletableFuture<Pair<Boolean, String>> future = new CompletableFuture<>();
            future.completeExceptionally(new RedisDatabaseNotAvailableException());
            return future;
        }

        return getOnceString(RedisKey.SERVER_SWITCH.keyOf(uuid.toString()))
                .thenApply(s ->{
                    if(s == null)
                        return new Pair<Boolean, String>(false, null);

                    return new Pair<Boolean, String>(true, s);
                });
    }

    @Override
    public CompletableFuture<Void> setUserData(IPrisonUserProfile userProfile, IMine iMine) {
        return null;
    }

    @Override
    public CompletableFuture<Pair<IPrisonUserProfile, IMine>> getUserData(UUID playerId) {
        return null;
    }

    private void setUserOnSwitchInternal(IPrisonUserProfile userProfile){
        String key = RedisKey.SERVER_SWITCH.keyOf(userProfile.getUserId().toString());
        pushString(key, this.prisonDataStore.getPluginInstance().getPrisonManagers().getConfigurationManager().getPluginConfiguration().getServerConfiguration().getServerName(), RedisKey.SERVER_SWITCH.getExpiryInSeconds())
                .exceptionally((err) -> {
                    if(err != null){
                        err.printStackTrace();
                        Bootstrap.getJavaPlugin().getLogger().severe("Failed to set the user in redis as "+ key);
                    }

                    return false;
                });
    }
}
