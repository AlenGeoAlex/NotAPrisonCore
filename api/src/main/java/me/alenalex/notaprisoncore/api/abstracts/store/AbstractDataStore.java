package me.alenalex.notaprisoncore.api.abstracts.store;

import me.alenalex.notaprisoncore.api.database.SQLDatabase;
import me.alenalex.notaprisoncore.api.exceptions.store.DatastoreInitializationException;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;


public abstract class AbstractDataStore<T> {
    private final SQLDatabase pluginDatabase;
    public AbstractDataStore(SQLDatabase pluginDatabase) {
        this.pluginDatabase = pluginDatabase;
    }
    public boolean createTable(){
        List<String> creationQueries = getTableCreationQueries();
        if(creationQueries == null || creationQueries.isEmpty())
            return true;

        try (final Connection connection = pluginDatabase.getConnection();
             final Statement statement = connection.createStatement()
        ){
            for (String creationQuery : creationQueries) {
                if(creationQuery == null || creationQuery.isEmpty()){
                    continue;
                }

                statement.execute(creationQuery);
            }
        }catch (Exception e){
            throw new DatastoreInitializationException("Failed to execute table creation queries on datastore "+getClass().getName(), e);
        }
        return true;
    }

    public CompletableFuture<Boolean> saveAsync(T entity){
        if(entity == null)
            return CompletableFuture.completedFuture(true);

        return CompletableFuture.supplyAsync(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return save(entity);
            }
        });
    }
    public CompletableFuture<Boolean> saveBatchAsync(Collection<T> entities){
        if(entities == null || entities.isEmpty())
            return CompletableFuture.completedFuture(true);

        return CompletableFuture.supplyAsync(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return saveBatch(entities);
            }
        });
    }

    protected abstract String tableName();
    protected abstract List<String> getTableCreationQueries();
    public abstract boolean save(T entity);
    public abstract boolean saveBatch(Collection<T> entities);

}
