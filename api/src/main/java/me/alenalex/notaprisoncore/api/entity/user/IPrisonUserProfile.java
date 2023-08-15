package me.alenalex.notaprisoncore.api.entity.user;

import me.alenalex.notaprisoncore.api.entity.IEntityDataHolder;
import me.alenalex.notaprisoncore.api.locale.LocaleKey;
import me.alenalex.notaprisoncore.api.locale.placeholder.MessagePlaceholder;

import java.util.UUID;

public interface IPrisonUserProfile {

    UUID getUserId();
    void resetLocale();

    void setLocaleType(String localeType);

    void sendLocalizedMessage(LocaleKey key, MessagePlaceholder... placeholders);

    void sendLocalizedTitle(LocaleKey key, MessagePlaceholder... placeholders);

    void sendLocalizedActionbar(LocaleKey key, MessagePlaceholder... placeholders);

    void playSoundFrom(LocaleKey key);

    void sendMessage(String message, MessagePlaceholder... placeholders);

    void chat(IPrisonUserProfile from, String message);
    IEntityDataHolder getSharedDataHolder();
    IEntityDataHolder getLocalDataHolder();

}
