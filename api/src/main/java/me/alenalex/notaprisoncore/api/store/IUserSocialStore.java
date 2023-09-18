package me.alenalex.notaprisoncore.api.store;

import me.alenalex.notaprisoncore.api.entity.user.IUserSocial;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IUserSocialStore {
    CompletableFuture<Optional<UUID>> createAsync(IUserSocial entity);
    CompletableFuture<Collection<IUserSocial>> getSocialOf(UUID uuid);
}
