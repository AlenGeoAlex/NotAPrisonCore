package me.alenalex.notaprisoncore.message;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@EqualsAndHashCode
@ToString
@Getter
public class MessageCommunicationStatus<R> {

    public static <R> MessageCommunicationStatus<R> success(MessageRequestModel<R> requestModel){
        return new MessageCommunicationStatus<R>(requestModel.getSource(), requestModel.getSourceId(),System.currentTimeMillis(), true, null, requestModel);
    }

    public static <R> MessageCommunicationStatus<R> fail(String reason){
        return new MessageCommunicationStatus<R>(null, null, 0, false, reason, null);
    }

    public MessageCommunicationStatus(String source, String sourceId, long sentOn, boolean success, String reason, MessageRequestModel<R> context) {
        this.source = source;
        this.sourceId = sourceId;
        this.sentOn = sentOn;
        this.success = success;
        this.reason = reason;
        this.context = context;
    }

    private final String source;
    private final String sourceId;
    private final long sentOn;
    private final boolean success;
    private final String reason;
    private final MessageRequestModel<R> context;



}
