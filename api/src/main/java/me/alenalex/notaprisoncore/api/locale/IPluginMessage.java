package me.alenalex.notaprisoncore.api.locale;

import me.alenalex.notaprisoncore.api.locale.placeholder.MessagePlaceholder;

import java.util.Collection;

public interface IPluginMessage<T> {
    void send(Collection<MessagePlaceholder> messagePlaceholders, T player);
    void send(Collection<MessagePlaceholder> messagePlaceholders, T... players);
    void sendTitle(Collection<MessagePlaceholder> messagePlaceholders, T player);
    void sendTitle(Collection<MessagePlaceholder> messagePlaceholders, T... players);
    void sendActionBar(Collection<MessagePlaceholder> messagePlaceholders, T player);
    void sendActionBar(Collection<MessagePlaceholder> messagePlaceholders, T... players);
    void playSound(T player);
    void playSound(T... player);
    void broadcast(Collection<MessagePlaceholder> messagePlaceholders);
    boolean isSingleLine();
}
