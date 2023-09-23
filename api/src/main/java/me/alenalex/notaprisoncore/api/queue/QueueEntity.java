package me.alenalex.notaprisoncore.api.queue;

import com.google.common.base.Objects;
import lombok.AllArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public abstract class QueueEntity<T> implements IQueueEntity<T> {

    private final T value;
    private final int priority;

    @Override
    public T get() {
        return value;
    }

    @Override
    public int getPriority() {
        return priority;
    }

    @Override
    public int compareTo(IQueueEntity<T> other) {
        return -Integer.compare(this.getPriority(), other.getPriority());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof QueueEntity)) return false;
        QueueEntity<?> that = (QueueEntity<?>) o;
        return Objects.equal(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
