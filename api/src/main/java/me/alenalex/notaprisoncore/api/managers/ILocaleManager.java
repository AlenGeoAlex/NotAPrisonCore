package me.alenalex.notaprisoncore.api.managers;

import me.alenalex.notaprisoncore.api.config.ILocaleProfile;
import me.alenalex.notaprisoncore.api.entity.user.IPrisonUserProfile;
import me.alenalex.notaprisoncore.api.locale.LocaleKey;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

/**
 * Locale manager is responsible to load locale profiles of different locales
 */
public interface ILocaleManager {

    /**
     * Get the locale profile of the specified language
     * @param locale id of the locale. This would be the file name of the file in /plugins/data-folder/locale
     * @return The profile of the specified language
     */
    @NotNull Optional<ILocaleProfile> getLocale(String locale);

    /**
     * Get the default locale of the plugin, This would never be null. If missing, the plugin will load up new one
     * @return The default locale or /plugins/data-folder/locale/default.yml
     */
    @NotNull ILocaleProfile getDefaultLocale();

    /**
     * Send a localized message to player. If the message is missing from the locale, it would fail silently
     * @param user The user to send the message
     * @param key The key of the localized message
     */
    void sendLocaleMessage(IPrisonUserProfile user, LocaleKey key);

}
