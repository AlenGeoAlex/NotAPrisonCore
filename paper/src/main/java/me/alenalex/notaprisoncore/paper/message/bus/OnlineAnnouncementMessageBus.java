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
import me.alenalex.notaprisoncore.message.models.OnlineAnnouncementMessage;
import me.alenalex.notaprisoncore.paper.bootstrap.Bootstrap;
import me.alenalex.notaprisoncore.paper.constants.MessageBusChannelConstants;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class OnlineAnnouncementMessageBus extends AbstractTwoWayMessageBus<OnlineAnnouncementMessage.OnlineAnnouncementRequest, OnlineAnnouncementMessage.OnlineAnnouncementResponse> {

    private static final TypeToken<MessageRequestModel<OnlineAnnouncementMessage.OnlineAnnouncementRequest>> REQUEST_TYPE_TOKEN = new TypeToken<MessageRequestModel<OnlineAnnouncementMessage.OnlineAnnouncementRequest>>() {};
    private static final TypeToken<MessageResponseModel<OnlineAnnouncementMessage.OnlineAnnouncementResponse>> RESPONSE_TYPE_TOKEN = new TypeToken<MessageResponseModel<OnlineAnnouncementMessage.OnlineAnnouncementResponse>>() {};

    public OnlineAnnouncementMessageBus(IRedisDatabase redisDatabase, IJsonWrapper jsonWrapper, ServerConfiguration configuration, String serverSourceAddress) {
        super(redisDatabase, jsonWrapper, configuration, serverSourceAddress);
    }

    @Override
    protected int requestTimeoutAfterSec() {
        return 60;
    }

    @Override
    public TypeToken<MessageRequestModel<OnlineAnnouncementMessage.OnlineAnnouncementRequest>> requestModelClazz() {
        return REQUEST_TYPE_TOKEN;
    }

    @Override
    public TypeToken<MessageResponseModel<OnlineAnnouncementMessage.OnlineAnnouncementResponse>> responseModelClazz() {
        return RESPONSE_TYPE_TOKEN;
    }

    @Override
    public RequestReceivedCallback<OnlineAnnouncementMessage.OnlineAnnouncementRequest, OnlineAnnouncementMessage.OnlineAnnouncementResponse> onRequest() {
        return new RequestReceivedCallback<OnlineAnnouncementMessage.OnlineAnnouncementRequest, OnlineAnnouncementMessage.OnlineAnnouncementResponse>() {
            @Override
            public CompletableFuture<Optional<OnlineAnnouncementMessage.OnlineAnnouncementResponse>> respond(MessageRequestModel<OnlineAnnouncementMessage.OnlineAnnouncementRequest> message) {
                if(message.getSource().equals(getSourceName())){
                    logWarning("A new server/source is trying to/already boot up with source name "+message.getSource()+" from ["+message.getSourceAddress()+"]. A shutdown message has been sent back to the originating server");
                    return CompletableFuture.completedFuture(Optional.of(new OnlineAnnouncementMessage.OnlineAnnouncementResponse()));
                } else{
                    if(message.getContext().isFirstRequest()){
                        logInfo("A new server/source "+message.getSource()+" ["+message.getSourceAddress()+"] is now coming online to the shared resource pool");
                    }
                    return CompletableFuture.completedFuture(Optional.empty());
                }
            }
        };
    }

    @Override
    public ResponseReceivedCallback<OnlineAnnouncementMessage.OnlineAnnouncementRequest, OnlineAnnouncementMessage.OnlineAnnouncementResponse> onResponse() {
        return new ResponseReceivedCallback<OnlineAnnouncementMessage.OnlineAnnouncementRequest, OnlineAnnouncementMessage.OnlineAnnouncementResponse>() {
            @Override
            public void process(MessageRequestModel<OnlineAnnouncementMessage.OnlineAnnouncementRequest> request, MessageResponseModel<OnlineAnnouncementMessage.OnlineAnnouncementResponse> response) {
                Bukkit.getScheduler().runTask(Bootstrap.getJavaPlugin(), new Runnable() {
                    @Override
                    public void run() {
                        Bootstrap.getJavaPlugin().getLogger().severe("--------------------------------------------");
                        Bootstrap.getJavaPlugin().getLogger().severe(" Failed to bind with NotAPrisonCore shared");
                        Bootstrap.getJavaPlugin().getLogger().severe(" bus");
                        Bootstrap.getJavaPlugin().getLogger().severe(" ");
                        Bootstrap.getJavaPlugin().getLogger().severe(" Reason : Another server is already running");
                        Bootstrap.getJavaPlugin().getLogger().severe("          with the same source name "+getSourceName());
                        Bootstrap.getJavaPlugin().getLogger().severe("          and 2 instances with the same source");
                        Bootstrap.getJavaPlugin().getLogger().severe("          cannot exist in parallel.");
                        Bootstrap.getJavaPlugin().getLogger().severe("--------------------------------------------");
                        Bukkit.getServer().shutdown();
                    }
                });
            }
        };
    }

    @Override
    public @NotNull String channelName() {
        return MessageBusChannelConstants.ONLINE_ANNOUNCEMENT_MESSAGE;
    }
}
