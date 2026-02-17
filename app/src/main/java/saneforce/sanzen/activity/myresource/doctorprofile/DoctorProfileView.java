package saneforce.sanzen.activity.myresource.doctorprofile;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.ActivityDoctorProfileViewBinding;
import saneforce.sanzen.databinding.ActivityProfileViewBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class DoctorProfileView extends AppCompatActivity {
    public ActivityDoctorProfileViewBinding activityDoctorProfileViewBinding;
    MasterDataDao masterDataDao;
    private RoomDB roomDB;
    int position;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityDoctorProfileViewBinding = ActivityDoctorProfileViewBinding.inflate(getLayoutInflater());
        setContentView(activityDoctorProfileViewBinding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        roomDB = RoomDB.getDatabase(this);
        position = getIntent().getIntExtra("position", 0);
        masterDataDao = roomDB.masterDataDao();

        JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR_MAS + SharedPref.getHqCode(this)).getMasterSyncDataJsonArray();
        //JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR_MAS).getMasterSyncDataJsonArray();
        if (jsonArray != null && jsonArray.length() > 0) {
            try {
                JSONObject doctorProfileObject = jsonArray.getJSONObject(position);
                String name = doctorProfileObject.optString("Name");
                String gender = doctorProfileObject.optString("ListedDr_Sex");
                String qualification = doctorProfileObject.optString("Qualification");
                String category = doctorProfileObject.optString("Category");
                String speciality = doctorProfileObject.optString("Specialty");
                String territory = doctorProfileObject.optString("Town_Name");
                String mobile = doctorProfileObject.optString("Mobile");
                String phone = doctorProfileObject.optString("Phone");
                String email = doctorProfileObject.optString("DrEmail");
                String address = doctorProfileObject.optString("cus_addr");


                JSONObject dobObj = doctorProfileObject.optJSONObject("DctrDOB");
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
                JSONObject dowObj = doctorProfileObject.optJSONObject("DctrDOW");
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




                activityDoctorProfileViewBinding.tvName.setText(setValue(name));
                activityDoctorProfileViewBinding.tvGender.setText(setValue(gender));
                activityDoctorProfileViewBinding.tvQualify.setText(setValue(qualification));
                activityDoctorProfileViewBinding.tvCategory.setText(setValue(category));
                activityDoctorProfileViewBinding.tvSpeciality.setText(setValue(speciality));
                activityDoctorProfileViewBinding.tvTerritory.setText(setValue(territory));
                activityDoctorProfileViewBinding.tvMob.setText(setValue(mobile));
                activityDoctorProfileViewBinding.tvPhone.setText(setValue(phone));
                activityDoctorProfileViewBinding.tvEmail.setText(setValue(email));
                activityDoctorProfileViewBinding.tvAddress.setText(setValue(address));
                activityDoctorProfileViewBinding.tvDob.setText(setValue(dob));
                activityDoctorProfileViewBinding.tvWedDate.setText(setValue(dow));



            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


            activityDoctorProfileViewBinding.ivBack.setOnClickListener(v -> {

                finish();
            });

    }
    private String setValue(String value) {
        if (value == null || value.trim().isEmpty() || value.equalsIgnoreCase("null")) {
            return "--";
        }
        return value;
    }
}