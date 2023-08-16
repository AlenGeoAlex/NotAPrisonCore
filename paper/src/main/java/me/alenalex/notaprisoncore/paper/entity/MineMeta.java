package me.alenalex.notaprisoncore.paper.entity;

import me.alenalex.notaprisoncore.api.entity.mine.IMineMeta;
import org.bukkit.Location;

public class MineMeta implements IMineMeta {
    @Override
    public boolean isInsideMine(Location location) {
        return false;
    }

    @Override
    public boolean isInsideMiningRegion(Location location) {
        return false;
    }
}
