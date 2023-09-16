package me.alenalex.notaprisoncore.api.abstracts.store;

import me.alenalex.notaprisoncore.api.common.json.IJsonWrapper;
import me.alenalex.notaprisoncore.api.database.redis.IRedisDatabase;
import me.alenalex.notaprisoncore.api.exceptions.database.redis.IllegalRedisDataException;
import me.alenalex.notaprisoncore.api.exceptions.database.redis.RedisDatabaseNotAvailableException;
import redis.clients.jedis.JedisPooled;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;


public abstract class AbstractRedisStore<T> {

    protected final IRedisDatabase redisDatabase;
    private final IJsonWrapper jsonWrapper;

    public AbstractRedisStore(IRedisDatabase redisDatabase, IJsonWrapper jsonWrapper) {
        this.redisDatabase = redisDatabase;
        this.jsonWrapper = jsonWrapper;
    }

    protected abstract Class<T> entityType();

    protected CompletableFuture<Boolean> pushJson(String key, T entity){
        if(!redisDatabase.isConnected()){
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            future.completeExceptionally(new RedisDatabaseNotAvailableException());
            return future;
        }
        return CompletableFuture.supplyAsync(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                try {
                    JedisPooled connection = redisDatabase.getConnection();
                    connection.set(key, jsonWrapper.stringify(entity));
                }catch (Exception e){
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        });
    }

    protected CompletableFuture<Boolean> pushString(String key, String value){
        if(!redisDatabase.isConnected()){
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            future.completeExceptionally(new RedisDatabaseNotAvailableException());
            return future;
        }
        return CompletableFuture.supplyAsync(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                try {
                    JedisPooled connection = redisDatabase.getConnection();
                    connection.set(key, value);
                }catch (Exception e){
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        });
    }

    protected CompletableFuture<Boolean> pushJson(String key, T entity, long expiry){
        if(!redisDatabase.isConnected()){
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            future.completeExceptionally(new RedisDatabaseNotAvailableException());
            return future;
        }
        return CompletableFuture.supplyAsync(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                try {
                    JedisPooled connection = redisDatabase.getConnection();
                    connection.setex(key, expiry ,jsonWrapper.stringify(entity));
                }catch (Exception e){
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        });
    }

    protected CompletableFuture<Boolean> pushString(String key, String value, long expiry){
        if(!redisDatabase.isConnected()){
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            future.completeExceptionally(new RedisDatabaseNotAvailableException());
            return future;
        }
        return CompletableFuture.supplyAsync(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                try {
                    JedisPooled connection = redisDatabase.getConnection();
                    connection.setex(key, expiry ,value);
                }catch (Exception e){
                    e.printStackTrace();
                    return false;
                }
                return true;
            }
        });
    }

    protected CompletableFuture<T> getOnceJson(String id) {
        if (!redisDatabase.isConnected()) {
            CompletableFuture<T> future = new CompletableFuture<>();
            future.completeExceptionally(new RedisDatabaseNotAvailableException());
            return future;
        }

        return CompletableFuture.supplyAsync(new Supplier<T>() {
            @Override
            public T get() {
                try {
                    JedisPooled pooled = redisDatabase.getConnection();
                    String res = pooled.get(id);
                    pooled.del(id);
                    if(res == null || res.isEmpty())
                        return null;

                    return jsonWrapper.fromString(res, entityType());
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new IllegalRedisDataException("Failed to deserializing the data from redis");
                }
            }
        });
    }

    protected CompletableFuture<String> getOnceString(String id){
        if(!redisDatabase.isConnected()){
            CompletableFuture<String> future = new CompletableFuture<>();
            future.completeExceptionally(new RedisDatabaseNotAvailableException());
            return future;
        }

        return CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                try {
                    JedisPooled pooled = redisDatabase.getConnection();
                    String res = pooled.get(id);
                    pooled.del(id);
                    return res;
                }catch (Exception e){
                    e.printStackTrace();
                    throw new IllegalRedisDataException("Failed to deserializing the data from redis");
                }
            }
        });
    }

    protected CompletableFuture<T> getJson(String id){
        if(!redisDatabase.isConnected()){
            CompletableFuture<T> future = new CompletableFuture<>();
            future.completeExceptionally(new RedisDatabaseNotAvailableException());
            return future;
        }

        return CompletableFuture.supplyAsync(new Supplier<T>() {
            @Override
            public T get() {
                try {
                    JedisPooled pooled = redisDatabase.getConnection();
                    String res = pooled.get(id);
                    if(res == null || res.isEmpty())
                        return null;

                    return jsonWrapper.fromString(res, entityType());
                }catch (Exception e){
                    e.printStackTrace();
                    throw new IllegalRedisDataException("Failed to deserializing the data from redis");
                }
            }
        });
    }

    protected CompletableFuture<String> getString(String id){
        if(!redisDatabase.isConnected()){
            CompletableFuture<String> future = new CompletableFuture<>();
            future.completeExceptionally(new RedisDatabaseNotAvailableException());
            return future;
        }

        return CompletableFuture.supplyAsync(new Supplier<String>() {
            @Override
            public String get() {
                try {
                    JedisPooled pooled = redisDatabase.getConnection();
                    return pooled.get(id);
                }catch (Exception e){
                    e.printStackTrace();
                    throw new IllegalRedisDataException("Failed to deserializing the data from redis");
                }
            }
        });
    }

    protected CompletableFuture<Void> delete(String id){
        if(!redisDatabase.isConnected()){
            CompletableFuture<Void> future = new CompletableFuture<>();
            future.completeExceptionally(new RedisDatabaseNotAvailableException());
            return future;
        }

        return CompletableFuture.supplyAsync(new Supplier<Void>() {
            @Override
            public Void get() {
                try {
                    JedisPooled pooled = redisDatabase.getConnection();
                    pooled.del(id);
                }catch (Exception e){
                    e.printStackTrace();
                    throw new IllegalRedisDataException("Failed to deserializing the data from redis");
                }

                return null;
            }
        });
    }
}
