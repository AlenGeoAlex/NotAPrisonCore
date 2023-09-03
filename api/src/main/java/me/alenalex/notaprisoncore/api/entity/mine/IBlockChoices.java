package me.alenalex.notaprisoncore.api.entity.mine;

import me.alenalex.notaprisoncore.api.config.entry.BlockEntry;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

public interface IBlockChoices {

    void addChoice(BlockEntry... entry);
    void addChoices(Collection<BlockEntry> blockEntries);
    void setChoices(Collection<BlockEntry> entryCollections);
    void clearAndSetDefault();
    void removeChoice(BlockEntry entry);
    List<BlockEntry> getChoices();
    @NotNull Iterator<BlockEntry> iterator();
    @NotNull BlockEntry atRandom();
    @NotNull String toJson();
}
