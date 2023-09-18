package me.alenalex.notaprisoncore.paper.database;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.database.IDatabaseProvider;
import me.alenalex.notaprisoncore.api.database.redis.IRedisDatabase;
import me.alenalex.notaprisoncore.api.database.sql.ISQLDatabase;
import me.alenalex.notaprisoncore.paper.NotAPrisonCore;

@Getter
@ToString
public class PrisonDatabaseProvider implements IDatabaseProvider {

    private final NotAPrisonCore pluginInstance;
    private final PrisonRedisDatabase redisDatabase;
    private final PrisonSqlDatabase sqlDatabase;
    public PrisonDatabaseProvider(NotAPrisonCore pluginInstance) {
        this.pluginInstance = pluginInstance;
        this.redisDatabase = new PrisonRedisDatabase(this.pluginInstance.getLogger(), this.pluginInstance.getPrisonManagers().configurationManager().getPluginConfiguration().redisConfiguration());
        this.sqlDatabase = new PrisonSqlDatabase(this.pluginInstance.getPrisonManagers().configurationManager().getPluginConfiguration().sqlConfiguration().asHikariConfig("mariadb"), this.pluginInstance.getLogger());
    }

    public ConnectionResponse connect(){
        try {
            this.sqlDatabase.createConnection();
            this.getPluginInstance().getLogger().info("Connected to SQL database.");
            this.getPluginInstance().getLogger().info("- Pool size is "+this.sqlDatabase.getPoolSize());
            this.getPluginInstance().getLogger().info("- Active Connection size is "+this.sqlDatabase.getActiveConnection());
        }catch (Exception e){
            e.printStackTrace();
            return ConnectionResponse.MYSQL_FAIL;
        }

        try {
            this.redisDatabase.createConnection();
            this.getPluginInstance().getLogger().info("Connected to Redis database.");
            this.getPluginInstance().getLogger().info("- Active Connection size is "+this.redisDatabase.getActiveConnection());
        }catch (Exception e){
            e.printStackTrace();
            return ConnectionResponse.REDIS_FAIL;
        }

        return ConnectionResponse.SUCCESS;
    }

    public void disconnect(){
        if(this.redisDatabase != null){
            getPluginInstance().getLogger().info("Found redis database instance, Attempting disconnection");
            try {
                this.redisDatabase.disconnect();
                getPluginInstance().getLogger().info("Successfully disconnected from redis database");
            }catch (Exception e){
                e.printStackTrace();
                getPluginInstance().getLogger().warning("Failed to disconnect from redis database");
            }
        }
        if(this.sqlDatabase != null){
            getPluginInstance().getLogger().info("Found sql database instance, Attempting disconnection");
            try {
                this.sqlDatabase.disconnect();
                getPluginInstance().getLogger().info("Successfully disconnected from mysql database");
            }catch (Exception e){
                e.printStackTrace();
                getPluginInstance().getLogger().warning("Failed to disconnect from mysql database");
            }
        }
    }

    @Override
    public IRedisDatabase getRedisDatabase() {
        return this.redisDatabase;
    }

    @Override
    public ISQLDatabase getSqlDatabase() {
        return this.sqlDatabase;
    }

    @EqualsAndHashCode
    @ToString
    @Getter
    @AllArgsConstructor
    public static class ConnectionResponse {

        public static final ConnectionResponse SUCCESS = new ConnectionResponse(true, "Successfully connected to all databases");
        public static final ConnectionResponse MYSQL_FAIL = new ConnectionResponse(false, "Failed to connect to mysql database. Check above/below for more errors");
        public static final ConnectionResponse REDIS_FAIL = new ConnectionResponse(false, "Failed to connect to redis database, Check above/below for more errors");

        private final boolean connectionSuccess;
        private final String response;
    }
}
