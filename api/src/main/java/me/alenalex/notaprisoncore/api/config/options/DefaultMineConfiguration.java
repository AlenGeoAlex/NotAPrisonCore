package me.alenalex.notaprisoncore.api.config.options;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.abstracts.AbstractConfigurationOption;
import me.alenalex.notaprisoncore.api.enums.MineAccess;

import java.util.Arrays;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class DefaultMineConfiguration extends AbstractConfigurationOption {

    private MineAccess defaultMineAccess;
    public DefaultMineConfiguration(Section section) {
        super(section);
    }

    @Override
    public void load() {
        this.defaultMineAccess = getSection().getOptionalEnum("mine-access", MineAccess.class).orElse(null);
    }

    @Override
    public ValidationResponse validate() {
        ValidationResponse.Builder builder = ValidationResponse.Builder.builder();
        if(defaultMineAccess == null){
            builder.withWarnings("Failed to match mine-access. Please pass in a value with in ["+ Arrays.toString(MineAccess.values()) +"]. Falling back to CLOSED");
            defaultMineAccess = MineAccess.CLOSED;
        }

        return builder.build();
    }
}
