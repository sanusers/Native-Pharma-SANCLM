package saneforce.santrip.storage;

import android.annotation.SuppressLint;
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
import java.util.ArrayList;
import java.util.Calendar;


import saneforce.santrip.activity.homeScreen.modelClass.CheckInOutModelClass;
import saneforce.santrip.activity.homeScreen.modelClass.ChildListModelClass;
import saneforce.santrip.activity.homeScreen.modelClass.EcModelClass;
import saneforce.santrip.activity.homeScreen.modelClass.GroupModelClass;
import saneforce.santrip.activity.homeScreen.modelClass.OutBoxCallList;
import saneforce.santrip.activity.presentation.createPresentation.BrandModelClass;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.response.LoginResponse;


public class SQLite extends SQLiteOpenHelper {

    public static final String DATA_BASE_NAME = "san_clm.db";

    public static final String COLUMN_ID = "Column_id";

    //Login Table
    public static final String LOGIN_TABLE = "login_table";
    public static final String LOGIN_DATA = "login_data";

    //Master Sync Table
    public static final String MASTER_SYNC_TABLE = "master_sync_table";
    public static final String MASTER_KEY = "master_key";
    public static final String MASTER_VALUE = "master_value";
    public static final String SYNC_STATUS = "sync_status"; // 0 - success, 1 - failed


    public static final String DCR_DocData = "DCR_DocData";
    public static final String Dcr_values = "Dcr_values";
    public static final String Hq_values = "Hq_values";

    //Tour PLan Table
    public static final String TOUR_PLAN_OFFLINE_TABLE = "tour_plan_offline_table";
    public static final String TOUR_PLAN_ONLINE_TABLE = "tour_plan_online_table";
    public static final String TP_MONTH = "tp_month";
    public static final String TP_DATA = "tp_data";
    public static final String TP_MONTH_STATUS = "tp_month_synced";
    public static final String TP_APPROVAL_STATUS = "tp_approval_status"; // 0 - Planning, 1 - Pending, 2 - Rejected, 3 - Approved
    public static final String TP_REJECTION_REASON = "tp_rejection_reason";

    //Offline Tables
    //Call Offline
    public static final String CALL_OFFLINE_TABLE = "call_offline_table";
    public static final String CALL_DATE = "call_date";
    public static final String CALL_TIME = "call_time";
    public static final String CALL_IN_TIME = "call_in_time";
    public static final String CALL_OUT_TIME = "call_out_time";
    public static final String CALL_CUS_CODE = "call_cus_code";
    public static final String CALL_CUS_NAME = "call_cus_name";
    public static final String CALL_CUS_TYPE = "call_cus_type";
    public static final String CALL_JSON_VALUES = "call_json_values";
    public static final String CALL_SYNC_COUNT = "call_sync_count";
    public static final String CALL_SYNC_STATUS = "call_sync_status";

    //Call Offline EventCapture
    public static final String CALL_OFFLINE_EC_TABLE = "call_offline_ec_table";
    public static final String CALL_DATE_EC = "call_date_ec";
    public static final String CALL_CUS_CODE_EC = "call_cus_code_ec";
    public static final String CALL_CUS_NAME_EC = "call_cus_name_ec";
    public static final String CALL_IMAGE_NAME = "call_image_name_ec";
    public static final String CALL_FILE_PATH = "call_file_path";
    public static final String CALL_JSON_VALUES_EC = "call_json_values_ec";
    public static final String CALL_STATUS_EC = "call_status_ec";
    public static final String CALL_SYNC_STATUS_EC = "call_sync_status_ec";

    //Offline Workplan
    public static final String CALL_OFFLINE_WORKTYPE_TABLE = "call_offline_wt_table";
    public static final String CALL_OFFLINE_WORKTYPE_DATE = "call_offline_wt_date";
    public static final String CALL_OFFLINE_WORKTYPE_NAME = "call_offline_wt_name";
    public static final String CALL_OFFLINE_WORKTYPE_CODE = "call_offline_wt_code";
    public static final String CALL_OFFLINE_WORKTYPE_JSON = "call_offline_wt_json";
    public static final String CALL_OFFLINE_WORKTYPE_STATUS = "call_offline_wt_status";
    public static final String CALL_OFFLINE_WORKTYPE_SYNC_STATUS = "call_offline_wt_sync_status";

    //CheckInOut
    public static final String OFFLINE_CHECKINOUT_TABLE = "offline_checkinout_table";
    public static final String OFFLINE_DATE_CHECKINOUT = "offline_checkinout_date";
    public static final String OFFLINE_CHECKINTIME = "offline_checkin_time";
    public static final String OFFLINE_CHECKOUTTIME = "offline_checkout_time";
    public static final String OFFLINE_JSON_CHECKIN = "offline_check_in_json";
    public static final String OFFLINE_JSON_CHECKOUT = "offline_check_out_json";
    public static final String OFFLINE_DATE_COUNT = "offline_date_count";
    public static final String OFFLINE_SYNC_STATUS = "offline_sync_status";


    //Presentation Table
    public static final String PRESENTATION_TABLE = "presentation_table";
    public static final String PRESENTATION_NAME = "presentation_name";
    public static final String PRESENTATION_DATA = "presentation_data";
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
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TOUR_PLAN_OFFLINE_TABLE + "(" + TP_MONTH + " text," + TP_DATA + " text," + TP_MONTH_STATUS + " text," + TP_APPROVAL_STATUS + " text," + TP_REJECTION_REASON + " text" + ")");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + TOUR_PLAN_ONLINE_TABLE + "(" + TP_MONTH + " text," + TP_DATA + " text," + TP_APPROVAL_STATUS + " text," + TP_REJECTION_REASON + " text" + ")");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + CALL_OFFLINE_TABLE + "(" + CALL_DATE + " text," + CALL_TIME + " text," + CALL_IN_TIME + " text," + CALL_OUT_TIME + " text," + CALL_CUS_NAME + " text," + CALL_CUS_TYPE + " text," + CALL_CUS_CODE + " text," + CALL_JSON_VALUES + " text," + CALL_SYNC_STATUS + " text," + CALL_SYNC_COUNT + " INTEGER" + ")");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + CALL_OFFLINE_EC_TABLE + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + CALL_DATE_EC + " text," + CALL_CUS_CODE_EC + " text," + CALL_CUS_NAME_EC + " text," + CALL_IMAGE_NAME + " text," + CALL_FILE_PATH + " text," + CALL_JSON_VALUES_EC + " text," + CALL_STATUS_EC + " text," + CALL_SYNC_STATUS_EC + " INTEGER" + ")");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + OFFLINE_CHECKINOUT_TABLE + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + OFFLINE_DATE_CHECKINOUT + " text," + OFFLINE_CHECKINTIME + " text," + OFFLINE_CHECKOUTTIME + " text," + OFFLINE_JSON_CHECKIN + " text," + OFFLINE_JSON_CHECKOUT + " text," + OFFLINE_DATE_COUNT + " text," + OFFLINE_SYNC_STATUS + " INTEGER" + ")");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + CALL_OFFLINE_WORKTYPE_TABLE + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + CALL_OFFLINE_WORKTYPE_DATE + " text," + CALL_OFFLINE_WORKTYPE_NAME + " text," + CALL_OFFLINE_WORKTYPE_CODE + " text," + CALL_OFFLINE_WORKTYPE_JSON + " text," + CALL_OFFLINE_WORKTYPE_STATUS + " text," + CALL_OFFLINE_WORKTYPE_SYNC_STATUS + " INTEGER" + ")");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + LINE_CHAT_DATA_TABLE + "(" + LINECHAR_CUSTCODE + " TEXT, " + LINECHAR_CUSTTYPE + " TEXT, " + LINECHAR_DCR_DT + " TEXT, " + LINECHAR_MONTH_NAME + " TEXT, " + LINECHAR_MNTH + " TEXT, " + LINECHAR_YR + " TEXT, " + LINECHAR_CUSTNAME + " TEXT, " + LINECHAR_TOWN_CODE + " TEXT, " + LINECHAR_TOWN_NAME + " TEXT, " + LINECHAR_DCR_FLAG + " TEXT, " + LINECHAR_FM_INDICATOR + " TEXT, " + LINECHAR_SF_CODE + " TEXT, " + LINECHAR_TRANS_SLNO + " TEXT, " + LINECHAR_AMSLNO + " TEXT);");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + PRESENTATION_TABLE + "(" + PRESENTATION_NAME + " TEXT PRIMARY KEY, " + PRESENTATION_DATA + " TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS " + DCR_DocData + "(" +Hq_values  + " text," + Dcr_values + " text"+ ")");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS " + LOGIN_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + MASTER_SYNC_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TOUR_PLAN_OFFLINE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TOUR_PLAN_ONLINE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CALL_OFFLINE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CALL_OFFLINE_EC_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + OFFLINE_CHECKINOUT_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + LINE_CHAT_DATA_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PRESENTATION_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DCR_DocData);
        onCreate(db);
    }

    public void deleteAllTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + LOGIN_TABLE);
        db.execSQL("DELETE FROM " + MASTER_SYNC_TABLE);
        db.execSQL("DELETE FROM " + TOUR_PLAN_OFFLINE_TABLE);
        db.execSQL("DELETE FROM " + TOUR_PLAN_ONLINE_TABLE);
        db.execSQL("DELETE FROM " + CALL_OFFLINE_TABLE);
        db.execSQL("DELETE FROM " + CALL_OFFLINE_EC_TABLE);
        db.execSQL("DELETE FROM " + OFFLINE_CHECKINOUT_TABLE);
        db.execSQL("DELETE FROM " + LINE_CHAT_DATA_TABLE);
        db.execSQL("DELETE FROM " + PRESENTATION_TABLE);
        db.execSQL("DELETE FROM " + DCR_DocData);
        db.close();



    }
    //------------------------ Login ------------------------------------
/*  --- Has been moved to room database. -> LoginDataDao, LoginDataTable
    public void saveLoginData(String data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(LOGIN_DATA, data);
        int updated = db.update(LOGIN_TABLE, contentValues, null, null);
        if (updated <= 0) {
            db.insert(LOGIN_TABLE, null, contentValues);
        }
        db.close();
    }

    public LoginResponse getLoginData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + LOGIN_TABLE, null);
        String data = "";
        LoginResponse loginResponse = new LoginResponse();
        if (cursor.moveToNext()) {
            data = cursor.getString(0);
        }
        cursor.close();
        db.close();
        if (!data.equals("")) {
            Type type = new TypeToken<LoginResponse>() {
            }.getType();
            loginResponse = new Gson().fromJson(data, type);
            return loginResponse;
        }
        return loginResponse;
    }

    --- Has been moved to room database. -> MasterDataDao, MasterDataTable
    //-------------------------- Master ----------------------------------------
    public void saveMasterSyncData(String key, String values, int syncStatus) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MASTER_KEY, key);
        contentValues.put(MASTER_VALUE, values);
        contentValues.put(SYNC_STATUS, syncStatus);

        String[] args = new String[]{key};
        int updated = db.update(MASTER_SYNC_TABLE, contentValues, MASTER_KEY + "=?", args);
        if (updated <= 0) {
            db.insert(MASTER_SYNC_TABLE, null, contentValues);
        }
        db.close();
    }

    public boolean getMasterSyncDataOfHQ(String key) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + MASTER_SYNC_TABLE + " where " + MASTER_KEY + "=" + "'" + key + "';", null);
        return cursor.moveToNext();
    }

    public JSONArray getMasterSyncDataByKey(String key) {
        JSONArray jsonArray = new JSONArray();
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from " + MASTER_SYNC_TABLE + " where " + MASTER_KEY + "=" + "'" + key + "';", null);
            String data = "";
            if (cursor.moveToNext()) {
                data = cursor.getString(1);
            }
            cursor.close();
            db.close();
            if (data != null && !data.isEmpty()) return jsonArray = new JSONArray(data);

        } catch (Exception ignored) {
        }
        return jsonArray;
    }

    public void saveMasterSyncStatus(String key, int status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(MASTER_KEY, key);
        contentValues.put(SYNC_STATUS, status);

        String[] args = new String[]{key};
        int updated = db.update(MASTER_SYNC_TABLE, contentValues, MASTER_KEY + "=?", args);
        if (updated <= 0) {
            db.insert(MASTER_SYNC_TABLE, null, contentValues);
        }
        db.close();
    }

    public int getMasterSyncStatusByKey(String key) {
        int data = 0;
        try {
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor = db.rawQuery("select * from " + MASTER_SYNC_TABLE + " where " + MASTER_KEY + "=" + "'" + key + "';", null);
            if (cursor.moveToNext()) {
                data = cursor.getInt(2);
            }
            cursor.close();
            db.close();

        } catch (Exception ignored) {

        }
        return data;
    }

    //    -----------------Remainder------------------------
  --- Has been moved to room database. -> DCRDocDataDao, DCRDocDataTable
    public void insert_docvalues(String hqcode, String values) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Hq_values, hqcode);
        contentValues.put(Dcr_values, values);
        db.insert(DCR_DocData, null, contentValues);
        db.close();
    }

    public JSONArray getDcr_datas(String key) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + DCR_DocData + " where " + Hq_values + "=" + "'" + key + "';", null);
        String data = "";
        if (cursor.moveToNext()) {
            data = cursor.getString(1);
        }
        cursor.close();
        db.close();

        JSONArray jsonArray = new JSONArray();
        try {
            if (data != null && !data.isEmpty())
                return jsonArray = new JSONArray(data.toString());
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return jsonArray;
    }




    //----------------------------offline----------------------
    //WorkType Table
    --- Has been moved to room database. -> CallOfflineWorkTypeDataDao, CallOfflineWorkTypeDataTable
    public void saveWorkPlan(String date, String wtCode, String wtName, String jsonValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CALL_OFFLINE_WORKTYPE_DATE, date);
        contentValues.put(CALL_OFFLINE_WORKTYPE_CODE, wtCode);
        contentValues.put(CALL_OFFLINE_WORKTYPE_NAME, wtName);
        contentValues.put(CALL_OFFLINE_WORKTYPE_JSON, jsonValues);
        contentValues.put(CALL_OFFLINE_WORKTYPE_STATUS, "");
        contentValues.put(CALL_OFFLINE_WORKTYPE_SYNC_STATUS, 0);
        db.insert(CALL_OFFLINE_WORKTYPE_TABLE, null, contentValues);
        db.close();
    }

    private String getListOfflineWt(String date, SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select * from " + CALL_OFFLINE_WORKTYPE_TABLE + " where " + CALL_OFFLINE_WORKTYPE_DATE + "='" + date + "';", null);
        StringBuilder fieldWork = new StringBuilder();
        while (cursor.moveToNext()) {
            if (fieldWork.toString().isEmpty()) {
                fieldWork.append(cursor.getString(2));
            } else {
                fieldWork.append(",").append(cursor.getString(2));
            }
        }
        return fieldWork.toString();
    }

    //Check In Out table
    --- Has been moved to room database. -> OfflineCheckInOutDataDao, OfflineCheckInOutDataTable
    public void saveCheckIn(String date, String checkInTime, String jsonValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + OFFLINE_CHECKINOUT_TABLE + " where " + OFFLINE_DATE_CHECKINOUT + "='" + date + "';", null);
        ContentValues contentValues = new ContentValues();
        contentValues.put(OFFLINE_DATE_CHECKINOUT, date);
        contentValues.put(OFFLINE_CHECKINTIME, checkInTime);
        contentValues.put(OFFLINE_CHECKOUTTIME, "");
        contentValues.put(OFFLINE_JSON_CHECKOUT, "");
        contentValues.put(OFFLINE_JSON_CHECKIN, jsonValues);
        contentValues.put(OFFLINE_DATE_COUNT, String.valueOf(cursor.getCount()));
        contentValues.put(OFFLINE_SYNC_STATUS, 0);
        db.insert(OFFLINE_CHECKINOUT_TABLE, null, contentValues);
        db.close();
    }

    public void saveCheckOut(String date, String checkOutTime, String jsonValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + OFFLINE_CHECKINOUT_TABLE + " where " + OFFLINE_DATE_CHECKINOUT + "='" + date + "';", null);
        ContentValues contentValues = new ContentValues();
        contentValues.put(OFFLINE_DATE_CHECKINOUT, date);
        contentValues.put(OFFLINE_CHECKOUTTIME, checkOutTime);
        contentValues.put(OFFLINE_JSON_CHECKOUT, jsonValues);
        contentValues.put(OFFLINE_DATE_COUNT, String.valueOf(cursor.getCount()));
        contentValues.put(OFFLINE_SYNC_STATUS, 0);

        int updated = db.update(OFFLINE_CHECKINOUT_TABLE, contentValues, OFFLINE_DATE_CHECKINOUT + "=? and " + OFFLINE_DATE_COUNT + "=?", new String[]{date, String.valueOf(cursor.getCount() - 1)});
        if (updated <= 0) {
            db.insert(OFFLINE_CHECKINOUT_TABLE, null, contentValues);
        }
        db.close();
    }

    public void updateCheckInOutStatus(String id, int checkSynced) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(OFFLINE_SYNC_STATUS, checkSynced);

        int updated = db.update(OFFLINE_CHECKINOUT_TABLE, contentValues, COLUMN_ID + "=?", new String[]{id});
        if (updated <= 0) {
            db.insert(OFFLINE_CHECKINOUT_TABLE, null, contentValues);
        }
        db.close();
    }

    public ArrayList<CheckInOutModelClass> getCheckInOutTime(String date, SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select * from " + OFFLINE_CHECKINOUT_TABLE + " where " + OFFLINE_DATE_CHECKINOUT + "='" + date + "';", null);
        String dates = "", jsonInData = "", jsonOutData = "", CheckInTime = "", CheckOutTime = "", CheckCount = "", id = "";
        int checkStatus;
        ArrayList<CheckInOutModelClass> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            id = cursor.getString(0);
            dates = cursor.getString(1);
            CheckInTime = cursor.getString(2);
            CheckOutTime = cursor.getString(3);
            jsonInData = cursor.getString(4);
            jsonOutData = cursor.getString(5);
            CheckCount = cursor.getString(6);
            checkStatus = cursor.getInt(7);
            if (dates != null) {
                list.add(new CheckInOutModelClass(Integer.parseInt(id), dates, CheckInTime, CheckOutTime, jsonInData, jsonOutData, Integer.parseInt(CheckCount), checkStatus));
            }
        }
        return list;
    }

    public void deleteOfflineCheckInOut(String date, String count) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + OFFLINE_CHECKINOUT_TABLE + " WHERE " + OFFLINE_DATE_COUNT + " = '" + count + "' " + " AND " + OFFLINE_DATE_CHECKINOUT + "='" + date + "';");
        db.close();
    }

    public int getCountCheckInOut(String date) {
        int Count;
        SQLiteDatabase db = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("select * from " + OFFLINE_CHECKINOUT_TABLE + " where " + OFFLINE_DATE_CHECKINOUT + "='" + date + "';", null);
        Count = cursor.getCount();
        db.close();
        return Count;
    }

    --- Has been moved to room database. -> CallOfflineECDataDao, CallOfflineECDataTable
    //Event Capture table
    public void saveOfflineEC(String date, String cusCode, String cusName, String img_name, String filepath, String jsonValues, String status, Integer sync) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CALL_DATE_EC, date);
        contentValues.put(CALL_CUS_CODE_EC, cusCode);
        contentValues.put(CALL_CUS_NAME_EC, cusName);
        contentValues.put(CALL_IMAGE_NAME, img_name);
        contentValues.put(CALL_FILE_PATH, filepath);
        contentValues.put(CALL_JSON_VALUES_EC, jsonValues);
        contentValues.put(CALL_STATUS_EC, status);
        contentValues.put(CALL_SYNC_STATUS_EC, sync);

        int updated = db.update(CALL_OFFLINE_EC_TABLE, contentValues, CALL_IMAGE_NAME + "=? and " + CALL_CUS_CODE_EC + "=?", new String[]{img_name, cusCode});
        if (updated <= 0) {
            db.insert(CALL_OFFLINE_EC_TABLE, null, contentValues);
        }
        db.close();
    }

    public boolean isAvailableEc(String date, String customerCode) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("select * from " + CALL_OFFLINE_EC_TABLE + " WHERE " + CALL_CUS_CODE_EC + " = '" + customerCode + "' " + " AND " + CALL_DATE_EC + " = '" + date + "';", null);
        return cursor.getCount() > 0;
    }

    public void updateECStatus(String id, String status, int checkSynced) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CALL_SYNC_STATUS_EC, checkSynced);
        contentValues.put(CALL_STATUS_EC, status);
        db.update(CALL_OFFLINE_EC_TABLE, contentValues, COLUMN_ID + "=?", new String[]{id});
        db.close();
    }

    public ArrayList<EcModelClass> getEcList(String date, SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select * from " + CALL_OFFLINE_EC_TABLE + " where " + CALL_DATE_EC + "='" + date + "';", null);
        String dates = "", jsonData = "", cusName = "", cusCode = "", imgName = "", file = "", id = "", status = "";
        int synced;
        ArrayList<EcModelClass> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            id = cursor.getString(0);
            dates = cursor.getString(1);
            cusCode = cursor.getString(2);
            cusName = cursor.getString(3);
            imgName = cursor.getString(4);
            file = cursor.getString(5);
            jsonData = cursor.getString(6);
            status = cursor.getString(7);
            synced = cursor.getInt(8);
            if (dates != null) {
                list.add(new EcModelClass(Integer.parseInt(id), dates, cusCode, cusName, imgName, file, jsonData, status, synced));
            }
        }
        return list;
    }

    public ArrayList<EcModelClass> getEcListFull() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + CALL_OFFLINE_EC_TABLE, null);
        String dates = "", jsonData = "", cusName = "", cusCode = "", imgName = "", file = "", id = "", status = "";
        int synced;
        ArrayList<EcModelClass> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            id = cursor.getString(0);
            dates = cursor.getString(1);
            cusCode = cursor.getString(2);
            cusName = cursor.getString(3);
            imgName = cursor.getString(4);
            file = cursor.getString(5);
            jsonData = cursor.getString(6);
            status = cursor.getString(7);
            synced = cursor.getInt(8);
            if (dates != null) {
                list.add(new EcModelClass(Integer.parseInt(id), dates, cusCode, cusName, imgName, file, jsonData, status, synced));
            }
        }
        db.close();
        return list;
    }

    public void deleteOfflineEC(String id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + CALL_OFFLINE_EC_TABLE + " WHERE " + COLUMN_ID + " = '" + id + "';");
        db.close();
    }


    public void deleteOfflineECImage(String imgName) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + CALL_OFFLINE_EC_TABLE + " WHERE " + CALL_IMAGE_NAME + " = '" + imgName + "';");
        db.close();
    }

    //Offline call table
    --- Has been moved to room database. -> CallOfflineDataDao, CallOfflineDataTable
    public void saveOfflineCallIN(String date, String inTime, String cusCode, String cusName, String cusType) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CALL_DATE, date);
        contentValues.put(CALL_IN_TIME, inTime);
        contentValues.put(CALL_CUS_CODE, cusCode);
        contentValues.put(CALL_CUS_NAME, cusName);
        contentValues.put(CALL_CUS_TYPE, cusType);
        contentValues.put(CALL_SYNC_STATUS, "");
        contentValues.put(CALL_SYNC_COUNT, 0);

        int updated = db.update(CALL_OFFLINE_TABLE, contentValues, CALL_DATE + "=? and " + CALL_CUS_CODE + "=?", new String[]{date, cusCode});
        if (updated <= 0) {
            db.insert(CALL_OFFLINE_TABLE, null, contentValues);
        }
        db.close();
    }

    public void saveOfflineCallOut(String date, String time, String outTime, String cusCode, String cusName, String cusType, String values, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CALL_DATE, date);
        contentValues.put(CALL_TIME, time);
        contentValues.put(CALL_OUT_TIME, outTime);
        contentValues.put(CALL_CUS_CODE, cusCode);
        contentValues.put(CALL_CUS_NAME, cusName);
        contentValues.put(CALL_CUS_TYPE, cusType);
        contentValues.put(CALL_JSON_VALUES, values);
        contentValues.put(CALL_SYNC_COUNT, 0);
        contentValues.put(CALL_SYNC_STATUS, status);

        int updated = db.update(CALL_OFFLINE_TABLE, contentValues, CALL_DATE + "=? and " + CALL_CUS_CODE + "=?", new String[]{date, cusCode});
        if (updated <= 0) {
            db.insert(CALL_OFFLINE_TABLE, null, contentValues);
        }
        db.close();
    }

    public void saveOfflineUpdateJson(String date, String custCode, String jsonValue) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CALL_JSON_VALUES, jsonValue);
        db.update(CALL_OFFLINE_TABLE, contentValues, CALL_DATE + "=? and " + CALL_CUS_CODE + "=?", new String[]{date, custCode});
        db.close();
    }

    public String getJsonCallList(String date, String cusCode) {
        SQLiteDatabase db = this.getWritableDatabase();
        String jsonData = "";
        Cursor cursor = db.rawQuery("select * from " + CALL_OFFLINE_TABLE + " where " + CALL_DATE + "='" + date + "' AND " + CALL_CUS_CODE + "='" + cusCode + "';", null);
        while (cursor.moveToNext()) {
            jsonData = cursor.getString(7);
        }
        db.close();
        return jsonData;
    }

    --- Has been moved to room database. -> CallUtil

    public void updateOfflineUpdateStatusEC(String date, String custCode, String count, String status, int EcSynced) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(CALL_DATE, date);
        contentValues.put(CALL_SYNC_COUNT, count);
        contentValues.put(CALL_SYNC_STATUS, status);
        db.update(CALL_OFFLINE_TABLE, contentValues, CALL_DATE + "=? and " + CALL_CUS_CODE + "=?", new String[]{date, custCode});

        if (isAvailableEc(date, custCode)) {
            ContentValues contentValuesEC = new ContentValues();
            contentValuesEC.put(CALL_DATE_EC, date);
            contentValuesEC.put(CALL_STATUS_EC, status);
            contentValues.put(CALL_SYNC_STATUS_EC, EcSynced);
            db.update(CALL_OFFLINE_EC_TABLE, contentValuesEC, CALL_DATE_EC + "=? and " + CALL_CUS_CODE_EC + "=?", new String[]{date, custCode});
        }
        db.close();
    }

    public void deleteOfflineCalls() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + CALL_OFFLINE_TABLE);
        db.execSQL("DELETE FROM " + CALL_OFFLINE_EC_TABLE);
        db.execSQL("DELETE FROM " + CALL_OFFLINE_WORKTYPE_TABLE);
        db.execSQL("DELETE FROM " + OFFLINE_CHECKINOUT_TABLE);
        db.close();
    }

    public void deleteOfflineCalls(String cusCode, String cusName, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + CALL_OFFLINE_TABLE + " WHERE " + CALL_CUS_CODE + " = '" + cusCode + "' " + " AND " + CALL_CUS_NAME + " = '" + cusName + "'" + " AND " + CALL_DATE + "='" + date + "';");
        db.execSQL("DELETE FROM " + CALL_OFFLINE_EC_TABLE + " WHERE " + CALL_CUS_CODE_EC + " = '" + cusCode + "' " + " AND " + CALL_CUS_NAME_EC + " = '" + cusName + "'" + " AND " + CALL_DATE_EC + "='" + date + "';");
        db.close();
    }


    public boolean isOutBoxDataAvailable() {
        boolean isAvailableCall = false, isAvailableEC = false, isAvailableCheckInOut = false, returnStatus = false;

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursorCheckInOut = db.rawQuery("select * from " + OFFLINE_CHECKINOUT_TABLE, null);
        if (cursorCheckInOut.getCount() > 0) {
            isAvailableCheckInOut = true;
        }

        Cursor cursor = db.rawQuery("select * from " + CALL_OFFLINE_TABLE, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                if (!cursor.getString(8).equalsIgnoreCase(Constants.DUPLICATE_CALL)) {
                    isAvailableCall = true;
                    break;
                }
            }
        }

        Cursor cursorEC = db.rawQuery("select * from " + CALL_OFFLINE_EC_TABLE, null);
        if (cursorEC.getCount() > 0) {
            isAvailableEC = true;
        }

        if (isAvailableCall || isAvailableEC || isAvailableCheckInOut) {
            returnStatus = true;
        }

        return returnStatus;
    }

    public ArrayList<GroupModelClass> getOutBoxDate() {
        boolean isAvailable = false;
        SQLiteDatabase db = this.getWritableDatabase();
        String date;
        ArrayList<GroupModelClass> list = new ArrayList<>();

        Cursor cursor = db.rawQuery("select * from " + OFFLINE_CHECKINOUT_TABLE, null);
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                date = cursor.getString(1);
                if (date != null) {
                    if (list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getGroupName().equalsIgnoreCase(date)) {
                                isAvailable = true;
                                break;
                            } else {
                                isAvailable = false;
                            }
                        }
                    } else {
                        isAvailable = false;
                    }
                    if (!isAvailable) {
                        list.add(new GroupModelClass(date));
                    }
                }
            }
        }

        Cursor cursorWt = db.rawQuery("select * from " + CALL_OFFLINE_WORKTYPE_TABLE, null);
        if (cursorWt.getCount() > 0) {
            while (cursorWt.moveToNext()) {
                date = cursorWt.getString(1);
                if (date != null) {
                    if (list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getGroupName().equalsIgnoreCase(date)) {
                                isAvailable = true;
                                break;
                            } else {
                                isAvailable = false;
                            }
                        }
                    } else {
                        isAvailable = false;
                    }
                    if (!isAvailable) {
                        list.add(new GroupModelClass(date));
                    }
                }
            }
        }

        Cursor cursorCall = db.rawQuery("select * from " + CALL_OFFLINE_TABLE, null);
        if (cursorCall.getCount() > 0) {
            while (cursorCall.moveToNext()) {
                date = cursorCall.getString(0);
                if (date != null) {
                    if (list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getGroupName().equalsIgnoreCase(date)) {
                                isAvailable = true;
                                break;
                            } else {
                                isAvailable = false;
                            }
                        }
                    } else {
                        isAvailable = false;
                    }
                    if (!isAvailable) {
                        list.add(new GroupModelClass(date));
                    }
                }
            }
        }

        Cursor cursorEC = db.rawQuery("select * from " + CALL_OFFLINE_EC_TABLE, null);
        if (cursorEC.getCount() > 0) {
            while (cursorEC.moveToNext()) {
                date = cursorEC.getString(1);
                if (date != null) {
                    if (list.size() > 0) {
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getGroupName().equalsIgnoreCase(date)) {
                                isAvailable = true;
                                break;
                            } else {
                                isAvailable = false;
                            }
                        }
                    } else {
                        isAvailable = false;
                    }
                    if (!isAvailable) {
                        list.add(new GroupModelClass(date));
                    }
                }
            }
        }

        ArrayList<GroupModelClass> listData = new ArrayList<>();
        ArrayList<ChildListModelClass> groupNamesList;

        if (list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                groupNamesList = new ArrayList<>();

                ArrayList<CheckInOutModelClass> checkInOut = getCheckInOutTime(list.get(i).getGroupName(), db);
                groupNamesList.add(new ChildListModelClass("Checking In/Out", 0, false, true, checkInOut, "", ""));

                groupNamesList.add(new ChildListModelClass(getListOfflineWt(list.get(i).getGroupName(), db), 1, false));

                ArrayList<OutBoxCallList> outBoxList = getOutBoxCallsList(list.get(i).getGroupName(), db);
                groupNamesList.add(new ChildListModelClass("Calls", 2, false, true, outBoxList, ""));

                ArrayList<EcModelClass> ecModelClasses = getEcList(list.get(i).getGroupName(), db);
                groupNamesList.add(new ChildListModelClass("Event Captured", 3, false, true, ecModelClasses));

                listData.add(new GroupModelClass(list.get(i).getGroupName(), groupNamesList, false, 0));
            }
        }

        db.close();
        return listData;
    }

    public ArrayList<OutBoxCallList> getOutBoxCallsList(String date, SQLiteDatabase db) {
        Cursor cursor = db.rawQuery("select * from " + CALL_OFFLINE_TABLE + " where " + CALL_DATE + "='" + date + "';", null);
        String dates = "", time = "", inTime = "", outTime = "", cusName = "", cusType = "", cusCode = "", jsonData = "", syncStatus = "";
        int syncCount = 0;
        ArrayList<OutBoxCallList> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            dates = cursor.getString(0);
            time = cursor.getString(1);
            inTime = cursor.getString(2);
            outTime = cursor.getString(3);
            cusName = cursor.getString(4);
            cusType = cursor.getString(5);
            cusCode = cursor.getString(6);
            jsonData = cursor.getString(7);
            syncStatus = cursor.getString(8);
            if (!cursor.getString(9).isEmpty() && !cursor.getString(9).equalsIgnoreCase("null"))
                syncCount = Integer.parseInt(cursor.getString(9));

            if (syncStatus.isEmpty()) {
                deleteOfflineCalls(cusCode, cusName, dates);
            }

            if (dates != null && !syncStatus.isEmpty()) {
                list.add(new OutBoxCallList(cusName, cusCode, dates, inTime, outTime, jsonData, cusType, syncStatus, syncCount));
            }
        }

        return list;
    }

    public ArrayList<OutBoxCallList> getOutBoxCallsFullList() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + CALL_OFFLINE_TABLE, null);
        String dates = "", time = "", inTime = "", outTime = "", cusName = "", cusType = "", cusCode = "", jsonData = "", syncStatus = "";
        int syncCount = 0;
        ArrayList<OutBoxCallList> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            dates = cursor.getString(0);
            time = cursor.getString(1);
            inTime = cursor.getString(2);
            outTime = cursor.getString(3);
            cusName = cursor.getString(4);
            cusType = cursor.getString(5);
            cusCode = cursor.getString(6);
            jsonData = cursor.getString(7);
            syncStatus = cursor.getString(8);
            if (!cursor.getString(9).isEmpty() && !cursor.getString(9).equalsIgnoreCase("null"))
                syncCount = Integer.parseInt(cursor.getString(9));

            if (syncStatus.isEmpty()) {
                deleteOfflineCalls(cusCode, cusName, dates);
            }

            if (dates != null && !syncStatus.isEmpty()) {
                list.add(new OutBoxCallList(cusName, cusCode, dates, inTime, outTime, jsonData, cusType, syncStatus, syncCount));
            }
        }

        db.close();
        return list;
    }

    public ArrayList<OutBoxCallList> getOutBoxCallsFullList(String status, String status2) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + CALL_OFFLINE_TABLE + " WHERE " + CALL_SYNC_STATUS + " IN " + "('" + status + "'" + ",'" + status2 + "')", null);
        String dates = "", time = "", inTime = "", outTime = "", cusName = "", cusType = "", cusCode = "", jsonData = "", syncStatus = "";
        int syncCount = 0;
        ArrayList<OutBoxCallList> list = new ArrayList<>();
        while (cursor.moveToNext()) {
            dates = cursor.getString(0);
            time = cursor.getString(1);
            inTime = cursor.getString(2);
            outTime = cursor.getString(3);
            cusName = cursor.getString(4);
            cusType = cursor.getString(5);
            cusCode = cursor.getString(6);
            jsonData = cursor.getString(7);
            syncStatus = cursor.getString(8);
            if (!cursor.getString(9).isEmpty() && !cursor.getString(9).equalsIgnoreCase("null"))
                syncCount = Integer.parseInt(cursor.getString(9));

            if (syncStatus.isEmpty()) {
                deleteOfflineCalls(cusCode, cusName, dates);
            }

            if (dates != null && !syncStatus.isEmpty()) {
                list.add(new OutBoxCallList(cusName, cusCode, dates, jsonData, syncStatus, syncCount));
            }
        }

        db.close();
        return list;
    }

    //----------------------------Tour Plan----------------------

    //Offline table
  --- Has been moved to room database. -> TourPlanOfflineDataDao, TourPlanOfflineDataTable

    public void saveTPData(String month, String data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TP_MONTH, month);
        contentValues.put(TP_DATA, data);

        String[] args = new String[]{month};
        int updated = db.update(TOUR_PLAN_OFFLINE_TABLE, contentValues, TP_MONTH + "=?", args);
        if (updated <= 0) {
            db.insert(TOUR_PLAN_OFFLINE_TABLE, null, contentValues);
        }
        db.close();
    }

    public void saveMonthlySyncStatus(String month, String status) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TP_MONTH, month);
        contentValues.put(TP_MONTH_STATUS, status);

        String[] args = new String[]{month};
        int updated = db.update(TOUR_PLAN_OFFLINE_TABLE, contentValues, TP_MONTH + "=?", args);
        if (updated <= 0) {
            db.insert(TOUR_PLAN_OFFLINE_TABLE, null, contentValues);
        }
        db.close();
    }

    public void saveMonthlySyncStatusMaster(String month, String status, String rejectionReason) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TP_MONTH, month);
        contentValues.put(TP_MONTH_STATUS, status);
        contentValues.put(TP_REJECTION_REASON, rejectionReason);

        String[] args = new String[]{month};
        int updated = db.update(TOUR_PLAN_OFFLINE_TABLE, contentValues, TP_MONTH + "=?", args);
        if (updated <= 0) {
            db.insert(TOUR_PLAN_OFFLINE_TABLE, null, contentValues);
        }
        db.close();
    }

    public JSONArray getTPDataOfMonth(String month) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TOUR_PLAN_OFFLINE_TABLE + " where " + TP_MONTH + "='" + month + "';", null);
        String data = "";
        if (cursor.moveToNext()) data = cursor.getString(1);

        cursor.close();
        db.close();
        JSONArray jsonArray = new JSONArray();

        try {
            if (!data.isEmpty()) jsonArray = new JSONArray(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    public String getMonthlySyncStatus(String month, String required) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TOUR_PLAN_OFFLINE_TABLE + " where " + TP_MONTH + "='" + month + "';", null);
        String data = "";
        if (cursor.moveToNext()) {
            if (required.equalsIgnoreCase("status")) data = cursor.getString(2);
            if (required.equalsIgnoreCase("reason")) data = cursor.getString(4);
        }
        cursor.close();
        db.close();

        if (data != null) {
            return data;
        } else {
            return "";
        }
    }

    public String getRejectionReasonOfMonth(String month) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TOUR_PLAN_OFFLINE_TABLE + " where " + TP_MONTH + "='" + month + "';", null);
        String data = "";
        if (cursor.moveToNext()) {
            data = cursor.getString(3);
        }
        cursor.close();
        db.close();
        return data;
    }

    --- Has been moved to room database. -> TourPlanOnlineDataDao, TourPlanOnlineDataTable
    //Online table
    public void saveTPDataOnlineTable(String month, String data, String status, String rejectionReason) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TP_MONTH, month);
        contentValues.put(TP_DATA, data);
        contentValues.put(TP_APPROVAL_STATUS, status);
        contentValues.put(TP_REJECTION_REASON, rejectionReason);

        String[] args = new String[]{month};
        int updated = db.update(TOUR_PLAN_ONLINE_TABLE, contentValues, TP_MONTH + "=?", args);
        if (updated <= 0) {
            db.insert(TOUR_PLAN_ONLINE_TABLE, null, contentValues);
        }
        db.close();
    }

    public JSONArray getTPDataOnlineTable(String month) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + TOUR_PLAN_ONLINE_TABLE + " where " + TP_MONTH + "='" + month + "';", null);
        String data = "";
        if (cursor.moveToNext()) data = cursor.getString(1);

        cursor.close();
        db.close();
        JSONArray jsonArray = new JSONArray();

        try {
            if (!data.isEmpty()) jsonArray = new JSONArray(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonArray;
    }

    //--------------Presentation Table-------------------
    --- Has been moved to room database. -> PresentationDataDao, PresentationDataTable
    public void savePresentation(String oldName, String newName, String data) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(PRESENTATION_NAME, newName);
        contentValues.put(PRESENTATION_DATA, data);

        if (!oldName.isEmpty()) {
            String[] args = new String[]{oldName};
            int updated = db.update(PRESENTATION_TABLE, contentValues, PRESENTATION_NAME + "=?", args);
            if (updated <= 0) {
                db.insert(PRESENTATION_TABLE, null, contentValues);
            }
        } else {
            db.insert(PRESENTATION_TABLE, null, contentValues);
        }

        db.close();
    }

    public ArrayList<BrandModelClass.Presentation> getPresentationData() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + PRESENTATION_TABLE, null);
        String data = "";
        ArrayList<BrandModelClass.Presentation> arrayList = new ArrayList<>();
        while (cursor.moveToNext()) {
            data = cursor.getString(1);
            if (data != null) {
                Type type = new TypeToken<BrandModelClass.Presentation>() {
                }.getType();
                BrandModelClass.Presentation presentations = new Gson().fromJson(data, type);
                arrayList.add(presentations);
            }
        }
        db.close();
        return arrayList;
    }

    public boolean presentationExists(String presentationName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("select * from " + PRESENTATION_TABLE + " where " + PRESENTATION_NAME + "='" + presentationName + "';", null);
        return cursor.moveToNext();
    }

    public boolean presentationDelete(String presentationName) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(PRESENTATION_TABLE, PRESENTATION_NAME + "='" + presentationName + "'", null) > 0;
    }

    //---------------------------------------------
    --- Has been moved to room database. -> CallTableDao, CallsLinechartTable

    public void deleteLineChart(String cusCode, String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + LINE_CHAT_DATA_TABLE + " WHERE " + LINECHAR_CUSTCODE + " = '" + cusCode + "' " + " AND " + LINECHAR_DCR_DT + "='" + date + "';");
        db.close();
    }

    public int getcurrentmonth_calls_count(String CustType) {
        SQLiteDatabase db = this.getReadableDatabase();
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1;

        String query = "SELECT COUNT(*) FROM " + LINE_CHAT_DATA_TABLE +
                " WHERE " + LINECHAR_MNTH + " = ? AND " + LINECHAR_CUSTTYPE + " = ?";
        String[] selectionArgs = { String.valueOf(currentMonth), CustType };

        Cursor cursor = db.rawQuery(query, selectionArgs);
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }


    public int getfeildworkcount( String cutype,String startDate, String endDate) {
        SQLiteDatabase db = this.getReadableDatabase();
        String Fieldwork = "F";

        String query = "SELECT COUNT(DISTINCT " + LINECHAR_DCR_DT + ") FROM " + LINE_CHAT_DATA_TABLE + " WHERE " + LINECHAR_DCR_DT + " >= '" + startDate + "' " + " AND " + LINECHAR_DCR_DT + " <= '" + endDate + "' " + " AND " + LINECHAR_FM_INDICATOR + " = '" + Fieldwork + "' " + " AND " + LINECHAR_CUSTTYPE + " = '" + cutype + "'";
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

*/
}