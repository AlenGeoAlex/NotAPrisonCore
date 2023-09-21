package me.alenalex.notaprisoncore.message;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;


@EqualsAndHashCode
@ToString
public class MessageResponseModel<T> {

    @NotNull
    private final String sourceId;
    @NotNull
    private final String source;
    private final long generatedTimeStamp;
    @NotNull
    private final String targetId;
    @NotNull
    private final String target;
    private final T context;
    @NotNull
    private final String sourceAddress;
    @NotNull
    private final String targetAddress;

    public MessageResponseModel(@NotNull String sourceId, @NotNull String source, @NotNull String sourceAddress , @NotNull MessageRequestModel<?> requestModel, T context) {
        this.sourceId = sourceId;
        this.source = source;
        this.generatedTimeStamp = System.currentTimeMillis();
        this.target = requestModel.getSource();
        this.targetId = requestModel.getSourceId();
        this.context = context;
        this.sourceAddress = sourceAddress;
        this.targetAddress = requestModel.getSourceAddress();

    }

    @NotNull
    public String getSourceId() {
        return sourceId;
    }

    @NotNull
    public String getSource() {
        return source;
    }

    public long getGeneratedTimeStamp() {
        return generatedTimeStamp;
    }

    @NotNull
    public String getTargetId() {
        return targetId;
    }

    @NotNull
    public String getTarget() {
        return target;
    }

    @NotNull
    public T getContext() {
        return context;
    }

    public @NotNull String getSourceAddress() {
        return sourceAddress;
    }

    public String getTargetAddress() {
        return targetAddress;
    }
}
