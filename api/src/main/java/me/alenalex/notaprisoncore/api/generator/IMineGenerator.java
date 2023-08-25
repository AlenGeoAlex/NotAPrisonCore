package me.alenalex.notaprisoncore.api.generator;

import me.alenalex.notaprisoncore.api.entity.mine.IMineMeta;
import me.alenalex.notaprisoncore.api.exceptions.NoSchematicFound;
import me.alenalex.notaprisoncore.api.managers.ISchematicFileManager;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface IMineGenerator {

    /**
     * Generates mines on the mine-world.
     * The method expects the schematicFileManager to have cached the file.
     * If not, call the {@link ISchematicFileManager#refresh()} to fetch new and latest files from the data path
     * @param requester The commandSender who requested to generate the mine
     * @param schematicName The name of the schematic
     * @return IMineMeta of the mine
     * @throws NoSchematicFound if no schematic with the said name exists
     */
    Optional<IMineMeta> generateMine(CommandSender requester, String schematicName) throws NoSchematicFound;

    /**
     * This generates a set of mines with in the provided cool down.
     * The method expects the schematicFileManager to have cached the file.
     * If not, call the {@link ISchematicFileManager#refresh()} to fetch new and latest files from the data path
     * <p>
     * NOTE: This is a sync method, just because its return type is {@link CompletableFuture} doesn't mean it's
     * an async operation.
     * The completableFuture instance would return a list of IMineMeta, Once the creation is complete
     * <p>
     * The operation will exceptionally stop if an error occurs while generation.
     * Handling the future is caller's responsibility
     * @param requester The commandSender who requested to generate the mine
     * @param schematicName The name of the schematic
     * @param generationCount No of mines to be generated
     * @param coolDownInterval Cool down interval needed to be given for each generation
     * @return IMineMeta of the mine
     */
    CompletableFuture<Collection<IMineMeta>> generateMines(CommandSender requester, String schematicName, int generationCount, long coolDownInterval);


}
