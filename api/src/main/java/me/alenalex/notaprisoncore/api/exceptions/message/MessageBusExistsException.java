package me.alenalex.notaprisoncore.api.exceptions.message;

public class MessageBusExistsException extends Exception{

    public MessageBusExistsException() {
    }

    public MessageBusExistsException(String message) {
        super(message);
    }

    public MessageBusExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public MessageBusExistsException(Throwable cause) {
        super(cause);
    }

    public MessageBusExistsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
