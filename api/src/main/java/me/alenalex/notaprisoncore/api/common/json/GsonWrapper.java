package me.alenalex.notaprisoncore.api.common.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.alenalex.notaprisoncore.api.common.serializer.BlockEntrySerializer;
import me.alenalex.notaprisoncore.api.common.serializer.LocationSerializer;
import me.alenalex.notaprisoncore.api.config.entry.BlockEntry;
import org.bukkit.Location;

import java.lang.reflect.Type;

public class GsonWrapper implements IJsonWrapper{
    private final Gson gson;

    public GsonWrapper() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Location.class, new LocationSerializer())
                .registerTypeAdapter(BlockEntry.class, new BlockEntrySerializer())
                .serializeNulls()
                .create();
    }

    @Override
    public String stringify(Object object) {
        if(object == null)
            return null;

        return gson.toJson(object);
    }

    @Override
    public <T> T fromString(String jsonString, Class<T> type) {
        if(jsonString == null || jsonString.isEmpty())
            return null;

        return gson.fromJson(jsonString, type);
    }

    @Override
    public <T> T fromString(String jsonString, Type type) {
        if(jsonString == null || jsonString.isEmpty())
            return null;

        return gson.fromJson(jsonString, type);
    }
}
