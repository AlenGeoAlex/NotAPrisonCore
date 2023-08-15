package me.alenalex.notaprisoncore.api.managers;

import org.jetbrains.annotations.NotNull;

public interface IPrisonManagers {

    @NotNull
    IConfigurationManager configurationManager();

    @NotNull ISchematicManager schematicManager();

    @NotNull ILocaleManager localeManager();

    @NotNull IWorldManager worldManager();

}
