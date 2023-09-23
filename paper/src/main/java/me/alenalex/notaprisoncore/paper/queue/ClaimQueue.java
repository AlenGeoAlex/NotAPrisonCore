package me.alenalex.notaprisoncore.paper.queue;

import me.alenalex.notaprisoncore.api.queue.PluginQueue;
import me.alenalex.notaprisoncore.api.entity.IClaimQueueEntity;

public class ClaimQueue extends PluginQueue<IClaimQueueEntity> {

    private final PrisonQueueProvider provider;

    public ClaimQueue(PrisonQueueProvider provider) {
        this.provider = provider;
    }

    public PrisonQueueProvider getProvider() {
        return provider;
    }
}
