package me.alenalex.notaprisoncore.api.exceptions;

public class DuplicateMineIdentifier extends Exception{

    public DuplicateMineIdentifier() {
    }

    public DuplicateMineIdentifier(String message) {
        super(message);
    }

    public DuplicateMineIdentifier(String message, Throwable cause) {
        super(message, cause);
    }

    public DuplicateMineIdentifier(Throwable cause) {
        super(cause);
    }

    public DuplicateMineIdentifier(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
