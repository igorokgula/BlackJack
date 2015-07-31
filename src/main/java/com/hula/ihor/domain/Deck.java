package com.hula.ihor.domain;

import com.hula.ihor.enumeration.CardLear;
import com.hula.ihor.enumeration.CardName;
import com.hula.ihor.generator.CardGenerator;
import com.hula.ihor.generator.UniformCardGenerator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Igor on 30.07.2015.
 */
public class Deck {
    private final List<Card> cards;

    private CardGenerator generator = new UniformCardGenerator();

    private static Deck instance = new Deck();

    private Deck() {
        this.cards = new ArrayList<Card>();
        generateCards();
    }

    private void generateCards() {
        for (CardLear lear : CardLear.values()) {
            for (CardName name : CardName.values()) {
                this.cards.add(new Card(lear, name));
            }
        }
    }

    public static Deck getInstance() {
        return instance;
    }

    public Card getNextCard() {
        return generator.generate(cards);
    }

}