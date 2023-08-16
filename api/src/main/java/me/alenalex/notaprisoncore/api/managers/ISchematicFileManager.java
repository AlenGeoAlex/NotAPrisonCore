package me.alenalex.notaprisoncore.api.managers;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Optional;

public interface ISchematicFileManager {

    @NotNull
    Optional<File> getSchematicFileOfName(String filename);

    @NotNull
    Optional<InputStream> getSchematicStreamOfFile(String filename) throws IOException;

    @NotNull
    Collection<File> getAllAvailableSchematicFiles();

    boolean refresh();

}
