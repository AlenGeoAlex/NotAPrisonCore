package me.alenalex.notaprisoncore.message.service;

import me.alenalex.notaprisoncore.message.MessageRequestModel;
import me.alenalex.notaprisoncore.message.interfaces.RequestReceivedCallback;
import me.alenalex.notaprisoncore.message.interfaces.RequestValidator;
import org.jetbrains.annotations.Nullable;

public interface IUniDirectionalMessageServiceBus<R> extends IMessageServiceBus<R> {
    RequestReceivedCallback<R, Void> onRequest();
    @Nullable
    default RequestValidator<R> validateRequest(){
        return null;
    }

}
