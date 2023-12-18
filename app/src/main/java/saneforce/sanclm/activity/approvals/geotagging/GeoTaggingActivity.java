package saneforce.sanclm.activity.approvals.geotagging;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.JsonArray;

import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.databinding.ActivityGeoTaggingBinding;
import saneforce.sanclm.network.ApiInterface;
import saneforce.sanclm.network.RetrofitClient;
import saneforce.sanclm.response.LoginResponse;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SharedPref;


public class GeoTaggingActivity extends AppCompatActivity {
    ActivityGeoTaggingBinding geoTaggingBinding;
    String SfName, SfType, SfCode, DivCode, Designation, StateCode, SubDivisionCode;
    ArrayList<GeoTaggingModelList> geoTaggingModelLists = new ArrayList<>();
    GeoTaggingAdapter geoTaggingAdapter;
    ApiInterface api_interface;
    LoginResponse loginResponse;
    JSONObject jsonGeoTagList = new JSONObject();
    ProgressDialog progressDialog = null;
    SQLite sqLite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        geoTaggingBinding = ActivityGeoTaggingBinding.inflate(getLayoutInflater());
        setContentView(geoTaggingBinding.getRoot());
        setUpAdapter();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        api_interface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));
        sqLite = new SQLite(getApplicationContext());
        getRequiredData();
        CallgeoTagApi();
        geoTaggingBinding.ivBack.setOnClickListener(view -> finish());
    }

    private void getRequiredData() {
        try {
            loginResponse = new LoginResponse();
            loginResponse = sqLite.getLoginData();

            SfType = loginResponse.getSf_type();
            SfCode = loginResponse.getSF_Code();
            SfName = loginResponse.getSF_Name();
            DivCode = loginResponse.getDivision_Code();
            SubDivisionCode = loginResponse.getSubdivision_code();
            Designation = loginResponse.getDesig();
            StateCode = loginResponse.getState_Code();
        } catch (Exception ignored) {

        }
    }

    private void CallgeoTagApi() {
        progressDialog = CommonUtilsMethods.createProgressDialog(GeoTaggingActivity.this);
        try {
            jsonGeoTagList.put("tableName", "getvwdcr");
            jsonGeoTagList.put("sfcode", SfCode);
            jsonGeoTagList.put("division_code", DivCode);
            //   jsonGeoTagList.put("Rsf", TodayPlanSfCode);
            jsonGeoTagList.put("sf_type", SfType);
            jsonGeoTagList.put("Designation", Designation);
            jsonGeoTagList.put("state_code", StateCode);
            jsonGeoTagList.put("subdivision_code", SubDivisionCode);
            Log.v("json_getGeoTag_list", jsonGeoTagList.toString());
        } catch (Exception ignored) {

        }

        Call<JsonArray> callGetGeoTagList;
        callGetGeoTagList = api_interface.getDcrApprovalList(jsonGeoTagList.toString());
    }

    private void setUpAdapter() {
        geoTaggingModelLists.add(new GeoTaggingModelList("Aravindh", "Chennai", "Tambaram,Chengalpattu,Kk Nagar", "No 31,Sri Venkatachala Street,3rd Street,Nesappakkam,Chennai - 620009"));
        geoTaggingModelLists.add(new GeoTaggingModelList("Surya", "Trichy", "Palakarai,Thennur,Thillai Nagar,Alwar Thoppu", "No 23,Quide Millath Street,Kaja Malai Nagar,Thennur,Trichy - 620017"));

        geoTaggingAdapter = new GeoTaggingAdapter(getApplicationContext(), geoTaggingModelLists);
        geoTaggingBinding.rvGeoTagging.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        geoTaggingBinding.rvGeoTagging.setAdapter(geoTaggingAdapter);
    }
}