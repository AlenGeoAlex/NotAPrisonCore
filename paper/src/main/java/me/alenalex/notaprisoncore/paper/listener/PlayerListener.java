package me.alenalex.notaprisoncore.paper.listener;

import me.alenalex.notaprisoncore.paper.NotAPrisonCore;
import me.alenalex.notaprisoncore.paper.abstracts.AbstractEventListener;
import me.alenalex.notaprisoncore.paper.misc.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerListener extends AbstractEventListener {
    public PlayerListener(NotAPrisonCore plugin) {
        super(plugin);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e) {
        if(Utils.Freeze.isFrozen(e.getPlayer()))
            return;

        if (e.getFrom().getX() == e.getTo().getX() &&
                e.getFrom().getY() == e.getTo().getY() &&
                e.getFrom().getZ() == e.getTo().getZ()) return;

        e.setTo(e.getFrom());
    }
}
