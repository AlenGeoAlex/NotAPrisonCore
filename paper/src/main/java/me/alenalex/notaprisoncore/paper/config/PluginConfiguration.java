package me.alenalex.notaprisoncore.paper.config;


import me.alenalex.notaprisoncore.api.config.IPluginConfiguration;
import me.alenalex.notaprisoncore.api.config.options.*;
import me.alenalex.notaprisoncore.api.abstracts.AbstractConfiguration;
import me.alenalex.notaprisoncore.api.enums.ConfigType;
import me.alenalex.notaprisoncore.paper.manager.ConfigurationManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;

public class PluginConfiguration extends AbstractConfiguration implements IPluginConfiguration {

    private final ConfigurationManager configurationManager;
    private SQLConfiguration sqlConfiguration;
    private MineWorldConfiguration mineWorldConfiguration;
    private ServerConfiguration serverConfiguration;
    private DefaultMineConfiguration defaultMineConfiguration;
    private ResetterConfiguration resetterConfiguration;
    private RedisConfiguration redisConfiguration;
    public PluginConfiguration(ConfigurationManager configurationManager) {
        super(configurationManager.getPrisonManagers().getPluginInstance().getLogger(),
                new File(configurationManager.getPrisonManagers().getPluginInstance().getBukkitPlugin().getDataFolder(), "config.yml"),
                configurationManager.getPrisonManagers().getPluginInstance().getBukkitPlugin().getResource("config.yml")
        );
        this.configurationManager = configurationManager;
    }


    @Override
    public @NotNull SQLConfiguration sqlConfiguration() {
        return sqlConfiguration;
    }

    @Override
    public @NotNull MineWorldConfiguration mineWorldConfiguration() {
        return mineWorldConfiguration;
    }

    @Override
    public @NotNull ServerConfiguration serverConfiguration() {
        return serverConfiguration;
    }

    @Override
    public @NotNull DefaultMineConfiguration defaultMineConfiguration() {
        return defaultMineConfiguration;
    }

    @Override
    public @NotNull RedisConfiguration redisConfiguration() {
        return redisConfiguration;
    }

    @Override
    public @NotNull ResetterConfiguration resetterConfiguration() {
        return resetterConfiguration;
    }

    @Override
    protected Field[] getFields() {
        return this.getClass().getDeclaredFields();
    }

    @Override
    protected ConfigType configType() {
        return ConfigType.PLUGIN;
    }
}
