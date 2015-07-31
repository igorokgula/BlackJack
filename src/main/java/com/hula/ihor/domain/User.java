package com.hula.ihor.domain;

import com.hula.ihor.enumeration.UserAction;
import io.netty.channel.Channel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Igor on 30.07.2015.
 */
public class User {
    private Channel channel;
    private Double bet;
    private List<Card> cards;
    private boolean isMoveDone = false;

    public User() {
        cards = new ArrayList<Card>();
    }

    public Channel getChannel() {
        return channel;
    }

    public void setChannel(Channel channel) {
        this.channel = channel;
    }

    public Double getBet() {
        return bet;
    }

    public void setBet(Double bet) {
        this.bet = bet;
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public List<Card> getCards() {
        return cards;
    }

    public void resetCards() {
        this.cards.clear();
    }

    public boolean isMoveDone() {
        return isMoveDone;
    }

    public void setIsMoveDone(boolean isMoveDone) {
        this.isMoveDone = isMoveDone;
    }
}
