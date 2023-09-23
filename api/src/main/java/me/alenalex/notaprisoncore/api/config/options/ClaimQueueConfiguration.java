package me.alenalex.notaprisoncore.api.config.options;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.abstracts.AbstractConfigurationOption;
import me.alenalex.notaprisoncore.api.enums.ConfigType;
import me.alenalex.notaprisoncore.api.exceptions.FailedConfigurationException;

import java.util.LinkedHashMap;
import java.util.TreeMap;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class ClaimQueueConfiguration extends AbstractConfigurationOption {

    private boolean enabled;
    private int queueLimit;
    private boolean changeHookEnabled;
    private PriorityConfiguration priorityConfiguration;
    public ClaimQueueConfiguration(Section section) {
        super(section);
    }

    @Override
    public void load() {
        this.enabled = getSection().getBoolean("enabled");
        this.queueLimit = getSection().getInt("max-in-queue");
        this.changeHookEnabled = getSection().getBoolean("enable-change-hook");
        this.priorityConfiguration = new PriorityConfiguration(getSection().getSection("priority"));
        this.priorityConfiguration.load();
        ValidationResponse validate = this.priorityConfiguration.validate();
        if(validate == null || validate.getStatus() == ValidationResponse.Status.OK){
            return;
        }

        if(validate.getStatus() == ValidationResponse.Status.INVALID){
            throw new FailedConfigurationException(ConfigType.PLUGIN, "Failed to load the configuration. Please resolve the errors above", null);
        }
    }
}
