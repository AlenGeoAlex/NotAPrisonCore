package me.alenalex.notaprisoncore.api.config.options;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.abstracts.AbstractConfigurationOption;
import me.alenalex.notaprisoncore.api.config.entry.LocationEntry;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class MineWorldConfiguration extends AbstractConfigurationOption {

    private String worldName;
    private int mineDistance;
    private LocationEntry defaultLocation;
    private boolean createVoidWorldIfAbsent;
    public MineWorldConfiguration(Section section) {
        super(section);
    }

    @Override
    public void load() {
        this.worldName = getSection().getString("world-name");
        this.mineDistance = getSection().getInt("mine-distance");
        this.createVoidWorldIfAbsent = getSection().getBoolean("create-void-world-if-absent");
        this.defaultLocation = new LocationEntry(getSection().getSection("default-location"));
    }

    @Override
    public ValidationResponse validate() {
        ValidationResponse.Builder builder = ValidationResponse.Builder
                .builder();
        if(defaultLocation == null)
        {
            builder.withErrors("No default location has been provided");
        }

        String defaultLocationWorldName = this.defaultLocation.getWorldName();

        if(!defaultLocationWorldName.equals(worldName)){
            builder.withWarnings("The world-name parameter and default-location.world doesn't match. The plugin will try to attempt to replace the default-location.world");
            this.defaultLocation = new LocationEntry(worldName, defaultLocation.getX(), defaultLocation.getY(), defaultLocation.getZ(), defaultLocation.getYaw(), defaultLocation.getPitch());
        }

        if(mineDistance <= 0){
            builder.withErrors("Mine border distance should be greater than 0");
        }

        return builder.build();
    }


}
