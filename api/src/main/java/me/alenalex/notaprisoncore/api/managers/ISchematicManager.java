package me.alenalex.notaprisoncore.api.managers;

import me.alenalex.notaprisoncore.api.provider.ISchematicProvider;
import org.jetbrains.annotations.NotNull;

public interface ISchematicManager {

    @NotNull ISchematicFileManager getSchematicFileManager();

    @NotNull ISchematicProvider<?> getHookedSchematicProvider();
}
