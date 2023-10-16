package me.alenalex.notaprisoncore.paper;

import me.alenalex.notaprisoncore.api.core.CoreApi;


public class NotAPrisonCoreApi extends CoreApi {

    private boolean enabled;

    public NotAPrisonCoreApi(NotAPrisonCore pluginInstance) {
        super(pluginInstance.getPrisonManagers(), pluginInstance.getPrisonDataStore(), pluginInstance.getDataHolder(), pluginInstance.getDatabaseProvider(), pluginInstance.getMessageService(), pluginInstance.getPrisonScheduler(), pluginInstance.getPrisonQueueProvider());
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
