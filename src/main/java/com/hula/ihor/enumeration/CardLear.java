package com.hula.ihor.enumeration;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Created by Igor on 30.07.2015.
 */
public enum CardLear {
    SPADES(0),
    CLUBS(1),
    HEARTS(2),
    DIAMONDS(3);

    private Integer number;

    private CardLear(Integer number){
        this.number = number;
    }

    private static final Map<Integer, CardLear> map =
            new HashMap<Integer, CardLear>();

    static {
        for (CardLear type : CardLear.values()) {
            map.put(type.number, type);
        }
    }

    public Integer getNumber() {
        return number;
    }

    public static CardLear fromInt(Integer number) {
        if (map.containsKey(number)) {
            return map.get(number);
        }
        throw new NoSuchElementException(number + "not found");
    }
}
