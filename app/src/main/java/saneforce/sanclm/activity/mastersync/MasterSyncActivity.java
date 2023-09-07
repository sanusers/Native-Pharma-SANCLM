package saneforce.sanclm.activity.mastersync;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanclm.R;
import saneforce.sanclm.TestMasterSyncActivity;
import saneforce.sanclm.activity.login.LoginActivity;
import saneforce.sanclm.common.Constants;
import saneforce.sanclm.databinding.ActivityMasterSyncBinding;
import saneforce.sanclm.network.ApiInterface;
import saneforce.sanclm.network.RetrofitClient;
import saneforce.sanclm.response.GetDrMasterResponse;
import saneforce.sanclm.response.LoginResponse;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SharedPref;


public class MasterSyncActivity extends AppCompatActivity {

    ActivityMasterSyncBinding binding;
    ApiInterface apiInterface;
    SQLite sqLite;
    LoginResponse loginResponse;
    String sfCode = "",division_code = "",rsf ="",sf_type = "",designation = "",state_code ="",subdivision_code = "";
    int doctorCount = 0,specialityCount = 0,qualificationCount = 0,categoryCount = 0,departmentCount = 0,classCount = 0,feedbackCount = 0,competitorCount = 0;
    int unlistedDrCount = 0,chemistCount = 0,stockiestCount = 0,hospitalCount = 0,cipCount = 0;

    ArrayList<MasterSyncItemModel> doctorModel = new ArrayList<>();
    ArrayList<MasterSyncItemModel> unlistedDrModel = new ArrayList<>();
    ArrayList<MasterSyncItemModel> chemistModel = new ArrayList<>();


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMasterSyncBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sqLite = new SQLite(getApplicationContext());
        sqLite.getWritableDatabase();

        Cursor cursor = sqLite.getLoginData();
        loginResponse = new LoginResponse();

        String loginData = "";
        if (cursor.moveToNext()){
            loginData = cursor.getString(0);
        }
        cursor.close();
        Type type = new TypeToken<LoginResponse>() {
        }.getType();
        loginResponse = new Gson().fromJson(loginData, type);
        Log.e("test","login data from sqlite : " + new Gson().toJson(loginResponse));
        binding.hqName.setText(loginResponse.getHQName());
        sfCode = loginResponse.getSF_Code();
        sf_type = loginResponse.getSf_type();
        division_code = loginResponse.getDivision_Code();
        subdivision_code = loginResponse.getSubdivision_code();
        designation = loginResponse.getDesig();
        rsf = loginResponse.getSF_Code();
        state_code = loginResponse.getState_Code();

        try {
            uiInitialization();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


        binding.hqName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                startActivity(new Intent(MasterSyncActivity.this, TestMasterSyncActivity.class));
            }
        });

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                startActivity(new Intent(MasterSyncActivity.this, LoginActivity.class));
            }
        });

        binding.listedDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                listItemClicked(binding.listedDoctor);
                binding.masterName.setText(loginResponse.getDrCap());
                binding.splCard.setVisibility(View.VISIBLE);
                binding.qlCard.setVisibility(View.VISIBLE);

                binding.masterCount.setText(String.valueOf(doctorCount));
                binding.childSync.setText("Sync Listed Doctor");

                binding.LinearLayout1.setVisibility(View.VISIBLE);
                binding.LinearLayout2.setVisibility(View.VISIBLE);
                binding.LinearLayout3.setVisibility(View.VISIBLE);
                binding.subordinateLayout.setVisibility(View.GONE);

            }
        });

        binding.unlistedDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                listItemClicked(binding.unlistedDoctor);
                binding.masterCard.setVisibility(View.VISIBLE);
                binding.masterName.setText(loginResponse.getNLCap());
                binding.splCard.setVisibility(View.GONE);
                binding.qlCard.setVisibility(View.GONE);

                binding.masterCount.setText(String.valueOf(unlistedDrCount));
                binding.childSync.setText("Sync Unlisted Doctor");

                binding.LinearLayout1.setVisibility(View.VISIBLE);
                binding.LinearLayout2.setVisibility(View.GONE);
                binding.LinearLayout3.setVisibility(View.GONE);
                binding.subordinateLayout.setVisibility(View.GONE);

            }
        });

        binding.chemist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                listItemClicked(binding.chemist);
                binding.masterCard.setVisibility(View.VISIBLE);
                binding.masterName.setText(loginResponse.getChmCap());
                binding.splCard.setVisibility(View.GONE);
                binding.qlCard.setVisibility(View.GONE);

                binding.masterCount.setText(String.valueOf(chemistCount));
                binding.childSync.setText("Sync Chemist");

                binding.LinearLayout1.setVisibility(View.VISIBLE);
                binding.LinearLayout2.setVisibility(View.GONE);
                binding.LinearLayout3.setVisibility(View.GONE);
                binding.subordinateLayout.setVisibility(View.GONE);

            }
        });

        binding.stockiest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                listItemClicked(binding.stockiest);
                binding.masterCard.setVisibility(View.VISIBLE);
                binding.masterName.setText(loginResponse.getStkCap());
                binding.splCard.setVisibility(View.GONE);
                binding.qlCard.setVisibility(View.GONE);

                binding.masterCount.setText(String.valueOf(stockiestCount));
                binding.childSync.setText("Sync Stockiest");

                binding.LinearLayout1.setVisibility(View.VISIBLE);
                binding.LinearLayout2.setVisibility(View.GONE);
                binding.LinearLayout3.setVisibility(View.GONE);
                binding.subordinateLayout.setVisibility(View.GONE);
            }
        });

        binding.hospital.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                listItemClicked(binding.hospital);
                binding.masterCard.setVisibility(View.VISIBLE);
                binding.masterName.setText("Hospital");
                binding.splCard.setVisibility(View.GONE);
                binding.qlCard.setVisibility(View.GONE);

                binding.masterCount.setText(String.valueOf(hospitalCount));
                binding.childSync.setText("Sync Hospital");

                binding.LinearLayout1.setVisibility(View.VISIBLE);
                binding.LinearLayout2.setVisibility(View.GONE);
                binding.LinearLayout3.setVisibility(View.GONE);
                binding.subordinateLayout.setVisibility(View.GONE);
            }
        });

        binding.cip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                listItemClicked(binding.cip);
                binding.masterCard.setVisibility(View.VISIBLE);
                binding.masterName.setText("CIP");
                binding.splCard.setVisibility(View.GONE);
                binding.qlCard.setVisibility(View.GONE);

                binding.masterCount.setText(String.valueOf(cipCount));
                binding.childSync.setText("Sync CIP");

                binding.LinearLayout1.setVisibility(View.VISIBLE);
                binding.LinearLayout2.setVisibility(View.GONE);
                binding.LinearLayout3.setVisibility(View.GONE);
                binding.subordinateLayout.setVisibility(View.GONE);
            }
        });

        binding.product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                listItemClicked(binding.product);
                binding.productLayout.setVisibility(View.VISIBLE);
                binding.childSync.setText("Sync Product");

                binding.LinearLayout1.setVisibility(View.GONE);
                binding.LinearLayout2.setVisibility(View.GONE);
                binding.LinearLayout3.setVisibility(View.GONE);
                binding.subordinateLayout.setVisibility(View.GONE);

            }
        });

        binding.input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                listItemClicked(binding.input);
                binding.masterCard.setVisibility(View.VISIBLE);
                binding.masterName.setText("Input");
                binding.splCard.setVisibility(View.GONE);
                binding.qlCard.setVisibility(View.GONE);
                binding.childSync.setText("Sync Input");

                binding.LinearLayout1.setVisibility(View.VISIBLE);
                binding.LinearLayout2.setVisibility(View.GONE);
                binding.LinearLayout3.setVisibility(View.GONE);
                binding.subordinateLayout.setVisibility(View.GONE);
                binding.productLayout.setVisibility(View.GONE);
            }
        });

        binding.leave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                listItemClicked(binding.leave);
                binding.masterCard.setVisibility(View.VISIBLE);
                binding.masterName.setText("Leave");
                binding.splCard.setVisibility(View.GONE);
                binding.qlCard.setVisibility(View.GONE);
                binding.childSync.setText("Sync Leave");

                binding.LinearLayout1.setVisibility(View.VISIBLE);
                binding.LinearLayout2.setVisibility(View.GONE);
                binding.LinearLayout3.setVisibility(View.GONE);
                binding.subordinateLayout.setVisibility(View.GONE);
                binding.productLayout.setVisibility(View.GONE);
            }
        });

        binding.tourPlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                listItemClicked(binding.tourPlan);
                binding.masterCard.setVisibility(View.VISIBLE);
                binding.masterName.setText("Tour Plan");
                binding.splCard.setVisibility(View.GONE);
                binding.qlCard.setVisibility(View.GONE);
                binding.childSync.setText("Sync Tour Plan");

                binding.LinearLayout1.setVisibility(View.VISIBLE);
                binding.LinearLayout2.setVisibility(View.GONE);
                binding.LinearLayout3.setVisibility(View.GONE);
                binding.subordinateLayout.setVisibility(View.GONE);
                binding.productLayout.setVisibility(View.GONE);
            }
        });

        binding.slide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                listItemClicked(binding.slide);
                binding.childSync.setText("Sync Slide");

                binding.LinearLayout1.setVisibility(View.GONE);
                binding.LinearLayout2.setVisibility(View.GONE);
                binding.LinearLayout3.setVisibility(View.GONE);
                binding.productLayout.setVisibility(View.GONE);
                binding.subordinateLayout.setVisibility(View.GONE);

            }
        });

        binding.subordinate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                listItemClicked(binding.subordinate);
                binding.subordinateLayout.setVisibility(View.VISIBLE);
                binding.childSync.setText("Sync Subordinate");

                binding.LinearLayout1.setVisibility(View.GONE);
                binding.LinearLayout2.setVisibility(View.GONE);
                binding.LinearLayout3.setVisibility(View.GONE);
                binding.productLayout.setVisibility(View.GONE);

            }
        });

        binding.masterCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                Log.e("test","master card clicked");

                if (binding.listedDoctor.isSelected()){
                    sync("Doctor","getdoctors",binding.masterProgressBar,binding.masterCount,Constants.DOCTOR);
                } else if (binding.unlistedDoctor.isSelected()) {
                    sync("Doctor","getunlisteddr",binding.masterProgressBar,binding.masterCount,Constants.UNLISTED_DOCTOR);
                } else if (binding.chemist.isSelected()) {
                    sync("Doctor","getchemist",binding.masterProgressBar,binding.masterCount,Constants.CHEMIST);
                } else if (binding.stockiest.isSelected()) {
                    sync("Doctor","getstockist",binding.masterProgressBar,binding.masterCount,Constants.STOCKIEST);
                }else if (binding.hospital.isSelected()) {
                    sync("Doctor","gethospital",binding.masterProgressBar,binding.masterCount,Constants.HOSPITAL);
                }else if (binding.cip.isSelected()) {
                    sync("Doctor","getcip",binding.masterProgressBar,binding.masterCount,Constants.CIP);
                }
            }
        });

        binding.splCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                sync("Doctor","getspeciality",binding.splProgressBar,binding.splCount,Constants.SPECIALITY);
            }
        });

        binding.qlCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                sync("Doctor","getquali",binding.qlProgressBar,binding.qlCount,Constants.QUALIFICATION);
            }
        });

        binding.catCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                sync("Doctor","getcategorys",binding.catProgressBar,binding.catCount,Constants.CATEGORY);
            }
        });

        binding.departmentCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                sync("Doctor","getdeparts",binding.depProgressBar,binding.depCount,Constants.DEPARTMENT);
            }
        });

        binding.classCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                sync("Doctor","getclass",binding.classProgressBar,binding.classCount,Constants.CLASS);
            }
        });

        binding.feedbackCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                sync("Doctor","getdrfeedback",binding.feedbackProgressBar,binding.feedbackCount,Constants.FEEDBACK);
            }
        });

        //Subordinate --------------
        binding.subordinateCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                sync("Subordinate","getsubordinate",binding.subordinateProgressBar,binding.subordinateMasterCount,Constants.SUBORDINATE);
            }
        });

        binding.subMgrCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                sync("Subordinate","getsubordinatemgr",binding.subMgrProgressBar,binding.subMgrCount,Constants.SUBORDINATE_MGR);
            }
        });

        binding.jWorkCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                sync("Subordinate","getjointwork",binding.jWorkProgressBar,binding.jWorkCount,Constants.JOINT_WORK);
            }
        });

        //product -----------------
        binding.productCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {

            }
        });

        binding.proCategoryCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                
            }
        });

        binding.brandCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {

            }
        });

        binding.compProductCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {

            }
        });

        binding.childSync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
//                binding.masterCard.performClick();

                if (binding.listedDoctor.isSelected()){
                    Log.e("test","listed doc selected");
                    sync("Doctor","getdoctors",binding.masterProgressBar,binding.masterCount,Constants.DOCTOR);
                    sync("Doctor","getspeciality",binding.splProgressBar,binding.splCount,Constants.SPECIALITY);
                    sync("Doctor","getquali",binding.qlProgressBar,binding.qlCount,Constants.QUALIFICATION);
                    sync("Doctor","getcategorys",binding.catProgressBar,binding.catCount,Constants.CATEGORY);
                    sync("Doctor","getdeparts",binding.depProgressBar,binding.depCount,Constants.DEPARTMENT);
                    sync("Doctor","getclass",binding.classProgressBar,binding.classCount,Constants.CLASS);
                    sync("Doctor","getdrfeedback",binding.feedbackProgressBar,binding.feedbackCount,Constants.FEEDBACK);
                } else if (binding.unlistedDoctor.isSelected()) {
                    sync("Doctor","getunlisteddr",binding.masterProgressBar,binding.masterCount,Constants.UNLISTED_DOCTOR);
                } else if (binding.chemist.isSelected()) {
                    sync("Doctor","getchemist",binding.masterProgressBar,binding.masterCount,Constants.CHEMIST);
                } else if (binding.stockiest.isSelected()) {
                    sync("Doctor","getstockist",binding.masterProgressBar,binding.masterCount,Constants.STOCKIEST);
                }else if (binding.hospital.isSelected()) {
                    sync("Doctor","gethospital",binding.masterProgressBar,binding.masterCount,Constants.HOSPITAL);
                }else if (binding.cip.isSelected()) {
                    sync("Doctor","getcip",binding.masterProgressBar,binding.masterCount,Constants.CIP);
                }else if (binding.subordinate.isSelected()) {
                    sync("Subordinate","getsubordinate",binding.subordinateProgressBar,binding.subordinateMasterCount,Constants.SUBORDINATE);
                    sync("Subordinate","getsubordinatemgr",binding.subMgrProgressBar,binding.subMgrCount,Constants.SUBORDINATE_MGR);
                    sync("Subordinate","getjointwork",binding.jWorkProgressBar,binding.jWorkCount,Constants.JOINT_WORK);
                }else if (binding.product.isSelected()){
                    sync("Product","getproducts",binding.productProgressBar,binding.productCount,Constants.PRODUCT);
                    sync("Product","getbrands",binding.brandProgressBar,binding.brandCount,Constants.BRAND);
                    sync("Product","getcompdet",binding.compProProgressBar,binding.compProCount,Constants.COMPETITOR_PROD);
                }
            }
        });

        binding.syncAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                syncAll();
            }
        });

    }

    public void uiInitialization() throws JSONException {
        binding.listedDoctor.setText(loginResponse.getDrCap());
        binding.chemist.setText(loginResponse.getChmCap());
        binding.stockiest.setText(loginResponse.getStkCap());
        binding.unlistedDoctor.setText(loginResponse.getNLCap());
        binding.masterName.setText(loginResponse.getDrCap());

        binding.listedDoctor.setSelected(true);

        doctorCount =  sqLite.getMasterSyncDataByKey(Constants.DOCTOR).length();
        specialityCount =  sqLite.getMasterSyncDataByKey(Constants.SPECIALITY).length();
        qualificationCount =  sqLite.getMasterSyncDataByKey(Constants.QUALIFICATION).length();
        categoryCount =  sqLite.getMasterSyncDataByKey(Constants.CATEGORY).length();
        departmentCount =  sqLite.getMasterSyncDataByKey(Constants.DEPARTMENT).length();
        classCount =  sqLite.getMasterSyncDataByKey(Constants.CLASS).length();
        feedbackCount =  sqLite.getMasterSyncDataByKey(Constants.FEEDBACK).length();
        competitorCount =  sqLite.getMasterSyncDataByKey(Constants.COMPETITOR_PROD).length();

        binding.masterCount.setText(String.valueOf(doctorCount));
        binding.splCount.setText(String.valueOf(specialityCount));
        binding.qlCount.setText(String.valueOf(qualificationCount));
        binding.catCount.setText(String.valueOf(categoryCount));
        binding.depCount.setText(String.valueOf(departmentCount));
        binding.classCount.setText(String.valueOf(classCount));
        binding.feedbackCount.setText(String.valueOf(feedbackCount));

        unlistedDrCount = sqLite.getMasterSyncDataByKey(Constants.UNLISTED_DOCTOR).length();
        chemistCount =  sqLite.getMasterSyncDataByKey(Constants.CHEMIST).length();
        stockiestCount =  sqLite.getMasterSyncDataByKey(Constants.STOCKIEST).length();
        hospitalCount =  sqLite.getMasterSyncDataByKey(Constants.HOSPITAL).length();
        cipCount =  sqLite.getMasterSyncDataByKey(Constants.CIP).length();

    }

    public void listItemClicked(TextView view){
        binding.listedDoctor.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.down_arrow), null);
        binding.unlistedDoctor.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.down_arrow), null);
        binding.chemist.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.down_arrow), null);
        binding.stockiest.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.down_arrow), null);
        binding.hospital.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.down_arrow), null);
        binding.cip.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.down_arrow), null);
        binding.subordinate.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.down_arrow), null);
        binding.product.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.down_arrow), null);
        binding.input.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.down_arrow), null);
        binding.leave.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.down_arrow), null);
        binding.tourPlan.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.down_arrow), null);
        binding.slide.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.down_arrow), null);

        binding.listedDoctor.setSelected(false);
        binding.unlistedDoctor.setSelected(false);
        binding.chemist.setSelected(false);
        binding.stockiest.setSelected(false);
        binding.hospital.setSelected(false);
        binding.cip.setSelected(false);
        binding.subordinate.setSelected(false);
        binding.product.setSelected(false);
        binding.input.setSelected(false);
        binding.leave.setSelected(false);
        binding.tourPlan.setSelected(false);
        binding.slide.setSelected(false);

        view.setCompoundDrawablesWithIntrinsicBounds(null, null, getResources().getDrawable(R.drawable.right_arrow), null);
        view.setSelected(true);
    }

    public void  sync(String masterFor,String remoteTableName,ProgressBar progressBar,TextView count,String localTableKey)  {

        try {
            progressBar.setVisibility(View.VISIBLE);
            String baseUrl = SharedPref.getBaseWebUrl(getApplicationContext());
            String pathUrl = SharedPref.getPhpPathUrl(getApplicationContext());
            String replacedUrl = pathUrl.replaceAll("\\?.*","/");
            Log.e("test","master url : "  + baseUrl + replacedUrl);
            apiInterface = RetrofitClient.getRetrofit(getApplicationContext(), baseUrl+replacedUrl);

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("tableName", remoteTableName);
            jsonObject.put("sfcode", sfCode);
            jsonObject.put("division_code", division_code);
            jsonObject.put("Rsf", rsf);
            jsonObject.put("sf_type", sf_type);
            jsonObject.put("Designation", designation);
            jsonObject.put("state_code", state_code);
            jsonObject.put("subdivision_code", subdivision_code);

            Log.e("test","master sync obj : " + jsonObject);
            Call<JsonArray> call = null;
            if (masterFor.equalsIgnoreCase("Doctor")){
                call = apiInterface.getDrMaster(jsonObject.toString());
            } else if (masterFor.equalsIgnoreCase("Subordinate")) {
                call = apiInterface.getSubordinateMaster(jsonObject.toString());
            } else if (masterFor.equalsIgnoreCase("Product")) {
                call = apiInterface.getProductMaster(jsonObject.toString());
            }

            call.enqueue(new Callback<JsonArray>() {
                @Override
                public void onResponse (@NonNull Call<JsonArray> call, @NonNull Response<JsonArray> response) {
                    progressBar.setVisibility(View.GONE);

                    if (response.isSuccessful()) {
                        Log.e("test","response body : " + response.body().toString());
                        try {
                            JSONArray jsonArray = new JSONArray(response.body().toString());
                            count.setText(String.valueOf(jsonArray.length()));

                            switch (remoteTableName){
                                case "getdoctors" :{
                                    doctorCount = jsonArray.length();
                                    break;
                                }
                                case "getunlisteddr" :{
                                    unlistedDrCount = jsonArray.length();
                                    break;
                                }case "getchemist" :{
                                    chemistCount = jsonArray.length();
                                    break;
                                }case "getstockist" :{
                                    stockiestCount = jsonArray.length();
                                    break;
                                }case "gethospital" :{
                                    hospitalCount = jsonArray.length();
                                    break;
                                }
                                case "getcip" :{
                                    cipCount = jsonArray.length();
                                    break;
                                }
                            }
                            saveIntoMasterTable(localTableKey,jsonArray.toString());
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                }

                @Override
                public void onFailure (@NonNull Call<JsonArray> call, @NonNull Throwable t) {
                    Log.e("test","failed : " + t);
                    progressBar.setVisibility(View.GONE);
                }
            });


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void saveIntoMasterTable(String key,String values){
        sqLite.saveMasterSyncData(key,values);

    }

    public void syncAll(){
        sync("Doctor","getdoctors",binding.masterProgressBar,binding.masterCount,Constants.DOCTOR);
        sync("Doctor","getspeciality",binding.splProgressBar,binding.splCount,Constants.SPECIALITY);
        sync("Doctor","getquali",binding.qlProgressBar,binding.qlCount,Constants.QUALIFICATION);
        sync("Doctor","getcategorys",binding.catProgressBar,binding.catCount,Constants.CATEGORY);
        sync("Doctor","getdeparts",binding.depProgressBar,binding.depCount,Constants.DEPARTMENT);
        sync("Doctor","getclass",binding.classProgressBar,binding.classCount,Constants.CLASS);
        sync("Doctor","getdrfeedback",binding.feedbackProgressBar,binding.feedbackCount,Constants.FEEDBACK);
        sync("Doctor","getunlisteddr",binding.masterProgressBar,binding.masterCount,Constants.UNLISTED_DOCTOR);
        sync("Doctor","getchemist",binding.masterProgressBar,binding.masterCount,Constants.CHEMIST);
        sync("Doctor","getstockist",binding.masterProgressBar,binding.masterCount,Constants.STOCKIEST);
        sync("Doctor","gethospital",binding.masterProgressBar,binding.masterCount,Constants.HOSPITAL);
        sync("Doctor","getcip",binding.masterProgressBar,binding.masterCount,Constants.CIP);

        //Subordinate
        sync("Subordinate","getsubordinate",binding.subordinateProgressBar,binding.subordinateMasterCount,Constants.SUBORDINATE);
        sync("Subordinate","getsubordinatemgr",binding.subMgrProgressBar,binding.subMgrCount,Constants.SUBORDINATE_MGR);
        sync("Subordinate","getjointwork",binding.jWorkProgressBar,binding.jWorkCount,Constants.JOINT_WORK);

        //Product
        sync("Product","getproducts",binding.productProgressBar,binding.productCount,Constants.PRODUCT);
        sync("Product","getbrands",binding.brandProgressBar,binding.brandCount,Constants.BRAND);
        sync("Product","getcompdet",binding.compProProgressBar,binding.compProCount,Constants.COMPETITOR_PROD);

        //Slide


    }
}