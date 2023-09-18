package me.alenalex.notaprisoncore.api.abstracts.debug;

import com.google.common.base.Throwables;
import lombok.*;
import me.alenalex.notaprisoncore.api.common.ConsoleColors;
import me.alenalex.notaprisoncore.api.debug.IDebugLogger;
import org.bukkit.ChatColor;
import org.jetbrains.annotations.Nullable;

import java.time.Instant;

@AllArgsConstructor
@EqualsAndHashCode
@ToString
public abstract class AbstractDebugger implements IDebugLogger {

    protected static final String BUKKIT_PREFIX_ERROR = ChatColor.WHITE+"["+ChatColor.YELLOW+"DEBUG"+ChatColor.WHITE+"] ["+ChatColor.GRAY+"{0}"+ChatColor.WHITE+"] ["+ChatColor.RED+"ERROR - {1}"+ChatColor.WHITE+"] "+ChatColor.RESET;
    protected static final String BUKKIT_PREFIX_INFO = ChatColor.WHITE+"["+ChatColor.YELLOW+"DEBUG"+ChatColor.WHITE+"] ["+ChatColor.GRAY+"{0}"+ChatColor.WHITE+"] ["+ChatColor.BLUE+"INFO - {1}"+ChatColor.WHITE+"] "+ChatColor.RESET;
    protected static final String CONSOLE_PREFIX_ERROR = ConsoleColors.WHITE.format("[")+ConsoleColors.YELLOW.format("DEBUG")+ConsoleColors.WHITE.format("] [")+ConsoleColors.GRAY.format("{0}")+ConsoleColors.WHITE.format("] [")+ConsoleColors.RED.format("ERROR - {1}")+ConsoleColors.WHITE.format("] ");
    protected static final String CONSOLE_PREFIX_INFO = ConsoleColors.WHITE.format("[")+ConsoleColors.YELLOW.format("DEBUG")+ConsoleColors.WHITE.format("] [")+ConsoleColors.GRAY.format("{0}")+ConsoleColors.WHITE.format("] [")+ConsoleColors.CYAN.format("INFO - {1}")+ConsoleColors.WHITE.format("] ");
    protected static final String LOG_ERROR = "[DEBUG] [{0}] [ERROR - {1}] ";
    protected static final String LOG_INFO = "[DEBUG] [{0}] [INFO - {1}] ";

    protected abstract void write(String message);
    protected abstract int logType();

    @Override
    public void debug(String message) {
        String format = format(logType(), message, null, null);
        if(format == null)
            return;

        write(format);
    }

    @Override
    public void debug(String message, Throwable error) {
        String format = format(logType(), message, error, null);
        if(format == null)
            return;

        write(format);
    }

    @Override
    public void debug(String message, Throwable error, @Nullable Class<?> debugInstance) {
        String format = format(logType(), message, error, debugInstance);
        if(format == null)
            return;

        write(format);
    }

    @Override
    public void debug(String message, @Nullable Class<?> debugInstance) {
        String format = format(logType(), message, null, debugInstance);
        if(format == null)
            return;

        write(format);
    }

    public String format(int type, String message, Throwable error, @Nullable Class<?> debugInstance){
        if(message == null && error == null)
            return null;

        String className = debugInstance == null ? "Unknown" : debugInstance.getName();
        boolean hasError = error != null;
        if(type == 1){
            return String.format(hasError ? BUKKIT_PREFIX_ERROR : BUKKIT_PREFIX_INFO, Instant.now().toString(), className) + (message)+" "+(hasError ? " Stacktrace :: "+ Throwables.getStackTraceAsString(error) : "");
        } else if (type == 2) {
            return String.format(hasError ? CONSOLE_PREFIX_ERROR : CONSOLE_PREFIX_INFO, Instant.now().toString(), className) + (message)+" "+(hasError ? " Stacktrace :: "+ Throwables.getStackTraceAsString(error) : "");
        }else{
            return String.format(hasError ? LOG_ERROR : LOG_INFO, Instant.now().toString(), className) + (message)+" "+(hasError ? " Stacktrace :: "+ Throwables.getStackTraceAsString(error) : "");
        }
    }
}
