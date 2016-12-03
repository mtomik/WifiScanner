package com.fei.mv.wifiscanner;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.fei.mv.wifiscanner.model.Record;
import com.fei.mv.wifiscanner.model.WifiScan;

import java.util.Date;
import java.util.List;

/**
 * Created by juraj on 28.11.2016.
 */

public class LocationCreateFragment extends Fragment implements View.OnClickListener {
    private WifiManager wifi;
    private SQLHelper sqlHelper;
    private Spinner sectionSpinner;
    private Spinner floorSpinner;
    private List<WifiScan> scanResults;
    private View rootView;
    private Button saveButton;
    private Button rescanButton;
    private ListView listView;
    private LocationCreateAdapter adapter;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.location_add, container, false);

        this.scanResults = ((MainActivity)getActivity()).startScan();
        this.wifi = ((MainActivity)getActivity()).wifi;
        this.sqlHelper = ((MainActivity)getActivity()).sqlHelper;

        initializeSpinners();
        initializeButtons();

        listView = (ListView) rootView.findViewById(R.id.listview_wifi) ;

        adapter = new LocationCreateAdapter(getActivity(), scanResults);
        listView.setAdapter(adapter);

        return rootView;
    }

    private void initializeButtons() {
        saveButton = (Button) rootView.findViewById(R.id.save_location);
        saveButton.setOnClickListener(this);
        rescanButton = (Button) rootView.findViewById(R.id.rescan_wifi);
        rescanButton.setOnClickListener(this);
    }


    public void initializeSpinners(){
        sectionSpinner = (Spinner) rootView.findViewById(R.id.section_spinner);
        ArrayAdapter<CharSequence> sectionAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.sections, android.R.layout.simple_spinner_item);
        sectionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sectionSpinner.setAdapter(sectionAdapter);

        floorSpinner = (Spinner) rootView.findViewById(R.id.floor_spinner);
        ArrayAdapter<CharSequence> floorAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.floorNum, android.R.layout.simple_spinner_item);
        floorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        floorSpinner.setAdapter(floorAdapter);
    }

    public void rescan(View v){
        scanResults = ((MainActivity)getActivity()).startScan();
        ((MainActivity)getActivity()).scanResults = scanResults;
        adapter.clear();
        adapter.addAll(scanResults);
        adapter.notifyDataSetChanged();
    }

    public void save(View v){
//        ListView items = (ListView) getActivity().findViewById(R.id.listview_wifi);

        Record record = new Record();
        String floor = floorSpinner.getSelectedItem().toString();
        String section = sectionSpinner.getSelectedItem().toString();
        record.setFloor(floorSpinner.getSelectedItem().toString());
        record.setSection(sectionSpinner.getSelectedItem().toString());
        record.setEdited_at(new Date());
        record.setWifiScan(((MainActivity)getActivity()).scanResults);

        sqlHelper.addLocationRecord(record);
        Record temp = sqlHelper.getLocationRecordByName(record.getSection()+record.getFloor());
        //((MainActivity)getActivity()).allRecords.add(temp);
        ((MainActivity)getActivity()).allRecords = sqlHelper.getAllLocationRecords();

        Toast.makeText(rootView.getContext(),"Record for "+section+floor+" added!",Toast.LENGTH_SHORT).show();

        LocationListFragment locationListFragment = new LocationListFragment();
        FragmentTransaction transaction = getFragmentManager().beginTransaction().replace(R.id.main_frame, locationListFragment);
        transaction.commit();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.rescan_wifi){
            rescan(v);
        }else if(v.getId() == R.id.save_location){
            save(v);
        }
    }
}
