package me.alenalex.notaprisoncore.api.config.options;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.abstracts.AbstractConfigurationOption;

import java.util.LinkedHashMap;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class PriorityConfiguration extends AbstractConfigurationOption {
    private final LinkedHashMap<String, Integer> weightMap;
    private int defaultWeight;
    public PriorityConfiguration(Section section) {
        super(section);
        this.weightMap = new LinkedHashMap<>();
    }

    @Override
    public void load() {
        this.weightMap.clear();
        this.defaultWeight = getSection().getInt("default-weight");
        Section weightSection = getSection().getSection("weight-map");
        for (String string : weightSection.getRoutesAsStrings(false)) {
            weightMap.put(string, weightSection.getInt(string));
        }
    }

    @Override
    public ValidationResponse validate() {
        ValidationResponse.Builder builder = ValidationResponse.Builder.builder();
        if(defaultWeight < 0){
            builder.withWarnings("Its appropriate to provide the default weight with as a positive number!");
        }

        if(!isWeightSorted()){
            builder.withWarnings("The weight-map seems to be incorrectly sorted, Its designed in a way that the first elements are checked first.");
        }

        return builder.build();
    }

    private boolean isWeightSorted(){
        Integer previousWeight = null;
        for (Integer value : this.weightMap.values()) {
            if(previousWeight == null){
                previousWeight = value;
                continue;
            }

            if(previousWeight > value){
                return false;
            }

            previousWeight = value;
        }
        return true;
    }
}
