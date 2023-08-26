package me.alenalex.notaprisoncore.api.entity.mine;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Optional;
import java.util.UUID;

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
    boolean hasIdentifier(String key);
    Optional<Location> getLocationOfIdentifier(String key);

}
