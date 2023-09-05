package me.alenalex.notaprisoncore.api.database.redis;

import me.alenalex.notaprisoncore.api.database.IDatabase;
import redis.clients.jedis.Jedis;


public interface IRedisDatabase extends IDatabase<Jedis> {
    int getActiveConnection();
}
