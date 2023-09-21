package me.alenalex.notaprisoncore.message.interfaces;

import me.alenalex.notaprisoncore.message.MessageRequestModel;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface RequestReceivedCallback<R, T> {

    CompletableFuture<Optional<T>> respond(MessageRequestModel<R> message);

}
