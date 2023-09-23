package me.alenalex.notaprisoncore.api.queue;

import me.alenalex.notaprisoncore.api.entity.IClaimQueueEntity;

public interface IQueueProvider {

    IPluginQueue<IClaimQueueEntity> getClaimQueue();

}
