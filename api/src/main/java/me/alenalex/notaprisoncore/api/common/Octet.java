package me.alenalex.notaprisoncore.api.common;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
@AllArgsConstructor
public class Octet<T1, T2, T3, T4> {
    private final T1 firstItem;
    private final T2 secondItem;
    private final T3 thirdItem;
    private final T4 fourthItem;

    public boolean hasOne(){
        return firstItem != null;
    }

    public boolean hasSecond(){
        return secondItem != null;
    }

    public boolean hasThird(){
        return thirdItem != null;
    }
    public boolean hasFourth() {return fourthItem != null; }

}
