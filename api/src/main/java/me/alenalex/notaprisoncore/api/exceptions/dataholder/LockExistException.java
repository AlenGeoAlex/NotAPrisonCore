package me.alenalex.notaprisoncore.api.exceptions.dataholder;

public class LockExistException extends Exception{

    public LockExistException() {
    }

    public LockExistException(String message) {
        super(message);
    }

    public LockExistException(String message, Throwable cause) {
        super(message, cause);
    }

    public LockExistException(Throwable cause) {
        super(cause);
    }

    public LockExistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
