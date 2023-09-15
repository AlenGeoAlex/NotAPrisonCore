package me.alenalex.notaprisoncore.api.entity.user;


import lombok.ToString;
import me.alenalex.notaprisoncore.api.enums.SocialStatus;
import org.jetbrains.annotations.NotNull;

import java.sql.Timestamp;
import java.util.UUID;

public interface IUserSocial {

    UUID getSocialId();
    UUID getSourceId();
    UUID getTargetId();
    SocialStatus getStatus();
    Timestamp getRequestedAt();
    Timestamp getActedAt();
    default boolean isFriends(){
        return getStatus() == SocialStatus.FRIENDS;
    }
    default boolean isRequestPending(){
        return getStatus() == SocialStatus.PENDING;
    }
    default boolean isBlocked(){
        return getStatus() == SocialStatus.BLOCKED;
    }

    @NotNull String getTargetName();

    void setSocialStatus(SocialStatus status, boolean alertPlayers);

    void setSocialStatus(SocialStatus status);

    void alertPlayers();
}
