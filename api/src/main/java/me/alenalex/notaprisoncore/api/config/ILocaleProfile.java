package me.alenalex.notaprisoncore.api.config;

import me.alenalex.notaprisoncore.api.locale.IPluginMessage;
import me.alenalex.notaprisoncore.api.locale.LocaleKey;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ILocaleProfile {
    @Nullable
    IPluginMessage<?> getMessageOfKey(@NotNull LocaleKey key);
    boolean isKeyRegistered(@NotNull LocaleKey key);
    boolean isKeyRegistered(@NotNull String key);


}
