package me.alenalex.notaprisoncore.api.entity.mine;

import me.alenalex.notaprisoncore.api.entity.user.IPrisonUserProfile;

import java.util.Collection;
import java.util.UUID;

public interface IMineSocialConnections {

    boolean isFriend(UUID userId);
    default boolean isFriend(IPrisonUserProfile profile){
        return isFriend(profile.getUserId());
    }
    boolean isBlocked(UUID userId);
    default boolean isBlocked(IPrisonUserProfile profile){
        return isBlocked(profile.getUserId());
    }
    boolean isRequested(UUID userId);
    default boolean isRequested(IPrisonUserProfile profile){
        return isRequested(profile.getUserId());
    }
    boolean request(IPrisonUserProfile userProfile);
    boolean block(IPrisonUserProfile userProfile);
    boolean add(IPrisonUserProfile userProfile);
    Collection<UUID> getFriends();
    Collection<UUID> getBlocked();
    Collection<UUID> getRequested();
}
