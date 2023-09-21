package me.alenalex.notaprisoncore.message.service;

import me.alenalex.notaprisoncore.message.MessageRequestModel;
import me.alenalex.notaprisoncore.message.MessageResponseModel;
import me.alenalex.notaprisoncore.message.interfaces.RequestReceivedCallback;
import me.alenalex.notaprisoncore.message.interfaces.RequestValidator;
import me.alenalex.notaprisoncore.message.interfaces.ResponseReceivedCallback;
import me.alenalex.notaprisoncore.message.interfaces.ResponseValidator;
import org.jetbrains.annotations.Nullable;

public interface IBiDirectionalMessageServiceBus<R, T> extends IMessageServiceBus<R> {
    default String responseChannelName(){
        return channelName()+".response";
    }
    RequestReceivedCallback<R, T> onRequest();
    ResponseReceivedCallback<R, T> onResponse();
    @Nullable
    default RequestValidator<R> validateRequest(){
        return null;
    }
    @Nullable
    default ResponseValidator<R, T> validateResponse(){
        return null;
    }
}
