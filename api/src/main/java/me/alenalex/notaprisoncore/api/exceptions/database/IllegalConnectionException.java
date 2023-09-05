package me.alenalex.notaprisoncore.api.exceptions.database;

public class IllegalConnectionException extends RuntimeException{

    public IllegalConnectionException() {
    }

    public IllegalConnectionException(String message) {
        super(message);
    }

    public IllegalConnectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalConnectionException(Throwable cause) {
        super(cause);
    }

    public IllegalConnectionException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
