package me.alenalex.notaprisoncore.api.database.redis;

import me.alenalex.notaprisoncore.api.config.options.RedisConfiguration;
import me.alenalex.notaprisoncore.api.exceptions.database.IllegalConnectionException;
import redis.clients.jedis.JedisPooled;

import java.util.logging.Logger;

public abstract class AbstractRedisDatabase implements IRedisDatabase{

    private final Logger logger;
    private final RedisConfiguration redisConfiguration;
    private JedisPooled jedisPooled;

    public AbstractRedisDatabase(Logger logger, RedisConfiguration redisConfiguration) {
        this.logger = logger;
        this.redisConfiguration = redisConfiguration;
    }

    @Override
    public void createConnection() {
        if(this.redisConfiguration == null){
            throw new IllegalConnectionException("Failed to connect to redis-database, No valid configuration provided");
        }

        if(this.jedisPooled != null){
            if(!this.jedisPooled.getPool().isClosed())
                this.jedisPooled.getPool().close();
            this.jedisPooled = null;
        }

        try {
            this.jedisPooled = new JedisPooled(this.redisConfiguration.getHost(), this.redisConfiguration.getPort(), this.redisConfiguration.getUser(), this.redisConfiguration.getPassword());
        }catch (Exception e){
            e.printStackTrace();
            throw new IllegalConnectionException("Error occurred while connecting to redis database", e);
        }

        if(!isConnected())
            throw new IllegalConnectionException("Failed to connect to redis database");
    }

    @Override
    public JedisPooled getConnection() {
        if(this.jedisPooled == null || this.jedisPooled.getPool().isClosed())
            throw new IllegalConnectionException("The plugin isn't connected to redis database");

        return this.jedisPooled;
    }

    @Override
    public boolean isConnected() {
        if(this.jedisPooled == null)
            return false;

        try {
            this.jedisPooled.ping();
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void disconnect() {
        if(this.jedisPooled == null)
            return;

        this.jedisPooled.getPool().close();
    }

    @Override
    public int getActiveConnection() {
        return this.jedisPooled.getPool().getNumActive();
    }

    @Override
    public String getStats() {
        return null;
    }
}
