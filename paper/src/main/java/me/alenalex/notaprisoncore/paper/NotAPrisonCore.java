package me.alenalex.notaprisoncore.paper;

import com.zaxxer.hikari.HikariConfig;
import lombok.Getter;
import me.alenalex.notaprisoncore.paper.database.PrisonSqlDatabase;
import me.alenalex.notaprisoncore.paper.manager.PrisonManagers;
import me.alenalex.notaprisoncore.paper.store.PrisonDataStore;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.util.logging.Logger;

public final class NotAPrisonCore {

    @Getter
    private final JavaPlugin bukkitPlugin;
    private final PrisonManagers prisonManagers;
    private PrisonSqlDatabase prisonSqlDatabase;
    private final PrisonDataStore prisonDataStore;
    public NotAPrisonCore(JavaPlugin bukkitPlugin) {
        this.bukkitPlugin = bukkitPlugin;
        this.prisonManagers = new PrisonManagers(this);
        this.prisonDataStore = new PrisonDataStore(this);
    }

    public void onLoad(){
        if(!prisonManagers.onLoad()){
            disableBukkitPlugin("Failed to load the plugin data. Cause would be specified above. Please check the logs");
            return;
        }
    }
    public void onEnable() {
        if(!prisonManagers.onEnable()){
            disableBukkitPlugin("Failed to enable the plugin. Cause would be specified above. Please check the logs");
            return;
        }

        HikariConfig mysql = prisonManagers.configurationManager().getPluginConfiguration().sqlConfiguration().asHikariConfig("mysql");
        this.prisonSqlDatabase = new PrisonSqlDatabase(mysql, getLogger());

        try {
            this.prisonSqlDatabase.createConnection();
        }catch (Exception e){
            e.printStackTrace();
            disableBukkitPlugin("Failed to connect to the database");
            return;
        }
        getLogger().info("Connected to SQL database.");
        getLogger().info("- Pool size is "+this.prisonSqlDatabase.getPoolSize());

        try {
            final InputStream scriptStream = getBukkitPlugin().getResource("sql/script.sql");
            this.prisonSqlDatabase.prepareFromScript(scriptStream);
        }catch (Exception e){
            e.printStackTrace();
            disableBukkitPlugin("Failed to complete the execution of script");
            return;
        }

        try {
            this.prisonDataStore.init();
        }catch (Exception e){
            e.printStackTrace();
            disableBukkitPlugin("Failed to initialize the data store for the plugin. Check the stack trace above for more error log");
            return;
        }
    }

    public void onDisable() {
        prisonManagers.onShutdown();
        this.prisonDataStore.disable();
        if(this.prisonSqlDatabase != null){
            try {
                this.prisonSqlDatabase.disconnect();
                getLogger().info("Disconnected SQL database");
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public Logger getLogger(){
        return this.bukkitPlugin.getLogger();
    }

    public PrisonManagers getPrisonManagers() {
        return prisonManagers;
    }

    public PrisonDataStore getPrisonDataStore(){
        return this.prisonDataStore;
    }

    private void disableBukkitPlugin(String reason){
        getLogger().warning(reason);
        bukkitPlugin.getServer().getPluginManager().disablePlugin(this.bukkitPlugin);
    }


}
