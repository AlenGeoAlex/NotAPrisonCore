package me.alenalex.notaprisoncore.api.config.options;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.abstracts.AbstractConfigurationOption;

@EqualsAndHashCode(callSuper = true)
@ToString
@Getter
public class ServerConfiguration extends AbstractConfigurationOption {

    private String serverName;
    private int metaReservationCount;
    public ServerConfiguration(Section section) {
        super(section);
    }

    @Override
    public void load() {
        this.serverName = getSection().getString("name");
        this.metaReservationCount = getSection().getInt("meta-reservation-count");
    }

    @Override
    public ValidationResponse validate() {
        ValidationResponse.Builder builder = ValidationResponse.Builder.builder();
        if(metaReservationCount <= 0){
            builder.withErrors("Meta Reservation Count should be greater than 0");
        }
        if(serverName.equals("Prison-A")){
            builder.withWarnings("Prison-A is the default name used, If not intentional please change the configuration");
        }
        return builder.build();
    }
}
