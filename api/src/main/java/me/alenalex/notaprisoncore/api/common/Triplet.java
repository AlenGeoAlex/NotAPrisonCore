package me.alenalex.notaprisoncore.api.common;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class Triplet<T1, T2, T3> {
    private final T1 firstItem;
    private final T2 secondItem;
    private final T3 thirdItem;

}
