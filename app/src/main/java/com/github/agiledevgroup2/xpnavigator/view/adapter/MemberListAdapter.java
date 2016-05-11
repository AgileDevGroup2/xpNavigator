package com.github.agiledevgroup2.xpnavigator.view.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.agiledevgroup2.xpnavigator.R;
import com.github.agiledevgroup2.xpnavigator.model.TrelloBoardMembers;
import com.github.agiledevgroup2.xpnavigator.model.TrelloMember;

/**
 * Created by Thibault on 28/04/2016.
 */
public class MemberListAdapter extends BaseAdapter {

    // ArrayList<String> name, company, email, id, status;
    private TrelloBoardMembers boardMembers;
    private Activity activity;
    public LayoutInflater inflater;

    public MemberListAdapter(Activity act, TrelloBoardMembers boardMembers) {
        this.boardMembers = boardMembers;
        this.activity = act;
        inflater = act.getLayoutInflater();
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return boardMembers.getmListMembers().size();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return boardMembers.getmListMembers().get(position);
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        TextView textView;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_member_item, null);
        }

        TrelloMember member = boardMembers.getMember(position);
        String fullName = member.getmFullName();
        String memberType = boardMembers.getMemberType(member);

        textView = (TextView) convertView.findViewById(R.id.member_name);
        textView.setText(fullName);

        textView = (TextView) convertView.findViewById(R.id.member_type);
        textView.setText(memberType);

        return convertView;
    }
}
