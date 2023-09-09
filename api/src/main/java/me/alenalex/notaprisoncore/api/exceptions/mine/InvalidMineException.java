package me.alenalex.notaprisoncore.api.exceptions.mine;

public class InvalidMineException extends RuntimeException{

    public InvalidMineException() {
    }

    public InvalidMineException(String message) {
        super(message);
    }

    public InvalidMineException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidMineException(Throwable cause) {
        super(cause);
    }

    public InvalidMineException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
