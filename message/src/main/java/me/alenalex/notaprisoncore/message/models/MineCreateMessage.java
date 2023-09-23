package me.alenalex.notaprisoncore.message.models;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@EqualsAndHashCode
@ToString
@Getter
@AllArgsConstructor
public class MineCreateMessage {

    private final UUID mineId;
    private final UUID ownerId;
    private final UUID metaId;


}
