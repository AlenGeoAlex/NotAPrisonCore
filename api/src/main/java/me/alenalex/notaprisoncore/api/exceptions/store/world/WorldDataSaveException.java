package me.alenalex.notaprisoncore.api.exceptions.store.world;

public class WorldDataSaveException extends RuntimeException{

    public WorldDataSaveException() {
    }

    public WorldDataSaveException(String message) {
        super(message);
    }

    public WorldDataSaveException(String message, Throwable cause) {
        super(message, cause);
    }

    public WorldDataSaveException(Throwable cause) {
        super(cause);
    }

    public WorldDataSaveException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
