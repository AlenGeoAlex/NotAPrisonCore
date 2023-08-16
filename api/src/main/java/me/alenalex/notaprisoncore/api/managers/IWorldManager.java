package me.alenalex.notaprisoncore.api.managers;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IWorldManager {

    @Nullable World getMineWorld(boolean createIfAbsent);

    @NotNull World getMineWorld();

}
