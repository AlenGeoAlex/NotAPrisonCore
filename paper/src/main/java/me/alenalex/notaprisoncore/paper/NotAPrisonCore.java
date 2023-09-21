package me.alenalex.notaprisoncore.paper;

import com.zaxxer.hikari.HikariConfig;
import lombok.Getter;
import me.alenalex.notaprisoncore.api.abstracts.AbstractMessageService;
import me.alenalex.notaprisoncore.paper.data.DataHolder;
import me.alenalex.notaprisoncore.paper.database.PrisonDatabaseProvider;
import me.alenalex.notaprisoncore.paper.database.PrisonSqlDatabase;

import me.alenalex.notaprisoncore.paper.listener.ConnectionListener;
import me.alenalex.notaprisoncore.paper.listener.PlayerListener;
import me.alenalex.notaprisoncore.paper.manager.PrisonManagers;
import me.alenalex.notaprisoncore.paper.message.PrisonMessageService;
import me.alenalex.notaprisoncore.paper.scheduler.PrisonScheduler;
import me.alenalex.notaprisoncore.paper.store.PrisonDataStore;
import me.alenalex.notaprisoncore.paper.wrapper.GsonWrapper;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.InputStream;
import java.util.logging.Logger;

public final class NotAPrisonCore {

    @Getter
    private final JavaPlugin bukkitPlugin;
    private final PrisonManagers prisonManagers;
    private PrisonDatabaseProvider databaseProvider;
    private PrisonDataStore prisonDataStore;
    private final DataHolder dataHolder;
    private boolean shouldRunEnable;
    private PrisonMessageService messageService;
    private final PrisonScheduler prisonScheduler;
    public NotAPrisonCore(JavaPlugin bukkitPlugin) {
        this.bukkitPlugin = bukkitPlugin;
        this.prisonManagers = new PrisonManagers(this);
        this.dataHolder = new DataHolder(this);
        this.shouldRunEnable = true;
        this.prisonScheduler = new PrisonScheduler(this);
    }

    public void onLoad(){
        if(!prisonManagers.onLoad()){
            disableBukkitPlugin("Failed to load the plugin data. Cause would be specified above. Please check the logs");
            this.shouldRunEnable = false;
            return;
        }
    }
    public void onEnable() {
        if(!shouldRunEnable){
            disableBukkitPlugin("Failed to load the plugin data. Cause would be specified above. Please check the logs");
            return;
        }
        if(!prisonManagers.onEnable()){
            disableBukkitPlugin("Failed to enable the plugin. Cause would be specified above. Please check the logs");
            return;
        }

        this.databaseProvider = new PrisonDatabaseProvider(this);

        try {
            PrisonDatabaseProvider.ConnectionResponse connectionResponse = this.databaseProvider.connect();
            if(connectionResponse == null) {
                disableBukkitPlugin("An unknown error happened while trying to connect to database, which returned null response");
                return;
            }

            if(!connectionResponse.isConnectionSuccess()){
                disableBukkitPlugin(connectionResponse.getResponse());
                return;
            }
        }catch (Exception e){
            e.printStackTrace();
            disableBukkitPlugin("Failed to connect to the database");
            return;
        }

        this.messageService = new PrisonMessageService(this);

        try {
            this.messageService.onEnable();
            int messageQueueSize = this.messageService.getChannels().size();
            getLogger().info("Message queue has been initialized with "+messageQueueSize);
            this.messageService.listen();
            getLogger().info("Message bus has started listening for messages...");
        }catch (Exception e){
            e.printStackTrace();
            disableBukkitPlugin("Failed to register the message service. Check above stack trace to know more");
        }
        try {
            this.prisonScheduler.onEnable();
        }catch (Exception e){
            e.printStackTrace();
            disableBukkitPlugin("Failed to enable scheduler task. Check above stack trace to know more");
        }
        this.prisonDataStore = new PrisonDataStore(this);
        try {
            this.prisonDataStore.init();
        }catch (Exception e){
            e.printStackTrace();
            disableBukkitPlugin("Failed to initialize the data store for the plugin. Check the stack trace above for more error log");
            return;
        }

        getLogger().info("Starting to load data from PrisonStore");
        try {
            this.prisonDataStore.load();
        }catch (Exception e){
            e.printStackTrace();
            disableBukkitPlugin("Failed to load the data store for the plugin. Check the stack trace above for more error log");
            return;
        }
        getLogger().info("Completed data store initialization");

        getLogger().info("Initializing data holder for plugin");
        try {
            this.dataHolder.onEnable();
        }catch (Exception e){
            e.printStackTrace();
            disableBukkitPlugin("Failed to load the data holder. Check the stack trace above for more error log");
            return;
        }
        this.prisonManagers.enableCommandManager();

        getBukkitPlugin().getServer().getPluginManager().registerEvents(new DevelopmentListener(this), this.getBukkitPlugin());
        new ConnectionListener(this);
        new PlayerListener(this);
    }

    public void onDisable() {
        prisonManagers.onShutdown();
        if(this.dataHolder != null){
            this.dataHolder.onDisable();
        }
        if(this.prisonDataStore != null){
            this.prisonDataStore.disable();
        }
        if(this.databaseProvider != null){
            try {
                this.databaseProvider.disconnect();
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        if(this.messageService != null){
            this.messageService.close();
        }

        if(this.prisonScheduler != null){
            this.prisonScheduler.onDisable();
        }
    }

    public Logger getLogger(){
        return this.bukkitPlugin.getLogger();
    }

    public PrisonDatabaseProvider getDatabaseProvider() {
        return databaseProvider;
    }

    public PrisonManagers getPrisonManagers() {
        return prisonManagers;
    }

    public PrisonDataStore getPrisonDataStore(){
        return this.prisonDataStore;
    }

    public DataHolder getDataHolder() {
        return dataHolder;
    }

    public PrisonMessageService getMessageService() {
        return messageService;
    }

    private void disableBukkitPlugin(String reason){
        getLogger().warning(reason);
        bukkitPlugin.getServer().getPluginManager().disablePlugin(this.bukkitPlugin);
    }




}
