package saneforce.sanclm.activity.reports;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanclm.R;
import saneforce.sanclm.activity.reports.dayReport.DataViewModel;
import saneforce.sanclm.commonClasses.UtilityClass;
import saneforce.sanclm.databinding.ActivityReportsBinding;
import saneforce.sanclm.network.ApiInterface;
import saneforce.sanclm.network.RetrofitClient;
import saneforce.sanclm.response.LoginResponse;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.utility.TimeUtils;

public class ReportsActivity extends AppCompatActivity {

    ActivityReportsBinding binding;
    ReportsAdapter reportsAdapter;
    ApiInterface apiInterface;
    SQLite sqLite;
    LoginResponse loginResponse;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        sqLite = new SQLite(ReportsActivity.this);
        loginResponse = sqLite.getLoginData();

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
        if(UtilityClass.isNetworkAvailable(getApplicationContext())){

//                apiInterface = RetrofitClient.getRetrofit(ReportsActivity.this, SharedPref.getCallApiUrl(ReportsActivity.this));
            apiInterface = RetrofitClient.getRetrofit(ReportsActivity.this, "http://sanffa.info/server/db_native_app.php/?");

            Map<String,String> map = new HashMap<>();
//                map.put("divisionCode", loginResponse.getDivision_Code());
//                map.put("rptSF", loginResponse.getSF_Code());
//                map.put("rSF", loginResponse.getSF_Code());
//                map.put("axn", "get/DayReports");
//                map.put("sfCode", loginResponse.getSF_Code());
            map.put("divisionCode", "25");
            map.put("rptSF", "MGR2240");
            map.put("rSF", "MGR2240");
            map.put("sfCode", "MGR2240");
            map.put("rptDt", date);

            switch (report.toUpperCase()){
                case "DAY REPORT" : {
                    map.put("axn", "get/DayReports");
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

            Call<JsonElement> call = apiInterface.getDayReport("",map);
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
                            }
                            navigate(jsonArray,report,date);
                        }
                    }catch (JSONException e){
                        throw new RuntimeException(e);
                    }

                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    binding.progressBar.setVisibility(View.GONE);

                }
            });
        }else{
            binding.progressBar.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), "Internet is not available", Toast.LENGTH_SHORT).show();
        }

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