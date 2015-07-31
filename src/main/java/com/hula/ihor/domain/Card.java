package com.hula.ihor.domain;

import com.hula.ihor.enumeration.CardLear;
import com.hula.ihor.enumeration.CardName;

/**
 * Created by Igor on 30.07.2015.
 */
public final class Card {

    private final CardLear lear;
    private final CardName name;

    public Card(CardLear lear, CardName name) {
        this.lear = lear;
        this.name = name;
    }

    public CardName getName() {
        return name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Card card = (Card) o;

        if (lear != card.lear) return false;
        return name == card.name;

    }

    @Override
    public int hashCode() {
        int result = lear.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "Card{ " + name + " of " + lear + " }\r\n";
    }
}
