package me.alenalex.notaprisoncore.api.data;

import me.alenalex.notaprisoncore.api.entity.mine.IMine;
import me.alenalex.notaprisoncore.api.entity.user.IPrisonUserProfile;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IProfileDataHolder extends ICommonDataHolder {
    @Nullable
    IPrisonUserProfile get(@NotNull UUID uuid);
    @Nullable
    default IPrisonUserProfile get(@NotNull Player player){
        return get(player.getUniqueId());
    }
    @Nullable
    default IPrisonUserProfile get(@NotNull IMine mine){
        return get(mine.getOwnerId());
    }
    @NotNull
    CompletableFuture<IPrisonUserProfile> getOrSoftLoad(@NotNull UUID uuid);
    @NotNull
    default CompletableFuture<IPrisonUserProfile> getOrSoftLoad(@NotNull Player player){
        return getOrSoftLoad(player.getUniqueId());
    }
    @NotNull
    default CompletableFuture<IPrisonUserProfile> getOrSoftLoad(@NotNull IMine mine){
        return getOrSoftLoad(mine.getOwnerId());
    }
    @NotNull
    CompletableFuture<IPrisonUserProfile> getOrHardLoad(@NotNull UUID uuid);
    @NotNull
    default CompletableFuture<IPrisonUserProfile> getOrHardLoad(@NotNull Player player){
        return getOrSoftLoad(player.getUniqueId());
    }
    @NotNull
    default CompletableFuture<IPrisonUserProfile> getOrHardLoad(@NotNull IMine mine){
        return getOrSoftLoad(mine.getOwnerId());
    }
    boolean isLoaded(@NotNull UUID uuid);
    default boolean isLoaded(@NotNull Player player){
        return isLoaded(player.getUniqueId());
    }
    void reload(@NotNull UUID uuid);
    void setLoading(@NotNull UUID bukkitPlayer);
    void releaseLoading(@NotNull UUID bukkitPlayer);
    boolean isLoading(@NotNull UUID bukkitPlayer);
}
