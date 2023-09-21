package me.alenalex.notaprisoncore.message.interfaces;

import me.alenalex.notaprisoncore.message.MessageRequestModel;

import java.util.concurrent.CompletableFuture;

public interface RequestReceivedCallback<R, T> {

    CompletableFuture<T> respond(MessageRequestModel<R> message);

}
