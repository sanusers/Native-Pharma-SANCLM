package saneforce.santrip.roomdatabase;

import static android.app.PendingIntent.getActivity;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.fragment.app.Fragment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import saneforce.santrip.activity.homeScreen.HomeDashBoard;
import saneforce.santrip.activity.homeScreen.fragment.CallAnalysisFragment;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.roomdatabase.CallTableDetails.CallTableDao;
import saneforce.santrip.roomdatabase.CallTableDetails.CallsLinechartTable;
import saneforce.santrip.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.santrip.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.santrip.storage.SQLite;

public class CallDataRestClass {

    static CallTableDao callTableDao;
    static MasterDataDao masterDataDao;
    static  RoomDB db;
    static Context contextt;
    public static void resetcallValues(Context context){
     try {
         contextt=context;

         db = RoomDB.getDatabase(context);
         callTableDao = db.callTableDao();
         masterDataDao=db.masterDataDao();
         callTableDao.deleteAllData();

         JSONArray dcrdatas = new JSONArray(masterDataDao.getDataByKey(Constants.CALL_SYNC));
         for (int i = 0; i < dcrdatas.length(); i++) {
             JSONObject jsonObject = dcrdatas.getJSONObject(i);
             CallsLinechartTable data = new CallsLinechartTable();
             data.setLINECHAR_CUSTCODE(jsonObject.optString("CustCode"));
             data.setLINECHAR_CUSTTYPE(jsonObject.optString("CustType"));
             data.setLINECHAR_DCR_DT(jsonObject.optString("Dcr_dt"));
             data.setLINECHAR_MONTH_NAME(jsonObject.optString("month_name"));
             data.setLINECHAR_MNTH(jsonObject.optString("Mnth"));
             data.setLINECHAR_YR(jsonObject.optString("Yr"));
             data.setLINECHAR_CUSTNAME(jsonObject.optString("CustName"));
             data.setLINECHAR_TOWN_CODE(jsonObject.optString("town_code"));
             data.setLINECHAR_TOWN_NAME(jsonObject.optString("town_name"));
             data.setLINECHAR_DCR_FLAG(jsonObject.optString("Dcr_flag"));
             data.setLINECHAR_SF_CODE(jsonObject.optString("SF_Code"));
             data.setLINECHAR_TRANS_SLNO(jsonObject.optString("Trans_SlNo"));
             data.setLINECHAR_FM_INDICATOR(jsonObject.optString("FW_Indicator"));
             data.setLINECHAR_AMSLNO(jsonObject.optString("AMSLNo"));


             Calendar calendar = Calendar.getInstance();
             int CurrentMonth = calendar.get(Calendar.MONTH)+1;
             if(jsonObject.optString("Mnth").equalsIgnoreCase(String.valueOf(CurrentMonth))){
                 String[] date = jsonObject.optString("Dcr_dt").split("-");
                 if(Integer.parseInt(date[2])<=15){
                     data.setLINECHAR_DATE_FLOG("A1");
                 }else {
                     data.setLINECHAR_DATE_FLOG("A2");
                 }

             }else  if(jsonObject.optString("Mnth").equalsIgnoreCase(String.valueOf(CurrentMonth-1))){
                 String[] date = jsonObject.optString("Dcr_dt").split("-");
                 if(Integer.parseInt(date[2])<=15){
                     data.setLINECHAR_DATE_FLOG("B1");
                 }else {
                     data.setLINECHAR_DATE_FLOG("B2");
                 }

             }else  if(jsonObject.optString("Mnth").equalsIgnoreCase(String.valueOf(CurrentMonth-2))){
                 String[] date = jsonObject.optString("Dcr_dt").split("-");
                 if(Integer.parseInt(date[2])<=15){
                     data.setLINECHAR_DATE_FLOG("C1");
                 }else {
                     data.setLINECHAR_DATE_FLOG("C2");
                 }

             }
             callTableDao.insert(data);

         }

         CallAnalysisFragment.SetcallDetailsInLineChart();

     }catch (Exception a){
         a.printStackTrace();
     }
    }




}
