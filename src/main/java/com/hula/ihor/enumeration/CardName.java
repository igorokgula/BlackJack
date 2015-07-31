package com.hula.ihor.enumeration;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Created by Igor on 30.07.2015.
 */
public enum CardName {
    ACE(12, 11),
    KING(11, 10),
    QUEEN(10, 10),
    KNAVE(9, 10),
    TEN(8, 10),
    NINE(7, 9),
    EIGHT(6, 8),
    SEVEN(5, 7),
    SIX(4, 6),
    FIVE(3, 5),
    FOUR(2, 4),
    THREE(1, 3),
    TWO(0, 2);

    private Integer number;
    private Integer value;

    private CardName(Integer number, Integer value){
        this.number = number;
        this.value = value;
    }

    private static final Map<Integer, CardName> map =
            new HashMap<Integer, CardName>();

    private static final Map<Integer, CardName> mapValues =
            new HashMap<Integer, CardName>();

    static {
        for (CardName type : CardName.values()) {
            map.put(type.number, type);
        }
        for (CardName type : CardName.values()) {
            mapValues.put(type.value, type);
        }
    }

    public Integer getValue() {
        return value;
    }

    public static CardName fromInt(Integer number) {
        if (map.containsKey(number)) {
            return map.get(number);
        }
        throw new NoSuchElementException(number + "not found");
    }
}
