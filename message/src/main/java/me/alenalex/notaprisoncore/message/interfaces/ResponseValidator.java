package me.alenalex.notaprisoncore.message.interfaces;

import me.alenalex.notaprisoncore.message.MessageRequestModel;
import me.alenalex.notaprisoncore.message.MessageResponseModel;

public interface ResponseValidator<R, T>{
    boolean validate(MessageRequestModel<R> request, MessageResponseModel<T> response);

}
