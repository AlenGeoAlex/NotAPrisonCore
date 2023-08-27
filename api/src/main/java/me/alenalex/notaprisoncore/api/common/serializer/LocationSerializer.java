package me.alenalex.notaprisoncore.api.common.serializer;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import me.alenalex.notaprisoncore.api.exceptions.FailedSerializationException;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.io.IOException;

public class LocationSerializer extends TypeAdapter<Location> {

    @Override
    public void write(JsonWriter out, Location value) throws IOException {
        out.beginObject();
        out.name("world");
        out.value(value.getWorld().getName());
        out.name("x");
        out.value(value.getX());
        out.name("y");
        out.value(value.getY());
        out.name("z");
        out.value(value.getZ());
        out.name("yaw");
        out.value(value.getYaw());
        out.name("pitch");
        out.value(value.getPitch());
        out.endObject();
    }

    @Override
    public Location read(JsonReader in) throws IOException {
        Location location = null;
        String fieldname = null;
        String worldName = null;
        double x = 0D;
        double y = 0D;
        double z = 0D;
        float pitch = 0f;
        float yaw = 0f;


        in.beginObject();
        while (in.hasNext()) {
            JsonToken token = in.peek();

            if (token.equals(JsonToken.NAME)) {
                fieldname = in.nextName();
            }

            if ("world".equals(fieldname)) {
                token = in.peek();
                worldName = in.nextString();
            }

            if ("x".equals(fieldname)) {
                token = in.peek();
                x = in.nextDouble();
            }

            if ("y".equals(fieldname)) {
                token = in.peek();
                y = in.nextDouble();
            }

            if ("z".equals(fieldname)) {
                token = in.peek();
                z = in.nextDouble();
            }

            if ("pitch".equals(fieldname)) {
                token = in.peek();
                pitch = (float) in.nextDouble();
            }

            if ("yaw".equals(fieldname)) {
                token = in.peek();
                yaw = (float) in.nextDouble();
            }
        }
        in.endObject();

        if(worldName == null || worldName.isEmpty())
            return null;

        World world = Bukkit.getWorld(worldName);

        if(world == null)
        {
            throw new FailedSerializationException("Location", in.toString(), null);
        }

        location = new Location(world, x, y, z, yaw, pitch);
        return location;
    }
}
