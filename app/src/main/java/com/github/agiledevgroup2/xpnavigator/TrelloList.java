package com.github.agiledevgroup2.xpnavigator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a trello-list
 */
public class TrelloList implements Comparable<TrelloList> {
    private String mId;
    private String mName;
    private String mBoardId;
    private List<TrelloCard> mCards;

    /**
     * constructs a new trello list
     * @param json json object provided by trello api
     * @throws JSONException exception thrown if json could not be parsed
     */
    public TrelloList(JSONObject json) throws JSONException {
        mId = json.getString("id");
        mName = json.getString("name");
        mBoardId = json.getString("idBoard");

        mCards = new ArrayList<>();
    }

    /**
     * get the list's id
     * @return list's id
     */
    public String getId() {
        return mId;
    }

    /**
     * set the list's id
     * @param mId new id
     */
    public void setId(String mId) {
        this.mId = mId;
    }

    /**
     * get the list's name
     * @return list's name
     */
    public String getName() {
        return mName;
    }

    /**
     * set the list's name
     * @param mName new name
     */
    public void setName(String mName) {
        this.mName = mName;
    }

    /**
     * get the board's id associated with this list
     * @return board id
     */
    public String getBoardId() {
        return mBoardId;
    }

    /**
     * set the board's id associated with this list
     * @param mBoardId new board id
     */
    public void setBoardId(String mBoardId) {
        this.mBoardId = mBoardId;
    }

    /**
     * get all cards of this list
     * @return list of trello-cards
     */
    public List<TrelloCard> getCards() {
        return mCards;
    }

    /**
     * add a card to this list, if not already present
     * @param card card to add
     */
    public void addCard(TrelloCard card) {
        if (!mCards.contains(card)) mCards.add(card);
    }

    /**
     * remove a card from this list
     * @param card card to remove
     */
    public void removeCard(TrelloCard card) {
        mCards.remove(card); //todo test if this remove method uses the compareTo method
    }

    /**
     * set cards of this list
     * @param cards list of cards to set
     */
    public void setCards(List<TrelloCard> cards) {
        mCards = cards;
    }

    /**
     * compare to other list
     * @param other other list
     * @return negative integer if this < other, 0 if equal or positive integer if this > other
     */
    @Override
    public int compareTo(TrelloList other) {
        return other.toString().compareTo(this.toString());
    }
    
    /**
     * returns a string representation of this class
     * @return string representation
     */
    @Override
    public String toString() {
         return "Id:" + mId + "-Name:" + mName + "-IdBoard:" + mBoardId;
    }
}
