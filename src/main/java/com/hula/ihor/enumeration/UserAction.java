package com.hula.ihor.enumeration;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

/**
 * Created by Igor on 30.07.2015.
 */
public enum UserAction {
    STAND("stand"),
    HIT("hit"),
    DOUBLE("double");

    private String name;

    private UserAction(String s) {
        name = s;
    }

    private static final Map<String, UserAction> map =
            new HashMap<String, UserAction>();

    static {
        for (UserAction type : UserAction.values()) {
            map.put(type.name, type);
        }
    }

    public String getName() {
        return name;
    }

    public static UserAction fromString(String name) {
        if (map.containsKey(name)) {
            return map.get(name);
        }
        throw new NoSuchElementException(name + "not found");
    }
}
