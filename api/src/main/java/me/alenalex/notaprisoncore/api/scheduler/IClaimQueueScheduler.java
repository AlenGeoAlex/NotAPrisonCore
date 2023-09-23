package me.alenalex.notaprisoncore.api.scheduler;

import java.util.UUID;

public interface IClaimQueueScheduler extends Runnable{

    boolean isEnabled();

    boolean isUserOnProcessList(UUID uuid);

}
