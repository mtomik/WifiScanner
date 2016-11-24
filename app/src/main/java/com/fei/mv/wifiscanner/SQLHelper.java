package com.fei.mv.wifiscanner;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.widget.SimpleCursorAdapter;

import com.fei.mv.wifiscanner.model.Record;
import com.fei.mv.wifiscanner.model.WifiScan;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class SQLHelper extends SQLiteOpenHelper {

    private static final String TEXT_TYPE = " TEXT";
    private static final String DATE_TYPE = " DATETIME";
    private static final String INT_TYPE = " INTEGER";
    private static final String COMMA_SEP = ",";

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "wifiScanAppDB";


    public static class FeedEntry implements BaseColumns {
        // Table Names
        private static final String TABLE_LOCATION = "locations";
        private static final String TABLE_AP = "accesspoints";
       // private static final String TABLE_LOCATION_AP = "location_aps";

        // Common column names
        private static final String KEY_ID = "id";

        // LOCATION Table - column nmaes
        private static final String COLUMN_LOC_NAME = "location_name";
        //private static final String COLUMN_SECTION = "section";
       // private static final String COLUMN_FLOOR = "floor";

        // ACCESSPOINT Table - column names
        private static final String COLUMN_MAC_ADD = "mac_address";
        private static final String COLUMN_SSID = "ssid";
        private static final String COLUMN_RSSI = "rssi";
        private static final String COLUMN_LOC = "location";
        private static final String COLUMN_SCAN_DATE = "scan_datetime";

        // LOCATION_ACCESSPOINTS Table - column names
        //private static final String COLUMN_LOCATION_ID = "location_id";
      //  private static final String COLUMN_AP_ID = "accesspoint_id";
    }

    // Table Create Statements
    // Todo table create statement
    private static final String CREATE_TABLE_LOCATION = "CREATE TABLE "
            + FeedEntry.TABLE_LOCATION + "("
            + FeedEntry.KEY_ID + " INTEGER PRIMARY KEY" + COMMA_SEP //+ FeedEntry.COLUMN_SECTION + TEXT_TYPE + COMMA_SEP
            + FeedEntry.COLUMN_LOC_NAME + TEXT_TYPE + ")";

    // accesspoint  table create statement
    private static final String CREATE_TABLE_AP = "CREATE TABLE "
            + FeedEntry.TABLE_AP + "("
            + FeedEntry.KEY_ID + " INTEGER PRIMARY KEY"+ COMMA_SEP
            + FeedEntry.COLUMN_SSID + TEXT_TYPE + COMMA_SEP
            + FeedEntry.COLUMN_MAC_ADD + TEXT_TYPE + COMMA_SEP
            + FeedEntry.COLUMN_RSSI + TEXT_TYPE + COMMA_SEP
            + FeedEntry.COLUMN_LOC + INT_TYPE + COMMA_SEP
            + FeedEntry.COLUMN_SCAN_DATE + DATE_TYPE +")";

    // todo_tag table create statement
    /*private static final String CREATE_TABLE_LOCATION_AP = "CREATE TABLE "
            + FeedEntry.TABLE_LOCATION_AP + "("
            + FeedEntry.KEY_ID + " INTEGER PRIMARY KEY,"
            + FeedEntry.COLUMN_LOCATION_ID + INT_TYPE + COMMA_SEP
            + FeedEntry.COLUMN_AP_ID + INT_TYPE + COMMA_SEP
            + FeedEntry.COLUMN_SCAN_DATE + DATE_TYPE + ")";
*/


    private static final String SQL_DELETE_AP =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_AP;
    private static final String SQL_DELETE_LOCATION =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_LOCATION;
   // private static final String SQL_DELETE_LOCATION_AP =
   //         "DROP TABLE IF EXISTS " + FeedEntry.TABLE_LOCATION_AP;

    public SQLHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_AP);
        db.execSQL(CREATE_TABLE_LOCATION);
       // db.execSQL(CREATE_TABLE_LOCATION_AP);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL(SQL_DELETE_AP);
        db.execSQL(SQL_DELETE_LOCATION);
    //    db.execSQL(SQL_DELETE_LOCATION_AP);

        // create new tables
        onCreate(db);
    }

    private String getDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
    }

    public long createAP(WifiScan ap, long location_id) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_SSID, ap.getSSID());
        values.put(FeedEntry.COLUMN_MAC_ADD, ap.getMAC());
        values.put(FeedEntry.COLUMN_RSSI, ap.getRSSI());
        values.put(FeedEntry.COLUMN_LOC, location_id);
        values.put(FeedEntry.COLUMN_SCAN_DATE, getDateTime());

        // insert row
        long todo_id = db.insert(FeedEntry.TABLE_AP, null, values);

        return todo_id;
    }


    public long addLocationRecord(Record location) {
        List<WifiScan> accesspoints = new ArrayList<WifiScan>();
        accesspoints = location.getWifiScan();
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FeedEntry.COLUMN_LOC_NAME, location.getFloor()+location.getSection());

        long loc_id = db.insert(FeedEntry.TABLE_LOCATION, null, values);

        for (WifiScan ap : accesspoints) {
            //if (!db.containsTweet(tweet.getId())) {
            long ap_id = createAP(ap,loc_id);

            //}
        }

        return loc_id;
    }
    public boolean containsLocation(String locName) {
        SQLiteDatabase db = this.getReadableDatabase();
        boolean contains = false;
        String selectQuery = "SELECT  * FROM " + FeedEntry.TABLE_LOCATION
                + " WHERE " + FeedEntry.COLUMN_LOC_NAME + " = '" + locName + "'"
                ;

        //Log.e(LOG, selectQuery);

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor != null)
            contains=true;
       // cursor.moveToFirst();
       // String id = cursor.getString(0);
       // String name = cursor.getString(1);
                //c.getString((c.getColumnIndex(FeedEntry.COLUMN_LOC_NAME)));
       // cursor.close();
        return contains;
    }


    public void addDefaultData(List<Record> locRecord ){
        SQLiteDatabase db = this.getWritableDatabase();
        for (Record location : locRecord) {
            if (!containsLocation(location.getSection()+location.getFloor())) {
            long ap_id = addLocationRecord(location);

            }
        }
    }

}
