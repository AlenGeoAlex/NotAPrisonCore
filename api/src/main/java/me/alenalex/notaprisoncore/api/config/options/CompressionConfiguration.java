package me.alenalex.notaprisoncore.api.config.options;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.abstracts.AbstractConfigurationOption;

@ToString
@EqualsAndHashCode(callSuper = true)
@Getter
public class CompressionConfiguration extends AbstractConfigurationOption {
    private boolean compressMineLocalData;
    private boolean compressUserProfileLocalData;
    public CompressionConfiguration(Section section) {
        super(section);
        this.load();
    }

    public CompressionConfiguration() {
        super(null);
    }

    @Override
    public void load() {
        this.compressMineLocalData = getSection().getBoolean("mine-local-meta");
        this.compressUserProfileLocalData = getSection().getBoolean("user-profile-local-meta");
    }
}
