package me.alenalex.notaprisoncore.api.abstracts.store;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.exceptions.store.FileStoreException;

import java.io.File;
import java.io.IOException;

@EqualsAndHashCode
@Getter
@ToString
public abstract class AbstractFileStore {

    private final String storeName;
    private YamlDocument storeDocument;
    private final File storeFile;

    public AbstractFileStore(String storeName, File storeFile) {
        this.storeName = storeName;
        this.storeFile = storeFile;
    }

    public void initStore(){
        if(!storeFile.exists()){
            try {
                storeFile.createNewFile();
            } catch (IOException e) {
                throw new FileStoreException(e);
            }
        }
        if(storeDocument != null) {
            storeDocument = null;
        }

        try {
            storeDocument = YamlDocument.create(
                    storeFile,
                    GeneralSettings.DEFAULT,
                    LoaderSettings.DEFAULT,
                    DumperSettings.DEFAULT,
                    UpdaterSettings.DEFAULT
            );
            this.storeDocument.save(storeFile);
        } catch (IOException e) {
            throw new FileStoreException("Failed to create a store file for store "+storeName, e);
        }
    }

    protected YamlDocument getStoreDocument() {
        return storeDocument;
    }

}
