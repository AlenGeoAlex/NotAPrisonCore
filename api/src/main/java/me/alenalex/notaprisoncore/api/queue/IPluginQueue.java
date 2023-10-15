package me.alenalex.notaprisoncore.api.queue;

import me.alenalex.notaprisoncore.api.exceptions.FailedQueueOperationException;

import java.util.List;

public interface IPluginQueue<T> {

    long getActiveCount();
    boolean enqueue(T entity);
    T dequeue();
    List<T> dequeue(int count);
    boolean remove(T entity);
    long positionOf(T entity);
    boolean contains(T entity);

}
