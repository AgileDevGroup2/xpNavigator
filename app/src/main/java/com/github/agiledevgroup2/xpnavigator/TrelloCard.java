package com.github.agiledevgroup2.xpnavigator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * This class represents a trello card
 */
public class TrelloCard implements Comparable<TrelloCard> {

    private String mId;
    private String mName;
    private String mDesc;
    private String mBoardId;
    private String mListId;
    private Date mLastChanged;

    /**
     * construct a new card from a trello json object
     * @param json json object from trello
     * @throws JSONException thrown if object could not be parsed
     */
    public TrelloCard(JSONObject json) throws JSONException {
        mId = json.getString("id");
        mName = json.getString("name");
        mDesc = json.getString("desc");
        mBoardId = json.getString("idBoard");
        mListId = json.getString("idList");

        mLastChanged = Util.getDateFromJson(json.getString("dateLastActivity"));
    }

    /**
     * get the card's id
     * @return card's id
     */
    public String getId() {
        return mId;
    }

    /**
     * set the car's id
     * @param mId new id
     */
    public void setId(String mId) {
        this.mId = mId;
    }

    /**
     * get the card's name
     * @return card's name
     */
    public String getName() {
        return mName;
    }

    /**
     * set the card's name
     * @param mName new name
     */
    public void setName(String mName) {
        this.mName = mName;
    }

    /**
     * get the card's description
     * @return card's description
     */
    public String getDesc() {
        return mDesc;
    }

    /**
     * set the card's description
     * @param mDesc new description
     */
    public void setDesc(String mDesc) {
        this.mDesc = mDesc;
    }

    /**
     * get the board's id associated to this card
     * @return board id
     */
    public String getBoardId() {
        return mBoardId;
    }

    /**
     * set the board's id associated with this card
     * @param mBoardId new board id
     */
    public void setBoardId(String mBoardId) {
        this.mBoardId = mBoardId;
    }

    /**
     * get the list's id associated with this card
     * @return list id
     */
    public String getListId() {
        return mListId;
    }

    /**
     * set a new list id to this card
     * @param mListId new list id
     */
    public void setListId(String mListId) {
        this.mListId = mListId;
    }

    /**
     * get the date this card was last edited at
     * @return date of last edit
     */
    public Date getLastChanged() {
        return mLastChanged;
    }

    /**
     * set the date this card was last edited at
     * @param mLastChanged new date of edit
     */
    public void setLastChanged(Date mLastChanged) {
        this.mLastChanged = mLastChanged;
    }

    /**
     * compare to other card
     * @param other other card
     * @return negative integer if this < other, 0 if equal or positive integer if this > other
     */
    @Override
    public int compareTo(TrelloCard other) {
        return this.toString().compareTo(other.toString());
    }

    /**
     * returns a string representation of this class
     * @return string representation
     */
    @Override
    public String toString() {
        return "Id:" + mId + "-Name:" + mName + "-Desc:" + mDesc + "-IdBoard:" + mBoardId + "-IdList:" + mListId;
    }
}
