package me.alenalex.notaprisoncore.api.config.options;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.abstracts.AbstractConfigurationOption;
import me.alenalex.notaprisoncore.api.config.entry.BlockEntry;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class BlockChoiceSettings extends AbstractConfigurationOption {

    private int maxCombinationOfBlockChoices;
    private final HashSet<BlockEntry> blockEntryHashSet;

    public BlockChoiceSettings(Section section) {
        super(section);
        this.blockEntryHashSet = new HashSet<>();
    }

    @Override
    public void load() {
        this.maxCombinationOfBlockChoices = getSection().getInt("max-combination-of-block-choices");
        List<String> blockChoiceList = getSection().getStringList("block-choices");
        this.blockEntryHashSet.clear();
        for (String block : blockChoiceList) {
            Optional<BlockEntry> optionalBlockEntry = BlockEntry.fromString(block);
            optionalBlockEntry.ifPresent(this.blockEntryHashSet::add);
        }
    }
}
