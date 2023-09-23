package me.alenalex.notaprisoncore.message.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

public class HeartbeatMessage {

    @AllArgsConstructor
    @Getter
    @ToString
    @EqualsAndHashCode
    public static final class OnlineAnnouncementRequest {
        private boolean firstRequest;
    }

    public static final class OnlineAnnouncementResponse {

    }

}
