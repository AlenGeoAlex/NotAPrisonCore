package me.alenalex.notaprisoncore.api.exceptions.store;

public class DatastoreException extends RuntimeException{

    public DatastoreException() {
    }

    public DatastoreException(String message) {
        super(message);
    }

    public DatastoreException(String message, Throwable cause) {
        super(message, cause);
    }

    public DatastoreException(Throwable cause) {
        super(cause);
    }

    public DatastoreException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
