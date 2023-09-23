package me.alenalex.notaprisoncore.paper;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class DevelopmentListener implements Listener {

    public final NotAPrisonCore core;

    public DevelopmentListener(NotAPrisonCore core) {
        this.core = core;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

    }
}



