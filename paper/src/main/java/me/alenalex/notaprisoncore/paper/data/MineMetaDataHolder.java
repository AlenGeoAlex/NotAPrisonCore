package me.alenalex.notaprisoncore.paper.data;

import me.alenalex.notaprisoncore.api.data.IMineMetaDataHolder;
import me.alenalex.notaprisoncore.api.entity.mine.IMineMeta;
import me.alenalex.notaprisoncore.api.exceptions.dataholder.LockExistException;
import me.alenalex.notaprisoncore.paper.entity.mine.MineMeta;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

public class MineMetaDataHolder implements IMineMetaDataHolder {
    private static final HashSet<IMineMeta> INTERNAL_LOCKED_METAS = new HashSet<>();

    public static boolean isMetaInternalLocked(UUID metaId){
        return INTERNAL_LOCKED_METAS.stream().anyMatch(s -> s.getMetaId().equals(metaId));
    }

    public static boolean isMetaInternalLocked(MineMeta meta){
        return INTERNAL_LOCKED_METAS.contains(meta);
    }

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

        IMineMeta meta = reservedMetaSet.stream().findAny().orElse(null);
        this.reservedMetaSet.remove(meta);
        INTERNAL_LOCKED_METAS.add(meta);
        checkAsync();
        return Optional.of(meta);
    }

    @Override
    public boolean claimMeta(IMineMeta meta){
        IMineMeta metaToBeClaimed = INTERNAL_LOCKED_METAS.stream().filter(x -> x.getMetaId().equals(meta.getMetaId())).findAny().orElse(null);
        if(metaToBeClaimed == null)
            return false;
        INTERNAL_LOCKED_METAS.remove(metaToBeClaimed);
        return true;
    }

    @Override
    public void releaseLockedMeta(IMineMeta meta){
        INTERNAL_LOCKED_METAS.remove(meta);
        this.reservedMetaSet.add(meta);
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
        this.dataHolder.getPlugin().getPrisonDataStore().getMineMetaStore().reserveMetas()
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

    @Override
    public void onEnable(){
        Collection<?> joined = this.dataHolder.getPlugin().getPrisonDataStore().getMineMetaStore()
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

    @Override
    public void onDisable(){
        Boolean finalResponse = this.dataHolder.getPlugin().getPrisonDataStore()
                .getMineMetaStore()
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
        int min = this.dataHolder.getPlugin().getPrisonManagers().getConfigurationManager().getPluginConfiguration().getServerConfiguration().getMinMetaReservedCount();
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
