package me.alenalex.notaprisoncore.paper.config;

import dev.dejvokep.boostedyaml.block.implementation.Section;
import me.alenalex.notaprisoncore.api.abstracts.AbstractFileConfiguration;
import me.alenalex.notaprisoncore.api.config.IMineIdentifierConfiguration;
import me.alenalex.notaprisoncore.api.config.entry.MinePositionalKeys;
import me.alenalex.notaprisoncore.api.entity.mine.MinePositionalKey;
import me.alenalex.notaprisoncore.api.enums.ConfigType;
import me.alenalex.notaprisoncore.api.exceptions.DuplicateMineIdentifier;
import me.alenalex.notaprisoncore.api.exceptions.FailedConfigurationException;
import me.alenalex.notaprisoncore.paper.manager.ConfigurationManager;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.*;

public class MineIdentifierConfiguration extends AbstractFileConfiguration implements IMineIdentifierConfiguration {
    private final ConfigurationManager configurationManager;
    private final List<MinePositionalKeys> minePositionalKeys;
    public MineIdentifierConfiguration(ConfigurationManager configurationManager) {
        super(new File(configurationManager.getPrisonManagers().getPluginInstance().getBukkitPlugin().getDataFolder(), "identifiers.yml"),
                configurationManager.getPrisonManagers().getPluginInstance().getBukkitPlugin().getResource("identifiers.yml")
        );
        this.configurationManager = configurationManager;
        this.minePositionalKeys = new ArrayList<>();
    }

    @Override
    public void load()  {
        Set<String> mineNames = this.configDocument.getRoutesAsStrings(false);
        for (String mineName : mineNames) {
            MinePositionalKeys mineKeys = new MinePositionalKeys(mineName);
            Section mineSection = this.configDocument.getSection(mineName);
            Set<String> positionalKeyNames = mineSection.getRoutesAsStrings(false);
            for (String positionalKeyName : positionalKeyNames) {
                Optional<MinePositionalKey> from = MinePositionalKey.from(mineSection.getSection(positionalKeyName));
                if(!from.isPresent())
                {
                    continue;
                }
                MinePositionalKey positionalKey = from.get();
                boolean exists = mineKeys.stream().anyMatch(minePositionalKey -> minePositionalKey.getIdentifier() == positionalKey.getIdentifier());
                if(exists)
                   throw new FailedConfigurationException(ConfigType.IDENTIFIER, "An existing key already have this material selected. Please choose another one", null);

                mineKeys.add(positionalKey);
            }

            if(!mineKeys.validateStrictKeys()){
                throw new FailedConfigurationException(ConfigType.IDENTIFIER, "upper-mine-corner, lower-mine-corner, spawn-point is a requirement, Positional Identifiers for them must be strictly passed in the configuration file", null);
            }
        }
    }

    @Override
    public @NotNull Optional<MinePositionalKeys> ofMine(String mine){
        return this.minePositionalKeys.stream().filter(keys -> keys.getMineName().equals(mine)).findAny();
    }

    @NotNull
    public MinePositionalKeys getOrCreate(String mine){
        MinePositionalKeys keys = ofMine(mine).orElse(null);
        if(keys == null){
            keys = new MinePositionalKeys(mine);
            this.minePositionalKeys.add(keys);
        }

        return keys;
    }

    @Override
    public @NotNull Collection<MinePositionalKeys> getKeys(){
        return new HashSet<>(this.minePositionalKeys);
    }
}
