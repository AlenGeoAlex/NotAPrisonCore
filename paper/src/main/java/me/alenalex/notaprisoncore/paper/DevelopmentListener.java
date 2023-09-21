package me.alenalex.notaprisoncore.paper;

import me.alenalex.notaprisoncore.message.models.Sample;
import me.alenalex.notaprisoncore.message.models.SampleOneWay;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.UUID;

public class DevelopmentListener implements Listener {

    public final NotAPrisonCore core;

    public DevelopmentListener(NotAPrisonCore core) {
        this.core = core;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {

    }
}



