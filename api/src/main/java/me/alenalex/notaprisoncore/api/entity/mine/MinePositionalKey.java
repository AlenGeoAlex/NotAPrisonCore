package me.alenalex.notaprisoncore.api.entity.mine;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.config.entry.LocationEntry;
import org.bukkit.Material;

import java.util.Optional;

@AllArgsConstructor
@ToString
@EqualsAndHashCode
public class MinePositionalKey {

    public static Optional<MinePositionalKey> from(Section section){
        boolean required = section.getBoolean("required");
        String key = section.getRouteAsString();
        String identifierRaw = section.getString("identifier");
        boolean readDirection = section.getBoolean("read-direction");
        float defaultYaw = section.getFloat("default-yaw");

        Material identifier = Material.getMaterial(identifierRaw);
        if(identifier == null)
            return Optional.empty();

        return Optional.of(new MinePositionalKey(required, key, identifier, readDirection, defaultYaw, null));
    }

    @Getter
    private final boolean required;
    @Getter
    private final String key;
    @Getter
    private final Material identifier;
    @Getter
    private final boolean readDirection;
    @Getter
    private final float defaultYaw;
    private LocationEntry locationEntry;

    public Optional<LocationEntry> getLocation(){
        return Optional.ofNullable(this.locationEntry);
    }

}
