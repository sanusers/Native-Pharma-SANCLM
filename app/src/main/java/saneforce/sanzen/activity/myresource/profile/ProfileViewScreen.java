package saneforce.sanzen.activity.myresource.profile;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.databinding.ActivityProfileViewBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.utility.TimeUtils;

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
                    try {
                        String fullDate = joinDateObj.optString("date");
                        joinDate = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_1, TimeUtils.FORMAT_5, fullDate);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                JSONObject dobObj = profileObject.optJSONObject("DOB");
                String dob = "";
                if (dobObj != null ) {
                    try {
                        String fullDate = dobObj.optString("date");
                        if (fullDate.contains("1900")) {
                            dob = "";
                        } else {
                            dob = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_1, TimeUtils.FORMAT_5, fullDate);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                JSONObject dowObj = profileObject.optJSONObject("DOW");
                String dow = "";
                if (dowObj != null) {
                    try {
                        String fullDate = dowObj.optString("date");
                        if (fullDate.contains("1900")) {
                            dow = "";
                        } else {
                            dow = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_1, TimeUtils.FORMAT_5, fullDate);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
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


                activityProfileViewScreenBinding.txtDr.setText(this.getString(R.string.division) + " :" );
                activityProfileViewScreenBinding.txtHq.setText(this.getString(R.string.sub_division) + " :" );
                activityProfileViewScreenBinding.txtTerritory.setText(this.getString(R.string.territory) + " :" );
                activityProfileViewScreenBinding.desig.setText(this.getString(R.string.designation) + " :" );
                activityProfileViewScreenBinding.state.setText(this.getString(R.string.state) + " :" );
                activityProfileViewScreenBinding.empCode.setText(this.getString(R.string.employe_code) + " :" );
                activityProfileViewScreenBinding.rptMgr.setText(this.getString(R.string.rpt_mgr) + " :" );
                activityProfileViewScreenBinding.joinDt.setText(this.getString(R.string.join_dt) + " :" );
                activityProfileViewScreenBinding.phone.setText(this.getString(R.string.mobile) + " :" );
                activityProfileViewScreenBinding.email.setText(this.getString(R.string.email) + " :" );
                activityProfileViewScreenBinding.dob.setText(this.getString(R.string.Dateofbirth) + " :" );
                activityProfileViewScreenBinding.dow.setText(this.getString(R.string.Dateofwedding) + " :" );

                activityProfileViewScreenBinding.profileContainer.removeAllViews();

                List<String> skipKeys = Arrays.asList(
                        "SfName", "Designation", "SfCode_Static", "HQName",
                        "DivisionName", "SubdivisionNames", "EmployeeCode",
                        "StateName", "ReportingMgrName", "SF_JoiningDate",
                        "DOB", "DOW", "Mobile", "Email"
                );

                Iterator<String> keys = profileObject.keys();
                List<String> dynamicKeys = new ArrayList<>();

                while (keys.hasNext()) {
                    String k = keys.next();
                    if (!skipKeys.contains(k)) dynamicKeys.add(k);
                }

                for (int i = 0; i < dynamicKeys.size(); i += 2) {
                    View row = getLayoutInflater().inflate(R.layout.activity_profile_rows, activityProfileViewScreenBinding.profileContainer, false);

                    //  Column 1 (Left Side)
                    String key1 = dynamicKeys.get(i);
                    TextView tvKey1 = row.findViewById(R.id.key);
                    TextView tvValue1 = row.findViewById(R.id.keys_name);

                    tvKey1.setText(key1.replaceAll("([a-z])([A-Z])", "$1 $2").replace("_", " ") + " :");
                    tvValue1.setText(profileObject.optString(key1));

                    //  Column 2 (Right Side)

                    TextView tvKey2 = row.findViewById(R.id.values);
                    TextView tvValue2 = row.findViewById(R.id.values_txt);

                    if (i + 1 < dynamicKeys.size()) {
                        String key2 = dynamicKeys.get(i + 1);


                        tvKey2.setText(key2.replaceAll("([a-z])([A-Z])", "$1 $2").replace("_", " ") + " :");
                        tvValue2.setText(profileObject.optString(key2));
                    } else {
                        // Oru row-la single data mattum irundha, 2nd side-ai empty-ah vaiyunga
                        tvKey2.setText("");
                        tvValue2.setText("");
                    }

                    activityProfileViewScreenBinding.profileContainer.addView(row);
                }


                /* String tp_rpt = profileObject.optString("TpMgrName");
                String lev_rpt = profileObject.optString("LeaveMgrName");
                String tp_range = "Every Month"+" "+profileObject.optString("Tp_Start_Date") + "-" +profileObject.optString("Tp_End_Date");*/
                /*activityProfileViewScreenBinding.startDtTxt.setText(sfdcrDate);*/
                /*activityProfileViewScreenBinding.tpStartDtTxt.setText(sftpDate);*/
               /* activityProfileViewScreenBinding.tpRptName.setText(tp_rpt);
                activityProfileViewScreenBinding.levRptName.setText(lev_rpt);
                activityProfileViewScreenBinding.tpMandRangeTxt.setText(tp_range);*/
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        activityProfileViewScreenBinding.ivBack.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

    }

}
