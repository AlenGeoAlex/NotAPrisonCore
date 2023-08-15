package me.alenalex.notaprisoncore.paper.manager;

import me.alenalex.notaprisoncore.api.config.IPluginConfiguration;
import me.alenalex.notaprisoncore.api.enums.ConfigType;
import me.alenalex.notaprisoncore.api.exceptions.FailedConfigurationException;
import me.alenalex.notaprisoncore.api.managers.IConfigurationManager;
import me.alenalex.notaprisoncore.paper.config.PluginConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ConfigurationManager implements IConfigurationManager {

    private final PrisonManagers prisonManagers;
    private final PluginConfiguration pluginConfiguration;
    public ConfigurationManager(PrisonManagers prisonManagers) {
        this.prisonManagers = prisonManagers;
        this.pluginConfiguration = new PluginConfiguration(this);
    }

    public void initAndLoadConfiguration() {
        this.prisonManagers.getPluginInstance().getLogger().info("Starting to load configurations");
        try {
            this.prisonManagers.getPluginInstance().getLogger().info("- Configuration : In progress");
            this.pluginConfiguration.create();
            this.pluginConfiguration.load();
            this.prisonManagers.getPluginInstance().getLogger().info("- Configuration : Loaded");
        } catch (IOException e) {
            throw new FailedConfigurationException(ConfigType.PLUGIN, "An unknown error occurred while create/initializing the document", e);
        }
    }

    public PrisonManagers getPrisonManagers() {
        return prisonManagers;
    }

    @Override
    public @NotNull IPluginConfiguration getPluginConfiguration() {
        return pluginConfiguration;
    }

}
