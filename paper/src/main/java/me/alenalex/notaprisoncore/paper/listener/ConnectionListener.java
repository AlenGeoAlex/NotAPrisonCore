package me.alenalex.notaprisoncore.paper.listener;

import me.alenalex.notaprisoncore.api.common.Octet;
import me.alenalex.notaprisoncore.api.common.Pair;
import me.alenalex.notaprisoncore.api.common.Triplet;
import me.alenalex.notaprisoncore.api.entity.mine.IMine;
import me.alenalex.notaprisoncore.api.entity.user.IPrisonUserProfile;
import me.alenalex.notaprisoncore.api.entity.user.IUserSocial;
import me.alenalex.notaprisoncore.api.exceptions.LoadPlayerException;
import me.alenalex.notaprisoncore.paper.NotAPrisonCore;
import me.alenalex.notaprisoncore.paper.abstracts.AbstractEventListener;
import me.alenalex.notaprisoncore.paper.constants.DefaultAdminMessages;
import me.alenalex.notaprisoncore.paper.constants.LocaleConstants;
import me.alenalex.notaprisoncore.paper.data.DataHolder;
import me.alenalex.notaprisoncore.paper.data.MineDataHolder;
import me.alenalex.notaprisoncore.paper.data.UserProfileDataHolder;
import me.alenalex.notaprisoncore.paper.data.UserSocialDataHolder;
import me.alenalex.notaprisoncore.paper.misc.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ConnectionListener extends AbstractEventListener {
    public ConnectionListener(NotAPrisonCore plugin) {
        super(plugin);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event){
        Player player = event.getPlayer();
        UUID playerId = player.getUniqueId();
        DataHolder dataHolder = getDataHolder();
        if(dataHolder.getProfileDataHolder().isLoading(playerId)){ //Quick disconnects and reconnects can cause this
            getPlugin().getLogger().info("Cancelling player saving as the player data is already set to load");
            return;
        }
        IPrisonUserProfile profile = dataHolder.getProfileDataHolder().get(player);
        if(!dataHolder.getProfileDataHolder().isLoaded(player) || profile == null){
            getPlugin().getLogger().info("Cancelling player saving as the player data is not yet loaded");
            return;
        }
        long startTime = System.currentTimeMillis();
        CompletableFuture<Void> redisTask = null;
        CompletableFuture<Void> userSwitchFuture = getStore().getRedisUserProfileStore().setUserOnSwitch(profile);
        IMine mine = dataHolder.getMineDataHolder().get(profile);
        if(profile.hasMine() && mine != null){
            CompletableFuture<Boolean> mineSetFuture = getStore().getRedisMineStore().set(mine);
            redisTask = CompletableFuture.allOf(userSwitchFuture, mineSetFuture);
        }else {
            redisTask = CompletableFuture.allOf(userSwitchFuture);
        }

        redisTask.whenComplete((res, err) -> {
            if(err != null){
                err.printStackTrace();
                getPlugin().getLogger().info("Failed to set the user switch/mine data to redis of "+playerId.toString()+". Check the stack trace below");
                //TODO send message to all servers and on joining show them that your last save was not successfully and try reconnecting
                return;
            }

            long redisEnd = System.currentTimeMillis();
            getPlugin().getLogger().info("Redis messaging for player "+player.getName()+" has been completed in "+(redisEnd - startTime)+" ms.");
        });

        //CompletableFuture<Void> completeTaskFuture = null;

        CompletableFuture<Boolean> profileSaveDbFuture = getStore().getUserProfileStore().updateAsync(profile);
        if(mine != null){
            CompletableFuture<Boolean> mineSaveDbFuture = getStore().getMineStore().updateAsync(mine);
            CompletableFuture.allOf(redisTask, profileSaveDbFuture, mineSaveDbFuture)
                    .whenComplete((res, err) -> {
                        if(err != null){
                            err.printStackTrace();
                            getPlugin().getLogger().severe("Failed to complete player leave procedure for "+playerId.toString()+" Stack trace has been thrown above");
                            return;
                        }
                        long totalEnd = System.currentTimeMillis();
                        getPlugin().getLogger().info("Entire player switching and saving task has been completed on "+(totalEnd - startTime)+" ms.");
                    });
        }else{
            CompletableFuture.allOf(redisTask, profileSaveDbFuture)
                    .whenComplete((res, err) -> {
                        if(err != null){
                            err.printStackTrace();
                            getPlugin().getLogger().severe("Failed to complete player leave procedure for "+playerId.toString()+" Stack trace has been thrown above");
                            return;
                        }
                        long totalEnd = System.currentTimeMillis();
                        getPlugin().getLogger().info("Entire player switching and saving task has been completed on "+(totalEnd - startTime)+" ms.");
                    });
        }


    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        Player player = event.getPlayer();
        DataHolder dataHolder = getDataHolder();
        UUID playerId = player.getUniqueId();
        if(dataHolder.getProfileDataHolder().isLoading(playerId)){ //Quick disconnects and reconnects can cause this
            getPlugin().getLogger().info("Cancelling player loading as the player data is already set to load");
            return;
        }
        getStore().getRedisUserSocialStore().setName(player.getUniqueId(), player.getName());
        long startTime = System.currentTimeMillis();
        dataHolder.getProfileDataHolder().setLoading(playerId);
        if(getConfiguration().getPluginConfiguration().getServerConfiguration().isFreezePlayerOnLoadEnabled()){
            Utils.Freeze.freeze(playerId);
        }

        CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(getConfiguration().getPluginConfiguration().getRedisSyncConfiguration().getRedisNetworkWaitMillis());
                }catch (Exception e){
                    e.printStackTrace();
                    getPlugin().getLogger().warning("Failed to apply network wait for the joining player...");
                }finally {
                    getStore().getRedisUserProfileStore()
                            .isUserOnSwitch(playerId)
                            .handle((res, err) -> {
                                if(err != null){
                                    err.printStackTrace();
                                    return new Pair<>(false, null);
                                }

                                return res;
                            })
                            .thenCompose((isUserSwitching) -> {
                                System.out.println("Is User switching "+isUserSwitching.getFirstItem());
                                if(isUserSwitching.getFirstItem()){
                                    IPrisonUserProfile userProfile = getStore().getUserProfileStore().id(playerId)
                                            .handle((res, err) -> {
                                                if(err != null){
                                                    err.printStackTrace();
                                                    return null;
                                                }

                                                return res.orElse(null);
                                            }).join();
                                    if(userProfile == null){
                                        throw new LoadPlayerException("Failed to load the player from sql/redis of "+playerId.toString());
                                    }
                                    if(!userProfile.hasMine()){
                                        return CompletableFuture.completedFuture(new Triplet<Boolean, Boolean, List<IUserSocial>>(true, false, null)); //First boolean is success or not, 2nd boolean is newUser or not, 3rd is user's social
                                    }
                                    IMine mine = getStore().getRedisMineStore().get(userProfile.getMineId())
                                            .handle((res, err) -> {
                                                if(err != null){
                                                    err.printStackTrace();
                                                    return null;
                                                }
                                                return res;
                                            })
                                            .join();
                                    if(mine == null)
                                        throw new LoadPlayerException("Failed to compose the player data from sql. The player data has been loaded, but the mine data from redis has been returned as null for mine id "+userProfile.getMineId().toString()+" for user "+playerId.toString());
                                    UserProfileDataHolder profileDataHolder = (UserProfileDataHolder) getDataHolder().getProfileDataHolder();
                                    profileDataHolder.load(userProfile);
                                    MineDataHolder mineDataHolder = (MineDataHolder) getDataHolder().getMineDataHolder();
                                    mineDataHolder.load(mine);
                                    return CompletableFuture.completedFuture(new Triplet<Boolean, Boolean, List<IUserSocial>>(true, false, null)); //First boolean is success or not, 2nd boolean is newUser or not, 3rd is user's social
                                }else{
                                    Optional<Octet<IPrisonUserProfile, List<IUserSocial>, IMine, Boolean>> optionalData = getStore().getUserProfileStore().getOrCreateUserProfile(playerId)
                                            .handle((res, err) -> {
                                                if(err != null){
                                                    err.printStackTrace();
                                                    return Optional.<Octet<IPrisonUserProfile, List<IUserSocial>, IMine, Boolean>>empty();
                                                }

                                                return res;
                                            })
                                            .join();

                                    if(!optionalData.isPresent())
                                        throw new LoadPlayerException("Failed to compose full player data from sql of "+playerId.toString());


                                    Octet<IPrisonUserProfile, List<IUserSocial>, IMine, Boolean> dataOctet = optionalData.get();
                                    UserProfileDataHolder profileDataHolder = (UserProfileDataHolder) getDataHolder().getProfileDataHolder();
                                    profileDataHolder.load(dataOctet.getFirstItem());
                                    dataOctet.getFirstItem().sendLocalizedMessage(LocaleConstants.FIRST_JOIN_LOCALE);
                                    if(dataOctet.hasThird()){
                                        MineDataHolder mineDataHolder = (MineDataHolder) getDataHolder().getMineDataHolder();
                                        mineDataHolder.load(dataOctet.getThirdItem());
                                    }

                                    return CompletableFuture.completedFuture(new Triplet<Boolean, Boolean, List<IUserSocial>>(true, dataOctet.getFourthItem(), dataOctet.getSecondItem())); //First boolean is success or not, 2nd boolean is newUser or not, 3rd is user's social
                                }
                            })
                            .whenComplete((res, err) -> {
                                //TODO load user socials and other stuffs

                                //Releasing the player loading should be the first thing to do
                                dataHolder.getProfileDataHolder().releaseLoading(playerId);
                                if(err != null || res.getFirstItem() == null || (!res.getFirstItem())){
                                    if(err != null){
                                        getPlugin().getLogger().warning("Failed to load player data, Stack trace would be provided below");
                                        err.printStackTrace();
                                    }else{
                                        getPlugin().getLogger().warning("Failed to load player data.");
                                    }

                                    if(getConfiguration().getPluginConfiguration().getServerConfiguration().isKickOnLoadFailureEnabled()){
                                        doSync(new Runnable() {
                                            @Override
                                            public void run() {
                                                player.kickPlayer(DefaultAdminMessages.FAILURE_KICK_MESSAGE);
                                            }
                                        });
                                    }
                                    return;
                                }

                                if(res.getSecondItem()){
                                    player.sendMessage("new user");
                                }

                                List<IUserSocial> userSocials = res.getThirdItem();
                                if(userSocials != null){
                                    UserSocialDataHolder socialDataHolder = (UserSocialDataHolder) getDataHolder().getUserSocialDataHolder();
                                    socialDataHolder.load(userSocials);
                                }

                                long timeTook = System.currentTimeMillis() - startTime;
                                getPlugin().getLogger().info("Successfully loaded player in "+timeTook+" ms");
                                if(getConfiguration().getPluginConfiguration().getServerConfiguration().isFreezePlayerOnLoadEnabled()){
                                    Utils.Freeze.removeFreeze(playerId);
                                }
                            });
                }
            }
        });

    }

}
