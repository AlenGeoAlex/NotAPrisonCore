package me.alenalex.notaprisoncore.paper.manager.schematic;

import me.alenalex.notaprisoncore.api.managers.ISchematicFileManager;
import org.jetbrains.annotations.NotNull;

import java.io.*;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;

public class SchematicFileManager implements ISchematicFileManager {
    private final SchematicManager schematicManager;
    private final File schematicDirectory;
    private final HashMap<String, File> schematicMap;
    public SchematicFileManager(SchematicManager schematicManager) {
        this.schematicManager = schematicManager;
        this.schematicDirectory = new File(schematicManager.getPrisonManagers().getPluginInstance().getBukkitPlugin().getDataFolder(), "schematics");
        this.schematicMap = new HashMap<>();
    }

    public void initAndLoad(){
        this.directoryInit();
        File[] schematics = this.schematicDirectory.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith("schematic");
            }
        });

        if(schematics == null)
            return;

        this.schematicManager.getPrisonManagers().getPluginInstance().getLogger().info("Loading schematics");
        for (File schematic : schematics) {
            String fileName = schematic.getName().split("\\.")[0];
            if(schematicMap.containsKey(fileName))
            {
                this.schematicManager.getPrisonManagers().getPluginInstance().getLogger().warning("Duplicate schematic with name "+fileName+" already exists!");
                continue;
            }
            schematicMap.put(fileName, schematic);
            this.schematicManager.getPrisonManagers().getPluginInstance().getLogger().info("- Loaded "+fileName+" of file "+schematic.getName());
        }
    }

    private void directoryInit(){
        if(!schematicDirectory.exists())
            schematicDirectory.mkdirs();
    }

    @Override
    public @NotNull Optional<File> getSchematicFileOfName(String filename){
        if(!schematicMap.containsKey(filename))
            return Optional.empty();

        return Optional.ofNullable(schematicMap.get(filename));
    }

    @Override
    public @NotNull Optional<InputStream> getSchematicStreamOfFile(String filename) throws IOException {
        if(!schematicMap.containsKey(filename))
            return Optional.empty();

        File file = schematicMap.get(filename);
        if(file == null)
            return Optional.empty();

        return Optional.of(Files.newInputStream(file.toPath()));
    }

    @NotNull
    public Collection<File> getAllAvailableSchematicFiles(){
        return new HashSet<>(schematicMap.values());
    }
}
