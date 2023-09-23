package me.alenalex.notaprisoncore.api.queue;


import java.util.ArrayList;
import java.util.HashMap;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.PriorityBlockingQueue;

public abstract class PluginQueue<T> implements IPluginQueue<T> {
    public PluginQueue() {}
    private final Queue<T> claimQueue = new PriorityBlockingQueue<>();
    private final HashMap<T, Integer> queuePositionCache = new HashMap<>();
    @Override
    public long getActiveCount() {
        return claimQueue.size();
    }

    @Override
    public boolean enqueue(T entity){
        if(claimQueue.contains(entity))
            return false;

        claimQueue.add(entity);
        updatePositions();
        return true;
    }

    @Override
    public T dequeue() {
        T uuid = claimQueue.poll();
        if (uuid != null) {
            queuePositionCache.remove(uuid);
        }
        updatePositions();
        return uuid;
    }

    @Override
    public List<T> dequeue(int count) {
        List<T> val = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            T dequeue = dequeue();
            if(dequeue == null)
                break;

            val.add(dequeue);
        }
        updatePositions();
        return val;
    }

    @Override
    public boolean remove(T entity) {
        claimQueue.remove(entity);
        updatePositions();
        return true;
    }

    @Override
    public long positionOf(T entity) {
        return queuePositionCache.getOrDefault(entity, -1);
    }

    @Override
    public boolean contains(T entity) {
        return this.claimQueue.contains(entity);
    }

    private void updatePositions(){
        int position = 0;
        this.queuePositionCache.clear();
        for (T uuid : claimQueue) {
            queuePositionCache.put(uuid, position);
            position++;
        }
    }

    public HashMap<T, Integer> getQueuePositionCache(){
        return new HashMap<>(queuePositionCache);
    }
}
