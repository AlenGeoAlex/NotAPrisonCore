package me.alenalex.notaprisoncore.api.config.entry;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.material.MaterialData;

import java.util.Optional;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
@Getter
public class BlockEntry {

    private final Material materialType;
    private final byte data;

    public static Optional<BlockEntry> fromString(String blockEntryString){
        if(blockEntryString.contains(":")){
            String[] blockDataSplit = blockEntryString.split(":");
            if(blockDataSplit.length != 2){
                return Optional.empty();
            }
            Material material = Material.getMaterial(blockDataSplit[0]);
            if(material == null){
                return Optional.empty();
            }
            ItemStack stack = new ItemStack(material);
            try {
                byte b = Byte.parseByte(blockDataSplit[1]);
                stack.setDurability(b);
            }catch (Exception e){
                return Optional.empty();
            }
            return Optional.of(new BlockEntry(material, stack.getData().getData()));
        }else{
            Material material = Material.getMaterial(blockEntryString);
            if(material == null){
                return Optional.empty();
            }
            return Optional.of(new BlockEntry(material));
        }
    }

    public BlockEntry(Material materialType) {
        this.materialType = materialType;
        this.data = -1;
    }

    public ItemStack toStack(){
        ItemStack itemStack = new ItemStack(materialType);
        if(data > 0)
            itemStack.setData(new MaterialData(materialType, data));

        return itemStack;
    }

    public boolean isSimilar(ItemStack stack){
        return stack.getType() == materialType && stack.getData().getData() == data;
    }
}
