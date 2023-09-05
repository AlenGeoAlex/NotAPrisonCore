package me.alenalex.notaprisoncore.api.events.mine;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.entity.mine.IMine;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

@EqualsAndHashCode(callSuper = true)
@ToString
@Getter
public abstract class MineEvent extends Event {

    private final IMine mine;

    public MineEvent(IMine mine) {
        this.mine = mine;
    }

    public MineEvent(boolean isAsync, IMine mine) {
        super(isAsync);
        this.mine = mine;
    }

    @Override
    public HandlerList getHandlers() {
        return null;
    }


}
