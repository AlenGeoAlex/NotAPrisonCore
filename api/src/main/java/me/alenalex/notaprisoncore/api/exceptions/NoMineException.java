package me.alenalex.notaprisoncore.api.exceptions;

public class NoMineException extends Exception{

    public NoMineException() {
    }

    public NoMineException(String message) {
        super(message);
    }

    public NoMineException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoMineException(Throwable cause) {
        super(cause);
    }

    public NoMineException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
