package me.alenalex.notaprisoncore.paper.entity.mine.worker;

import me.alenalex.notaprisoncore.api.entity.mine.IMineResetter;
import me.alenalex.notaprisoncore.paper.abstracts.AbstractResetWorker;
import org.bukkit.World;

import java.util.concurrent.CompletableFuture;

public class InternalResetWorker extends AbstractResetWorker {
    public InternalResetWorker(IMineResetter resetter) {
        super(resetter);
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    protected CompletableFuture<Long> work() {
        CompletableFuture<Long> future = new CompletableFuture<>();
        World world = this.getMineResetter().getMineMeta().getSpawnPoint().getWorld();

        return future;
    }
}
