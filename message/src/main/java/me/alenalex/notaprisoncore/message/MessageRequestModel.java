package me.alenalex.notaprisoncore.message;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode
@ToString
public class MessageRequestModel<T> {

    @NotNull
    private final String sourceId;
    @NotNull
    private final String source;
    private final long generatedTimeStamp;
    private final T context;
    private final String sourceAddress;
    public MessageRequestModel(@NotNull String sourceId, @NotNull String source, @NotNull String sourceAddress ,T context) {
        this.sourceId = sourceId;
        this.source = source;
        this.generatedTimeStamp = System.currentTimeMillis();
        this.context = context;
        this.sourceAddress = sourceAddress;
    }

    public MessageRequestModel(@NotNull String sourceId, @NotNull String source, @NotNull String sourceAddress, long generatedTimeStamp, T context) {
        this.sourceId = sourceId;
        this.source = source;
        this.generatedTimeStamp = generatedTimeStamp;
        this.context = context;
        this.sourceAddress = sourceAddress;
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

    public T getContext() {
        return context;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }
}
