package me.alenalex.notaprisoncore.paper.manager.schematic;

import me.alenalex.notaprisoncore.api.exceptions.FailedLoadingStateException;
import me.alenalex.notaprisoncore.api.managers.ISchematicFileManager;
import me.alenalex.notaprisoncore.api.managers.ISchematicManager;
import me.alenalex.notaprisoncore.api.provider.ISchematicProvider;
import me.alenalex.notaprisoncore.paper.manager.PrisonManagers;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.NotNull;

public class SchematicManager implements ISchematicManager {
    private final PrisonManagers prisonManagers;
    private final SchematicFileManager schematicFileManager;
    private ClipboardProvider clipboardProvider;
    public SchematicManager(PrisonManagers prisonManagers) {
        this.prisonManagers = prisonManagers;
        this.schematicFileManager = new SchematicFileManager(this);
        this.clipboardProvider = null;
    }

    public void load(){
        this.schematicFileManager.initAndLoad();
        if(Bukkit.getServer().getPluginManager().isPluginEnabled("FastAsyncWorldEdit")){
            this.clipboardProvider = new ClipboardProvider(this);
        }

        if(this.clipboardProvider == null){
            throw new FailedLoadingStateException("Failed to hook up with a schematic provider! Please use a valid world-edit plugin");
        }
        this.prisonManagers.getPluginInstance().getLogger().info("- Schematic Provider : "+this.clipboardProvider.providerPluginName());
    }

    @Override
    public @NotNull ISchematicFileManager getSchematicFileManager() {
        return schematicFileManager;
    }

    @Override
    public @NotNull ISchematicProvider<?> getHookedSchematicProvider() {
        return clipboardProvider;
    }

    public PrisonManagers getPrisonManagers() {
        return prisonManagers;
    }
}
