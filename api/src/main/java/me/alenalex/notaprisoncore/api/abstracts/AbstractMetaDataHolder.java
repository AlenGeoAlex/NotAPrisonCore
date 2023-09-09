package me.alenalex.notaprisoncore.api.abstracts;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.entity.IEntityMetaDataHolder;
import me.alenalex.notaprisoncore.api.exceptions.meta.FailedMetaDataInitialization;
import me.alenalex.notaprisoncore.api.exceptions.meta.IllegalMetaData;
import org.apache.commons.lang3.SerializationUtils;

import java.lang.reflect.Constructor;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@ToString
@EqualsAndHashCode
public abstract class AbstractMetaDataHolder implements IEntityMetaDataHolder {

    protected final ConcurrentHashMap<String, Object> dataHolder;

    public AbstractMetaDataHolder(ConcurrentHashMap<String, Object> dataHolder) {
        this.dataHolder = dataHolder;
    }

    public AbstractMetaDataHolder() {
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

    public String encode(){
        byte[] serializedData = SerializationUtils.serialize(this.dataHolder);
        return Base64.getEncoder().encodeToString(serializedData);
    }

    public static <T extends AbstractMetaDataHolder> T decode(String base64, Class<T> targetType) throws FailedMetaDataInitialization {
        if(base64 == null || base64.isEmpty())
            throw new IllegalMetaData("The passed meta data is either corrupted or empty");

        byte[] decodedData = Base64.getDecoder().decode(base64);
        ConcurrentHashMap<String, Object> decodedMap = null;
        try {
            decodedMap = (ConcurrentHashMap<String, Object>) SerializationUtils.deserialize(decodedData);
        }catch (Exception e){
            throw new IllegalMetaData("Failed to deserialize/convert the meta data", e);
        }

        T instance;
        try {
            Constructor<T> constructor = targetType.getDeclaredConstructor(ConcurrentHashMap.class);
            instance = constructor.newInstance(decodedMap);
        }catch (Exception e){
            e.printStackTrace();
            throw new FailedMetaDataInitialization("Failed to create the meta data class, Is the constructor with ConcurrentHashMap present in the class? Check stack trace for more info", e);
        }

        return instance;
    }

}
