package me.alenalex.notaprisoncore.api.database;

import me.alenalex.notaprisoncore.api.database.redis.IRedisDatabase;
import me.alenalex.notaprisoncore.api.database.sql.ISQLDatabase;

public interface IDatabaseProvider {

    IRedisDatabase getRedisDatabase();

    ISQLDatabase getSqlDatabase();

}
