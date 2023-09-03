package me.alenalex.notaprisoncore.api.config.options;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.abstracts.AbstractConfigurationOption;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class ResetterConfiguration extends AbstractConfigurationOption {

    private String resetterType;
    private String beforeResetKey;
    private String afterResetKey;
    private boolean useAfterResetHook;

    public ResetterConfiguration(Section section) {
        super(section);
    }

    @Override
    public void load() {
        this.resetterType = getSection().getString("resetter-to-use");
        this.beforeResetKey = getSection().getString("before-reset-identifier");
        this.afterResetKey = getSection().getString("after-reset-identifier");
        this.useAfterResetHook = getSection().getBoolean("use-after-reset-hook");
    }

    @Override
    public ValidationResponse validate() {
        ValidationResponse.Builder builder = ValidationResponse.Builder.builder();

        if(this.beforeResetKey == null){
            this.beforeResetKey = "spawn-point";
            builder.withWarnings("before-reset-identifier is missing, temporarily replacing it with "+beforeResetKey);
        }

        if(beforeResetKey.equals(afterResetKey) && this.isUseAfterResetHook()){
            //this.useAfterResetHook = false;
            builder.withWarnings("Disabling use-after-reset-hook since both before and after is same. If not intentional, disabling it can be save unwanted computations");
        }

        return builder.build();
    }
}
