package me.alenalex.notaprisoncore.api.exceptions.store;

public class DatastoreInitializationException extends DatastoreException{

    public DatastoreInitializationException() {
    }

    public DatastoreInitializationException(String message) {
        super(message);
    }

    public DatastoreInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatastoreInitializationException(Throwable cause) {
        super(cause);
    }

    public DatastoreInitializationException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
