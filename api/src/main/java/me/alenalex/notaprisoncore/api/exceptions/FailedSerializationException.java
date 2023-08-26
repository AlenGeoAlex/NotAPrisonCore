package me.alenalex.notaprisoncore.api.exceptions;

import org.jetbrains.annotations.Nullable;

public class FailedSerializationException extends RuntimeException {

    public FailedSerializationException(String type, String value, @Nullable Throwable throwable) {
        super("Failed to serialize data of type "+type+" with value "+value);
        if (throwable != null) {
            throwable.printStackTrace();
        }
    }
}
