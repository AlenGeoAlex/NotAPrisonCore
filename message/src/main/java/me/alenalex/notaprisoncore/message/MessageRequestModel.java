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

    public MessageRequestModel(@NotNull String sourceId,@NotNull String source, T context) {
        this.sourceId = sourceId;
        this.source = source;
        this.generatedTimeStamp = System.currentTimeMillis();
        this.context = context;
    }

    public MessageRequestModel(@NotNull String sourceId, @NotNull String source, long generatedTimeStamp, T context) {
        this.sourceId = sourceId;
        this.source = source;
        this.generatedTimeStamp = generatedTimeStamp;
        this.context = context;
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
}
