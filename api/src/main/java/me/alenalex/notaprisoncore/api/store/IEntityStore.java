package me.alenalex.notaprisoncore.api.store;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public interface IEntityStore<E, I>{
    CompletableFuture<Collection<E>> all();
    CompletableFuture<E> id(I id);
    CompletableFuture<I> createAsync(E entity);
    CompletableFuture<Boolean> updateAsync(E entity);
    boolean updateBatchSync(Collection<E> entities);
    default CompletableFuture<Boolean> updateBatchAsync(Collection<E> entities){
        return CompletableFuture.supplyAsync(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return updateBatchSync(entities);
            }
        });
    }
    CompletableFuture<Boolean> deleteAsync(I entityId);

}
