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
import me.alenalex.notaprisoncore.paper.bootstrap.Bootstrap;
import me.alenalex.notaprisoncore.paper.constants.MessageBusChannelConstants;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class HeartbeatMessageBus extends AbstractTwoWayMessageBus<me.alenalex.notaprisoncore.message.models.HeartbeatMessage.OnlineAnnouncementRequest, me.alenalex.notaprisoncore.message.models.HeartbeatMessage.OnlineAnnouncementResponse> {

    private static final TypeToken<MessageRequestModel<me.alenalex.notaprisoncore.message.models.HeartbeatMessage.OnlineAnnouncementRequest>> REQUEST_TYPE_TOKEN = new TypeToken<MessageRequestModel<me.alenalex.notaprisoncore.message.models.HeartbeatMessage.OnlineAnnouncementRequest>>() {};
    private static final TypeToken<MessageResponseModel<me.alenalex.notaprisoncore.message.models.HeartbeatMessage.OnlineAnnouncementResponse>> RESPONSE_TYPE_TOKEN = new TypeToken<MessageResponseModel<me.alenalex.notaprisoncore.message.models.HeartbeatMessage.OnlineAnnouncementResponse>>() {};

    public HeartbeatMessageBus(IRedisDatabase redisDatabase, IJsonWrapper jsonWrapper, ServerConfiguration configuration, String serverSourceAddress) {
        super(redisDatabase, jsonWrapper, configuration, serverSourceAddress);
    }

    @Override
    protected int requestTimeoutAfterSec() {
        return 60;
    }

    @Override
    public TypeToken<MessageRequestModel<me.alenalex.notaprisoncore.message.models.HeartbeatMessage.OnlineAnnouncementRequest>> requestModelClazz() {
        return REQUEST_TYPE_TOKEN;
    }

    @Override
    public TypeToken<MessageResponseModel<me.alenalex.notaprisoncore.message.models.HeartbeatMessage.OnlineAnnouncementResponse>> responseModelClazz() {
        return RESPONSE_TYPE_TOKEN;
    }

    @Override
    public RequestReceivedCallback<me.alenalex.notaprisoncore.message.models.HeartbeatMessage.OnlineAnnouncementRequest, me.alenalex.notaprisoncore.message.models.HeartbeatMessage.OnlineAnnouncementResponse> onRequest() {
        return new RequestReceivedCallback<me.alenalex.notaprisoncore.message.models.HeartbeatMessage.OnlineAnnouncementRequest, me.alenalex.notaprisoncore.message.models.HeartbeatMessage.OnlineAnnouncementResponse>() {
            @Override
            public CompletableFuture<Optional<me.alenalex.notaprisoncore.message.models.HeartbeatMessage.OnlineAnnouncementResponse>> respond(MessageRequestModel<me.alenalex.notaprisoncore.message.models.HeartbeatMessage.OnlineAnnouncementRequest> message) {
                if(message.getSource().equals(getSourceName())){
                    logWarning("A new server/source is trying to/already boot up with source name "+message.getSource()+" from ["+message.getSourceAddress()+"]. A shutdown message has been sent back to the originating server");
                    return CompletableFuture.completedFuture(Optional.of(new me.alenalex.notaprisoncore.message.models.HeartbeatMessage.OnlineAnnouncementResponse()));
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
    public ResponseReceivedCallback<me.alenalex.notaprisoncore.message.models.HeartbeatMessage.OnlineAnnouncementRequest, me.alenalex.notaprisoncore.message.models.HeartbeatMessage.OnlineAnnouncementResponse> onResponse() {
        return new ResponseReceivedCallback<me.alenalex.notaprisoncore.message.models.HeartbeatMessage.OnlineAnnouncementRequest, me.alenalex.notaprisoncore.message.models.HeartbeatMessage.OnlineAnnouncementResponse>() {
            @Override
            public void process(MessageRequestModel<me.alenalex.notaprisoncore.message.models.HeartbeatMessage.OnlineAnnouncementRequest> request, MessageResponseModel<me.alenalex.notaprisoncore.message.models.HeartbeatMessage.OnlineAnnouncementResponse> response) {
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
        return MessageBusChannelConstants.HEARTBEAT_ANNOUNCEMENT_CHANNEL;
    }
}
