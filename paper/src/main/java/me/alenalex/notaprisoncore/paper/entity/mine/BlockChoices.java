package me.alenalex.notaprisoncore.paper.entity.mine;

import com.google.gson.stream.JsonWriter;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.config.entry.BlockEntry;
import me.alenalex.notaprisoncore.api.entity.mine.IBlockChoices;
import me.alenalex.notaprisoncore.api.provider.IRandomProvider;
import me.alenalex.notaprisoncore.paper.abstracts.AbstractBlockChoices;
import me.alenalex.notaprisoncore.paper.bootstrap.Bootstrap;
import me.alenalex.notaprisoncore.paper.wrapper.GsonWrapper;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@EqualsAndHashCode(callSuper = true)
@ToString
public class BlockChoices extends AbstractBlockChoices {


    public BlockChoices() {
        super();
        clearAndSetDefault();
    }

    public BlockChoices(List<BlockEntry> playerChoices) {
        super(playerChoices);
        checkInternal();
    }

    @Override
    public void setChoices(Collection<BlockEntry> entryCollections) {
        super.setChoices(entryCollections);
        checkInternal();
    }

    @Override
    public void removeChoice(BlockEntry entry) {
        super.removeChoice(entry);
        if(this.playerChoices.isEmpty()){
            ((Bootstrap) Bootstrap.getJavaPlugin()).getLogger().warning("Cannot set a block choice to empty. Replacing with default");
        }
        checkInternal();
    }

    @Override
    public @NotNull String toJson() {
        return GsonWrapper.singleton().stringify(this.playerChoices);
    }

    public void toJsonWriter(JsonWriter writer){
        GsonWrapper.singleton().gson().toJson(this.playerChoices, ArrayList.class, writer);
    }

    private void checkInternal(){
        if(this.playerChoices.isEmpty()){
            Bootstrap bootstrap = (Bootstrap) Bootstrap.getJavaPlugin();
            HashSet<BlockEntry> blockList = bootstrap.getPluginInstance().getPrisonManagers().getConfigurationManager().getPluginConfiguration().getDefaultMineConfiguration().getDefaultResetBlockList();
            this.playerChoices.addAll(blockList);
        }
    }
}
