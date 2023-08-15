package me.alenalex.notaprisoncore.api.config.entry;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.Material;

import java.util.Optional;

@Getter
@EqualsAndHashCode
@ToString
public class MaterialEntry {

    public static Optional<MaterialEntry> from(Section section, String key){
        String rawMaterial = section.getString(key);
        return from(rawMaterial);
    }

    public static Optional<MaterialEntry> from(String rawMaterial){
        if(rawMaterial == null || rawMaterial.isEmpty())
            return Optional.empty();
        Material material = null;
        Byte data = null;
        if(rawMaterial.contains(":")){
            String[] blockDataSplit = rawMaterial.split(":");
            if(blockDataSplit.length != 2){
                return Optional.empty();
            }
            data = Byte.parseByte(blockDataSplit[1]);
            rawMaterial = blockDataSplit[0];
        }

        material = Material.getMaterial(rawMaterial);
        if(material == null)
            return Optional.empty();

        return Optional.of(new MaterialEntry(material, data));
    }

    private final Material type;
    private final Byte data;

    private MaterialEntry(Material type, Byte data) {
        this.type = type;
        this.data = data;
    }
}
