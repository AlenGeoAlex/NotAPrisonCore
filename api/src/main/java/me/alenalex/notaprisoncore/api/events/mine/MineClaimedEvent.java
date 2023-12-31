package me.alenalex.notaprisoncore.api.events.mine;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.entity.mine.IMine;
import me.alenalex.notaprisoncore.api.entity.user.IPrisonUserProfile;
import org.bukkit.event.HandlerList;

@EqualsAndHashCode(callSuper = true)
@ToString
@Getter
public class MineClaimedEvent extends MineEvent{


    private final IPrisonUserProfile userProfile;

    public MineClaimedEvent(IMine mine, IPrisonUserProfile userProfile) {
        super(mine);
        this.userProfile = userProfile;
    }

    public MineClaimedEvent(boolean isAsync, IMine mine, IPrisonUserProfile userProfile) {
        super(isAsync, mine);
        this.userProfile = userProfile;
    }

    private static final HandlerList HANDLERS = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }
}
