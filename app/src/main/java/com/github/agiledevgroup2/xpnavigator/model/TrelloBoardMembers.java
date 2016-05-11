package com.github.agiledevgroup2.xpnavigator.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Thibault on 28/04/2016.
 */
public class TrelloBoardMembers {

    private String mIdBoard;
    private ArrayList<TrelloMember> mListMembers;
    private HashMap<String, String> mMemberType;

    public TrelloBoardMembers ()
    {
        mListMembers = new ArrayList<TrelloMember>();
        mMemberType = new HashMap<String, String>();
    }

    public TrelloBoardMembers(JSONArray json, String idBoard) throws JSONException
    {
        mIdBoard = idBoard;
        mListMembers = new ArrayList<TrelloMember>();
        mMemberType = new HashMap<String, String>();


        for(int i = 0 ; i < json.length(); i++)
        {
            JSONObject jsonObject = json.getJSONObject(i);
            mMemberType.put(jsonObject.getString("idMember"), jsonObject.getString("memberType"));
        }
    }

    public void addMember(TrelloMember member)
    {

        if (!mMemberType.containsKey(member.getmId()))
            mListMembers.add(mListMembers.size(), member);
        else
            mListMembers.add(0, member);
    }

    public boolean containsKey (String id)
    {
        return mMemberType.containsKey(id);
    }


    public String getMemberType (TrelloMember member)
    {
        return mMemberType.get(member.getmId());
    }

    public TrelloMember getMember (int pos)
    {
        return mListMembers.get(pos);
    }

    public String getmIdBoard() {
        return mIdBoard;
    }

    public void setmIdBoard(String mIdBoard) {
        this.mIdBoard = mIdBoard;
    }

    public ArrayList<TrelloMember> getmListMembers() {
        return mListMembers;
    }

    public void setmListMembers(ArrayList<TrelloMember> mListMembers) {
        this.mListMembers = mListMembers;
    }

    public HashMap<String, String> getmMemberType() {
        return mMemberType;
    }

    public void setmMemberType(HashMap<String, String> mMemberType) {
        this.mMemberType = mMemberType;
    }
}
