package me.alenalex.notaprisoncore.api.exceptions.database;

public class FailedDatabaseException extends RuntimeException{

    public FailedDatabaseException() {
    }

    public FailedDatabaseException(String message) {
        super(message);
    }

    public FailedDatabaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailedDatabaseException(Throwable cause) {
        super(cause);
    }

    public FailedDatabaseException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
