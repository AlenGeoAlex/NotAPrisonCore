package me.alenalex.notaprisoncore.api.store;

import me.alenalex.notaprisoncore.api.entity.mine.IMineMeta;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IMineMetaStore extends IEntityStore<IMineMeta, UUID> {

    /**
     * Reserve metas for the specific server instance.
     * The serverName and reservationCount would be taken from the configuration.
     * @return Collection of MineMetas that are reserved for this specific server instance
     */
    CompletableFuture<Collection<IMineMeta>> reserveMetas();

    /**
     * Reserve metas for the specific server instance
     * @param serverName Name of the server instance provided to be locked
     * @param reservationCount No of reservation to be locked
     * @return Collection of MineMetas that are reserved for this specific server instance
     */
    CompletableFuture<Collection<IMineMeta>> reserveMetas(String serverName, int reservationCount);

    /**
     * Release all the existing locked metas for the server instance
     * The serverName would be taken from the configuration.
     * @return Boolean, true if succeeded
     */
    CompletableFuture<Boolean> releaseReservedMetas();

    /**
     * Release all the existing locked metas for the server instance
     * @param serverName Name of the server instance provided to be locked
     * @return Boolean, true if succeeded
     */
    CompletableFuture<Boolean> releaseReservedMetas(String serverName);

}
