package me.alenalex.notaprisoncore.paper.scheduler;

import me.alenalex.notaprisoncore.api.config.options.ClaimQueueConfiguration;
import me.alenalex.notaprisoncore.api.entity.IClaimQueueEntity;
import me.alenalex.notaprisoncore.api.entity.mine.IMine;
import me.alenalex.notaprisoncore.api.entity.user.IPrisonUserProfile;
import me.alenalex.notaprisoncore.api.queue.IPluginQueue;
import me.alenalex.notaprisoncore.api.scheduler.IClaimQueueScheduler;
import me.alenalex.notaprisoncore.paper.constants.LocaleConstants;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

public class ClaimQueueScheduler extends Thread implements IClaimQueueScheduler {

    private final PrisonScheduler prisonScheduler;
    private final Set<UUID> currentlyClaiming;

    public ClaimQueueScheduler(PrisonScheduler prisonScheduler) {
        this.prisonScheduler = prisonScheduler;
        this.currentlyClaiming = ConcurrentHashMap.newKeySet();
    }

    private boolean enabled;

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isUserOnProcessList(UUID uuid) {
        return currentlyClaiming.contains(uuid);
    }

    public void setEnabled(boolean enabled){
        this.enabled = enabled;
    }

    @Override
    public void run() {
        IPluginQueue<IClaimQueueEntity> claimQueue = prisonScheduler.getPluginInstance().getPrisonQueueProvider().getClaimQueue();
        ClaimQueueConfiguration claimQueueConfiguration = prisonScheduler.getPluginInstance().getPrisonManagers().getConfigurationManager().getPluginConfiguration().getClaimQueueConfiguration();
        try {
            while (claimQueueConfiguration.isEnabled()){
                boolean hasElementsInQueue = claimQueue.getActiveCount() > 0;

                if(!hasElementsInQueue)
                   return;

                List<IClaimQueueEntity> dequeued = claimQueue.dequeue(claimQueueConfiguration.getQueueLimit());
                if(dequeued.isEmpty())
                    return;
                List<CompletableFuture<IMine>> claimFuture = new ArrayList<>();
                for (IClaimQueueEntity queueEntity : dequeued) {
                    Player player = queueEntity.get();
                    if(!player.isOnline())
                        continue;

                    IPrisonUserProfile userProfile = prisonScheduler.getPluginInstance().getDataHolder().getProfileDataHolder().get(player.getUniqueId());
                    if(userProfile == null)
                        continue;
                    this.currentlyClaiming.add(player.getUniqueId());
                    CompletableFuture<IMine> future = prisonScheduler.getPluginInstance().getPrisonManagers().getMineManager().claimMineForUser(userProfile);
                    future.whenComplete((mine, err) -> {
                        if(!player.isOnline())
                            return;

                        if(err != null){
                            err.printStackTrace();
                            userProfile.sendLocalizedMessage(LocaleConstants.MINE_CLAIM_FAILED);
                            return;
                        }

                        userProfile.sendLocalizedMessage(LocaleConstants.MINE_CLAIM_SUCCESS);
                    });
                    claimFuture.add(future);
                }

                CompletableFuture.allOf(claimFuture.toArray(new CompletableFuture[0])).join();
                this.currentlyClaiming.clear();
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


}
