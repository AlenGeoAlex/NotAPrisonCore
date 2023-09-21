package me.alenalex.notaprisoncore.message;

import java.util.UUID;

public abstract class AbstractMessageBuilder {

    private static AbstractMessageBuilder INSTANCE = null;

    public static AbstractMessageBuilder get(){
        if(INSTANCE == null)
            throw new LinkageError("A message builder Instance is not yet build! Please create a new MessageBuilder Instance!");

        return INSTANCE;
    }

    public AbstractMessageBuilder(){
    }
    public abstract String sourceName();

    public void build(){
        INSTANCE = this;
    }

    public <T> MessageRequestModel<T> buildRequest(T requestContext){
        String sourceId = generateSourceId();
        return new MessageRequestModel<T>(sourceId, sourceName(), requestContext);
    }

    public <T> MessageResponseModel<T> buildRequest(T responseContext, MessageRequestModel<?> requestModel){
        String sourceId = generateSourceId();
        return new MessageResponseModel<T>(sourceId, sourceName(), requestModel, responseContext);
    }

    public String generateSourceId(){
        return sourceName()+"-"+ UUID.randomUUID().toString();
    }
}
