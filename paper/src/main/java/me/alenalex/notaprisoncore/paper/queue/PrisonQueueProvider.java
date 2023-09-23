package me.alenalex.notaprisoncore.paper.queue;

import me.alenalex.notaprisoncore.api.entity.IClaimQueueEntity;
import me.alenalex.notaprisoncore.api.queue.IPluginQueue;
import me.alenalex.notaprisoncore.api.queue.IQueueProvider;
import me.alenalex.notaprisoncore.paper.NotAPrisonCore;

public class PrisonQueueProvider implements IQueueProvider {

    private final NotAPrisonCore pluginInstance;
    private final IPluginQueue<IClaimQueueEntity> claimQueue;

    public PrisonQueueProvider(NotAPrisonCore pluginInstance) {
        this.pluginInstance = pluginInstance;
        this.claimQueue = new ClaimQueue(this);
    }

    @Override
    public IPluginQueue<IClaimQueueEntity> getClaimQueue() {
        return claimQueue;
    }

    public NotAPrisonCore getPluginInstance() {
        return pluginInstance;
    }
}
