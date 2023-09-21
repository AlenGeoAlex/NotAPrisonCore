package me.alenalex.notaprisoncore.message.interfaces;

import me.alenalex.notaprisoncore.message.MessageRequestModel;
import me.alenalex.notaprisoncore.message.MessageResponseModel;

public interface ResponseReceivedCallback <R, T>{
    void process(MessageRequestModel<R> request, MessageResponseModel<T> response);

}
