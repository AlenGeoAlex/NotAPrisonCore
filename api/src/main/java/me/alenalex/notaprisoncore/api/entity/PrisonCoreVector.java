package me.alenalex.notaprisoncore.api.entity;

import com.sk89q.worldedit.Vector;

public class PrisonCoreVector {

    private final double x;
    private final double y;
    private final double z;

    public PrisonCoreVector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public PrisonCoreVector(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public PrisonCoreVector(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public PrisonCoreVector(PrisonCoreVector other){
        this.z = other.z;
        this.y = other.y;
        this.x = other.x;
    }

    public PrisonCoreVector(Vector otherWeVector){
        this.x = otherWeVector.getX();
        this.y = otherWeVector.getY();
        this.z = otherWeVector.getZ();
    }

    public PrisonCoreVector(org.bukkit.util.Vector otherBukkitVector){
        this.x = otherBukkitVector.getX();
        this.y = otherBukkitVector.getY();
        this.z = otherBukkitVector.getZ();
    }

    public Vector toWorldEditVector(){
        return new Vector(x, y, z);
    }

    public org.bukkit.util.Vector toBukkitVector(){
        return new org.bukkit.util.Vector(x, y, z);
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }
}
