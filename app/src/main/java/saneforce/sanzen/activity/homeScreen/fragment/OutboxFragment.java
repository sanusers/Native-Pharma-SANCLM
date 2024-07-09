package saneforce.sanzen.activity.homeScreen.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import id.zelory.compressor.Compressor;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.homeScreen.adapters.outbox.OutBoxHeaderAdapter;
import saneforce.sanzen.activity.homeScreen.modelClass.CheckInOutModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.DaySubmitModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.EcModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.GroupModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.OutBoxCallList;
import saneforce.sanzen.activity.homeScreen.modelClass.WorkPlanModelClass;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.OutboxFragmentBinding;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.CallDataRestClass;
import saneforce.sanzen.roomdatabase.CallOfflineECTableDetails.CallOfflineECDataDao;
import saneforce.sanzen.roomdatabase.CallOfflineWorkTypeTableDetails.CallOfflineWorkTypeDataDao;
import saneforce.sanzen.roomdatabase.CallsUtil;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.sanzen.roomdatabase.OfflineCheckInOutTableDetails.OfflineCheckInOutDataDao;
import saneforce.sanzen.roomdatabase.OfflineDaySubmit.OfflineDaySubmitDao;
import saneforce.sanzen.roomdatabase.RoomDB;

import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.NetworkCheckInterface;
import saneforce.sanzen.utility.NetworkUtil;
import saneforce.sanzen.utility.TimeUtils;


public class OutboxFragment extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static OutboxFragmentBinding outBoxBinding;
    public static ArrayList<GroupModelClass> listDates = new ArrayList<>();
    public static boolean IsFromDCR=false;
    @SuppressLint("StaticFieldLeak")
    public static OutBoxHeaderAdapter outBoxHeaderAdapter;
    static NetworkCheckInterface mCheckNetwork;
    ApiInterface apiInterface;
    boolean isCallAvailable;
    CommonUtilsMethods commonUtilsMethods;
    private static Context context;
    private RoomDB db;
    private MasterDataDao masterDataDao;
    private OfflineCheckInOutDataDao offlineCheckInOutDataDao;
    private CallOfflineECDataDao callOfflineECDataDao;
    private CallOfflineWorkTypeDataDao offlineWorkTypeDataDao;
    private OfflineDaySubmitDao offlineDaySubmitDao;
    private static CallsUtil callsUtil;

    public static void NetworkConnectCallHomeDashBoard(String log) {

        Log.e("NOT_CONNECT",""+log);
        if (!TextUtils.isEmpty(log)) {
            if (!log.equalsIgnoreCase("NOT_CONNECT")) {
                if (mCheckNetwork != null) mCheckNetwork.checkNetwork();
            }
        }
    }

    public static void SendOfflineData(NetworkCheckInterface mCheckNetworkData) {
        mCheckNetwork = mCheckNetworkData;
    }

    @SuppressLint("NotifyDataSetChanged")
    public static void SetupOutBoxAdapter(Activity activity, Context context) {
        listDates = callsUtil.getOutBoxDatesWithData();
        outBoxHeaderAdapter = new OutBoxHeaderAdapter(activity, context, listDates);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        OutboxFragment.context = context;
        outBoxBinding.rvOutBoxHead.setLayoutManager(mLayoutManager);
        outBoxBinding.rvOutBoxHead.setAdapter(outBoxHeaderAdapter);
        notifyedmethod();

    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.v("fragment", "OutBox");
        outBoxBinding = OutboxFragmentBinding.inflate(inflater, container, false);
        View v = outBoxBinding.getRoot();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        db = RoomDB.getDatabase(requireContext());
        masterDataDao =db.masterDataDao();
        offlineCheckInOutDataDao = db.offlineCheckInOutDataDao();
        callOfflineECDataDao = db.callOfflineECDataDao();
        offlineWorkTypeDataDao = db.callOfflineWorkTypeDataDao();
        offlineDaySubmitDao = db.offlineDaySubmitDao();
        callsUtil = new CallsUtil(requireContext());
        SetupOutBoxAdapter(requireActivity(), requireContext());

        new Handler().postDelayed(this::refreshPendingFunction, 200);

        outBoxBinding.clearCalls.setOnClickListener(v1 -> {
            if(!listDates.isEmpty()) {
                Set<String> dates = callsUtil.getOutboxDates();
                ArrayList<String> finalDates = new ArrayList<>();
                for (String date : dates) {
                    finalDates.add(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_17, date));
                }
                String datesString = Arrays.toString(finalDates.toArray()).replace("[", "").replace("]", "").replaceAll(",", "\n-");
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dcr_cancel_alert);
                dialog.setCancelable(false);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();
                TextView btn_yes = dialog.findViewById(R.id.btn_yes);
                TextView btn_no = dialog.findViewById(R.id.btn_no);
                TextView message = dialog.findViewById(R.id.ed_alert_msg);
                String content = "Available Outbox dates are :\n- " + datesString + "\n\n" + context.getString(R.string.are_you_sure_you_want_to_clear);
                message.setText(content);
                btn_yes.setOnClickListener(view12 -> {
                    clearCalls();
                    dialog.dismiss();
                });
                btn_no.setOnClickListener(view12 -> {
                    dialog.dismiss();
                });
            }
        });

        return v;
    }

    private void clearCalls() {
        ArrayList<OutBoxCallList> outBoxCallLists = callsUtil.getAllOutBoxCallsList();
        try {
            if (!outBoxCallLists.isEmpty()) {
                JSONArray jsonArray = new JSONArray(masterDataDao.getDataByKey(Constants.CALL_SYNC));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    for (int j = 0; j < outBoxCallLists.size(); j++) {
                        if (jsonObject.getString("Dcr_dt").equalsIgnoreCase(outBoxCallLists.get(j).getDates()) && jsonObject.getString("CustCode").equalsIgnoreCase(outBoxCallLists.get(j).getCusCode())) {
                            jsonArray.remove(i);
                            i--;
                        }
                    }
                }
                MasterDataTable data = new MasterDataTable();
                data.setMasterKey(Constants.CALL_SYNC);
                data.setMasterValues(jsonArray.toString());
                data.setSyncStatus(0);
                MasterDataTable mNChecked = masterDataDao.getMasterSyncDataByKey(Constants.CALL_SYNC);
                if (mNChecked != null) {
                    masterDataDao.updateData(Constants.CALL_SYNC, jsonArray.toString());
                } else {
                    masterDataDao.insert(data);
                }
                CallDataRestClass.resetcallValues(context);
            }
        } catch (Exception e) {
            Log.e("Outbox", "clearCalls: "+ e.getMessage());
            e.printStackTrace();
        }

        if (SharedPref.getSrtNd(requireContext()).equalsIgnoreCase("0")) {
            if(offlineCheckInOutDataDao.getCheckInOutCount(CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd")) > 0) {
                SharedPref.setCheckInTime(requireContext(), "");
                SharedPref.setCheckDateTodayPlan(requireContext(), "");
            }
        }
        callsUtil.deleteOfflineCalls();
        listDates.clear();
        outBoxHeaderAdapter = new OutBoxHeaderAdapter(requireActivity(), requireContext(), listDates);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(requireContext());
        outBoxBinding.rvOutBoxHead.setLayoutManager(mLayoutManager);
        outBoxBinding.rvOutBoxHead.setAdapter(outBoxHeaderAdapter);
    }

    private void refreshPendingFunction() {

        Log.e("RefreshStatus","Is Working");
        SendOfflineData(this::sendingOfflineCalls);

    }

    private void sendingOfflineCalls() {
        apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));

        //CheckInOutData
        if (!listDates.isEmpty()) {
            isCallAvailable = false;
            for (int i = 0; i < listDates.size(); i++) {
                GroupModelClass modelClass = listDates.get(i);
                if (listDates.get(i).getSynced() == 0) {
                    isCallAvailable = true;
                    CallCheckInOut(i, 0, listDates.get(i).getChildItems().get(0).getCheckInOutModelClasses(), modelClass);
                    break;
                }
            }
        } else {
            isCallAvailable = false;
        }

        if (!isCallAvailable) {
            SetupOutBoxAdapter(requireActivity(), requireContext());
        }
    }

    private void CallCheckInOut(int ParentPos, int ChildPos, ArrayList<CheckInOutModelClass> checkInOutModelClasses, GroupModelClass modelClass) {
        if (SharedPref.getSrtNd(requireContext()).equalsIgnoreCase("0")) {
            if (checkInOutModelClasses.size() > 0) {
                isCallAvailable = false;
                for (int m = 0; m < checkInOutModelClasses.size(); m++) {
                    CheckInOutModelClass checkInOutModelClass = checkInOutModelClasses.get(m);
                    if (checkInOutModelClass.getCheckStatus() == 0) {
                        isCallAvailable = true;
                        Log.v("SendOutboxCall", "--checkInOut--" + checkInOutModelClass.getDates() + "---" + checkInOutModelClass.getCheckInTime() + "----" + checkInOutModelClass.getCheckOutTime());
                        if (checkInOutModelClass.getJsonOutValues().isEmpty()) {
                            CallCheckInOutAPI(ParentPos, checkInOutModelClass, ChildPos, m, checkInOutModelClass.getJsonInValues(), modelClass);
                        } else {
                            CallCheckInOutAPI(ParentPos, checkInOutModelClass, ChildPos, m, checkInOutModelClass.getJsonOutValues(), modelClass);
                        }
                    }
                    break;
                }
            } else {
                isCallAvailable = false;
            }
        } else {
            isCallAvailable = false;
        }

        if (!isCallAvailable) {
            CallAPIWorkPlan(ParentPos, 1, listDates.get(ParentPos).getChildItems().get(1).getWorkPlanModelClass(), modelClass);
        }
    }

    private void CallAPIWorkPlan(int ParentPos, int ChildPos, WorkPlanModelClass workPlanModelClass, GroupModelClass modelClass) {
        if(workPlanModelClass != null) {
            isCallAvailable = false;
            if(workPlanModelClass.getSyncStatus() == 0) {
                isCallAvailable = true;
                Log.v("SendOutboxCall", "--WorkPlan--" + workPlanModelClass.getDate() + "---" + workPlanModelClass.getWtName() + "---" + workPlanModelClass.getWtCode());
                if(!workPlanModelClass.getJsonValues().isEmpty()) {
                    CallSendWorkPlan(ParentPos, workPlanModelClass, ChildPos, workPlanModelClass.getJsonValues(), modelClass);
                }
            }
        }else {
            isCallAvailable = false;
        }

        if(!isCallAvailable) {
            CallOfflineCalls(ParentPos, 2, listDates.get(ParentPos).getChildItems().get(2).getOutBoxCallLists(), modelClass);
        }
    }

    private void CallOfflineCalls(int ParentPos, int ChildPos, ArrayList<OutBoxCallList> outBoxCallLists, GroupModelClass modelClass) {
        if (outBoxCallLists.size() > 0) {
            isCallAvailable = false;
            for (int m = 0; m < outBoxCallLists.size(); m++) {
                OutBoxCallList outBoxCallList = outBoxCallLists.get(m);
                if (outBoxCallList.getStatus().equalsIgnoreCase(Constants.WAITING_FOR_SYNC) || outBoxCallList.getStatus().equalsIgnoreCase(Constants.CALL_FAILED)) {
                    if (outBoxCallList.getSyncCount() <= 4) {
                        isCallAvailable = true;
                        Log.v("SendOutboxCall", "---" + outBoxCallList.getCusName());
                        CallSendAPI(outBoxCallList, ParentPos, ChildPos, m, outBoxCallList.getDates(), outBoxCallList.getCusName(), outBoxCallList.getCusCode(), outBoxCallList.getJsonData(), outBoxCallList.getCusType(), outBoxCallList.getSyncCount(), modelClass);
                        break;
                    }
                }
            }
        } else {
            isCallAvailable = false;
        }

        if (!isCallAvailable) {
            if (CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd").equalsIgnoreCase(listDates.get(ParentPos).getGroupName())) {
          //      CallsFragment.CallTodayCallsAPI(context, apiInterface, false);
            }
            CallOfflineImage(ParentPos, 3, listDates.get(ParentPos).getChildItems().get(3).getEcModelClasses(), modelClass);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void CallOfflineImage(int ParentPos, int ChildPos, ArrayList<EcModelClass> ecModelClasses, GroupModelClass modelClass) {
        if (ecModelClasses.size() > 0) {
            isCallAvailable = false;
            for (int i = 0; i < ecModelClasses.size(); i++) {
                EcModelClass ecModelClass = ecModelClasses.get(i);
                Log.v("SendOutboxCall", "--image--" + ecModelClass.getDates() + "---" + ecModelClass.getImg_name());
                if (ecModelClass.getSynced() == 0) {
                    isCallAvailable = true;
                    CallSendAPIImage(ParentPos, ecModelClass, ChildPos, i, ecModelClass.getJson_values(), ecModelClass.getFilePath(), String.valueOf(ecModelClass.getId()), modelClass);
                    break;
                }
            }
        } else {
            isCallAvailable = false;
        }
        if (!isCallAvailable) {
            CallAPIDaySubmit(ParentPos, 4, listDates.get(ParentPos).getChildItems().get(4).getDaySubmitModelClass(), modelClass);
        }
    }

    private void CallAPIDaySubmit(int ParentPos, int ChildPos, DaySubmitModelClass daySubmitModelClass, GroupModelClass modelClass) {
        if(daySubmitModelClass != null) {
            isCallAvailable = false;
            if(daySubmitModelClass.getSyncStatus() == 0) {
                isCallAvailable = true;
                Log.v("SendOutboxCall", "--Day Submit--" + daySubmitModelClass.getDate() + "---" + daySubmitModelClass.getJsonValues());
                if(!daySubmitModelClass.getJsonValues().isEmpty()) {
                    CallSendDaySubmit(ParentPos, daySubmitModelClass, ChildPos, daySubmitModelClass.getJsonValues(), modelClass);
                }
            }
        }else {
            isCallAvailable = false;
        }

        if (!isCallAvailable) {
            if(listDates.get(ParentPos).getChildItems().get(0).getCheckInOutModelClasses().isEmpty()
                    && listDates.get(ParentPos).getChildItems().get(1).getWorkPlanModelClass() == null
                    && listDates.get(ParentPos).getChildItems().get(2).getOutBoxCallLists().isEmpty()
                    && listDates.get(ParentPos).getChildItems().get(3).getEcModelClasses().isEmpty()
                    && listDates.get(ParentPos).getChildItems().get(4).getDaySubmitModelClass() == null) {
                listDates.remove(ParentPos);
            }else {
                modelClass.setSynced(1);
            }
            notifyedmethod();
            sendingOfflineCalls();
        }
    }


    private void CallSendAPIImage(int parentPos, EcModelClass ecModelClass, int childPos, int CurrentPos, String jsonValues, String filePath, String id, GroupModelClass modelClass) {
        ApiInterface apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getTagApiImageUrl(context));
        MultipartBody.Part img = convertImg("EventImg", filePath);
        HashMap<String, RequestBody> values = field(jsonValues);
        Call<JsonObject> saveImgDcr = apiInterface.SaveImg(values, img);
        saveImgDcr.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        JSONObject json = new JSONObject(response.body().toString());
                        if (json.getString("success").equalsIgnoreCase("true") && json.getString("msg").equalsIgnoreCase("Photo Has Been Updated")) {
                            DeleteCacheFile(filePath, id, CurrentPos, parentPos, childPos, modelClass);
                        } else {
                            ecModelClass.setSynced(1);
                            ecModelClass.setSync_status(Constants.DUPLICATE_CALL);
                            callOfflineECDataDao.updateECStatus(id, Constants.DUPLICATE_CALL, 1);
                            CallOfflineImage(parentPos, childPos, listDates.get(parentPos).getChildItems().get(childPos).getEcModelClasses(), modelClass);
                        }
                    } catch (Exception e) {
                        Log.v("SendOutboxCall", "-error---" + e);
                        ecModelClass.setSynced(1);
                        ecModelClass.setSync_status(Constants.EXCEPTION_ERROR);
                        callOfflineECDataDao.updateECStatus(id, Constants.EXCEPTION_ERROR, 1);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                ecModelClass.setSynced(1);
                ecModelClass.setSync_status(Constants.CALL_FAILED);
                callOfflineECDataDao.updateECStatus(id, Constants.CALL_FAILED, 1);
                CallOfflineImage(parentPos, childPos, listDates.get(parentPos).getChildItems().get(childPos).getEcModelClasses(), modelClass);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void DeleteCacheFile(String filePath, String id, int currentPos, int parentPos, int childPos, GroupModelClass modelClass) {
        File fileDelete = new File(filePath);
        if (fileDelete.exists()) {
            if (fileDelete.delete()) {
                System.out.println("file Deleted :" + filePath);
            } else {
                System.out.println("file not Deleted :" + filePath);
            }
        }
        callOfflineECDataDao.deleteOfflineEC(id);
        listDates.get(parentPos).getChildItems().get(childPos).getEcModelClasses().remove(currentPos);
        notifyedmethod();
        CallOfflineImage(parentPos, childPos, listDates.get(parentPos).getChildItems().get(childPos).getEcModelClasses(), modelClass);
    }

    private void CallSendAPI(OutBoxCallList outBoxCallList, int parentPos, int childPos, int CurrentPos, String date, String cusName, String cusCode, String jsonData, String cusType, int syncCount, GroupModelClass modelClass) {
        JSONObject jsonSaveDcr;
        try {
            jsonSaveDcr = new JSONObject(jsonData);
            Map<String, String> mapString = new HashMap<>();
            mapString.put("axn", "save/dcr");
            Call<JsonElement> callSaveDcr = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonSaveDcr.toString());

            callSaveDcr.enqueue(new Callback<JsonElement>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonSaveRes = new JSONObject(String.valueOf(response.body()));
                            if (jsonSaveRes.getString("success").equalsIgnoreCase("true") && jsonSaveRes.getString("msg").isEmpty()) {
                                callsUtil.deleteOfflineCalls(cusCode, cusName, date);
                                listDates.get(parentPos).getChildItems().get(childPos).getOutBoxCallLists().remove(CurrentPos);
                            } else if (jsonSaveRes.getString("success").equalsIgnoreCase("false") && jsonSaveRes.getString("msg").equalsIgnoreCase("Call Already Exists")) {
                                callsUtil.updateOfflineUpdateStatusEC(date, cusCode, 5, Constants.DUPLICATE_CALL, 1);
                                outBoxCallList.setStatus(Constants.DUPLICATE_CALL);
                                outBoxCallList.setSyncCount(5);
                                UpdateEcData(date, cusCode, cusName, Constants.DUPLICATE_CALL, 1);
                                DeleteUpdateDcrTable(date, cusCode, cusType);
                            }
                            CallOfflineCalls(parentPos, childPos, listDates.get(parentPos).getChildItems().get(childPos).getOutBoxCallLists(), modelClass);
                        } catch (Exception e) {
                            callsUtil.updateOfflineUpdateStatusEC(date, cusCode, 5, Constants.EXCEPTION_ERROR, 0);
                            outBoxCallList.setStatus(Constants.EXCEPTION_ERROR);
                            outBoxCallList.setSyncCount(5);
                            UpdateEcData(date, cusCode, cusName, Constants.EXCEPTION_ERROR, 0);
                            CallOfflineCalls(parentPos, childPos, listDates.get(parentPos).getChildItems().get(childPos).getOutBoxCallLists(), modelClass);
                            Log.v("SendOutboxCall", "---" + e);
                        }
                    }
                    notifyedmethod();
                }

                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    callsUtil.updateOfflineUpdateStatusEC(date, cusCode, syncCount + 1, Constants.CALL_FAILED, 1);
                    outBoxCallList.setStatus(Constants.DUPLICATE_CALL);
                    outBoxCallList.setSyncCount(syncCount + 1);
                    UpdateEcData(date, cusCode, cusName, Constants.CALL_FAILED, 1);
                    CallOfflineCalls(parentPos, childPos, listDates.get(parentPos).getChildItems().get(childPos).getOutBoxCallLists(), modelClass);
                    notifyedmethod();
                }
            });

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void UpdateEcData(String date, String cusCode, String cusName, String status, int synced) {
        if (callOfflineECDataDao.isAvailableEc(date, cusCode)) {
            for (int i = 0; i < listDates.size(); i++) {
                if (listDates.get(i).getGroupName().equalsIgnoreCase(date)) {
                    for (int j = 0; j < listDates.get(i).getChildItems().get(3).getEcModelClasses().size(); j++) {
                        EcModelClass ecModelClass = listDates.get(i).getChildItems().get(3).getEcModelClasses().get(j);
                        if (ecModelClass.getDates().equalsIgnoreCase(date) && ecModelClass.getCusCode().equalsIgnoreCase(cusCode) && ecModelClass.getCusName().equalsIgnoreCase(cusName)) {
                            ecModelClass.setSync_status(status);
                            ecModelClass.setSynced(synced);
                            break;
                        }
                    }
                }
            }
        }
    }

    private void DeleteUpdateDcrTable(String date, String cusCode, String cusType) {

        try {
            JSONArray jsonArray = new JSONArray(masterDataDao.getDataByKey(Constants.CALL_SYNC));
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.getString("Dcr_dt").equalsIgnoreCase(date) && jsonObject.getString("CustCode").equalsIgnoreCase(cusCode)) {
                    jsonArray.remove(i);
                    break;
                }
            }

            MasterDataTable mData =new MasterDataTable();
            mData.setMasterKey(Constants.CALL_SYNC);
            mData.setMasterValues(jsonArray.toString());
            mData.setSyncStatus(0);
            MasterDataTable Checked = masterDataDao.getMasterSyncDataByKey(Constants.CALL_SYNC);
            if(Checked !=null){
                masterDataDao.updateData(Constants.CALL_SYNC, jsonArray.toString());
            }else {
                masterDataDao.insert(mData);
            }
            CallDataRestClass.resetcallValues(context);


//            switch (cusType) {
//                case "1":
//                    int doc_current_callcount = sqLite.getcurrentmonth_calls_count("1");
//                    callAnalysisBinding.txtDocCount.setText(String.format("%d / %d", doc_current_callcount, Doctor_list.length()));
//                    break;
//                case "2":
//                    int che_current_callcount = sqLite.getcurrentmonth_calls_count("2");
//                    callAnalysisBinding.txtCheCount.setText(String.format("%d / %d", che_current_callcount, Chemist_list.length()));
//                    break;
//                case "3":
//                    int stockiest_current_callcount = sqLite.getcurrentmonth_calls_count("3");
//                    callAnalysisBinding.txtStockCount.setText(String.format("%d / %d", stockiest_current_callcount, Stockiest_list.length()));
//                    break;
//                case "4":
//                    int unlistered_current_callcount = sqLite.getcurrentmonth_calls_count("4");
//                    callAnalysisBinding.txtUnlistCount.setText(String.format("%d / %d", unlistered_current_callcount, unlistered_list.length()));
//                    break;
//                case "5":
//                    int cip_current_callcount = sqLite.getcurrentmonth_calls_count("5");
//                    callAnalysisBinding.txtCipCount.setText(String.format("%d / %d", cip_current_callcount, cip_list.length()));
//                    break;
//                case "6":
//                    int hos_current_callcount = sqLite.getcurrentmonth_calls_count("6");
//                    callAnalysisBinding.txtHosCount.setText(String.format("%d / %d", hos_current_callcount, hos_list.length()));
//                    break;
//            }
        } catch (Exception ignored) {

        }
    }

    private void CallCheckInOutAPI(int ParentPos, CheckInOutModelClass checkClass, int ChildPos, int CurrentPos, String jsonOutValues, GroupModelClass modelClass) {
        String address = "";
        JSONObject obj;
        try {
            obj = new JSONObject(jsonOutValues);
            address = CommonUtilsMethods.gettingAddress(requireActivity(), Double.parseDouble(obj.getString("lat")), Double.parseDouble(obj.getString("long")), false);
            obj.put("address", address);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "save/activity");
        Call<JsonElement> callCheckInOut = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, obj.toString());
        callCheckInOut.enqueue(new Callback<JsonElement>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                assert response.body() != null;
                Log.v("CheckInOut", response.body() + "--" + response.isSuccessful());
                String CheckInOutStatus = "";
                if (response.isSuccessful()) {
                    try {
                        JSONArray jsonArray = new JSONArray(response.body().toString());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject obj = jsonArray.getJSONObject(i);
                            CheckInOutStatus = obj.getString("msg");
                        }

                        if (CheckInOutStatus.equalsIgnoreCase("1")) {
                            offlineCheckInOutDataDao.deleteOfflineCheckInOut(checkClass.getDates(), checkClass.getCheckCount());
                            listDates.get(ParentPos).getChildItems().get(ChildPos).getCheckInOutModelClasses().remove(CurrentPos);
                        } else {
                            offlineCheckInOutDataDao.updateCheckInOutStatus(checkClass.getId(), 1);
                            checkClass.setCheckStatus(1);
                        }
                        CallCheckInOut(ParentPos, ChildPos, listDates.get(ParentPos).getChildItems().get(ChildPos).getCheckInOutModelClasses(), modelClass);
                        notifyedmethod();
                    } catch (Exception e) {
                        offlineCheckInOutDataDao.deleteOfflineCheckInOut(checkClass.getDates(), checkClass.getCheckCount());
                        listDates.get(ParentPos).getChildItems().get(ChildPos).getCheckInOutModelClasses().remove(CurrentPos);
                        CallCheckInOut(ParentPos, ChildPos, listDates.get(ParentPos).getChildItems().get(ChildPos).getCheckInOutModelClasses(), modelClass);
                        notifyedmethod();
                    }


                } else {
                    checkClass.setCheckStatus(1);
                    CallCheckInOut(ParentPos, ChildPos, listDates.get(ParentPos).getChildItems().get(ChildPos).getCheckInOutModelClasses(), modelClass);
                    notifyedmethod();
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                offlineCheckInOutDataDao.updateCheckInOutStatus(checkClass.getId(), 1);
                checkClass.setCheckStatus(1);
                CallCheckInOut(ParentPos, ChildPos, listDates.get(ParentPos).getChildItems().get(ChildPos).getCheckInOutModelClasses(), modelClass);
                notifyedmethod();
            }
        });
    }

    private void CallSendWorkPlan(int ParentPos, WorkPlanModelClass workPlanModelClass, int ChildPos, String jsonValues, GroupModelClass modelClass) {
        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "edetsave/dayplan");
        Call<JsonElement> saveMyDayPlan = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonValues);
        saveMyDayPlan.enqueue(new Callback<JsonElement>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                Log.v("DayPlan", response.body() + "--" + response.isSuccessful());
                if(response.isSuccessful()) {
                    try {
                        JSONObject json = new JSONObject(Objects.requireNonNull(response.body()).toString());
                        if(json.getString("success").equalsIgnoreCase("true")) {
                            offlineWorkTypeDataDao.delete(workPlanModelClass.getDate());
                            listDates.get(ParentPos).getChildItems().get(ChildPos).setWorkPlanModelClass(null);
                        }else {
                            offlineWorkTypeDataDao.updateWorkTypeStatus(workPlanModelClass.getId(), 1);
                            workPlanModelClass.setSyncStatus(1);
                        }
                        CallAPIWorkPlan(ParentPos, ChildPos, listDates.get(ParentPos).getChildItems().get(ChildPos).getWorkPlanModelClass(), modelClass);
                        notifyedmethod();
                    } catch (Exception ignored) {
                        offlineWorkTypeDataDao.delete(workPlanModelClass.getDate());
                        listDates.get(ParentPos).getChildItems().get(ChildPos).setWorkPlanModelClass(null);
                        CallAPIWorkPlan(ParentPos, ChildPos, listDates.get(ParentPos).getChildItems().get(ChildPos).getWorkPlanModelClass(), modelClass);
                        notifyedmethod();
                    }
                }else {
                    workPlanModelClass.setSyncStatus(1);
                    CallAPIWorkPlan(ParentPos, ChildPos, listDates.get(ParentPos).getChildItems().get(ChildPos).getWorkPlanModelClass(), modelClass);
                    notifyedmethod();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                offlineWorkTypeDataDao.updateWorkTypeStatus(workPlanModelClass.getId(), 1);
                workPlanModelClass.setSyncStatus(1);
                CallAPIWorkPlan(ParentPos, ChildPos, listDates.get(ParentPos).getChildItems().get(ChildPos).getWorkPlanModelClass(), modelClass);
                notifyedmethod();
            }
        });
    }

    private void CallSendDaySubmit(int ParentPos, DaySubmitModelClass daySubmitModelClass, int ChildPos, String jsonValues, GroupModelClass modelClass) {
        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "save/daysubmit");
        Call<JsonElement> callFinalSubmit = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonValues);
        callFinalSubmit.enqueue(new Callback<JsonElement>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                assert response.body() != null;
                Log.v("FinalSubmit", response.body() + "--" + response.isSuccessful());
                if(response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        if(jsonObject.getString("success").equalsIgnoreCase("true")) {
                            offlineDaySubmitDao.delete(daySubmitModelClass.getDate());
                            listDates.get(ParentPos).getChildItems().get(ChildPos).setDaySubmitModelClass(null);
                        }else {
                            offlineDaySubmitDao.updateDaySubmitStatus(daySubmitModelClass.getDate(), 1);
                            daySubmitModelClass.setSyncStatus(1);
                        }
                        CallAPIDaySubmit(ParentPos, ChildPos, listDates.get(ParentPos).getChildItems().get(ChildPos).getDaySubmitModelClass(), modelClass);
                        notifyedmethod();
                    } catch (Exception ignored) {
                        offlineDaySubmitDao.delete(daySubmitModelClass.getDate());
                        offlineDaySubmitDao.updateDaySubmitStatus(daySubmitModelClass.getDate(), 1);
                        CallAPIDaySubmit(ParentPos, ChildPos, listDates.get(ParentPos).getChildItems().get(ChildPos).getDaySubmitModelClass(), modelClass);
                        notifyedmethod();
                    }
                }else {
                    daySubmitModelClass.setSyncStatus(1);
                    CallAPIDaySubmit(ParentPos, ChildPos, listDates.get(ParentPos).getChildItems().get(ChildPos).getDaySubmitModelClass(), modelClass);
                    notifyedmethod();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                offlineDaySubmitDao.updateDaySubmitStatus(daySubmitModelClass.getDate(), 1);
                daySubmitModelClass.setSyncStatus(1);
                CallAPIDaySubmit(ParentPos, ChildPos, listDates.get(ParentPos).getChildItems().get(ChildPos).getDaySubmitModelClass(), modelClass);
            }
        });
    }

   /* private void CallApiList(ArrayList<OutBoxCallList> outBoxCallLists) {
        if (outBoxCallLists.size() > 0) {
            isCallAvailable = false;
            for (int i = 0; i < outBoxCallLists.size(); i++) {
                OutBoxCallList outBoxCallList = outBoxCallLists.get(i);
                if (outBoxCallList.getSyncCount() <= 4) {
                    isCallAvailable = true;
                    Log.v("SendOutboxCall", "----" + outBoxCallList.getCusName());
                    CallSendAPI(outBoxCallLists, i, outBoxCallList.getDates(), outBoxCallList.getCusName(), outBoxCallList.getCusCode(), outBoxCallList.getJsonData(), outBoxCallList.getSyncCount());
                    break;
                }
            }
        } else {
            isCallAvailable = false;
        }

        if (!isCallAvailable) {
            //Call Event Capture
            CallsFragment.CallTodayCallsAPI(requireContext(), apiInterface, sqLite, false);
            ArrayList<EcModelClass> ecModelClasses = sqLite.getEcListFull();
            CallApiLocalEC(ecModelClasses);
        }
    }

    private void CallApiLocalEC(ArrayList<EcModelClass> ecModelClasses) {
        if (ecModelClasses.size() > 0) {
            for (int i = 0; i < ecModelClasses.size(); i++) {
                EcModelClass ecModelClass = ecModelClasses.get(i);
                Log.v("SendOutboxCall", "----" + ecModelClass.getDates() + "---" + ecModelClass.getName());
                CallSendAPIImage(ecModelClasses, i, ecModelClass.getId(), ecModelClass.getJson_values(), ecModelClass.getFilePath());
                break;
            }
        } else {
            SetupOutBoxAdapter(requireActivity(), sqLite, requireContext());
            CallAnalysisFragment.SetcallDetailsInLineChart(sqLite, context);
        }
    }*/

    public HashMap<String, RequestBody> field(String val) {
        HashMap<String, RequestBody> xx = new HashMap<>();
        xx.put("data", createFromString(val));
        return xx;
    }

    private RequestBody createFromString(String txt) {
        return RequestBody.create(txt, MultipartBody.FORM);
    }

    public MultipartBody.Part convertImg(String tag, String path) {
        Log.d("path", tag + "-" + path);
        MultipartBody.Part yy = null;
        try {
            File file;
            if (path.contains(".png") || path.contains(".jpg") || path.contains(".jpeg")) {
                file = new Compressor(context).compressToFile(new File(path));
                Log.d("path", tag + "-" + path);
            } else {
                file = new File(path);
            }
            RequestBody requestBody = RequestBody.create(file, MultipartBody.FORM);
            yy = MultipartBody.Part.createFormData(tag, file.getName(), requestBody);

            Log.d("path", String.valueOf(yy));
        } catch (Exception ignored) {
        }
        return yy;
    }
/*
    private void CallSendAPIImage(ArrayList<EcModelClass> ecModelClasses, int position, String id, String jsonValues, String filePath) {
        ApiInterface apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getTagApiImageUrl(context));
        MultipartBody.Part img = convertImg("EventImg", filePath);
        HashMap<String, RequestBody> values = field(jsonValues);
        Call<JsonObject> saveImgDcr = apiInterface.saveImgDcr(values, img);

        saveImgDcr.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        JSONObject json = new JSONObject(response.body().toString());
                        Log.v("ImgUpload", json.toString());
                        if (json.getString("success").equalsIgnoreCase("true") && json.getString("msg").equalsIgnoreCase("Photo Has Been Updated")) {
                            DeleteCacheFile(ecModelClasses, filePath, id, position);
                        } else {
                            DeleteCacheFile(ecModelClasses, filePath, id, position);
                        }
                    } catch (Exception ignored) {
                        DeleteCacheFile(ecModelClasses, filePath, id, position);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                DeleteCacheFile(ecModelClasses, filePath, id, position);
            }
        });
    }
*/

/*
    private void DeleteCacheFile(ArrayList<EcModelClass> ecModelClasses, String filePath, String id, int position) {
        File fileDelete = new File(filePath);
        if (fileDelete.exists()) {
            if (fileDelete.delete()) {
                System.out.println("file Deleted :" + filePath);
            } else {
                System.out.println("file not Deleted :" + filePath);
            }
        }
        sqLite.deleteOfflineEC(id);
        ecModelClasses.remove(position);
        CallApiLocalEC(ecModelClasses);
    }
*/

/*
    private void CallSendAPI(ArrayList<OutBoxCallList> outBoxCallList, int position, String dates, String cusName, String cusCode, String jsonData, int syncCount) {
        JSONObject jsonSaveDcr;
        try {
            jsonSaveDcr = new JSONObject(jsonData);
            Call<JsonObject> callSaveDcr;
            callSaveDcr = apiInterface.saveDcr(jsonSaveDcr.toString());
            callSaveDcr.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonSaveRes = new JSONObject(String.valueOf(response.body()));
                            if (jsonSaveRes.getString("success").equalsIgnoreCase("true") && jsonSaveRes.getString("msg").isEmpty()) {
                                sqLite.deleteOfflineCalls(cusCode, cusName, dates);
                                outBoxCallList.remove(position);
                            } else if (jsonSaveRes.getString("success").equalsIgnoreCase("false") && jsonSaveRes.getString("msg").equalsIgnoreCase("Call Already Exists")) {
                                sqLite.saveOfflineUpdateStatus(dates, cusCode, String.valueOf(5), Constants.DUPLICATE_CALL);
                                outBoxCallList.set(position, new OutBoxCallList(cusName, cusCode, dates, jsonData, Constants.DUPLICATE_CALL, 5));
                                JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.DCR);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    if (jsonObject.getString("Dcr_dt").equalsIgnoreCase(dates) && jsonObject.getString("CustCode").equalsIgnoreCase(cusCode)) {
                                        jsonArray.remove(i);
                                        break;
                                    }
                                }
                                sqLite.saveMasterSyncData(Constants.DCR, jsonArray.toString(), 0);
                            }

                            CallApiList(outBoxCallList);
                        } catch (Exception e) {
                            outBoxCallList.remove(position);
                            CallApiList(outBoxCallList);
                            Log.v("SendOutboxCall", "---" + e);
                        }
                    }
                }

                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                    sqLite.saveOfflineUpdateStatus(dates, cusCode, String.valueOf(syncCount + 1), Constants.CALL_FAILED);
                    outBoxCallList.set(position, new OutBoxCallList(cusName, cusCode, dates, jsonData, Constants.CALL_FAILED, syncCount + 1));
                    CallApiList(outBoxCallList);
                }
            });

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
*/

    @Override
    public void onResume() {
        super.onResume();

        if(IsFromDCR){
            String status = NetworkUtil.getConnectivityStatusString(requireContext());
            OutboxFragment.NetworkConnectCallHomeDashBoard(status);
            IsFromDCR=false;
        }


//        new Handler().postDelayed(this::refreshPendingFunction, 200);
    }


   public static void notifyedmethod(){
        outBoxHeaderAdapter.notifyDataSetChanged();
       if(listDates.size()>0){
           outBoxBinding.rvOutBoxHead.setVisibility(View.VISIBLE);
           outBoxBinding.outboxEmtyImage.setVisibility(View.GONE);
       }else {
           outBoxBinding.rvOutBoxHead.setVisibility(View.GONE);
           outBoxBinding.outboxEmtyImage.setVisibility(View.VISIBLE);
       }
   }
}