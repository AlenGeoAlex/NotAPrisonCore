package me.alenalex.notaprisoncore.api.store;

public interface IPrisonDataStore {
    IWorldStore worldStore();
    IMineMetaStore mineMetaStore();
    IMineStore mineStore();

}
