package me.alenalex.notaprisoncore.api.debug;

public interface IDebugLogger {
    void debug(String message);
    void debug(String message, Throwable error);
    void debug(String message, Throwable error, Class<?> debugInstance);
    void debug(String message, Class<?> debugInstance);
    default void debug(Object message){
        debug(message.toString());
    }
    default void debug(Object message, Throwable error){
        debug(message.toString(), error);
    }
    default void debug(Object message, Throwable error, Class<?> debugInstance){
        debug(message.toString(), error, debugInstance);
    }
    default void debug(Object message, Class<?> debugInstance){
        debug(message.toString(), debugInstance);
    }
    void debug(Throwable error);
    void debug(Throwable error, Class<?> debugInstance);
}
