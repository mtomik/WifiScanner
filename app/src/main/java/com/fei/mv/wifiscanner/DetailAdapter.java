package com.fei.mv.wifiscanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.fei.mv.wifiscanner.model.Record;
import com.fei.mv.wifiscanner.model.WifiScan;


public class DetailAdapter extends ArrayAdapter<WifiScan> implements View.OnClickListener{


    private Context mContext;
    private SQLHelper sqlHelper;
    private Record record;

    private static class ViewHolder {
        TextView ssidText;
        TextView macText;
        TextView signal;
        CheckBox checkBox;
    }

    public DetailAdapter(Record record, Context context){
        super(context,R.layout.list_item_detail,record.getWifiScan());
        mContext = context;
        this.record = record;
        this.sqlHelper = ((MainActivity)getContext()).sqlHelper;
    }


    @Override
    public void onClick(View v) {

//        int position=(Integer) v.getTag();
//        Object object= getItem(position);
//        WifiScan dataModel=(WifiScan)object;

    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        ViewHolder viewHolder;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_detail, parent, false);
            viewHolder.ssidText = (TextView) convertView.findViewById(R.id.ssid);
            viewHolder.macText = (TextView) convertView.findViewById(R.id.mac);
            viewHolder.signal = (TextView) convertView.findViewById(R.id.signal);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.activateBox);

            convertView.setTag(viewHolder);

            // volanie update na db
            viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox cb = (CheckBox) v;
                    WifiScan scan = (WifiScan) cb.getTag();
                    scan.setIs_used(cb.isChecked() ? 1 : 0 );
                    sqlHelper.updateWifiScanUsed(record.getSection()+record.getFloor(), scan);
                    //Toast.makeText(mContext, "Zaznam aktualizovany! "+position, Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        final WifiScan dataModel = record.getWifiScan().get(position);
        viewHolder.checkBox.setChecked( dataModel.getIs_used() == 1 );
        viewHolder.ssidText.setText(dataModel.getSSID());
        viewHolder.macText.setText(dataModel.getMAC());
        viewHolder.signal.setText(dataModel.getRSSI());
        viewHolder.checkBox.setTag(dataModel);

        return convertView;
    }
}
