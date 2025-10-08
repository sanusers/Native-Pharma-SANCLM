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

//        MasterDataTable profile = masterDataDao.getMasterDataTableOrNew(Constants.PROFILE);
        JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.PROFILE).getMasterSyncDataJsonArray();
        if(jsonArray != null && jsonArray.length()>0){
            try {
                JSONObject profileObject = jsonArray.getJSONObject(position);
                String name = profileObject.optString("SfName");
                String div = profileObject.optString("DivisionName");
                String sub_div = profileObject.optString("SubdivisionNames");
                String hq = profileObject.optString("HQName");
                String desig = profileObject.optString("Designation");

                JSONObject sfdcrDateObj = profileObject.optJSONObject("SFDCRDate");
                String sfdcrDate = "";
                if (sfdcrDateObj != null) {
                    String fullDate = sfdcrDateObj.optString("date");
                    if (fullDate.contains(" ")) {
                        sfdcrDate = fullDate.split(" ")[0];
                    } else {
                        sfdcrDate = fullDate;
                    }
                }

                JSONObject sftpDateObj = profileObject.optJSONObject("SFTPDate");
                String sftpDate = "";
                if (sftpDateObj != null) {
                    String fullDate = sftpDateObj.optString("date");
                    if (fullDate.contains(" ")) {
                        sftpDate = fullDate.split(" ")[0];
                    } else {
                        sftpDate = fullDate;
                    }
                }

                String dcr_rpt = profileObject.optString("DcrMgrName");
                String tp_rpt = profileObject.optString("TpMgrName");
                String lev_rpt = profileObject.optString("LeaveMgrName");
                String tp_range = profileObject.optString("Tp_Start_Date") + "-" +profileObject.optString("Tp_End_Date");

                activityProfileViewScreenBinding.userName.setText(name);
                activityProfileViewScreenBinding.division.setText(div);
                activityProfileViewScreenBinding.subDiv.setText(sub_div);
                activityProfileViewScreenBinding.hq.setText(hq);
                activityProfileViewScreenBinding.desigName.setText(desig);
                activityProfileViewScreenBinding.startDtTxt.setText(sfdcrDate);
                activityProfileViewScreenBinding.tpStartDtTxt.setText(sftpDate);


                activityProfileViewScreenBinding.dcrRptName.setText(dcr_rpt);
                activityProfileViewScreenBinding.tpRptName.setText(tp_rpt);
                activityProfileViewScreenBinding.levRptName.setText(lev_rpt);
                activityProfileViewScreenBinding.tpMandRangeTxt.setText(tp_range);





            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



        activityProfileViewScreenBinding.ivBack.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });


    }

}
