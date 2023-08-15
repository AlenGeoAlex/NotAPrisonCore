package me.alenalex.notaprisoncore.api.abstracts;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import dev.dejvokep.boostedyaml.spigot.SpigotSerializer;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@EqualsAndHashCode
@Getter
@ToString
public abstract class AbstractFileConfiguration {
    protected YamlDocument configDocument;
    private final File configFile;
    private final InputStream fileStream;
    public AbstractFileConfiguration(File configFile) {
        this.configFile = configFile;
        this.fileStream = null;
    }

    public AbstractFileConfiguration(File configFile, InputStream fileStream) {
        this.configFile = configFile;
        this.fileStream = fileStream;
    }

    public void create() throws IOException {
        if(this.fileStream == null){
            this.configDocument = YamlDocument.create(
                    this.configFile,
                    GeneralSettings.builder().setSerializer(SpigotSerializer.getInstance()).build(),
                    LoaderSettings.builder().setCreateFileIfAbsent(true).setErrorLabel("NPCore-Config").setDetailedErrors(true).build(),
                    DumperSettings.DEFAULT,
                    UpdaterSettings.builder().setAutoSave(true).setVersioning(new BasicVersioning("config-version")).build()
            );
        }else{
            this.configDocument = YamlDocument.create(
                    this.configFile,
                    this.fileStream,
                    GeneralSettings.builder().setSerializer(SpigotSerializer.getInstance()).build(),
                    LoaderSettings.builder().setCreateFileIfAbsent(true).setErrorLabel("NPCore-Config").setDetailedErrors(true).build(),
                    DumperSettings.DEFAULT,
                    UpdaterSettings.builder().setAutoSave(true).setVersioning(new BasicVersioning("config-version")).build()
            );
        }
        this.configDocument.update();
    }

    public abstract void load();
}
