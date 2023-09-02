package me.alenalex.notaprisoncore.api.config.options;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.abstracts.AbstractConfigurationOption;
import me.alenalex.notaprisoncore.api.config.entry.BlockEntry;
import me.alenalex.notaprisoncore.api.enums.MineAccess;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Getter
@EqualsAndHashCode(callSuper = true)
@ToString
public class DefaultMineConfiguration extends AbstractConfigurationOption {

    private MineAccess defaultMineAccess;
    private final HashSet<BlockEntry> defaultResetBlockList = new HashSet<>();
    private BigDecimal defaultVaultBalance;
    public DefaultMineConfiguration(Section section) {
        super(section);
    }

    @Override
    public void load() {
        this.defaultMineAccess = getSection().getOptionalEnum("mine-access", MineAccess.class).orElse(null);
        List<String> defaultResetBlockList = getSection().getStringList("reset-block-choices");
        for (String defaultResetBlock : defaultResetBlockList) {
            Optional<BlockEntry> optionalBlockEntry = BlockEntry.fromString(defaultResetBlock);
            optionalBlockEntry.ifPresent(this.defaultResetBlockList::add);
        }
        String stringBalance = this.getSection().getString(" mine-vault-balance");
        try {
            defaultVaultBalance = new BigDecimal(stringBalance);
        }catch (Exception e){
            defaultVaultBalance = null;
        }
    }

    @Override
    public ValidationResponse validate() {
        ValidationResponse.Builder builder = ValidationResponse.Builder.builder();
        if(defaultMineAccess == null){
            builder.withWarnings("Failed to match mine-access. Please pass in a value with in ["+ Arrays.toString(MineAccess.values()) +"]. Falling back to CLOSED");
            defaultMineAccess = MineAccess.CLOSED;
        }

        if(defaultResetBlockList.isEmpty()){
            builder.withErrors("reset-block-choices should not be empty, Please populate it.");
        }

        if(defaultVaultBalance == null || defaultVaultBalance.compareTo(BigDecimal.ZERO) < 0){
            builder.withWarnings("Default value of mine vault is incorrectly set. Forcing it to");
            defaultVaultBalance = new BigDecimal(0);
        }

        return builder.build();
    }
}
