package com.fei.mv.wifiscanner;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.app.ListFragment;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.fei.mv.wifiscanner.model.WifiScan;
import com.fei.mv.wifiscanner.model.WifiScanCompared;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tomasstrba on 1.12.16.
 */

public class IdentifiedLocationFragment extends Fragment {

    private WifiManager wifi;
    private SQLHelper sqlHelper;
    private Spinner sectionSpinner;
    private Spinner floorSpinner;
    private List<WifiScan> scanResults;
    private View rootView;
    private List<WifiScanCompared> resultComparetWifis;


    public IdentifiedLocationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        MainActivity activity = (MainActivity) getActivity();
        this.scanResults = ((MainActivity) getActivity()).scanResults;
        this.wifi = ((MainActivity) getActivity()).wifi;
        this.sqlHelper = ((MainActivity) getActivity()).sqlHelper;

        View view = inflater.inflate(R.layout.activity_detail, container, false);
        ListView listView = (ListView) view.findViewById(R.id.detail_list_view);
        TextView locText = (TextView) view.findViewById(R.id.location_result);
        String nazov = activity.foundFloor;
        //ToDo if is N/A  toas nenaslo sa ---
        comparaWifiScans(scanResults,sqlHelper.getLocationRecordByName(nazov).getWifiScan());

        IdentifiedLocationAdapter adapter = new IdentifiedLocationAdapter(getActivity(), resultComparetWifis);

        listView.setAdapter(adapter);


        return view;
    }


    private void comparaWifiScans(List<WifiScan> list_akt_data, List<WifiScan> list_db_data) {
        resultComparetWifis = new ArrayList<WifiScanCompared>();
        List<String> listMac = new ArrayList<String>();

        for (WifiScan akt_scan : list_akt_data) {
            WifiScanCompared wifi = new WifiScanCompared();
            wifi.setcompareResult("new");
            wifi.setSSID(akt_scan.getSSID());
            wifi.setRSSI("NewLevel:"+akt_scan.getRSSI());
            wifi.setMAC(akt_scan.getMAC());
            listMac.add(wifi.getMAC());
            for (WifiScan db_scan : list_db_data) {
                if (akt_scan.getMAC().equals(db_scan.getMAC())) {
                    wifi.setcompareResult("identical");
                    wifi.setRSSI(wifi.getRSSI() + "OldLevel:"+db_scan.getRSSI());
                }
            }
            resultComparetWifis.add(wifi);
        }

        for (WifiScan db_scan : list_db_data) {
            WifiScanCompared wifidb = new WifiScanCompared();
            if(!listMac.contains(db_scan.getMAC())){
                wifidb.setcompareResult("unknown");
                wifidb.setSSID(db_scan.getSSID());
                wifidb.setRSSI("OldLevel:"+db_scan.getRSSI());
                wifidb.setMAC(db_scan.getMAC());
                resultComparetWifis.add(wifidb);
            }

        }

    }

}
