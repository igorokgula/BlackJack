package com.hula.ihor.generator;

import com.hula.ihor.domain.Card;
import com.hula.ihor.enumeration.CardLear;
import com.hula.ihor.enumeration.CardName;
import com.hula.ihor.exception.NoCardsInDeckException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Igor on 30.07.2015.
 */
public class UniformCardGenerator implements CardGenerator {

    public Card generate(List<Card> cards) {
        Card card = null;

        if (cards.size() <= 0) {
            throw new NoCardsInDeckException();
        }
        Random random = new Random();
        do {
            int lear = random.nextInt(CardLear.values().length);
            int name = random.nextInt(CardName.values().length);

            card = new Card(CardLear.fromInt(lear), CardName.fromInt(name));
        } while (!cards.contains(card));

        cards.remove(card);

        return card;
    }
}
