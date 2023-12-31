package me.alenalex.notaprisoncore.paper.data;

import me.alenalex.notaprisoncore.api.entity.user.IPrisonUserProfile;
import me.alenalex.notaprisoncore.api.entity.user.IUserSocial;
import me.alenalex.notaprisoncore.api.enums.SocialStatus;
import me.alenalex.notaprisoncore.api.exceptions.store.DatastoreException;
import me.alenalex.notaprisoncore.api.data.IUserSocialDataHolder;
import me.alenalex.notaprisoncore.paper.bootstrap.Bootstrap;
import me.alenalex.notaprisoncore.paper.entity.profile.UserSocial;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class UserSocialDataHolder implements IUserSocialDataHolder {
    private final DataHolder dataHolder;
    private final HashSet<UserSocial> socials;
    public UserSocialDataHolder(DataHolder dataHolder) {
        this.dataHolder = dataHolder;
        this.socials = new HashSet<>();
    }

    public Optional<UserSocial> getSocialWith(UUID userId){
        return socials.stream().filter(social -> social.getSourceId().equals(userId) || social.getTargetId().equals(userId)).findAny();
    }

    public Optional<UserSocial> getSocialWith(IPrisonUserProfile profile){
        return getSocialWith(profile.getUserId());
    }

    @Override
    public CompletableFuture<IUserSocial> createSocialWith(IPrisonUserProfile sourceProfile, IPrisonUserProfile targetProfile, SocialStatus status){
        return createSocialWith(sourceProfile, targetProfile, status, true);
    }

    @Override
    public CompletableFuture<IUserSocial> createSocialWith(IPrisonUserProfile sourceProfile, IPrisonUserProfile targetProfile, SocialStatus status, boolean alertPlayers){
        UserSocial userSocial = getSocialWith(targetProfile).orElse(null);
        if(userSocial != null){
            return CompletableFuture.completedFuture(userSocial);
        }

        UserSocial newSocial = new UserSocial(sourceProfile.getUserId(), targetProfile.getUserId(), SocialStatus.PENDING, Timestamp.from(Instant.now()), null);
        if(status != SocialStatus.PENDING){
            newSocial.setSocialStatus(status, alertPlayers);
        }

        CompletableFuture<IUserSocial> future = new CompletableFuture<>();

        Bootstrap bootstrap = (Bootstrap) Bootstrap.getJavaPlugin();
        bootstrap.getPluginInstance().getPrisonDataStore().getUserSocialStore()
                .createAsync(newSocial)
                .whenComplete((resId, err) -> {
                    if(err != null){
                        err.printStackTrace();
                        future.completeExceptionally(err);
                        return;
                    }
                    if(!resId.isPresent()){
                        future.completeExceptionally(new DatastoreException("Failed to generate a valid id for user social of "+newSocial.getSourceId()+" - "+newSocial.getTargetId()));
                        return;
                    }
                    UUID id =resId.get();
                    try {
                        newSocial.setId(id);
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        future.completeExceptionally(e);
                        return;
                    }

                    this.socials.add(newSocial);
                    //TODO Send message to both
                });

        return future;
    }

    public void load(List<IUserSocial> userSocials){
        Set<UserSocial> collected = userSocials.stream().map(s -> (UserSocial) s).collect(Collectors.toSet());
        this.socials.addAll(collected);
    }
}
