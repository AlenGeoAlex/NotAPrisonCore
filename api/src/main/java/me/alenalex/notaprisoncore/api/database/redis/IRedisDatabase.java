package me.alenalex.notaprisoncore.api.database.redis;

import me.alenalex.notaprisoncore.api.database.IDatabase;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPooled;
import redis.clients.jedis.UnifiedJedis;


public interface IRedisDatabase extends IDatabase<JedisPooled> {
    int getActiveConnection();
    String getStats();
}
