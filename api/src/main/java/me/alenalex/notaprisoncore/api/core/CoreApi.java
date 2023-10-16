package me.alenalex.notaprisoncore.api.core;

import me.alenalex.notaprisoncore.api.abstracts.message.IMessageService;
import me.alenalex.notaprisoncore.api.data.IDataHolder;
import me.alenalex.notaprisoncore.api.database.IDatabaseProvider;
import me.alenalex.notaprisoncore.api.exceptions.api.IllegalInitializationException;
import me.alenalex.notaprisoncore.api.managers.IPrisonManagers;
import me.alenalex.notaprisoncore.api.queue.IQueueProvider;
import me.alenalex.notaprisoncore.api.scheduler.IPrisonScheduler;
import me.alenalex.notaprisoncore.api.store.IPrisonDataStore;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class CoreApi implements ICoreApi{
    private static CoreApi INSTANCE = null;
    private final IPrisonManagers managers;
    private final IPrisonDataStore store;
    private final IDataHolder dataHolder;
    private final IDatabaseProvider databaseProvider;
    private final IMessageService messageService;
    private final IPrisonScheduler prisonScheduler;
    private final IQueueProvider queueProvider;

    public CoreApi(IPrisonManagers managers, IPrisonDataStore store, IDataHolder dataHolder, IDatabaseProvider databaseProvider, IMessageService messageService, IPrisonScheduler prisonScheduler, IQueueProvider queueProvider) {
        if(INSTANCE != null)
            throw new IllegalInitializationException();

        this.managers = managers;
        this.store = store;
        this.dataHolder = dataHolder;
        this.databaseProvider = databaseProvider;
        this.messageService = messageService;
        this.prisonScheduler = prisonScheduler;
        this.queueProvider = queueProvider;
        INSTANCE = this;
    }

    @Override
    public @NotNull IPrisonManagers getManagers() {
        return this.managers;
    }

    @Override
    public @NotNull IPrisonDataStore getStore() {
        return this.store;
    }

    @Override
    public @NotNull IDataHolder getHolder() {
        return this.dataHolder;
    }

    @Override
    public @NotNull IDatabaseProvider getDatabaseProvider() {
        return this.databaseProvider;
    }

    @Override
    public @NotNull IMessageService getMessagingService() {
        return this.messageService;
    }

    @Override
    public @NotNull IPrisonScheduler getCoreScheduler() {
        return this.prisonScheduler;
    }

    @Override
    public @NotNull IQueueProvider getQueueProvider() {
        return this.queueProvider;
    }

    @Nullable
    public static ICoreApi getCore(){
        return INSTANCE;
    }
}
