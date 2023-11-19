package me.alenalex.notaprisoncore.api.common.json.serializer;

import com.google.gson.*;
import java.lang.reflect.Type;

public class AbstractClassDeserializer<T> implements JsonDeserializer<T> {
    @Override
    public T deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        JsonObject typeInfo = jsonObject.getAsJsonObject("$.internal-type");
        if (typeInfo == null) {
            return null;
        }
        String packageName = typeInfo.get("package").getAsString();
        String className = typeInfo.get("class").getAsString();
        try {
            Class<?> clazz = Class.forName(packageName + "." + className);
            return context.deserialize(jsonObject.get("data"), clazz);
        } catch (ClassNotFoundException e) {
            return null;
        }
    }
}