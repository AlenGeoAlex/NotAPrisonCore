package me.alenalex.notaprisoncore.paper.entity.mine;

import com.sk89q.worldedit.regions.Region;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.entity.mine.IMineMeta;
import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@EqualsAndHashCode
@ToString
public class MineMeta implements IMineMeta {
    private UUID metaId;
    private Region mineSchematicRegion;
    private Location lowerMiningPoint;
    private Location upperMiningPoint;
    private final Location spawnPoint;
    private final HashMap<String, Location> locationIdentifier;

    public MineMeta(UUID uuid, Region mineSchematicRegion, Location lowerMiningPoint, Location upperMiningPoint, Location spawnPoint, HashMap<String, Location> locationIdentifier) {
        this.metaId = uuid;
        this.mineSchematicRegion = mineSchematicRegion;
        this.lowerMiningPoint = lowerMiningPoint;
        this.upperMiningPoint = upperMiningPoint;
        this.spawnPoint = spawnPoint;
        this.locationIdentifier = locationIdentifier;
    }

    public MineMeta(UUID uuid, Region mineSchematicRegion, Location lowerMiningPoint, Location upperMiningPoint, Location spawnPoint) {
        this.metaId = uuid;
        this.mineSchematicRegion = mineSchematicRegion;
        this.lowerMiningPoint = lowerMiningPoint;
        this.upperMiningPoint = upperMiningPoint;
        this.spawnPoint = spawnPoint;
        this.locationIdentifier = new HashMap<>();
    }

    public MineMeta(Region mineSchematicRegion, Location lowerMiningPoint, Location upperMiningPoint, Location spawnPoint, HashMap<String, Location> locationIdentifier) {
        this.mineSchematicRegion = mineSchematicRegion;
        this.lowerMiningPoint = lowerMiningPoint;
        this.upperMiningPoint = upperMiningPoint;
        this.spawnPoint = spawnPoint;
        this.locationIdentifier = locationIdentifier;
    }

    public MineMeta(Region mineSchematicRegion, Location lowerMiningPoint, Location upperMiningPoint, Location spawnPoint) {
        this.mineSchematicRegion = mineSchematicRegion;
        this.lowerMiningPoint = lowerMiningPoint;
        this.upperMiningPoint = upperMiningPoint;
        this.spawnPoint = spawnPoint;
        this.locationIdentifier = new HashMap<>();
    }


    @Override
    public UUID getMetaId() {
        return metaId;
    }

    public void setMetaId(UUID uuid) throws IllegalAccessException {
        if(this.metaId == null)
            this.metaId = uuid;
        else throw new IllegalAccessException("Meta ID for the mine is already set");
    }

    @Override
    public boolean isInsideMine(Location location) {
        return mineSchematicRegion.contains(location.getBlockX(), location.getBlockZ());
    }

    @Override
    public boolean isInsideMiningRegion(Location location) {
        return (location.getX() < getLowerMiningPoint().getX() && location.getX() > getLowerMiningPoint().getX() &&
                location.getZ() < getUpperMiningPoint().getZ() && location.getZ() > getUpperMiningPoint().getZ());
    }

    @Override
    public Location getLowerMiningPoint() {
        return lowerMiningPoint;
    }
    @Override
    public Location getUpperMiningPoint() {
        return upperMiningPoint;
    }
    @Override
    public Location getSpawnPoint() {
        return spawnPoint;
    }

    @Override
    public Vector getMineSchematicUpperPoint() {
        com.sk89q.worldedit.Vector maximumPoint = this.mineSchematicRegion.getMaximumPoint();
        return new Vector(maximumPoint.getX(), maximumPoint.getY(), maximumPoint.getZ());
    }

    @Override
    public Vector getMineSchematicLowerPoint() {
        com.sk89q.worldedit.Vector minimumPoint = this.mineSchematicRegion.getMinimumPoint();
        return new Vector(minimumPoint.getX(), minimumPoint.getY(), minimumPoint.getZ());
    }

    @Override
    public boolean hasIdentifier(String key) {
        return this.locationIdentifier.containsKey(key);
    }

    @Override
    public Optional<Location> getLocationOfIdentifier(String key) {
        return Optional.ofNullable(locationIdentifier.get(key));
    }

    public HashMap<String, Location> getLocationIdentifier() {
        return locationIdentifier;
    }
}