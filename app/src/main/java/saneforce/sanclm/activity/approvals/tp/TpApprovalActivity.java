package saneforce.sanclm.activity.approvals.tp;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanclm.activity.approvals.OnItemClickListenerApproval;
import saneforce.sanclm.activity.approvals.dcr.pojo.DCRApprovalList;
import saneforce.sanclm.activity.approvals.dcr.pojo.DcrDetailModelList;
import saneforce.sanclm.databinding.ActivityTpApprovalBinding;
import saneforce.sanclm.network.ApiInterface;
import saneforce.sanclm.network.RetrofitClient;
import saneforce.sanclm.response.LoginResponse;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SharedPref;

public class TpApprovalActivity extends AppCompatActivity implements OnItemClickListenerApproval {
    public static String SfName, SfType, SfCode, DivCode, Designation, StateCode, SubDivisionCode, TodayPlanSfCode, SelectedSfCode, SelectedMonthYear;
    @SuppressLint("StaticFieldLeak")
    public static ActivityTpApprovalBinding tpApprovalBinding;
    LoginResponse loginResponse;
    SQLite sqLite;
    ApiInterface api_interface;
    ArrayList<TpModelList> tpModelLists = new ArrayList<>();
    TpApprovalAdapter tpApprovalAdapter;
    JSONObject jsonTp = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tpApprovalBinding = ActivityTpApprovalBinding.inflate(getLayoutInflater());
        setContentView(tpApprovalBinding.getRoot());
        sqLite = new SQLite(this);
        api_interface = RetrofitClient.getRetrofit(getApplicationContext(), SharedPref.getCallApiUrl(getApplicationContext()));
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        getRequiredData();
        CallTpListApi();
        tpApprovalBinding.ivBack.setOnClickListener(view -> {
            SelectedSfCode = "";
            SelectedMonthYear = "";
             getOnBackPressedDispatcher().onBackPressed();
         //   startActivity(new Intent(TpApprovalActivity.this, ApprovalsActivity.class));
        });
        tpApprovalBinding.searchTp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });
    }

    private void CallTpListApi() {
        try {
            jsonTp.put("tableName", "gettpapproval");
            jsonTp.put("sfcode", SfCode);
            jsonTp.put("division_code", DivCode);
            jsonTp.put("Rsf", SfCode);
            jsonTp.put("sf_type", SfType);
            jsonTp.put("Designation", Designation);
            jsonTp.put("state_code", SubDivisionCode);
            Log.v("json_getTpList", jsonTp.toString());
        } catch (Exception ignored) {

        }
        Call<JsonArray> callGetTPApproval;
        callGetTPApproval = api_interface.getTpApprovalList(jsonTp.toString());
        callGetTPApproval.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(@NonNull Call<JsonArray> call, @NonNull Response<JsonArray> response) {
                assert response.body() != null;
                Log.v("jjj", response.body() + "--" + response.isSuccessful());
                if (response.isSuccessful()) {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().toString());
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject json = jsonArray.getJSONObject(i);
                                tpModelLists.add(new TpModelList(json.getString("Sf_Code"), json.getString("SFName"), json.getString("Mnth"), json.getString("Yr"), json.getString("Mn")));
                            }
                            tpApprovalAdapter = new TpApprovalAdapter(TpApprovalActivity.this, tpModelLists, TpApprovalActivity.this);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
                            tpApprovalBinding.rvTpList.setLayoutManager(mLayoutManager);
                            tpApprovalBinding.rvTpList.setAdapter(tpApprovalAdapter);
                        } else {
                            Toast.makeText(TpApprovalActivity.this, "No Data Available", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception ignored) {

                    }
                } else {
                    Toast.makeText(TpApprovalActivity.this, "Response Failed! Please Try Again", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonArray> call, @NonNull Throwable t) {
                Toast.makeText(TpApprovalActivity.this, "Response Failed! Please Try Again", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filter(String text) {
        ArrayList<TpModelList> filteredNames = new ArrayList<>();
        for (TpModelList s : tpModelLists) {
            if (s.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredNames.add(s);
            }
        }
        tpApprovalAdapter.filterList(filteredNames);
    }

    private void getRequiredData() {
        loginResponse = new LoginResponse();
        loginResponse = sqLite.getLoginData();

        SfType = loginResponse.getSf_type();
        SfCode = loginResponse.getSF_Code();
        SfName = loginResponse.getSF_Name();
        DivCode = loginResponse.getDivision_Code();
        SubDivisionCode = loginResponse.getSubdivision_code();
        Designation = loginResponse.getDesig();
        StateCode = loginResponse.getState_Code();
        TodayPlanSfCode = SharedPref.getTodayDayPlanSfCode(TpApprovalActivity.this);
    }

    @Override
    public void onClick(DCRApprovalList dcrApprovalList, int pos) {
    }

    @Override
    public void onClickDcrDetail(DcrDetailModelList dcrDetailModelList) {

    }

    @Override
    public void onItemClick(TpModelList tpModelLists) {
        tpApprovalBinding.tvName.setText(tpModelLists.getName());
        tpApprovalBinding.tvTpPlannedFor.setText(String.format("%s %s", tpModelLists.getMonth(), tpModelLists.getYear()));
        tpApprovalBinding.constraintTpListContent.setVisibility(View.VISIBLE);
        SelectedSfCode = tpModelLists.getCode();
        SelectedMonthYear = String.format("%s %s", tpModelLists.getMonth(), tpModelLists.getYear());
    }
}