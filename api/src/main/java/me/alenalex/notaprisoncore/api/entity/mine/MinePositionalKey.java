package me.alenalex.notaprisoncore.api.entity.mine;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import dev.dejvokep.boostedyaml.route.Route;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.config.entry.LocationEntry;
import org.bukkit.Material;

import java.util.Optional;

@ToString
@EqualsAndHashCode
public class MinePositionalKey {

    private MinePositionalKey(Builder builder) {
        required = builder.required;
        key = builder.key;
        identifier = builder.identifier;
        readDirection = builder.readDirection;
        defaultYaw = builder.defaultYaw;
        locationEntry = builder.locationEntry;
    }

    private MinePositionalKey(boolean required, String key, Material identifier, boolean readDirection, float defaultYaw, LocationEntry locationEntry) {
        this.required = required;
        this.key = key;
        this.identifier = identifier;
        this.readDirection = readDirection;
        this.defaultYaw = defaultYaw;
        this.locationEntry = locationEntry;
    }

    public static Optional<MinePositionalKey> from(Section section){
        boolean required = section.getBoolean("required");
        Route route = section.getRoute();
        String key = route.get(route.length() - 1).toString();
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


    public static final class Builder {
        private boolean required;
        private String key;
        private Material identifier;
        private boolean readDirection;
        private float defaultYaw;
        private LocationEntry locationEntry;

        private Builder() {
        }

        public static Builder builder() {
            return new Builder();
        }

        public Builder withRequired(boolean required) {
            this.required = required;
            return this;
        }

        public Builder withKey(String key) {
            this.key = key;
            return this;
        }

        public Builder withIdentifier(Material identifier) {
            this.identifier = identifier;
            return this;
        }

        public Builder withReadDirection(boolean readDirection) {
            this.readDirection = readDirection;
            return this;
        }

        public Builder withDefaultYaw(float defaultYaw) {
            this.defaultYaw = defaultYaw;
            return this;
        }

        public Builder withLocationEntry(LocationEntry locationEntry) {
            this.locationEntry = locationEntry;
            return this;
        }

        public MinePositionalKey build() {
            return new MinePositionalKey(this);
        }
    }
}
