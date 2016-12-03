package com.fei.mv.wifiscanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.fei.mv.wifiscanner.model.WifiScan;

import java.util.List;

/**
 * Created by juraj on 3.12.2016.
 */

public class LocationCreateAdapter extends ArrayAdapter<WifiScan> implements View.OnClickListener {

    private Context mContext;
    private List<WifiScan> scanResults;

    private static class ViewHolder {
        TextView ssidText;
        TextView macText;
        CheckBox checkBox;
    }

    public LocationCreateAdapter(Context context, List<WifiScan> scanResults) {
        super(context, R.layout.list_item_detail, scanResults);
        this.scanResults = scanResults;
    }

    @Override
    public void onClick(View v) {
//        int position=(Integer) v.getTag();
//        Object object= getItem(position);
//        WifiScan dataModel=(WifiScan)object;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        //final WifiScan dataModel = getItem(position);
        ViewHolder viewHolder;
        //final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_detail, parent, false);
            viewHolder.ssidText = (TextView) convertView.findViewById(R.id.ssid);
            viewHolder.macText = (TextView) convertView.findViewById(R.id.mac);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.activateBox);

            convertView.setTag(viewHolder);


            viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    WifiScan scan = (WifiScan) cb.getTag();
                    scan.setIs_used(cb.isChecked() ? 1 : 0 );
                    updateScanResult(scan);
                }
            });


            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final WifiScan dataModel = scanResults.get(position);
        viewHolder.checkBox.setChecked( dataModel.getIs_used() == 1 );
        viewHolder.ssidText.setText(dataModel.getSSID());
        viewHolder.macText.setText(dataModel.getMAC());
        viewHolder.checkBox.setTag(dataModel);

        return convertView;
    }

    public void updateScanResult(WifiScan scan){
        for(WifiScan w : scanResults){
            if(scan.equals(w)){
                w.setIs_used(scan.getIs_used());
                break;
            }
        }
        ((MainActivity)getContext()).scanResults = scanResults;
    }


}
