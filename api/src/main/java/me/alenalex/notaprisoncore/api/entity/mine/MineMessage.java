package me.alenalex.notaprisoncore.api.entity.mine;

import lombok.Getter;

import java.util.UUID;

@Getter
public abstract class MineMessage {

    public MineMessage(UUID mineId) {
        this.mineId = mineId;
        this.ignoreIfAbsent = setIgnoreIfAbsent();
    }

    private final UUID mineId;
    private final boolean ignoreIfAbsent;
    protected abstract boolean setIgnoreIfAbsent();
    public abstract void execute(IMine mine);
    public boolean canIgnoreIfAbsent(){
        return ignoreIfAbsent;
    }
}
