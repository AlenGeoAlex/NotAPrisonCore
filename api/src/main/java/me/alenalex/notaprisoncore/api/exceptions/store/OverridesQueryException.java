package me.alenalex.notaprisoncore.api.exceptions.store;

public class OverridesQueryException extends RuntimeException{

    public OverridesQueryException(String tableName, String queryType) {
        super("Usage of "+queryType+" for modifying "+tableName+" is overridden by the interface method. Check the interface documentation.");
    }
}
