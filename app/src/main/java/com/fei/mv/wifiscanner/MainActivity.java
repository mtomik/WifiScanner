package com.fei.mv.wifiscanner;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.fei.mv.wifiscanner.model.Record;
import com.fei.mv.wifiscanner.model.WifiScan;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final int MY_PERMISSIONS_ACCESS_COARSE_LOCATION = 1;

    WifiManager wifi;
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
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (!wifi.isWifiEnabled()) {
            Toast.makeText(MainActivity.this, "Enabling Wifi", Toast.LENGTH_SHORT).show();
            wifi.setWifiEnabled(true);
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSIONS_ACCESS_COARSE_LOCATION);
        } else {
            showMeFloor();
        }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSIONS_ACCESS_COARSE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    showMeFloor();
                } else {
                    Toast.makeText(this, R.string.location_permission_not_granted,
                            Toast.LENGTH_LONG).show();
                }
                return;

            default:
                return;
        }
    }

    public void showSaveLocation() {
        scanResults = startScan();
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

    public List<WifiScan> startScan() {
        wifi.startScan();
        List<WifiScan> scans = new ArrayList<>();
        for (ScanResult result : wifi.getScanResults()) {
            WifiScan scan = new WifiScan();
            scan.setSSID(result.SSID);
            scan.setRSSI(String.valueOf(result.level));
            scan.setMAC(result.BSSID);
            scans.add(scan);
        }
        return scans;
    }

    public void showMeFloor() {
        scanResults = startScan();
        String foundFloor = getTheFloor(scanResults);
        locationListFragment.setLocationResultText(foundFloor);
    }

    public String getTheFloor(List<WifiScan> listOfFindWifi){
        //tu vytiahneme vsetky poschodia so vsetkymi wifinami
        List<Record> floors = allRecords;
        Map<String,Integer> scoredFloors = new HashMap<>();
        Map<String,Integer> sortedScoredFloors = new HashMap<>();
        //Toast.makeText(this," z DB nacital "+floors.size()+" poschodi",Toast.LENGTH_SHORT).show();
        List<WifiScan> savedFloorWifi;
        //List<WifiScan> findFloorWifi = new ArrayList<>();

        //testovacie find wifi
        /*
        for (Record flor:floors){
            if ((flor.getSection()+flor.getFloor()).equals("A2")){
                findFloorWifi = flor.getWifiScan();
                break;
            }

        }
        */
        for (Record floor:floors){
            savedFloorWifi = floor.getWifiScan();
            //int floorScore = compareWifis(findFloorWifi,savedFloorWifi);
            int floorScore = compareWifis(listOfFindWifi,savedFloorWifi);
            scoredFloors.put(floor.getSection()+floor.getFloor(),floorScore);
        }
        sortedScoredFloors = sortByComparator(scoredFloors,false);
        List<Entry<String, Integer>> entryListScores = new LinkedList<Entry<String, Integer>>(sortedScoredFloors.entrySet());
        String key = new String();
        if (entryListScores.get(0).getValue() != 0){
            key = entryListScores.get(0).getKey();
        }else
            return "N/A";

        /*
        Integer[] scores = new Integer[floors.size()];
        scoredFloors.values().toArray(scores);
        //zoradit mapu podla score
        Arrays.sort(scores);
        List<Object> listScores = Arrays.asList((Object[]) scores);
        Collections.reverse(listScores);

        //najdenie kluca podla value

        //int counter=0;
        //Vector<String> vec = new Vector<String>();
        String helDesc = new String();
        if ((Integer)listScores.get(0) != 0){
            Iterator<Map.Entry<String,Integer>> iter = scoredFloors.entrySet().iterator();

            while (iter.hasNext()) {
                Map.Entry<String,Integer> entry = iter.next();
                if (entry.getValue().equals(listScores.get(0))) {
                    //counter++;
                    key = entry.getKey();
                    //vec.add(key);
                    //helDesc.concat(key);
                }
            }
        }else
            return "N/A";
        */
        Toast.makeText(this,entryListScores.get(0).getKey().toString()+": "+ entryListScores.get(0).getValue().toString()+"  "+entryListScores.get(1).getKey().toString()+": "+ entryListScores.get(1).getValue().toString()+"  "+
                entryListScores.get(2).getKey().toString()+": "+ entryListScores.get(2).getValue().toString()+"  "+entryListScores.get(3).getKey().toString()+": "+ entryListScores.get(3).getValue().toString()+"  ",Toast.LENGTH_LONG).show();
        return key;
    }

    public int compareWifis(List<WifiScan> listOfFindWifi, List<WifiScan> savedFloorWifi){
        int score = 0;

        //sortovanie podla signalu
        //List<WifiScan> listOfFindWifi = new1.getWifiScan();

        Collections.sort(savedFloorWifi, new Comparator<WifiScan>() {
            @Override
            public int compare(WifiScan wifi1, WifiScan wifi2) {
                return Integer.parseInt(wifi1.getRSSI()) > Integer.parseInt(wifi2.getRSSI()) ? -1 : Integer.parseInt(wifi1.getRSSI()) == Integer.parseInt(wifi2.getRSSI()) ? 0 : 1;
            }
        });

        for (WifiScan wifina:listOfFindWifi){
            score = score + isWifiInList(wifina, savedFloorWifi);
        }
        score = (int) Math.round(Math.sqrt(score));
        return score;
    }

    public int isWifiInList(WifiScan wifina, List<WifiScan> savedFloorWifi){
        int signalTolerance = 5;
        boolean topWifi = true;
        for (WifiScan wifiFromList:savedFloorWifi){
            if (wifiFromList.getIs_used()==1){
                if (wifiFromList.getMAC().equals(wifina.getMAC())){
                    int euklid = (int)Math.pow(Math.abs( Math.abs(Integer.parseInt(wifiFromList.getRSSI())) - Math.abs(Integer.parseInt(wifina.getRSSI())) ),2);
                    /*
                     if( Math.abs( Math.abs(Integer.parseInt(wifiFromList.getRSSI())) - Math.abs(Integer.parseInt(wifina.getRSSI())) ) <= signalTolerance ){
                         if (topWifi){
                              topWifi = false;
                              return 3;
                         }
                         else
                            return 2;
                     }else
                         return 1;
                         */
                    return euklid;
                }
            }
            topWifi = false;
        }
        return 0;
    }

    private Map<String, Integer> sortByComparator(Map<String, Integer> unsortMap, final boolean ascendingOrder)
    {

        List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(unsortMap.entrySet());

        // Sorting the list based on values
        Collections.sort(list, new Comparator<Entry<String, Integer>>()
        {
            public int compare(Entry<String, Integer> o1,
                               Entry<String, Integer> o2)
            {
                if (ascendingOrder)
                {
                    return o1.getValue().compareTo(o2.getValue());
                }
                else
                {
                    return o2.getValue().compareTo(o1.getValue());

                }
            }
        });

        // Maintaining insertion order with the help of LinkedList
        Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
        for (Entry<String, Integer> entry : list)
        {
            sortedMap.put(entry.getKey(), entry.getValue());
        }

        return sortedMap;
    }
}





