package me.alenalex.notaprisoncore.api.entity;

import java.util.Iterator;
import java.util.Optional;

public interface IEntityDataHolder {
    <E> void set(String key, E value);
    Optional<Object> get(String key);
    Optional<Object> getWithDefault(String key, Object defaultValue);
    <E> Optional<E> getAs(String key, Class<E> classType);
    <E> Optional<E> getAsWithDefault(String key, Class<E> classType ,Object defaultValue);
    boolean has(String key);
    void remove(String key);
    void clear();
    Iterator<Object> valueIterator();
}
