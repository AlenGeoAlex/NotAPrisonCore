package me.alenalex.notaprisoncore.api.exceptions;

import me.alenalex.notaprisoncore.api.enums.ConfigType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class FailedConfigurationException extends RuntimeException{

    public FailedConfigurationException(ConfigType configType, @NotNull String errorMessage, @Nullable Throwable throwable){
        super("Failed to load "+configType.getName()+"due to ["+errorMessage+"]. More stack trace would be printed below");
        if (throwable != null) {
            throwable.printStackTrace();
        }
    }

}
