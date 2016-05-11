package com.github.agiledevgroup2.xpnavigator.model;

import android.util.Log;

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
    private ArrayList<TrelloMember> mListSupervisors;

    private HashMap<String, String> mMemberType;

    public TrelloBoardMembers ()
    {
        mListMembers = new ArrayList<TrelloMember>();
        mMemberType = new HashMap<String, String>();
        mListSupervisors = new ArrayList<TrelloMember>();
    }

    public TrelloBoardMembers( String idBoard)
    {
        mIdBoard = idBoard;
        mListMembers = new ArrayList<TrelloMember>();
        mMemberType = new HashMap<String, String>();
        mListSupervisors = new ArrayList<TrelloMember>();
    }

    public void setOrganization (JSONArray json) throws JSONException
    {
        for(int i = 0 ; i < json.length(); i++)
        {
            JSONObject jsonObject = json.getJSONObject(i);
            mMemberType.put(jsonObject.getString("idMember"), jsonObject.getString("memberType"));
        }
    }

    public void addMember(TrelloMember member)
    {

        if (mMemberType.containsKey(member.getmId()))
        {
            if (mMemberType.get(member.getmId()).equals("admin"))
            {
                mListMembers.add(0, member);
            }
            else mListMembers.add(mListMembers.size(), member);
        }
        else mListSupervisors.add(member);
    }

    public void addInvitatedMember(TrelloMember member)
    {


    }

    public int nbPeople()
    {
        return mListMembers.size() + mListSupervisors.size();
    }

    public TrelloMember getMemberPosition (int position)
    {
        if (position < mListMembers.size())
            return mListMembers.get(position);
        else //if (position < mListMembers.size() + mListSupervisors.size())
            return mListSupervisors.get(position - mListMembers.size());

    }

    public boolean containsKey (String id)
    {
        return mMemberType.containsKey(id);
    }


    public String getMemberType (TrelloMember member)
    {
        return mMemberType.get(member.getmId());
    }



    // ---------------------  GETTERS AND SETTERS ---------------------

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

    public ArrayList<TrelloMember> getmListSupervisors() {
        return mListSupervisors;
    }

    public void setmListSupervisors(ArrayList<TrelloMember> mListSupervisors) {
        this.mListSupervisors = mListSupervisors;
    }

    public HashMap<String, String> getmMemberType() {
        return mMemberType;
    }

    public void setmMemberType(HashMap<String, String> mMemberType) {
        this.mMemberType = mMemberType;
    }
}
