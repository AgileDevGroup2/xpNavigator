package com.github.agiledevgroup2.xpnavigator;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * Todo: Comment stuff
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

    //Setter

    public void setName(String mName) {
        this.mName = mName;
    }

    public void setDesc(String mDesc) {
        this.mDesc = mDesc;
    }

    public void setId(String mId) {
        this.mId = mId;
    }

    public void setLastEdited(Date mLastEdited) {
        this.mLastEdited = mLastEdited;
    }

    public void setLists(List<TrelloList> mLists) {
        this.mLists = mLists;
    }

    //Getter

    public String getName() {
        return mName;
    }

    public String getId() {
        return mId;
    }

    public String getDesc() {
        return mDesc;
    }

    public Date getmLastEdited() {
        return mLastEdited;
    }

    public List<TrelloList> getLists() {
        return mLists;
    }

    public void addList(TrelloList list) {
        if (!mLists.contains(list)) mLists.add(list);
    }

    public void removeList(TrelloList list) {
        mLists.remove(list);
    }
}
