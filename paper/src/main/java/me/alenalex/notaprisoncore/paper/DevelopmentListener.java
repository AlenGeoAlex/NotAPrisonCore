package me.alenalex.notaprisoncore.paper;

import me.alenalex.notaprisoncore.api.entity.mine.IMine;
import me.alenalex.notaprisoncore.api.entity.mine.IMineMeta;
import me.alenalex.notaprisoncore.api.entity.mine.IMineResetWorker;
import me.alenalex.notaprisoncore.paper.bootstrap.Bootstrap;
import me.alenalex.notaprisoncore.paper.entity.mine.BlockChoices;
import me.alenalex.notaprisoncore.paper.entity.mine.Mine;
import me.alenalex.notaprisoncore.paper.entity.mine.MineMeta;
import me.alenalex.notaprisoncore.paper.wrapper.GsonWrapper;
import net.royawesome.jlibnoise.module.combiner.Min;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.sql.SQLException;
import java.util.Optional;
import java.util.UUID;

public class DevelopmentListener implements Listener {

    public final NotAPrisonCore core;

    public DevelopmentListener(NotAPrisonCore core) {
        this.core = core;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
//        Bukkit.getScheduler().runTaskLater(core.getBukkitPlugin(), new Runnable() {
//            @Override
//            public void run() {
//                Optional<IMineMeta> unclaimedMeta = core.getPrisonManagers().getPluginInstance().getDataHolder().mineMetaDataHolder().getUnclaimedMeta();
//                IMineMeta meta = unclaimedMeta.get();
//                if (meta == null)
//                    return;
//
//                Mine mine = new Mine(event.getPlayer().getUniqueId(), (MineMeta) meta);
//                mine.setDefaults(core.getPrisonManagers());
//                core.getPrisonDataStore().mineStore().claimMine(mine)
//                        .whenComplete((m, er) -> {
//                            if (er != null) {
//                                er.printStackTrace();
//                                if (er instanceof SQLException) {
//                                    System.out.println(((SQLException) er).getSQLState());
//                                    System.out.println(er.getMessage());
//                                }
//                                core.getDataHolder().mineMetaDataHolder().releaseLockedMeta(meta);
//                            }
//
//                            UUID uuid = m.get();
//                            if (uuid == null) {
//                                System.out.println("No uuid");
//                            }
//
//                            System.out.println(uuid);
//                            core.getDataHolder().mineMetaDataHolder().claimMeta(meta);
//                            core.getPrisonDataStore().redisMineStore().set(mine);
//                            core.getPrisonDataStore().redisMineStore().get(mine.getId())
//                                    .whenComplete((iMine, err) -> {
//                                        if(err != null){
//                                            err.printStackTrace();
//                                            return;
//                                        }
//
//                                       System.out.println(GsonWrapper.singleton().stringify(iMine));
//                                    });
//                        });
//            }
//        }, 100);

        core.getPrisonDataStore().mineStore().id(UUID.fromString("002e1d18-4ffa-11ee-9938-020017006c0c")).whenComplete((m, er) -> {
            if(er != null){
                er.printStackTrace();
                return;
            }

            IMine mine = m.orElse(null);
            if(mine == null)
            {
                System.out.println("No Mine");
                return;
            }
            core.getPrisonDataStore().redisMineStore().set(mine);
            IMine join = core.getPrisonDataStore().redisMineStore().get(mine.getId())
                    .handle((iMine, err) -> {
                        System.out.println("00");
                        try {
                            if (err != null) {
                                err.printStackTrace();
                                return null;
                            }
                            System.out.println("123");
                            System.out.println(GsonWrapper.singleton().stringify(iMine));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        return iMine;
                    }).join();

            System.out.println("Outside future");
            System.out.println(join.toString());
        });
    }
}



