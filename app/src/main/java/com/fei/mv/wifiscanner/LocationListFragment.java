package com.fei.mv.wifiscanner;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
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

    ExpandableListView locationListView;
    TextView locationResultText;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final MainActivity activity = (MainActivity) getActivity();

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

        final View view = inflater.inflate(R.layout.location_list_fragment, container, false);
        locationListView = (ExpandableListView) view.findViewById(R.id.location_list);

        final LocationListAdapter listAdapter = new LocationListAdapter(
                getActivity(), this.locationHeaders, this.locationItems);
        locationListView.setAdapter(listAdapter);
        locationListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int i, int i1, long l) {
                Bundle bundle = new Bundle();
                Record record = (Record) listAdapter.getChild(i, i1);
                bundle.putInt("index", record.getId());

                DetailFragment detailFragment = new DetailFragment();
                detailFragment.setArguments(bundle);
                getFragmentManager().beginTransaction().replace(R.id.main_frame, detailFragment)
                        .addToBackStack("location_detail").commit();
                return false;
            }
        });

        RelativeLayout locationResultLayout =
                (RelativeLayout) view.findViewById(R.id.location_result_layout);

        locationResultLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IdentifiedLocationFragment createFragment = new IdentifiedLocationFragment();

                getFragmentManager().beginTransaction().replace(R.id.main_frame, createFragment)
                        .addToBackStack("show_compare_result").commit();
            }
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        MainActivity activity = (MainActivity) getActivity();
        activity.updateCurrentLocation();
        locationResultText = (TextView) this.getView().findViewById(R.id.location_result);
        locationResultText.setText(activity.getCurrentLocation());
    }
}
