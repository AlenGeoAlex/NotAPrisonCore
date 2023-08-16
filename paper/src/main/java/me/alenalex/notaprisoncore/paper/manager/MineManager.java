package me.alenalex.notaprisoncore.paper.manager;

import me.alenalex.notaprisoncore.api.config.entry.MinePositionalKeys;
import me.alenalex.notaprisoncore.api.entity.mine.MinePositionalKey;
import me.alenalex.notaprisoncore.api.exceptions.DuplicateMineIdentifier;
import me.alenalex.notaprisoncore.api.generator.IMineGenerator;
import me.alenalex.notaprisoncore.api.managers.IMineManager;
import me.alenalex.notaprisoncore.paper.NotAPrisonCore;
import me.alenalex.notaprisoncore.paper.config.MineIdentifierConfiguration;
import me.alenalex.notaprisoncore.paper.generator.MineGenerator;

import java.util.Collection;

public class MineManager implements IMineManager {

    private final NotAPrisonCore plugin;
    private final MineGenerator mineGenerator;

    public MineManager(NotAPrisonCore plugin) {
        this.plugin = plugin;
        this.mineGenerator = new MineGenerator(this);
    }

    @Override
    public IMineGenerator generator() {
        return this.mineGenerator;
    }

    @Override
    public void registerMineIdentifiers(String schematic, MinePositionalKey key) throws DuplicateMineIdentifier {
        MineIdentifierConfiguration configuration = (MineIdentifierConfiguration) this.plugin.getPrisonManagers().configurationManager().getMineIdentifierConfiguration();
        MinePositionalKeys minePositionalKeys = configuration.getOrCreate(schematic);
        boolean exists = minePositionalKeys.stream().anyMatch(minePositionalKey -> minePositionalKey.getIdentifier() == key.getIdentifier());
        if(exists)
            throw new DuplicateMineIdentifier("An existing key already have this material selected. Please choose another one");

        minePositionalKeys.add(key);
    }

    @Override
    public void registerMineIdentifiersOnAllSchematics(MinePositionalKey key) throws DuplicateMineIdentifier {
        MineIdentifierConfiguration configuration = (MineIdentifierConfiguration) this.plugin.getPrisonManagers().configurationManager().getMineIdentifierConfiguration();
        Collection<MinePositionalKeys> keys = configuration.getKeys();
        for (MinePositionalKeys positionalKeys : keys) {
            registerMineIdentifiers(positionalKeys.getMineName(), key);
        }
    }

    public NotAPrisonCore getPlugin() {
        return plugin;
    }
}
