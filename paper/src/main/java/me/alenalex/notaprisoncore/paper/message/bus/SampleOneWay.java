package me.alenalex.notaprisoncore.paper.message.bus;

import com.google.common.reflect.TypeToken;
import me.alenalex.notaprisoncore.api.abstracts.message.AbstractOneWayMessageBus;
import me.alenalex.notaprisoncore.api.common.json.IJsonWrapper;
import me.alenalex.notaprisoncore.api.config.options.ServerConfiguration;
import me.alenalex.notaprisoncore.api.database.redis.IRedisDatabase;
import me.alenalex.notaprisoncore.message.MessageRequestModel;
import me.alenalex.notaprisoncore.message.interfaces.RequestReceivedCallback;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class SampleOneWay extends AbstractOneWayMessageBus<me.alenalex.notaprisoncore.message.models.SampleOneWay> {

    private static final TypeToken<MessageRequestModel<me.alenalex.notaprisoncore.message.models.SampleOneWay>> REQUEST_MODEL_TYPE_TOKEN = new TypeToken<MessageRequestModel<me.alenalex.notaprisoncore.message.models.SampleOneWay>>() {
    };
    public SampleOneWay(IRedisDatabase redisDatabase, IJsonWrapper jsonWrapper, ServerConfiguration configuration) {
        super(redisDatabase, jsonWrapper, configuration);
    }

    @Override
    public TypeToken<MessageRequestModel<me.alenalex.notaprisoncore.message.models.SampleOneWay>> requestClazz() {
        return REQUEST_MODEL_TYPE_TOKEN;
    }

    @Override
    public @NotNull String channelName() {
        return "sample-one-way";
    }

    @Override
    public RequestReceivedCallback<me.alenalex.notaprisoncore.message.models.SampleOneWay, Void> onRequest() {
        return new RequestReceivedCallback<me.alenalex.notaprisoncore.message.models.SampleOneWay, Void>() {
            @Override
            public CompletableFuture<Void> respond(MessageRequestModel<me.alenalex.notaprisoncore.message.models.SampleOneWay> message) {
                System.out.println("Request has been recieved for one way communication");
                System.out.println(message);
                return null;
            }
        };
    }
}
