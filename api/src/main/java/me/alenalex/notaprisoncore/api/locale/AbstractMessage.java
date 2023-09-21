package me.alenalex.notaprisoncore.api.locale;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import me.alenalex.notaprisoncore.api.locale.placeholder.MessagePlaceholder;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

@ToString
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
    protected WrappedMessageType parse(@Nullable Player player, MessagePlaceholder... placeholders){
        WrappedMessageType clone = wrappedMessageType.clone();
        if(placeholders.length == 0){
            return clone;
        }

        for (int eachMessageIndex = 0; eachMessageIndex < clone.message.length; eachMessageIndex++) {
            clone.message[eachMessageIndex] = parsePlaceholder(clone.message[eachMessageIndex], player, placeholders);
        }

        for (int eachTitleIndex = 0; eachTitleIndex < clone.title.length; eachTitleIndex++) {
            clone.title[eachTitleIndex] = parsePlaceholder(clone.title[eachTitleIndex], player, placeholders);
        }

        clone.actionBar = parsePlaceholder(clone.actionBar, player, placeholders);
        return clone;
    }


    @NotNull
    protected WrappedMessageType parse(@Nullable Collection<MessagePlaceholder> placeholders, @Nullable Player player){
        WrappedMessageType clone = wrappedMessageType.clone();
        if((placeholders == null || placeholders.isEmpty()) & !clone.parsePlaceholderApi){
            return clone;
        }

        for (int eachMessageIndex = 0; eachMessageIndex < clone.message.length; eachMessageIndex++) {
            clone.message[eachMessageIndex] = parsePlaceholder(clone.message[eachMessageIndex], player, placeholders);
        }

        for (int eachTitleIndex = 0; eachTitleIndex < clone.title.length; eachTitleIndex++) {
            clone.title[eachTitleIndex] = parsePlaceholder(clone.title[eachTitleIndex], player, placeholders);
        }

        clone.actionBar = parsePlaceholder(clone.actionBar, player, placeholders);
        return clone;
    }

    @NotNull
    protected String[] parseTitle(@Nullable Collection<MessagePlaceholder> placeholders, @Nullable Player player){
        String[] title = wrappedMessageType.title.clone();
        for (int eachTitleIndex = 0; eachTitleIndex < title.length; eachTitleIndex++) {
            title[eachTitleIndex] = parsePlaceholder(title[eachTitleIndex], player, placeholders);
        }
        return title;
    }

    @Nullable
    protected String parseActionBar(@Nullable Collection<MessagePlaceholder> placeholders, @Nullable Player player){
        String actionBar = wrappedMessageType.actionBar;
        if(actionBar == null)
            return null;

        return parsePlaceholder(actionBar, player , placeholders);
    }


    private String parsePlaceholder(String message, @Nullable Player player, MessagePlaceholder... placeholders){
        if(message == null || message.isEmpty())
            return null;

        if(this.wrappedMessageType.parsePlaceholderApi && player != null){
            message = PlaceholderAPI.setPlaceholders(player, message);
        }

        for (MessagePlaceholder placeholder : placeholders) {
            message = placeholder.replace(message);
        }
        return message;
    }

    private String parsePlaceholder(String message,@Nullable Player player, Collection<MessagePlaceholder> placeholders){
        if(message == null || message.isEmpty())
            return null;

        if(this.wrappedMessageType.parsePlaceholderApi && player != null){
            message = PlaceholderAPI.setPlaceholders(player, message);
        }

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
        public static final String TITLE_SETTINGS_KEY = "[TITLE-SETTING]";
        @NotNull
        public static WrappedMessageType process(List<String> message){
            if(message == null || message.isEmpty())
                return WrappedMessageType.EMPTY;

            List<String> messages = new ArrayList<>();
            List<String> title = new ArrayList<>(2);
            String sound = null;
            String actionBar  = null;
            boolean parsePlaceholderApi = false;
            int fadeIn = 40;
            int fadeOut = 40;
            int stay = 80;
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
                }else if(eachLine.startsWith(TITLE_SETTINGS_KEY)){
                    String settingRaw = eachLine.substring(TITLE_SETTINGS_KEY.length()).trim();
                    String[] split = settingRaw.split(";");
                    if(split.length != 3)
                        continue;

                    fadeIn = Integer.parseInt(split[0]);
                    fadeOut = Integer.parseInt(split[1]);
                    stay = Integer.parseInt(split[2]);
                }else{
                    if(eachLine.isEmpty())
                        continue;

                    messages.add(eachLine);
                }
            }

            return new WrappedMessageType(messages.toArray(new String[0]), title.toArray(new String[0]), actionBar, sound, parsePlaceholderApi, fadeIn, fadeOut, stay);
        }

    }

    @EqualsAndHashCode
    @Getter
    @ToString
    @AllArgsConstructor
    public static class WrappedMessageType implements Cloneable{

        public static final WrappedMessageType EMPTY = new WrappedMessageType(new String[0], new String[0], null, null, false, 30, 40, 80);

        private String[] message = null;
        private String[] title = null;
        private String actionBar = null;
        private String sound = null;
        private boolean parsePlaceholderApi = false;
        private int fadeIn = 30;
        private int fadeOut = 40;
        private int stay = 80;
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
