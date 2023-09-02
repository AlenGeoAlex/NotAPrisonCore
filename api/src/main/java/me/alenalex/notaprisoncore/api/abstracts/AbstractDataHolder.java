package me.alenalex.notaprisoncore.api.abstracts;

import me.alenalex.notaprisoncore.api.entity.IEntityDataHolder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractDataHolder implements IEntityDataHolder {

    protected final ConcurrentHashMap<String, Object> dataHolder;

    public AbstractDataHolder(ConcurrentHashMap<String, Object> dataHolder) {
        this.dataHolder = dataHolder;
    }

    public AbstractDataHolder() {
        this.dataHolder = new ConcurrentHashMap<>();
    }


    @Override
    public <E> void set(String key, E value) {
        this.dataHolder.put(key, value);
    }

    @Override
    public Optional<Object> get(String key) {
        return Optional.ofNullable(this.dataHolder.get(key));
    }

    @Override
    public Optional<Object> getWithDefault(String key, Object defaultValue) {
        return Optional.ofNullable(this.dataHolder.getOrDefault(key, defaultValue));
    }

    @Override
    public <E> Optional<E> getAs(String key, Class<E> classType) {
        Object object = this.dataHolder.get(key);
        E response = null;
        try {
            response = classType.cast(object);
        }catch (Exception ignored){}
        return Optional.ofNullable(response);
    }

    @Override
    public <E> Optional<E> getAsWithDefault(String key, Class<E> classType, Object defaultValue) {
        Object object = this.dataHolder.get(key);
        if(object == null)
            object = defaultValue;

        E response = null;
        try {
            response = classType.cast(object);
        }catch (Exception ignored){}
        return Optional.ofNullable(response);
    }

    @Override
    public boolean has(String key) {
        return this.dataHolder.containsKey(key);
    }

    @Override
    public void remove(String key) {
        this.dataHolder.remove(key);
    }

    @Override
    public void clear() {
        this.dataHolder.clear();
    }

    @Override
    public Iterator<Object> valueIterator() {
        return new ArrayList<>(this.dataHolder.values()).iterator();
    }

}
