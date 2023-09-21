package me.alenalex.notaprisoncore.paper.scheduler;

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

    public PrisonScheduler(NotAPrisonCore pluginInstance) {
        this.pluginInstance = pluginInstance;
        this.scheduledExecutorService = Executors.newScheduledThreadPool(4);
    }

    public void onEnable(){
        schedule(new HeartbeatTask(), 1, 1, TimeUnit.MINUTES);
    }

    public void onDisable(){
        this.scheduledExecutorService.shutdown();
    }

    public ScheduledFuture<?> schedule(Runnable runnable, long interval, long delay, TimeUnit unit){
        return this.scheduledExecutorService.scheduleAtFixedRate(runnable, interval, delay, unit);
    }


}
