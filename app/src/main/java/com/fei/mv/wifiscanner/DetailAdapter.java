package com.fei.mv.wifiscanner;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.fei.mv.wifiscanner.model.WifiScan;

import java.util.List;


public class DetailAdapter extends ArrayAdapter<WifiScan> implements View.OnClickListener{

    private List<WifiScan> dataSet;

    private Context mContext;

    // View lookup cache
    private static class ViewHolder {
        TextView ssidText;
        TextView macText;
    }

    public DetailAdapter(List<WifiScan> data, Context context){
        super(context,R.layout.list_item_detail,data);
        mContext = context;
        dataSet = data;
    }


    @Override
    public void onClick(View v) {

        int position=(Integer) v.getTag();
        Object object= getItem(position);
        WifiScan dataModel=(WifiScan)object;

        switch (v.getId())
        {
//            case R.id.item_info:
//                Snackbar.make(v, "Release date " +dataModel.getFeature(), Snackbar.LENGTH_LONG)
//                        .setAction("No action", null).show();
//                break;
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        WifiScan dataModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view.
        ViewHolder viewHolder; // view lookup cache stored in tag
        final View result;

        if (convertView == null) {

            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.list_item_detail, parent, false);
            viewHolder.ssidText = (TextView) convertView.findViewById(R.id.ssid);
            viewHolder.macText = (TextView) convertView.findViewById(R.id.mac);


            result=convertView;

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
            result=convertView;
        }

        // Add animations
        // http://www.journaldev.com/10416/android-listview-with-custom-adapter-example-tutorial
//        Animation animation = AnimationUtils.loadAnimation(mContext, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
//        result.startAnimation(animation);
//        lastPosition = position;

        viewHolder.ssidText.setText(dataModel.getSSID());
        viewHolder.macText.setText(dataModel.getMAC());

        // Return the completed view to render on screen
        return convertView;
    }
}
