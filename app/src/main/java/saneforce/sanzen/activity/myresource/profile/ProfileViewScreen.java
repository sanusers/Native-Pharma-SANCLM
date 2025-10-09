package saneforce.sanzen.activity.myresource.profile;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.ActivityProfileViewBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.sanzen.roomdatabase.RoomDB;


public class ProfileViewScreen extends AppCompatActivity {
    public ActivityProfileViewBinding activityProfileViewScreenBinding;
    MasterDataDao masterDataDao;
    private RoomDB roomDB;
    int position;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityProfileViewScreenBinding = ActivityProfileViewBinding.inflate(getLayoutInflater());
        setContentView(activityProfileViewScreenBinding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();
        JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.PROFILE).getMasterSyncDataJsonArray();
        if(jsonArray != null && jsonArray.length()>0){
            try {
                JSONObject profileObject = jsonArray.getJSONObject(position);
                String name = profileObject.optString("SfName");
                String div = profileObject.optString("DivisionName");
                String sub_div = profileObject.optString("SubdivisionNames");
                String hq = profileObject.optString("HQName");
                String desig = profileObject.optString("Designation");
                String state = profileObject.optString("StateName");
                String emp_code = profileObject.optString("EmployeeCode");
                String rpt_mgr = profileObject.optString("ReportingMgrName");
                String phone = profileObject.optString("Mobile");
                String email = profileObject.optString("Email");

                JSONObject joinDateObj = profileObject.optJSONObject("SF_JoiningDate");
                String joinDate = "";
                if (joinDateObj != null) {
                    String fullDate = joinDateObj.optString("date");
                    if (fullDate.contains(" ")) {
                        joinDate = fullDate.split(" ")[0];
                    } else {
                        joinDate = fullDate;
                    }
                }
                JSONObject dobObj = profileObject.optJSONObject("DOB");
                String dob = "";
                if (dobObj != null) {
                    String fullDate = dobObj.optString("date");
                    if (fullDate.contains(" ")) {
                        dob = fullDate.split(" ")[0];
                    } else {
                        dob = fullDate;
                    }
                }
                JSONObject dowObj = profileObject.optJSONObject("DOW");
                String dow = "";
                if (dowObj != null) {
                    String fullDate = dowObj.optString("date");
                    if (fullDate.contains(" ")) {
                        dow = fullDate.split(" ")[0];
                    } else {
                        dow = fullDate;
                    }
                }

                activityProfileViewScreenBinding.userName.setText(name);
                activityProfileViewScreenBinding.division.setText(div);
                activityProfileViewScreenBinding.subDiv.setText(sub_div);
                activityProfileViewScreenBinding.hq.setText(hq);
                activityProfileViewScreenBinding.desigName.setText(desig);
                activityProfileViewScreenBinding.stateTxt.setText(state);
                activityProfileViewScreenBinding.empCodeTxt.setText(emp_code);
                activityProfileViewScreenBinding.rptMgrName.setText(rpt_mgr);
                activityProfileViewScreenBinding.joinDtName.setText(joinDate);
                activityProfileViewScreenBinding.phoneTxt.setText(phone);
                activityProfileViewScreenBinding.emailTxt.setText(email);
                activityProfileViewScreenBinding.dobTxt.setText(dob);
                activityProfileViewScreenBinding.dowTxt.setText(dow);









                /* String tp_rpt = profileObject.optString("TpMgrName");
                String lev_rpt = profileObject.optString("LeaveMgrName");
                String tp_range = "Every Month"+" "+profileObject.optString("Tp_Start_Date") + "-" +profileObject.optString("Tp_End_Date");
*/

                /*activityProfileViewScreenBinding.startDtTxt.setText(sfdcrDate);*/
                /*activityProfileViewScreenBinding.tpStartDtTxt.setText(sftpDate);*/
               /* activityProfileViewScreenBinding.tpRptName.setText(tp_rpt);
                activityProfileViewScreenBinding.levRptName.setText(lev_rpt);
                activityProfileViewScreenBinding.tpMandRangeTxt.setText(tp_range);*/


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



        activityProfileViewScreenBinding.ivBack.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });


    }

}
