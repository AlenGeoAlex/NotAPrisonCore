package me.alenalex.notaprisoncore.api.data;

import me.alenalex.notaprisoncore.api.entity.user.IPrisonUserProfile;
import me.alenalex.notaprisoncore.api.entity.user.IUserSocial;
import me.alenalex.notaprisoncore.api.enums.SocialStatus;

import java.util.concurrent.CompletableFuture;

public interface IUserSocialDataHolder {

    CompletableFuture<IUserSocial> createSocialWith(IPrisonUserProfile sourceProfile, IPrisonUserProfile targetProfile, SocialStatus status);

    CompletableFuture<IUserSocial> createSocialWith(IPrisonUserProfile sourceProfile, IPrisonUserProfile targetProfile, SocialStatus status, boolean alertPlayers);
}
