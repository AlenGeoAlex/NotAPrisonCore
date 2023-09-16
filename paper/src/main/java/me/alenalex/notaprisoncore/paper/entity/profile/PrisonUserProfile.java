package me.alenalex.notaprisoncore.paper.entity.profile;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.config.ILocaleProfile;
import me.alenalex.notaprisoncore.api.entity.IEntityMetaDataHolder;
import me.alenalex.notaprisoncore.api.entity.mine.IMine;
import me.alenalex.notaprisoncore.api.entity.user.IPrisonUserProfile;
import me.alenalex.notaprisoncore.api.locale.IPluginMessage;
import me.alenalex.notaprisoncore.api.locale.LocaleKey;
import me.alenalex.notaprisoncore.api.locale.placeholder.MessagePlaceholder;
import me.alenalex.notaprisoncore.paper.NotAPrisonCore;
import me.alenalex.notaprisoncore.paper.bootstrap.Bootstrap;
import me.alenalex.notaprisoncore.paper.config.LocaleProfile;
import me.alenalex.notaprisoncore.paper.constants.Defaults;
import me.alenalex.notaprisoncore.paper.entity.dataholder.LocalEntityMetaDataHolder;
import me.alenalex.notaprisoncore.paper.entity.dataholder.SharedEntityMetaDataHolder;
import me.alenalex.notaprisoncore.paper.entity.mine.Mine;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@EqualsAndHashCode
@Getter
@ToString
public final class PrisonUserProfile implements IPrisonUserProfile {

    private final UUID playerUniqueId;
    private String localeType;
    private LocaleProfile cachedLocaleProfile;
    private final LocalEntityMetaDataHolder localEntityMetaDataHolder;
    private final SharedEntityMetaDataHolder sharedEntityMetaDataHolder;
    private final Timestamp createdAt;
    private final Timestamp lastLoggedInAt;
    private long level;
    private final BigInteger points;
    private UUID mineId;
    public PrisonUserProfile(UUID playerUniqueId, String localeType, LocalEntityMetaDataHolder localEntityMetaDataHolder, SharedEntityMetaDataHolder sharedEntityMetaDataHolder, Timestamp createdAt, Timestamp lastLoggedInAt, long level, BigInteger points, UUID mineId) {
        this.playerUniqueId = playerUniqueId;
        this.localeType = localeType;
        this.localEntityMetaDataHolder = localEntityMetaDataHolder;
        this.sharedEntityMetaDataHolder = sharedEntityMetaDataHolder;
        this.createdAt = createdAt;
        this.lastLoggedInAt = lastLoggedInAt;
        this.level = level;
        this.points = points;
        this.cacheLocaleProfile();
    }

    @Override
    public @NotNull UUID getUserId() {
        return this.playerUniqueId;
    }

    @Override
    public @Nullable UUID getMineId() {
        return null;
    }

    @Override
    public void resetLocale(){
        this.setLocaleType(Defaults.DEFAULT_LOCALE);
    }

    @Override
    public void setLocaleType(String localeType){
        this.localeType = localeType;
        this.cacheLocaleProfile();
    }

    @Override
    public void sendLocalizedMessage(LocaleKey key, MessagePlaceholder... placeholders) {
        IPluginMessage<Player> message = this.cachedLocaleProfile.getMessageOfKey(key);
        if (message != null) {
            message.send(Arrays.stream(placeholders).collect(Collectors.toList()), Bukkit.getPlayer(this.playerUniqueId));
        }
    }

    @Override
    public void sendLocalizedTitle(LocaleKey key, MessagePlaceholder... placeholders) {
        IPluginMessage<Player> message = this.cachedLocaleProfile.getMessageOfKey(key);
        if(message != null){
            message.sendTitle(Arrays.stream(placeholders).collect(Collectors.toList()), Bukkit.getPlayer(this.playerUniqueId));
        }
    }

    @Override
    public void sendLocalizedActionbar(LocaleKey key, MessagePlaceholder... placeholders) {
        IPluginMessage<Player> message = this.cachedLocaleProfile.getMessageOfKey(key);
        if(message != null){
            message.sendActionBar(Arrays.stream(placeholders).collect(Collectors.toList()), Bukkit.getPlayer(this.playerUniqueId));
        }
    }

    @Override
    public void playSoundFrom(LocaleKey key) {
        IPluginMessage<Player> message = this.cachedLocaleProfile.getMessageOfKey(key);
        if(message != null){
            message.playSound(Bukkit.getPlayer(this.playerUniqueId));
        }
    }

    @Override
    public void sendMessage(String message, MessagePlaceholder... placeholders) {
        Player player = Bukkit.getPlayer(playerUniqueId);
        if(player == null)
            return;

        for (MessagePlaceholder placeholder : placeholders) {
            message = placeholder.replace(message);
        }
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    @Override
    public void chat(IPrisonUserProfile from, String message) {

    }

    @Override
    public @NotNull IEntityMetaDataHolder getSharedDataHolder() {
        return this.sharedEntityMetaDataHolder;
    }

    @Override
    public @NotNull IEntityMetaDataHolder getLocalDataHolder() {
        return this.localEntityMetaDataHolder;
    }

    @Override
    public @NotNull Timestamp getCreatedAt() {
        return this.createdAt;
    }

    @Override
    public @NotNull Timestamp getLastLoggedIn() {
        return this.lastLoggedInAt;
    }

    @Override
    public long getPlayerLevel() {
        return this.level;
    }

    @Override
    public BigInteger getPoints() {
        return this.points;
    }

    @Override
    public CompletableFuture<Boolean> save() {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> loadLocalMetaDataAsync() {
        return null;
    }

    @Override
    public CompletableFuture<Boolean> saveLocalMetaDataAsync() {
        return null;
    }

    @NotNull
    public Optional<Player> getPlayer(){
        return Optional.ofNullable(Bukkit.getPlayer(playerUniqueId));
    }

    private void cacheLocaleProfile(){
        Bootstrap bootstrap = (Bootstrap) Bootstrap.getJavaPlugin();
        NotAPrisonCore pluginInstance = bootstrap.getPluginInstance();
        Optional<ILocaleProfile> optionalILocaleProfile = pluginInstance.getPrisonManagers().localeManager().getLocale(this.localeType);
        ILocaleProfile localeProfile = (ILocaleProfile) optionalILocaleProfile.orElse(null);
        Optional<Player> playerOptional = getPlayer();
        String playerIdentifier =  playerOptional.isPresent() ? playerOptional.get().getName() : playerUniqueId.toString();
        if(localeProfile == null){
            localeProfile = pluginInstance.getPrisonManagers().localeManager().getDefaultLocale();
            pluginInstance.getLogger().info("Failed to locate the locale of player [Name/UUID="+playerIdentifier+"]. The specified player locale type is [LocaleType"+localeType+"]. Falling back to the default locale");
        }
        this.cachedLocaleProfile = (LocaleProfile) localeProfile;
    }
}
