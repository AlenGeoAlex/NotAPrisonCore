package me.alenalex.notaprisoncore.api.debug;

public interface IDebugger extends IDebugLogger{

    void dumpSystemInformation();
    void dumpSqlInformation();
    void dumpRedisInformation();

}
