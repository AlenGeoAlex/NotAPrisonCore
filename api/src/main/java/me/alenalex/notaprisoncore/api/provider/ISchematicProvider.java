package me.alenalex.notaprisoncore.api.provider;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Optional;

public interface ISchematicProvider<T> {
    @NotNull
    Optional<T> getSchematic(File file);

    @NotNull Class<T> getSchematicType();

    @NotNull String providerPluginName();

}
