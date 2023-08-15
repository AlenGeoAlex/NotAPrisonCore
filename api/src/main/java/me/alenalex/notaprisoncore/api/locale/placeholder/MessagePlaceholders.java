package me.alenalex.notaprisoncore.api.locale.placeholder;

import java.util.Arrays;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MessagePlaceholders {

    private MessagePlaceholders(){
    }

    public MessagePlaceholder of(String key, String value){
        return new MessagePlaceholder(key, value);
    }

    public MessagePlaceholder of(String key, double value){
        return new MessagePlaceholder(key, String.valueOf(value));
    }

    public MessagePlaceholder of(String key, int value){
        return new MessagePlaceholder(key, String.valueOf(value));
    }

    public MessagePlaceholder of(String key, float value){
        return new MessagePlaceholder(key, String.valueOf(value));
    }

    public MessagePlaceholder of(String key, boolean value){
        return new MessagePlaceholder(key, String.valueOf(value));
    }

    public MessagePlaceholder of(String key, Object value){
        return new MessagePlaceholder(key, value.toString());
    }

    public Collection<MessagePlaceholder> of(MessagePlaceholder... placeholders){
        return Arrays.stream(placeholders).collect(Collectors.toList());
    }
}
