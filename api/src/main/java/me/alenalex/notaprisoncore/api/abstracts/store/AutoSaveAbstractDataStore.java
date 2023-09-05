package me.alenalex.notaprisoncore.api.abstracts.store;

import me.alenalex.notaprisoncore.api.database.sql.AbstractSQLDatabase;
import me.alenalex.notaprisoncore.api.exceptions.store.DatastoreInitializationException;

import java.util.Set;
import java.util.concurrent.*;

public abstract class AutoSaveAbstractDataStore<E, I> extends AbstractDataStore<E, I> implements Runnable {
    private final ScheduledFuture<?> taskHandle;
    public AutoSaveAbstractDataStore(AbstractSQLDatabase pluginDatabase, long saveInterval, TimeUnit unit) {
        super(pluginDatabase);
        this.taskHandle = AutoSaveAbstractDataStore.TaskScheduler.getDefaultScheduler().add(this, saveInterval, unit);
    }
    public void close(){
        if(this.taskHandle != null) {
            this.taskHandle.cancel(true);
        }
        AutoSaveAbstractDataStore.TaskScheduler.getDefaultScheduler().removeStore(this);
    }

    private static class TaskScheduler {
        private static TaskScheduler SINGLETON_INSTANCE = new TaskScheduler();

        public static TaskScheduler getDefaultScheduler(){
            return SINGLETON_INSTANCE;
        }
        private final ScheduledExecutorService executorService;
        private final Set<AutoSaveAbstractDataStore<?, ?>> runningTasks;
        private TaskScheduler(){
            if(SINGLETON_INSTANCE != null)
                throw new IllegalStateException("TaskScheduler for store is already initialized, Use the static getter");

            SINGLETON_INSTANCE = this;
            this.executorService = new ScheduledThreadPoolExecutor(4);
            this.runningTasks = ConcurrentHashMap.newKeySet();
        }

        public void removeStore(AutoSaveAbstractDataStore<?, ?> store){
            runningTasks.remove(store);
            if(runningTasks.isEmpty()){
                executorService.shutdownNow();
            }
        }

        public ScheduledFuture<?> add(AutoSaveAbstractDataStore<?, ?> store, long saveInterval, TimeUnit unit){
            if(this.runningTasks.contains(store))
                throw new DatastoreInitializationException("This auto store has been already initialized");

            ScheduledFuture<?> scheduledFuture = null;

            try {
                scheduledFuture = this.executorService.scheduleAtFixedRate(store, saveInterval, saveInterval,unit);
                this.runningTasks.add(store);
            }catch (Exception e){
                e.printStackTrace();
                throw new DatastoreInitializationException("Failed to initialize auto save for the datastore");
            }

            return scheduledFuture;
        }
    }
}
