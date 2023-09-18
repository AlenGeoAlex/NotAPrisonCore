package me.alenalex.notaprisoncore.api.store;

import me.alenalex.notaprisoncore.api.common.Octet;
import me.alenalex.notaprisoncore.api.entity.mine.IMine;
import me.alenalex.notaprisoncore.api.entity.user.IPrisonUserProfile;
import me.alenalex.notaprisoncore.api.entity.user.IUserSocial;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IUserProfileStore extends IEntityStore<IPrisonUserProfile, UUID>{

    CompletableFuture<Optional<Octet<IPrisonUserProfile, List<IUserSocial>, IMine, Boolean>>> getOrCreateUserProfile(UUID id);
}
