package me.alenalex.notaprisoncore.api.exceptions.database.redis;

public class IllegalRedisDataException extends RuntimeException{

    public IllegalRedisDataException() {
    }

    public IllegalRedisDataException(String message) {
        super(message);
    }

    public IllegalRedisDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalRedisDataException(Throwable cause) {
        super(cause);
    }

    public IllegalRedisDataException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
