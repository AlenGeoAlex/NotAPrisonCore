package me.alenalex.notaprisoncore.api.locale;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.locale.placeholder.MessagePlaceholder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractMessage implements IPluginMessage<Player>{

    private final AbstractMessage.WrappedMessageType wrappedMessageType;

    public AbstractMessage(List<String> message){
        this.wrappedMessageType = MessageTypeProcessor.process(message);
        for (int eachMessageIndex = 0; eachMessageIndex < this.wrappedMessageType.message.length; eachMessageIndex++) {
            this.wrappedMessageType.message[eachMessageIndex] = colorize(this.wrappedMessageType.message[eachMessageIndex]);
        }
        for (int eachTitleIndex = 0; eachTitleIndex < this.wrappedMessageType.title.length; eachTitleIndex++) {
            this.wrappedMessageType.title[eachTitleIndex] = colorize(this.wrappedMessageType.title[eachTitleIndex]);
        }
        this.wrappedMessageType.actionBar = colorize(this.wrappedMessageType.actionBar);
    }
    protected abstract String colorize(String message);
    @NotNull
    protected WrappedMessageType parse(MessagePlaceholder... placeholders){
        WrappedMessageType clone = wrappedMessageType.clone();
        if(placeholders.length == 0){
            return clone;
        }

        for (int eachMessageIndex = 0; eachMessageIndex < clone.message.length; eachMessageIndex++) {
            clone.message[eachMessageIndex] = parsePlaceholder(clone.message[eachMessageIndex], placeholders);
        }

        for (int eachTitleIndex = 0; eachTitleIndex < clone.title.length; eachTitleIndex++) {
            clone.message[eachTitleIndex] = parsePlaceholder(clone.title[eachTitleIndex], placeholders);
        }

        clone.actionBar = parsePlaceholder(clone.actionBar, placeholders);
        return clone;
    }

    @NotNull
    protected WrappedMessageType parse(@Nullable Collection<MessagePlaceholder> placeholders){
        WrappedMessageType clone = wrappedMessageType.clone();
        if(placeholders == null || placeholders.isEmpty()){
            return clone;
        }

        for (int eachMessageIndex = 0; eachMessageIndex < clone.message.length; eachMessageIndex++) {
            clone.message[eachMessageIndex] = parsePlaceholder(clone.message[eachMessageIndex], placeholders);
        }

        for (int eachTitleIndex = 0; eachTitleIndex < clone.title.length; eachTitleIndex++) {
            clone.message[eachTitleIndex] = parsePlaceholder(clone.title[eachTitleIndex], placeholders);
        }

        clone.actionBar = parsePlaceholder(clone.actionBar, placeholders);
        return clone;
    }

    @NotNull
    protected String[] parseTitle(@Nullable Collection<MessagePlaceholder> placeholders){
        String[] title = wrappedMessageType.title.clone();
        for (int eachTitleIndex = 0; eachTitleIndex < title.length; eachTitleIndex++) {
            title[eachTitleIndex] = parsePlaceholder(title[eachTitleIndex], placeholders);
        }
        return title;
    }

    @Nullable
    protected String parseActionBar(@Nullable Collection<MessagePlaceholder> placeholders){
        String actionBar = wrappedMessageType.actionBar;
        if(actionBar == null)
            return null;

        return parsePlaceholder(actionBar, placeholders);
    }


    private String parsePlaceholder(String message, MessagePlaceholder... placeholders){
        if(message == null || message.isEmpty())
            return null;

        for (MessagePlaceholder placeholder : placeholders) {
            message = placeholder.replace(message);
        }
        return message;
    }

    private String parsePlaceholder(String message, Collection<MessagePlaceholder> placeholders){
        if(message == null || message.isEmpty())
            return null;

        for (MessagePlaceholder placeholder : placeholders) {
            message = placeholder.replace(message);
        }
        return message;
    }

    protected WrappedMessageType getWrappedMessageType() {
        return wrappedMessageType;
    }

    @Override
    public boolean isSingleLine() {
        if(wrappedMessageType == null || wrappedMessageType.message == null)
            return false;

        return wrappedMessageType.message.length == 1;
    }

    private static class MessageTypeProcessor {

        public static final String TITLE_HEAD_KEY = "[TITLE-HEAD]";
        public static final String TITLE_SUB_KEY = "[TITLE-SUB]";
        public static final String SOUND_KEY = "[SOUND]";
        public static final String ACTION_BAR_KEY = "[ACTION-BAR]";
        public static final String PLACEHOLDER_API_FLAG = "[PLACEHOLDER-API]";
        @NotNull
        public static WrappedMessageType process(List<String> message){
            if(message == null || message.isEmpty())
                return WrappedMessageType.EMPTY;

            List<String> messages = new ArrayList<>();
            List<String> title = new ArrayList<>(2);
            String sound = null;
            String actionBar  = null;
            boolean parsePlaceholderApi = false;
            for (String eachLine : message) {
                if(eachLine.startsWith(TITLE_HEAD_KEY)){
                    String titleHead = eachLine.substring(TITLE_HEAD_KEY.length()).trim();
                    title.add(0, titleHead);
                }else if(eachLine.startsWith(TITLE_SUB_KEY)){
                    String titleHead = eachLine.substring(TITLE_SUB_KEY.length()).trim();
                    title.add(1, titleHead);
                }else if(eachLine.startsWith(SOUND_KEY)){
                    sound = eachLine.substring(SOUND_KEY.length()).trim();
                }else if(eachLine.startsWith(ACTION_BAR_KEY)) {
                    actionBar = eachLine.substring(ACTION_BAR_KEY.length()).trim();
                }else if(eachLine.equals(PLACEHOLDER_API_FLAG)){
                    parsePlaceholderApi = true;
                }else{
                    if(eachLine.isEmpty())
                        continue;

                    message.add(eachLine);
                }
            }

            return new WrappedMessageType(message.toArray(new String[0]), title.toArray(new String[0]), actionBar, sound, parsePlaceholderApi);
        }

    }

    @EqualsAndHashCode
    @Getter
    @ToString
    @AllArgsConstructor
    public static class WrappedMessageType implements Cloneable{

        public static final WrappedMessageType EMPTY = new WrappedMessageType(new String[0], new String[0], null, null, false);

        private String[] message = null;
        private String[] title = null;
        private String actionBar = null;
        private String sound = null;
        private boolean parsePlaceholderApi = false;
        public boolean hasActionBar(){
            return actionBar != null && !actionBar.isEmpty();
        }

        public boolean hasSound(){
            return sound != null && !sound.isEmpty();
        }

        @Override
        public WrappedMessageType clone() {
            try {
                WrappedMessageType clone = (WrappedMessageType) super.clone();
                return clone;
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }
}
