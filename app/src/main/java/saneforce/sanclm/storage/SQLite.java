package saneforce.sanclm.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import saneforce.sanclm.response.LoginResponse;


public class SQLite extends SQLiteOpenHelper {

    public static final String DATA_BASE_NAME = "san_clm.db";

    //Login Table
    public static final String LOGIN_TABLE = "login_table";
    public static final String LOGIN_DATA = "login_data";

    //Master Sync Table
    public static final String MASTER_SYNC_TABLE = "master_sync_table";
    public static final String MASTER_KEY = "master_key";
    public static final String MASTER_VALUE = "master_value";
    public static final String SYNC_STATUS = "sync_status"; // 0 - success, 1 - failed

    //Tour PLan Table
    public static final String TOUR_PLAN_TABLE = "tour_plan_table";
    public static final String TP_MONTH = "tp_month";
    public static final String TP_DATA = "tp_data";

    //Line Chat table
    private static final String LINE_CHAT_DATA_TABLE = "LINE_CHAT_DATA_TABLE";
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
    private static final String LINECHAR_FM_INDICATOR = "LINECHAR_FM_INDICATOR";

    public SQLite(@Nullable Context context) {
        super(context, DATA_BASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + LOGIN_TABLE + "(" + LOGIN_DATA + " text" + ")");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + MASTER_SYNC_TABLE + "(" + MASTER_KEY + " text," + MASTER_VALUE + " text," + SYNC_STATUS + " INTEGER" + ")");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TOUR_PLAN_TABLE + "(" + TP_MONTH + " text," + TP_DATA + " text" + ")");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + LINE_CHAT_DATA_TABLE + "(" + LINECHAR_CUSTCODE + " TEXT, " + LINECHAR_CUSTTYPE + " TEXT, " + LINECHAR_DCR_DT + " TEXT, " +
                LINECHAR_MONTH_NAME + " TEXT, " + LINECHAR_MNTH + " TEXT, " + LINECHAR_YR + " TEXT, " + LINECHAR_CUSTNAME + " TEXT, " + LINECHAR_TOWN_CODE + " TEXT, " +
                LINECHAR_TOWN_NAME + " TEXT, " + LINECHAR_DCR_FLAG + " TEXT, " + LINECHAR_FM_INDICATOR + " TEXT, " + LINECHAR_SF_CODE + " TEXT, " + LINECHAR_TRANS_SLNO + " TEXT, " + LINECHAR_AMSLNO + " TEXT);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + LOGIN_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + MASTER_SYNC_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TOUR_PLAN_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + LINE_CHAT_DATA_TABLE);

        onCreate(db);
    }

    public void deleteAllTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + LOGIN_TABLE);
        db.execSQL("DELETE FROM " + MASTER_SYNC_TABLE);
        db.execSQL("DELETE FROM " + TOUR_PLAN_TABLE);
        db.execSQL("DELETE FROM " + LINE_CHAT_DATA_TABLE);

        db.close();
    }


    //------------------------ Login ------------------------------------
    public void saveLoginData(String data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LOGIN_DATA,data);
        int updated = db.update(LOGIN_TABLE,contentValues,null,null);
        if (updated <= 0){
            db.insert(LOGIN_TABLE,null,contentValues);
        }
        db.close();
    }

    public LoginResponse getLoginData(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + LOGIN_TABLE,null);
        String data = "";
        LoginResponse loginResponse = new LoginResponse();
        if (cursor.moveToNext()){
            data = cursor.getString(0);
        }
        cursor.close();
        if (!data.equals("")){
            Type type = new TypeToken<LoginResponse>() {
            }.getType();
            loginResponse = new Gson().fromJson(data,type);
            return loginResponse;
        }
        return loginResponse;
    }

    //-------------------------- Master ----------------------------------------
    public void saveMasterSyncData(String key, String values,int syncStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MASTER_KEY, key);
        contentValues.put(MASTER_VALUE, values);
        contentValues.put(SYNC_STATUS,syncStatus);

        String[] args = new String[]{key};
        int updated = db.update(MASTER_SYNC_TABLE,contentValues,MASTER_KEY + "=?",args);
        if (updated <= 0){
            db.insert(MASTER_SYNC_TABLE,null,contentValues);
        }
        db.close();
    }

    public boolean getMasterSyncDataOfHQ(String key){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + MASTER_SYNC_TABLE + " where " + MASTER_KEY + "=" + "'" + key + "';", null);
        return cursor.moveToNext();
    }

    public JSONArray getMasterSyncDataByKey(String key) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + MASTER_SYNC_TABLE + " where " + MASTER_KEY + "=" + "'" + key + "';", null);
        String data = "";
        if (cursor.moveToNext()){
            data = cursor.getString(1);
        }
        cursor.close();

        JSONArray jsonArray = new JSONArray();
        try {
            if (data != null && !data.isEmpty())
                return jsonArray = new JSONArray(data.toString());
        }catch (Exception exception){
            exception.printStackTrace();
        }
        return jsonArray;
    }

    public void saveMasterSyncStatus(String key,int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MASTER_KEY,key);
        contentValues.put(SYNC_STATUS,status);

        String[] args = new String[]{key};
        int updated = db.update(MASTER_SYNC_TABLE,contentValues,MASTER_KEY + "=?", args);
        if (updated <= 0){
            db.insert(MASTER_SYNC_TABLE,null,contentValues);
        }
        db.close();
    }

    public int getMasterSyncStatusByKey(String key) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + MASTER_SYNC_TABLE + " where " + MASTER_KEY + "=" + "'" + key + "';", null);
        int data = 0;
        if (cursor.moveToNext()){
            data = cursor.getInt(2);
        }
        cursor.close();
        return data;
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

    //----------------------------Tour Plan----------------------

    public void saveTPData(String month,String data){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TP_MONTH, month);
        contentValues.put(TP_DATA, data);

        String[] args = new String[]{month};
        int updated = db.update(TOUR_PLAN_TABLE,contentValues,TP_MONTH + "=?", args);
        if (updated <= 0) {
            db.insert(TOUR_PLAN_TABLE,null,contentValues);
        }
        db.close();
    }

    public JSONArray getTPData(String month) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TOUR_PLAN_TABLE + " where " + TP_MONTH + "=" + "'" + month + "';",null);
        String data = "";
        if (cursor.moveToNext())
            data = cursor.getString(1);

        cursor.close();
       JSONArray jsonArray = new JSONArray();

       try {
           if (!data.isEmpty())
               jsonArray = new JSONArray(data);
       } catch (JSONException e) {
           throw new RuntimeException(e);
       }
        return jsonArray;
    }




    // insertdata Linechart
    public void insertLinecharData(String custCode, String custType, String dcrDt, String monthName, String mnth, String yr, String custName, String townCode,
                                   String townName, String dcrFlag, String sfCode, String transSlNo, String amslNo,String fw_indicater) {
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
        values.put(LINECHAR_FM_INDICATOR, fw_indicater);
        values.put(LINECHAR_AMSLNO, amslNo);

        db.insert(LINE_CHAT_DATA_TABLE, null, values);
        db.close();
    }


    public void clearLinecharTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + LINE_CHAT_DATA_TABLE);
        db.close();
    }


    public int getcurrentmonth_calls_count(String CustType){

        SQLiteDatabase db = this.getReadableDatabase();
        String currentYearMonth = new SimpleDateFormat("yyyy-MM", Locale.getDefault()).format(new Date());
        String query = "SELECT COUNT(*) FROM " + LINE_CHAT_DATA_TABLE + " WHERE strftime('%Y-%m', " + LINECHAR_DCR_DT + ") = '" + currentYearMonth + "'" + " AND " + LINECHAR_CUSTTYPE + " = '" + CustType + "'";

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


    public int getcalls_count_by_range(String startDate, String endDate, String custType) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT COUNT(*) FROM " + LINE_CHAT_DATA_TABLE + " WHERE " + LINECHAR_DCR_DT + " >= '" + startDate + "' " + " AND " + LINECHAR_DCR_DT + " <= '" + endDate + "' " + " AND " + LINECHAR_CUSTTYPE + " = '" + custType + "'";

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

//    public int getHalfMonthDataCount(String startDate, String endDate, String custType) {
//        SQLiteDatabase db = this.getReadableDatabase();
//        String query = "SELECT COUNT(*) FROM " + LINECHAT_DATA +
//                " WHERE " + LINECHAR_DCR_DT + " >= '" + startDate + "' " +
//                " AND " + LINECHAR_DCR_DT + " <= '" + endDate + "' " ;
//    }

    public int getfeildworkcount(String startDate, String endDate ) {
        SQLiteDatabase db = this.getReadableDatabase();
        String custType="0";
        String Fieldwork="F";

        String query = "SELECT COUNT(*) FROM " + LINE_CHAT_DATA_TABLE + " WHERE " + LINECHAR_DCR_DT + " >= '" + startDate + "' " + " AND " + LINECHAR_DCR_DT + " <= '" + endDate + "' " +
                " AND " + LINECHAR_FM_INDICATOR + " = '" + Fieldwork + "' "+ " AND " + LINECHAR_CUSTTYPE + " = '" + custType + "'";
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
        String query = "SELECT COUNT(*) FROM " + LINE_CHAT_DATA_TABLE + " WHERE " + LINECHAR_CUSTTYPE + " = '" + custType + "'" + " AND " + LINECHAR_MNTH + " = '" + month + "'";
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