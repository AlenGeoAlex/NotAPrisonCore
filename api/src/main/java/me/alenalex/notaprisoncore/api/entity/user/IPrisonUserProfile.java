package me.alenalex.notaprisoncore.api.entity.user;

import me.alenalex.notaprisoncore.api.entity.IEntityMetaDataHolder;
import me.alenalex.notaprisoncore.api.locale.LocaleKey;
import me.alenalex.notaprisoncore.api.locale.placeholder.MessagePlaceholder;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

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
    IEntityMetaDataHolder getSharedDataHolder();
    IEntityMetaDataHolder getLocalDataHolder();
    Timestamp getCreatedAt();
    Timestamp getLastLoggedIn();
    long getPlayerLevel();
    BigInteger getPoints();
    CompletableFuture<Boolean> save();
    CompletableFuture<Boolean> loadLocalMetaDataAsync();
    CompletableFuture<Boolean> saveLocalMetaDataAsync();

}
