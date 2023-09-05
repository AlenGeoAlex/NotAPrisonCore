package me.alenalex.notaprisoncore.api.database.redis;

import me.alenalex.notaprisoncore.api.config.options.RedisConfiguration;
import me.alenalex.notaprisoncore.api.exceptions.database.IllegalConnectionException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPooled;

import java.sql.SQLException;
import java.util.logging.Logger;

public abstract class AbstractRedisDatabase implements IRedisDatabase{

    private final Logger logger;
    private final RedisConfiguration redisConfiguration;
    private JedisPool pool;

    public AbstractRedisDatabase(Logger logger, RedisConfiguration redisConfiguration) {
        this.logger = logger;
        this.redisConfiguration = redisConfiguration;
    }

    @Override
    public void createConnection() {
        if(this.redisConfiguration == null){
            throw new IllegalConnectionException("Failed to connect to redis-database, No valid configuration provided");
        }

        if(this.pool != null){
            if(!this.pool.isClosed())
                this.pool.close();
            this.pool = null;
        }

        try {
            this.pool = new JedisPool(this.redisConfiguration.getHost(), this.redisConfiguration.getPort(), this.redisConfiguration.getUser(), this.redisConfiguration.getPassword());
        }catch (Exception e){
            e.printStackTrace();
            throw new IllegalConnectionException("Error occurred while connecting to redis database", e);
        }


    }

    @Override
    public Jedis getConnection() {
        if(this.pool == null)
            throw new IllegalConnectionException("The plugin isn't connected to redis database");

        return this.pool.getResource();
    }

    @Override
    public boolean isConnected() {
        if(this.pool == null)
            return false;
        return !this.pool.isClosed();
    }

    @Override
    public void disconnect() {
        if(this.pool == null || this.pool.isClosed())
            return;

        this.pool.close();
    }

    @Override
    public int getActiveConnection() {
        return this.pool.getNumActive();
    }
}
