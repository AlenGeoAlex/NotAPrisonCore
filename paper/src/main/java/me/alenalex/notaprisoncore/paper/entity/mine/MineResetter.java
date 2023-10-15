package me.alenalex.notaprisoncore.paper.entity.mine;

import me.alenalex.notaprisoncore.api.entity.mine.IBlockChoices;
import me.alenalex.notaprisoncore.api.entity.mine.IMineMeta;
import me.alenalex.notaprisoncore.api.entity.mine.IMineResetWorker;
import me.alenalex.notaprisoncore.api.entity.mine.IMineResetter;
import me.alenalex.notaprisoncore.paper.entity.mine.worker.FaweResetWorker;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MineResetter implements IMineResetter {

    private final BlockChoices blockChoices;
    private final TemporaryBlockChoices temporaryBlockChoices;
    private final MineMeta meta;
    private IMineResetWorker resetWorker;
    private long lastResetOn;

    public MineResetter(BlockChoices blockChoices, TemporaryBlockChoices temporaryBlockChoices,  MineMeta meta) {
        this.blockChoices = blockChoices;
        this.meta = meta;
        this.temporaryBlockChoices = temporaryBlockChoices;
        this.lastResetOn = System.currentTimeMillis();
    }

    @Override
    public long lastResetOn() {
        return 0;
    }

    @Override
    public IBlockChoices getBlockChoices() {
        return blockChoices;
    }

    @Override
    public boolean hasTemporaryBlocks() {
        return this.temporaryBlockChoices.getChoiceCount() > 0;
    }

    @Override
    public IBlockChoices getTemporaryBlockChoices() {
        return this.temporaryBlockChoices;
    }

    @Override
    public IMineMeta getMineMeta() {
        return meta;
    }

    @Override
    public @Nullable IMineResetWorker currentWorker() {
        return resetWorker;
    }


    @Override
    public @NotNull IMineResetWorker createWorker() {
        return new FaweResetWorker(this);
    }

    @Override
    public IBlockChoices getDefaultBlockChoice() {
        if(hasTemporaryBlocks())
            return getTemporaryBlockChoices();

        return getBlockChoices();
    }

    public void resetStarted(IMineResetWorker resetWorker){
        this.lastResetOn = System.currentTimeMillis();
        this.resetWorker = resetWorker;
    }

    public void completed(){
        this.resetWorker = null;
    }
}
