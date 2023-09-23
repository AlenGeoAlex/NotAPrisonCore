package me.alenalex.notaprisoncore.api.queue;

public interface IQueueEntity<T> extends Comparable<IQueueEntity<T>> {

    T get();
    int getPriority();

}
