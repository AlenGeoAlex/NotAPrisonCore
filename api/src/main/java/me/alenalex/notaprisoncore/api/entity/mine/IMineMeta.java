package me.alenalex.notaprisoncore.api.entity.mine;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface IMineMeta {

    boolean isInsideMine(Location location);
    default boolean isInsideMine(Player player){
        return isInsideMine(player.getLocation());
    }
    boolean isInsideMiningRegion(Location location);
    default boolean isInsideMiningRegion(Player player){
        return isInsideMiningRegion(player.getLocation());
    }

}
