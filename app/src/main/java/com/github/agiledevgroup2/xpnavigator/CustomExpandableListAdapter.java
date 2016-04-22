package com.github.agiledevgroup2.xpnavigator;


import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.BaseExpandableListAdapter;

import java.util.HashMap;
import java.util.List;

public class CustomExpandableListAdapter extends BaseExpandableListAdapter {
    private List<String> expandableListItems;
    private HashMap<String,List<String>> expandableListOverview;
    private Context context;

    public CustomExpandableListAdapter(Context context, List<String> expandableListItems,
                                       HashMap<String, List<String>> expandableListOverview) {
        this.context = context;
        this.expandableListItems = expandableListItems;
        this.expandableListOverview = expandableListOverview;
    }

    @Override
    public int getGroupCount() {
        return this.expandableListItems.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this.expandableListOverview.get(this.expandableListItems.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        return null;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
