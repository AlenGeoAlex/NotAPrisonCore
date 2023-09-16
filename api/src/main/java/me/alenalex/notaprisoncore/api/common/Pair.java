package me.alenalex.notaprisoncore.api.common;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
public final class Pair<T1, T2> {

    private final T1 firstItem;
    private final T2 secondItem;

}
