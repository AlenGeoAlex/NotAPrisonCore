package me.alenalex.notaprisoncore.api.database;

import redis.clients.jedis.Jedis;

import java.sql.SQLException;

public interface IDatabase<T> {

    void createConnection();
    T getConnection() throws Exception;
    boolean isConnected();
    void disconnect();

}
