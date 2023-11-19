package me.alenalex.notaprisoncore.paper.message.bus;

import com.google.common.reflect.TypeToken;
import me.alenalex.notaprisoncore.api.abstracts.message.AbstractOneWayMessageBus;
import me.alenalex.notaprisoncore.api.common.json.IJsonWrapper;
import me.alenalex.notaprisoncore.api.config.options.ServerConfiguration;
import me.alenalex.notaprisoncore.api.core.ICoreApi;
import me.alenalex.notaprisoncore.api.database.redis.IRedisDatabase;
import me.alenalex.notaprisoncore.api.entity.mine.IMine;
import me.alenalex.notaprisoncore.api.entity.mine.MineMessage;
import me.alenalex.notaprisoncore.message.MessageRequestModel;
import me.alenalex.notaprisoncore.message.interfaces.RequestReceivedCallback;
import me.alenalex.notaprisoncore.paper.NotAPrisonCore;
import me.alenalex.notaprisoncore.paper.bootstrap.Bootstrap;
import me.alenalex.notaprisoncore.paper.constants.MessageBusChannelConstants;
import me.alenalex.notaprisoncore.paper.wrapper.GsonWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class MineCommunicationMessageBus extends AbstractOneWayMessageBus<MineMessage> {

    private static final TypeToken<MessageRequestModel<MineMessage>> REQUEST_TYPE_TOKEN = new TypeToken<MessageRequestModel<MineMessage>>() {};

    public MineCommunicationMessageBus(IRedisDatabase redisDatabase, IJsonWrapper jsonWrapper, ServerConfiguration configuration, String serverSourceAddress) {
        super(redisDatabase, jsonWrapper, configuration, serverSourceAddress);
    }

    @Override
    public TypeToken<MessageRequestModel<MineMessage>> requestClazz() {
        return REQUEST_TYPE_TOKEN;
    }

    @Override
    public @NotNull String channelName() {
        return MessageBusChannelConstants.MINE_COMMUNICATION_CHANNEL;
    }

    @Override
    public RequestReceivedCallback<MineMessage, Void> onRequest() {

        return new RequestReceivedCallback<MineMessage, Void>() {
            @Override
            public CompletableFuture<Optional<Void>> respond(MessageRequestModel<MineMessage> message) {
                MineMessage context = message.getContext();
                UUID mineId = context.getMineId();

                ICoreApi api = getPluginApi();
                IMine mine = api.getHolder().getMineDataHolder().get(mineId);

                if(mine == null){
                    if(context.canIgnoreIfAbsent())
                        return null;

                    api.getStore().getMineStore().id(mineId)
                            .whenComplete((iMine, throwable) -> {
                                if(throwable != null){
                                    logSevere("Failed to load the mine for internal messaging "+ GsonWrapper.singleton().stringify(context));
                                    throwable.printStackTrace();
                                }

                                iMine.ifPresent(context::execute);
                            });
                }


                return null;
            }
        };
    }

}
