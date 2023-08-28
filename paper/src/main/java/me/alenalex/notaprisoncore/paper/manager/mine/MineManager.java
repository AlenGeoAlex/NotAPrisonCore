package me.alenalex.notaprisoncore.paper.manager.mine;

import me.alenalex.notaprisoncore.api.config.entry.MinePositionalKeys;
import me.alenalex.notaprisoncore.api.entity.mine.IMineMeta;
import me.alenalex.notaprisoncore.api.entity.mine.MinePositionalKey;
import me.alenalex.notaprisoncore.api.exceptions.DuplicateMineIdentifier;
import me.alenalex.notaprisoncore.api.generator.IMineGenerator;
import me.alenalex.notaprisoncore.api.managers.IMineManager;
import me.alenalex.notaprisoncore.api.provider.IMineMetaProvider;
import me.alenalex.notaprisoncore.paper.NotAPrisonCore;
import me.alenalex.notaprisoncore.paper.config.MineIdentifierConfiguration;
import me.alenalex.notaprisoncore.paper.entity.mine.MineMeta;
import me.alenalex.notaprisoncore.paper.generator.MineGenerator;
import me.alenalex.notaprisoncore.paper.manager.PrisonManagers;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class MineManager implements IMineManager {

    private final PrisonManagers managers;
    private final IMineGenerator mineGenerator;
    private final IMineMetaProvider mineMetaProvider;

    public MineManager(PrisonManagers managers) {
        this.managers = managers;
        this.mineGenerator = new MineGenerator(this);
        this.mineMetaProvider = new MineMetaProvider(this);
    }

    @Override
    public IMineGenerator generator() {
        return this.mineGenerator;
    }

    @Override
    public IMineMetaProvider metaProvider() {
        return mineMetaProvider;
    }

    @Override
    public void registerMineIdentifiers(String schematic, MinePositionalKey key) throws DuplicateMineIdentifier {
        MineIdentifierConfiguration configuration = (MineIdentifierConfiguration) this.getPlugin().getPrisonManagers().configurationManager().getMineIdentifierConfiguration();
        MinePositionalKeys minePositionalKeys = configuration.getOrCreate(schematic);
        boolean exists = minePositionalKeys.stream().anyMatch(minePositionalKey -> minePositionalKey.getIdentifier() == key.getIdentifier());
        if(exists)
            throw new DuplicateMineIdentifier("An existing key already have this material selected. Please choose another one");

        minePositionalKeys.add(key);
    }

    @Override
    public void registerMineIdentifiersOnAllSchematics(MinePositionalKey key) throws DuplicateMineIdentifier {
        MineIdentifierConfiguration configuration = (MineIdentifierConfiguration) this.getPlugin().getPrisonManagers().configurationManager().getMineIdentifierConfiguration();
        Collection<MinePositionalKeys> keys = configuration.getKeys();
        for (MinePositionalKeys positionalKeys : keys) {
            registerMineIdentifiers(positionalKeys.getMineName(), key);
        }
    }

    @Override
    public CompletableFuture<Boolean> registerMineMeta(IMineMeta meta) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        AtomicBoolean completed = new AtomicBoolean(true);
        this.managers.getPluginInstance()
                .getPrisonDataStore()
                .mineMetaStore()
                .createAsync(meta)
                .whenComplete((optionalId, err) -> {
                   if(err != null){
                       getPlugin().getLogger().warning("Failed to register the mine meta. Error stack trace can be seen below");
                       err.printStackTrace();
                       future.completeExceptionally(err);
                       completed.set(false);
                       return;
                   }

                   if(!optionalId.isPresent()){
                       getPlugin().getLogger().warning("Failed to register mine meta. Reason: Unable to fetch the registration id");
                       future.complete(false);
                       completed.set(false);
                       return;
                   }

                    UUID uuid = optionalId.get();
                    try {
                        ((MineMeta) meta).setMetaId(uuid);
                    } catch (IllegalAccessException e) {
                        completed.set(false);
                        getPlugin().getLogger().warning("The meta is already registered under different meta id. The older meta would be deleted");
                        this.managers.getPluginInstance()
                                .getPrisonDataStore()
                                .mineMetaStore()
                                .deleteAsync(meta.getMetaId());
                    }
                    getPlugin().getLogger().info("Successfully created meta with id "+meta.getMetaId()+" on "+meta.getSpawnPoint());
                    future.complete(completed.get());
                });

        return future;
    }

    public PrisonManagers getManagers() {
        return managers;
    }

    public NotAPrisonCore getPlugin() {
        return managers.getPluginInstance();
    }
}
