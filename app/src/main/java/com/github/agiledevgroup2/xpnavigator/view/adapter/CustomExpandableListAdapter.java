package com.github.agiledevgroup2.xpnavigator.view.adapter;


import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.github.agiledevgroup2.xpnavigator.R;
import com.github.agiledevgroup2.xpnavigator.model.TrelloCard;
import com.github.agiledevgroup2.xpnavigator.model.TrelloList;

import java.util.HashMap;
import java.util.List;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {
    private List<TrelloList> expandableListItems;
    private HashMap<String,List<TrelloCard>> expandableListOverview;
    private Context context;

    public CustomExpandableListAdapter(Context context, List<TrelloList> expandableListItems,
                                       HashMap<String, List<TrelloCard>> expandableListOverview) {
        this.context = context;
        this.expandableListItems = expandableListItems;
        this.expandableListOverview = expandableListOverview;

    }

    @Override
    public int getGroupCount() {
        /*Might need Fixing some time*/return this.expandableListItems.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.expandableListOverview.get(this.expandableListItems.get(groupPosition).getName()).size();
    }



    @Override
    public Object getGroup(int groupPosition) {
        return this.expandableListItems.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.expandableListOverview.get(this.expandableListItems.
                get(groupPosition).getName())
                .get(childPosition);
    }


    /*TODO IMPLEMENT METHODS ACCORDING TO TRELLOCARD / TRELLOLIST INSTEAD OF STRING*/
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        TrelloList item =  (TrelloList) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_listgroup, null);
        }
        TextView listTitleTextView = (TextView) convertView
                .findViewById(R.id.listTitle);
        listTitleTextView.setTypeface(null, Typeface.BOLD);
        listTitleTextView.setText(item.getName());
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        TrelloCard child = (TrelloCard) getChild(groupPosition,childPosition);
        if (convertView == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = layoutInflater.inflate(R.layout.list_card, null);
        }
        TextView expandedListTextView = (TextView) convertView
                .findViewById(R.id.expandedListItem);
        expandedListTextView.setText(child.getName());

        /*
        Button button = (Button) convertView.findViewById(R.id.listViewChildButton);
        button.setFocusable(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


            }
        });*/
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
