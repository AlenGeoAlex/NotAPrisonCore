package me.alenalex.notaprisoncore.api.managers;

import me.alenalex.notaprisoncore.api.config.IPluginConfiguration;
import org.jetbrains.annotations.NotNull;

/**
 * All the configuration related stuffs of the plugin
 * would be accessible from here
 */
public interface IConfigurationManager {

    /**
     * The main configuration of the plugin
     * @return The plugin configuration
     */
    @NotNull IPluginConfiguration getPluginConfiguration();

}
