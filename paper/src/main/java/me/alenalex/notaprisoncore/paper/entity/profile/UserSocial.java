package me.alenalex.notaprisoncore.paper.entity.profile;

import com.google.common.base.Objects;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.entity.user.IUserSocial;
import me.alenalex.notaprisoncore.api.enums.SocialStatus;
import me.alenalex.notaprisoncore.paper.locale.Message;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.UUID;

@ToString
public class UserSocial implements IUserSocial {

    private UUID id;
    private UUID sourceId;
    private UUID targetId;
    private SocialStatus socialStatus;
    private Timestamp requestedAt;
    private Timestamp actedAt;
    private String targetName;

    public UserSocial(UUID id, UUID sourceId, UUID targetId, SocialStatus socialStatus, Timestamp requestedAt, Timestamp actedAt) {
        this.id = id;
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.socialStatus = socialStatus;
        this.requestedAt = requestedAt;
        this.actedAt = actedAt;
        this.targetName = null;
    }

    public UserSocial(UUID sourceId, UUID targetId, SocialStatus socialStatus, Timestamp requestedAt, Timestamp actedAt) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.socialStatus = socialStatus;
        this.requestedAt = requestedAt;
        this.actedAt = actedAt;
        this.targetName = null;
    }

    public UserSocial(UUID sourceId, UUID targetId, SocialStatus socialStatus, Timestamp requestedAt, Timestamp actedAt, String targetName) {
        this.sourceId = sourceId;
        this.targetId = targetId;
        this.socialStatus = socialStatus;
        this.requestedAt = requestedAt;
        this.actedAt = actedAt;
        this.targetName = targetName;
    }

    @Override
    public UUID getSocialId() {
        return id;
    }

    @Override
    public UUID getSourceId() {
        return sourceId;
    }

    @Override
    public UUID getTargetId() {
        return targetId;
    }

    @Override
    public SocialStatus getStatus() {
        return socialStatus;
    }

    @Override
    public Timestamp getRequestedAt() {
        return requestedAt;
    }

    @Override
    @Nullable
    public Timestamp getActedAt() {
        return actedAt;
    }

    @Override
    @NotNull
    public String getTargetName(){
        if(targetName == null){
            cacheTargetName();
        }

        return targetName;
    }

    public void setId(UUID id) throws IllegalAccessException {
        if(this.id != null){
            throw new IllegalAccessException("Tried to set id for a user social which already has "+this.getSocialId());
        }

        this.id = id;
    }

    private void cacheTargetName(){
        Player targetPlayer = null;

        try {
            targetPlayer = Bukkit.getPlayer(targetId);
        }catch (Exception ignored){}

        if(targetPlayer == null){
            this.targetName = "Unknown";
        }else{
            this.targetName = targetPlayer.getName();
        }
    }

    @Override
    public void setSocialStatus(SocialStatus status, boolean alertPlayers){
        if(status == this.socialStatus)
            return;

        this.socialStatus = status;
        this.actedAt = Timestamp.from(Instant.now());
        if(alertPlayers){
            this.alertPlayers();
        }
    }

    @Override
    public void setSocialStatus(SocialStatus status){
        this.setSocialStatus(status, true);
    }

    @Override
    public void alertPlayers(){
        Message sourceMessage = null;
        Message targetMessage = null;

        //TODO send
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserSocial)) return false;
        UserSocial that = (UserSocial) o;
        return Objects.equal(id, that.id) && Objects.equal(getSourceId(), that.getSourceId()) && Objects.equal(getTargetId(), that.getTargetId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, getSourceId(), getTargetId());
    }
}
