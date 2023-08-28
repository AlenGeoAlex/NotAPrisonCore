package me.alenalex.notaprisoncore.paper.entity.mine;

import me.alenalex.notaprisoncore.api.config.entry.BlockEntry;
import me.alenalex.notaprisoncore.api.entity.mine.IBlockChoices;
import me.alenalex.notaprisoncore.api.provider.IRandomProvider;
import me.alenalex.notaprisoncore.paper.bootstrap.Bootstrap;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class BlockChoices implements IBlockChoices {

    private final List<BlockEntry> playerChoices;

    public BlockChoices() {
        this.playerChoices = new ArrayList<>();
    }

    public BlockChoices(List<BlockEntry> playerChoices) {
        this.playerChoices = playerChoices;
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
        HashSet<BlockEntry> blockList = bootstrap.getPluginInstance().getPrisonManagers().configurationManager().getPluginConfiguration().defaultMineConfiguration().getDefaultResetBlockList();
        this.playerChoices.addAll(blockList);
    }

    @Override
    public void removeChoice(BlockEntry entry) {
        this.playerChoices.remove(entry);
    }

    @Override
    public @NotNull Iterator<BlockEntry> iterator() {
        return new ArrayList<>(playerChoices).iterator();
    }

    @Override
    public @NotNull BlockEntry atRandom() {
        return IRandomProvider.getRandomElementFrom(playerChoices);
    }
}
