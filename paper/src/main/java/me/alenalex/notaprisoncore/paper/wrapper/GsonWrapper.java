package me.alenalex.notaprisoncore.paper.wrapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.stream.JsonReader;
import me.alenalex.notaprisoncore.api.common.json.IJsonWrapper;
import me.alenalex.notaprisoncore.api.entity.mine.IMine;
import me.alenalex.notaprisoncore.api.entity.mine.IMineMeta;
import me.alenalex.notaprisoncore.paper.entity.mine.Mine;
import me.alenalex.notaprisoncore.paper.entity.mine.MineMeta;
import me.alenalex.notaprisoncore.paper.serializer.BlockEntrySerializer;
import me.alenalex.notaprisoncore.paper.serializer.LocationSerializer;
import me.alenalex.notaprisoncore.api.config.entry.BlockEntry;
import me.alenalex.notaprisoncore.paper.serializer.MineMetaSerializer;
import me.alenalex.notaprisoncore.paper.serializer.MineSerializer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.lang.reflect.Type;

public class GsonWrapper implements IJsonWrapper {

    private static final IJsonWrapper JSON_WRAPPER = new GsonWrapper();

    public static IJsonWrapper singleton(){
        return JSON_WRAPPER;
    }

    private final Gson gson;

    public GsonWrapper() {
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Location.class, new LocationSerializer())
                .registerTypeAdapter(BlockEntry.class, new BlockEntrySerializer())
                .registerTypeAdapter(MineMeta.class, new MineMetaSerializer())
                .registerTypeAdapter(IMineMeta.class, new MineMetaSerializer())
                .registerTypeAdapter(IMine.class, new MineSerializer())
                .registerTypeAdapter(Mine.class, new MineSerializer())
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

    @Override
    public Gson gson() {
        return gson;
    }

    public static class GsonUtils {
        public static Vector readVector(JsonReader in) throws IOException {
            int x = 0;
            int y = 0;
            int z = 0;

            in.beginObject();
            while (in.hasNext()) {
                String fieldName = in.nextName();
                switch (fieldName) {
                    case "x":
                        x = in.nextInt();
                        break;
                    case "y":
                        y = in.nextInt();
                        break;
                    case "z":
                        z = in.nextInt();
                        break;
                    default:
                        in.skipValue(); // Ignore unknown fields
                        break;
                }
            }
            in.endObject();

            return new Vector(x, y, z);
        }

        // Helper method to read a Location object
        public static Location readLocation(JsonReader in) throws IOException {
            String worldName = null;
            double x = 0.0;
            double y = 0.0;
            double z = 0.0;
            float yaw = 0.0f;
            float pitch = 0.0f;

            in.beginObject();
            while (in.hasNext()) {
                String fieldName = in.nextName();
                switch (fieldName) {
                    case "world":
                        worldName = in.nextString();
                        break;
                    case "x":
                        x = in.nextDouble();
                        break;
                    case "y":
                        y = in.nextDouble();
                        break;
                    case "z":
                        z = in.nextDouble();
                        break;
                    case "yaw":
                        yaw = (float) in.nextDouble();
                        break;
                    case "pitch":
                        pitch = (float) in.nextDouble();
                        break;
                    default:
                        in.skipValue(); // Ignore unknown fields
                        break;
                }
            }
            in.endObject();

            if (worldName == null) {
                throw new IOException("Missing 'world' field in Location");
            }

            World world = Bukkit.getWorld(worldName);
            if (world == null) {
                throw new IOException("Invalid world name: " + worldName);
            }

            return new Location(world, x, y, z, yaw, pitch);
        }
    }


}
