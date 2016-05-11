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
        return boardMembers.nbPeople();
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return boardMembers.getMemberPosition(position);
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

        TrelloMember member = boardMembers.getMemberPosition(position);
        String fullName = member.getmFullName();


        textView = (TextView) convertView.findViewById(R.id.member_name);
        textView.setText(fullName);

        if (position < boardMembers.getmListMembers().size()) {
            String memberType = boardMembers.getMemberType(member);

            textView = (TextView) convertView.findViewById(R.id.member_type);
            textView.setText(memberType);
        }

        if(position == 0)
        {
            textView = (TextView) convertView.findViewById(R.id.header_memberlist);
            textView.setVisibility(View.VISIBLE);
            textView.setText("MEMBER");
        }

        if (position == boardMembers.getmListMembers().size())
        {

            textView = (TextView) convertView.findViewById(R.id.header_memberlist);
            textView.setVisibility(View.VISIBLE);
            textView.setText("NON MEMBER");

        }

        return convertView;
    }
}
