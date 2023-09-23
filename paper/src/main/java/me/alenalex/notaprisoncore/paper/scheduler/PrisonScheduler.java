package me.alenalex.notaprisoncore.paper.scheduler;

import me.alenalex.notaprisoncore.api.scheduler.IClaimQueueScheduler;
import me.alenalex.notaprisoncore.api.scheduler.IPrisonScheduler;
import me.alenalex.notaprisoncore.paper.NotAPrisonCore;
import me.alenalex.notaprisoncore.paper.task.HeartbeatTask;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PrisonScheduler implements IPrisonScheduler {

    private final NotAPrisonCore pluginInstance;
    private final ScheduledExecutorService scheduledExecutorService;
    private final ClaimQueueScheduler claimQueueScheduler;

    public PrisonScheduler(NotAPrisonCore pluginInstance) {
        this.pluginInstance = pluginInstance;
        this.scheduledExecutorService = Executors.newScheduledThreadPool(4);
        this.claimQueueScheduler = new ClaimQueueScheduler(this);
    }

    public void onEnable(){
        schedule(new HeartbeatTask(), 1, 1, TimeUnit.MINUTES);
        if(this.pluginInstance.getPrisonManagers().getConfigurationManager().getPluginConfiguration().getClaimQueueConfiguration().isEnabled()){
            this.claimQueueScheduler.start();
            this.claimQueueScheduler.setEnabled(true);
        }
    }

    public void onDisable(){
        this.scheduledExecutorService.shutdown();
        if(this.claimQueueScheduler.isEnabled() && this.claimQueueScheduler.isAlive()) {
            this.claimQueueScheduler.stop();
            this.claimQueueScheduler.setEnabled(false);
        }
    }

    public ScheduledFuture<?> schedule(Runnable runnable, long interval, long delay, TimeUnit unit){
        return this.scheduledExecutorService.scheduleAtFixedRate(runnable, interval, delay, unit);
    }


    @Override
    public IClaimQueueScheduler getClaimQueueScheduler() {
        return claimQueueScheduler;
    }

    @Override
    public ScheduledExecutorService getPrisonScheduler() {
        return this.scheduledExecutorService;
    }

    public NotAPrisonCore getPluginInstance() {
        return pluginInstance;
    }
}
