package com.fei.mv.wifiscanner;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fei.mv.wifiscanner.model.Record;
import com.fei.mv.wifiscanner.model.WifiScan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    WifiManager wifi;
    private static final String TAG = "MainActivity";
    TextView resultText;
    ResultWriter writer;
    EditText floorText;
    Spinner sectionSpinner;
    List<Record> allRecords;
    SQLHelper sqlHelper;
    List<WifiScan> scanResults;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sqlHelper = new SQLHelper(this);
        allRecords = sqlHelper.getAllLocationRecords();

        LocationListFragment locationListFragment = new LocationListFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_frame, locationListFragment).commit();

        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        if(!wifi.isWifiEnabled()){
            Toast.makeText(MainActivity.this,"Enabling Wifi", Toast.LENGTH_SHORT).show();
            wifi.setWifiEnabled(true);
        }else{
            // TODO: urob scan wifi a najdi polohu ak nepoznas polohu -> fragment na ulozenie polohy
            //scanResults = startScan(getCurrentFocus());
        }
    }

    public void saveNewLocation(View v){
        scanResults = startScan(getCurrentFocus());

        LocationCreateFragment createFragment = new LocationCreateFragment();
        createFragment.initializeView(scanResults, wifi, sqlHelper);

        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, createFragment).commit();
    }

    public List<Record> getAllRecords() {
        return allRecords;
    }

    public List<WifiScan> startScan(View v){
        List<WifiScan> scan = new ArrayList<>();
//        String section = sectionSpinner.getSelectedItem().toString();
//        String floor = floorText.getText().toString();

        wifi.startScan();
        List<ScanResult> result =  wifi.getScanResults();
        for (ScanResult one : result){
            WifiScan newOne = new WifiScan();
            newOne.setSSID(one.SSID);
            newOne.setRSSI(String.valueOf(one.level));
            newOne.setMAC(one.BSSID);

            scan.add(newOne);
//            resultText.append("BSSID: "+one.BSSID+" SSID: "+one.SSID+" Level:"+one.level+"\n");
        }
        return scan;
//        writer.addNewFloor(section, floor,scan);
//        Toast.makeText(this,"Record for "+section+floor+" added!",Toast.LENGTH_SHORT).show();
//        showMeFloor(scan);
    }

    public void clearOutput(View v){
        this.resultText.setText("");
    }

    public void save(View v){
        writer.save(this);
    }


    public void showMeFloor(List<WifiScan> listWifs){

        ////testovacie data
        Record new1 = new Record();
        new1.setFloor("3");
        new1.setSection("A");
        //List<WifiScan> listWifs = new ArrayList<>();
        /*
        for (int i=0; i < 10; i++){
            WifiScan wif = new WifiScan();
            wif.setRSSI(Integer.toString(ThreadLocalRandom.current().nextInt(-100, 0 + 1)));
            listWifs.add(wif);
        }*/
        new1.setWifiScan(listWifs);
        ////

        //tu pride iba list zdetekovanych wifin
        List<WifiScan> listOfFindWifi = new1.getWifiScan();
        Collections.sort(listOfFindWifi, new Comparator<WifiScan>() {
            @Override
            public int compare(WifiScan wifi1, WifiScan wifi2) {
                return Integer.parseInt(wifi1.getRSSI()) > Integer.parseInt(wifi2.getRSSI()) ? -1 : Integer.parseInt(wifi1.getRSSI()) == Integer.parseInt(wifi2.getRSSI()) ? 0 : 1;
            }
        });
        Record foundFloor = getTheFloor(listOfFindWifi);
        new1.getWifiScan();
    }

    public Record getTheFloor(List<WifiScan> listOfFindWifi){
        //tu vytiahneme vsetky poschodia s 3 najsilnejsimi wifinami alebo aj  so vsetkymi, tu si ich uz vieme vysortovat
        List<Record> floors = new ArrayList<>();
        // test data
        Record new1 = new Record();
        new1.setFloor("4");
        new1.setSection("B");
        //tu som pridal tie iste wifiny co zdetekovalo, aby som si to otestoval, ale nechapem preco to porovnanie co je nizsie nesedi, MAC adresy su rovnake, ale vrati false
        new1.setWifiScan(listOfFindWifi);
        floors.add(new1);
        //

        for (Record floor:floors){
            List<WifiScan> savedFloorWifi = floor.getWifiScan();
            if(compareWifis(listOfFindWifi,savedFloorWifi)){
                return floor;
            }
        }
        return null;
    }

    public boolean compareWifis(List<WifiScan> listOfFindWifi, List<WifiScan> savedFloorWifi){
        for(int i=0; i < 3; i++){
            if(!listOfFindWifi.get(i).getMAC().equals(savedFloorWifi.get(i).getMAC()));
                return false;
        }
        return true;
    }
}





