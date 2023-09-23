package me.alenalex.notaprisoncore.api.scheduler;

import java.util.concurrent.ScheduledExecutorService;

public interface IPrisonScheduler {

    IClaimQueueScheduler getClaimQueueScheduler();

    ScheduledExecutorService getPrisonScheduler();

}
