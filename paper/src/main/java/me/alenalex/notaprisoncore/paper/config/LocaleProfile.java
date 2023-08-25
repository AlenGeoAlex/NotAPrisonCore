package me.alenalex.notaprisoncore.paper.config;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import me.alenalex.notaprisoncore.api.config.ILocaleProfile;
import me.alenalex.notaprisoncore.api.enums.ConfigType;
import me.alenalex.notaprisoncore.api.exceptions.FailedConfigurationException;
import me.alenalex.notaprisoncore.api.locale.AbstractMessage;
import me.alenalex.notaprisoncore.api.locale.IPluginMessage;
import me.alenalex.notaprisoncore.api.locale.LocaleKey;
import me.alenalex.notaprisoncore.paper.constants.LocaleConstants;
import me.alenalex.notaprisoncore.paper.locale.Message;
import me.alenalex.notaprisoncore.paper.manager.LocaleManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

public class LocaleProfile implements ILocaleProfile {

    private final LocaleManager localeManager;
    private final File localeConfigurationFile;
    private YamlDocument localeDocument;
    private final HashMap<LocaleKey, AbstractMessage> pluginMessageHashMap;
    public LocaleProfile(LocaleManager localeManager, File localeConfigurationFile) {
        this.localeManager = localeManager;
        this.localeConfigurationFile = localeConfigurationFile;
        this.pluginMessageHashMap = new HashMap<>();
    }

    public void init() throws IOException {
        this.localeDocument = YamlDocument.create(
                localeConfigurationFile,
                GeneralSettings.DEFAULT,
                LoaderSettings.builder()
                        .setAutoUpdate(false)
                        .setErrorLabel("NPCore-Locale")
                        .setDetailedErrors(true)
                        .build(),
                DumperSettings.DEFAULT,
                UpdaterSettings.builder().setVersioning(new BasicVersioning("locale-version")).setAutoSave(true).build()
        );
    }

    public void load(boolean doReload){
        int totalKeys = 0;
        if(this.localeDocument == null)
            throw new FailedConfigurationException(ConfigType.LOCALE, "The locale file ["+localeConfigurationFile.getName()+"] isn't yet initialized", null);

        if(doReload){
            try {
                this.localeDocument.reload();
                this.pluginMessageHashMap.clear();
            } catch (IOException e) {
                throw new FailedConfigurationException(ConfigType.LOCALE, "Failed to reload locale file "+this.localeConfigurationFile.getName()+". Please see detailed stack trace below", e);
            }
        }

        for (Field eachLocaleKey : LocaleConstants.class.getDeclaredFields()) {
            eachLocaleKey.setAccessible(true);
            if(!Modifier.isStatic(eachLocaleKey.getModifiers()) && !eachLocaleKey.getType().equals(LocaleKey.class))
                continue;

            totalKeys++;
            LocaleKey key = null;
            try {
                key = (LocaleKey) eachLocaleKey.get(null);
            }catch (Exception e){
                localeManager.getPrisonManagers().getPluginInstance().getLogger().warning("Failed to get the key for locale of "+eachLocaleKey.getName()+". Please check this with the developer. Skipping the said key");
                e.printStackTrace();
            }

            if(key == null){
                localeManager.getPrisonManagers().getPluginInstance().getLogger().warning("Failed to get the key for locale of "+eachLocaleKey.getName()+". Please check this with the developer");
                continue;
            }

            try {
                loadKey(key);
            }catch (FailedConfigurationException e){
                localeManager.getPrisonManagers().getPluginInstance().getLogger().warning("Failed to load the locale key "+key.getMessageKey()+" on locale "+this.localeConfigurationFile.getName()+".");
            }
            eachLocaleKey.setAccessible(false);
        }
        localeManager.getPrisonManagers().getPluginInstance().getLogger().info("Loaded "+pluginMessageHashMap.size()+" out of "+totalKeys+" for locale - "+localeConfigurationFile.getName().split("\\.")[0]);
    }

    public void load(){
        this.load(false);
    }

    public void loadKey(LocaleKey key){
        if(this.localeDocument == null)
            throw new FailedConfigurationException(ConfigType.LOCALE, "The locale file ["+localeConfigurationFile.getName()+"] isn't yet initialized", null);

        String messageKey = key.getMessageKey();
        if(!localeDocument.contains(messageKey))
        {
            localeManager.getPrisonManagers().getPluginInstance().getLogger().warning("Failed to locate the language for key [Key="+messageKey+"] with in the config file [File="+localeConfigurationFile.getName()+"]. Please add it if its missing! This key will be skipped");
            return;
        }
        List<String> message = null;
        if(localeDocument.isString(messageKey)){
            message = Collections.singletonList(localeDocument.getString(messageKey));
        }else{
            message = localeDocument.getStringList(messageKey);
        }

        this.pluginMessageHashMap.put(key, new Message(message));
    }

    @Override
    @Nullable
    public IPluginMessage<Player> getMessageOfKey(@NotNull LocaleKey key) {
        return this.pluginMessageHashMap.get(key);
    }

    @Override
    public boolean isKeyRegistered(@NotNull LocaleKey key) {
        return this.pluginMessageHashMap.containsKey(key);
    }

    @Override
    public boolean isKeyRegistered(@NotNull String key) {
        return this.isKeyRegistered(LocaleKey.of(key));
    }


}
