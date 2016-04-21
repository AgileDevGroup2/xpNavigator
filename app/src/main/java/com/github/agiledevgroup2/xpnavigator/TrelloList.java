package com.github.agiledevgroup2.xpnavigator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Todo: Comment stuff
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

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmBoardId() {
        return mBoardId;
    }

    public List<TrelloCard> getCards() {
        return mCards;
    }

    public void setmBoardId(String mBoardId) {
        this.mBoardId = mBoardId;
    }

    public void addCard(TrelloCard card) {
        if (!mCards.contains(card)) mCards.add(card);
    }

    public void removeCard(TrelloCard card) {
        mCards.remove(card); //todo test if this remove method uses the compareTo method
    }

    public void setCards(List<TrelloCard> cards) {
        mCards = cards;
    }

    public int compareTo(TrelloList other) {
        return other.toString().compareTo(this.toString());
    }

    @Override
    public String toString() {
         return "Id:" + mId + "-Name:" + mName + "-IdBoard:" + mBoardId;
    }
}
