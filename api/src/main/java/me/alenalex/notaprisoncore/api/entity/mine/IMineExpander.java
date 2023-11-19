package me.alenalex.notaprisoncore.api.entity.mine;

import me.alenalex.notaprisoncore.api.entity.PrisonCoreVector;

import java.util.concurrent.CompletableFuture;

public interface IMineExpander {

    boolean expand(PrisonCoreVector increaseMax, PrisonCoreVector increaseMin);

}
