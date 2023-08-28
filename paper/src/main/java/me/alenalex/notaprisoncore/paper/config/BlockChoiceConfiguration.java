package me.alenalex.notaprisoncore.paper.config;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.abstracts.AbstractConfiguration;
import me.alenalex.notaprisoncore.api.config.IBlockChoiceConfiguration;
import me.alenalex.notaprisoncore.api.config.options.BlockChoiceSettings;
import me.alenalex.notaprisoncore.api.enums.ConfigType;
import me.alenalex.notaprisoncore.paper.manager.ConfigurationManager;

import java.io.File;
import java.lang.reflect.Field;

@Getter
@ToString
@EqualsAndHashCode(callSuper = false)
public class BlockChoiceConfiguration extends AbstractConfiguration implements IBlockChoiceConfiguration {

    private final ConfigurationManager configurationManager;

    private BlockChoiceSettings blockChoiceSettings;

    public BlockChoiceConfiguration(ConfigurationManager configurationManager) {
        super(configurationManager.getPrisonManagers().getPluginInstance().getLogger(),
                new File(configurationManager.getPrisonManagers().getPluginInstance().getBukkitPlugin().getDataFolder(), "block-choices.yml"),
                configurationManager.getPrisonManagers().getPluginInstance().getBukkitPlugin().getResource("block-choices.yml")
        );
        this.configurationManager = configurationManager;
    }

    @Override
    protected Field[] getFields() {
        return this.getClass().getDeclaredFields();
    }

    @Override
    protected ConfigType configType() {
        return ConfigType.BLOCK_CHOICES;
    }
}
