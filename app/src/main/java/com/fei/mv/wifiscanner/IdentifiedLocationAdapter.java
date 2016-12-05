package com.fei.mv.wifiscanner;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.fei.mv.wifiscanner.model.WifiScanCompared;
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
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        RowElementsHolder rowElementHolder = null;

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
        } else {
            rowElementHolder = (RowElementsHolder) convertView.getTag();
        }

        WifiScanCompared wifi = comparedWifiList.get(position);
        rowElementHolder.ssidText.setText(wifi.getSSID());
        rowElementHolder.macText.setText(wifi.getMAC());
        if(wifi.getRSSI() == "-100"){
            rowElementHolder.rssiText.setText("Aktuálny signál: " +"N/A"+ ", Predošlý signál: "+ wifi.getRSSIold());
        }else if(wifi.getRSSIold() == "-100"){
            rowElementHolder.rssiText.setText("Aktuálny signál: " + wifi.getRSSI()+ ", Predošlý signál: "+ "N/A");
        }else{
            rowElementHolder.rssiText.setText("Aktuálny signál: " + wifi.getRSSI()+ ", Predošlý signál: "+ wifi.getRSSIold());
        }

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

        return convertView;
    }

}
