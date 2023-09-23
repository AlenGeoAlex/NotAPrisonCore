package me.alenalex.notaprisoncore.paper.message.bus;

import com.google.common.reflect.TypeToken;
import me.alenalex.notaprisoncore.api.abstracts.message.AbstractOneWayMessageBus;
import me.alenalex.notaprisoncore.api.common.json.IJsonWrapper;
import me.alenalex.notaprisoncore.api.config.options.ServerConfiguration;
import me.alenalex.notaprisoncore.api.database.redis.IRedisDatabase;
import me.alenalex.notaprisoncore.message.MessageRequestModel;
import me.alenalex.notaprisoncore.message.interfaces.RequestReceivedCallback;
import me.alenalex.notaprisoncore.message.models.MineCreateMessage;
import me.alenalex.notaprisoncore.paper.constants.MessageBusChannelConstants;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public class MineCreationMessageBus extends AbstractOneWayMessageBus<MineCreateMessage> {

    private static final TypeToken<MessageRequestModel<MineCreateMessage>> MODEL_TYPE_TOKEN = new TypeToken<MessageRequestModel<MineCreateMessage>>() {};

    public MineCreationMessageBus(IRedisDatabase redisDatabase, IJsonWrapper jsonWrapper, ServerConfiguration configuration, String serverSourceAddress) {
        super(redisDatabase, jsonWrapper, configuration, serverSourceAddress);
    }

    @Override
    public TypeToken<MessageRequestModel<MineCreateMessage>> requestClazz() {
        return MODEL_TYPE_TOKEN;
    }

    @Override
    public @NotNull String channelName() {
        return MessageBusChannelConstants.MINE_CREATION_CHANNEL;
    }

    @Override
    public RequestReceivedCallback<MineCreateMessage, Void> onRequest() {
        return new RequestReceivedCallback<MineCreateMessage, Void>() {
            @Override
            public CompletableFuture<Optional<Void>> respond(MessageRequestModel<MineCreateMessage> message) {
                logInfo("A new mine has been created on "+message.getSource()+" ["+message.getSourceAddress()+"] for "+message.getContext().getOwnerId().toString()+" with the mine-id "+message.getContext().getMineId().toString()+" and meta id "+message.getContext().getMetaId().toString()+".");
                return null;
            }
        };
    }
}
