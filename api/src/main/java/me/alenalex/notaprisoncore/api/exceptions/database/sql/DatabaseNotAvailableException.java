package me.alenalex.notaprisoncore.api.exceptions.database.sql;

public class DatabaseNotAvailableException extends RuntimeException{

    public DatabaseNotAvailableException() {
        super("Database is not available, Either the db server disconnected or the plugin is failing to keep track of connections. Please restart the server");
    }
}
