package me.alenalex.notaprisoncore.message.service;

import me.alenalex.notaprisoncore.message.MessageCommunicationStatus;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

public interface IMessageServiceBus<R> {

    @NotNull String channelName();
    MessageCommunicationStatus<R> sendMessage(R message) throws Exception;
    default CompletableFuture<MessageCommunicationStatus<R>> sendMessageAsync(R message){
        return CompletableFuture.supplyAsync(() -> {
            try {
                return sendMessage(message);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }
}
