package me.alenalex.notaprisoncore.api.events.mine;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.entity.mine.IMine;
import me.alenalex.notaprisoncore.api.entity.user.IPrisonUserProfile;

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
}
