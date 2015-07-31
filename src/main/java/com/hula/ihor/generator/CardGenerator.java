package com.hula.ihor.generator;

import com.hula.ihor.domain.Card;

import java.util.List;

/**
 * Created by Igor on 30.07.2015.
 */
public interface CardGenerator {
    public Card generate(List<Card> cards);
}
