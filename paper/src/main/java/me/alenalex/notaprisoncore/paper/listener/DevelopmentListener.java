package me.alenalex.notaprisoncore.paper.listener;

import me.alenalex.notaprisoncore.api.exceptions.NoSchematicFound;
import me.alenalex.notaprisoncore.paper.NotAPrisonCore;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class DevelopmentListener implements Listener {

    public final NotAPrisonCore core;

    public DevelopmentListener(NotAPrisonCore core) {
        this.core = core;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event){
        Bukkit.getScheduler().runTaskLater(core.getBukkitPlugin(), new Runnable() {
            @Override
            public void run() {
                try {
                    core.getPrisonManagers().mineManager().generator().generateMine(event.getPlayer(), "Minas");
                    event.getPlayer().teleport(new Location(Bukkit.getWorld("Minas"), 0, 0, 0));
                } catch (NoSchematicFound e) {
                    e.printStackTrace();
                }
            }
        }, 60);
    }
}
