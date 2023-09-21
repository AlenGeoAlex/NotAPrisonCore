package me.alenalex.notaprisoncore.api.abstracts.message;

import com.google.common.reflect.TypeToken;
import me.alenalex.notaprisoncore.api.common.json.IJsonWrapper;
import me.alenalex.notaprisoncore.api.config.options.ServerConfiguration;
import me.alenalex.notaprisoncore.api.database.redis.IRedisDatabase;
import me.alenalex.notaprisoncore.message.MessageRequestModel;
import me.alenalex.notaprisoncore.message.interfaces.RequestValidator;
import me.alenalex.notaprisoncore.message.service.IUniDirectionalMessageServiceBus;

public abstract class AbstractOneWayMessageBus<R> extends AbstractMessageBus<R> implements IUniDirectionalMessageServiceBus<R> {

    private final String sourceName;
    private final String serverSourceAddress;
    private final RequestValidator<R> validator;

    public AbstractOneWayMessageBus(IRedisDatabase redisDatabase, IJsonWrapper jsonWrapper, ServerConfiguration configuration, String serverSourceAddress) {
        super(redisDatabase, jsonWrapper);
        this.sourceName = configuration.getServerName();
        this.serverSourceAddress = serverSourceAddress;
        this.validator = validateRequest();
    }

    public abstract TypeToken<MessageRequestModel<R>> requestClazz();

    public void receive(Object rawRequest){
        if(rawRequest == null)
          return;

        MessageRequestModel<R> request = (MessageRequestModel<R>) rawRequest;

        if(request.getSourceAddress().equals(this.serverSourceAddress))
            return;

        if(validator != null && !validator.validate(request))
            return;

        onRequest().respond(request);
    }
}
