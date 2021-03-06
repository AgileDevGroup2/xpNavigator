package com.github.agiledevgroup2.xpnavigator.controller;

import com.github.agiledevgroup2.xpnavigator.model.TrelloBoard;
import com.github.agiledevgroup2.xpnavigator.model.TrelloBoardMembers;
import com.github.agiledevgroup2.xpnavigator.model.TrelloCard;
import com.github.agiledevgroup2.xpnavigator.model.TrelloList;

import java.util.List;

/**
 * Interface for API callbacks
 */
public interface ApiListener {


    void failureCallback (String failure);


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
    void cardsCallback(List<TrelloCard> cards, String listName);

    /**
     * method invoked after asynchronous api call for cards from trello
     *
     */
    void membersBoardCallback (TrelloBoardMembers boardMembers);

    /**
     *
     * @param nameBoardTeam
     */
    void nameBoardTeamCallback (String nameBoardTeam);



}
