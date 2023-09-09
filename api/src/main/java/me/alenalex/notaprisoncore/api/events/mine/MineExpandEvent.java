package me.alenalex.notaprisoncore.api.events.mine;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.entity.mine.IMine;
import org.bukkit.event.HandlerList;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class MineExpandEvent extends MineEvent{

    public MineExpandEvent(boolean isAsync, IMine mine) {
        super(isAsync, mine);
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
