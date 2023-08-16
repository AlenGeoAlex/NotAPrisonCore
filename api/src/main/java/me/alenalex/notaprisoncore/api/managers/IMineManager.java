package me.alenalex.notaprisoncore.api.managers;

import me.alenalex.notaprisoncore.api.entity.mine.MinePositionalKey;
import me.alenalex.notaprisoncore.api.exceptions.DuplicateMineIdentifier;
import me.alenalex.notaprisoncore.api.generator.IMineGenerator;

public interface IMineManager {
    /**
     * Get the instance of Mine Generator
     * @return The default mine generator instance
     */
    IMineGenerator generator();

    /**
     * Register a mine identifier on the specified schematic.
     * If no existsing configuration is present for the schematic, It would
     * create one and add
     * @param schematic The name of the schematic
     * @param key The positional key
     * @throws DuplicateMineIdentifier If the identifier material exists with the same schematic
     */
    void registerMineIdentifiers(String schematic, MinePositionalKey key) throws DuplicateMineIdentifier;

    /**
     * Registers a mine identifier on all existing schematic at the present.
     * If a new schematic type is registered, call {@link IMineManager#registerMineIdentifiers(String, MinePositionalKey)}
     * to get on the specified schematic
     * @param key The positional key
     * @throws DuplicateMineIdentifier If the identifier material exists with the same schematic*
     */
    void registerMineIdentifiersOnAllSchematics(MinePositionalKey key) throws DuplicateMineIdentifier;

}
