package me.alenalex.notaprisoncore.paper.abstracts;

import me.alenalex.notaprisoncore.api.entity.mine.IMine;
import me.alenalex.notaprisoncore.api.entity.user.IPrisonUserProfile;
import me.alenalex.notaprisoncore.api.managers.IConfigurationManager;
import me.alenalex.notaprisoncore.paper.NotAPrisonCore;
import me.alenalex.notaprisoncore.paper.bootstrap.Bootstrap;
import me.alenalex.notaprisoncore.paper.data.DataHolder;
import me.alenalex.notaprisoncore.paper.manager.ConfigurationManager;
import me.alenalex.notaprisoncore.paper.store.PrisonDataStore;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public abstract class AbstractEventListener implements Listener {
    private final NotAPrisonCore plugin;
    public AbstractEventListener(NotAPrisonCore plugin) {
        this.plugin = plugin;
        Bukkit.getServer().getPluginManager().registerEvents(this, Bootstrap.getJavaPlugin());
    }

    protected NotAPrisonCore getPlugin() {
        return plugin;
    }

    @Nullable
    protected IPrisonUserProfile getProfile(UUID uuid){
        return null;
    }

    @Nullable
    protected IPrisonUserProfile getProfile(Player player){
        if(player == null)
            return null;
        return getProfile(player.getUniqueId());
    }

    @Nullable
    protected IMine getMine(UUID uuid){
        return null;
    }

    @Nullable
    protected IMine getMine(Player player){
        if(player == null)
            return null;

        return getMine(player.getUniqueId());
    }

    @Nullable
    protected IMine getMine(IPrisonUserProfile userProfile){
        if(userProfile == null || !userProfile.hasMine())
            return null;

        return getMine(userProfile.getMineId());
    }

    @NotNull
    protected DataHolder getDataHolder(){
        return this.plugin.getDataHolder();
    }

    @NotNull
    protected PrisonDataStore getStore(){
        return this.plugin.getPrisonDataStore();
    }

    @NotNull
    protected ConfigurationManager getConfiguration(){
        return (ConfigurationManager) this.plugin.getPrisonManagers().configurationManager();
    }

    protected void doSync(Runnable runnable){
        Bukkit.getScheduler().runTask(Bootstrap.getJavaPlugin(), runnable);
    }

    protected void doSyncLater(Runnable runnable, long delay){
        Bukkit.getScheduler().runTaskLater(Bootstrap.getJavaPlugin(), runnable, delay);
    }
}
