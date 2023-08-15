package me.alenalex.notaprisoncore.api.config.entry;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.common.json.JsonWrapper;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
public class LocationEntry {

    private final String worldName;
    private final double x;
    private final double y;
    private final double z;
    private final float yaw;
    private final float pitch;

    public LocationEntry(String worldName, double x, double y, double z) {
        this.worldName = worldName;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = 0F;
        this.pitch = 0F;
    }

    public LocationEntry(Section configSection) {
        this.worldName = configSection.getString("world");
        this.x = configSection.getDouble("x");
        this.y = configSection.getDouble("y");
        this.z = configSection.getDouble("z");
        this.pitch = configSection.getFloat("pitch");
        this.yaw = configSection.getFloat("yaw");
    }

    public HashMap<String, Object> toMap(){
        final HashMap<String, Object> objectHashMap = new HashMap<>();
        objectHashMap.put("world", this.worldName);
        objectHashMap.put("x", x);
        objectHashMap.put("y", y);
        objectHashMap.put("z", z);
        objectHashMap.put("pitch", pitch);
        objectHashMap.put("yaw", yaw);
        return objectHashMap;
    }

    public String toJson(){
        return JsonWrapper.WRAPPER.get().toJson(this);
    }

    public Optional<Location> to(){
        World world = Bukkit.getWorld(worldName);
        return Optional.of(new Location(world, x, y, z, yaw, pitch));
    }

    public Optional<World> getWorld(){
        return Optional.ofNullable(Bukkit.getWorld(worldName));
    }
}
