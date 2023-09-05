package me.alenalex.notaprisoncore.api.database.sql;

import com.google.common.base.Charsets;
import com.google.common.io.CharStreams;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.exceptions.database.IllegalConnectionException;
import me.alenalex.notaprisoncore.api.exceptions.database.sql.ScriptException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

@Getter
@EqualsAndHashCode
@ToString
public abstract class AbstractSQLDatabase implements ISQLDatabase {

    private final HikariConfig hikariConfig;

    private HikariDataSource dataSource;

    private final Logger logger;

    public AbstractSQLDatabase(HikariConfig hikariConfig, Logger logger) {
        this.hikariConfig = hikariConfig;
        this.logger = logger;
    }

    @Override
    public void createConnection() {
        if(this.hikariConfig == null){
            throw new IllegalConnectionException("No valid configuration provided for hikari");
        }

        if(this.dataSource != null){
            if(!this.dataSource.isClosed())
                this.dataSource.close();
            this.dataSource = null;
        }

        try {
            this.dataSource = new HikariDataSource(this.hikariConfig);
        }catch (Exception e){
            e.printStackTrace();
            throw new IllegalConnectionException("Error occurred while connecting to mysql database", e);
        }
        if(!isConnected()){
            throw new IllegalConnectionException("Failed to connect to database.");
        }
    }

    @Override
    public boolean isConnected() {
        if(dataSource == null)
            return false;
        return dataSource.isRunning();
    }

    @Override
    public void disconnect() {
        if(dataSource == null || dataSource.isClosed())
            return;

        dataSource.close();
    }

    @Override
    public @Nullable HikariDataSource getDataSource() {
        return this.dataSource;
    }

    @Override
    public int getActiveConnection() {
        if(this.dataSource == null || !this.dataSource.isRunning())
            return 0;

        return this.dataSource.getHikariPoolMXBean().getActiveConnections();
    }

    @Override
    public int getPoolSize() {
        if(this.dataSource == null || !this.dataSource.isRunning())
            return 0;

        return this.dataSource.getHikariPoolMXBean().getTotalConnections();
    }

    @Override
    public Connection getConnection() throws SQLException {
        if(this.dataSource == null)
            throw new IllegalConnectionException("The plugin isn't yet connected to mysql database!");

        return this.dataSource.getConnection();
    }

    public boolean prepareFromScript(@NotNull InputStream scriptStream){
        long start = System.currentTimeMillis();
        String sqlScript = null;
        try(InputStreamReader reader = new InputStreamReader(scriptStream, Charsets.UTF_8)){
            sqlScript = CharStreams.toString(reader);
        }catch (Exception e){
            throw new ScriptException("Failed to read the SQL Script for DDL Commands. Please contact the developer");
        }
        if(sqlScript.isEmpty())
            return true;

        try(final Connection connection = getConnection();
            final Statement statement = connection.createStatement();
            ) {
            statement.execute(sqlScript);
        }catch (Exception e){
            e.printStackTrace();
            throw new ScriptException("Failed to execute the SQL Script for DDL Commands. Please contact the developer");
        }
        long end = System.currentTimeMillis();
        getLogger().info("Successfully executed SQL Script on "+ (end-start) + "ms.");
        return true;
    }
}
