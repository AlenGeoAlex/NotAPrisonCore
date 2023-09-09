package me.alenalex.notaprisoncore.api.events.mine;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.entity.mine.IMine;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;

import java.util.concurrent.atomic.AtomicBoolean;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class PreMineExpandEvent extends MineEvent implements Cancellable {
    private AtomicBoolean cancelled;
    public PreMineExpandEvent(IMine mine) {
        super(mine);
    }

    public PreMineExpandEvent(boolean isAsync, IMine mine) {
        super(isAsync, mine);
    }

    @Override
    public boolean isCancelled() {
        return cancelled.get();
    }

    @Override
    public void setCancelled(boolean cancel) {
        cancelled.set(cancel);
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
