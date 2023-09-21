package me.alenalex.notaprisoncore.api.abstracts.message;

import me.alenalex.notaprisoncore.api.common.json.IJsonWrapper;
import me.alenalex.notaprisoncore.api.database.redis.IRedisDatabase;
import me.alenalex.notaprisoncore.message.AbstractMessageBuilder;
import me.alenalex.notaprisoncore.message.MessageCommunicationStatus;
import me.alenalex.notaprisoncore.message.MessageRequestModel;
import me.alenalex.notaprisoncore.message.service.IMessageServiceBus;
import redis.clients.jedis.JedisPooled;

import java.util.UUID;
import java.util.logging.Logger;

public abstract class AbstractMessageBus<R> implements IMessageServiceBus<R> {
    private static final Logger LOGGER = Logger.getLogger("NPC-MessageServiceBus");

    private final IRedisDatabase redisDatabase;
    private final IJsonWrapper jsonWrapper;

    public AbstractMessageBus(IRedisDatabase redisDatabase, IJsonWrapper jsonWrapper) {
        this.redisDatabase = redisDatabase;
        this.jsonWrapper = jsonWrapper;
    }

    @Override
    public MessageCommunicationStatus<R> sendMessage(R message) throws Exception {
        if(!redisDatabase.isConnected()){
            return MessageCommunicationStatus.fail("Redis connection is unavailable");
        }

        AbstractMessageBuilder abstractMessageBuilder = AbstractMessageBuilder.get();
        MessageRequestModel<R> rMessageRequestModel = abstractMessageBuilder.buildRequest(message);

        JedisPooled connection = redisDatabase.getConnection();
        connection.publish(channelName(), jsonWrapper.stringify(rMessageRequestModel));
        return MessageCommunicationStatus.success(rMessageRequestModel);
    }

    protected IRedisDatabase getRedisDatabase() {
        return redisDatabase;
    }

    protected IJsonWrapper getJsonWrapper() {
        return jsonWrapper;
    }

    protected void logInfo(String message){
        LOGGER.info(message);
    }

    protected void logWarning(String message){
        LOGGER.warning(message);
    }

    protected void logSevere(String message){
        LOGGER.severe(message);
    }
}
