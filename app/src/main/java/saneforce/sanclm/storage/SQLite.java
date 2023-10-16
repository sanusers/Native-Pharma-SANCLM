package saneforce.sanclm.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

import org.json.JSONArray;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class SQLite extends SQLiteOpenHelper {

    public static final String DATA_BASE_NAME = "san_clm.db";
    public static final String LOGIN_TABLE = "login_table";
    public static final String LOGIN_DATA = "login_data";
    private static final String LINECHAT_DATA = "LINECHAT_DATA";


    //Master Sync
    public static final String MASTER_SYNC_TABLE = "master_sync_data";
    public static final String MASTER_KEY = "master_key";
    public static final String MASTER_VALUE = "master_value";


    private static final String LINECHAR_CUSTCODE = "LINECHAR_CUSTCODE";
    private static final String LINECHAR_CUSTTYPE = "LINECHAR_CUSTTYPE";
    private static final String LINECHAR_DCR_DT = "LINECHAR_DCR_DT";
    private static final String LINECHAR_MONTH_NAME = "LINECHAR_MONTH_NAME";
    private static final String LINECHAR_MNTH = "LINECHAR_MNTH";
    private static final String LINECHAR_YR = "LINECHAR_YR";
    private static final String LINECHAR_CUSTNAME = "LINECHAR_CUSTNAME";
    private static final String LINECHAR_TOWN_CODE = "LINECHAR_TOWN_CODE";
    private static final String LINECHAR_TOWN_NAME = "LINECHAR_TOWN_NAME";
    private static final String LINECHAR_DCR_FLAG = "LINECHAR_DCR_FLAG";
    private static final String LINECHAR_SF_CODE = "LINECHAR_SF_CODE";
    private static final String LINECHAR_TRANS_SLNO = "LINECHAR_TRANS_SLNO";
    private static final String LINECHAR_AMSLNO = "LINECHAR_AMSLNO";


    public SQLite (@Nullable Context context) {
        super(context, DATA_BASE_NAME, null, 1);
    }

    @Override
    public void onCreate (SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + LOGIN_TABLE + "(" + LOGIN_DATA + " text" + ")");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + MASTER_SYNC_TABLE + "(" + MASTER_KEY + " text," + MASTER_VALUE + " text" + ")");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + LINECHAT_DATA + " (" +
                LINECHAR_CUSTCODE + " TEXT, " +
                LINECHAR_CUSTTYPE + " TEXT, " +
                LINECHAR_DCR_DT + " TEXT, " +
                LINECHAR_MONTH_NAME + " TEXT, " +
                LINECHAR_MNTH + " TEXT, " +
                LINECHAR_YR + " TEXT, " +
                LINECHAR_CUSTNAME + " TEXT, " +
                LINECHAR_TOWN_CODE + " TEXT, " +
                LINECHAR_TOWN_NAME + " TEXT, " +
                LINECHAR_DCR_FLAG + " TEXT, " +
                LINECHAR_SF_CODE + " TEXT, " +
                LINECHAR_TRANS_SLNO + " TEXT, " +
                LINECHAR_AMSLNO + " TEXT);");

    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + LOGIN_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + MASTER_SYNC_TABLE);

        onCreate(db);
    }

    public void deleteAllTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + LOGIN_TABLE);
        db.execSQL("DELETE FROM " + MASTER_SYNC_TABLE);

        db.close();
    }


    //------------------------ Login ------------------------------------
    public void saveLoginData(String data){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LOGIN_DATA,data);
        int updated = db.update(LOGIN_TABLE,contentValues,null,null);
        if (updated <= 0){
            db.insert(LOGIN_TABLE,null,contentValues);
        }
        db.close();
    }

    public Cursor getLoginData(){
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT * FROM " + LOGIN_TABLE, null);
    }

    //-------------------------- Master ----------------------------------------
    public void saveMasterSyncData(String key, String values) {

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MASTER_KEY, key);
        contentValues.put(MASTER_VALUE, values);

        String[] args = new String[]{key};
        int updated = db.update(MASTER_SYNC_TABLE,contentValues,MASTER_KEY + "=?",args);
        if (updated <= 0){
            db.insert(MASTER_SYNC_TABLE,null,contentValues);
        }
        db.close();
    }

    public JSONArray getMasterSyncDataByKey(String key)  {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + MASTER_SYNC_TABLE + " where " + MASTER_KEY + "=" + "'" + key + "';", null);
        String data = "";
        if (cursor.moveToNext()){
            data = cursor.getString(1);
        }

        JSONArray jsonArray = new JSONArray();
        try {
            if (!data.equals("")){
                jsonArray = new JSONArray(data.toString());
            }
        }catch (Exception exception){
            exception.printStackTrace();
        }
        cursor.close();
        return jsonArray;
    }

//    public void saveDrMaster(String hqCode,String drList){
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues contentValues = new ContentValues();
//        contentValues.put(HQ_CODE, hqCode);
//        contentValues.put(DOCTOR_DATA,drList);
//
//        String[] args = new String[]{hqCode};
//        int updated = db.update(DOCTOR_MASTER_TABLE, contentValues, HQ_CODE + "=?", args);
//        if (updated <= 0){
//            db.insert(DOCTOR_MASTER_TABLE,null,contentValues);
//        }
//        db.close();
//    }

//    public JSONArray getDrMaster(String hqCode){
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT * FROM " + DOCTOR_MASTER_TABLE + "WHERE " + HQ_CODE + "=" + "'" + hqCode + "';", null);
//        String data = "";
//        if (cursor.moveToNext()){
//            data = cursor.getString(1);
//        }
//
//        JSONArray jsonArray = new JSONArray();
//        try {
//            if (!data.equals("")){
//                jsonArray = new JSONArray(data.toString());
//            }
//        }catch (Exception exception){
//            exception.printStackTrace();
//        }
//        cursor.close();
//        return jsonArray;
//    }


    // insertdata Linechart
    public void insertLinecharData(String custCode, String custType, String dcrDt, String monthName,
                           String mnth, String yr, String custName, String townCode,
                           String townName, String dcrFlag, String sfCode, String transSlNo,
                           String amslNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(LINECHAR_CUSTCODE, custCode);
        values.put(LINECHAR_CUSTTYPE, custType);
        values.put(LINECHAR_DCR_DT, dcrDt);
        values.put(LINECHAR_MONTH_NAME, monthName);
        values.put(LINECHAR_MNTH, mnth);
        values.put(LINECHAR_YR, yr);
        values.put(LINECHAR_CUSTNAME, custName);
        values.put(LINECHAR_TOWN_CODE, townCode);
        values.put(LINECHAR_TOWN_NAME, townName);
        values.put(LINECHAR_DCR_FLAG, dcrFlag);
        values.put(LINECHAR_SF_CODE, sfCode);
        values.put(LINECHAR_TRANS_SLNO, transSlNo);
        values.put(LINECHAR_AMSLNO, amslNo);

        db.insert(LINECHAT_DATA, null, values);
        db.close();
    }


    public void clearLinecharTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + LINECHAT_DATA);
        db.close();
    }


    public int getcurrentmonth_calls_count(String CustType){

        SQLiteDatabase db = this.getReadableDatabase();

        String currentYearMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());

        String query = "SELECT COUNT(*) FROM " + LINECHAT_DATA +
                " WHERE strftime('%Y-%m', " + LINECHAR_DCR_DT + ") = '" + currentYearMonth + "'" +
                " AND " + LINECHAR_CUSTTYPE + " = '" + CustType + "'";

        Cursor cursor = db.rawQuery(query, null);

        int count = 0;
        if (cursor != null) {
            cursor.moveToFirst();
            count = cursor.getInt(0);
            cursor.close();
        }

        db.close();

        return count;
    };



    public int gettotalcallscount(String CustType){

        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT COUNT(*) FROM " + LINECHAT_DATA +
                " WHERE " + LINECHAR_CUSTTYPE + " = '" + CustType + "'";

        Cursor cursor = db.rawQuery(query, null);

        int count = 0;
        if (cursor != null) {
            cursor.moveToFirst();
            count = cursor.getInt(0);
            cursor.close();
        }

        db.close();

        return count;
    };





    public int getHalfMonthDataCount(String startDate, String endDate, String custType) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT COUNT(*) FROM " + LINECHAT_DATA +
                " WHERE " + LINECHAR_DCR_DT + " >= '" + startDate + "' " +
                " AND " + LINECHAR_DCR_DT + " <= '" + endDate + "' " +
                " AND " + LINECHAR_CUSTTYPE + " = '" + custType + "'";

        Cursor cursor = db.rawQuery(query, null);

        int count = 0;
        if (cursor != null) {
            cursor.moveToFirst();
            count = cursor.getInt(0);
            cursor.close();
        }

        db.close();

        return count;
    }

    public boolean isMonthDataAvailableForCustType(String custType, String month) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT COUNT(*) FROM " + LINECHAT_DATA +
                " WHERE " + LINECHAR_CUSTTYPE + " = '" + custType + "'" +
                " AND " + LINECHAR_MNTH + " = '" + month + "'";

        Cursor cursor = db.rawQuery(query, null);

        int count = 0;
        if (cursor != null) {
            cursor.moveToFirst();
            count = cursor.getInt(0);
            cursor.close();
        }

        db.close();


        return count > 0;
    }
}