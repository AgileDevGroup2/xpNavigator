package com.github.agiledevgroup2.xpnavigator;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * This class represents a trello board
 */
public class TrelloBoard {

    private static final String LOG_TAG = "TrelloBoard";

    private String mName;
    private String mId;
    private String mDesc;
    private Date mLastEdited;
    private List<TrelloList> mLists;

    /**
     * creates a new Board Object, throws JSONException if the json object can't be parsed
     * @param json json from trello
     * @throws JSONException wrong json objects
     */
    public TrelloBoard(JSONObject json) throws JSONException {
        mName = json.getString("name");
        mId = json.getString("id");
        mDesc = json.getString("desc");

        mLists = new ArrayList<>();

        mLastEdited = Util.getDateFromJson(json.getString("dateLastActivity"));
    }

    /**
     * set Name of this Board
     * @param mName new name
     */
    public void setName(String mName) {
        this.mName = mName;
    }

    /**
     * set description of this Board
     * @param mDesc new description
     */
    public void setDesc(String mDesc) {
        this.mDesc = mDesc;
    }

    /**
     * set id for this Board (not recommended to use) todo: might remove that...
     * @param mId new id
     */
    public void setId(String mId) {
        this.mId = mId;
    }

    /**
     * set last edited parameter (not recommended to use) todo: might remove that...
     * @param mLastEdited new date for last edited
     */
    public void setLastEdited(Date mLastEdited) {
        this.mLastEdited = mLastEdited;
    }

    /**
     * set lists of this board
     * @param mLists list of trello-lists
     */
    public void setLists(List<TrelloList> mLists) {
        this.mLists = mLists;
    }

    /**
     * get name of this board
     * @return board'S name
     */
    public String getName() {
        return mName;
    }

    /**
     * returns the id of this board
     * @return board's id
     */
    public String getId() {
        return mId;
    }

    /**
     * returns the board's description
     * @return board's description
     */
    public String getDesc() {
        return mDesc;
    }

    /**
     * returns the time the board got edited the last time
     * @return date of last edit
     */
    public Date getmLastEdited() {
        return mLastEdited;
    }

    /**
     * returns all trello-lists of the board
     * @return list of board's trello-lists
     */
    public List<TrelloList> getLists() {
        return mLists;
    }

    /**
     * add list to this board, if not already present
     * @param list list to add
     */
    public void addList(TrelloList list) {
        if (!mLists.contains(list)) mLists.add(list);
    }

    /**
     * remove a list from this board
     * @param list list to remove
     */
    public void removeList(TrelloList list) {
        mLists.remove(list);
    }
}
