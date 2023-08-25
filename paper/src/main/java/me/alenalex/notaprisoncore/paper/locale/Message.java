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

        WrappedMessageType wrappedMessageType = parse(messagePlaceholders);

        for (String eachMessage : wrappedMessageType.getMessage()) {
            player.sendMessage(eachMessage);
        }

        String[] title = wrappedMessageType.getTitle();
        if(title[0] != null || title[1] != null)
            player.sendTitle(title[0], title[1], 1000, 4000, 1000);

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
        WrappedMessageType wrappedMessageType = parse(messagePlaceholders);

        for (String eachMessage : wrappedMessageType.getMessage()) {
            for (Player player : players) {
                player.sendMessage(eachMessage);
            }
        }

        String[] title = wrappedMessageType.getTitle();
        if(title[0] != null || title[1] != null){
            for (Player player : players) {
                player.sendTitle(title[0], title[1], 1000, 4000, 1000);
            }
        }

        String actionBar = wrappedMessageType.getActionBar();
        if(actionBar != null && !actionBar.isEmpty()){
            for (Player player : players) {
                player.sendActionBar(actionBar);
            }
        }

        String sound = wrappedMessageType.getSound();
        if(sound != null && !sound.isEmpty())
        {
            try {
                Sound actionSound = Sound.valueOf(sound);
                for (Player player : players) {
                    player.playSound(player.getLocation(), actionSound, 1f, 0f);
                }
            }catch (Exception ignored){}
        }
    }

    @Override
    public void sendTitle(Collection<MessagePlaceholder> messagePlaceholders, Player player) {
        if(player == null)
            return;

        String[] title = parseTitle(messagePlaceholders);
        if(title[0] != null || title[1] != null)
            player.sendTitle(title[0], title[1], 1000, 4000, 1000);
    }

    @Override
    public void sendTitle(Collection<MessagePlaceholder> messagePlaceholders, Player... players) {

        String[] title = parseTitle(messagePlaceholders);
        if(title[0] != null || title[1] != null)
            for (Player player : players) {
                player.sendTitle(title[0], title[1], 1000, 4000, 1000);
            }
    }

    @Override
    public void sendActionBar(Collection<MessagePlaceholder> messagePlaceholders, Player player) {
        if(player == null)
            return;

        String actionBar = parseActionBar(messagePlaceholders);
        if(actionBar != null){
            player.sendActionBar(actionBar);
        }
    }

    @Override
    public void sendActionBar(Collection<MessagePlaceholder> messagePlaceholders, Player... players) {
        String actionBar = parseActionBar(messagePlaceholders);
        if(actionBar != null){
            for (Player player : players) {
                player.sendActionBar(actionBar);
            }
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
        send(messagePlaceholders, Bukkit.getOnlinePlayers().toArray(new Player[0]));
    }
}
