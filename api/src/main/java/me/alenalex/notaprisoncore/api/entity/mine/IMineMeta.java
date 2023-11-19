package me.alenalex.notaprisoncore.api.entity.mine;

import com.sk89q.worldedit.regions.Region;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IMineMeta {
    UUID getMetaId();
    boolean isInsideMine(Location location);
    default boolean isInsideMine(Player player){
        return isInsideMine(player.getLocation());
    }
    boolean isInsideMiningRegion(Location location);
    default boolean isInsideMiningRegion(Player player){
        return isInsideMiningRegion(player.getLocation());
    }
    Location getLowerMiningPoint();
    Location getUpperMiningPoint();
    Location getSpawnPoint();
    Vector getMineSchematicUpperPoint();
    Vector getMineSchematicLowerPoint();
    Region getMineRegion();
    void setRegion(Region region);
    boolean updateIfChanged(Region region);
    boolean hasIdentifier(String key);
    Optional<Location> getLocationOfIdentifier(String key);
    HashMap<String, Location> getLocationIdentifier();
    CompletableFuture<Boolean> setSpawnPoint(Location location);
}
