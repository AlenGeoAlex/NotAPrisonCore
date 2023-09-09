package me.alenalex.notaprisoncore.api.events;

import org.bukkit.event.Event;


public abstract class NotAPrisonCoreEvents extends Event {

    public NotAPrisonCoreEvents() {
    }

    public NotAPrisonCoreEvents(boolean isAsync) {
        super(isAsync);
    }
}
