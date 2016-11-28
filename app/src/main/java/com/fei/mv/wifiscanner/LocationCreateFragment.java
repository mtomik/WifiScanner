package com.fei.mv.wifiscanner;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import com.fei.mv.wifiscanner.model.WifiScan;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by juraj on 28.11.2016.
 */

public class LocationCreateFragment extends Fragment {
    private Spinner sectionSpinner;
    private Spinner floorSpinner;
    private List<WifiScan> scanResults;
    private ArrayAdapter<String> infoAdapter;

    private List<String> info;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.location_add, container, false);

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

        // TODO: naplnit listview
        infoAdapter = new ArrayAdapter<String>(getActivity(), R.layout.list_item_wifi ,R.id.wifi_info, info);
        ListView view = (ListView) rootView.findViewById(R.id.listview_wifi);

        view.setAdapter(infoAdapter);



        return rootView;
    }

    public void setScanResult(List<WifiScan> scanResults){
        this.scanResults = scanResults;
        if(scanResults != null){
            info = new ArrayList<String>();
            for (WifiScan s : scanResults){
                this.info.add("Mac:" + s.getMAC()+"\nSSID:"+s.getSSID()+"\tLevel:"+s.getRSSI());
            }
        }
    }
}
