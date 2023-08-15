package me.alenalex.notaprisoncore.api.database;

import com.zaxxer.hikari.HikariDataSource;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;

public interface IDatabase {
    void createConnection();
    boolean isConnected();
    void disconnect();
    @Nullable
    HikariDataSource getDataSource();
    int getActiveConnection();
    int getPoolSize();
    Connection getConnection();
}
