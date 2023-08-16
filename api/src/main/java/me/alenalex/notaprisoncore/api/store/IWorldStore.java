package me.alenalex.notaprisoncore.api.store;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public interface IWorldStore {

    @NotNull
    Location nextFreeLocation();

}
