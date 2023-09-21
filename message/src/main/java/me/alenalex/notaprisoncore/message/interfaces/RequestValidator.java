package me.alenalex.notaprisoncore.message.interfaces;


import me.alenalex.notaprisoncore.message.MessageRequestModel;

public interface RequestValidator<R> {
    boolean validate(MessageRequestModel<R> request);

}
