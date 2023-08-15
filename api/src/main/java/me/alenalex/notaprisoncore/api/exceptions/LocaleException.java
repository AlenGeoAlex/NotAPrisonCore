package me.alenalex.notaprisoncore.api.exceptions;

public class LocaleException extends RuntimeException{

    public LocaleException() {
    }

    public LocaleException(String message) {
        super(message);
    }

    public LocaleException(String message, Throwable cause) {
        super(message, cause);
    }

    public LocaleException(Throwable cause) {
        super(cause);
    }

    public LocaleException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
