package me.alenalex.notaprisoncore.paper.locale;

import me.alenalex.notaprisoncore.api.locale.AbstractMessage;
import me.alenalex.notaprisoncore.api.locale.placeholder.MessagePlaceholder;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class Message extends AbstractMessage {
    public Message(List<String> message) {
        super(message);
    }
    @Override
    protected String colorize(String message) {
        if(message == null)
            return null;

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    @Override
    public void send(Collection<MessagePlaceholder> messagePlaceholders, Player player) {
        if(player == null)
            return;

        WrappedMessageType wrappedMessageType = parse(messagePlaceholders, player);

        for (String eachMessage : wrappedMessageType.getMessage()) {
            player.sendMessage(eachMessage);
        }

        String[] title = wrappedMessageType.getTitle();
        if(title[0] != null || title[1] != null)
            player.sendTitle(title[0], title[1], wrappedMessageType.getFadeIn(), wrappedMessageType.getStay(), wrappedMessageType.getFadeOut());

        String actionBar = wrappedMessageType.getActionBar();
        if(actionBar != null && !actionBar.isEmpty()){
            player.sendActionBar(actionBar);
        }

        String sound = wrappedMessageType.getSound();
        if(sound != null && !sound.isEmpty()){
            try {
                Sound actionSound = Sound.valueOf(sound);
                player.playSound(player.getLocation(), actionSound, 1f, 0f);
            }catch (IllegalArgumentException ignored){}
        }
    }

    @Override
    public void send(Collection<MessagePlaceholder> messagePlaceholders, Player... players) {
        for (Player player : players) {
            send(messagePlaceholders, player);
        }
    }

    @Override
    public void sendTitle(Collection<MessagePlaceholder> messagePlaceholders, Player player) {
        if(player == null)
            return;

        String[] title = parseTitle(messagePlaceholders, player);
        if(title[0] != null || title[1] != null)
            player.sendTitle(title[0], title[1], 20, 80, 10);
    }

    @Override
    public void sendTitle(Collection<MessagePlaceholder> messagePlaceholders, Player... players) {
        for (Player player : players) {
            sendTitle(messagePlaceholders, player);
        }
    }

    @Override
    public void sendActionBar(Collection<MessagePlaceholder> messagePlaceholders, Player player) {
        if(player == null)
            return;

        String actionBar = parseActionBar(messagePlaceholders, player);
        if(actionBar != null){
            player.sendActionBar(actionBar);
        }
    }

    @Override
    public void sendActionBar(Collection<MessagePlaceholder> messagePlaceholders, Player... players) {
        for (Player player : players) {
            sendActionBar(messagePlaceholders, player);
        }
    }

    @Override
    public void playSound(Player player) {
        if(player == null)
            return;

        String sound = getWrappedMessageType().getSound();
        if(sound == null){
            return;
        }
        try {
            Sound actionSound = Sound.valueOf(sound);
            player.playSound(player.getLocation(), actionSound, 1f, 0f);
        }catch (IllegalArgumentException ignored){}
    }

    @Override
    public void playSound(Player... players) {
        String sound = getWrappedMessageType().getSound();
        if(sound == null){
            return;
        }
        try {
            Sound actionSound = Sound.valueOf(sound);
            for (Player player : players) {
                player.playSound(player.getLocation(), actionSound, 1f, 0f);
            }
        }catch (IllegalArgumentException ignored){}
    }

    @Override
    public void broadcast(Collection<MessagePlaceholder> messagePlaceholders) {
        send(messagePlaceholders,  Bukkit.getOnlinePlayers().toArray(new Player[0]));
    }
}
