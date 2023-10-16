package me.alenalex.notaprisoncore.api.exceptions.api;

public class IllegalInitializationException extends RuntimeException{

    public IllegalInitializationException() {
        super("The api for the prison core is already initialized by its own implementation. Please don't call new CoreApi");
    }
}
