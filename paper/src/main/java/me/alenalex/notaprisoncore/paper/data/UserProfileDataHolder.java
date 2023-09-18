package me.alenalex.notaprisoncore.paper.data;

import me.alenalex.notaprisoncore.api.common.Octet;
import me.alenalex.notaprisoncore.api.data.IProfileDataHolder;
import me.alenalex.notaprisoncore.api.entity.mine.IMine;
import me.alenalex.notaprisoncore.api.entity.user.IPrisonUserProfile;
import me.alenalex.notaprisoncore.api.entity.user.IUserSocial;
import me.alenalex.notaprisoncore.api.exceptions.store.DatastoreException;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class UserProfileDataHolder implements IProfileDataHolder {

    private final DataHolder dataHolder;
    private final ConcurrentHashMap<UUID, IPrisonUserProfile> profileMap;
    private final Set<UUID> onLoadingSet;

    public UserProfileDataHolder(DataHolder dataHolder) {
        this.dataHolder = dataHolder;
        this.profileMap = new ConcurrentHashMap<>();
        this.onLoadingSet = ConcurrentHashMap.newKeySet();
    }

    @Override
    public IPrisonUserProfile get(@NotNull UUID uuid) {
        return this.profileMap.get(uuid);
    }

    @Override
    public @NotNull CompletableFuture<IPrisonUserProfile> getOrSoftLoad(@NotNull UUID uuid) {
        IPrisonUserProfile profile = get(uuid);

        if (profile != null) {
            return CompletableFuture.completedFuture(profile);
        } else {
            return softLoad(uuid)
                    .thenApply(res -> {
                        this.profileMap.put(uuid, res);
                        return res;
                    })
                    .exceptionally(err -> {
                        err.printStackTrace();
                        return null;
                    });
        }
    }

    @Override
    public @NotNull CompletableFuture<IPrisonUserProfile> getOrHardLoad(@NotNull UUID uuid) {
        IPrisonUserProfile profile = get(uuid);

        if (profile != null) {
            return CompletableFuture.completedFuture(profile);
        } else {
            return hardLoad(uuid)
                    .thenApply(res -> {
                        this.profileMap.put(uuid, res.getFirstItem());
                        MineDataHolder mineDataHolder = (MineDataHolder) this.dataHolder.getPlugin().getDataHolder().mineDataHolder();
                        mineDataHolder.load(res.getThirdItem());
                        //Load user socials
                        return res.getFirstItem();
                    })
                    .exceptionally(err -> {
                        err.printStackTrace(); // Handle the error appropriately
                        return null; // Return null or another default value if needed
                    });
        }
    }

    @Override
    public boolean isLoaded(@NotNull UUID uuid) {
        return this.profileMap.containsKey(uuid);
    }

    @Override
    public void reload(@NotNull UUID uuid) {
        //TODO do a hardload, if the element exists swap swappable properties with setters, if the element does't exists, just put new object
        //TODO this method would be mostly used to reload changes made from other servers. Always send message of changes after the saving to db part is complete
    }

    @Override
    public void setLoading(@NotNull UUID bukkitPlayer) {
        this.onLoadingSet.add(bukkitPlayer);
    }

    @Override
    public void releaseLoading(@NotNull UUID bukkitPlayer) {
        this.onLoadingSet.remove(bukkitPlayer);
    }

    @Override
    public boolean isLoading(@NotNull UUID bukkitPlayer) {
        return this.onLoadingSet.contains(bukkitPlayer);
    }

    @Override
    public void onEnable() {

    }

    @Override
    public void onDisable() {

    }

    public void load(IPrisonUserProfile profile){
        this.profileMap.put(profile.getUserId(), profile);
    }

    private CompletableFuture<IPrisonUserProfile> softLoad(UUID uuid){
        return this.dataHolder.getPlugin()
                .getPrisonDataStore()
                .userProfileStore()
                .id(uuid)
                .handle((res, err) -> {
                    if (err != null) {
                        err.printStackTrace();
                        return Optional.empty();
                    }
                    return Optional.ofNullable(res);
                })
                .thenCompose(optionalRes -> {
                    if (optionalRes.isPresent()) {
                        IPrisonUserProfile userProfile = (IPrisonUserProfile) optionalRes.get();
                        return CompletableFuture.completedFuture(userProfile);
                    } else {
                        throw new DatastoreException("Failed to get the user from database for id "+uuid.toString());
                    }
                });
    }

    private CompletableFuture<Octet<IPrisonUserProfile, List<IUserSocial>, IMine, Boolean>> hardLoad(UUID uuid){
        return this.dataHolder.getPlugin()
                .getPrisonDataStore()
                .userProfileStore()
                .getOrCreateUserProfile(uuid)
                .handle((res, err) -> {
                    if(err != null)
                        throw new DatastoreException(err);

                    if(!res.isPresent())
                        throw new DatastoreException("Failed to get the user from database for id "+uuid.toString());

                    return res.get();
                });
    }
}
