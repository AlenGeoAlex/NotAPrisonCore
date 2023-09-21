package me.alenalex.notaprisoncore.api.abstracts.message;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.reflect.TypeToken;
import me.alenalex.notaprisoncore.api.common.json.IJsonWrapper;
import me.alenalex.notaprisoncore.api.config.options.ServerConfiguration;
import me.alenalex.notaprisoncore.api.database.redis.IRedisDatabase;
import me.alenalex.notaprisoncore.message.AbstractMessageBuilder;
import me.alenalex.notaprisoncore.message.MessageCommunicationStatus;
import me.alenalex.notaprisoncore.message.MessageRequestModel;
import me.alenalex.notaprisoncore.message.MessageResponseModel;
import me.alenalex.notaprisoncore.message.interfaces.RequestValidator;
import me.alenalex.notaprisoncore.message.interfaces.ResponseValidator;
import me.alenalex.notaprisoncore.message.service.IBiDirectionalMessageServiceBus;
import redis.clients.jedis.JedisPooled;

import java.util.concurrent.TimeUnit;

public abstract class AbstractTwoWayMessageBus<R, T> extends AbstractMessageBus<R> implements IBiDirectionalMessageServiceBus<R, T> {

    private final String sourceName;
    private final RequestValidator<R> requestValidator;
    private final ResponseValidator<R, T> responseValidator;
    private final Cache<String, MessageRequestModel<R>> requestCache;
    private final String serverSourceAddress;
    public AbstractTwoWayMessageBus(IRedisDatabase redisDatabase, IJsonWrapper jsonWrapper, ServerConfiguration configuration, String serverSourceAddress) {
        super(redisDatabase, jsonWrapper);
        this.sourceName = configuration.getServerName();
        this.requestValidator = validateRequest();
        this.responseValidator = validateResponse();
        this.requestCache = Caffeine.newBuilder()
                .expireAfterWrite(requestTimeoutAfterSec(), TimeUnit.MINUTES)
                .build();
        this.serverSourceAddress = serverSourceAddress;
    }

    protected abstract int requestTimeoutAfterSec();
    public abstract TypeToken<MessageRequestModel<R>> requestModelClazz();
    public abstract TypeToken<MessageResponseModel<T>> responseModelClazz();
    @Override
    public MessageCommunicationStatus<R> sendMessage(R message) throws Exception {
        MessageCommunicationStatus<R> messageCommunicationStatus = super.sendMessage(message);
        if(messageCommunicationStatus == null)
            return null;
        if(messageCommunicationStatus.isSuccess())
            this.requestCache.put(messageCommunicationStatus.getSourceId(), messageCommunicationStatus.getContext());

        return messageCommunicationStatus;
    }

    public void receive(Object rawRequest){
        if(rawRequest == null)
            return;

        MessageRequestModel<R> request = (MessageRequestModel<R>) rawRequest;

        if(request.getSourceAddress().equals(this.serverSourceAddress))
            return;

        if(requestValidator != null && !requestValidator.validate(request))
            return;
        onRequest().respond(request)
                .whenComplete((res, err) -> {
                    if(err != null){
                        err.printStackTrace();
                        return;
                    }

                    if(!res.isPresent()){
                        return;
                    }

                    if(!getRedisDatabase().isConnected()){
                        return;
                    }

                    AbstractMessageBuilder abstractMessageBuilder = AbstractMessageBuilder.get();
                    try {
                        JedisPooled connection = getRedisDatabase().getConnection();
                        MessageResponseModel<T> tMessageResponseModel = abstractMessageBuilder.buildRequest(res.get(), request);
                        connection.publish(responseChannelName(), getJsonWrapper().stringify(tMessageResponseModel));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                });
    }

    public void receiveResponse(Object rawResponse){
        if(rawResponse == null)
            return;

        MessageResponseModel<T> response = (MessageResponseModel<T>) rawResponse;

        if(response.getSourceAddress().equals(this.serverSourceAddress) || !response.getTargetAddress().equals(this.serverSourceAddress))
            return;

        String requestId = response.getTargetId();
        MessageRequestModel<R> request = this.requestCache.getIfPresent(requestId);
        if(request == null){
            return;
        }
        this.requestCache.invalidate(requestId);

        if(responseValidator != null && !responseValidator.validate(request, response))
            return;

        onResponse().process(request, response);
    }

    public String getSourceName() {
        return sourceName;
    }

    public String getServerSourceAddress() {
        return serverSourceAddress;
    }
}
