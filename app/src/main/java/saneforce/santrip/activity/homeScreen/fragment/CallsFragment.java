package saneforce.santrip.activity.homeScreen.fragment;

import static androidx.core.content.ContextCompat.registerReceiver;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.santrip.activity.homeScreen.adapters.Call_adapter;
import saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.santrip.activity.homeScreen.modelClass.CallsModalClass;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.commonClasses.UtilityClass;
import saneforce.santrip.databinding.CallsFragmentBinding;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;
import saneforce.santrip.response.LoginResponse;
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.storage.SharedPref;


public class CallsFragment extends Fragment {

    @SuppressLint("StaticFieldLeak")
    public static CallsFragmentBinding binding;
    public static Call_adapter adapter;
    ApiInterface apiInterface;
    public static String SfType, FwFlag, SfCode, SfName, DivCode, Designation, StateCode, SubDivisionCode, TodayPlanSfCode, SampleValidation, InputValidation;
    LoginResponse loginResponse;
    public static ArrayList<CallsModalClass> TodayCallList = new ArrayList<>();
    SQLite sqLite;
    public static boolean isNeedtoAdd;
    public static ProgressDialog progressDialog;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.v("fragment", "TodayCalls");
        binding = CallsFragmentBinding.inflate(inflater, container, false);
        View v = binding.getRoot();
        sqLite = new SQLite(requireContext());

        apiInterface = RetrofitClient.getRetrofit(requireContext(), SharedPref.getCallApiUrl(requireContext()));
        getRequiredData();
        CallTodayCallsAPI(requireContext(), apiInterface, sqLite, true);

        binding.rlSyncCall.setOnClickListener(v12 -> {
            if (UtilityClass.isNetworkAvailable(requireContext())) {
                CallTodayCallsAPI(requireContext(), apiInterface, sqLite, true);
            } else {
                Toast.makeText(requireContext(), "No Network Available!", Toast.LENGTH_SHORT).show();
            }
        });

        binding.tvAddCall.setOnClickListener(view -> {
            if (!SharedPref.getTodayDayPlanSfCode(requireContext()).isEmpty() && !SharedPref.getTodayDayPlanSfCode(requireContext()).equalsIgnoreCase("null")) {
                startActivity(new Intent(getContext(), DcrCallTabLayoutActivity.class));
            } else {
                Toast.makeText(requireContext(), "Kindly submit myDayPlan", Toast.LENGTH_SHORT).show();
            }
        });

        return v;
    }

    public static void CallTodayCallsAPI(Context context, ApiInterface apiInterface, SQLite sqLite, boolean isProgressNeed) {
        TodayCallList.clear();
        if (UtilityClass.isNetworkAvailable(context)) {
            SharedPref.setTodayCallList(context, "");
            if (isProgressNeed)
                progressDialog = CommonUtilsMethods.createProgressDialog(context);
            try {
                apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("tableName", "gettodycalls");
                jsonObject.put("sfcode", SfCode);
                jsonObject.put("ReqDt", CommonUtilsMethods.getCurrentDate());
                jsonObject.put("division_code", DivCode);
                jsonObject.put("Rsf", TodayPlanSfCode);
                jsonObject.put("sf_type", SfType);
                jsonObject.put("Designation", Designation);
                jsonObject.put("state_code", StateCode);
                jsonObject.put("subdivision_code", SubDivisionCode);
                Log.v("TodayCalls", "--json--" + jsonObject);

                Call<JsonArray> getTodayCalls = null;
                getTodayCalls = apiInterface.getTodayCalls(jsonObject.toString());

                ApiInterface finalApiInterface = apiInterface;
                getTodayCalls.enqueue(new Callback<JsonArray>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(@NonNull Call<JsonArray> call, @NonNull Response<JsonArray> response) {
                        if (response.isSuccessful()) {
                            try {
                                assert response.body() != null;
                                SharedPref.setTodayCallList(context, response.body().toString());
                                JSONArray jsonArray = new JSONArray(response.body().toString());
                                JSONArray jsonArray1 = sqLite.getMasterSyncDataByKey(Constants.DCR);
                                JSONArray jsonArray2 = sqLite.getMasterSyncDataByKey(Constants.DCR);
                                ArrayList<CallsModalClass> TodayCallListOne = new ArrayList<>();
                                ArrayList<CallsModalClass> TodayCallListTwo = new ArrayList<>();

                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject json = jsonArray.getJSONObject(i);
                                    TodayCallList.add(new CallsModalClass(json.getString("Trans_SlNo"), json.getString("ADetSLNo"), json.getString("CustName"), json.getString("CustCode"), json.getString("vstTime"), json.getString("CustType")));
                                    TodayCallListTwo.add(new CallsModalClass(json.getString("Trans_SlNo"), json.getString("ADetSLNo"), json.getString("CustName"), json.getString("CustCode"), json.getString("vstTime"), json.getString("CustType")));

                                    for (int j = 0; j < jsonArray1.length(); j++) {
                                        JSONObject jsonObject = jsonArray1.getJSONObject(j);
                                        if (json.getString("vstTime").substring(0, 10).equalsIgnoreCase(jsonObject.getString("Dcr_dt")) && jsonObject.getString("CustCode").equalsIgnoreCase(json.getString("CustCode"))) {
                                            TodayCallListOne.add(new CallsModalClass(json.getString("Trans_SlNo"), json.getString("ADetSLNo"), json.getString("CustName"), json.getString("CustCode"), json.getString("vstTime"), json.getString("CustType")));
                                            jsonArray1.remove(j);
                                            break;
                                        }
                                    }
                                }

                                if (jsonArray.length() > 0) {

                                    JSONArray jsonArrayWt = sqLite.getMasterSyncDataByKey(Constants.WORK_TYPE);
                                    for (int i = 0; i < jsonArrayWt.length(); i++) {
                                        JSONObject workTypeData = jsonArrayWt.getJSONObject(i);
                                        if (workTypeData.getString("FWFlg").equalsIgnoreCase("F")) {
                                            FwFlag = workTypeData.getString("FWFlg");
                                        }
                                    }

                                    if (TodayCallListTwo.size() != TodayCallListOne.size()) {
                                        for (int i = 0; i < TodayCallListTwo.size(); i++) {
                                            if (TodayCallListOne.size() > 0) {
                                                isNeedtoAdd = true;
                                                for (int j = 0; j < TodayCallListOne.size(); j++) {
                                                    if (TodayCallListTwo.get(i).getDocCode().equalsIgnoreCase(TodayCallListOne.get(j).getDocCode())) {
                                                        TodayCallListTwo.remove(i);
                                                    }
                                                }
                                            } else {
                                                isNeedtoAdd = false;
                                                SaveDCRData(TodayCallListTwo, i, jsonArray2);
                                            }
                                        }
                                        if (isNeedtoAdd && TodayCallListTwo.size() > 0) {
                                            for (int i = 0; i < TodayCallListTwo.size(); i++) {
                                                SaveDCRData(TodayCallListTwo, i, jsonArray2);
                                            }
                                        }
                                    }
                                    sqLite.saveMasterSyncData(Constants.DCR, jsonArray2.toString(), 0);
                                    CallAnalysisFragment.SetcallDetailsInLineChart(sqLite, context);
                                }


                                binding.txtCallcount.setText(String.valueOf(TodayCallList.size()));

                                adapter = new Call_adapter(context, TodayCallList, finalApiInterface);
                                LinearLayoutManager manager = new LinearLayoutManager(context);
                                binding.recyelerview.setNestedScrollingEnabled(false);
                                binding.recyelerview.setHasFixedSize(true);
                                binding.recyelerview.setLayoutManager(manager);
                                binding.recyelerview.setAdapter(adapter);
                                adapter.notifyDataSetChanged();
                                if (isProgressNeed)
                                    progressDialog.dismiss();
                            } catch (Exception e) {
                                if (isProgressNeed)
                                    progressDialog.dismiss();
                                Log.v("TodayCalls", "--error--" + e);
                            }
                        } else {
                            if (isProgressNeed)
                                progressDialog.dismiss();
                            Toast.makeText(context, "Response Failed! Please Try Again", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonArray> call, @NonNull Throwable t) {
                        if (isProgressNeed)
                            progressDialog.dismiss();
                        Toast.makeText(context, "Response Failed! Please Try Again", Toast.LENGTH_SHORT).show();
                    }
                });
            } catch (Exception e) {
                if (isProgressNeed)
                    progressDialog.dismiss();
                Log.v("TodayCalls", "--error--2--" + e);
            }
        } else {
            try {
                if (!SharedPref.getTodayCallList(context).isEmpty()) {
                    JSONArray jsonArray = new JSONArray(SharedPref.getTodayCallList(context));
                    TodayCallList.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject json = jsonArray.getJSONObject(i);
                        TodayCallList.add(new CallsModalClass(json.getString("Trans_SlNo"), json.getString("ADetSLNo"), json.getString("CustName"), json.getString("CustCode"), json.getString("vstTime"), json.getString("CustType")));
                    }
                    binding.txtCallcount.setText(String.valueOf(TodayCallList.size()));
                    adapter = new Call_adapter(context, TodayCallList, apiInterface);
                    LinearLayoutManager manager = new LinearLayoutManager(context);
                    binding.recyelerview.setNestedScrollingEnabled(false);
                    binding.recyelerview.setHasFixedSize(true);
                    binding.recyelerview.setLayoutManager(manager);
                    binding.recyelerview.setAdapter(adapter);
                } else {
                    TodayCallList.clear();
                    binding.txtCallcount.setText(String.valueOf(TodayCallList.size()));
                    adapter = new Call_adapter(context, TodayCallList, apiInterface);
                    LinearLayoutManager manager = new LinearLayoutManager(context);
                    binding.recyelerview.setNestedScrollingEnabled(false);
                    binding.recyelerview.setHasFixedSize(true);
                    binding.recyelerview.setLayoutManager(manager);
                    binding.recyelerview.setAdapter(adapter);
                }
            } catch (Exception e) {

            }
        }
    }

    public static void SaveDCRData(ArrayList<CallsModalClass> todayCallListTwo, int i, JSONArray jsonArray2) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("CustCode", todayCallListTwo.get(i).getDocCode());
            jsonObject.put("CustType", todayCallListTwo.get(i).getDocNameID());
            jsonObject.put("Dcr_dt", todayCallListTwo.get(i).getCallsDateTime().substring(0, 10));
            jsonObject.put("month_name", CommonUtilsMethods.getCurrentMonthName());
            jsonObject.put("Mnth", CommonUtilsMethods.getCurrentMonthNumber());
            jsonObject.put("Yr", CommonUtilsMethods.getCurrentYear());
            jsonObject.put("CustName", todayCallListTwo.get(i).getDocName());
            jsonObject.put("town_code", "");
            jsonObject.put("town_name", "");
            jsonObject.put("Dcr_flag", "");
            jsonObject.put("SF_Code", SfCode);
            jsonObject.put("Trans_SlNo", todayCallListTwo.get(i).getTrans_Slno());
            jsonObject.put("FW_Indicator", FwFlag);
            jsonObject.put("AMSLNo", todayCallListTwo.get(i).getADetSLNo());
            jsonArray2.put(jsonObject);
        } catch (Exception ignored) {

        }
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
        SampleValidation = loginResponse.getSample_validation();
        InputValidation = loginResponse.getInput_validation();
        TodayPlanSfCode = SharedPref.getTodayDayPlanSfCode(requireContext());
    }
}