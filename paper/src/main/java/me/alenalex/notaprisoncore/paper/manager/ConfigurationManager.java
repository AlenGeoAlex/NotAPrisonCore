package me.alenalex.notaprisoncore.paper.manager;

import me.alenalex.notaprisoncore.api.config.IBlockChoiceConfiguration;
import me.alenalex.notaprisoncore.api.config.IMineIdentifierConfiguration;
import me.alenalex.notaprisoncore.api.config.IPluginConfiguration;
import me.alenalex.notaprisoncore.api.enums.ConfigType;
import me.alenalex.notaprisoncore.api.exceptions.FailedConfigurationException;
import me.alenalex.notaprisoncore.api.managers.IConfigurationManager;
import me.alenalex.notaprisoncore.paper.config.BlockChoiceConfiguration;
import me.alenalex.notaprisoncore.paper.config.MineIdentifierConfiguration;
import me.alenalex.notaprisoncore.paper.config.PluginConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class ConfigurationManager implements IConfigurationManager {

    private final PrisonManagers prisonManagers;
    private final PluginConfiguration pluginConfiguration;
    private final MineIdentifierConfiguration mineIdentifierConfiguration;
    private final BlockChoiceConfiguration blockChoiceConfiguration;
    public ConfigurationManager(PrisonManagers prisonManagers) {
        this.prisonManagers = prisonManagers;
        this.pluginConfiguration = new PluginConfiguration(this);
        this.mineIdentifierConfiguration = new MineIdentifierConfiguration(this);
        this.blockChoiceConfiguration = new BlockChoiceConfiguration(this);
    }

    public void initAndLoadConfiguration() {
        this.prisonManagers.getPluginInstance().getLogger().info("Starting to load configurations");
        try {
            this.prisonManagers.getPluginInstance().getLogger().info("- Configuration : In progress");
            this.pluginConfiguration.create();
            this.pluginConfiguration.load();
            this.prisonManagers.getPluginInstance().getLogger().info("- Configuration : Loaded");
        } catch (Exception e) {
            throw new FailedConfigurationException(ConfigType.PLUGIN, "An unknown error occurred while create/initializing the document", e);
        }

        this.prisonManagers.getPluginInstance().getLogger().info("Starting to load block identifiers configuration");
        try {
            this.prisonManagers.getPluginInstance().getLogger().info("- Block Identifier Configuration : In progress");
            this.mineIdentifierConfiguration.create();
            this.mineIdentifierConfiguration.load();
            this.prisonManagers.getPluginInstance().getLogger().info("- Block Identifier Configuration : Loaded");
        } catch (Exception e) {
            throw new FailedConfigurationException(ConfigType.IDENTIFIER, "An unknown error occurred while create/initializing the document", e);
        }

        this.prisonManagers.getPluginInstance().getLogger().info("Starting to load block choices configuration");
        try {
            this.prisonManagers.getPluginInstance().getLogger().info("- Block Choice Configuration : In progress");
            this.blockChoiceConfiguration.create();
            this.blockChoiceConfiguration.load();
            this.prisonManagers.getPluginInstance().getLogger().info("- Block Choice Configuration : Loaded ["+this.blockChoiceConfiguration.getBlockChoiceSettings().getBlockEntryHashSet().size()+"] blocks.");
        }catch (Exception e){
            throw new FailedConfigurationException(ConfigType.BLOCK_CHOICES, "An unknown error occurred while create/initializing the document", e);
        }
    }

    public PrisonManagers getPrisonManagers() {
        return prisonManagers;
    }

    @Override
    public @NotNull IPluginConfiguration getPluginConfiguration() {
        return pluginConfiguration;
    }

    @Override
    public @NotNull IMineIdentifierConfiguration getMineIdentifierConfiguration() {
        return mineIdentifierConfiguration;
    }

    @Override
    public @NotNull IBlockChoiceConfiguration getBlockChoiceConfiguration() {
        return blockChoiceConfiguration;
    }

}
