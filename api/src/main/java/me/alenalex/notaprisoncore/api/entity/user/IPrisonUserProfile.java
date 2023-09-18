package me.alenalex.notaprisoncore.api.entity.user;

import me.alenalex.notaprisoncore.api.entity.IEntityMetaDataHolder;
import me.alenalex.notaprisoncore.api.entity.mine.IMine;
import me.alenalex.notaprisoncore.api.locale.LocaleKey;
import me.alenalex.notaprisoncore.api.locale.placeholder.MessagePlaceholder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public interface IPrisonUserProfile {

    @NotNull UUID getUserId();
    @Nullable UUID getMineId();
    void resetLocale();
    void setLocaleType(String localeType);
    String getLocaleType();
    void sendLocalizedMessage(LocaleKey key, MessagePlaceholder... placeholders);
    void sendLocalizedTitle(LocaleKey key, MessagePlaceholder... placeholders);
    void sendLocalizedActionbar(LocaleKey key, MessagePlaceholder... placeholders);
    void playSoundFrom(LocaleKey key);
    void sendMessage(String message, MessagePlaceholder... placeholders);
    void chat(IPrisonUserProfile from, String message);
    @NotNull IEntityMetaDataHolder getSharedDataHolder();
    @NotNull IEntityMetaDataHolder getLocalDataHolder();
    @NotNull Timestamp getCreatedAt();
    @NotNull Timestamp getLastLoggedIn();
    long getPlayerLevel();
    BigInteger getPoints();
    CompletableFuture<Boolean> save();
    CompletableFuture<Boolean> loadLocalMetaDataAsync();
    CompletableFuture<Boolean> saveLocalMetaDataAsync();
    default boolean hasMine(){
        return getMineId() != null;
    }


}
