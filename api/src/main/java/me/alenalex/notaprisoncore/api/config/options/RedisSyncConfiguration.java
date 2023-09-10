package me.alenalex.notaprisoncore.api.config.options;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.abstracts.AbstractConfigurationOption;

@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
public class RedisSyncConfiguration extends AbstractConfigurationOption {

    private long redisNetworkWaitMillis;

    public RedisSyncConfiguration(Section section) {
        super(section);
    }

    @Override
    public void load() {
        this.redisNetworkWaitMillis = getSection().getLong("network-wait-millisecond");
    }

    @Override
    public ValidationResponse validate() {
        ValidationResponse.Builder builder = ValidationResponse.Builder.builder();
        if(redisNetworkWaitMillis < 500){
            builder.withWarnings("Its advised to set the network wait timeout to be more than 500 millis, This allows the host server to set the mine data to redis and the receiving server to wait the timeout period");
        }
        return builder.build();
    }
}
