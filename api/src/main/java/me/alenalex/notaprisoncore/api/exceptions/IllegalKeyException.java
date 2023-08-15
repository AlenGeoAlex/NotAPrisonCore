package me.alenalex.notaprisoncore.api.exceptions;

public class IllegalKeyException extends RuntimeException{

    public IllegalKeyException() {
        this(null);
    }
    public IllegalKeyException(String key) {
        super("Failed to create key [Key="+key+"]. This can either cause because the provided key is null or empty!");
    }
}
