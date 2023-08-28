package me.alenalex.notaprisoncore.api.config;

import me.alenalex.notaprisoncore.api.config.options.DefaultMineConfiguration;
import me.alenalex.notaprisoncore.api.config.options.MineWorldConfiguration;
import me.alenalex.notaprisoncore.api.config.options.SQLConfiguration;
import me.alenalex.notaprisoncore.api.config.options.ServerConfiguration;
import org.jetbrains.annotations.NotNull;

public interface IPluginConfiguration {

    @NotNull SQLConfiguration sqlConfiguration();
    @NotNull MineWorldConfiguration mineWorldConfiguration();
    @NotNull ServerConfiguration serverConfiguration();
    @NotNull DefaultMineConfiguration defaultMineConfiguration();
}
