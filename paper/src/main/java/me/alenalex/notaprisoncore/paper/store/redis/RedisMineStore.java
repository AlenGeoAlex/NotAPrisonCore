package me.alenalex.notaprisoncore.paper.store.redis;

import me.alenalex.notaprisoncore.api.abstracts.pattern.Retry;
import me.alenalex.notaprisoncore.api.abstracts.store.AbstractRedisStore;
import me.alenalex.notaprisoncore.api.enums.RedisKey;
import me.alenalex.notaprisoncore.api.entity.mine.IMine;
import me.alenalex.notaprisoncore.api.exceptions.database.redis.IllegalRedisDataException;
import me.alenalex.notaprisoncore.api.store.redis.IRedisMineStore;
import me.alenalex.notaprisoncore.paper.bootstrap.Bootstrap;
import me.alenalex.notaprisoncore.paper.entity.mine.Mine;
import me.alenalex.notaprisoncore.paper.store.PrisonDataStore;
import me.alenalex.notaprisoncore.paper.wrapper.GsonWrapper;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class RedisMineStore extends AbstractRedisStore<Mine> implements IRedisMineStore {
    private final PrisonDataStore prisonDataStore;
    public RedisMineStore(PrisonDataStore prisonDataStore) {
        super(prisonDataStore.getPluginInstance().getDatabaseProvider().getRedisDatabase(), GsonWrapper.singleton());
        this.prisonDataStore = prisonDataStore;
    }

    @Override
    public CompletableFuture<Boolean> set(IMine iMine){
        Mine mine = (Mine) iMine;
        if(mine == null || mine.isInvalid()){
            Bootstrap.getJavaPlugin().getLogger().warning("Failed to set the mine with meta id "+mine.getMetaId().toString()+" of owner "+mine.getOwnerId().toString()+" since the unique id of the mine is missing");
            return CompletableFuture.completedFuture(false);
        }

        UUID mineId = mine.getId();
        return pushJson(RedisKey.MINE_DATA.keyOf(mineId.toString()), mine, RedisKey.MINE_DATA.getExpiryInSeconds());
    }


    @Override
    public CompletableFuture<IMine> get(UUID mineId){
        return CompletableFuture.supplyAsync(new Supplier<IMine>() {
            @Override
            public IMine get() {
                try {
                    Thread.sleep(prisonDataStore.getPluginInstance().getPrisonManagers().getConfigurationManager().getPluginConfiguration().getRedisSyncConfiguration().getRedisNetworkWaitMillis());

                    IMine redis = new Retry<IMine>(500, 5) {
                        @Override
                        public Optional<IMine> work() {
                            return Optional.ofNullable(getJson(RedisKey.MINE_DATA.keyOf(mineId.toString()))
                                    .handle((iMine, err) -> {
                                        if (err != null) {
                                            err.printStackTrace();
                                            return null;
                                        }
                                        System.out.println("Has Mine in redis");
                                        return iMine;
                                    }).join());
                        }
                    }.doSync();

                    System.out.println("Has data from redis ? "+(redis != null));
                    return redis;
                }catch (Exception e) {
                    throw new IllegalRedisDataException("Failed to get the data after retry");
                }
            }
        });
    }

    @Override
    protected Class<Mine> entityType() {
        return Mine.class;
    }
}
