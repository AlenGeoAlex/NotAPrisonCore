package me.alenalex.notaprisoncore.api.exceptions;

public class MineResetException extends RuntimeException{

    public MineResetException() {
    }

    public MineResetException(String message) {
        super(message);
    }

    public MineResetException(String message, Throwable cause) {
        super(message, cause);
    }

    public MineResetException(Throwable cause) {
        super(cause);
    }

    public MineResetException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
