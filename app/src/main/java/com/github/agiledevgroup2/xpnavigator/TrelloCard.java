package com.github.agiledevgroup2.xpnavigator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Todo: Comment stuff
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

    public String getmDesc() {
        return mDesc;
    }

    public void setmDesc(String mDesc) {
        this.mDesc = mDesc;
    }

    public String getmBoardId() {
        return mBoardId;
    }

    public void setmBoardId(String mBoardId) {
        this.mBoardId = mBoardId;
    }

    public String getmListId() {
        return mListId;
    }

    public void setmListId(String mListId) {
        this.mListId = mListId;
    }

    public Date getmLastChanged() {
        return mLastChanged;
    }

    public void setmLastChanged(Date mLastChanged) {
        this.mLastChanged = mLastChanged;
    }

    public int compareTo(TrelloCard other) {
        return this.toString().compareTo(other.toString());
    }

    @Override
    public String toString() {
        return "";
    }
}
