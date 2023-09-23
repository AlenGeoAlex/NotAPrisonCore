package me.alenalex.notaprisoncore.api.abstracts.message;

import me.alenalex.notaprisoncore.api.exceptions.message.MessageBusExistsException;
import me.alenalex.notaprisoncore.message.models.HeartbeatMessage;
import me.alenalex.notaprisoncore.message.models.MineCreateMessage;
import me.alenalex.notaprisoncore.message.service.IMessageServiceBus;

public interface IMessageService {

    default void registerMessageBus(IMessageServiceBus<?> messageServiceBus) throws MessageBusExistsException {
        if(messageServiceBus instanceof AbstractOneWayMessageBus)
            registerMessageBus((AbstractOneWayMessageBus<?>) messageServiceBus);
        else if(messageServiceBus instanceof AbstractTwoWayMessageBus)
            registerMessageBus((AbstractTwoWayMessageBus<?,?>) messageServiceBus);
        else throw new IllegalStateException("No known message bus provider found!");
    }
    void registerMessageBus(AbstractOneWayMessageBus<?> oneWayMessageBus) throws MessageBusExistsException;
    void registerMessageBus(AbstractTwoWayMessageBus<?, ?> twoWayMessageBus) throws MessageBusExistsException;
    void unregisterMessageBus(String channelName);
    boolean isListening();
    IMessageServiceBus<?> getService(String messageChannel);
    IMessageServiceBus<HeartbeatMessage.OnlineAnnouncementRequest> getHeartbeatService();
    IMessageServiceBus<MineCreateMessage> getMineCreationService();
}
