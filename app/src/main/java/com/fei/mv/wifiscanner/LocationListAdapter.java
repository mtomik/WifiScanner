package com.fei.mv.wifiscanner;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.fei.mv.wifiscanner.model.Record;

import java.util.HashMap;
import java.util.List;

/**
 * Created by rakyi on 14.11.2016.
 */

public class LocationListAdapter extends BaseExpandableListAdapter {
    private Context context;
    private List<String> locationHeaders;
    private HashMap<String, List<Record>> locationItems;

    public LocationListAdapter(Context context, List<String> locationHeaders,
                               HashMap<String, List<Record>> locationItems) {
        this.context = context;
        this.locationHeaders = locationHeaders;
        this.locationItems = locationItems;
    }

    @Override
    public int getGroupCount() {
        return locationHeaders.size();
    }

    @Override
    public int getChildrenCount(int i) {
        return locationItems.get(this.locationHeaders.get(i)).size();
    }

    @Override
    public Object getGroup(int i) {
        return locationHeaders.get(i);
    }

    @Override
    public Object getChild(int i, int i1) {
        return locationItems.get(this.locationHeaders.get(i)).get(i1);
    }

    @Override
    public long getGroupId(int i) {
        return i;
    }

    @Override
    public long getChildId(int i, int i1) {
        return i1;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int i, boolean b, View convertView, ViewGroup viewGroup) {
        String locationHeader = (String) getGroup(i);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.location_list_group, null);
        }

        TextView lblListHeader = (TextView) convertView
                .findViewById(R.id.location_list_header);
        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(locationHeader);

        return convertView;
    }

    @Override
    public View getChildView(int i, int i1, boolean b, View convertView, ViewGroup viewGroup) {
        final Record child = (Record) getChild(i, i1);

        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) this.context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.location_list_item, null);
        }

        TextView floorText = (TextView) convertView.findViewById(R.id.location_list_floor);
        String floor = child.getFloor();
        if (floor.equals("0")) {
            floor = this.context.getResources().getString(R.string.ground_floor);
        } else {
            floor = floor + this.context.getResources().getString(R.string.floor);
        }

        floorText.setText(floor);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int i, int i1) {
        return true;
    }
}
