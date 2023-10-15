package me.alenalex.notaprisoncore.paper.abstracts;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.config.options.ResetterConfiguration;
import me.alenalex.notaprisoncore.api.entity.mine.IMineResetWorker;
import me.alenalex.notaprisoncore.api.entity.mine.IMineResetter;
import me.alenalex.notaprisoncore.api.exceptions.MineResetException;
import me.alenalex.notaprisoncore.paper.bootstrap.Bootstrap;
import me.alenalex.notaprisoncore.paper.entity.mine.MineMeta;
import me.alenalex.notaprisoncore.paper.entity.mine.MineResetter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;

@EqualsAndHashCode
@ToString
public abstract class AbstractResetWorker implements IMineResetWorker {

    private final IMineResetter mineResetter;
    private long startedOn;
    private final AtomicBoolean running;
    private final AtomicBoolean completed;
    private final List<UUID> resetTeleportedPlayers;

    public AbstractResetWorker(IMineResetter resetter) {
        this.mineResetter = resetter;
        this.startedOn = Long.MIN_VALUE;
        this.running = new AtomicBoolean(false);
        this.completed = new AtomicBoolean(false);
        this.resetTeleportedPlayers = new ArrayList<>();
    }

    @Override
    public long getStartedOn(){
        return startedOn;
    }

    @Override
    public long duration(){
        if(startedOn == Long.MIN_VALUE)
            return 0;

        return System.currentTimeMillis() - this.startedOn;
    }

    @Override
    public CompletableFuture<WorkerResponse> reset(){
        CompletableFuture<WorkerResponse> future = new CompletableFuture<>();
        try {
            if(running.get() || this.mineResetter.isResetOnProgress()){
                future.completeExceptionally(new MineResetException("The reset task is already under progress"));
                return future;
            }

            if(completed.get()){
                future.completeExceptionally(new MineResetException("Please dispose this worker, The mine has been successfully rested"));
                return future;
            }
            //TODO Send message
            if(!this.beforeSync()){
                future.complete(new WorkerResponse(startedOn, System.currentTimeMillis(), 0, false));
                return future;
            }

            startedOn = System.currentTimeMillis();
            running.set(true);
            ((MineResetter) this.mineResetter).resetStarted(this);
            this.work()
                    .whenComplete((res, err) -> {
                        running.set(false);
                        completed.set(true);
                        if(err != null){
                            future.completeExceptionally(err);
                            return;
                        }

                        WorkerResponse workerResponse = new WorkerResponse(startedOn, System.currentTimeMillis(), res, true);
                        future.complete(workerResponse);
                        //TODO Send message
                        Bukkit.getScheduler().runTask(Bootstrap.getJavaPlugin(), new Runnable() {
                            @Override
                            public void run() {
                                afterSync();
                            }
                        });
                    });
        }catch (Exception e){
            e.printStackTrace();
            future.completeExceptionally(e);
        }finally {
            ((MineResetter) this.mineResetter).completed();
        }
        return future;
    }

    protected boolean beforeSync(){
        try {
            MineMeta mineMeta = (MineMeta) this.mineResetter.getMineMeta();
            World mineWorld = mineMeta.getSpawnPoint().getWorld();

            ResetterConfiguration configuration = getResetterConfiguration();
            String beforeResetKey = configuration.getBeforeResetKey();

            Location location = mineMeta.getLocationOfIdentifier(beforeResetKey).orElse(mineMeta.getSpawnPoint());
            for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
                if(!onlinePlayer.getWorld().equals(mineWorld))
                    continue;

                System.out.println("Is "+onlinePlayer.getName()+" inside "+(mineMeta.isInsideMiningRegion(onlinePlayer.getLocation())));
                if(mineMeta.isInsideMiningRegion(onlinePlayer.getLocation())){
                    onlinePlayer.teleport(location);
                    onlinePlayer.sendMessage("Resetting mine");
                    this.resetTeleportedPlayers.add(onlinePlayer.getUniqueId());
                    System.out.println(onlinePlayer.getName());
                }
                System.out.println(this.resetTeleportedPlayers.size());
            }
            return true;
        }catch (Exception e){
            return false;
        }
    }

    protected void afterSync(){
        ResetterConfiguration configuration = getResetterConfiguration();
        if(!configuration.isUseAfterResetHook())
            return;

        String afterResetKey = configuration.getAfterResetKey();
        MineMeta mineMeta = (MineMeta) this.mineResetter.getMineMeta();
        World mineWorld = mineMeta.getSpawnPoint().getWorld();
        Location location = mineMeta.getLocationOfIdentifier(afterResetKey).orElse(mineMeta.getSpawnPoint());
        for (UUID teleportedPlayerUid : resetTeleportedPlayers) {
            Player player = Bukkit.getPlayer(teleportedPlayerUid);
            if(player == null)
                continue;

            if(!player.getWorld().equals(mineWorld))
                continue;

            player.sendMessage("Reset complete");
            if(mineMeta.isInsideMine(player)){
                player.teleport(location);
            }
        }
    }

    protected abstract CompletableFuture<Long> work();

    protected ResetterConfiguration getResetterConfiguration(){
        Bootstrap bootstrap = (Bootstrap) Bootstrap.getJavaPlugin();
        return bootstrap.getPluginInstance().getPrisonManagers().getConfigurationManager().getPluginConfiguration().getResetterConfiguration();
    }

    protected IMineResetter getMineResetter() {
        return mineResetter;
    }
}
