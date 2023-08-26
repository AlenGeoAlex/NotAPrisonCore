package me.alenalex.notaprisoncore.paper.manager;

import me.alenalex.notaprisoncore.api.config.options.MineWorldConfiguration;
import me.alenalex.notaprisoncore.api.managers.IWorldManager;
import org.bukkit.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WorldManager implements IWorldManager {

    private final PrisonManagers prisonManagers;
    private MineWorldConfiguration worldConfiguration;
    public WorldManager(PrisonManagers prisonManagers) {
        this.prisonManagers = prisonManagers;
    }

    public void load(){
        this.worldConfiguration = this.prisonManagers.configurationManager().getPluginConfiguration().mineWorldConfiguration();
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
    public @NotNull World getMineWorld(){
        return getMineWorld(true);
    }

    @Override
    public boolean loadChunkAt(Location location){
        try {
            int x = location.getBlockX() >> 4;
            int z = location.getBlockZ() >> 4;
            location.getWorld().loadChunk(x, z);
            this.prisonManagers.getPluginInstance().getBukkitPlugin().getLogger().info("Loaded chunk at ["+x+", "+z+"]");
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean loadChunkAt(Location center, int radius){
        try {
            int minX = (center.getBlockX() - radius) >> 4;
            int minZ = (center.getBlockZ() - radius) >> 4;
            int maxX = (center.getBlockX() + radius) >> 4;
            int maxZ = (center.getBlockZ() + radius) >> 4;

            for(int x = minX; x <= maxX; x++){
                for(int z = minZ; z <= maxZ; z++){
                    center.getWorld().loadChunk(x, z);
                    this.prisonManagers.getPluginInstance().getBukkitPlugin().getLogger().info("Loaded chunk at ["+x+", "+z+"]");
                }
            }
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
    }

    private World createWorld(String worldName){
        final WorldCreator creator = new WorldCreator(worldName);
        creator.type(WorldType.FLAT);
        creator.generatorSettings("2;0;1");
        creator.createWorld();
        return creator.createWorld();
    }

}
