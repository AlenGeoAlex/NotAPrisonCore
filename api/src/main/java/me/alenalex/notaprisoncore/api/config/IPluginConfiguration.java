package me.alenalex.notaprisoncore.api.config;

import me.alenalex.notaprisoncore.api.config.options.*;
import org.jetbrains.annotations.NotNull;

public interface IPluginConfiguration {

    @NotNull SQLConfiguration sqlConfiguration();
    @NotNull MineWorldConfiguration mineWorldConfiguration();
    @NotNull ServerConfiguration serverConfiguration();
    @NotNull DefaultMineConfiguration defaultMineConfiguration();

    @NotNull ResetterConfiguration resetterConfiguration();
}
