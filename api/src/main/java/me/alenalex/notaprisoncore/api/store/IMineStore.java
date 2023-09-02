package me.alenalex.notaprisoncore.api.store;

import me.alenalex.notaprisoncore.api.entity.mine.IMine;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IMineStore extends IEntityStore<IMine, UUID> {

    CompletableFuture<UUID> claimMine(IMine mine);

}
