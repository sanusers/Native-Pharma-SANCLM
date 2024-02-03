package saneforce.santrip.activity.reports;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.santrip.R;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.databinding.ActivityReportsBinding;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;
import saneforce.santrip.response.LoginResponse;
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.storage.SharedPref;
import saneforce.santrip.utility.NetworkStatusTask;

public class ReportsActivity extends AppCompatActivity {

    ActivityReportsBinding binding;
    ReportsAdapter reportsAdapter;
    ApiInterface apiInterface;
    SQLite sqLite;
    LoginResponse loginResponse;
    CommonUtilsMethods commonUtilsMethods;


    //To Hide the bottomNavigation When popup
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            binding.getRoot().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sqLite = new SQLite(ReportsActivity.this);
        loginResponse = sqLite.getLoginData();
        commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());
        commonUtilsMethods.setUpLanguage(getApplicationContext());

        populateAdapter();
        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    public void populateAdapter(){
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Day Report");
        arrayList.add("Monthly Report");
        arrayList.add("Day Check In Report");
        arrayList.add("Customer Check In Report");
        arrayList.add("Visit Monitor");

        reportsAdapter = new ReportsAdapter(arrayList,ReportsActivity.this);
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(ReportsActivity.this,4);
        binding.recView.setLayoutManager(layoutManager);
        binding.recView.setAdapter(reportsAdapter);
    }

    public void getData(String report,String date){

        binding.progressBar.setVisibility(View.VISIBLE);
        NetworkStatusTask networkStatusTask = new NetworkStatusTask(this, new NetworkStatusTask.NetworkStatusInterface() {
            @Override
            public void isNetworkAvailable(Boolean status) {
                if(status){

                    try {
//                    apiInterface = RetrofitClient.getRetrofit(ReportsActivity.this, SharedPref.getCallApiUrl(ReportsActivity.this));
                        apiInterface = RetrofitClient.getRetrofit(ReportsActivity.this, SharedPref.getCallApiUrl(ReportsActivity.this));

                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("sfcode", loginResponse.getSF_Code());
                        jsonObject.put("sf_type", loginResponse.getSf_type());
                        jsonObject.put("divisionCode", loginResponse.getDivision_Code());
                        jsonObject.put("Rsf", loginResponse.getSF_Code());
                        jsonObject.put("Designation", loginResponse.getDesig());
                        jsonObject.put("state_code", loginResponse.getState_Code());
                        jsonObject.put("subdivision_code", loginResponse.getSubdivision_code());
                        jsonObject.put("rptDt", date);

//                        jsonObject.put("divisionCode", "25");
//                        jsonObject.put("rptSF", "MGR2240");
//                        jsonObject.put("rSF", "MGR2240");
//                        jsonObject.put("sfCode", "MGR2240");

                        switch (report.toUpperCase()){
                            case "DAY REPORT" : {
                                jsonObject.put("tableName", "getdayrpt");
                                break;
                            }
//                case "MONTHLY REPORT" : {
//                    break;
//                }
//                case "DAY CHECK IN REPORT" : {
//                    break;
//                }
//                case "CUSTOMER CHECK IN REPORT" : {
//                    break;
//                }
//                case "VISIT MONITOR" : {
//                    break;
//                }
                        }

                        Call<JsonElement> call = apiInterface.getReports(jsonObject.toString());
                        call.enqueue(new Callback<JsonElement>() {
                            @Override
                            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                                binding.progressBar.setVisibility(View.GONE);
                                try {
                                    if(response.isSuccessful() && response.body() != null){
                                        JsonElement jsonElement = response.body();
                                        JSONArray jsonArray = new JSONArray();
                                        if(jsonElement.isJsonArray()){
                                            jsonArray = new JSONArray(jsonElement.getAsJsonArray().toString());
                                            navigate(jsonArray,report,date);
                                        }
                                    }
                                }catch (JSONException e){
                                    e.printStackTrace();
                                }

                            }

                            @Override
                            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                                binding.progressBar.setVisibility(View.GONE);

                            }
                        });
                    }catch (JSONException e){
                        e.printStackTrace();
                    }

                }else{
                    binding.progressBar.setVisibility(View.GONE);
                    commonUtilsMethods.ShowToast(getApplicationContext(),getString(R.string.no_network),100);
                }
            }
        });
        networkStatusTask.execute();
    }

    public void navigate(JSONArray jsonArray,String report,String date){
        Intent intent = new Intent(ReportsActivity.this, ReportFragContainerActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("data",jsonArray.toString());
        bundle.putString("date",date);
        bundle.putString("fragment",report);
        intent.putExtra("reportBundle",bundle);
        startActivity(intent);

    }


}