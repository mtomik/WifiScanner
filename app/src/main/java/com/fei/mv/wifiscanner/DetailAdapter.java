package com.fei.mv.wifiscanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.fei.mv.wifiscanner.model.Record;
import com.fei.mv.wifiscanner.model.WifiScan;


public class DetailAdapter extends ArrayAdapter<WifiScan> implements View.OnClickListener{


    private Context mContext;
    private SQLHelper sqlHelper;
    private Record record;

    private static class ViewHolder {
        TextView ssidText;
        TextView macText;
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

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        WifiScan dataModel=(WifiScan)object;

    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        final WifiScan dataModel = getItem(position);
        ViewHolder viewHolder;
        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_detail, parent, false);
            viewHolder.ssidText = (TextView) convertView.findViewById(R.id.ssid);
            viewHolder.macText = (TextView) convertView.findViewById(R.id.mac);
            viewHolder.checkBox = (CheckBox) convertView.findViewById(R.id.activateBox);

            // volanie update na db
            viewHolder.checkBox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dataModel.setIs_used( ((CheckBox)v).isChecked() ? 1 : 0 );
                    sqlHelper.updateWifiScanUsed(record.getSection()+record.getFloor(), dataModel);
                    Toast.makeText(mContext, "Zaznam aktualizovany!", Toast.LENGTH_SHORT).show();
                }
            });
            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }


        viewHolder.checkBox.setChecked( dataModel.getIs_used() == 1 );
        viewHolder.ssidText.setText(dataModel.getSSID());
        viewHolder.macText.setText(dataModel.getMAC());

        return convertView;
    }
}
