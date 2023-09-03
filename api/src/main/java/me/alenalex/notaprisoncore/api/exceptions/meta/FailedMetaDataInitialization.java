package me.alenalex.notaprisoncore.api.exceptions.meta;

public class FailedMetaDataInitialization extends Exception{

    public FailedMetaDataInitialization() {
    }

    public FailedMetaDataInitialization(String message) {
        super(message);
    }

    public FailedMetaDataInitialization(String message, Throwable cause) {
        super(message, cause);
    }

    public FailedMetaDataInitialization(Throwable cause) {
        super(cause);
    }

    public FailedMetaDataInitialization(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
