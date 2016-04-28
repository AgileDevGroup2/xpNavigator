package com.github.agiledevgroup2.xpnavigator;

<<<<<<< HEAD
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Thibault on 28/04/2016.
 */
public class TrelloMember {

    private String mId;
    private String mFullName;
    private String mUsername;
    private String mEmail;


    public TrelloMember(JSONObject json) throws JSONException
    {
        mId = json.getString("id");
        mEmail = "";
        mUsername = json.getString("username");
        mFullName = json.getString("fullName");

    }

    public String getmId() {
        return mId;
    }

    public void setmId(String mId) {
        this.mId = mId;
    }

    public String getmFullName() {
        return mFullName;
    }

    public void setmFullName(String mFullName) {
        this.mFullName = mFullName;
    }

    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmUsername() {
        return mUsername;
    }

    public void setmUsername(String mUsername) {
        this.mUsername = mUsername;
    }
=======

public class TrelloMember {
>>>>>>> 360f5c71889d202da08fc0f99135439814532f81
}
