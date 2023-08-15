package me.alenalex.notaprisoncore.api.locale;

import com.google.common.base.CaseFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.exceptions.IllegalKeyException;
import org.jetbrains.annotations.NotNull;

@Getter
@ToString
@EqualsAndHashCode
public final class LocaleKey {
    @NotNull
    public static LocaleKey of(String messageKey){
        if(messageKey == null || messageKey.length() == 0)
            throw new IllegalKeyException();
        String lowerCase = messageKey.toLowerCase();
        return new LocaleKey(lowerCase);
    }

    private final String messageKey;
    private LocaleKey(String messageKey) {
        this.messageKey = messageKey;
    }
}
