package com.fei.mv.wifiscanner;


import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.fei.mv.wifiscanner.model.Record;
import com.fei.mv.wifiscanner.model.WifiScan;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.Predicate;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


class ResultWriter {

    private Gson gson;
    private List<Record> recordList;
    private File backup;
    private Context context;


     ResultWriter(String fileName, Context context){
         this.context = context;
         this.gson = new GsonBuilder().create();
         this.recordList = new ArrayList<>();
         this.backup =  new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);

         if(backup.exists() && !backup.isDirectory()){
             recover();
         }


    }

    private void recover(){

        String json = null;

        try(FileInputStream fis = new FileInputStream(this.backup);
            FileChannel fc = fis.getChannel() ){

            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());

            json = Charset.defaultCharset().decode(bb).toString();

            Type listType = new TypeToken<ArrayList<Record>>(){}.getType();
            this.recordList = gson.fromJson(json,listType);

            Toast.makeText(context,"Data from "+backup.getName()+" loaded! Data count: "+recordList.size(),Toast.LENGTH_LONG).show();
        }
        catch(IOException e){
            Log.e("Exception", "File write failed: " + e.toString());
            Toast.makeText(this.context,"Error loading file: "+this.backup.getAbsolutePath(),Toast.LENGTH_SHORT).show();
        }

    }

     void addNewFloor(final String section, final String floor, List<WifiScan> scan){

        Record r = new Record();
        r.setFloor(floor);
        r.setSection(section);
        r.setWifiScan(scan);

        Predicate condition = new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return Objects.equals(((Record) object).getFloor(),floor) && Objects.equals(((Record) object).getSection(), section);
            }
        };

        Collection result = CollectionUtils.select(recordList, condition);
        if(result != null && !result.isEmpty()){
            this.recordList.removeAll(result);
        }
        this.recordList.add(r);
    }

    void save(Context context){


        try (FileWriter fw = new FileWriter(this.backup)){
            fw.write(gson.toJson(this.recordList));
            Toast.makeText(context,"Data saved into: "+this.backup.getAbsolutePath(),Toast.LENGTH_LONG).show();
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
            Toast.makeText(this.context,"Error saving file: "+this.backup.getAbsolutePath(),Toast.LENGTH_SHORT).show();

        }
    }


}
