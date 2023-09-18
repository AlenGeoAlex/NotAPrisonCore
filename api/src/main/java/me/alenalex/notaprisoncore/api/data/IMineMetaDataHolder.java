package me.alenalex.notaprisoncore.api.data;

import me.alenalex.notaprisoncore.api.entity.mine.IMineMeta;
import me.alenalex.notaprisoncore.api.exceptions.dataholder.LockExistException;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface IMineMetaDataHolder extends ICommonDataHolder {

    CompletableFuture<Boolean> acquireLocksAndCacheMetas() throws LockExistException;

    boolean isLockingInProgress();

    Optional<IMineMeta> getUnclaimedMeta();

    boolean claimMeta(IMineMeta meta);
    void releaseLockedMeta(IMineMeta meta);

}
