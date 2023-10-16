package me.alenalex.notaprisoncore.api.core;

import me.alenalex.notaprisoncore.api.abstracts.message.IMessageService;
import me.alenalex.notaprisoncore.api.data.IDataHolder;
import me.alenalex.notaprisoncore.api.database.IDatabaseProvider;
import me.alenalex.notaprisoncore.api.managers.IPrisonManagers;
import me.alenalex.notaprisoncore.api.queue.IQueueProvider;
import me.alenalex.notaprisoncore.api.scheduler.IPrisonScheduler;
import me.alenalex.notaprisoncore.api.store.IPrisonDataStore;
import org.jetbrains.annotations.NotNull;

public interface ICoreApi {

    @NotNull IPrisonManagers getManagers();
    @NotNull IPrisonDataStore getStore();
    @NotNull IDataHolder getHolder();
    @NotNull IDatabaseProvider getDatabaseProvider();
    @NotNull IMessageService getMessagingService();
    @NotNull IPrisonScheduler getCoreScheduler();
    @NotNull IQueueProvider getQueueProvider();
    boolean isEnabled();

}
