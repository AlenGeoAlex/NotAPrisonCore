package me.alenalex.notaprisoncore.api.config;

import me.alenalex.notaprisoncore.api.config.options.*;
import org.jetbrains.annotations.NotNull;

public interface IPluginConfiguration {

    @NotNull SQLConfiguration getSqlConfiguration();
    @NotNull MineWorldConfiguration getMineWorldConfiguration();
    @NotNull ServerConfiguration getServerConfiguration();
    @NotNull DefaultMineConfiguration getDefaultMineConfiguration();
    @NotNull RedisConfiguration getRedisConfiguration();
    @NotNull ResetterConfiguration getResetterConfiguration();
    @NotNull RedisSyncConfiguration getRedisSyncConfiguration();
    @NotNull ClaimQueueConfiguration getClaimQueueConfiguration();
}
