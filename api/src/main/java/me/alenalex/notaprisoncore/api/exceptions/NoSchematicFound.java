package me.alenalex.notaprisoncore.api.exceptions;

public class NoSchematicFound extends Exception{

    public NoSchematicFound() {
    }

    public NoSchematicFound(String message) {
        super(message);
    }

    public NoSchematicFound(String message, Throwable cause) {
        super(message, cause);
    }

    public NoSchematicFound(Throwable cause) {
        super(cause);
    }

    public NoSchematicFound(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
