package me.alenalex.notaprisoncore.api.store;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public interface IEntityStore<E, I>{
    /**
     * Get all the records regarding the entity
     * @return Collection of all the records
     */
    CompletableFuture<Collection<E>> all();

    /**
     * Get an optional wrapped entity of the provided entity id
     * @param id of the entity
     * @return An optional entity if present or else an Empty entity
     */
    CompletableFuture<Optional<E>> id(I id);

    /**
     * Create a new entity on the database
     * @param entity The entity to be created
     * @return The optional id of the newly generated entity if created
     */
    CompletableFuture<Optional<I>> createAsync(E entity);

    /**
     * Update the entity on the database
     * <b>NOTE: For bulk operations either call {@link IEntityStore#updateBatchSync(Collection)} for synchronous
     * operation or {@link IEntityStore#updateBatchAsync(Collection)} for asynchronous operation</b>
     * @param entity Entity to be updated
     * @return Boolean, Whether the update was successful or not
     */
    CompletableFuture<Boolean> updateAsync(E entity);

    /**
     * Update entities in bulk
     * <br>
     * <b>This is a synchronous operation</b>
     * @param entities entities to be saved
     * @return Boolean, If the operation is succeeded
     */
    boolean updateBatchSync(Collection<E> entities);

    /**
     * Update entities in bulk async
     * <br>
     * <b>This is a asynchronous operation</b>
     * @param entities entities to be saved
     * @return Boolean, If the operation is succeeded
     */
    default CompletableFuture<Boolean> updateBatchAsync(Collection<E> entities){
        return CompletableFuture.supplyAsync(new Supplier<Boolean>() {
            @Override
            public Boolean get() {
                return updateBatchSync(entities);
            }
        });
    }

    /**
     * Delete an entity
     * @param entityId id of the entity to be deleted
     * @return Boolean, If the entity is deleted
     */
    CompletableFuture<Boolean> deleteAsync(I entityId);

}
