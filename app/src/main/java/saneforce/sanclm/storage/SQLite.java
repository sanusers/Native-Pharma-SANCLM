package saneforce.sanclm.storage;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import org.json.JSONArray;

public class SQLite extends SQLiteOpenHelper {

    public static final String DATA_BASE_NAME = "san_clm.db";
    public static final String LOGIN_TABLE = "login_table";
    public static final String LOGIN_DATA = "login_data";

    //Master Sync
    public static final String MASTER_SYNC_TABLE = "master_sync_data";
    public static final String MASTER_KEY = "master_key";
    public static final String MASTER_VALUE = "master_value";

    //Doctor Master Table
    public static final String DOCTOR_MASTER_TABLE = "Doctor_Master";
    public static final String HQ_CODE = "hqCode";
    public static final String DOCTOR_DATA = "DrData";



    public SQLite (@Nullable Context context) {
        super(context, DATA_BASE_NAME, null, 1);
    }

    @Override
    public void onCreate (SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS " + LOGIN_TABLE + "(" + LOGIN_DATA + " text" + ")");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + MASTER_SYNC_TABLE + "(" + MASTER_KEY + " text," + MASTER_VALUE + " text" + ")");

        //Dr Table
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DOCTOR_MASTER_TABLE + " (" + HQ_CODE + " text," + DOCTOR_DATA + " text" + ")");

    }

    @Override
    public void onUpgrade (SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + LOGIN_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + MASTER_SYNC_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DOCTOR_MASTER_TABLE);

        onCreate(db);
    }

    public void deleteAllTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + LOGIN_TABLE);
        db.execSQL("DELETE FROM " + MASTER_SYNC_TABLE);
        db.execSQL("DELETE FROM " + DOCTOR_MASTER_TABLE);

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

    public void saveDrMaster(String hqCode,String drList){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(HQ_CODE, hqCode);
        contentValues.put(DOCTOR_DATA,drList);

        String[] args = new String[]{hqCode};
        int updated = db.update(DOCTOR_MASTER_TABLE, contentValues, HQ_CODE + "=?", args);
        if (updated <= 0){
            db.insert(DOCTOR_MASTER_TABLE,null,contentValues);
        }
        db.close();
    }

    public JSONArray getDrMaster(String hqCode){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DOCTOR_MASTER_TABLE + "WHERE " + HQ_CODE + "=" + "'" + hqCode + "';", null);
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



}
