package com.fei.mv.wifiscanner;

import android.net.wifi.ScanResult;
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

import java.util.ArrayList;
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
    private ArrayAdapter<String> infoAdapter;
    private View rootView;
    private List<String> info;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.location_add, container, false);

        this.scanResults = ((MainActivity)getActivity()).scanResults;
        this.wifi = ((MainActivity)getActivity()).wifi;
        this.sqlHelper = ((MainActivity)getActivity()).sqlHelper;

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

        fillInfoList();


//        infoAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_wifi ,R.id.wifi_info, info);
//        ListView view = (ListView) rootView.findViewById(R.id.listview_wifi);
//
//        view.setAdapter(infoAdapter);
        populateListView(rootView);
        Button saveButton = (Button) rootView.findViewById(R.id.save_location);
        saveButton.setOnClickListener(this);
        Button rescanButton = (Button) rootView.findViewById(R.id.rescan_wifi);
        rescanButton.setOnClickListener(this);

        return rootView;
    }

    public void populateListView(View rootView){
        infoAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_wifi ,R.id.wifi_info, info);
        ListView view = (ListView) rootView.findViewById(R.id.listview_wifi);

        view.setAdapter(infoAdapter);
    }

    public void fillInfoList(){
        info = null;
        if(scanResults != null){
            info = new ArrayList<String>();
            for (WifiScan s : scanResults){
                this.info.add("Mac:" + s.getMAC()+"\nSSID:"+s.getSSID()+"\tLevel:"+s.getRSSI());
            }
        }
    }

    public void rescan(View v){
        List<WifiScan> scan = new ArrayList<>();

        wifi.startScan();
        List<ScanResult> result =  wifi.getScanResults();
        for (ScanResult one : result){
            WifiScan newOne = new WifiScan();
            newOne.setSSID(one.SSID);
            newOne.setRSSI(String.valueOf(one.level));
            newOne.setMAC(one.BSSID);

            scan.add(newOne);
        }
        scanResults = scan;
        fillInfoList();
        infoAdapter.clear();
        infoAdapter.addAll(info);
        infoAdapter.notifyDataSetChanged();
    }

    public void save(View v){
        Record record = new Record();
        String floor = floorSpinner.getSelectedItem().toString();
        String section = sectionSpinner.getSelectedItem().toString();
        record.setFloor(floorSpinner.getSelectedItem().toString());
        record.setSection(sectionSpinner.getSelectedItem().toString());
        record.setEdited_at(new Date());
        record.setWifiScan(scanResults);

        sqlHelper.addLocationRecord(record);

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
