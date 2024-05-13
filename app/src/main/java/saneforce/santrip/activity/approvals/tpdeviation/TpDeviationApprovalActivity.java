package saneforce.santrip.activity.approvals.tpdeviation;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.santrip.R;
import saneforce.santrip.activity.approvals.ApprovalsActivity;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.databinding.ActivityTpDeviationApprovalBinding;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;

import saneforce.santrip.storage.SharedPref;

public class TpDeviationApprovalActivity extends AppCompatActivity {
    ActivityTpDeviationApprovalBinding tpDeviationApprovalBinding;
    ArrayList<TpDeviationModelList> tpDeviationModelLists = new ArrayList<>();
    TpDeviationAdapter tpDeviationAdapter;
    JSONObject jsonTpDeviation = new JSONObject();
    ApiInterface api_interface;

//    SQLite sqLite;
    ProgressDialog progressDialog = null;
    CommonUtilsMethods commonUtilsMethods;

    //To Hide the bottomNavigation When popup
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            tpDeviationApprovalBinding.getRoot().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tpDeviationApprovalBinding = ActivityTpDeviationApprovalBinding.inflate(getLayoutInflater());
        setContentView(tpDeviationApprovalBinding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        api_interface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));
//        sqLite = new SQLite(getApplicationContext());
       commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());

        CallTpDeviationAPI();

        tpDeviationApprovalBinding.ivBack.setOnClickListener(v -> {
            Intent intent = new Intent(TpDeviationApprovalActivity.this, ApprovalsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        });

        tpDeviationApprovalBinding.searchTpDeviation.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filter(s.toString());
            }
        });

    }


    private void filter(String text) {
        ArrayList<TpDeviationModelList> filteredNames = new ArrayList<>();
        for (TpDeviationModelList s : tpDeviationModelLists) {
            if (s.getSfName().toLowerCase().contains(text.toLowerCase()) || s.getDate().contains(text.toLowerCase())) {
                filteredNames.add(s);
            }
        }
        tpDeviationAdapter.filterList(filteredNames);
    }

    private void CallTpDeviationAPI() {
        progressDialog = CommonUtilsMethods.createProgressDialog(TpDeviationApprovalActivity.this);
        try {
            jsonTpDeviation.put("tableName", "getdevappr");
            jsonTpDeviation.put("sfcode", SharedPref.getSfType(this));
            jsonTpDeviation.put("division_code", SharedPref.getDivisionCode(this));
            jsonTpDeviation.put("Rsf", SharedPref.getHqCode(this));
            jsonTpDeviation.put("sf_type", SharedPref.getSfType(this));
            jsonTpDeviation.put("Designation", SharedPref.getDesig(this));
            jsonTpDeviation.put("state_code", SharedPref.getStateCode(this));
            jsonTpDeviation.put("subdivision_code", SharedPref.getSubdivisionCode(this));
            Log.v("json_get_tpDev_list", jsonTpDeviation.toString());
        } catch (Exception ignored) {

        }
        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "get/approvals");
        Call<JsonElement> callGetTpDevList = api_interface.getJSONElement(SharedPref.getCallApiUrl(context), mapString,jsonTpDeviation.toString());
        callGetTpDevList.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                assert response.body() != null;
                if (response.isSuccessful()) {
                    try {
                        progressDialog.dismiss();
                        JSONArray jsonArray = new JSONArray(response.body().toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject json = jsonArray.getJSONObject(i);
                            tpDeviationModelLists.add(new TpDeviationModelList(json.getString("sf_name"), json.getString("sf_code"), json.getString("sl_no"), json.getString("missed_date"), json.getString("Deviation_Reason")));
                        }
                        tpDeviationAdapter = new TpDeviationAdapter(TpDeviationApprovalActivity.this, tpDeviationModelLists);
                        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                        tpDeviationApprovalBinding.rvTpDeviation.setLayoutManager(mLayoutManager);
                        tpDeviationApprovalBinding.rvTpDeviation.setAdapter(tpDeviationAdapter);
                    } catch (Exception ignored) {

                    }
                } else {
                    progressDialog.dismiss();
                    commonUtilsMethods.showToastMessage(TpDeviationApprovalActivity.this,getString(R.string.toast_response_failed));
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                progressDialog.dismiss();
                commonUtilsMethods.showToastMessage(TpDeviationApprovalActivity.this,getString(R.string.toast_response_failed));
            }
        });


    }

}