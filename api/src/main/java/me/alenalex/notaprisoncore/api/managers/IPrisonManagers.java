package me.alenalex.notaprisoncore.api.managers;

import org.jetbrains.annotations.NotNull;

public interface IPrisonManagers {

    @NotNull IConfigurationManager getConfigurationManager();

    @NotNull ISchematicManager getSchematic();

    @NotNull ILocaleManager getLocaleManager();

    @NotNull IWorldManager getWorldManager();

    @NotNull IMineManager getMineManager();

}
