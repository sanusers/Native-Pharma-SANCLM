package saneforce.sanzen.activity.myresource.doctorprofile;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.ActivityDoctorProfileViewBinding;
import saneforce.sanzen.databinding.ActivityProfileViewBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class DoctorProfileView extends AppCompatActivity {
    public ActivityDoctorProfileViewBinding activityDoctorProfileViewBinding;
    String CusType = "";
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
        Bundle extra = getIntent().getExtras();

        if (extra != null) {
            CusType = extra.getString("PosDCRname", "");
            switch (CusType) {
                case "D":
                    if (SharedPref.getDrCap(this).isEmpty() || SharedPref.getDrCap(this) == null) {
                        activityDoctorProfileViewBinding.tagSelection.setText(getResources().getString(R.string.txt_doctor) + " " + "Profile");
                    } else {
                        activityDoctorProfileViewBinding.tagSelection.setText(SharedPref.getDrCap(this) + " " + "profile");
                    }
                    JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR_MAS + SharedPref.getHqCode(this)).getMasterSyncDataJsonArray();
                    //JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR_MAS).getMasterSyncDataJsonArray();
                    if (jsonArray != null && jsonArray.length() > 0) {
                        try {
                            JSONObject doctorProfileObject = jsonArray.getJSONObject(position);
                            String name = doctorProfileObject.optString("Name");
                            String gender = doctorProfileObject.optString("ListedDr_Sex");
                            String qualification = doctorProfileObject.optString("DrDesig");
                            String category = doctorProfileObject.optString("Category");
                            String speciality = doctorProfileObject.optString("Specialty");
                            String territory = doctorProfileObject.optString("Town_Name");
                            String mobile = doctorProfileObject.optString("Mobile");
                            String phone = doctorProfileObject.optString("Phone");
                            String email = doctorProfileObject.optString("DrEmail");
                            String address = doctorProfileObject.optString("cus_addr");
                            String classShortName = doctorProfileObject.optString("Doc_Class_ShortName");


                            JSONObject dobObj = doctorProfileObject.optJSONObject("DctrDOB");
                            String dob = "";
                            if (dobObj != null) {
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
                            activityDoctorProfileViewBinding.tvClass.setText(setValue(classShortName));
                            activityDoctorProfileViewBinding.tvDob.setText(setValue(dob));
                            activityDoctorProfileViewBinding.tvWedDate.setText(setValue(dow));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case "C":
                    if (SharedPref.getChmCap(this).isEmpty() || SharedPref.getChmCap(this) == null) {
                        activityDoctorProfileViewBinding.tagSelection.setText(getResources().getString(R.string.txt_chemist) + " " + "Profile");
                    } else {
                        activityDoctorProfileViewBinding.tagSelection.setText(SharedPref.getChmCap(this) + " " + "profile");
                    }
                    JSONArray chmJsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CHEMIST_MAS + SharedPref.getHqCode(this)).getMasterSyncDataJsonArray();

                    if (chmJsonArray != null && chmJsonArray.length() > 0) {
                        try {
                            JSONObject chmJsonArrayJSONObject = chmJsonArray.getJSONObject(position);
                            String chmName = chmJsonArrayJSONObject.optString("Name");
                            String chmGender = chmJsonArrayJSONObject.optString("ListedDr_Sex");
                            String chmQualification = chmJsonArrayJSONObject.optString("Qualification");
                            String chmCategory = chmJsonArrayJSONObject.optString("Chm_cat");
                            String chmSpeciality = chmJsonArrayJSONObject.optString("Specialty");
                            String chmTerritory = chmJsonArrayJSONObject.optString("Town_Name");
                            String chmMobile = chmJsonArrayJSONObject.optString("Chemists_Mobile");
                            String chmPhone = chmJsonArrayJSONObject.optString("Chemists_Phone");
                            String chmEmail = chmJsonArrayJSONObject.optString("Chemists_Email");
                            String chmAddress = chmJsonArrayJSONObject.optString("cus_addr");

                            JSONArray categoryArray = masterDataDao
                                    .getMasterDataTableOrNew(Constants.CATEGORY)
                                    .getMasterSyncDataJsonArray();

                            if (categoryArray != null) {
                                for (int i = 0; i < categoryArray.length(); i++) {
                                    JSONObject catObj = categoryArray.getJSONObject(i);
                                    String code = catObj.optString("Code");

                                    if (code.equals(chmCategory)) {
                                        chmCategory = catObj.optString("Doc_Cat_Name");
                                        break;
                                    }
                                }
                            }

                            JSONObject chmDobObj = chmJsonArrayJSONObject.optJSONObject("DctrDOB");
                            String chmDob = "";
                            if (chmDobObj != null) {
                                try {
                                    String fullDate = chmDobObj.optString("date");
                                    if (fullDate.contains("1900")) {
                                        chmDob = "";
                                    } else {
                                        chmDob = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_1, TimeUtils.FORMAT_5, fullDate);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            JSONObject chmDowObj = chmJsonArrayJSONObject.optJSONObject("DctrDOW");
                            String chmDow = "";
                            if (chmDowObj != null) {
                                try {
                                    String fullDate = chmDowObj.optString("date");
                                    if (fullDate.contains("1900")) {
                                        chmDow = "";
                                    } else {
                                        chmDow = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_1, TimeUtils.FORMAT_5, fullDate);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                            activityDoctorProfileViewBinding.tvName.setText(setValue(chmName));
                            activityDoctorProfileViewBinding.tvGender.setText(setValue(chmGender));
                            activityDoctorProfileViewBinding.tvQualify.setText(setValue(chmQualification));
                            activityDoctorProfileViewBinding.tvCategory.setText(setValue(chmCategory));
                            activityDoctorProfileViewBinding.tvSpeciality.setText(setValue(chmSpeciality));
                            activityDoctorProfileViewBinding.tvTerritory.setText(setValue(chmTerritory));
                            activityDoctorProfileViewBinding.tvMob.setText(setValue(chmMobile));
                            activityDoctorProfileViewBinding.tvPhone.setText(setValue(chmPhone));
                            activityDoctorProfileViewBinding.tvEmail.setText(setValue(chmEmail));
                            activityDoctorProfileViewBinding.tvAddress.setText(setValue(chmAddress));
                            activityDoctorProfileViewBinding.tvDob.setText(setValue(chmDob));
                            activityDoctorProfileViewBinding.tvWedDate.setText(setValue(chmDow));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case "S":
                    if (SharedPref.getStkCap(this).isEmpty() || SharedPref.getStkCap(this) == null) {
                        activityDoctorProfileViewBinding.tagSelection.setText(getResources().getString(R.string.txt_stockist) + " " + "Profile");
                    } else {
                        activityDoctorProfileViewBinding.tagSelection.setText(SharedPref.getStkCap(this) + " " + "profile");
                    }
                    JSONArray stkJsonArray = masterDataDao.getMasterDataTableOrNew(Constants.STOCKIEST_MAS + SharedPref.getHqCode(this)).getMasterSyncDataJsonArray();
                    if (stkJsonArray != null && stkJsonArray.length() > 0) {
                        try {
                            JSONObject stkJsonArrayJSONObject = stkJsonArray.getJSONObject(position);
                            String stkName = stkJsonArrayJSONObject.optString("Name");
                            String stkGender = stkJsonArrayJSONObject.optString("ListedDr_Sex");
                            String stkQualification = stkJsonArrayJSONObject.optString("Qualification");
                            String stkCategory = stkJsonArrayJSONObject.optString("Chm_cat");
                            String stkSpeciality = stkJsonArrayJSONObject.optString("Specialty");
                            String stkTerritory = stkJsonArrayJSONObject.optString("Town_Name");
                            String stkMobile = stkJsonArrayJSONObject.optString("Stockiest_Mobile");
                            String stkPhone = stkJsonArrayJSONObject.optString("Stockiest_Phone");
                            String stkEmail = stkJsonArrayJSONObject.optString("Stockiest_Email");
                            String stkAddress = stkJsonArrayJSONObject.optString("cus_addr");


                            JSONObject stkDobObj = stkJsonArrayJSONObject.optJSONObject("DctrDOB");
                            String stkDob = "";
                            if (stkDobObj != null) {
                                try {
                                    String fullDate = stkDobObj.optString("date");
                                    if (fullDate.contains("1900")) {
                                        stkDob = "";
                                    } else {
                                        stkDob = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_1, TimeUtils.FORMAT_5, fullDate);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            JSONObject stkDowObj = stkJsonArrayJSONObject.optJSONObject("DctrDOW");
                            String stkDow = "";
                            if (stkDowObj != null) {
                                try {
                                    String fullDate = stkDowObj.optString("date");
                                    if (fullDate.contains("1900")) {
                                        stkDow = "";
                                    } else {
                                        stkDow = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_1, TimeUtils.FORMAT_5, fullDate);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }



                            activityDoctorProfileViewBinding.tvName.setText(setValue(stkName));
                            activityDoctorProfileViewBinding.tvGender.setText(setValue(stkGender));
                            activityDoctorProfileViewBinding.tvQualify.setText(setValue(stkQualification));
                            activityDoctorProfileViewBinding.tvCategory.setText(setValue(stkCategory));
                            activityDoctorProfileViewBinding.tvSpeciality.setText(setValue(stkSpeciality));
                            activityDoctorProfileViewBinding.tvTerritory.setText(setValue(stkTerritory));
                            activityDoctorProfileViewBinding.tvMob.setText(setValue(stkMobile));
                            activityDoctorProfileViewBinding.tvPhone.setText(setValue(stkPhone));
                            activityDoctorProfileViewBinding.tvEmail.setText(setValue(stkEmail));
                            activityDoctorProfileViewBinding.tvAddress.setText(setValue(stkAddress));
                            activityDoctorProfileViewBinding.tvDob.setText(setValue(stkDob));
                            activityDoctorProfileViewBinding.tvWedDate.setText(setValue(stkDow));



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;

                case "U":
                    if (SharedPref.getUNLcap(this).isEmpty() || SharedPref.getUNLcap(this) == null) {
                        activityDoctorProfileViewBinding.tagSelection.setText(getResources().getString(R.string.txt_undr) + " " + "Profile");
                    } else {
                        activityDoctorProfileViewBinding.tagSelection.setText(SharedPref.getUNLcap(this) + " " + "profile");
                    }

                    JSONArray unlistJsonArray = masterDataDao.getMasterDataTableOrNew(Constants.UNLISTED_DOCTOR_MAS + SharedPref.getHqCode(this)).getMasterSyncDataJsonArray();
                    if (unlistJsonArray != null && unlistJsonArray.length() > 0) {
                        try {
                            JSONObject unlistJsonArrayJSONObject = unlistJsonArray.getJSONObject(position);
                            String unlistName = unlistJsonArrayJSONObject.optString("Name");
                            String unlistGender = unlistJsonArrayJSONObject.optString("ListedDr_Sex");
                            String unlistQualification = unlistJsonArrayJSONObject.optString("Doc_QuaName");
                            String unlistCategory = unlistJsonArrayJSONObject.optString("CategoryName");
                            String unlistSpeciality = unlistJsonArrayJSONObject.optString("SpecialtyName");
                            String unlistTerritory = unlistJsonArrayJSONObject.optString("Town_Name");
                            String unlistMobile = unlistJsonArrayJSONObject.optString("Mobile");
                            String unlistPhone = unlistJsonArrayJSONObject.optString("Phone");
                            String unlistEmail = unlistJsonArrayJSONObject.optString("Email");
                            String unlistAddress = unlistJsonArrayJSONObject.optString("cus_addr");


                            JSONObject unlistDobObj = unlistJsonArrayJSONObject.optJSONObject("UnlstDOB");
                            String unlistDob = "";
                            if (unlistDobObj != null) {
                                try {
                                    String fullDate = unlistDobObj.optString("date");
                                    if (fullDate.contains("1900")) {
                                        unlistDob = "";
                                    } else {
                                        unlistDob = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_1, TimeUtils.FORMAT_5, fullDate);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                            JSONObject stkDowObj = unlistJsonArrayJSONObject.optJSONObject("UnlstDOW");
                            String unlistDow = "";
                            if (stkDowObj != null) {
                                try {
                                    String fullDate = stkDowObj.optString("date");
                                    if (fullDate.contains("1900")) {
                                       unlistDow = "";
                                    } else {
                                        unlistDow = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_1, TimeUtils.FORMAT_5, fullDate);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }



                            activityDoctorProfileViewBinding.tvName.setText(setValue(unlistName));
                            activityDoctorProfileViewBinding.tvGender.setText(setValue(unlistGender));
                            activityDoctorProfileViewBinding.tvQualify.setText(setValue(unlistQualification));
                            activityDoctorProfileViewBinding.tvCategory.setText(setValue(unlistCategory));
                            activityDoctorProfileViewBinding.tvSpeciality.setText(setValue(unlistSpeciality));
                            activityDoctorProfileViewBinding.tvTerritory.setText(setValue(unlistTerritory));
                            activityDoctorProfileViewBinding.tvMob.setText(setValue(unlistMobile));
                            activityDoctorProfileViewBinding.tvPhone.setText(setValue(unlistPhone));
                            activityDoctorProfileViewBinding.tvEmail.setText(setValue(unlistEmail));
                            activityDoctorProfileViewBinding.tvAddress.setText(setValue(unlistAddress));
                            activityDoctorProfileViewBinding.tvDob.setText(setValue(unlistDob));
                            activityDoctorProfileViewBinding.tvWedDate.setText(setValue(unlistDow));



                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    break;

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