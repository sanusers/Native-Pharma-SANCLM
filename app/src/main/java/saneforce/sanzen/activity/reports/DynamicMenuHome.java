package saneforce.sanzen.activity.reports;

import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.reports.dayReport.model.MenuModel;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.ActivityDynamicMenuBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.NetworkStatusTask;

public class DynamicMenuHome extends AppCompatActivity {
    ActivityDynamicMenuBinding binding;
    TextView title;
    ArrayList<MenuModel> menuList = new ArrayList<>();
    DynamicAdapter dynamicAdapter;
    GridView gridView;
    LinearLayout backArrow;
    ApiInterface apiInterface;
    CommonUtilsMethods commonUtilsMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDynamicMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        title = binding.title;
        if(!SharedPref.getDynamicOptionCaps(DynamicMenuHome.this).equalsIgnoreCase("")) {
            title.setText(SharedPref.getDynamicOptionCaps(DynamicMenuHome.this));
        }else{
            title.setText(R.string.option);
        }

        gridView = findViewById(R.id.gridView);
        backArrow = findViewById(R.id.backArrow);
        binding.backArrow.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        dynamicAdapter = new DynamicAdapter( menuList, this);
        gridView.setAdapter(dynamicAdapter);
        binding.menuRecycler.setLayoutManager(new LinearLayoutManager(this));
        commonUtilsMethods = new CommonUtilsMethods(this);

        if (isNetworkConnected()) {
            loadMenuFromApi();
        } else {
            commonUtilsMethods.showToastMessage(this, getString(R.string.no_network), true);

        }
        if(SharedPref.getDynamicOptionNeed(DynamicMenuHome.this).equalsIgnoreCase("0")) {
            binding.reportSync.setVisibility(View.VISIBLE);
            binding.reportSync.setOnClickListener(view -> {
                loadMenuFromApi();
            });
        }
    }
    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    private void loadMenuFromApi() {
        if (UtilityClass.isNetworkAvailable(this)) {
            binding.dynamicProg.setVisibility(View.VISIBLE);
            menuList.clear();
            dynamicAdapter.notifyDataSetChanged();
            NetworkStatusTask networkStatusTask = new NetworkStatusTask(this, status -> {
                if (status) {
                    try {
                        apiInterface = RetrofitClient.getRetrofit(DynamicMenuHome.this, SharedPref.getCallApiUrl(DynamicMenuHome.this));

                        JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(this);
                        jsonObject.put("sfcode", SharedPref.getSfCode(this));
                        jsonObject.put("division_code", SharedPref.getDivisionCode(this));
                        jsonObject.put("Rsf", SharedPref.getHqCode(this));
                        jsonObject.put("tableName", "getdynamicmenu");


                        Log.d("Report", "getData: " + jsonObject);

                        Map<String, String> mapString = new HashMap<>();
                        mapString.put("axn", "get/reports");
                        Call<JsonElement> call = apiInterface.getJSONElement(SharedPref.getCallApiUrl(this), mapString, jsonObject.toString());
                        call.enqueue(new Callback<JsonElement>() {
                            @Override
                            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                                binding.dynamicProg.setVisibility(View.GONE);
                                try {
                                    if (response.isSuccessful() && response.body() != null) {
                                        JsonArray jsonArray = response.body().getAsJsonArray();
                                        if (jsonArray.size() > 0) {
                                            for (int i = 0; i < jsonArray.size(); i++) {
                                                JsonObject menuObject = jsonArray.get(i).getAsJsonObject();
                                                String menu_name = menuObject.get("Menu_Name").getAsString();
                                                String menu_icon = menuObject.get("Menu_Icon").getAsString();
                                                JsonArray menu_sub_details = menuObject.get("Menu_Options").getAsJsonArray();

                                                menu_icon = SharedPref.getTagImageUrl(DynamicMenuHome.this) + "/" + menu_icon;

                                                MenuModel menuModel = new MenuModel(menu_name, menu_icon, menu_sub_details);
                                                menuList.add(menuModel);
                                            }
                                            dynamicAdapter.notifyDataSetChanged();
                                        }else{
                                            binding.noReportFoundTxt.setVisibility(View.VISIBLE);
                                            //commonUtilsMethods.showToastMessage(DynamicMenuHome.this,"No Record Found");
                                            commonUtilsMethods.showToastMessage(DynamicMenuHome.this,getString(R.string.no_record_found), true);
                                        }


                                       /* JsonElement jsonElement = response.body();
                                        JSONArray jsonArray1 = new JSONArray();
                                        if (jsonElement.isJsonArray()) {
                                            jsonArray1 = new JSONArray(jsonElement.getAsJsonArray().toString());

                                        }*/
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }


                            @Override
                            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                                binding.dynamicProg.setVisibility(View.GONE);
                                binding.noReportFoundTxt.setVisibility(View.VISIBLE);
                                commonUtilsMethods.showToastMessage(DynamicMenuHome.this, getString(R.string.poor_connection)+" "+getString(R.string.please_try_again), true);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    binding.dynamicProg.setVisibility(View.GONE);
                    commonUtilsMethods.showToastMessage(DynamicMenuHome.this, getString(R.string.poor_connection), true);
                }

            });
            networkStatusTask.execute();
        } else {
            binding.dynamicProg.setVisibility(View.GONE);
            commonUtilsMethods.showToastMessage(DynamicMenuHome.this, getString(R.string.no_network), true);
        }

    }
}
