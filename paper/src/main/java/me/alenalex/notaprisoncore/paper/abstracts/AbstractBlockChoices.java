package me.alenalex.notaprisoncore.paper.abstracts;

import me.alenalex.notaprisoncore.api.config.entry.BlockEntry;
import me.alenalex.notaprisoncore.api.entity.mine.IBlockChoices;
import me.alenalex.notaprisoncore.api.provider.IRandomProvider;
import me.alenalex.notaprisoncore.paper.bootstrap.Bootstrap;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public abstract class AbstractBlockChoices implements IBlockChoices {

    protected final List<BlockEntry> playerChoices;


    public AbstractBlockChoices(List<BlockEntry> playerChoices) {
        this.playerChoices = playerChoices;
    }

    public AbstractBlockChoices() {
        this.playerChoices = new ArrayList<>();
    }

    @Override
    public void addChoice(BlockEntry... entry) {
        for (BlockEntry blockEntry : entry) {
            if(blockEntry == null)
                continue;

            this.playerChoices.add(blockEntry);
        }
    }

    @Override
    public void addChoices(Collection<BlockEntry> blockEntries) {
        for (BlockEntry blockEntry : blockEntries) {
            if(blockEntry == null)
                continue;

            this.playerChoices.add(blockEntry);
        }
    }

    @Override
    public void setChoices(Collection<BlockEntry> entryCollections) {
        this.playerChoices.clear();
        addChoices(entryCollections);
    }

    @Override
    public void clearAndSetDefault() {
        this.playerChoices.clear();
        Bootstrap bootstrap = (Bootstrap) Bootstrap.getJavaPlugin();
        HashSet<BlockEntry> blockList = bootstrap.getPluginInstance().getPrisonManagers().getConfigurationManager().getPluginConfiguration().getDefaultMineConfiguration().getDefaultResetBlockList();
        this.playerChoices.addAll(blockList);
    }

    @Override
    public void removeChoice(BlockEntry entry) {
        this.playerChoices.remove(entry);
    }

    @Override
    public int getChoiceCount() {
        return this.playerChoices.size();
    }

    @Override
    public List<BlockEntry> getChoices() {
        return new ArrayList<>(playerChoices);
    }

    @Override
    public @NotNull Iterator<BlockEntry> iterator() {
        return new ArrayList<>(playerChoices).iterator();
    }


    @Override
    public @NotNull BlockEntry atRandom() {
        return IRandomProvider.getRandomFromList(playerChoices);
    }

}
