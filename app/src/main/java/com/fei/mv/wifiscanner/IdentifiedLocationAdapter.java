package com.fei.mv.wifiscanner;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.fei.mv.wifiscanner.model.WifiScan;
import com.fei.mv.wifiscanner.model.WifiScanCompared;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tomasstrba on 1.12.16.
 */

public class IdentifiedLocationAdapter extends ArrayAdapter<WifiScanCompared> {
    private List<WifiScanCompared> comparedWifiList;
    private Context context;


    public IdentifiedLocationAdapter(Context context, List<WifiScanCompared> listData) {
        super(context, R.layout.list_item_wifi, listData);
        this.comparedWifiList = listData;
        this.context = context;

    }

    private static class RowElementsHolder {
        TextView ssidText;
        TextView macText;
        TextView rssiText;
        ImageView compareStatus;
        //TextView compareStatus;
        //CheckBox chBoxIsUsed;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        RowElementsHolder rowElementHolder = null;
        ;

        Log.v("ConvertView", String.valueOf(position));

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_identified_location, parent, false);

            rowElementHolder = new RowElementsHolder();
            rowElementHolder.ssidText = (TextView) convertView.findViewById(R.id.wifi_infoSSDI);
            rowElementHolder.macText = (TextView) convertView.findViewById(R.id.wifi_infoMAC);
            rowElementHolder.rssiText = (TextView) convertView.findViewById(R.id.wifi_infoRSSI);
            rowElementHolder.compareStatus = (ImageView) convertView.findViewById(R.id.compare_Status_icon);

            convertView.setTag(rowElementHolder);
            /**
             holder.name.setOnClickListener( new View.OnClickListener() {
             public void onClick(View v) {
             CheckBox cb = (CheckBox) v ;
             Country country = (Country) cb.getTag();
             Toast.makeText(getApplicationContext(),
             "Clicked on Checkbox: " + cb.getText() +
             " is " + cb.isChecked(),
             Toast.LENGTH_LONG).show();
             country.setSelected(cb.isChecked());
             }
             });
             **/
        } else {
            rowElementHolder = (RowElementsHolder) convertView.getTag();
        }

        WifiScanCompared wifi = comparedWifiList.get(position);
        rowElementHolder.ssidText.setText(wifi.getSSID());
        rowElementHolder.macText.setText(wifi.getMAC());
        rowElementHolder.rssiText.setText(wifi.getRSSI());
        if(wifi.getcompareResult()== "identical"){
            Drawable myIcon = context.getResources().getDrawable(R.drawable.ic_action_check);
            rowElementHolder.compareStatus.setImageDrawable(myIcon);
        }
        if(wifi.getcompareResult()== "new"){
            Drawable myIcon = context.getResources().getDrawable(R.drawable.ic_action_new);
            rowElementHolder.compareStatus.setImageDrawable(myIcon);
        }
        if(wifi.getcompareResult()== "unknown"){
            Drawable myIcon = context.getResources().getDrawable(R.drawable.ic_action_unknown);
            rowElementHolder.compareStatus.setImageDrawable(myIcon);
        }

        //rowElementHolder.name.setChecked(wifi.isSelected());
        //rowElementHolder.name.setTag(wifi);
        return convertView;


    }


}