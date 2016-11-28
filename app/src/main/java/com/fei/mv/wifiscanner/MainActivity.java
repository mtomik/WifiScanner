package com.fei.mv.wifiscanner;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fei.mv.wifiscanner.model.Record;
import com.fei.mv.wifiscanner.model.WifiScan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

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
    LocationListFragment locationListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sqlHelper = new SQLHelper(this);
        allRecords = sqlHelper.getAllLocationRecords();

        locationListFragment = new LocationListFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.main_frame, locationListFragment).commit();

        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        if (!wifi.isWifiEnabled()) {
            Toast.makeText(MainActivity.this,"Enabling Wifi", Toast.LENGTH_SHORT).show();
            wifi.setWifiEnabled(true);
        } else {
            // TODO: urob scan wifi a najdi polohu ak nepoznas polohu -> fragment na ulozenie polohy
            //scanResults = startScan(getCurrentFocus());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        showMeFloor();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_location:
                showSaveLocation();
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    public void showSaveLocation() {
        scanResults = startScan(getCurrentFocus());
        LocationCreateFragment createFragment = new LocationCreateFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, createFragment)
                .addToBackStack("save_location").commit();
    }

    public List<Record> getAllRecords() {
        return allRecords;
    }

    public Record getRecordById(int id){
        for (Record record : allRecords) {
            if (record.getId() == id) {
                return record;
            }
        }
        return null;
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


    public void showMeFloor(){
         /*
        for (int i=0; i < 10; i++){
            WifiScan wif = new WifiScan();
            wif.setRSSI(Integer.toString(ThreadLocalRandom.current().nextInt(-100, 0 + 1)));
            listWifs.add(wif);
        }*/

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

        /*
        //sortovanie podla signalu
        List<WifiScan> listOfFindWifi = new1.getWifiScan();
        Collections.sort(listOfFindWifi, new Comparator<WifiScan>() {
            @Override
            public int compare(WifiScan wifi1, WifiScan wifi2) {
                return Integer.parseInt(wifi1.getRSSI()) > Integer.parseInt(wifi2.getRSSI()) ? -1 : Integer.parseInt(wifi1.getRSSI()) == Integer.parseInt(wifi2.getRSSI()) ? 0 : 1;
            }
        });
        */
        String foundFloor = getTheFloor(scan);
        locationListFragment.setLocationResultText(foundFloor);
    }

    public String getTheFloor(List<WifiScan> listOfFindWifi){
        //tu vytiahneme vsetky poschodia so vsetkymi wifinami
        List<Record> floors = allRecords;
        Map<String,Integer> scoredFloors = new HashMap<>();

        //Toast.makeText(this," z DB nacital "+floors.size()+" poschodi",Toast.LENGTH_SHORT).show();
        List<WifiScan> savedFloorWifi;
        //List<WifiScan> findFloorWifi = new ArrayList<>();

        //testovacie find wifi
        /*
        for (Record flor:floors){
            findFloorWifi = flor.getWifiScan();
            break;
        }
        */
        for (Record floor:floors){
            savedFloorWifi = floor.getWifiScan();
//            int floorScore = compareWifis(findFloorWifi,savedFloorWifi);
            int floorScore = compareWifis(listOfFindWifi,savedFloorWifi);
            scoredFloors.put(floor.getSection()+floor.getFloor(),floorScore);
        }
        Integer[] scores = new Integer[floors.size()];
        scoredFloors.values().toArray(scores);
        //zoradit mapu podla score
        Arrays.sort(scores);
        List<Object> listScores = Arrays.asList((Object[]) scores);
        Collections.reverse(listScores);

        //najdenie kluca podla value
        String key = new String();
        if ((Integer)listScores.get(0) != 0){
            Iterator<Map.Entry<String,Integer>> iter = scoredFloors.entrySet().iterator();

            while (iter.hasNext()) {
                Map.Entry<String,Integer> entry = iter.next();
                if (entry.getValue().equals(listScores.get(0))) {
                    key = entry.getKey();
                }
            }
        }else
            return "N/A";
        return key;
    }

    public int compareWifis(List<WifiScan> listOfFindWifi, List<WifiScan> savedFloorWifi){
        int score = 0;
        for (WifiScan wifina:listOfFindWifi){
            if (isWifiInList(wifina, savedFloorWifi))
                score++;
        }
        return score;
    }

    public boolean isWifiInList(WifiScan wifina, List<WifiScan> savedFloorWifi){
        for (WifiScan wifiFromList:savedFloorWifi){
            if (wifiFromList.getMAC().equals(wifina.getMAC()))
                return true;
        }
        return false;
    }

}





