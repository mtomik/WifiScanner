package com.fei.mv.wifiscanner;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.fei.mv.wifiscanner.model.Record;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by rakyi on 26.11.2016.
 */

public class LocationListFragment extends Fragment {
    List<String> locationHeaders;
    HashMap<String, List<Record>> locationItems;

    ExpandableListView locationList;
    TextView locationResultText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) getActivity();

        locationHeaders = new ArrayList<>();
        locationItems = new HashMap<>();

        for (Record record : activity.getAllRecords()) {
            String section = record.getSection();
            if (!locationItems.containsKey(section)) {
                locationHeaders.add(section);
                List<Record> list = new ArrayList<>();
                list.add(record);
                locationItems.put(section, list);
            } else {
                locationItems.get(section).add(record);
            }
        }

        View view = inflater.inflate(R.layout.location_list_fragment, container, false);
        locationList = (ExpandableListView) view.findViewById(R.id.location_list);
        // TODO Nastavit vysledok.
//        locationResultText = (TextView) view.findViewById(R.id.location_result);
//        locationResultText.setText();

        LocationListAdapter listAdapter = new LocationListAdapter(
                getActivity(), this.locationHeaders, this.locationItems);
        locationList.setAdapter(listAdapter);
        return view;
    }
}
