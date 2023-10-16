package me.alenalex.notaprisoncore.api.abstracts.store;

import me.alenalex.notaprisoncore.api.common.json.IJsonWrapper;
import me.alenalex.notaprisoncore.api.database.redis.IRedisDatabase;
import me.alenalex.notaprisoncore.api.exceptions.database.redis.IllegalRedisDataException;
import me.alenalex.notaprisoncore.api.exceptions.database.redis.RedisDatabaseNotAvailableException;
import redis.clients.jedis.JedisPooled;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
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

    protected CompletableFuture<Void> mapSet(String hash, String key, String value){
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
                 pooled.hset(hash, key, value);
                }catch (Exception e){
                    e.printStackTrace();
                    throw new IllegalRedisDataException("Failed to set the data to the hash "+hash+" with key "+key+" and value "+value);
                }
                return null;
            }
        });
    }

    protected CompletableFuture<Void> mapSet(String hash, HashMap<String, String> map){
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
                    pooled.hset(hash, map);
                }catch (Exception e){
                    e.printStackTrace();
                    throw new IllegalRedisDataException("Failed to set the data to the hash "+hash+" with "+map.keySet().toString());
                }
                return null;
            }
        });
    }

    protected CompletableFuture<Optional<String>> mapGet(String hash, String key){
        if(!redisDatabase.isConnected()){
            CompletableFuture<Optional<String>> future = new CompletableFuture<>();
            future.completeExceptionally(new RedisDatabaseNotAvailableException());
            return future;
        }

        return CompletableFuture.supplyAsync(new Supplier<Optional<String>>() {
            @Override
            public Optional<String> get() {
                try {
                    JedisPooled pooled = redisDatabase.getConnection();
                    return Optional.ofNullable(pooled.hget(hash, key));
                }catch (Exception e){
                    e.printStackTrace();
                    throw new IllegalRedisDataException("Failed to get the data from hash "+hash+" with key "+key);
                }
            }
        });
    }

//    protected CompletableFuture<HashMap<String, String>> mapGet(String hash, String... keys){
//        if(!redisDatabase.isConnected()){
//            CompletableFuture<HashMap<String, String>> future = new CompletableFuture<>();
//            future.completeExceptionally(new RedisDatabaseNotAvailableException());
//            return future;
//        }
//
//        return CompletableFuture.supplyAsync(new Supplier<HashMap<String, String>>() {
//            @Override
//            public HashMap<String, String> get() {
//                try {
//                    JedisPooled pooled = redisDatabase.getConnection();
//                    List<String> responseList = pooled.hmget(hash, keys);
//                    final HashMap<String, String> responseMap = new HashMap<>();
//                    for (int i = 0; i < keys.length; i++) {
//                        String key = keys[i];
//                        String value = responseList.get(i);
//                        responseMap.put(key, value);
//                    }
//
//                    return responseMap;
//                }catch (Exception e){
//                    e.printStackTrace();
//                    throw new IllegalRedisDataException("Failed to get the data from hash "+hash+" with key "+ Arrays.toString(keys));
//                }
//            }
//        });
//    }

    protected CompletableFuture<HashMap<T, String>> mapGet(String hash, T... keys){
        if(!redisDatabase.isConnected()){
            CompletableFuture<HashMap<T, String>> future = new CompletableFuture<>();
            future.completeExceptionally(new RedisDatabaseNotAvailableException());
            return future;
        }

        return CompletableFuture.supplyAsync(new Supplier<HashMap<T, String>>() {
            @Override
            public HashMap<T, String> get() {
                try {
                    JedisPooled pooled = redisDatabase.getConnection();
                    String[] strKeys = new String[keys.length];
                    for (int i = 0; i < keys.length; i++) {
                        strKeys[i] = keys[i].toString();
                    }
                    List<String> responseList = pooled.hmget(hash, strKeys);
                    final HashMap<T, String> responseMap = new HashMap<>();
                    for (int i = 0; i < keys.length; i++) {
                        T key = keys[i];
                        String value = responseList.get(i);
                        responseMap.put(key, value);
                    }

                    return responseMap;
                }catch (Exception e){
                    e.printStackTrace();
                    throw new IllegalRedisDataException("Failed to get the data from hash "+hash+" with key "+ Arrays.toString(keys));
                }
            }
        });
    }

    protected CompletableFuture<Void> mapClear(String hash){
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
                    pooled.del(hash);
                }catch (Exception e){
                    e.printStackTrace();
                    throw new IllegalRedisDataException("Failed to clear the data from hash "+hash+"");
                }
                return null;
            }
        });
    }

    protected CompletableFuture<Void> mapDelete(String hash, String key){
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
                    pooled.hdel(hash, key);
                }catch (Exception e){
                    e.printStackTrace();
                    throw new IllegalRedisDataException("Failed to delete the data from hash "+hash+" with key "+key);
                }
                return null;
            }
        });
    }

    protected CompletableFuture<Void> mapDelete(String hash, String... keys){
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
                    pooled.hdel(hash, keys);
                }catch (Exception e){
                    e.printStackTrace();
                    throw new IllegalRedisDataException("Failed to delete the data from hash "+hash+" with key "+ Arrays.toString(keys));
                }
                return null;
            }
        });
    }

    protected CompletableFuture<Boolean> mapContains(String hash, String key){
        if(!redisDatabase.isConnected()){
            CompletableFuture<Boolean> future = new CompletableFuture<>();
            future.completeExceptionally(new RedisDatabaseNotAvailableException());
            return future;
        }

        return CompletableFuture.supplyAsync(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                try {
                    JedisPooled pooled = redisDatabase.getConnection();
                    return pooled.hexists(hash, key);
                }catch (Exception e){
                    e.printStackTrace();
                    throw new IllegalRedisDataException("Failed to check exists the data from hash "+hash+" with key "+key);
                }
            }
        });
    }
}
