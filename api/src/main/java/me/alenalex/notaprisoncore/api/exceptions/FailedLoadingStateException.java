package me.alenalex.notaprisoncore.api.exceptions;

public class FailedLoadingStateException extends RuntimeException{

    public FailedLoadingStateException() {
    }

    public FailedLoadingStateException(String message) {
        super(message);
    }

    public FailedLoadingStateException(String message, Throwable cause) {
        super(message, cause);
    }

    public FailedLoadingStateException(Throwable cause) {
        super(cause);
    }

    public FailedLoadingStateException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
