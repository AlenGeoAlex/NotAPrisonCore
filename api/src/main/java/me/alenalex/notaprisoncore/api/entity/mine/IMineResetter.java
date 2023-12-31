package me.alenalex.notaprisoncore.api.entity.mine;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IMineResetter {

    default boolean isResetOnProgress(){
        return currentWorker() != null;
    }
    long lastResetOn();
    IBlockChoices getBlockChoices();
    boolean hasTemporaryBlocks();
    IBlockChoices getTemporaryBlockChoices();
    IMineMeta getMineMeta();
    @Nullable IMineResetWorker currentWorker();
    @NotNull IMineResetWorker createWorker();
    IBlockChoices getDefaultBlockChoice();

}
