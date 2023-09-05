package me.alenalex.notaprisoncore.api.config.options;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.abstracts.AbstractConfigurationOption;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class RedisConfiguration extends AbstractConfigurationOption {

    private String host;
    private String password;
    private int port;
    private String user;

    public RedisConfiguration(Section section) {
        super(section);
    }

    @Override
    public void load() {
        this.host = getSection().getString("host");
        this.password = getSection().getString("password");
        this.port = getSection().getInt("port");
        this.user = getSection().getString("user");
    }

    @Override
    public ValidationResponse validate() {
        ValidationResponse.Builder builder = ValidationResponse.Builder.builder();
        if(host == null || host.isEmpty()){
            builder.withErrors("No host provided for redis configuration");
        }

        return builder.build();
    }
}
