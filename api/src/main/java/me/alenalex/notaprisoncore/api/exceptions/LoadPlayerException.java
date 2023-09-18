package me.alenalex.notaprisoncore.api.exceptions;

public class LoadPlayerException extends RuntimeException{

    public LoadPlayerException() {
    }

    public LoadPlayerException(String message) {
        super(message);
    }

    public LoadPlayerException(String message, Throwable cause) {
        super(message, cause);
    }

    public LoadPlayerException(Throwable cause) {
        super(cause);
    }

    public LoadPlayerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
