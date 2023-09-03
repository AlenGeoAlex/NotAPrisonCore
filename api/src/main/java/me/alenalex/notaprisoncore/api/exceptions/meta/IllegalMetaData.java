package me.alenalex.notaprisoncore.api.exceptions.meta;

public class IllegalMetaData extends RuntimeException{

    public IllegalMetaData() {
    }

    public IllegalMetaData(String message) {
        super(message);
    }

    public IllegalMetaData(String message, Throwable cause) {
        super(message, cause);
    }

    public IllegalMetaData(Throwable cause) {
        super(cause);
    }

    public IllegalMetaData(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
