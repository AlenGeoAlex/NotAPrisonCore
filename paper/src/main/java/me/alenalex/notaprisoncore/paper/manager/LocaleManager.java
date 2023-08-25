package me.alenalex.notaprisoncore.paper.manager;

import me.alenalex.notaprisoncore.api.config.ILocaleProfile;
import me.alenalex.notaprisoncore.api.entity.user.IPrisonUserProfile;
import me.alenalex.notaprisoncore.api.enums.ConfigType;
import me.alenalex.notaprisoncore.api.exceptions.FailedConfigurationException;
import me.alenalex.notaprisoncore.api.locale.LocaleKey;
import me.alenalex.notaprisoncore.api.managers.ILocaleManager;
import me.alenalex.notaprisoncore.paper.config.LocaleProfile;
import me.alenalex.notaprisoncore.paper.constants.Defaults;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;

public class LocaleManager implements ILocaleManager {

    private final PrisonManagers prisonManagers;
    private final HashMap<String, LocaleProfile> languages;
    private final File localeDirectory;
    public LocaleManager(PrisonManagers prisonManagers) {
        this.prisonManagers = prisonManagers;
        this.languages = new HashMap<>();
        this.localeDirectory = new File(this.prisonManagers.getPluginInstance().getBukkitPlugin().getDataFolder(), "locale");
    }

    public void init(){
        if(!localeDirectory.exists()){
            localeDirectory.mkdirs();
        }

        initDefault();
    }

    public void load(){
        File[] localeRawFiles = localeDirectory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".yml") || name.endsWith(".yaml");
            }
        });

        if(localeRawFiles == null || localeRawFiles.length == 0){
            throw new FailedConfigurationException(ConfigType.LOCALE, "No locale configuration file is found with in /locale/*", null);
        }

        List<String> conflictedLocales = null;
        this.prisonManagers.getPluginInstance().getLogger().info("Detected locales");
        for (File localeRawFile : localeRawFiles) {
            String localeName = localeRawFile.getName().split("\\.")[0];
            if(languages.containsKey(localeName)){
                this.prisonManagers.getPluginInstance().getLogger().warning("Multiple locale found with the same name [FileName="+localeRawFile.getName()+"] detected . This locale won't be loaded");
                if(conflictedLocales == null){
                    conflictedLocales = new ArrayList<>();
                }
                conflictedLocales.add(localeName);
                continue;
            }

            LocaleProfile profile = new LocaleProfile(this, localeRawFile);
            try {
                profile.init();
            } catch (IOException e) {
                throw new FailedConfigurationException(ConfigType.LOCALE, "Failed to initialize the locale profile - "+localeRawFile.getName(), null);
            }
            this.languages.put(localeName, profile);
            this.prisonManagers.getPluginInstance().getLogger().info("- "+localeName);
        }

        if(conflictedLocales != null){
            for (String conflictedLocale : conflictedLocales) {
                this.languages.remove(conflictedLocale);
            }
        }

        this.prisonManagers.getPluginInstance().getLogger().info("Starting locales loading");
        for (Map.Entry<String, LocaleProfile> profileEntry : this.languages.entrySet()) {
            profileEntry.getValue().load();
            this.prisonManagers.getPluginInstance().getLogger().info("- Locale loaded "+profileEntry.getKey());
        }

    }

    private void initDefault(){
        File defaultLocale = new File(localeDirectory, "default.yml");
        if(!defaultLocale.exists()){
            this.prisonManagers.getPluginInstance().getBukkitPlugin().saveResource("locale/default.yml", false);
            this.prisonManagers.getPluginInstance().getLogger().info("Default locale default.yml is missing. Please don't remove this locale");
        }
    }

    private LocaleProfile loadDefault(){
        File defaultLocale = new File(localeDirectory, "default.yml");
        String localeName = defaultLocale.getName().split("\\.")[0];
        LocaleProfile profile = new LocaleProfile(this, defaultLocale);
        profile.load();
        this.languages.put(localeName, profile);
        return profile;
    }

    public PrisonManagers getPrisonManagers() {
        return prisonManagers;
    }


    @Override
    public @NotNull Optional<ILocaleProfile> getLocale(String locale) {
        return Optional.ofNullable(this.languages.get(locale));
    }

    @Override
    public @NotNull ILocaleProfile getDefaultLocale() {
        LocaleProfile profile = this.languages.get(Defaults.DEFAULT_LOCALE);
        if(profile == null){
            initDefault();
            profile = loadDefault();
        }
        return profile;
    }

    @Override
    public void sendLocaleMessage(IPrisonUserProfile user, LocaleKey key) {
        user.sendLocalizedMessage(key);
    }
}
