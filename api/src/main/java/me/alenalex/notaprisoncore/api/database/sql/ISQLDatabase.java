package me.alenalex.notaprisoncore.api.database.sql;

import com.zaxxer.hikari.HikariDataSource;
import me.alenalex.notaprisoncore.api.database.IDatabase;
import org.jetbrains.annotations.Nullable;

import java.sql.Connection;
import java.sql.SQLException;

public interface ISQLDatabase extends IDatabase<Connection> {
    @Nullable
    HikariDataSource getDataSource();
    int getActiveConnection();
    int getPoolSize();
}
