package me.alenalex.notaprisoncore.api.common.json;

import java.lang.reflect.Type;

public interface IJsonWrapper {

    public static final IJsonWrapper DEFAULT_INSTANCE = new GsonWrapper();

    String stringify(Object object);

    <T> T fromString(String jsonString, Class<T> type);

    <T> T fromString(String jsonString, Type type);

}
