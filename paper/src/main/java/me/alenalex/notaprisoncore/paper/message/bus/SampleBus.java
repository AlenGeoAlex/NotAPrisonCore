package me.alenalex.notaprisoncore.paper.message.bus;

import com.google.common.reflect.TypeToken;
import me.alenalex.notaprisoncore.api.abstracts.message.AbstractTwoWayMessageBus;
import me.alenalex.notaprisoncore.api.common.json.IJsonWrapper;
import me.alenalex.notaprisoncore.api.config.options.ServerConfiguration;
import me.alenalex.notaprisoncore.api.database.redis.IRedisDatabase;
import me.alenalex.notaprisoncore.message.MessageRequestModel;
import me.alenalex.notaprisoncore.message.MessageResponseModel;
import me.alenalex.notaprisoncore.message.interfaces.RequestReceivedCallback;
import me.alenalex.notaprisoncore.message.interfaces.ResponseReceivedCallback;
import me.alenalex.notaprisoncore.message.models.Sample;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public class SampleBus extends AbstractTwoWayMessageBus<Sample.SampleRequest, Sample.SampleResponse> {
    private static final TypeToken<MessageRequestModel<Sample.SampleRequest>> REQUEST_TYPE_TOKEN = new TypeToken<MessageRequestModel<Sample.SampleRequest>>() {};
    private static final TypeToken<MessageResponseModel<Sample.SampleResponse>> RESPONSE_MODEL_TYPE_TOKEN = new TypeToken<MessageResponseModel<Sample.SampleResponse>>() {};

    public SampleBus(IRedisDatabase redisDatabase, IJsonWrapper jsonWrapper, ServerConfiguration configuration) {
        super(redisDatabase, jsonWrapper, configuration);
    }

    @Override
    protected int requestTimeoutAfterSec() {
        return 30;
    }

    @Override
    public TypeToken<MessageRequestModel<Sample.SampleRequest>> requestModelClazz() {
        return REQUEST_TYPE_TOKEN;
    }

    @Override
    public TypeToken<MessageResponseModel<Sample.SampleResponse>> responseModelClazz() {
        return RESPONSE_MODEL_TYPE_TOKEN;
    }


    @Override
    public RequestReceivedCallback<Sample.SampleRequest, Sample.SampleResponse> onRequest() {
        return new RequestReceivedCallback<Sample.SampleRequest, Sample.SampleResponse>() {
            @Override
            public CompletableFuture<Sample.SampleResponse> respond(MessageRequestModel<Sample.SampleRequest> message) {
                System.out.println("Recieved Request "+message.toString());
                return CompletableFuture.completedFuture(new Sample.SampleResponse(message.getContext().getName(), message.getContext().getName()+" Modified"));
            }
        };
    }

    @Override
    public ResponseReceivedCallback<Sample.SampleRequest, Sample.SampleResponse> onResponse() {
        return new ResponseReceivedCallback<Sample.SampleRequest, Sample.SampleResponse>() {
            @Override
            public void process(MessageRequestModel<Sample.SampleRequest> request, MessageResponseModel<Sample.SampleResponse> response) {
                System.out.println("Recieved Response for ");
                System.out.println("Request "+request.toString());
                System.out.println("Response "+response.toString());
            }
        };
    }

    @Override
    public @NotNull String channelName() {
        return "Test-Channel";
    }
}
