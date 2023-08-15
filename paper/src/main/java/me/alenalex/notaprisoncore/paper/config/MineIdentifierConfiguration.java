package me.alenalex.notaprisoncore.paper.config;

import me.alenalex.notaprisoncore.api.abstracts.AbstractFileConfiguration;
import me.alenalex.notaprisoncore.paper.manager.ConfigurationManager;

import java.io.File;

public class MineIdentifierConfiguration extends AbstractFileConfiguration {
    private final ConfigurationManager configurationManager;
    public MineIdentifierConfiguration(ConfigurationManager configurationManager) {
        super(new File(configurationManager.getPrisonManagers().getPluginInstance().getBukkitPlugin().getDataFolder(), "identifiers.yml"),
                configurationManager.getPrisonManagers().getPluginInstance().getBukkitPlugin().getResource("identifiers.yml")
        );
        this.configurationManager = configurationManager;
    }

    @Override
    public void load() {

    }
}
