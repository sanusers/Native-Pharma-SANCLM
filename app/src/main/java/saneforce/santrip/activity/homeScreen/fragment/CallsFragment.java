package saneforce.santrip.activity.homeScreen.fragment;

import static saneforce.santrip.activity.homeScreen.HomeDashBoard.CheckInOutNeed;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

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
import saneforce.santrip.activity.homeScreen.HomeDashBoard;
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
    public static String SfType, FwFlag, SfCode, SfName, DivCode, Designation, StateCode, SubDivisionCode, TodayPlanSfCode, SampleValidation, InputValidation;
    public static ArrayList<CallsModalClass> TodayCallList = new ArrayList<>();
    public static boolean isNeedtoAdd;
    public static ProgressDialog progressDialog;
    ApiInterface apiInterface;
    LoginResponse loginResponse;
    SQLite sqLite;
    CommonUtilsMethods commonUtilsMethods;

    public static void CallTodayCallsAPI(Context context, ApiInterface apiInterface, SQLite sqLite, boolean isProgressNeed) {
        TodayCallList.clear();
        CommonUtilsMethods commonUtilsMethods = new CommonUtilsMethods(context);
        if (UtilityClass.isNetworkAvailable(context)) {
            SharedPref.setTodayCallList(context, "");
            if (isProgressNeed) progressDialog = CommonUtilsMethods.createProgressDialog(context);
            try {
                apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("tableName", "gettodycalls");
                jsonObject.put("sfcode", SfCode);
                jsonObject.put("ReqDt", CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd"));
                jsonObject.put("division_code", DivCode);
                jsonObject.put("Rsf", TodayPlanSfCode);
                jsonObject.put("sf_type", SfType);
                jsonObject.put("Designation", Designation);
                jsonObject.put("state_code", StateCode);
                jsonObject.put("subdivision_code", SubDivisionCode);
                Log.v("TodayCalls", "--json--" + jsonObject);

                Map<String, String> mapString = new HashMap<>();
                mapString.put("axn", "table/additionaldcrmasterdata");
                Call<JsonElement> getTodayCalls = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());

                ApiInterface finalApiInterface = apiInterface;
                getTodayCalls.enqueue(new Callback<JsonElement>() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                        if (response.isSuccessful()) {
                            try {
                                assert response.body() != null;
                                SharedPref.setTodayCallList(context, response.body().toString());
                                JSONArray jsonArray = new JSONArray(response.body().toString());
                                JSONArray jsonArray1 = sqLite.getMasterSyncDataByKey(Constants.CALL_SYNC);
                                JSONArray jsonArray2 = sqLite.getMasterSyncDataByKey(Constants.CALL_SYNC);
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
                                    sqLite.saveMasterSyncData(Constants.CALL_SYNC, jsonArray2.toString(), 0);
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
                                if (isProgressNeed) progressDialog.dismiss();
                            } catch (Exception e) {
                                if (isProgressNeed) progressDialog.dismiss();
                                Log.v("TodayCalls", "--error--" + e);
                            }
                        } else {
                            if (isProgressNeed) progressDialog.dismiss();
                            commonUtilsMethods.ShowToast(context, context.getString(R.string.toast_response_failed), 100);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                        if (isProgressNeed) progressDialog.dismiss();
                        commonUtilsMethods.ShowToast(context, context.getString(R.string.toast_response_failed), 100);
                    }
                });
            } catch (Exception e) {
                if (isProgressNeed) progressDialog.dismiss();
                Log.v("TodayCalls", "--error--2--" + e);
            }
        } else {
            try {
                TodayCallList.clear();
                String CheckDate = "";
                boolean isDataAvailable = false;
                if (!SharedPref.getTodayCallList(context).isEmpty()) {
                    JSONArray jsonArray = new JSONArray(SharedPref.getTodayCallList(context));
                    CheckDate = jsonArray.getJSONObject(0).getString("vstTime").substring(0, 10);

                    if (CheckDate.equalsIgnoreCase(CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd"))) {
                        isDataAvailable = true;
                    }

                    if (isDataAvailable) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject json = jsonArray.getJSONObject(i);
                            TodayCallList.add(new CallsModalClass(json.getString("Trans_SlNo"), json.getString("ADetSLNo"), json.getString("CustName"), json.getString("CustCode"), json.getString("vstTime"), json.getString("CustType")));
                        }
                    }
                }
                binding.txtCallcount.setText(String.valueOf(TodayCallList.size()));
                adapter = new Call_adapter(context, TodayCallList, apiInterface);
                LinearLayoutManager manager = new LinearLayoutManager(context);
                binding.recyelerview.setNestedScrollingEnabled(false);
                binding.recyelerview.setHasFixedSize(true);
                binding.recyelerview.setLayoutManager(manager);
                binding.recyelerview.setAdapter(adapter);
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
            jsonObject.put("month_name", CommonUtilsMethods.getCurrentInstance("MMMM"));
            jsonObject.put("Mnth", CommonUtilsMethods.getCurrentInstance("M"));
            jsonObject.put("Yr", CommonUtilsMethods.getCurrentInstance("yyyy"));
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

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = CallsFragmentBinding.inflate(inflater, container, false);
        View v = binding.getRoot();
        sqLite = new SQLite(requireContext());
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());

        apiInterface = RetrofitClient.getRetrofit(requireContext(), SharedPref.getCallApiUrl(requireContext()));
        getRequiredData();
        CallTodayCallsAPI(requireContext(), apiInterface, sqLite, false);

        binding.rlSyncCall.setOnClickListener(v12 -> {
            if (UtilityClass.isNetworkAvailable(requireContext())) {
                CallTodayCallsAPI(requireContext(), apiInterface, sqLite, true);
            } else {
                commonUtilsMethods.ShowToast(requireContext(), requireContext().getString(R.string.no_network), 100);
            }
        });

        binding.tvAddCall.setOnClickListener(view -> {
            // startActivity(new Intent(getContext(), DcrCallTabLayoutActivity.class));
            if (CheckInOutNeed.equalsIgnoreCase("0")) {
                if (SharedPref.getSkipCheckIn(requireContext())) {
                    if (SharedPref.getTodayDayPlanSfCode(requireContext()).equalsIgnoreCase("null") || SharedPref.getTodayDayPlanSfCode(requireContext()).isEmpty()) {
                        commonUtilsMethods.ShowToast(requireContext(), requireContext().getString(R.string.submit_mydayplan), 100);
                    } else {
                        startActivity(new Intent(getContext(), DcrCallTabLayoutActivity.class));
                    }
                } else {
                    commonUtilsMethods.ShowToast(requireContext(), requireContext().getString(R.string.submit_checkin), 100);
                    try {
                        HomeDashBoard.dialogCheckInOut.show();
                    } catch (Exception ignored) {

                    }
                }
            } else {
                if (SharedPref.getTodayDayPlanSfCode(requireContext()).equalsIgnoreCase("null") || SharedPref.getTodayDayPlanSfCode(requireContext()).isEmpty()) {
                    commonUtilsMethods.ShowToast(requireContext(), requireContext().getString(R.string.submit_mydayplan), 100);
                } else {
                    startActivity(new Intent(getContext(), DcrCallTabLayoutActivity.class));
                }
            }
        });

        return v;
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