package me.alenalex.notaprisoncore.paper.manager;

import me.alenalex.notaprisoncore.api.config.options.MineWorldConfiguration;
import me.alenalex.notaprisoncore.api.managers.IWorldManager;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.WorldType;
import org.jetbrains.annotations.Nullable;

public class WorldManager implements IWorldManager {

    private final PrisonManagers prisonManagers;
    private final MineWorldConfiguration worldConfiguration;
    public WorldManager(PrisonManagers prisonManagers) {
        this.prisonManagers = prisonManagers;
        this.worldConfiguration = this.prisonManagers.configurationManager().getPluginConfiguration().mineWorldConfiguration();
    }

    public void load(){
        if(this.worldConfiguration.isCreateVoidWorldIfAbsent()){
            World mineWorld = getMineWorld(false);
            if(mineWorld == null){
                this.prisonManagers.getPluginInstance().getLogger().info("World with name "+this.worldConfiguration.getWorldName()+" is not present. Generating a void world...");
                createWorld(this.worldConfiguration.getWorldName());
                this.prisonManagers.getPluginInstance().getLogger().info("Generation completed");
            }
        }
    }

    @Override
    @Nullable
    public World getMineWorld(boolean createIfAbsent){
        String worldName = this.worldConfiguration.getWorldName();
        //Not caching the world, If the world is deleted, can cause memory leaks
        World world = Bukkit.getWorld(worldName);
        if(world == null){
            if(createIfAbsent){
                world = createWorld(worldName);
            }
        }
        return world;
    }

    @Override
    @Nullable
    public World getMineWorld(){
        return getMineWorld(true);
    }

    private World createWorld(String worldName){
        final WorldCreator creator = new WorldCreator(worldName);
        creator.type(WorldType.FLAT);
        creator.generatorSettings("2;0;1");
        creator.createWorld();
        return creator.createWorld();
    }
}
