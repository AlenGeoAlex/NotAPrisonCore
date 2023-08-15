package me.alenalex.notaprisoncore.paper.manager.schematic;

import com.boydti.fawe.object.schematic.Schematic;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import me.alenalex.notaprisoncore.api.provider.ISchematicProvider;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.util.Optional;

public class ClipboardProvider implements ISchematicProvider<Schematic> {

    private final SchematicManager schematicManager;

    public ClipboardProvider(SchematicManager schematicManager) {
        this.schematicManager = schematicManager;
    }

    @Override
    public @NotNull Optional<Schematic> getSchematic(File file) {
        ClipboardFormat schematicFormat = ClipboardFormat.SCHEMATIC;
        Schematic schematic = null;
        try(FileInputStream inputStream = new FileInputStream(file)){
            schematic = schematicFormat.load(inputStream);
        }catch (Exception e){
            e.printStackTrace();
        }
        return Optional.ofNullable(schematic);
    }

    @Override
    public @NotNull Class<Schematic> getSchematicType() {
        return Schematic.class;
    }

    @Override
    public @NotNull String providerPluginName() {
        return "FastAsyncWorldEdit [FAWE]";
    }
}
