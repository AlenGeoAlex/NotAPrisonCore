package me.alenalex.notaprisoncore.paper.data;

import me.alenalex.notaprisoncore.api.data.IMineMetaDataHolder;
import me.alenalex.notaprisoncore.api.entity.mine.IMineMeta;
import me.alenalex.notaprisoncore.api.exceptions.dataholder.LockExistException;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class MineMetaDataHolder implements IMineMetaDataHolder {
    private final DataHolder dataHolder;
    private final HashSet<IMineMeta> reservedMetaSet;
    private final AtomicBoolean lockingInProgress;
    public MineMetaDataHolder(DataHolder dataHolder) {
        this.dataHolder = dataHolder;
        this.reservedMetaSet = new HashSet<>();
        this.lockingInProgress = new AtomicBoolean(false);
    }

    @Override
    public Optional<IMineMeta> getUnclaimedMeta(){
        if(isLockingInProgress())
            return Optional.empty();

        if(reservedMetaSet.isEmpty())
            return Optional.empty();

        return reservedMetaSet.stream().findAny();
    }

    @Override
    public boolean claimMeta(IMineMeta meta){
        IMineMeta metaToBeClaimed = reservedMetaSet.stream().filter(x -> x.getMetaId().equals(meta.getMetaId())).findAny().orElse(null);
        if(metaToBeClaimed == null)
            return false;

        this.reservedMetaSet.remove(metaToBeClaimed);
        checkAsync();
        return true;
    }

    @Override
    public CompletableFuture<Boolean> acquireLocksAndCacheMetas() throws LockExistException {
        if(lockingInProgress.get()){
            throw new LockExistException("A process has already started locking and caching the metas. Please wait till it is completed");
        }
        lockingInProgress.set(true);
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        this.reservedMetaSet.clear();
        this.getDataHolder().getPlugin().getLogger().info("Starting to acquire lock...");
        this.dataHolder.getPlugin().getPrisonDataStore().mineMetaStore().reserveMetas()
                .whenComplete((res, err) -> {
                    if(err != null){
                        lockingInProgress.set(false);
                        future.completeExceptionally(err);
                        return;
                    }

                    if(res == null || res.isEmpty()){
                        lockingInProgress.set(false);
                        future.complete(false);
                        return;
                    }

                    this.reservedMetaSet.addAll(res);
                    lockingInProgress.set(false);
                    future.complete(true);
                });
        return future;
    }

    public void onEnable(){
        Collection<?> joined = this.dataHolder.getPlugin().getPrisonDataStore().mineMetaStore()
                .reserveMetas()
                .handle((metaCollection, err) -> {
                    if (err != null) {
                        getDataHolder().getPlugin().getLogger().severe("Failed to reserve metas.");
                        err.printStackTrace();
                        return Collections.emptyList();
                    }

                    return metaCollection;
                }).join();

        if(joined.isEmpty()){
            getDataHolder().getPlugin().getLogger().warning("No locked metas found! Claiming new mines will be failed");
        }

        for (Object object : joined) {
            if(object == null)
                continue;

            if(!(object instanceof IMineMeta))
                continue;

            IMineMeta meta = (IMineMeta) object;
            reservedMetaSet.add(meta);
        }
        getDataHolder().getPlugin().getLogger().info("Locked "+reservedMetaSet.size()+" metas for this server instance");
    }

    public void onDisable(){
        Boolean finalResponse = this.dataHolder.getPlugin().getPrisonDataStore()
                .mineMetaStore()
                .releaseReservedMetas()
                .handle((response, err) -> {
                    if (err != null) {
                        getDataHolder().getPlugin().getLogger().severe("Failed to release existing lock for this server instance");
                        err.printStackTrace();
                        return false;
                    }

                    return response;
                }).join();

        if(finalResponse)
            getDataHolder().getPlugin().getLogger().info("Released existing lock.");
        else getDataHolder().getPlugin().getLogger().warning("Failed to release lock on plugin");
    }

    public DataHolder getDataHolder() {
        return dataHolder;
    }

    @Override
    public boolean isLockingInProgress() {
        return lockingInProgress.get();
    }

    private void checkAsync(){
        int min = this.dataHolder.getPlugin().getPrisonManagers().configurationManager().getPluginConfiguration().serverConfiguration().getMinMetaReservedCount();
        if(this.reservedMetaSet.size() < min){
            this.dataHolder.getPlugin().getLogger().info("Cached metas are below minimum, Acquiring new locks and caching metas!");
            try {
                acquireLocksAndCacheMetas()
                        .whenComplete((res, err) -> {
                            if(err != null){
                                getDataHolder().getPlugin().getLogger().warning("Failed to acquire new locked metas. Please check the stacktrace below");
                                err.printStackTrace();
                                return;
                            }

                            if(res == null || !res){
                                getDataHolder().getPlugin().getLogger().warning("No new meta were locked or cached. This either free unclaimed generated mines is empty. Generation of new mines would be aborted");
                                return;
                            }
                            getDataHolder().getPlugin().getLogger().info("Successfully refreshed reserved meta cache with "+this.reservedMetaSet.size()+" metas");
                        });
            } catch (LockExistException e) {
                getDataHolder().getPlugin().getLogger().warning("The plugin is already locked out and is in progress of acquiring more metas. Aborting task!");
            }
        }
    }
}
