package me.alenalex.notaprisoncore.api.locale.placeholder;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@EqualsAndHashCode
@ToString
public final class MessagePlaceholder {

    private final String key;
    private final String value;
    public String replace(String message){
        return message.replace("{"+key+"}", value);
    }
}
