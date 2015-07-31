package com.hula.ihor.domain;

import com.hula.ihor.enumeration.UserAction;
import com.hula.ihor.exception.TableIsFullException;
import com.hula.ihor.exception.UserNotFoundException;
import io.netty.channel.Channel;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by Igor on 30.07.2015.
 */
public class Table {

    private static final String ROUND_STARTED_MESSAGE =
            "New round started! \r\nPlease, type your bet amount (100 < bet < 1000). (20 seconds remaining...) \r\n";
    private static final String MOVE_IS_DONE_MESSAGE = "Your move is done.\r\n";
    private static final String MAKE_YOUR_MOVE_MESSAGE = "Make your move: HIT or STAND or DOUBLE!";

    private static final int PLACES_MAX_COUNT = 5;
    private static final int COUNT_DEFAULT_MOVES = 2;
    private static final int MIN_DEALER_VALUE = 17;
    private static int TIME_FOR_MAKING_BET = 20000;
    private static int TIME_FOR_MAKING_ACTION = 20000;

    private static int casinoMoney = 1000000;

    private static Table instance = new Table();


    private List<Place> places;

    private Deck deck = Deck.getInstance();

    private User dealer = new User();


    private Table() {
        places = new CopyOnWriteArrayList<Place>();
        for (int i = 0; i < PLACES_MAX_COUNT; i++) {
            places.add(new Place());
        }
    }

    public static Table getInstance() {
        return instance;
    }

    public synchronized int addUser(User user) throws TableIsFullException {
        Place freePlace = findFreePlace();
        if (freePlace != null) {
            freePlace.setUser(user);
        } else {
            throw new TableIsFullException();
        }
        return freePlace.getNumber();
    }

    public synchronized void removeUser(User user) {
        Place placeOfUser = findPlaceOfUser(user);
        synchronized (this) {
            if (placeOfUser != null) {
                placeOfUser.setUser(null);
                return;
            }
        }
    }

    private Place findPlaceOfUser(User user) {
        for (Place p : places) {
            if (p.getUser() != null && p.getUser().equals(user)) {
                return p;
            }
        }
        return null;
    }

    private synchronized Place findFreePlace() {
        for (Place p : places) {
            if (p.getUser() == null) {
                return p;
            }
        }
        return null;
    }

    public String setCardToUser(User user) {
        synchronized (this) {
            Card card = deck.getNextCard();
            user.addCard(card);
            return card.toString();
        }
    }

    public void playRound() {
        notifyPlayersAboutStartOfRound();
        try {
            Thread.sleep(TIME_FOR_MAKING_BET);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        setTwoCardsToUsers();
        for (Place p : places) {
            if (p.getUser() != null && p.getUser().getBet() != null) {
                p.getUser().getChannel().writeAndFlush(MAKE_YOUR_MOVE_MESSAGE);
                synchronized (this) {
                    try {
                        wait(TIME_FOR_MAKING_ACTION);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        setDealerCards();
        notifyPlayersAboutResultOfRound();

        roundEnded();
    }

    public String dealWithUserAction(String request, User user) {
        StringBuffer response = new StringBuffer();
        try {
            if (UserAction.fromString(request) == UserAction.HIT) {
                Table.getInstance().setCardToUser(user);
                response.append(user.getCards().get(user.getCards().size() - 1) + "\r\n");
            } else if (UserAction.fromString(request) == UserAction.STAND) {
                response.append(MOVE_IS_DONE_MESSAGE);
                user.setIsMoveDone(true);
                Table.getInstance().notifyUserMoveIsDone();
            } else if (UserAction.fromString(request) == UserAction.DOUBLE){
                user.setBet(user.getBet() * 2);
                Table.getInstance().setCardToUser(user);
                user.setIsMoveDone(true);
                response.append(user.getCards().get(user.getCards().size() - 1) + "\r\n");
                response.append(MOVE_IS_DONE_MESSAGE);
                Table.getInstance().notifyUserMoveIsDone();
            }
        } catch (NoSuchElementException e) {
            e.printStackTrace();
        }

        return response.toString();
    }

    public synchronized void notifyUserMoveIsDone() {
        notify();
    }

    private void setTwoCardsToUsers() {
        for (int i = 0; i < COUNT_DEFAULT_MOVES; i++) {
            for (Place p : places) {
                if (p.getUser() != null && p.getUser().getBet() != null) {
                    p.getUser().getChannel().writeAndFlush(setCardToUser(p.getUser()));
                }
            }
            setCardToUser(dealer);
        }
    }

    private void notifyPlayersAboutStartOfRound() {
            for (Place p : places) {
                User user = p.getUser();
                if (user != null) {
                    user.getChannel().writeAndFlush(ROUND_STARTED_MESSAGE);
                }
            }
        }

    private void setDealerCards() {
        int dealerValue = userCardsValue(dealer);
        while (dealerValue < MIN_DEALER_VALUE) {
            setCardToUser(dealer);
            dealerValue = userCardsValue(dealer);
        }
    }

    private int userCardsValue(User user) {
        int value = 0;

        int countOfIces = 0;
        for (Card card : user.getCards()) {
            int cardValue = card.getName().getValue();
            if (cardValue == 11) {
                countOfIces++;
            }
            value += cardValue;
        }
        for (int i = 0; i < countOfIces; i++) {
            if (value <= 21) {
                break;
            }
            value -= 10;
        }

        return value;
    }

    private void notifyPlayersAboutResultOfRound() {
        final int dealerValue = userCardsValue(dealer);
        StringBuffer response = new StringBuffer();

        for (Place p : places) {
            User user = p.getUser();
            if (user != null && user.getBet() != null) {
                final int userValue = userCardsValue(user);
                Channel channel = user.getChannel();
                double koef = 0;
                if (userValue > 21 || (dealerValue > userValue && dealerValue <= 21)) {
                    koef = prizeKoef(dealerValue);
                    response.append("You lost "+ koef * user.getBet());
                    casinoMoney += koef * user.getBet();
                    user.setBet(null);
                } else if (dealerValue < userValue || dealerValue > 21) {
                    koef = prizeKoef(userValue);
                    response.append("You won "+ koef * user.getBet());
                    casinoMoney -= koef * user.getBet();
                    user.setBet(null);
                } else {
                    response.append("Draw ");
                }
                response.append("\n You: " + userValue + "\nDealer: " + dealerValue + " \r\n");
                channel.writeAndFlush(response.toString());
                response.setLength(0);
            }
        }
    }

    private double prizeKoef(int points) {
        if (points == 21) {
            return 3.0/2.0;
        }
        return 1.0;
    }

    public void roundEnded() {
        for (Place p : places) {
            if (p.getUser() != null) {
                p.getUser().resetCards();
                p.getUser().setIsMoveDone(false);
            }
        }
        dealer.resetCards();
    }
}
