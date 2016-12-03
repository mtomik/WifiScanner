package com.fei.mv.wifiscanner;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import com.fei.mv.wifiscanner.model.Record;
import com.fei.mv.wifiscanner.model.WifiScan;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class SQLHelper extends SQLiteOpenHelper {
    private Context context;


    private static final String TEXT_TYPE = " TEXT";
    private static final String DATE_TYPE = " DATETIME";
    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "wifiScanAppDB";
    // Logcat tag
    private static final String LOG = SQLHelper.class.getName();


    public static class FeedEntry implements BaseColumns {
        // Table Names
        private static final String TABLE_LOCATION = "locations";
        private static final String TABLE_WS = "wifiscans";

        // Common column names
        private static final String KEY_ID = "id";

        // LOCATION Table - column nmaes
        private static final String COLUMN_LOC_NAME = "location_name";
        private static final String COLUMN_SCAN_DATE = "scan_datetime";

        // WifiScan Table - column names
        private static final String COLUMN_MAC_ADD = "mac_address";
        private static final String COLUMN_SSID = "ssid";
        private static final String COLUMN_RSSI = "rssi";
        private static final String COLUMN_LOC = "location";
        private static final String COLUMN_USED = "used";


    }

    // Vytvorenie tabulky pre pozicie Locations
    private static final String CREATE_TABLE_LOCATION = "CREATE TABLE "
            + FeedEntry.TABLE_LOCATION + "("
            + FeedEntry.KEY_ID + " INTEGER PRIMARY KEY" + COMMA_SEP
            + FeedEntry.COLUMN_LOC_NAME + TEXT_TYPE  + COMMA_SEP
            + FeedEntry.COLUMN_SCAN_DATE + DATE_TYPE + ")";

    // WifiScan tabulka vytvorenie
    private static final String CREATE_TABLE_WS = "CREATE TABLE "
            + FeedEntry.TABLE_WS + "("
            + FeedEntry.KEY_ID + " INTEGER PRIMARY KEY"+ COMMA_SEP
            + FeedEntry.COLUMN_SSID + TEXT_TYPE + COMMA_SEP
            + FeedEntry.COLUMN_MAC_ADD + TEXT_TYPE + COMMA_SEP
            + FeedEntry.COLUMN_RSSI + TEXT_TYPE + COMMA_SEP
            + FeedEntry.COLUMN_USED + INT_TYPE + COMMA_SEP // kedze SQLite nema boolean je tento stlpec int a True = 1 , false=0
            + FeedEntry.COLUMN_LOC +  INT_TYPE + COMMA_SEP
            + " FOREIGN KEY ("+FeedEntry.COLUMN_LOC+") REFERENCES "+FeedEntry.TABLE_LOCATION+"("+FeedEntry.KEY_ID+"))";


    private static final String SQL_DELETE_WS =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_WS;
    private static final String SQL_DELETE_LOCATION =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_LOCATION;

    public SQLHelper(Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //Vytvorenie tabuliek v db podla vopred vyskladanych SQL dotazov
        db.execSQL(CREATE_TABLE_LOCATION);
        db.execSQL(CREATE_TABLE_WS);
        readDefaultData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // update db najskor dropne stare tabulky a potom zavola onCreate
        db.execSQL(SQL_DELETE_WS);
        db.execSQL(SQL_DELETE_LOCATION);
        onCreate(db);
    }

// ------------------------ "WifiScan" metody   ----------------//

    /**
     * Private - pridanie jedneho zaznamu o wifine
     */
    private long insertWS(WifiScan ws, long location_id,SQLiteDatabase db) {
        //SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_SSID, ws.getSSID());
        values.put(FeedEntry.COLUMN_MAC_ADD, ws.getMAC());
        values.put(FeedEntry.COLUMN_RSSI, ws.getRSSI());
        values.put(FeedEntry.COLUMN_USED, ws.getIs_used());
        values.put(FeedEntry.COLUMN_LOC, location_id);

        long ws_id = db.insert(FeedEntry.TABLE_WS, null, values);
        return ws_id;
    }

    /**
     * Private - pridanie listu zaznamov o wifinach ku konkretnej pozicii
     */
    private void insertWifiScans(List<WifiScan> wifiScans, long location_id,SQLiteDatabase db) {
        //SQLiteDatabase db = this.getWritableDatabase();

        for (WifiScan ws : wifiScans) {
            //ws.setIs_used(1);
            long ws_id = insertWS(ws,location_id,db);
        }

    }
    /**
     * Private - ziskanie vsetkych ID wifin zaznamov pre konkretnu poziciu pomocou jej ID
     */
    private List<Long> getWifiScanIDsByLocation(long location_id){
        SQLiteDatabase db = this.getReadableDatabase();
        List<Long> wsIDs = new ArrayList<Long>();
        String selectQuery = "SELECT  * FROM " + FeedEntry.TABLE_WS + " WHERE "
                + FeedEntry.COLUMN_LOC + " = " + location_id;

        Log.e(LOG, selectQuery);
        Cursor c = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                wsIDs.add(c.getLong((c.getColumnIndex(FeedEntry.KEY_ID))));
            } while (c.moveToNext());
        }

        return wsIDs;

    }

    /**
     * Ziskanie vsetkych objektov WifiScan pre konkretnu poziciu pomocou jej nazvu
     */
    public List<WifiScan> getWifiScansByLocation(String location){
        SQLiteDatabase db = this.getWritableDatabase();
        List<WifiScan> wifiScans = new ArrayList<WifiScan>();
        String id = getLocationIDbyName(location,db).toString();
        String selectQueryWS = "SELECT  * FROM " + FeedEntry.TABLE_WS + " WHERE "
                + FeedEntry.COLUMN_LOC + " = " + id;

        Log.e(LOG, selectQueryWS);
        Cursor c = db.rawQuery(selectQueryWS, null);

        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                WifiScan ws = new WifiScan();
                ws.setSSID(c.getString((c.getColumnIndex(FeedEntry.COLUMN_SSID))));
                ws.setMAC((c.getString(c.getColumnIndex(FeedEntry.COLUMN_MAC_ADD))));
                ws.setRSSI(c.getString(c.getColumnIndex(FeedEntry.COLUMN_RSSI)));
                ws.setIs_used(c.getInt(c.getColumnIndex(FeedEntry.COLUMN_USED)));
                // adding to todo list
                wifiScans.add(ws);
            } while (c.moveToNext());
        }

        return wifiScans;

    }

    /**
     * Update na USED - is_used v WifiScan
     * @param locationName
     * @param ws
     */
    public void updateWifiScanUsed(String locationName, WifiScan ws){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_USED, ws.getIs_used());

        System.out.println("WS "+ ws);

        db.update(FeedEntry.TABLE_WS,values,
                FeedEntry.COLUMN_LOC + "=" + getLocationIDbyName(locationName,db)
                        + " AND "+FeedEntry.COLUMN_MAC_ADD + "='" + ws.getMAC()+"'", null);
    }

    /**
     * Update wifin priradenych k nejakej pozici. Najskor zmaze stare zaznamy a potom prida nove-updatnute.
     */
    public void updateWifiScansByLocation(String location, List<WifiScan> wifiScans ){
        SQLiteDatabase db = this.getWritableDatabase();
        List<WifiScan> wifiScansDB = new ArrayList<WifiScan>();
        List<Long> wsIDs = new ArrayList<Long>();
        wifiScansDB = getWifiScansByLocation(location);
        wsIDs = getWifiScanIDsByLocation(getLocationIDbyName(location,db));

        for (Long id : wsIDs) {
            db.delete(FeedEntry.TABLE_WS, FeedEntry.KEY_ID + "="+id,null);
        }
        insertWifiScans(wifiScans,getLocationIDbyName(location,db),db);
    }


// ------------------------ "Record/Location" metody ----------------//

    /**
     * Private - ziskanie aktualneho datumu getDataTime()
     */
    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }
    private Date getDatefromString(String sDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();

        try {
            date = dateFormat.parse(sDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
    *  Pridanie noveho zazanmu pozicie: blok/poschodie/wifiny
    */
    public long addLocationRecord(Record location,SQLiteDatabase db) {
        List<WifiScan> wifiscans = new ArrayList<WifiScan>();
        wifiscans = location.getWifiScan();
       // SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_LOC_NAME, location.getSection()+location.getFloor());
        values.put(FeedEntry.COLUMN_SCAN_DATE, getDateTime());

        long loc_id = db.insert(FeedEntry.TABLE_LOCATION, null, values);

        insertWifiScans(wifiscans,loc_id,db);

        return loc_id;
    }

    public long addLocationRecord(Record location) {
        SQLiteDatabase db = this.getWritableDatabase();
        return addLocationRecord(location, db);
    }

    /**
     *  Private - True/False Overenie ci tabulka obsahuje zaznam o danej pozici blok/poschodie
     */
    private boolean containsLocation(String locName,SQLiteDatabase db) {
        //SQLiteDatabase db = this.getReadableDatabase();
        boolean contains = false;
        String selectQuery = "SELECT  * FROM " + FeedEntry.TABLE_LOCATION
                + " WHERE " + FeedEntry.COLUMN_LOC_NAME + " = '" + locName + "'"
                ;

        Log.e(LOG, selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null)
            cursor.moveToFirst();

        if(cursor.getCount()> 0){
            contains = true;
        }


        return contains;
    }

    /**
     *  Private - Ziskanie ID pozicie podla nazvu pozicie
     */
    private Long getLocationIDbyName(String location_name,SQLiteDatabase db) {
        //SQLiteDatabase db = this.getReadableDatabase();

        String selectQuery = "SELECT  * FROM " + FeedEntry.TABLE_LOCATION + " WHERE "
                + FeedEntry.COLUMN_LOC_NAME + " = '"+ location_name+"'";

        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);

        if (c != null)
            c.moveToFirst();

        return c.getLong((c.getColumnIndex(FeedEntry.KEY_ID)));
    }

    /**
     *  Ziskanie vsetkych objektov typu Record z DB
     */
    public List<Record> getAllLocationRecords(){

        SQLiteDatabase db = this.getWritableDatabase();
        List<WifiScan> wifiScans = new ArrayList<WifiScan>();
        List<Record> records = new ArrayList<Record>();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String selectQueryWS = "SELECT  * FROM " + FeedEntry.TABLE_LOCATION ;

        Log.e(LOG, selectQueryWS);
        Cursor c = db.rawQuery(selectQueryWS, null);

        //prejdem cursorom vsetky zazanamy co vrati select a naplnim ich do pola Record-ov
        if (c.moveToFirst()) {
            do {
                Record r = new Record();
                String loc_name = c.getString(c.getColumnIndex(FeedEntry.COLUMN_LOC_NAME));
                r.setId(c.getInt(c.getColumnIndex(FeedEntry.KEY_ID)));
                r.setSection(loc_name.substring(0,1));
                r.setFloor(loc_name.substring(1,2));
                r.setEdited_at(getDatefromString((c.getString(c.getColumnIndex(FeedEntry.COLUMN_SCAN_DATE)))));
                r.setWifiScan(getWifiScansByLocation(loc_name));

                records.add(r);
            } while (c.moveToNext());
        }
        return records;
    }

    public Record getLocationRecordByName(String name){

        SQLiteDatabase db = this.getWritableDatabase();
        List<WifiScan> wifiScans = new ArrayList<WifiScan>();
        Record r = new Record();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String selectQueryWS = "SELECT  * FROM " + FeedEntry.TABLE_LOCATION ;

        Log.e(LOG, selectQueryWS);
        Cursor c = db.rawQuery(selectQueryWS, null);

        //prejdem cursorom vsetky zazanamy co vrati select a naplnim ich do pola Record-ov
        if (c.moveToFirst()) {
                r.setId(c.getInt(c.getColumnIndex(FeedEntry.KEY_ID)));
                r.setSection(name.substring(0,1));
                r.setFloor(name.substring(1,2));
                r.setEdited_at(getDatefromString((c.getString(c.getColumnIndex(FeedEntry.COLUMN_SCAN_DATE)))));
                r.setWifiScan(getWifiScansByLocation(name));
        }
        return r;
    }

    // ------------------------ "DEFAUL" metody ----------------//

    /**
     *  Metoda na prvu inicializaciu DB z suboru test.json, metodu treba volat v ResultWriteri pri recover() metode
     *  Neskor bude private - kedze default data budeme importovat nejak inac...
     *  TODO treba doriesit import def dat   ===    HOTOVO
     *
     */
    private void readDefaultData(SQLiteDatabase db){
        Gson gson = new Gson();
        List<Record> recordList = new ArrayList<>();

        InputStream is = context.getResources().openRawResource(R.raw.default_data);
        Writer writer = new StringWriter();
        char[] buffer = new char[1024];
        try {
            Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            int n;
            while ((n = reader.read(buffer)) != -1) {
                writer.write(buffer, 0, n);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String jsonString = writer.toString();
        Type listType = new TypeToken<ArrayList<Record>>(){}.getType();
        recordList = gson.fromJson(jsonString,listType);

        for (Record location : recordList) {

            //Podmienka aby sa nepridavali rovnake zaznamy viacej krat tu by asi ani nemusela byt kedze je to on create TODO treba asi odstranit
           if (!containsLocation(location.getSection()+location.getFloor(),db)) {
               long loc_id = addLocationRecord(location,db);
           }
        }

    }
}
