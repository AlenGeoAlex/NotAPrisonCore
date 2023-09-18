package me.alenalex.notaprisoncore.api.abstracts.store;

import me.alenalex.notaprisoncore.api.database.sql.ISQLDatabase;
import org.xerial.snappy.Snappy;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class AbstractDataStoreWithDirectory<E, I> extends AbstractDataStore<E, I> {
    private final File localDirectory;
    private final boolean compressionEnabled;
    public AbstractDataStoreWithDirectory(ISQLDatabase pluginDatabase, File localDirectory, boolean compressionEnabled) {
        super(pluginDatabase);
        this.localDirectory = localDirectory;
        this.compressionEnabled = compressionEnabled;
        if(!this.localDirectory.exists())
            this.localDirectory.mkdirs();
    }

    protected File getOrCreate(String mine) throws IOException {
        if(!this.localDirectory.exists())
            this.localDirectory.mkdirs();

        String fileName = mine +".dat";
        File metaDataFile = null;
        File[] possibleFiles = this.localDirectory.listFiles(x -> x.getName().equals(fileName));
        if(possibleFiles == null){
            metaDataFile = create(this.localDirectory, fileName);
        }else if(possibleFiles.length == 0){
            metaDataFile = create(localDirectory, fileName);
        }else{
            metaDataFile = possibleFiles[0];
        }

        return metaDataFile;
    }

    protected File create(File parentDirectory, String fileName) throws IOException {
        File file = new File(this.localDirectory, fileName);
        file.createNewFile();
        return file;
    }

    public void writeLocal(String id, String data) throws IOException {
        this.writeLocal(id, data, this.compressionEnabled);
    }
    public void writeLocal(String id, String data, boolean compressionEnabled) throws IOException {
        File file = getOrCreate(id);
        Files.write(file.toPath(), compressionEnabled ? compress(data) : data.getBytes(StandardCharsets.UTF_8), StandardOpenOption.WRITE, StandardOpenOption.SYNC);
    }

    public String readLocal(String id) throws IOException {
        return this.readLocal(id, this.compressionEnabled);
    }

    public String readLocal(String id, boolean compressionEnabled) throws IOException {
        File file = getOrCreate(id);
        if(compressionEnabled){
            byte[] byteArray = Files.readAllBytes(file.toPath());
            return uncompressed(byteArray);
        }else{
            String base64String = null;
            try (Stream<String> lines = Files.lines(file.toPath())) {
                base64String = lines.collect(Collectors.joining(System.lineSeparator()));
            }
            return base64String;
        }
    }

    public byte[] compress(String raw) throws IOException {
        return Snappy.compress(raw.getBytes(StandardCharsets.UTF_8));
    }

    public String uncompressed(byte[] compressed) throws IOException {
        return Snappy.uncompressString(compressed);
    }

}
