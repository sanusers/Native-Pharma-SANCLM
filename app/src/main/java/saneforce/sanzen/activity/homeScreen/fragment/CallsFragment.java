package saneforce.sanzen.activity.homeScreen.fragment;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
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

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.activityModule.DynamicActivity;
import saneforce.sanzen.activity.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.activity.homeScreen.adapters.Call_adapter;
import saneforce.sanzen.activity.homeScreen.fragment.worktype.WorkPlanFragment;
import saneforce.sanzen.activity.homeScreen.modelClass.CallsModalClass;
import saneforce.sanzen.commonClasses.CheckInOutManager;
import saneforce.sanzen.commonClasses.CommonAlertBox;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.CallsFragmentBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.CallDataRestClass;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.NetworkStatusTask;
import saneforce.sanzen.utility.TimeUtils;

public class CallsFragment extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static CallsFragmentBinding binding;
    public static Call_adapter adapter;
    public static String FwFlag;
    public static ArrayList<CallsModalClass> TodayCallList = new ArrayList<>();
    public static boolean isNeedtoAdd;
    public static ProgressDialog progressDialog;
    private static ApiInterface apiInterface;
    private RoomDB db;
    private static MasterDataDao masterDataDao;
    CommonUtilsMethods commonUtilsMethods;
    @SuppressLint("StaticFieldLeak")
    public static Context Mcontext;
    public static boolean syncCalls = false;

    public static void syncCalls() {
        if (Mcontext != null && apiInterface != null) {
            CallTodayCallsAPI(Mcontext, apiInterface, false);
        }
    }

    public static void CallTodayCallsAPI(Context context, ApiInterface apiInterface, boolean isProgressNeed) {
        if (HomeDashBoard.selectedDate != null) {
            if (UtilityClass.isNetworkAvailable(context)) {
                CommonUtilsMethods commonUtilsMethods = new CommonUtilsMethods(context);
                apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));
                ApiInterface finalApiInterface1 = apiInterface;
                if (isProgressNeed)
                    progressDialog = CommonUtilsMethods.createProgressDialog(context);
                NetworkStatusTask networkStatusTask = new NetworkStatusTask(context, status -> {
                    if (status) {
                        SharedPref.setTodayCallList(context, "");
                        try {
                            JSONObject jsonObject = CommonUtilsMethods.CommonObjectParameter(context);
                            jsonObject.put("tableName", "gettodycalls");
                            jsonObject.put("sfcode", SharedPref.getSfCode(context));
                            jsonObject.put("ReqDt", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_34, TimeUtils.FORMAT_4, HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_34))));
                            jsonObject.put("day_flag", "0");
                            jsonObject.put("division_code", SharedPref.getDivisionCode(context));
                            jsonObject.put("Rsf", SharedPref.getHqCode(context));
                            Log.v("TodayCalls", "--json--" + jsonObject);

                            Map<String, String> mapString = new HashMap<>();
                            mapString.put("axn", "table/additionaldcrmasterdata");
                            Call<JsonElement> getTodayCalls = finalApiInterface1.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonObject.toString());

                            ApiInterface finalApiInterface = finalApiInterface1;
                            getTodayCalls.enqueue(new Callback<JsonElement>() {
                                @SuppressLint("NotifyDataSetChanged")
                                @Override
                                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                                    binding.rlSyncCall.setEnabled(true);
                                    if (response.isSuccessful()) {
                                        try {
                                            assert response.body() != null;
                                            SharedPref.setTodayCallList(context, response.body().toString());
                                            JSONArray jsonArray = new JSONArray(response.body().toString());
                                            SharedPref.setLastCallDate(context, "");
                                            JSONArray jsonArray1 = masterDataDao.getMasterDataTableOrNew(Constants.CALL_SYNC).getMasterSyncDataJsonArray();
                                            JSONArray jsonArray2 = masterDataDao.getMasterDataTableOrNew(Constants.CALL_SYNC).getMasterSyncDataJsonArray();
                                            ArrayList<CallsModalClass> TodayCallListOne = new ArrayList<>();
                                            ArrayList<CallsModalClass> TodayCallListTwo = new ArrayList<>();
                                            TodayCallList.clear();
                                            for (int i = 0; i < jsonArray.length(); i++) {
                                                JSONObject json = jsonArray.getJSONObject(i);
                                                SharedPref.setLastCallDate(context, HomeDashBoard.selectedDate.toString());
                                                TodayCallList.add(new CallsModalClass(json.optString("Trans_SlNo"), json.optString("ADetSLNo"), json.optString("CustName"), json.optString("CustCode"), json.optString("CustType"), json.optString("vstTime"), json.optString("DCRdt"), json.optString("CustType"), json.optString("Prod_Samp"), json.optString("Inputs")));
                                                TodayCallListTwo.add(new CallsModalClass(json.optString("Trans_SlNo"), json.optString("ADetSLNo"), json.optString("CustName"), json.optString("CustCode"), json.optString("CustType"), json.optString("vstTime"), json.optString("DCRdt"), json.optString("CustType"), json.optString("Prod_Samp"), json.optString("Inputs")));

                                                if (SharedPref.getOneBuild(context).equalsIgnoreCase("0")) {
                                                    for (int j = 0; j < jsonArray1.length(); j++) {
                                                        JSONObject jsonObject = jsonArray1.getJSONObject(j);
                                                        if (json.optString("DCRdt").substring(0, 10).equalsIgnoreCase(jsonObject.optString("Dcr_dt")) && jsonObject.optString("CustCode").equalsIgnoreCase(json.optString("CustCode"))&& jsonObject.optString("CustType").equalsIgnoreCase(json.optString("CustType"))) {
                                                            TodayCallListOne.add(new CallsModalClass(json.optString("Trans_SlNo"), json.optString("ADetSLNo"), json.optString("CustName"), json.optString("CustCode"), json.optString("CustType"), json.optString("vstTime"), json.optString("DCRdt"), json.optString("CustType"), json.optString("Prod_Samp"), json.optString("Inputs")));
                                                            jsonArray1.remove(j);
                                                            break;
                                                        }
                                                    }
                                                } else {
                                                    for (int j = 0; j < jsonArray1.length(); j++) {
                                                        JSONObject jsonObject = jsonArray1.getJSONObject(j);
                                                        if (json.optString("vstTime").substring(0, 10).equalsIgnoreCase(jsonObject.optString("Dcr_dt")) && jsonObject.optString("CustCode").equalsIgnoreCase(json.optString("CustCode")) && jsonObject.optString("CustType").equalsIgnoreCase(json.optString("CustType"))) {
                                                            TodayCallListOne.add(new CallsModalClass(json.optString("Trans_SlNo"), json.optString("ADetSLNo"), json.optString("CustName"), json.optString("CustCode"), json.optString("CustType"), json.optString("vstTime"), json.optString("DCRdt"), json.optString("CustType"), json.optString("Prod_Samp"), json.optString("Inputs")));
                                                            jsonArray1.remove(j);
                                                            break;
                                                        }
                                                    }
                                                }
                                            }

                                            if (jsonArray.length() > 0) {

                                                JSONArray jsonArrayWt = masterDataDao.getMasterDataTableOrNew(Constants.WORK_TYPE).getMasterSyncDataJsonArray();
                                                for (int i = 0; i < jsonArrayWt.length(); i++) {
                                                    JSONObject workTypeData = jsonArrayWt.getJSONObject(i);
                                                    if (workTypeData.optString("FWFlg").equalsIgnoreCase("F")) {
                                                        FwFlag = workTypeData.optString("FWFlg");
                                                    }
                                                }

                                                if (TodayCallListTwo.size() != TodayCallListOne.size()) {
                                                    for (int i = 0; i < TodayCallListTwo.size(); i++) {
                                                        if (TodayCallListOne.size() > 0) {
                                                            isNeedtoAdd = true;
                                                            for (int j = 0; j < TodayCallListOne.size(); j++) {
                                                                if (TodayCallListTwo.get(i).getCustCode().equalsIgnoreCase(TodayCallListOne.get(j).getCustCode())) {
                                                                    TodayCallListTwo.remove(i);
                                                                }
                                                            }
                                                        } else {
                                                            isNeedtoAdd = false;
                                                            SaveDCRData(context, TodayCallListTwo, i, jsonArray2);
                                                        }
                                                    }
                                                    if (isNeedtoAdd && TodayCallListTwo.size() > 0) {
                                                        for (int i = 0; i < TodayCallListTwo.size(); i++) {
                                                            SaveDCRData(context, TodayCallListTwo, i, jsonArray2);
                                                        }
                                                    }
                                                }

                                                MasterDataTable data = new MasterDataTable();
                                                data.setMasterKey(Constants.CALL_SYNC);
                                                data.setMasterValues(jsonArray2.toString());
                                                data.setSyncStatus(0);
                                                MasterDataTable mNChecked = masterDataDao.getMasterSyncDataByKey(Constants.CALL_SYNC);
                                                if (mNChecked != null) {
                                                    masterDataDao.updateData(Constants.CALL_SYNC, jsonArray2.toString());
                                                } else {
                                                    masterDataDao.insert(data);

                                                }
                                                CallDataRestClass.resetcallValues(context);
                                            }
                                            binding.txtCallcount.setText(String.valueOf(TodayCallList.size()));
                                            adapter.notifyDataSetChanged();
                                            if (isProgressNeed) progressDialog.dismiss();
                                            SharedPref.setLastCallSyncDate(context, HomeDashBoard.selectedDate.toString());
                                        } catch (Exception e) {
                                            if (isProgressNeed) progressDialog.dismiss();
                                            Log.v("TodayCalls", "--error--" + e);
                                            e.printStackTrace();
                                        }
                                    } else {
                                        if (isProgressNeed) progressDialog.dismiss();
                                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.no_network));
                                    }
                                }
                                @Override
                                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                                    binding.rlSyncCall.setEnabled(true);
                                    if (isProgressNeed) progressDialog.dismiss();
                                    commonUtilsMethods.showToastMessage(context, context.getString(R.string.no_network));
                                }
                            });
                        } catch (Exception e) {
                            if (isProgressNeed) progressDialog.dismiss();
                            Log.v("TodayCalls", "--error--2--" + e);
                        }
                    } else {
                        if (isProgressNeed) {
                            binding.rlSyncCall.setEnabled(true);
                            progressDialog.dismiss();
                            commonUtilsMethods.showToastMessage(context, context.getString(R.string.poor_connection));
                        }
                    }
                });
                networkStatusTask.execute();
            } else {
                binding.rlSyncCall.setEnabled(true);
                getFromLocal(context, apiInterface);
            }
        }
    }

    private static void getFromLocal(Context context, ApiInterface apiInterface) {
        try {
            TodayCallList.clear();
            String CheckDate = "";
            boolean isDataAvailable = false;
            if (!SharedPref.getTodayCallList(context).isEmpty()) {
                JSONArray jsonArray = new JSONArray(SharedPref.getTodayCallList(context));
//                if (SharedPref.getOneBuild(context).equalsIgnoreCase("0")) {
//                    CheckDate = jsonArray.getJSONObject(0).optString("DCRdt").substring(0, 10);
//                } else {
//                    CheckDate = jsonArray.getJSONObject(0).optString("vstTime").substring(0, 10);
//                }
//
//                if (CheckDate.equalsIgnoreCase(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_34, TimeUtils.FORMAT_4, HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_34))))) {
//                    isDataAvailable = true;
//                }
//
//                if (isDataAvailable) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject json = jsonArray.getJSONObject(i);
                        SharedPref.setLastCallDate(context, HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4)));
                        TodayCallList.add(new CallsModalClass(json.optString("Trans_SlNo"), json.optString("ADetSLNo"), json.optString("CustName"), json.optString("CustCode"), json.optString("CustType"), json.optString("vstTime"), json.optString("DCRdt"), json.optString("CustType"), json.optString("Prod_Samp"), json.optString("Inputs")));
                    }
//                }
            }
            binding.txtCallcount.setText(String.valueOf(TodayCallList.size()));
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void SaveDCRData(Context context, ArrayList<CallsModalClass> todayCallListTwo, int i, JSONArray jsonArray2) {
        try {
            SharedPref.setLastCallDate(context, HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4)));
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("CustCode", todayCallListTwo.get(i).getCustCode());
            jsonObject.put("CustType", todayCallListTwo.get(i).getDocNameID());
            jsonObject.put("Dcr_dt", todayCallListTwo.get(i).getCallsDateTime().substring(0, 10));
            jsonObject.put("month_name", CommonUtilsMethods.getCurrentInstance("MMMM"));
            jsonObject.put("Mnth", CommonUtilsMethods.getCurrentInstance("M"));
            jsonObject.put("Yr", CommonUtilsMethods.getCurrentInstance("yyyy"));
            jsonObject.put("CustName", todayCallListTwo.get(i).getCustName());
            jsonObject.put("town_code", "");
            jsonObject.put("town_name", "");
            jsonObject.put("Dcr_flag", "");
            jsonObject.put("SF_Code", SharedPref.getSfCode(context));
            jsonObject.put("Trans_SlNo", todayCallListTwo.get(i).getTrans_Slno());
            jsonObject.put("FW_Indicator", FwFlag);
            jsonObject.put("WorkType_Name", "");
            jsonObject.put("AMSLNo", todayCallListTwo.get(i).getADetSLNo());
            jsonObject.put("versionNo", context.getString(R.string.app_version));
            jsonObject.put("mod", Constants.APP_MODE);
            jsonObject.put("Device_version", Build.VERSION.RELEASE);
            jsonObject.put("Device_name", Build.MANUFACTURER + " - " + Build.MODEL);
            jsonObject.put("AppName", context.getString(R.string.str_app_name));
            jsonObject.put("language", SharedPref.getSelectedLanguage(context));
            jsonArray2.put(jsonObject);
        } catch (Exception ignored) {

        }
    }

    @SuppressLint("StringFormatInvalid")
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = CallsFragmentBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        Mcontext = requireContext();

        apiInterface = RetrofitClient.getRetrofit(requireContext(), SharedPref.getCallApiUrl(requireContext()));
        getFromLocal(requireContext(), apiInterface);
        if (
//                syncCalls ||
                (HomeDashBoard.selectedDate != null && !(SharedPref.getLastCallSyncDate(requireContext()).equalsIgnoreCase(HomeDashBoard.selectedDate.toString())))) {
            syncCalls = false;
            CallTodayCallsAPI(requireContext(), apiInterface, false);
        }
        db = RoomDB.getDatabase(requireContext());
        masterDataDao = db.masterDataDao();

        if (SharedPref.getActivityNd(requireContext()).equalsIgnoreCase("0")) {
            binding.TvAddActivty.setVisibility(View.VISIBLE);
        } else {
            binding.TvAddActivty.setVisibility(View.GONE);
        }
        //binding.TvAddActivty.setText("Add " + SharedPref.getActivityCap(requireContext()));
        String activityCap = SharedPref.getActivityCap(requireContext());
        binding.TvAddActivty.setText(getString(R.string.add_activity, activityCap));


        adapter = new Call_adapter(requireContext(), TodayCallList, apiInterface);
        LinearLayoutManager manager = new LinearLayoutManager(requireContext());
        binding.recyelerview.setNestedScrollingEnabled(false);
        binding.recyelerview.setHasFixedSize(true);
        binding.recyelerview.setLayoutManager(manager);
        binding.recyelerview.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        binding.rlSyncCall.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (SharedPref.getApprovalManatoryStatus(requireContext()) && SharedPref.getSfType(requireActivity()).equalsIgnoreCase("2") && SharedPref.getApprMandatoryNeed(requireActivity()).equalsIgnoreCase("0")) {
                    CommonAlertBox.ApprovalAlert(requireActivity());
                } else if (SharedPref.getTpmanatoryStatus(requireContext()) && SharedPref.getTpMandatoryNeed(requireActivity()).equalsIgnoreCase("0") && SharedPref.getTpNeed(requireActivity()).equalsIgnoreCase("0")) {
                    CommonAlertBox.TpAlert(requireActivity());
                } else {
                    if (UtilityClass.isNetworkAvailable(requireContext())) {
                        binding.rlSyncCall.setEnabled(false);
                        CallTodayCallsAPI(requireContext(), apiInterface, true);
                    } else {
                        commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_network));
                    }
                }
            }
        });


        binding.TvAddActivty.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
//            if(UtilityClass.isNetworkAvailable(requireContext())){
//            if(HomeDashBoard.selectedDate == null || (HomeDashBoard.selectedDate != null && HomeDashBoard.selectedDate.toString().isEmpty())){
//                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.submit_work_plan));
//            }else
                if (SharedPref.getApprovalManatoryStatus(requireContext()) && SharedPref.getSfType(requireActivity()).equalsIgnoreCase("2") && SharedPref.getApprMandatoryNeed(requireActivity()).equalsIgnoreCase("0")) {
                    CommonAlertBox.ApprovalAlert(requireActivity());
                } else if (SharedPref.getTpmanatoryStatus(requireContext()) && SharedPref.getTpMandatoryNeed(requireContext()).equalsIgnoreCase("0") && SharedPref.getTpNeed(requireContext()).equalsIgnoreCase("0")) {
                    CommonAlertBox.TpAlert(requireActivity());
                } else {
                    JSONArray workTypeArray = masterDataDao.getMasterDataTableOrNew(Constants.WORK_PLAN).getMasterSyncDataJsonArray();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                    try {
                        if (workTypeArray.length() > 0) {
                            JSONObject FirstSeasonDayPlanObject = workTypeArray.getJSONObject(0);
                            String DayPlanDate1 = FirstSeasonDayPlanObject.getJSONObject("TPDt").optString("date");
                            Date FirstPlanDate = sdf.parse(DayPlanDate1);
                            String CurrentDate = HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4));
                            Date CurentDate = sdf.parse(CurrentDate);
                            if (workTypeArray.length() > 1) {
                                JSONObject SecondSeasonDayPlanObject = workTypeArray.getJSONObject(1);
                                String DayPlanDate2 = SecondSeasonDayPlanObject.getJSONObject("TPDt").optString("date");
                                Date SecondPlanDate = sdf.parse(DayPlanDate2);
                                if ((FirstPlanDate != null && FirstPlanDate.equals(CurentDate)) || (SecondPlanDate != null && SecondPlanDate.equals(CurentDate))) {
                                    startActivity(new Intent(requireActivity(), DynamicActivity.class));
                                } else {
                                    commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.submit_work_plan));
                                }
                            } else {
                                if (FirstPlanDate != null && FirstPlanDate.equals(CurentDate)) {
                                    startActivity(new Intent(requireActivity(), DynamicActivity.class));
                                } else {
                                    commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.submit_work_plan));
                                }
                            }
                        } else {
                            commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.submit_work_plan));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();

                    }
                }
//        }else {
//                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_network));
//            }
            }
        });


        binding.tvAddCall.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (HomeDashBoard.selectedDate == null || HomeDashBoard.selectedDate.toString().isEmpty()) {
                    commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.submit_work_plan));
                } else if (SharedPref.getApprovalManatoryStatus(requireContext()) && SharedPref.getSfType(requireActivity()).equalsIgnoreCase("2") && SharedPref.getApprMandatoryNeed(requireActivity()).equalsIgnoreCase("0")) {
                    CommonAlertBox.ApprovalAlert(requireActivity());
                } else if (SharedPref.getTpmanatoryStatus(requireContext()) && SharedPref.getTpMandatoryNeed(requireContext()).equalsIgnoreCase("0") && SharedPref.getTpNeed(requireContext()).equalsIgnoreCase("0")) {
                    CommonAlertBox.TpAlert(requireActivity());
                }
//            else if(CheckInOutManager.isCheckedId(requireContext())) {
//                WorkPlanFragment.showCheckInDialog();
//                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.submit_checkin));
//            }
                else {
                    if (SharedPref.getSfCode(requireContext()).equalsIgnoreCase("0")) {
                        if (SharedPref.getHqCode(requireContext()).equalsIgnoreCase("null") || SharedPref.getHqCode(requireContext()).isEmpty()) {
                            commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.submit_work_plan));
                        } else if (SharedPref.getSrtNd(requireContext()).equalsIgnoreCase("0") && !CheckInOutManager.isCheckedIn(requireContext())
                                && HomeDashBoard.selectedDate != null
                                && HomeDashBoard.selectedDate.toString().equalsIgnoreCase(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4))) {
//                        WorkPlanFragment.showCheckInDialog();
                            commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.submit_checkin));
                        } else if (WorkPlanFragment.isFromTP) {
                            commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.submit_work_plan));
                        } else if (WorkPlanFragment.deviation.equalsIgnoreCase("1") && SharedPref.getTpdcrMgrappr(requireContext()).equalsIgnoreCase("0") && SharedPref.getTpdcrDeviationApprStatus(requireContext()).equalsIgnoreCase("3")) {
                            commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.get_deviation_approval));
                        } else {
                            startActivity(new Intent(getContext(), DcrCallTabLayoutActivity.class));
                        }
                    } else {
                        JSONArray workTypeArray = masterDataDao.getMasterDataTableOrNew(Constants.WORK_PLAN).getMasterSyncDataJsonArray();
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                        try {
                            if (SharedPref.getSrtNd(requireContext()).equalsIgnoreCase("0") && !CheckInOutManager.isCheckedIn(requireContext())
                                    && HomeDashBoard.selectedDate != null
                                    && HomeDashBoard.selectedDate.toString().equalsIgnoreCase(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_4))) {
//                            WorkPlanFragment.showCheckInDialog();
                                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.submit_checkin));
                            } else if (WorkPlanFragment.isFromTP && WorkPlanFragment.binding.txtSave.isEnabled()) {
                                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.submit_work_plan));
                            } else if (WorkPlanFragment.deviation.equalsIgnoreCase("1") && SharedPref.getTpdcrMgrappr(requireContext()).equalsIgnoreCase("0") && SharedPref.getTpdcrDeviationApprStatus(requireContext()).equalsIgnoreCase("3")) {
                               // commonUtilsMethods.showToastMessage(requireContext(), "Get Deviation Approval");
                                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.get_deviation_approval));
                            } else if (workTypeArray.length() > 0) {
                                JSONObject FirstSeasonDayPlanObject = workTypeArray.getJSONObject(0);
                                String DayPlanDate1 = FirstSeasonDayPlanObject.getJSONObject("TPDt").optString("date");
                                String FWFlg1 = FirstSeasonDayPlanObject.optString("FWFlg");
                                Date FirstPlanDate = sdf.parse(DayPlanDate1);
                                String CurrentDate = HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4));
                                Date CurentDate = sdf.parse(CurrentDate);
                                if (workTypeArray.length() > 1) {
                                    JSONObject SecondSeasonDayPlanObject = workTypeArray.getJSONObject(1);
                                    String DayPlanDate2 = SecondSeasonDayPlanObject.getJSONObject("TPDt").optString("date");
                                    String FWFlg2 = SecondSeasonDayPlanObject.optString("FWFlg");
                                    Date SecondPlanDate = sdf.parse(DayPlanDate2);
                                    if ((FirstPlanDate != null && FirstPlanDate.equals(CurentDate)) || (SecondPlanDate != null && SecondPlanDate.equals(CurentDate))) {
                                        if (!FWFlg1.equalsIgnoreCase("F") && (!FWFlg2.equalsIgnoreCase("F"))) {
                                            commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.unable_to_add_call_for_non_field_work));
                                        } else {
                                            startActivity(new Intent(getContext(), DcrCallTabLayoutActivity.class));
                                        }
                                    } else {
                                        commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.submit_work_plan));
                                    }
                                } else {
                                    if (FirstPlanDate != null && FirstPlanDate.equals(CurentDate)) {
                                        if (!FWFlg1.equalsIgnoreCase("F")) {
                                            commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.unable_to_add_call_for_non_field_work));
                                        } else {
                                            startActivity(new Intent(getContext(), DcrCallTabLayoutActivity.class));
                                        }
                                    } else {
                                        commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.submit_work_plan));
                                    }
                                }
                            } else {
                                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.kindly_submit_field_work));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}