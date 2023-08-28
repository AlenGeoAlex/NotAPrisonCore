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
    private int minMetaReservedCount;
    public ServerConfiguration(Section section) {
        super(section);
    }

    @Override
    public void load() {
        this.serverName = getSection().getString("name");
        this.metaReservationCount = getSection().getInt("meta-reservation-count");
        this.minMetaReservedCount = getSection().getInt("min-meta-reserved-count");
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
        if(minMetaReservedCount >= metaReservationCount){
            this.minMetaReservedCount = metaReservationCount - 1;
            builder.withWarnings("Please set min-meta-reserved-count a value less than meta-reservation-count. meta-reservation-count simply means the minimum amount of metas/location the server should always keep. This should be always a value less than than meta-reservation-count. For now, I have set it to "+minMetaReservedCount);
        }

        if(minMetaReservedCount <= 0){
            builder.withWarnings("The min-meta-reserved-count is set to 0, that means the no one would be able to claim new mines.");
        }
        return builder.build();
    }
}
