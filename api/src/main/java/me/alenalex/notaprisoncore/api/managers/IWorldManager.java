package me.alenalex.notaprisoncore.api.managers;

import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IWorldManager {

    @Nullable World getMineWorld(boolean createIfAbsent);

    @NotNull World getMineWorld();

    boolean loadChunkAt(Location location);

    boolean loadChunkAt(Location center, int radius);
}
