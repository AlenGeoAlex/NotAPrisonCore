package me.alenalex.notaprisoncore.paper.entity.profile;

import me.alenalex.notaprisoncore.api.entity.user.IPrisonUserProfile;
import me.alenalex.notaprisoncore.api.entity.user.IUserSocial;
import me.alenalex.notaprisoncore.api.enums.SocialStatus;
import me.alenalex.notaprisoncore.api.exceptions.store.DatastoreException;
import me.alenalex.notaprisoncore.api.provider.IUserSocialProvider;
import me.alenalex.notaprisoncore.paper.bootstrap.Bootstrap;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class UserSocialProvider implements IUserSocialProvider {
    private final HashSet<UserSocial> socials;
    public UserSocialProvider(UUID sourceId) {
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
        bootstrap.getPluginInstance().getPrisonDataStore().userSocialStore()
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
}
