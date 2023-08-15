package me.alenalex.notaprisoncore.api.entity.mine;

import me.alenalex.notaprisoncore.api.entity.IEntityDataHolder;
import me.alenalex.notaprisoncore.api.enums.MineAccess;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.UUID;

public interface IMine {
    UUID getId();
    IMineMeta getMeta();
    IEntityDataHolder getSharedDataHolder();
    IEntityDataHolder getLocalDataHolder();
    MineAccess access();

    //TODO: Remember to kick out other players inside the mine if
    MineAccess access(MineAccess access);
    IMineSocialConnections getMineSocials();
    default boolean isInsideMine(Location location){
        return getMeta().isInsideMine(location);
    }
    default boolean isInsideMine(Player player){
        return isInsideMine(player.getLocation());
    }
    default boolean isInsideMiningRegion(Location location){
        return getMeta().isInsideMiningRegion(location);
    }
    default boolean isInsideMiningRegion(Player player){
        return isInsideMiningRegion(player.getLocation());
    }
    default void close(){
        access(MineAccess.CLOSED);
    }
    default void open(){
        access(MineAccess.OPEN);
    }
}
