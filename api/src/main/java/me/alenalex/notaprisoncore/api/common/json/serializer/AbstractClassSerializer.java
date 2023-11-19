package me.alenalex.notaprisoncore.api.common.json.serializer;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

import java.lang.reflect.Type;

public class AbstractClassSerializer<T> implements JsonSerializer<T> {
    @Override
    public JsonElement serialize(T src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("data", context.serialize(src, src.getClass()));
        JsonObject typeInfo = new JsonObject();
        typeInfo.addProperty("package", src.getClass().getPackage().getName());
        typeInfo.addProperty("class", src.getClass().getSimpleName());
        result.add("$.internal-type", typeInfo);
        return result;
    }
}