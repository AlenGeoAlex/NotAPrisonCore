package me.alenalex.notaprisoncore.paper.bootstrap;

import me.alenalex.notaprisoncore.paper.NotAPrisonCore;
import org.bukkit.plugin.java.JavaPlugin;

public class Bootstrap extends JavaPlugin {
    private static JavaPlugin INSTANCE;
    private final NotAPrisonCore pluginInstance;

    public Bootstrap() {
        INSTANCE = this;
        this.pluginInstance = new NotAPrisonCore(this);
    }

    @Override
    public void onLoad() {
        pluginInstance.onLoad();
    }

    @Override
    public void onDisable() {
        pluginInstance.onDisable();
    }

    @Override
    public void onEnable() {
        pluginInstance.onEnable();
    }

    public NotAPrisonCore getPluginInstance() {
        return pluginInstance;
    }

    public static JavaPlugin getJavaPlugin(){
        return INSTANCE;
    }



}
