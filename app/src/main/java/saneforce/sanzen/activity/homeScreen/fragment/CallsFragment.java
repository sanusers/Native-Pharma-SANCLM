package saneforce.sanzen.activity.homeScreen.fragment;

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
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.activity.homeScreen.adapters.Call_adapter;
import saneforce.sanzen.activity.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.sanzen.activity.homeScreen.modelClass.CallsModalClass;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
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
    public static String  FwFlag;
    public static ArrayList<CallsModalClass> TodayCallList = new ArrayList<>();
    public static boolean isNeedtoAdd;
    public static ProgressDialog progressDialog;
    ApiInterface apiInterface;

    private RoomDB db;
    private static MasterDataDao masterDataDao;
    CommonUtilsMethods commonUtilsMethods;


    public static  Context Mcontext;

    public static void CallTodayCallsAPI(Context context, ApiInterface apiInterface, boolean isProgressNeed) {
        if(HomeDashBoard.selectedDate != null) {
            if(UtilityClass.isNetworkAvailable(context)) {
                CommonUtilsMethods commonUtilsMethods = new CommonUtilsMethods(context);
                apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));
                ApiInterface finalApiInterface1 = apiInterface;
                if(isProgressNeed)
                    progressDialog = CommonUtilsMethods.createProgressDialog(context);
                NetworkStatusTask networkStatusTask = new NetworkStatusTask(context, status -> {
                    if(status) {
                        SharedPref.setTodayCallList(context, "");
                        try {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("tableName", "gettodycalls");
                            jsonObject.put("sfcode", SharedPref.getSfCode(context));
                            jsonObject.put("ReqDt", TimeUtils.GetConvertedDate(TimeUtils.FORMAT_34, TimeUtils.FORMAT_4, HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_34))));
                            jsonObject.put("day_flag", "0");
                            jsonObject.put("division_code", SharedPref.getDivisionCode(context));
                            jsonObject.put("Rsf", SharedPref.getHqCode(context));
                            jsonObject.put("sf_type", SharedPref.getSfType(context));
                            jsonObject.put("Designation", SharedPref.getDesig(context));
                            jsonObject.put("state_code", SharedPref.getStateCode(context));
                            jsonObject.put("subdivision_code", SharedPref.getSubdivisionCode(context));
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
                                    if(response.isSuccessful()) {

                                        try {
                                            assert response.body() != null;
                                            SharedPref.setTodayCallList(context, response.body().toString());
                                            JSONArray jsonArray = new JSONArray(response.body().toString());


                                            JSONArray jsonArray1 = masterDataDao.getMasterDataTableOrNew(Constants.CALL_SYNC).getMasterSyncDataJsonArray();
                                            JSONArray jsonArray2 = masterDataDao.getMasterDataTableOrNew(Constants.CALL_SYNC).getMasterSyncDataJsonArray();
                                            ArrayList<CallsModalClass> TodayCallListOne = new ArrayList<>();
                                            ArrayList<CallsModalClass> TodayCallListTwo = new ArrayList<>();
                                            TodayCallList.clear();
                                            for (int i = 0; i<jsonArray.length(); i++) {
                                                JSONObject json = jsonArray.getJSONObject(i);
                                                TodayCallList.add(new CallsModalClass(json.getString("Trans_SlNo"), json.getString("ADetSLNo"), json.getString("CustName"), json.getString("CustCode"), json.getString("vstTime"), json.getString("CustType")));
                                                TodayCallListTwo.add(new CallsModalClass(json.getString("Trans_SlNo"), json.getString("ADetSLNo"), json.getString("CustName"), json.getString("CustCode"), json.getString("vstTime"), json.getString("CustType")));

                                                for (int j = 0; j<jsonArray1.length(); j++) {
                                                    JSONObject jsonObject = jsonArray1.getJSONObject(j);
                                                    if(json.getString("vstTime").substring(0, 10).equalsIgnoreCase(jsonObject.getString("Dcr_dt")) && jsonObject.getString("CustCode").equalsIgnoreCase(json.getString("CustCode"))) {
                                                        TodayCallListOne.add(new CallsModalClass(json.getString("Trans_SlNo"), json.getString("ADetSLNo"), json.getString("CustName"), json.getString("CustCode"), json.getString("vstTime"), json.getString("CustType")));
                                                        jsonArray1.remove(j);
                                                        break;
                                                    }
                                                }
                                            }

                                            if(jsonArray.length()>0) {

                                                JSONArray jsonArrayWt = masterDataDao.getMasterDataTableOrNew(Constants.WORK_TYPE).getMasterSyncDataJsonArray();
                                                for (int i = 0; i<jsonArrayWt.length(); i++) {
                                                    JSONObject workTypeData = jsonArrayWt.getJSONObject(i);
                                                    if(workTypeData.getString("FWFlg").equalsIgnoreCase("F")) {
                                                        FwFlag = workTypeData.getString("FWFlg");
                                                    }
                                                }

                                                if(TodayCallListTwo.size() != TodayCallListOne.size()) {
                                                    for (int i = 0; i<TodayCallListTwo.size(); i++) {
                                                        if(TodayCallListOne.size()>0) {
                                                            isNeedtoAdd = true;
                                                            for (int j = 0; j<TodayCallListOne.size(); j++) {
                                                                if(TodayCallListTwo.get(i).getDocCode().equalsIgnoreCase(TodayCallListOne.get(j).getDocCode())) {
                                                                    TodayCallListTwo.remove(i);
                                                                }
                                                            }
                                                        }else {
                                                            isNeedtoAdd = false;
                                                            SaveDCRData(context, TodayCallListTwo, i, jsonArray2);
                                                        }
                                                    }
                                                    if(isNeedtoAdd && TodayCallListTwo.size()>0) {
                                                        for (int i = 0; i<TodayCallListTwo.size(); i++) {
                                                            SaveDCRData(context, TodayCallListTwo, i, jsonArray2);
                                                        }
                                                    }
                                                }

                                                MasterDataTable data = new MasterDataTable();
                                                data.setMasterKey(Constants.CALL_SYNC);
                                                data.setMasterValues(jsonArray2.toString());
                                                data.setSyncStatus(0);
                                                MasterDataTable mNChecked = masterDataDao.getMasterSyncDataByKey(Constants.CALL_SYNC);
                                                if(mNChecked != null) {
                                                    masterDataDao.updateData(Constants.CALL_SYNC, jsonArray2.toString());
                                                }else {
                                                    masterDataDao.insert(data);

                                                }
                                                CallDataRestClass.resetcallValues(context);
                                            }

//                                        binding.txtCallcount.setText(String.valueOf(TodayCallList.size()));
//                                        adapter = new Call_adapter(context, TodayCallList, finalApiInterface);
//                                        LinearLayoutManager manager = new LinearLayoutManager(context);
//                                        binding.recyelerview.setNestedScrollingEnabled(false);
//                                        binding.recyelerview.setHasFixedSize(true);
//                                        binding.recyelerview.setLayoutManager(manager);
//                                        binding.recyelerview.setAdapter(adapter);
                                            binding.txtCallcount.setText(String.valueOf(TodayCallList.size()));
                                            adapter.notifyDataSetChanged();

                                            if(isProgressNeed) progressDialog.dismiss();
                                        } catch (Exception e) {
                                            if(isProgressNeed) progressDialog.dismiss();
                                            Log.v("TodayCalls", "--error--" + e);
                                            e.printStackTrace();
                                        }
                                    }else {
                                        if(isProgressNeed) progressDialog.dismiss();
                                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.toast_response_failed));
                                    }
                                }

                                @Override
                                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                                    binding.rlSyncCall.setEnabled(true);
                                    if(isProgressNeed) progressDialog.dismiss();
                                    commonUtilsMethods.showToastMessage(context, context.getString(R.string.toast_response_failed));
                                }
                            });
                        } catch (Exception e) {
                            if(isProgressNeed) progressDialog.dismiss();
                            Log.v("TodayCalls", "--error--2--" + e);
                        }
                    }else {
                        if(isProgressNeed) {
                            binding.rlSyncCall.setEnabled(true);
                            progressDialog.dismiss();
                            commonUtilsMethods.showToastMessage(context, context.getString(R.string.poor_connection));
                        }
                    }
                });
                networkStatusTask.execute();
            }else {
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
                CheckDate = jsonArray.getJSONObject(0).getString("vstTime").substring(0, 10);

                if (CheckDate.equalsIgnoreCase(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_34, TimeUtils.FORMAT_4, HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_34))))) {
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

            adapter.notifyDataSetChanged();
//            adapter = new Call_adapter(context, TodayCallList, apiInterface);
//            LinearLayoutManager manager = new LinearLayoutManager(context);
//            binding.recyelerview.setNestedScrollingEnabled(false);
//            binding.recyelerview.setHasFixedSize(true);
//            binding.recyelerview.setLayoutManager(manager);
//            binding.recyelerview.setAdapter(adapter);
//            adapter.notifyDataSetChanged();
        } catch (Exception ignored) {
        }
    }

    public static void SaveDCRData(Context context,ArrayList<CallsModalClass> todayCallListTwo, int i, JSONArray jsonArray2) {
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
            jsonObject.put("SF_Code",  SharedPref.getSfCode(context));
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
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        Mcontext=requireContext();

        apiInterface = RetrofitClient.getRetrofit(requireContext(), SharedPref.getCallApiUrl(requireContext()));
        getFromLocal(requireContext(), apiInterface);
        CallTodayCallsAPI(requireContext(), apiInterface, false);
        db = RoomDB.getDatabase(requireContext());
        masterDataDao =db.masterDataDao();






        adapter = new Call_adapter(requireContext(), TodayCallList, apiInterface);
        LinearLayoutManager manager = new LinearLayoutManager(requireContext());
        binding.recyelerview.setNestedScrollingEnabled(false);
        binding.recyelerview.setHasFixedSize(true);
        binding.recyelerview.setLayoutManager(manager);
        binding.recyelerview.setAdapter(adapter);
        adapter.notifyDataSetChanged();





        binding.rlSyncCall.setOnClickListener(v12 -> {
            if (UtilityClass.isNetworkAvailable(requireContext())) {
                binding.rlSyncCall.setEnabled(false);
                CallTodayCallsAPI(requireContext(), apiInterface, true);
            } else {
                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_network));
            }
        });

        binding.tvAddCall.setOnClickListener(view -> {
            // startActivity(new Intent(getContext(), DcrCallTabLayoutActivity.class));
            if (SharedPref.getSfCode(requireContext()).equalsIgnoreCase("0")) {
                if (SharedPref.getSkipCheckIn(requireContext())) {
                    if (SharedPref.getHqCode(requireContext()).equalsIgnoreCase("null") || SharedPref.getHqCode(requireContext()).isEmpty()) {
                        commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.submit_mydayplan));
                    } else {
                        startActivity(new Intent(getContext(), DcrCallTabLayoutActivity.class));
                    }
                } else {
                    commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.submit_checkin));
                    try {
                        HomeDashBoard.dialogCheckInOut.show();
                    } catch (Exception ignored) {

                    }
                }
            } else {
                JSONArray workTypeArray = masterDataDao.getMasterDataTableOrNew(Constants.MY_DAY_PLAN).getMasterSyncDataJsonArray();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                try {
                    if(workTypeArray.length() > 0) {
                        JSONObject FirstSeasonDayPlanObject = workTypeArray.getJSONObject(0);
                        String DayPlanDate1 = FirstSeasonDayPlanObject.getJSONObject("TPDt").getString("date");
                        String FWFlg1 = FirstSeasonDayPlanObject.getString("FWFlg");
                        Date FirstPlanDate = sdf.parse(DayPlanDate1);
                        String CurrentDate = HomeDashBoard.selectedDate.format(DateTimeFormatter.ofPattern(TimeUtils.FORMAT_4));
                        Date CurentDate = sdf.parse(CurrentDate);
                        if(workTypeArray.length() > 1) {
                            JSONObject SecondSeasonDayPlanObject = workTypeArray.getJSONObject(1);
                            String DayPlanDate2 = SecondSeasonDayPlanObject.getJSONObject("TPDt").getString("date");
                            String FWFlg2 = SecondSeasonDayPlanObject.getString("FWFlg");
                            Date SecondPlanDate = sdf.parse(DayPlanDate2);
                            if((FirstPlanDate != null && FirstPlanDate.equals(CurentDate)) || (SecondPlanDate != null && SecondPlanDate.equals(CurentDate))) {
                                if(!FWFlg1.equalsIgnoreCase("F") && (!FWFlg2.equalsIgnoreCase("F")))
                                    commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.unable_to_add_call_for_non_field_work));
                                else
                                    startActivity(new Intent(getContext(), DcrCallTabLayoutActivity.class));
                            }else {
                                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.submit_mydayplan));
                            }
                        }else {
                            if(FirstPlanDate != null && FirstPlanDate.equals(CurentDate)) {
                                if(!FWFlg1.equalsIgnoreCase("F"))
                                    commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.unable_to_add_call_for_non_field_work));
                                else
                                    startActivity(new Intent(getContext(), DcrCallTabLayoutActivity.class));
                            }else {
                                commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.submit_mydayplan));
                            }
                        }
                    }else{
                        commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.kindly_submit_field_work));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}