package me.alenalex.notaprisoncore.message;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.jetbrains.annotations.NotNull;

@EqualsAndHashCode
@ToString
public class MessageRequestModel<T> {

    @NotNull
    private final String sourceId;
    @NotNull
    private final String source;
    @Getter
    private final long generatedTimeStamp;
    @Getter
    private final T context;
    @Getter
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

}
