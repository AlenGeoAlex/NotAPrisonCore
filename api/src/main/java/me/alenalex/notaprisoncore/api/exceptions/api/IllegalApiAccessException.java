package me.alenalex.notaprisoncore.api.exceptions.api;

import java.lang.reflect.Method;

public class IllegalApiAccessException extends RuntimeException{

    public IllegalApiAccessException(String caller) {
        super("The API for the core has not been yet initialized! An unknown call has been originated from "+caller+" method.");
    }

    public IllegalApiAccessException(Class<?> caller, Method method) {
        super("The API for the core has not been yet initialized! An unknown call has been originated from ["+caller.getName()+"].["+method.getName()+"] method.");
    }

    public IllegalApiAccessException(Class<?> caller, String method) {
        super("The API for the core has not been yet initialized! An unknown call has been originated from ["+caller.getName()+"].["+method+"] method.");
    }

    public IllegalApiAccessException(Class<?> caller) {
        super("The API for the core has not been yet initialized! An unknown call has been originated from ["+caller.getName()+"] class.");
    }
}
