package me.alenalex.notaprisoncore.api.common.json;

import com.google.gson.Gson;

import java.lang.reflect.Type;

public interface IJsonWrapper {

    String stringify(Object object);

    <T> T fromString(String jsonString, Class<T> type);

    <T> T fromString(String jsonString, Type type);

    Gson gson();

}
