package com.github.agiledevgroup2.xpnavigator;

import java.util.List;

/**
 * Interface for API callbacks
 */
public interface ApiListener {

    /**
     * method invoked after asynchronous api call for boards from trello
     * @param boards fetched boards from trello
     */
    void boardsCallback(List<TrelloBoard> boards);

    /**
     * method invoked after asynchronous api call for lists from trello
     * @param lists fetched boards from trello
     * @param boardId board the lists belong to
     */
    void listsCallback(List<TrelloList> lists, String boardId);

    /**
     * method invoked after asynchronous api call for cards from trello
     * @param cards fetched cards from trello
     * @param listId list the cards belong to
     */
    void cardsCallback(List<TrelloCard> cards, String listId);



}
