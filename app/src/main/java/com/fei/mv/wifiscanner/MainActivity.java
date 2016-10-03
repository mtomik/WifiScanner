package com.fei.mv.wifiscanner;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.TextureView;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.fei.mv.wifiscanner.model.WifiScan;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    WifiManager wifi;
    private static final String TAG = "MainActivity";
    TextView resultText;
    ResultWriter writer;
    EditText floorText;
    Spinner sectionSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resultText = (TextView) findViewById(R.id.result);
        floorText = (EditText) findViewById(R.id.floorText);
       // sectionText = (EditText) findViewById(R.id.sectionText);


        sectionSpinner = (Spinner) findViewById(R.id.sectionSpinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sections, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sectionSpinner.setAdapter(adapter);

        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        if(!wifi.isWifiEnabled()){
            Toast.makeText(MainActivity.this,"Enabling Wifi", Toast.LENGTH_SHORT).show();
            wifi.setWifiEnabled(true);
        }

        writer = new ResultWriter("test.json",this);

    }


    public void startScan(View v){
        List<WifiScan> scan = new ArrayList<>();
        String section = sectionSpinner.getSelectedItem().toString();
        String floor = floorText.getText().toString();

        wifi.startScan();
        List<ScanResult> result =  wifi.getScanResults();
        for (ScanResult one : result){
            WifiScan newOne = new WifiScan();
            newOne.setSSID(one.SSID);
            newOne.setRSSI(String.valueOf(one.level));

            scan.add(newOne);
            resultText.append("BSSID: "+one.BSSID+" SSID: "+one.SSID+" Level:"+one.level+"\n");
        }

        writer.addNewFloor(section, floor,scan);
        Toast.makeText(this,"Record for "+section+floor+" added!",Toast.LENGTH_SHORT).show();
    }

    public void clearOutput(View v){
        this.resultText.setText("");
    }

    public void save(View v){
        writer.save(this);
    }
}
