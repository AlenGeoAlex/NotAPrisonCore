package me.alenalex.notaprisoncore.paper.manager;

import me.alenalex.notaprisoncore.api.managers.*;
import me.alenalex.notaprisoncore.paper.NotAPrisonCore;
import me.alenalex.notaprisoncore.paper.manager.schematic.SchematicManager;
import org.jetbrains.annotations.NotNull;

public class PrisonManagers implements IPrisonManagers {

    private final NotAPrisonCore pluginInstance;
    private final ConfigurationManager configurationManager;
    private final SchematicManager schematicManager;
    private final LocaleManager localeManager;
    private final WorldManager worldManager;
    private final MineManager mineManager;
    public PrisonManagers(NotAPrisonCore pluginInstance) {
        this.pluginInstance = pluginInstance;
        this.configurationManager = new ConfigurationManager(this);
        this.schematicManager = new SchematicManager(this);
        this.localeManager = new LocaleManager(this);
        this.worldManager = new WorldManager(this);
        this.mineManager = new MineManager(this);
    }

    public boolean onLoad(){
        try {
            this.configurationManager.initAndLoadConfiguration();
            this.localeManager.init();
        }catch (Exception e){
            this.pluginInstance.getLogger().warning(e.getMessage());
            return false;
        }
        return true;
    }

    public boolean onEnable(){
        this.schematicManager.load();
        this.localeManager.load();
        this.worldManager.load();
        return true;
    }

    public void onShutdown(){

    }

    @NotNull
    @Override
    public IConfigurationManager configurationManager() {
        return this.configurationManager;
    }

    @Override
    public @NotNull ISchematicManager schematicManager() {
        return this.schematicManager;
    }
    @Override
    public @NotNull ILocaleManager localeManager() {
        return this.localeManager;
    }

    @Override
    public @NotNull IWorldManager worldManager() {
        return worldManager;
    }

    @Override
    public @NotNull IMineManager mineManager() {
        return mineManager;
    }

    @NotNull
    public NotAPrisonCore getPluginInstance() {
        return this.pluginInstance;
    }
}
