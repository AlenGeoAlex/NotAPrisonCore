package me.alenalex.notaprisoncore.api.exceptions.database.redis;

public class RedisDatabaseNotAvailableException extends RuntimeException{

    public RedisDatabaseNotAvailableException() {
    }

    public RedisDatabaseNotAvailableException(String message) {
        super(message);
    }

    public RedisDatabaseNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public RedisDatabaseNotAvailableException(Throwable cause) {
        super(cause);
    }

    public RedisDatabaseNotAvailableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
