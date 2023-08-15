package me.alenalex.notaprisoncore.api.config.options;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.abstracts.AbstractConfigurationOption;

@EqualsAndHashCode(callSuper = true)
@ToString
@Getter
public class ServerConfiguration extends AbstractConfigurationOption {

    private String serverName;
    public ServerConfiguration(Section section) {
        super(section);
    }

    @Override
    public void load() {
        this.serverName = getSection().getString("name");
    }
}
