package me.alenalex.notaprisoncore.api.store;

import me.alenalex.notaprisoncore.api.common.Triplet;
import me.alenalex.notaprisoncore.api.entity.mine.IMine;
import me.alenalex.notaprisoncore.api.entity.user.IPrisonUserProfile;
import me.alenalex.notaprisoncore.api.entity.user.IUserSocial;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IUserProfileStore {


    CompletableFuture<Optional<Triplet<IPrisonUserProfile, List<IUserSocial>, IMine>>> getOrCreateUserProfile(UUID id);
}
