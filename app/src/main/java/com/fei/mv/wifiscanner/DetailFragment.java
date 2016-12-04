package com.fei.mv.wifiscanner;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.fei.mv.wifiscanner.model.Record;

/**
 * Created by martintomik on 28/11/2016.
 */

public class DetailFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        MainActivity activity = (MainActivity) getActivity();

        int index = getArguments().getInt("index");
        Record record = activity.getRecordById(index);


        View view = inflater.inflate(R.layout.activity_detail, container, false);
        ListView listView = (ListView)view.findViewById(R.id.detail_list_view);
        TextView text = (TextView) view.findViewById(R.id.recordText);

        String floor = record.getFloor();
        if (floor.equals("0")) {
            floor = activity.getResources().getString(R.string.ground_floor);
        } else {
            floor = floor + ". " + activity.getResources().getString(R.string.floor);
        }

        text.setText("Blok " + record.getSection() + " - " + floor);

        DetailAdapter adapter = new DetailAdapter(record, getActivity());
        listView.setAdapter(adapter);


        return view;

    }
}
