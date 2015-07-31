package com.hula.ihor.domain;

/**
 * Created by Igor on 30.07.2015.
 */
public class Place {

    private static int counter = 1;

    private User user = null;

    private int number = counter++;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getNumber() {
        return number;
    }
}
