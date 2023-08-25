package me.alenalex.notaprisoncore.api.exceptions;

public class FailedMineGenerationException extends RuntimeException{

    public FailedMineGenerationException() {
    }

    public FailedMineGenerationException(String message) {
        super(message);
    }

    public FailedMineGenerationException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailedMineGenerationException(Throwable cause) {
        super(cause);
    }

    public FailedMineGenerationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
