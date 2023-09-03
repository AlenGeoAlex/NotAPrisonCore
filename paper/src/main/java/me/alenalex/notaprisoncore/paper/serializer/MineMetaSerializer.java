package me.alenalex.notaprisoncore.paper.serializer;

import com.google.common.reflect.TypeToken;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.regions.CuboidRegion;
import me.alenalex.notaprisoncore.api.entity.mine.IMineMeta;
import me.alenalex.notaprisoncore.api.exceptions.FailedSerializationException;
import me.alenalex.notaprisoncore.paper.entity.mine.MineMeta;
import me.alenalex.notaprisoncore.paper.wrapper.GsonWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MineMetaSerializer extends TypeAdapter<IMineMeta> {

    private static final Type ADDITIONAL_IDENTIFIER_TYPE = new TypeToken<HashMap<String, Location>>() {
    }.getType();
    @Override
    public void write(JsonWriter out, IMineMeta value) throws IOException {
        out.beginObject();
        out.name("id");
        out.value(value.getMetaId().toString());
        out.name("spawn-point");
        GsonWrapper.singleton().gson().toJson(value.getSpawnPoint(), Location.class, out);
        out.name("lower-schematic-region");
        GsonWrapper.singleton().gson().toJson(value.getMineSchematicLowerPoint(), Vector.class, out);
        out.name("upper-schematic-region");
        GsonWrapper.singleton().gson().toJson(value.getMineSchematicUpperPoint(), Vector.class, out);
        out.name("lower-mining-point");
        GsonWrapper.singleton().gson().toJson(value.getLowerMiningPoint(), Location.class, out);
        out.name("upper-mining-point");
        GsonWrapper.singleton().gson().toJson(value.getUpperMiningPoint(), Location.class, out);
        out.name("location-identifiers");
        GsonWrapper.singleton().gson().toJson(value.getLocationIdentifier(), ADDITIONAL_IDENTIFIER_TYPE, out);
        out.endObject();
    }

    @Override
    public IMineMeta read(JsonReader in) throws IOException {
        UUID metaId = null;
        Location lowerMiningPoint = null;
        Location upperMiningPoint = null;
        Location spawnPoint = null;
        Vector lowerSchematicRegion = null;
        Vector upperSchematicRegion = null;
        HashMap<String, Location> locationIdentifier = null;

        in.beginObject();
        while (in.hasNext()) {
            String name = in.nextName();
            switch (name) {
                case "id":
                    metaId = UUID.fromString(in.nextString());
                    break;
                case "lower-mining-point":
                    lowerMiningPoint = GsonWrapper.singleton().gson().fromJson(in, Location.class);
                    break;
                case "upper-mining-point":
                    upperMiningPoint = GsonWrapper.singleton().gson().fromJson(in, Location.class);
                    break;
                case "spawn-point":
                    spawnPoint = GsonWrapper.singleton().gson().fromJson(in, Location.class);
                    break;
                case "location-identifiers":
                    locationIdentifier = GsonWrapper.singleton().gson().fromJson(in, ADDITIONAL_IDENTIFIER_TYPE);
                    break;
                case "lower-schematic-region":
                    lowerSchematicRegion =  GsonWrapper.singleton().gson().fromJson(in, Vector.class);
                    break;
                case "upper-schematic-region":
                    upperSchematicRegion = GsonWrapper.singleton().gson().fromJson(in, Vector.class);
                    break;
                default:
                    // Handle unknown fields if needed
                    in.skipValue();
                    //; // Don't forget to include this break statement
            }
        }
        in.endObject();
        if(lowerMiningPoint == null || upperMiningPoint == null || lowerSchematicRegion == null || upperSchematicRegion == null || spawnPoint == null)
            throw new FailedSerializationException("MineMeta", "Unknown", null);

        return new MineMeta(metaId, new CuboidRegion(BukkitUtil.toVector(lowerSchematicRegion), BukkitUtil.toVector(upperSchematicRegion)), lowerMiningPoint, upperMiningPoint, spawnPoint, locationIdentifier);
    }

    private HashMap<String, Location> readLocationIdentifiers(JsonReader in) throws IOException {
        HashMap<String, Location> locationIdentifiers = new HashMap<>();

        in.beginObject();
        while (in.hasNext()) {
            String key = in.nextName(); // Read the identifier key
            Location location = GsonWrapper.GsonUtils.readLocation(in); // Use the readLocation helper method to read the Location object
            locationIdentifiers.put(key, location);
        }
        in.endObject();

        return locationIdentifiers;
    }

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
