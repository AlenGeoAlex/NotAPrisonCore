package me.alenalex.notaprisoncore.paper.database;

import me.alenalex.notaprisoncore.api.config.options.RedisConfiguration;
import me.alenalex.notaprisoncore.api.database.redis.AbstractRedisDatabase;

import java.util.logging.Logger;

public class PrisonRedisDatabase extends AbstractRedisDatabase {
    public PrisonRedisDatabase(Logger logger, RedisConfiguration redisConfiguration) {
        super(logger, redisConfiguration);
    }
}
