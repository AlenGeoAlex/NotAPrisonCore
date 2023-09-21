package me.alenalex.notaprisoncore.message.service;

import me.alenalex.notaprisoncore.message.MessageCommunicationStatus;
import me.alenalex.notaprisoncore.message.MessageRequestModel;
import me.alenalex.notaprisoncore.message.interfaces.RequestValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public interface IMessageServiceBus<R> {

    @NotNull String channelName();
    MessageCommunicationStatus sendMessageSync(R message) throws Exception;
    default CompletableFuture<MessageCommunicationStatus> sendMessageAsync(R message){
        return CompletableFuture.supplyAsync(() -> {
            try {
                return sendMessageSync(message);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
