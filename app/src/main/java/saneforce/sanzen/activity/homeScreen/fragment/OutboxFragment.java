package saneforce.sanzen.activity.homeScreen.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
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

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import id.zelory.compressor.Compressor;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.AWS.AWSBuckets;
import saneforce.sanzen.AWS.AWSBucketsSign;
import saneforce.sanzen.AWS.Util;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.activity.homeScreen.adapters.outbox.OutBoxHeaderAdapter;
import saneforce.sanzen.activity.homeScreen.modelClass.ActivityModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.ActivityUploadModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.CheckInOutModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.ChildListModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.DaySubmitModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.EcModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.GroupModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.OutBoxCallList;
import saneforce.sanzen.activity.homeScreen.modelClass.SignModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.WorkPlanModelClass;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.databinding.OutboxFragmentBinding;
import saneforce.sanzen.network.ApiCallback;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.ActivityOfflineTableDetails.ActivityOfflineDataDao;
import saneforce.sanzen.roomdatabase.ActivityUploadTableDetails.ActivityUploadDataDao;
import saneforce.sanzen.roomdatabase.CallDataRestClass;
import saneforce.sanzen.roomdatabase.CallOfflineECTableDetails.CallOfflineECDataDao;
import saneforce.sanzen.roomdatabase.CallOfflineSignTableDetails.CallOfflineSignDataDao;
import saneforce.sanzen.roomdatabase.CallOfflineTableDetails.CallOfflineDataDao;
import saneforce.sanzen.roomdatabase.CallOfflineTableDetails.CallOfflineDataTable;
import saneforce.sanzen.roomdatabase.CallOfflineWorkTypeTableDetails.CallOfflineWorkTypeDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.sanzen.roomdatabase.OfflineCheckInOutTableDetails.OfflineCheckInOutDataDao;
import saneforce.sanzen.roomdatabase.OfflineDaySubmit.OfflineDaySubmitDao;
import saneforce.sanzen.roomdatabase.OutboxUtil;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.NetworkCheckInterface;
import saneforce.sanzen.utility.NetworkUtil;
import saneforce.sanzen.utility.TimeUtils;

public class OutboxFragment extends Fragment {
    private static final String TAG = "Outbox";
    @SuppressLint("StaticFieldLeak")
    public static OutboxFragmentBinding outBoxBinding;
    public static ArrayList<GroupModelClass> listDates = new ArrayList<>();
    public static boolean IsFromDCR = false;
    @SuppressLint("StaticFieldLeak")
    public static OutBoxHeaderAdapter outBoxHeaderAdapter;
    static NetworkCheckInterface mCheckNetwork;
    ApiInterface apiInterface;
    boolean isCallAvailable;
    CommonUtilsMethods commonUtilsMethods;
    private static Context context;
    private RoomDB db;
    private static MasterDataDao masterDataDao;
    private OfflineCheckInOutDataDao offlineCheckInOutDataDao;
    private CallOfflineDataDao callOfflineDataDao;
    private CallOfflineECDataDao callOfflineECDataDao;
    private CallOfflineSignDataDao callOfflineSignDataDao;
    private CallOfflineWorkTypeDataDao offlineWorkTypeDataDao;
    private OfflineDaySubmitDao offlineDaySubmitDao;
    private ActivityOfflineDataDao activityOfflineDataDao;
    private ActivityUploadDataDao activityUploadDataDao;
    private static OutboxUtil outboxUtil;
    private int callSyncCount = 0;

    Util util;
    ProgressDialog progressDialog = null;

    private Bitmap Bitmap;

    public static void NetworkConnectCallHomeDashBoard(String log) {
        Log.e("NOT_CONNECT", "" + log);
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
        listDates = outboxUtil.getOutBoxDatesWithData(context);
        outBoxHeaderAdapter = new OutBoxHeaderAdapter(activity, context, listDates);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        OutboxFragment.context = context;
        outBoxBinding.rvOutBoxHead.setLayoutManager(mLayoutManager);
        outBoxBinding.rvOutBoxHead.setAdapter(outBoxHeaderAdapter);
        notifyedmethod();
//        if(callsUtil.getOutboxDates().isEmpty()) {
//            masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.DATE_SYNC_DUP, "[]", 0));
//        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.v("fragment", "OutBox");
        outBoxBinding = OutboxFragmentBinding.inflate(inflater, container, false);
        View view = outBoxBinding.getRoot();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        db = RoomDB.getDatabase(requireContext());
        masterDataDao = db.masterDataDao();
        offlineCheckInOutDataDao = db.offlineCheckInOutDataDao();
        callOfflineDataDao = db.callOfflineDataDao();
        callOfflineECDataDao = db.callOfflineECDataDao();
        callOfflineSignDataDao = db.callOfflineSignDataDao();
        offlineWorkTypeDataDao = db.callOfflineWorkTypeDataDao();
        offlineDaySubmitDao = db.offlineDaySubmitDao();
        activityOfflineDataDao = db.activityOfflineDataDao();
        activityUploadDataDao = db.activityUploadDataDao();
        outboxUtil = new OutboxUtil(requireContext());
        util = new Util();
        callSyncCount = 0;
        SetupOutBoxAdapter(requireActivity(), requireContext());

        new Handler().postDelayed(this::refreshPendingFunction, 200);

        outBoxBinding.clearAll.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (offlineDaySubmitDao.isNonSyncAvailableDaySubmit()) {
                    //CommonUtilsMethods.showToastMessage(requireActivity(), "Cannot clear when day submitted");
                    CommonUtilsMethods.showToastMessage(requireActivity(),getString(R.string.cannot_clear_when_day_submitted));
                } else {
                    if (!listDates.isEmpty()) {
                        Set<String> dates = outboxUtil.getOutboxDates();
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
                        btn_yes.setOnClickListener(new SafeClickListener() {
                            @Override
                            public void onSafeClick(View view) {
                                addDateSyncDataBack(dates);
                                clearCalls();
                                HomeDashBoard.checkAndSetEntryDate(requireContext(), true);
                                dialog.dismiss();
                            }
                        });
                        btn_no.setOnClickListener(new SafeClickListener() {
                            @Override
                            public void onSafeClick(View view) {
                                dialog.dismiss();
                            }
                        });
                    }
                }
            }
        });

        return view;
    }

    private void addDateSyncDataBack(Set<String> outboxDates) {
        try {
            JSONArray dateSyncArray = masterDataDao.getMasterDataTableOrNew(Constants.DATE_SYNC).getMasterSyncDataJsonArray();
            JSONArray dateSyncDupArray = masterDataDao.getMasterDataTableOrNew(Constants.DATE_SYNC_DUP).getMasterSyncDataJsonArray();
            Set<JSONObject> dateSyncObjects = new HashSet<>();

            for (int index = 0; index < dateSyncDupArray.length(); index++) {
                JSONObject jsonObject = dateSyncDupArray.getJSONObject(index);
                String date = jsonObject.getJSONObject("dt").getString("date").substring(0, 10);
                if (outboxDates.contains(date)) {
                    dateSyncObjects.add(jsonObject);
                }
            }

            for (int i = 0; i < dateSyncArray.length(); i++) {
                dateSyncObjects.add(dateSyncArray.getJSONObject(i));
            }

            dateSyncArray = new JSONArray();
            for (JSONObject jsonObject : dateSyncObjects) {
                dateSyncArray.put(jsonObject);
            }

            masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.DATE_SYNC, dateSyncArray.toString(), 0));
//            masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.CALL_SYNC, masterDataDao.getMasterDataTableOrNew(Constants.CALL_SYNC_DUP).getMasterSyncDataJsonArray().toString(), 0));
            if (HomeDashBoard.binding.textDate.getText().toString() == null || HomeDashBoard.binding.textDate.getText().toString().isEmpty()) {
                masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.WORK_PLAN, "[]", 0));
            }
        } catch (Exception e) {
            Log.e("Outbox clear calls", "addDateSyncDataBack: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void clearCalls() {
        ArrayList<OutBoxCallList> outBoxCallLists = outboxUtil.getAllOutBoxCallsList();
        Set<String> dates = outboxUtil.getOutboxDates();
        try {
            if (HomeDashBoard.selectedDate != null && offlineWorkTypeDataDao.getAllCallOfflineWTDates().contains(HomeDashBoard.selectedDate.toString())) {
                Log.e("outbox workplan", "clearCalls: date found");
                masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.WORK_PLAN, "[]", 0));
                SharedPref.setDayPlanStartedDate(requireContext(), "");
                if (!SharedPref.getDcrSequential(requireContext()).equals("0")) {
                    SharedPref.setSelectedDateCal(requireContext(), "");
                }
            }
            if (!dates.isEmpty()) {
                JSONArray jsonArray = new JSONArray(masterDataDao.getDataByKey(Constants.CALL_SYNC));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (dates.contains(jsonObject.getString("Dcr_dt"))) {
                        jsonArray.remove(i);
                        i--;
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
                CallDataRestClass.resetcallValues(requireContext());

                if (HomeDashBoard.selectedDate != null) {
                    JSONArray jsonArrayCalls = new JSONArray(SharedPref.getTodayCallList(requireContext()));
                    boolean callsAvailable = false;
                    for (int i = 0; i < jsonArrayCalls.length(); i++) {
                        JSONObject json = jsonArrayCalls.getJSONObject(i);
                        if (TimeUtils.GetConvertedDate(TimeUtils.FORMAT_1, TimeUtils.FORMAT_4, json.getString("vstTime")).equalsIgnoreCase(HomeDashBoard.selectedDate.toString())) {
                            callsAvailable = true;
                        }
                    }
                    if (!callsAvailable) {
                        SharedPref.setLastCallDate(requireContext(), "");
                    } else {
                        SharedPref.setLastCallDate(requireContext(), HomeDashBoard.selectedDate.toString());
                    }
                }
            }
        } catch (Exception e) {
            Log.e("Outbox", "clearCalls: " + e.getMessage());
            e.printStackTrace();
        }

        if (SharedPref.getSrtNd(requireContext()).equalsIgnoreCase("0")) {
            if (offlineCheckInOutDataDao.getCheckInOutCount(CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd")) > 0) {
                SharedPref.setCheckInTime(requireContext(), "");
                SharedPref.setCheckDateTodayPlan(requireContext(), "");
            }
        }

        try {
            List<CallOfflineDataTable> callOfflineDataTables = callOfflineDataDao.getAllOutBoxCallList();
            for (CallOfflineDataTable callOfflineDataTable : callOfflineDataTables) {
                String jsonArray = callOfflineDataTable.getCallJsonValues();
                UpdateInputSample(jsonArray);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        outboxUtil.deleteOfflineCalls();
        listDates.clear();
        outBoxHeaderAdapter = new OutBoxHeaderAdapter(requireActivity(), requireContext(), listDates);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(requireContext());
        outBoxBinding.rvOutBoxHead.setLayoutManager(mLayoutManager);
        outBoxBinding.rvOutBoxHead.setAdapter(outBoxHeaderAdapter);
    }

    private void refreshPendingFunction() {
        Log.e("RefreshStatus", "Is Working");
        SendOfflineData(this::sendingOfflineCalls);
    }

    private void UpdateInputSample(String jsonArray) {
        try {
            JSONObject json = new JSONObject(jsonArray);
            //Input
            if (SharedPref.getInputValidation(context).equalsIgnoreCase("1")) {
                JSONArray jsonArrayInpStk = masterDataDao.getMasterDataTableOrNew(Constants.INPUT_BALANCE).getMasterSyncDataJsonArray();
                JSONArray jsonInput = json.getJSONArray("Inputs");
                Log.v("input_wrk", String.valueOf(jsonInput));
                if (jsonInput.length() > 0) {
                    for (int i = 0; i < jsonInput.length(); i++) {
                        JSONObject jsIp = jsonInput.getJSONObject(i);
                        //InputStockChange
                        for (int j = 0; j < jsonArrayInpStk.length(); j++) {
                            JSONObject jsonObject = jsonArrayInpStk.getJSONObject(j);
                            Log.v("chkInpStk", jsIp.getString("Code") + "-----" + jsonObject.getString("Code"));
                            if (jsIp.getString("Code").equalsIgnoreCase(jsonObject.getString("Code"))) {
                                int EnterQty = Integer.parseInt(jsIp.getString("IQty"));
                                int BalanceStock = Integer.parseInt(jsonObject.getString("Balance_Stock"));
                                int FinalStock = EnterQty + BalanceStock;
                                jsonObject.remove("Balance_Stock");
                                jsonObject.put("Balance_Stock", FinalStock);
                                break;
                            }
                        }
                    }
                    masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.INPUT_BALANCE, jsonArrayInpStk.toString(), 2));
                }
            }

            //Sample
            if (SharedPref.getSampleValidation(context).equalsIgnoreCase("1")) {
                JSONArray jsonArraySamStk = masterDataDao.getMasterDataTableOrNew(Constants.STOCK_BALANCE).getMasterSyncDataJsonArray();
                JSONArray jsonPrdArray = new JSONArray(json.getString("Products"));
                Log.v("sample_wrk", String.valueOf(jsonPrdArray));
                if (jsonPrdArray.length() > 0) {
                    //InputStockChange
                    for (int i = 0; i < jsonPrdArray.length(); i++) {
                        JSONObject js = jsonPrdArray.getJSONObject(i);
                        if (js.getString("Group").equalsIgnoreCase("0")) {
                            for (int j = 0; j < jsonArraySamStk.length(); j++) {
                                JSONObject jsonObject = jsonArraySamStk.getJSONObject(j);
                                if (js.getString("Code").equalsIgnoreCase(jsonObject.getString("Code"))) {
                                    int EnterQty = Integer.parseInt(js.getString("SmpQty"));
                                    int BalanceStock = Integer.parseInt(jsonObject.getString("Balance_Stock"));
                                    int FinalStock = EnterQty + BalanceStock;
                                    jsonObject.remove("Balance_Stock");
                                    jsonObject.put("Balance_Stock", FinalStock);
                                    break;
                                }
                            }
                        }
                    }
                    masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.STOCK_BALANCE, jsonArraySamStk.toString(), 0));
                }
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendingOfflineCalls() {
        apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));

        if (callSyncCount > 0) {
            CallsFragment.syncCalls();
            callSyncCount = 0;
        }

        Log.d(TAG, "sendingOfflineCalls: " + listDates.size());
        processDatesSequentially(listDates, 0);

//        //CheckInOutData
//        if (!listDates.isEmpty()) {
//            isCallAvailable = false;
//            for (int i = 0; i < listDates.size(); i++) {
//                GroupModelClass modelClass = listDates.get(i);
//                if (listDates.get(i).getSynced() == 0) {
//                    isCallAvailable = true;
//                    CallCheckInOut(i, 0, listDates.get(i).getChildItems().get(0).getCheckInOutModelClasses(), modelClass);
//                    break;
//                }
//            }
//        } else {
//            isCallAvailable = false;
//        }
//
//        if (!isCallAvailable && !requireActivity().isFinishing()) {
//            SetupOutBoxAdapter(requireActivity(), requireContext());
//        }
    }

    private void processDatesSequentially(ArrayList<GroupModelClass> datesList, int dateIndex) {
        if (dateIndex >= datesList.size()) {
            if (callSyncCount > 0) {
                CallsFragment.syncCalls();
                callSyncCount = 0;
            }
            SetupOutBoxAdapter(requireActivity(), requireContext());
            return;
        }

        GroupModelClass dateGroup = datesList.get(dateIndex);
        Log.d(TAG, "processDatesSequentially: " + dateIndex + " -> " + dateGroup.getGroupName());
        processApisForDate(dateGroup, 0, new ApiCallback() {
            @Override
            public void onSuccess() {
                processDatesSequentially(datesList, dateIndex + 1);
            }

            @Override
            public void onFailure() {
                stopSync();
            }
        });
    }

    private void stopSync() {
        System.out.println("❌ Sync stopped due to API failure.");
        if (callSyncCount > 0) {
            CallsFragment.syncCalls();
            callSyncCount = 0;
        }
        SetupOutBoxAdapter(requireActivity(), requireContext());
    }

    private void processApisForDate(GroupModelClass dateGroup, int apiIndex, ApiCallback callback) {
        if (apiIndex >= dateGroup.getChildItems().size()) {
            callback.onSuccess();
            return;
        }

        ChildListModelClass child = dateGroup.getChildItems().get(apiIndex);

        callApiForChild(child, new ApiCallback() {
            @Override
            public void onSuccess() {
                if (apiIndex == 1 && child.getWorkPlanModelClass() != null && child.getWorkPlanModelClass().getWtStatus() != null && !child.getWorkPlanModelClass().getWtStatus().isEmpty()) {
                    processApisForDate(dateGroup, dateGroup.getChildItems().size(), callback);
                } else {
                    processApisForDate(dateGroup, apiIndex + 1, callback);
                }
            }

            @Override
            public void onFailure() {
//                if (child.getChildId() == 3 || child.getChildId() == 4 || child.getChildId() == 6) {
//                    processApisForDate(dateGroup, apiIndex + 1, callback);
//                } else {
                callback.onFailure();
//                }
            }
        });
    }

    private void callApiForChild(ChildListModelClass child, ApiCallback callback) {
        switch (child.getChildId()) {
            case 0:
                checkInSubmitAPI(child, 0, 0, callback);
                break;

            case 1:
                workPlanSubmitAPI(child, 0, callback);
                break;

            case 2:
                callSubmitAPI(child, 0, 0, callback);
                break;

            case 3:
                eventCaptureSubmitAPI(child, 0, 0, callback);
                break;

            case 4:
                signatureSubmitAPI(child, 0, 0, callback);
                break;

            case 5:
                activitySubmitAPI(child, 0, 0, callback);
                break;

            case 6:
                activityUploadSubmitAPI(child, 0, 0, callback);
                break;

            case 7:
                daySubmitAPI(child, 0, callback);
                break;

            default:
                callback.onSuccess();
                break;
        }
    }

    private void checkInSubmitAPI(ChildListModelClass child, int index, int attempt, ApiCallback callback) {
        String address = "";
        ArrayList<CheckInOutModelClass> checkInOutModelClasses = child.getCheckInOutModelClasses();
        if (checkInOutModelClasses == null || checkInOutModelClasses.isEmpty() || index >= checkInOutModelClasses.size()) {
            callback.onSuccess();
            return;
        }
        JSONObject obj = new JSONObject();
        CheckInOutModelClass checkInOutModelClass = checkInOutModelClasses.get(index);
        try {
            if (checkInOutModelClass.getJsonOutValues().isEmpty()) {
                obj = new JSONObject(checkInOutModelClass.getJsonInValues());
            } else {
                obj = new JSONObject(checkInOutModelClass.getJsonOutValues());
            }
            address = CommonUtilsMethods.gettingAddress(requireActivity(), Double.parseDouble(obj.getString("lat")), Double.parseDouble(obj.getString("long")), false);
            obj.put("address", address);
        } catch (JSONException e) {
            e.printStackTrace();
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
                            offlineCheckInOutDataDao.deleteOfflineCheckInOut(checkInOutModelClass.getDates(), checkInOutModelClass.getCheckCount());
                            checkInOutModelClasses.remove(checkInOutModelClass);
                            //notifyedmethod();
                            checkInSubmitAPI(child, index, 0, callback);
                        } else {
                            offlineCheckInOutDataDao.updateCheckInOutStatus(checkInOutModelClass.getId(), 1);
                            checkInOutModelClass.setCheckStatus(1);
                            //notifyedmethod();
                            callback.onFailure();
                        }
                    } catch (Exception e) {
                        offlineCheckInOutDataDao.deleteOfflineCheckInOut(checkInOutModelClass.getDates(), checkInOutModelClass.getCheckCount());
                        checkInOutModelClasses.remove(checkInOutModelClass);
                        //notifyedmethod();
                        callback.onFailure();
                    }


                } else {
                    checkInOutModelClass.setCheckStatus(1);
                    //notifyedmethod();
                    callback.onFailure();
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                offlineCheckInOutDataDao.updateCheckInOutStatus(checkInOutModelClass.getId(), 1);
                checkInOutModelClass.setCheckStatus(1);
                //notifyedmethod();
                if (attempt != 5) {
                    checkInSubmitAPI(child, index, attempt + 1, callback);
                } else {
                    callback.onFailure();
                }
            }
        });
    }

    private void workPlanSubmitAPI(ChildListModelClass child, int attempt, ApiCallback callback) {
        WorkPlanModelClass workPlanModelClass = child.getWorkPlanModelClass();
        if (workPlanModelClass == null || workPlanModelClass.getSyncStatus() != 0) {
            callback.onSuccess();
            return;
        }
        Map<String, String> mapString = new HashMap<>();
        if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("1") || SharedPref.getOneBuild(requireContext()).equalsIgnoreCase("0")) {
            mapString.put("axn", "edetsave/dayplan");
        } else {
            mapString.put("axn", "multihqsave/dayplan");
        }
        Call<JsonElement> saveMyDayPlan = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, workPlanModelClass.getJsonValues());
        saveMyDayPlan.enqueue(new Callback<JsonElement>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                Log.v("DayPlan", response.body() + "--" + response.isSuccessful());
                if (response.isSuccessful()) {
                    try {
                        JSONObject json = new JSONObject(Objects.requireNonNull(response.body()).toString());
                        if (json.getString("success").equalsIgnoreCase("true")) {
                            offlineWorkTypeDataDao.delete(workPlanModelClass.getDate());
                            child.setWorkPlanModelClass(null);
                            //notifyedmethod();
                            callback.onSuccess();
                        } else {
                            if (json.optBoolean("update")) {
                                String msg = json.optString("Msg");
                                offlineWorkTypeDataDao.updateWorkTypeStatus(workPlanModelClass.getId(), msg, 2);
                                workPlanModelClass.setSyncStatus(2);
                                workPlanModelClass.setWtStatus(msg);
                                //notifyedmethod();
                                callback.onSuccess();
                            } else {
                                offlineWorkTypeDataDao.updateWorkTypeStatus(workPlanModelClass.getId(), 1);
                                workPlanModelClass.setSyncStatus(1);
                                //notifyedmethod();
                                callback.onFailure();
                            }
                        }
                    } catch (Exception ignored) {
                        offlineWorkTypeDataDao.updateWorkTypeStatus(workPlanModelClass.getId(), 1);
                        workPlanModelClass.setSyncStatus(1);
                        //notifyedmethod();
                        callback.onFailure();
                    }
                } else {
                    offlineWorkTypeDataDao.updateWorkTypeStatus(workPlanModelClass.getId(), 1);
                    workPlanModelClass.setSyncStatus(1);
                    //notifyedmethod();
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                offlineWorkTypeDataDao.updateWorkTypeStatus(workPlanModelClass.getId(), 1);
                workPlanModelClass.setSyncStatus(1);
                //notifyedmethod();
                t.printStackTrace();
                if (attempt != 5) {
                    workPlanSubmitAPI(child, attempt + 1, callback);
                } else {
                    callback.onFailure();
                }
            }
        });
    }

    private void callSubmitAPI(ChildListModelClass child, int index, int attempt, ApiCallback callback) {
        ArrayList<OutBoxCallList> callsList = child.getOutBoxCallLists();
        if (callsList == null || callsList.isEmpty() || index >= callsList.size()) {
            callback.onSuccess();
            return;
        }
        OutBoxCallList outBoxCallList = callsList.get(index);

        if (outBoxCallList.getStatus().equalsIgnoreCase(Constants.WAITING_FOR_SYNC) || outBoxCallList.getStatus().equalsIgnoreCase(Constants.CALL_FAILED)) {
            Map<String, String> mapString = new HashMap<>();
            mapString.put("axn", "save/dcr");
            Call<JsonElement> callSaveDcr = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, outBoxCallList.getJsonData());

            callSaveDcr.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonSaveRes = new JSONObject(String.valueOf(response.body()));
                            if (jsonSaveRes.getString("success").equalsIgnoreCase("true") && jsonSaveRes.getString("msg").isEmpty()) {
                                outboxUtil.updateOfflineUpdateStatusEC(outBoxCallList.getDates(), outBoxCallList.getCusCode(), 0, Constants.WAITING_FOR_SYNC, 0);
                                outboxUtil.deleteOfflineCalls(outBoxCallList.getCusCode(), outBoxCallList.getCusName(), outBoxCallList.getDates());
                                callsList.remove(outBoxCallList);
                                //notifyedmethod();
                                callSyncCount++;
                                callSubmitAPI(child, index, 0, callback);
                            } else if (jsonSaveRes.getString("success").equalsIgnoreCase("false") && jsonSaveRes.getString("msg").equalsIgnoreCase("Call Already Exists")) {
                                outboxUtil.updateOfflineUpdateStatusEC(outBoxCallList.getDates(), outBoxCallList.getCusCode(), 5, Constants.DUPLICATE_CALL, 1);
                                outBoxCallList.setStatus(Constants.DUPLICATE_CALL);
                                outBoxCallList.setSyncCount(5);
                                UpdateEcData(outBoxCallList.getDates(), outBoxCallList.getCusCode(), outBoxCallList.getCusName(), Constants.DUPLICATE_CALL, 1);
                                DeleteUpdateDcrTable(outBoxCallList.getDates(), outBoxCallList.getCusCode(), outBoxCallList.getCusType());
                                //notifyedmethod();
                                callback.onSuccess();
                            } else if (jsonSaveRes.getString("success").equalsIgnoreCase("false")) {
                                if (jsonSaveRes.has("msg")) {
                                    outboxUtil.updateOfflineUpdateStatusEC(outBoxCallList.getDates(), outBoxCallList.getCusCode(), 5, jsonSaveRes.getString("msg"), 1);
                                    outBoxCallList.setStatus(jsonSaveRes.getString("msg"));
                                    outBoxCallList.setSyncCount(5);
                                    UpdateEcData(outBoxCallList.getDates(), outBoxCallList.getCusCode(), outBoxCallList.getCusName(), jsonSaveRes.getString("msg"), 1);
                                    DeleteUpdateDcrTable(outBoxCallList.getDates(), outBoxCallList.getCusCode(), outBoxCallList.getCusType());
                                    //notifyedmethod();
                                    callback.onFailure();
                                } else if (jsonSaveRes.has("Msg")) {
                                    outboxUtil.updateOfflineUpdateStatusEC(outBoxCallList.getDates(), outBoxCallList.getCusCode(), 5, jsonSaveRes.getString("Msg"), 1);
                                    outBoxCallList.setStatus(jsonSaveRes.getString("Msg"));
                                    outBoxCallList.setSyncCount(5);
                                    UpdateEcData(outBoxCallList.getDates(), outBoxCallList.getCusCode(), outBoxCallList.getCusName(), jsonSaveRes.getString("Msg"), 1);
                                    DeleteUpdateDcrTable(outBoxCallList.getDates(), outBoxCallList.getCusCode(), outBoxCallList.getCusType());
                                    //notifyedmethod();
                                    callback.onFailure();
                                }
                            }
                        } catch (Exception e) {
                            outboxUtil.updateOfflineUpdateStatusEC(outBoxCallList.getDates(), outBoxCallList.getCusCode(), 5, Constants.EXCEPTION_ERROR, 0);
                            outBoxCallList.setStatus(Constants.EXCEPTION_ERROR);
                            outBoxCallList.setSyncCount(5);
                            UpdateEcData(outBoxCallList.getDates(), outBoxCallList.getCusCode(), outBoxCallList.getCusName(), Constants.EXCEPTION_ERROR, 0);
                            e.printStackTrace();
                            //notifyedmethod();
                            callback.onFailure();
                        }
                    }
                }

                @Override
                public void onFailure(Call<JsonElement> call, Throwable throwable) {
                    Log.v("CallsResponse", "" + throwable.getMessage().toString());
                    outboxUtil.updateOfflineUpdateStatusEC(outBoxCallList.getDates(), outBoxCallList.getCusCode(), attempt + 1, Constants.CALL_FAILED, 1);
                    outBoxCallList.setStatus(Constants.CALL_FAILED);
                    outBoxCallList.setSyncCount(attempt + 1);
                    UpdateEcData(outBoxCallList.getDates(), outBoxCallList.getCusCode(), outBoxCallList.getCusName(), Constants.CALL_FAILED, 1);
                    //notifyedmethod();
                    if (attempt != 5) {
                        callSubmitAPI(child, index, attempt + 1, callback);
                    } else {
                        callback.onFailure();
                    }
                }
            });
        } else {
            callSubmitAPI(child, index + 1, 0, callback);
        }
    }

    private void eventCaptureSubmitAPI(ChildListModelClass child, int index, int attempt, ApiCallback callback) {
        ArrayList<EcModelClass> callsImageList = child.getEcModelClasses();
        if (callsImageList == null || callsImageList.isEmpty() || index >= callsImageList.size()) {
            callback.onSuccess();
            return;
        }
        EcModelClass ecModelClass = callsImageList.get(index);
        if (ecModelClass.getSynced() == 0) {
            if (SharedPref.getS3BucketNeed(context).equalsIgnoreCase("0")) {
                CallSendAPIImageS3(child, index, attempt, ecModelClass, callback);
            } else {
                CallSendAPIImage(child, index, attempt, ecModelClass, callback);
            }
        } else {
            eventCaptureSubmitAPI(child, index + 1, 0, callback);
        }
    }

    private void CallSendAPIImageS3(ChildListModelClass child, int index, int attempt, EcModelClass ecModelClass, ApiCallback callback) {
        try {
            util.getS3Client(context);
            String bucketName = "san-one";
            File fileToUpload = new File(ecModelClass.getFilePath());
            Log.d("fileToUpload", "CallImageAPI: " + fileToUpload.getAbsolutePath());
            if (!fileToUpload.exists()) {
                Log.d("fileToUpload", "not exists: " + ecModelClass.getFilePath());
                callback.onSuccess();
            } else {
//                String s3Key = SharedPref.getDivisionCode(context).replace(",", "/") + "Event_Capture" + "/" + fileToUpload.getName();
                String s3Key = "uploads/" + SharedPref.getDivisionSname(context) + SharedPref.getDivisionCode(context).replace(",", "/") + "event_capture" + "/" + fileToUpload.getName();
                Log.d("TAG", "CallSendAPIImage: " + s3Key);

                TransferNetworkLossHandler.getInstance(context);

                TransferUtility transferUtility = TransferUtility.builder()
                        .context(context)
                        .s3Client(util.getS3Client(context))
                        .build();

                TransferObserver uploadObserver = transferUtility.upload(
                        bucketName,
                        s3Key,
                        fileToUpload);
                Log.d("uploadObserver", "CallSendAPIImage: " + uploadObserver);
                uploadObserver.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int idInt, TransferState state) {
                        if (state == TransferState.COMPLETED) {
                            Log.d("TAG", "ecModelClass: " + ecModelClass.getFilePath());
                            InsertImage(ecModelClass.getFilePath(), context);
                            try {
                                File fileDelete = new File(ecModelClass.getFilePath());
                                if (fileDelete.exists()) {
                                    if (fileDelete.delete()) {
                                        Log.i("file Deleted :", "onResponse: " + ecModelClass.getFilePath());
                                    } else {
                                        Log.e("file Deleted :", "onResponse: " + ecModelClass.getFilePath());
                                    }
                                }
                                callOfflineECDataDao.deleteOfflineEC(String.valueOf(ecModelClass.getId()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            child.getEcModelClasses().remove(ecModelClass);
                            //notifyedmethod();
                            eventCaptureSubmitAPI(child, index, 0, callback);
                        } else if (state == TransferState.FAILED) {
                            Log.e("S3 Upload", "Upload Failed");
                            InsertImage(ecModelClass.getFilePath(), context);
                            ecModelClass.setSynced(1);
                            ecModelClass.setSync_status(Constants.CALL_FAILED);
                            callOfflineECDataDao.updateECStatus(String.valueOf(ecModelClass.getId()), Constants.CALL_FAILED, 1);
                            //notifyedmethod();
                            callback.onFailure();
                        }

                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        double progress = (bytesCurrent * 100.0) / bytesTotal;
                        Log.d("S3 Upload", "Upload Progress: " + progress + "%");
                    }

                    @Override
                    public void onError(int idInt, Exception ex) {
                        Log.e("S3 Upload", "Error: " + ex.getMessage());
                        ecModelClass.setSynced(1);
                        ecModelClass.setSync_status(Constants.EXCEPTION_ERROR);
                        callOfflineECDataDao.updateECStatus(String.valueOf(ecModelClass.getId()), Constants.EXCEPTION_ERROR, 1);
                        //notifyedmethod();
                        if (attempt != 5) {
                            CallSendAPIImageS3(child, index, attempt + 1, ecModelClass, callback);
                        } else {
                            callback.onFailure();
                        }
                    }
                });

            }
        } catch (Exception e) {
            Log.v("img_tag", e.toString());
            ecModelClass.setSynced(1);
            ecModelClass.setSync_status(Constants.EXCEPTION_ERROR);
            callOfflineECDataDao.updateECStatus(String.valueOf(ecModelClass.getId()), Constants.EXCEPTION_ERROR, 1);
            //notifyedmethod();
            callback.onFailure();
        }
    }

    private void CallSendAPIImage(ChildListModelClass child, int index, int attempt, EcModelClass ecModelClass, ApiCallback callback) {
        ApiInterface apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getTagApiImageUrl(context));
        MultipartBody.Part img = convertImg("EventImg", ecModelClass.getFilePath());
        HashMap<String, RequestBody> values = field(ecModelClass.getJson_values());
        Call<JsonObject> saveImgDcr = apiInterface.SaveImg(values, img);
        saveImgDcr.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        JSONObject json = new JSONObject(response.body().toString());
                        if (json.getString("success").equalsIgnoreCase("true") && json.getString("msg").equalsIgnoreCase("Photo Has Been Updated")) {
                            try {
                                File fileDelete = new File(ecModelClass.getFilePath());
                                if (fileDelete.exists()) {
                                    if (fileDelete.delete()) {
                                        Log.i("file Deleted :", "onResponse: " + ecModelClass.getFilePath());
                                    } else {
                                        Log.e("file Deleted :", "onResponse: " + ecModelClass.getFilePath());
                                    }
                                }
                                callOfflineECDataDao.deleteOfflineEC(String.valueOf(ecModelClass.getId()));
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            child.getEcModelClasses().remove(ecModelClass);
                            //notifyedmethod();
                            eventCaptureSubmitAPI(child, index, 0, callback);
                        } else {
                            ecModelClass.setSynced(1);
                            ecModelClass.setSync_status(Constants.DUPLICATE_CALL);
                            callOfflineECDataDao.updateECStatus(String.valueOf(ecModelClass.getId()), Constants.DUPLICATE_CALL, 1);
                            //notifyedmethod();
                            callback.onSuccess();
                        }
                    } catch (Exception e) {
                        Log.v("SendOutboxCall", "-error---" + e);
                        ecModelClass.setSynced(1);
                        ecModelClass.setSync_status(Constants.EXCEPTION_ERROR);
                        callOfflineECDataDao.updateECStatus(String.valueOf(ecModelClass.getId()), Constants.EXCEPTION_ERROR, 1);
                        //notifyedmethod();
                        callback.onFailure();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                ecModelClass.setSynced(1);
                ecModelClass.setSync_status(Constants.CALL_FAILED);
                callOfflineECDataDao.updateECStatus(String.valueOf(ecModelClass.getId()), Constants.CALL_FAILED, 1);
                //notifyedmethod();
                if (attempt != 5) {
                    CallSendAPIImage(child, index, attempt + 1, ecModelClass, callback);
                } else {
                    callback.onFailure();
                }
            }
        });
    }

    private void signatureSubmitAPI(ChildListModelClass child, int index, int attempt, ApiCallback callback) {
        ArrayList<SignModelClass> callsSignList = child.getSignModelClasses();
        if (callsSignList == null || callsSignList.isEmpty() || index >= callsSignList.size()) {
            callback.onSuccess();
            return;
        }
        SignModelClass signModelClass = callsSignList.get(index);

        if (signModelClass.getSynced() == 0) {
            if (SharedPref.getS3BucketNeed(context).equalsIgnoreCase("0")) {
                CallSendAPIImageS3(child, index, attempt, signModelClass, callback);
            } else {
                CallSendAPISignImage(child, index, attempt, signModelClass, callback);
            }
        } else {
            signatureSubmitAPI(child, index + 1, 0, callback);
        }
    }

    private void CallSendAPIImageS3(ChildListModelClass child, int index, int attempt, SignModelClass signModelClass, ApiCallback callback) {
        try {
            util.getS3Client(context);
            String bucketName = "san-one";
            if (!signModelClass.getFilePath().isEmpty()) {
                File fileToUpload = new File(signModelClass.getFilePath());
                Log.d("fileToUpload", "CallImageAPI: " + fileToUpload.getAbsolutePath());
                if (fileToUpload.toString().isEmpty()) {
                    Log.d("fileToUploadSignObFrag", "not exists: " + signModelClass.getFilePath());
                } else {
//                    String s3Key = SharedPref.getDivisionCode(context).replace(",", "/") + "Signature" + "/" + fileToUpload.getName();
                    String s3Key = "uploads/" + SharedPref.getDivisionSname(context) + SharedPref.getDivisionCode(context).replace(",", "/") + "signature" + "/" + fileToUpload.getName();
                    Log.d("TAG", "CallSendAPIImage: " + s3Key);

                    TransferNetworkLossHandler.getInstance(context);

                    TransferUtility transferUtility = TransferUtility.builder()
                            .context(context)
                            .s3Client(util.getS3Client(context))
                            .build();

                    TransferObserver uploadObserver = transferUtility.upload(
                            bucketName,
                            s3Key,
                            fileToUpload);
                    uploadObserver.setTransferListener(new TransferListener() {
                        @Override
                        public void onStateChanged(int idInt, TransferState state) {
                            if (state == TransferState.COMPLETED) {
                                Log.d("TAG", "signModelClass: " + signModelClass.getFilePath());
                                InsertImageSign(signModelClass.getFilePath(), context);
                                try {
                                    File fileDelete = new File(signModelClass.getFilePath());
                                    if (fileDelete.exists()) {
                                        if (fileDelete.delete()) {
                                            Log.i("file Deleted :", "onResponse: " + signModelClass.getFilePath());
                                        } else {
                                            Log.e("file Deleted :", "onResponse: " + signModelClass.getFilePath());
                                        }
                                    }
                                    callOfflineSignDataDao.deleteOfflineSignImage(signModelClass.getFilePath());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                child.getSignModelClasses().remove(signModelClass);
                                //notifyedmethod();
                                signatureSubmitAPI(child, index, 0, callback);
                            } else if (state == TransferState.FAILED) {
                                Log.e("S3 Upload", "Upload Failed");
                                InsertImageSign(signModelClass.getFilePath(), context);
                                signModelClass.setSynced(1);
                                signModelClass.setSync_status(Constants.CALL_FAILED);
                                callOfflineSignDataDao.updateSignStatus(String.valueOf(signModelClass.getId()), Constants.CALL_FAILED, 1);
                                //notifyedmethod();
                                callback.onFailure();
                            }

                        }

                        @Override
                        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                            double progress = (bytesCurrent * 100.0) / bytesTotal;
                            Log.d("S3 Upload", "Upload Progress: " + progress + "%");
                        }

                        @Override
                        public void onError(int idInt, Exception ex) {
                            Log.e("S3 Upload", "Error: " + ex.getMessage());
                            signModelClass.setSynced(1);
                            signModelClass.setSync_status(Constants.UPLOAD_FAILED);
                            callOfflineSignDataDao.updateSignStatus(String.valueOf(signModelClass.getId()), Constants.UPLOAD_FAILED, 1);
                            //notifyedmethod();
                            if (attempt != 5) {
                                CallSendAPIImageS3(child, index, attempt + 1, signModelClass, callback);
                            } else {
                                callback.onFailure();
                            }
                        }
                    });
                }

            }
        } catch (Exception e) {
            Log.v("img_tagOF", e.toString());
            signModelClass.setSynced(1);
            signModelClass.setSync_status(Constants.UPLOAD_FAILED);
            callOfflineSignDataDao.updateSignStatus(String.valueOf(signModelClass.getId()), Constants.UPLOAD_FAILED, 1);
            //notifyedmethod();
            callback.onFailure();
        }
    }

    private void CallSendAPISignImage(ChildListModelClass child, int index, int attempt, SignModelClass signModelClass, ApiCallback callback) {
        ApiInterface apiInterface = RetrofitClient.getRetrofit(requireContext(), SharedPref.getTagApiImageUrl(requireContext()));
        MultipartBody.Part img = convertImg("SignImg", signModelClass.getFilePath());
        HashMap<String, RequestBody> values = field(signModelClass.getJson_values());
        Call<JsonObject> saveImgDcr = apiInterface.SaveImg(values, img);
        saveImgDcr.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        JSONObject json = new JSONObject(response.body().toString());
                        if (json.getString("success").equalsIgnoreCase("true") && json.getString("msg").equalsIgnoreCase("Profile Has Been Updated")) {
                            try {
                                File fileDelete = new File(signModelClass.getFilePath());
                                if (fileDelete.exists()) {
                                    if (fileDelete.delete()) {
                                        Log.i("file Deleted :", "onResponse: " + signModelClass.getFilePath());
                                    } else {
                                        Log.e("file Deleted :", "onResponse: " + signModelClass.getFilePath());
                                    }
                                }
                                callOfflineSignDataDao.deleteOfflineSignImage(signModelClass.getFilePath());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            child.getSignModelClasses().remove(signModelClass);
                            //notifyedmethod();
                            signatureSubmitAPI(child, index, 0, callback);
                        } else {
                            signModelClass.setSynced(1);
                            signModelClass.setSync_status(Constants.DUPLICATE_CALL);
                            callOfflineSignDataDao.updateSignStatus(String.valueOf(signModelClass.getId()), Constants.DUPLICATE_CALL, 1);
                            callback.onSuccess();
                        }
                    } catch (Exception e) {
                        Log.v("SendOutboxCall", "-error---" + e);
                        e.printStackTrace();
                        signModelClass.setSynced(1);
                        signModelClass.setSync_status(Constants.EXCEPTION_ERROR);
                        callOfflineSignDataDao.updateSignStatus(String.valueOf(signModelClass.getId()), Constants.DUPLICATE_CALL, 1);
                        callback.onFailure();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                t.printStackTrace();
                signModelClass.setSynced(1);
                signModelClass.setSync_status(Constants.CALL_FAILED);
                callOfflineSignDataDao.updateSignStatus(String.valueOf(signModelClass.getId()), Constants.CALL_FAILED, 1);
                if (attempt != 5) {
                    CallSendAPISignImage(child, index, attempt + 1, signModelClass, callback);
                } else {
                    callback.onFailure();
                }
            }
        });
    }

    private void activitySubmitAPI(ChildListModelClass child, int index, int attempt, ApiCallback callback) {
        try {
            ArrayList<ActivityModelClass> activityList = child.getActivityModelClasses();
            if (activityList == null || activityList.isEmpty() || index >= activityList.size()) {
                callback.onSuccess();
                return;
            }
            ActivityModelClass activityModelClass = activityList.get(index);

            if (activityModelClass.getSyncStatus().equalsIgnoreCase(Constants.WAITING_FOR_SYNC) || activityModelClass.getSyncStatus().equalsIgnoreCase(Constants.CALL_FAILED)) {
                Map<String, String> mapString = new HashMap<>();
                mapString.put("axn", "save/activity");
                Call<JsonElement> activitySaveDcr = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, activityModelClass.getJsonData());
                activitySaveDcr.enqueue(new Callback<JsonElement>() {
                    @Override
                    public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                        if (response.isSuccessful()) {
                            try {
                                JSONObject jsonSaveRes = new JSONObject(String.valueOf(response.body()));
                                if (jsonSaveRes.getString("success").equalsIgnoreCase("true")) {
                                    activityOfflineDataDao.deleteOfflineActivity(activityModelClass.getId());
                                    activityList.remove(activityModelClass);
                                    //notifyedmethod();
                                    callback.onSuccess();
                                } else {
                                    outboxUtil.updateStatusActivity(activityModelClass.getId(), 5, Constants.FAILED);
                                    activityModelClass.setSyncStatus(Constants.FAILED);
                                    activityModelClass.setSyncCount(5);
                                    //notifyedmethod();
                                    callback.onFailure();
                                }
                            } catch (Exception e) {
                                outboxUtil.updateStatusActivity(activityModelClass.getId(), 5, Constants.EXCEPTION_ERROR);
                                activityModelClass.setSyncStatus(Constants.EXCEPTION_ERROR);
                                activityModelClass.setSyncCount(5);
                                Log.v("SendOutboxCall", "---" + e);
                                e.printStackTrace();
                                //notifyedmethod();
                                callback.onFailure();
                            }
                        }
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                        outboxUtil.updateStatusActivity(activityModelClass.getId(), activityModelClass.getSyncCount() + 1, Constants.FAILED);
                        activityModelClass.setSyncStatus(Constants.FAILED);
                        activityModelClass.setSyncCount(activityModelClass.getSyncCount() + 1);
                        //notifyedmethod();
                        if (attempt != 5) {
                            activitySubmitAPI(child, index, attempt + 1, callback);
                        } else {
                            callback.onFailure();
                        }
                    }
                });

            }
        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure();
        }
    }

    private void activityUploadSubmitAPI(ChildListModelClass child, int index, int attempt, ApiCallback callback) {
        ArrayList<ActivityUploadModelClass> activityUploadList = child.getActivityUploadModelClasses();
        if (activityUploadList == null || activityUploadList.isEmpty() || index >= activityUploadList.size()) {
            callback.onSuccess();
            return;
        }
        ActivityUploadModelClass activityUploadModelClass = activityUploadList.get(index);

//        if (SharedPref.getS3BucketNeed(context).equalsIgnoreCase("0")) {
////                CallSendAPIImageS3(ParentPos, ecModelClass, ChildPos, i, ecModelClass.getJson_values(), ecModelClass.getFilePath(), String.valueOf(ecModelClass.getId()), modelClass);
//        } else {
        try {
            File file = new File(activityUploadModelClass.getFilePath());
            MultipartBody.Part img = convertImg("ActivityFile", String.valueOf(file));
            HashMap<String, RequestBody> values = field(activityUploadModelClass.getJsonData());
            Call<JsonObject> saveAttachment = apiInterface.SaveImg(values, img);
            saveAttachment.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonSaveRes = new JSONObject(String.valueOf(response.body()));
                            if (jsonSaveRes.getString("success").equalsIgnoreCase("true")) {
                                activityUploadDataDao.deleteUploadActivity(activityUploadModelClass.getId(), activityUploadModelClass.getActivityID());
                                activityUploadList.remove(activityUploadModelClass);
                                //notifyedmethod();
                                callback.onSuccess();
                            } else {
                                outboxUtil.updateStatusActivity(activityUploadModelClass.getActivityID(), 5, Constants.FAILED);
                                activityUploadModelClass.setSyncStatus(Constants.FAILED);
                                activityUploadModelClass.setSyncCount(5);
                                //notifyedmethod();
                                callback.onFailure();
                            }
                        } catch (Exception e) {
                            outboxUtil.updateStatusActivity(activityUploadModelClass.getActivityID(), 5, Constants.EXCEPTION_ERROR);
                            activityUploadModelClass.setSyncStatus(Constants.EXCEPTION_ERROR);
                            activityUploadModelClass.setSyncCount(5);
                            Log.v("SendOutboxCall", "---" + e);
                            e.printStackTrace();
                            //notifyedmethod();
                            callback.onFailure();
                        }
                    }
                }

                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable throwable) {
                    outboxUtil.updateStatusActivity(activityUploadModelClass.getActivityID(), activityUploadModelClass.getSyncCount() + 1, Constants.FAILED);
                    activityUploadModelClass.setSyncStatus(Constants.FAILED);
                    activityUploadModelClass.setSyncCount(activityUploadModelClass.getSyncCount() + 1);
                    //notifyedmethod();
                    if (attempt != 5) {
                        activityUploadSubmitAPI(child, index, attempt + 1, callback);
                    } else {
                        callback.onFailure();
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure();
        }
//        }
    }

    private void daySubmitAPI(ChildListModelClass child, int attempt, ApiCallback callback) {
        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "save/daysubmit");
        DaySubmitModelClass daySubmitModelClass = child.getDaySubmitModelClass();
        if (daySubmitModelClass != null) {
            Call<JsonElement> callFinalSubmit = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, daySubmitModelClass.getJsonValues());
            callFinalSubmit.enqueue(new Callback<JsonElement>() {
                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    assert response.body() != null;
                    Log.v("FinalSubmit", response.body() + "--" + response.isSuccessful());
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonObject = new JSONObject(response.body().toString());
                            if (jsonObject.getString("success").equalsIgnoreCase("true")) {
                                offlineDaySubmitDao.delete(daySubmitModelClass.getDate());
                                child.setDaySubmitModelClass(null);
                                //notifyedmethod();
                                callback.onSuccess();
                            } else {
                                offlineDaySubmitDao.updateDaySubmitStatus(daySubmitModelClass.getDate(), 1);
                                daySubmitModelClass.setSyncStatus(1);
                                //notifyedmethod();
                                callback.onFailure();
                            }
                        } catch (Exception ignored) {
                            offlineDaySubmitDao.delete(daySubmitModelClass.getDate());
                            offlineDaySubmitDao.updateDaySubmitStatus(daySubmitModelClass.getDate(), 1);
                            //notifyedmethod();
                            callback.onFailure();
                        }
                    } else {
                        daySubmitModelClass.setSyncStatus(1);
                        //notifyedmethod();
                        callback.onFailure();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    offlineDaySubmitDao.updateDaySubmitStatus(daySubmitModelClass.getDate(), 1);
                    daySubmitModelClass.setSyncStatus(1);
                    if (attempt != 5) {
                        daySubmitAPI(child, attempt + 1, callback);
                    } else {
                        callback.onFailure();
                    }
                }
            });
        } else {
            callback.onSuccess();
        }
    }

    private void CallCheckInOut(int ParentPos, int ChildPos, ArrayList<CheckInOutModelClass> checkInOutModelClasses, GroupModelClass modelClass) {
//        if (SharedPref.getSrtNd(requireContext()).equalsIgnoreCase("0")) {
        if (checkInOutModelClasses != null && checkInOutModelClasses.size() > 0) {
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
//        } else {
//            isCallAvailable = false;
//        }

        if (!isCallAvailable) {
            CallAPIWorkPlan(ParentPos, 1, listDates.get(ParentPos).getChildItems().get(1).getWorkPlanModelClass(), modelClass);
        }
    }

    private void CallAPIWorkPlan(int ParentPos, int ChildPos, WorkPlanModelClass workPlanModelClass, GroupModelClass modelClass) {
        if (workPlanModelClass != null) {
            isCallAvailable = false;
            if (workPlanModelClass.getSyncStatus() == 0) {
                isCallAvailable = true;
                Log.v("SendOutboxCall", "--WorkPlan--" + workPlanModelClass.getDate() + "---" + workPlanModelClass.getWtName() + "---" + workPlanModelClass.getWtCode());
                if (!workPlanModelClass.getJsonValues().isEmpty()) {
                    CallSendWorkPlan(ParentPos, workPlanModelClass, ChildPos, workPlanModelClass.getJsonValues(), modelClass);
                }
            }
        } else {
            isCallAvailable = false;
        }

        if (!isCallAvailable) {
            CallOfflineCalls(ParentPos, 2, listDates.get(ParentPos).getChildItems().get(2).getOutBoxCallLists(), modelClass);
        }
    }

    private void CallOfflineCalls(int ParentPos, int ChildPos, ArrayList<OutBoxCallList> outBoxCallLists, GroupModelClass modelClass) {
        if (outBoxCallLists != null && outBoxCallLists.size() > 0) {
            isCallAvailable = false;
            for (int m = 0; m < outBoxCallLists.size(); m++) {
                OutBoxCallList outBoxCallList = outBoxCallLists.get(m);
                if (outBoxCallList.getStatus().equalsIgnoreCase(Constants.WAITING_FOR_SYNC) || outBoxCallList.getStatus().equalsIgnoreCase(Constants.CALL_FAILED)) {
                    if (outBoxCallList.getSyncCount() <= 4) {
                        isCallAvailable = true;
                        Log.v("SendOutboxCall", "---" + outBoxCallList.getCusName());
                        callSyncCount++;
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
        if (ecModelClasses != null && ecModelClasses.size() > 0) {
            isCallAvailable = false;
            for (int i = 0; i < ecModelClasses.size(); i++) {
                EcModelClass ecModelClass = ecModelClasses.get(i);
                Log.v("SendOutboxCall", "--image--" + ecModelClass.getDates() + "---" + ecModelClass.getImg_name());
                if (ecModelClass.getSynced() == 0) {
                    isCallAvailable = true;
                    if (SharedPref.getS3BucketNeed(context).equalsIgnoreCase("0")) {
                        CallSendAPIImageS3(ParentPos, ecModelClass, ChildPos, i, ecModelClass.getJson_values(), ecModelClass.getFilePath(), String.valueOf(ecModelClass.getId()), modelClass);
                    } else {
                        CallSendAPIImage(ParentPos, ecModelClass, ChildPos, i, ecModelClass.getJson_values(), ecModelClass.getFilePath(), String.valueOf(ecModelClass.getId()), modelClass);
                    }
                    break;
                }
            }
        } else {
            isCallAvailable = false;
        }
        if (!isCallAvailable) {
            CallOfflineSignImg(ParentPos, 4, listDates.get(ParentPos).getChildItems().get(4).getSignModelClasses(), modelClass);

        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void CallOfflineSignImg(int ParentPos, int ChildPos, ArrayList<SignModelClass> signModelClasses, GroupModelClass modelClass) {
        if (signModelClasses != null && signModelClasses.size() > 0) {
            Log.d("SignModelClass", "CallOfflineSignImg: " + signModelClasses.size());
            isCallAvailable = false;
            for (int i = 0; i < signModelClasses.size(); i++) {
                SignModelClass signModelClass = signModelClasses.get(i);
                if (signModelClass.getSynced() == 0) {
                    isCallAvailable = true;
                    if (SharedPref.getS3BucketNeed(requireContext()).equalsIgnoreCase("0")) {
                        CallSendSignImageS3(ParentPos, signModelClass, ChildPos, i, signModelClass.getJson_values(), signModelClass.getFilePath(), String.valueOf(signModelClass.getId()), modelClass);
                    } else {
                        CallSendSignImage(ParentPos, signModelClass, ChildPos, i, signModelClass.getJson_values(), signModelClass.getFilePath(), String.valueOf(signModelClass.getId()), modelClass);
                    }
                    break;
                }
            }
        } else {
            isCallAvailable = false;
            Log.d("MC", "CallOfflineSignImg: " + "is absent");
        }
        if (!isCallAvailable) {
            CallAPIOfflineActivity(ParentPos, 5, listDates.get(ParentPos).getChildItems().get(5).getActivityModelClasses(), modelClass);
        }
    }

    private void CallAPIOfflineActivity(int ParentPos, int ChildPos, ArrayList<ActivityModelClass> activityModelClasses, GroupModelClass modelClass) {
        if (activityModelClasses != null && !activityModelClasses.isEmpty()) {
            isCallAvailable = false;
            for (int m = 0; m < activityModelClasses.size(); m++) {
                ActivityModelClass activityModelClass = activityModelClasses.get(m);
                if (activityModelClass.getSyncStatus().equalsIgnoreCase(Constants.WAITING_FOR_SYNC) || activityModelClass.getSyncStatus().equalsIgnoreCase(Constants.FAILED)) {
                    if (activityModelClass.getSyncCount() <= 4) {
                        isCallAvailable = true;
                        Log.v("SendOutboxCall", "---" + activityModelClass.getName() + " -> " + activityModelClass.getActivityDate() + " - " + activityModelClass.getActivityTime());
                        CallSendActivityAPI(ParentPos, ChildPos, modelClass, activityModelClass, m);
                        break;
                    }
                }
            }
        } else {
            isCallAvailable = false;
        }
        if (!isCallAvailable) {
            CallAPIActivityUpload(ParentPos, 6, listDates.get(ParentPos).getChildItems().get(6).getActivityUploadModelClasses(), modelClass);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void CallAPIActivityUpload(int ParentPos, int ChildPos, ArrayList<ActivityUploadModelClass> activityUploadModelClasses, GroupModelClass modelClass) {
        if (activityUploadModelClasses != null && !activityUploadModelClasses.isEmpty()) {
            isCallAvailable = false;
            for (int m = 0; m < activityUploadModelClasses.size(); m++) {
                ActivityUploadModelClass activityUploadModelClass = activityUploadModelClasses.get(m);
                if (activityUploadModelClass.getSyncStatus().equalsIgnoreCase(Constants.WAITING_FOR_SYNC) || activityUploadModelClass.getSyncStatus().equalsIgnoreCase(Constants.FAILED)) {
                    if (activityUploadModelClass.getSyncCount() == 0) {
                        isCallAvailable = true;
                        Log.v("SendOutboxCall", "--image--" + activityUploadModelClass.getActivityDate() + "---" + activityUploadModelClass.getImageName() + activityUploadModelClass.getActivityID());
                        Log.v("SendOutboxCall_______", "--image--" + activityUploadModelClass.getJsonData());
                        CallSendActivityUploadAPI(ParentPos, ChildPos, modelClass, activityUploadModelClass, m);
                        break;
                    }
                }
            }
        } else {
            isCallAvailable = false;
        }
        if (!isCallAvailable) {
            CallAPIDaySubmit(ParentPos, 7, listDates.get(ParentPos).getChildItems().get(7).getDaySubmitModelClass(), modelClass);
        }
    }

    private void CallAPIDaySubmit(int ParentPos, int ChildPos, DaySubmitModelClass daySubmitModelClass, GroupModelClass modelClass) {
        if (daySubmitModelClass != null) {
            isCallAvailable = false;
            if (daySubmitModelClass.getSyncStatus() == 0) {
                isCallAvailable = true;
                Log.v("SendOutboxCall", "--Day Submit--" + daySubmitModelClass.getDate() + "---" + daySubmitModelClass.getJsonValues());
                if (!daySubmitModelClass.getJsonValues().isEmpty()) {
                    CallSendDaySubmit(ParentPos, daySubmitModelClass, ChildPos, daySubmitModelClass.getJsonValues(), modelClass);
                }
            }
        } else {
            isCallAvailable = false;
        }

        if (!isCallAvailable) {
            if (listDates.get(ParentPos).getChildItems().get(0).getCheckInOutModelClasses().isEmpty()
                    && listDates.get(ParentPos).getChildItems().get(1).getWorkPlanModelClass() == null
                    && listDates.get(ParentPos).getChildItems().get(2).getOutBoxCallLists().isEmpty()
                    && listDates.get(ParentPos).getChildItems().get(3).getEcModelClasses().isEmpty()
                    && listDates.get(ParentPos).getChildItems().get(4).getSignModelClasses().isEmpty()
                    && listDates.get(ParentPos).getChildItems().get(5).getActivityModelClasses().isEmpty()
                    && listDates.get(ParentPos).getChildItems().get(6).getActivityUploadModelClasses().isEmpty()
                    && listDates.get(ParentPos).getChildItems().get(7).getDaySubmitModelClass() == null) {
                listDates.remove(ParentPos);
            } else {
                modelClass.setSynced(1);
            }
            //notifyedmethod();
            sendingOfflineCalls();
        }
    }


    private void CallSendAPIImage(int parentPos, EcModelClass ecModelClass, int childPos, int CurrentPos,
                                  String jsonValues, String filePath, String id, GroupModelClass modelClass) {
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

    // Event Capture S3
    private void CallSendAPIImageS3(int parentPos, EcModelClass ecModelClass, int childPos, int CurrentPos,
                                    String jsonValues, String filePath, String id, GroupModelClass modelClass) {
        try {
            util.getS3Client(context);
            String bucketName = "san-one";
            File fileToUpload = new File(filePath);
            Log.d("fileToUpload", "CallImageAPI: " + fileToUpload.getAbsolutePath());
            if (!fileToUpload.exists()) {
                DeleteCacheFile(filePath, id, CurrentPos, parentPos, childPos, modelClass);
                Log.d("fileToUpload", "not exists: " + filePath);
            } else {
                String s3Key = "uploads/" + SharedPref.getDivisionSname(context) + SharedPref.getDivisionCode(context).replace(",", "/") + "event_capture" + "/" + fileToUpload.getName();
                Log.d("TAG", "CallSendAPIImage: " + s3Key);


                TransferNetworkLossHandler.getInstance(context);

                TransferUtility transferUtility = TransferUtility.builder()
                        .context(context)
                        .s3Client(util.getS3Client(context))
                        .build();

                TransferObserver uploadObserver = transferUtility.upload(
                        bucketName,
                        s3Key,
                        fileToUpload);
                Log.d("uploadObserver", "CallSendAPIImage: " + uploadObserver);
                uploadObserver.setTransferListener(new TransferListener() {
                    @Override
                    public void onStateChanged(int idInt, TransferState state) {
                        if (state == TransferState.COMPLETED) {
                            Log.d("TAG", "ecModelClass: " + filePath);
                            InsertImage(ecModelClass.getFilePath(), context);
                            DeleteCacheFile(filePath, id, CurrentPos, parentPos, childPos, modelClass);
                            Log.d("S3 Upload", "Upload Successful: " + s3Key);
                            try {
                                if (!listDates.isEmpty() && listDates.size() > parentPos) {
                                    listDates.get(parentPos).getChildItems().get(childPos).getEcModelClasses().remove(CurrentPos);
                                    CallOfflineImage(parentPos, childPos, listDates.get(parentPos).getChildItems().get(childPos).getEcModelClasses(), modelClass);
                                }
                                //notifyedmethod();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (state == TransferState.FAILED) {

                            Log.e("S3 Upload", "Upload Failed");
                            InsertImage(ecModelClass.getFilePath(), context);
                            ecModelClass.setSynced(1);
                            ecModelClass.setSync_status(Constants.CALL_FAILED);
                            try {
                                if (!listDates.isEmpty() && listDates.size() > parentPos) {
                                    callOfflineECDataDao.updateECStatus(id, Constants.CALL_FAILED, 1);
                                    CallOfflineImage(parentPos, childPos, listDates.get(parentPos).getChildItems().get(childPos).getEcModelClasses(), modelClass);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //notifyedmethod();
                        }

                    }

                    @Override
                    public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                        double progress = (bytesCurrent * 100.0) / bytesTotal;
                        Log.d("S3 Upload", "Upload Progress: " + progress + "%");
                    }

                    @Override
                    public void onError(int idInt, Exception ex) {
                        Log.e("S3 Upload", "Error: " + ex.getMessage());
                        ecModelClass.setSynced(1);
                        ecModelClass.setSync_status(Constants.EXCEPTION_ERROR);
                        callOfflineECDataDao.updateECStatus(id, Constants.EXCEPTION_ERROR, 1);
                        try {
                            if (!listDates.isEmpty() && listDates.size() > parentPos) {
                                CallOfflineImage(parentPos, childPos, listDates.get(parentPos).getChildItems().get(childPos).getEcModelClasses(), modelClass);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //notifyedmethod();
                    }
                });

            }
        } catch (Exception e) {
            Log.v("img_tag", e.toString());
            ecModelClass.setSynced(1);
            ecModelClass.setSync_status(Constants.EXCEPTION_ERROR);
            callOfflineECDataDao.updateECStatus(id, Constants.EXCEPTION_ERROR, 1);
            try {
                if (!listDates.isEmpty() && listDates.size() > parentPos) {
                    CallOfflineImage(parentPos, childPos, listDates.get(parentPos).getChildItems().get(childPos).getEcModelClasses(), modelClass);
                }
            } catch (Exception a) {
                a.printStackTrace();
            }
            //notifyedmethod();
        }

    }

    @SuppressLint("NotifyDataSetChanged")
    private void DeleteCacheFile(String filePath, String id, int currentPos, int parentPos, int childPos, GroupModelClass modelClass) {
        try {
            File fileDelete = new File(filePath);
            if (fileDelete.exists()) {
                if (fileDelete.delete()) {
//                System.out.println("file Deleted :" + filePath);
                } else {
//                System.out.println("file not Deleted :" + filePath);
                }
            }
            callOfflineECDataDao.deleteOfflineEC(id);
            try {
                if (!listDates.isEmpty() && listDates.size() > parentPos) {
                    listDates.get(parentPos).getChildItems().get(childPos).getEcModelClasses().remove(currentPos);
                    CallOfflineImage(parentPos, childPos, listDates.get(parentPos).getChildItems().get(childPos).getEcModelClasses(), modelClass);
                }
            } catch (Exception a) {
                a.printStackTrace();
            }
            //notifyedmethod();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void CallSendSignImageS3(int parentPos, SignModelClass signModelClass, int childPos, int CurrentPos,
                                     String jsonValues, String filePath, String id, GroupModelClass modelClass) {
        try {
            util.getS3Client(context);
//            String bucketName = "san-edet";
            String bucketName = "san-one";
            if (!filePath.isEmpty()) {
                File fileToUpload = new File(filePath);
                Log.d("fileToUpload", "CallImageAPI: " + fileToUpload.getAbsolutePath());
                if (fileToUpload.toString().isEmpty()) {
                    Log.d("fileToUploadSignObFrag", "not exists: " + filePath);
                } else {
//                    String s3Key = SharedPref.getDivisionCode(context).replace(",", "/") + "Signature" + "/" + fileToUpload.getName();
                    String s3Key = "uploads/" + SharedPref.getDivisionSname(context) + SharedPref.getDivisionCode(context).replace(",", "/") + "signature" + "/" + fileToUpload.getName();


                    Log.d("TAG", "CallSendAPIImage: " + s3Key);

                    TransferNetworkLossHandler.getInstance(context);

                    TransferUtility transferUtility = TransferUtility.builder()
                            .context(context)
                            .s3Client(util.getS3Client(context))
                            .build();

                    TransferObserver uploadObserver = transferUtility.upload(
                            bucketName,
                            s3Key,
                            fileToUpload);
                    uploadObserver.setTransferListener(new TransferListener() {
                        @Override
                        public void onStateChanged(int idInt, TransferState state) {
                            if (state == TransferState.COMPLETED) {
                                Log.d("TAG", "signModelClass: " + filePath);
                                InsertImageSign(signModelClass.getFilePath(), context);
                                DeleteCacheFileSign(filePath, id, CurrentPos, parentPos, childPos, modelClass);
                                Log.d("S3 Upload", "Upload Successful: " + s3Key);
                                try {
                                    if (!listDates.isEmpty() && listDates.size() > parentPos) {
                                        listDates.get(parentPos).getChildItems().get(childPos).getSignModelClasses().remove(CurrentPos);
                                        CallOfflineSignImg(parentPos, childPos, listDates.get(parentPos).getChildItems().get(childPos).getSignModelClasses(), modelClass);
                                    }

                                    //notifyedmethod();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (state == TransferState.FAILED) {

                                Log.e("S3 Upload", "Upload Failed");
                                InsertImageSign(signModelClass.getFilePath(), context);
                                signModelClass.setSynced(1);
                                signModelClass.setSync_status(Constants.CALL_FAILED);
                                try {
                                    if (!listDates.isEmpty() && listDates.size() > parentPos) {
                                        callOfflineSignDataDao.updateSignStatus(id, Constants.CALL_FAILED, 1);
                                        CallOfflineSignImg(parentPos, childPos, listDates.get(parentPos).getChildItems().get(childPos).getSignModelClasses(), modelClass);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                //notifyedmethod();
                            }

                        }

                        @Override
                        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                            double progress = (bytesCurrent * 100.0) / bytesTotal;
                            Log.d("S3 Upload", "Upload Progress: " + progress + "%");
                        }

                        @Override
                        public void onError(int idInt, Exception ex) {
                            Log.e("S3 Upload", "Error: " + ex.getMessage());
                            signModelClass.setSynced(1);
                            signModelClass.setSync_status(Constants.EXCEPTION_ERROR);
                            callOfflineSignDataDao.updateSignStatus(id, Constants.EXCEPTION_ERROR, 1);
                            try {
                                if (!listDates.isEmpty() && listDates.size() > parentPos) {
                                    CallOfflineSignImg(parentPos, childPos, listDates.get(parentPos).getChildItems().get(childPos).getSignModelClasses(), modelClass);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //notifyedmethod();
                        }
                    });
                }

            }
        } catch (Exception e) {
            Log.v("img_tagOF", e.toString());
            signModelClass.setSynced(1);
            signModelClass.setSync_status(Constants.EXCEPTION_ERROR);
            callOfflineSignDataDao.updateSignStatus(id, Constants.EXCEPTION_ERROR, 1);
            try {
                if (!listDates.isEmpty() && listDates.size() > parentPos) {
                    CallOfflineSignImg(parentPos, childPos, listDates.get(parentPos).getChildItems().get(childPos).getSignModelClasses(), modelClass);
                }
            } catch (Exception a) {
                a.printStackTrace();
            }
            //notifyedmethod();
        }

    }

    private void CallSendSignImage(int parentPos, SignModelClass signModelClass, int childPos, int CurrentPos, String jsonValues, String filePath, String id, GroupModelClass modelClass) {
        ApiInterface apiInterface = RetrofitClient.getRetrofit(requireContext(), SharedPref.getTagApiImageUrl(requireContext()));
        MultipartBody.Part img = convertImg("SignImg", filePath);
        HashMap<String, RequestBody> values = field(jsonValues);
        Call<JsonObject> saveImgDcr = apiInterface.SaveImg(values, img);
        saveImgDcr.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                if (response.isSuccessful()) {
                    try {
                        assert response.body() != null;
                        JSONObject json = new JSONObject(response.body().toString());
                        if (json.getString("success").equalsIgnoreCase("true") && json.getString("msg").equalsIgnoreCase("Profile Has Been Updated")) {
                            DeleteCacheFileSign(filePath, id, CurrentPos, parentPos, childPos, modelClass);
                        } else {
                            signModelClass.setSynced(1);
                            signModelClass.setSync_status(Constants.DUPLICATE_CALL);
                            callOfflineSignDataDao.updateSignStatus(id, Constants.DUPLICATE_CALL, 1);
                            CallOfflineSignImg(parentPos, childPos, listDates.get(parentPos).getChildItems().get(childPos).getSignModelClasses(), modelClass);
                        }
                    } catch (Exception e) {
                        Log.v("SendOutboxCall", "-error---" + e);
                        signModelClass.setSynced(1);
                        signModelClass.setSync_status(Constants.EXCEPTION_ERROR);
                        callOfflineSignDataDao.updateSignStatus(id, Constants.DUPLICATE_CALL, 1);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                signModelClass.setSynced(1);
                signModelClass.setSync_status(Constants.CALL_FAILED);
                callOfflineSignDataDao.updateSignStatus(id, Constants.CALL_FAILED, 1);
                CallOfflineSignImg(parentPos, childPos, listDates.get(parentPos).getChildItems().get(childPos).getSignModelClasses(), modelClass);
            }
        });
    }


    @SuppressLint("NotifyDataSetChanged")
    private void DeleteCacheFileSign(String filePath, String id, int currentPos, int parentPos, int childPos, GroupModelClass modelClass) {
        try {
            File fileDelete = new File(filePath);
            if (fileDelete.exists()) {
                if (fileDelete.delete()) {
//                System.out.println("file Deleted :" + filePath);
                } else {
//                System.out.println("file not Deleted :" + filePath);
                }
            }
            callOfflineSignDataDao.deleteOfflineSignImage(filePath);
            try {
                if (!listDates.isEmpty() && listDates.size() > parentPos) {
                    listDates.get(parentPos).getChildItems().get(childPos).getSignModelClasses().remove(currentPos);
                    CallOfflineSignImg(parentPos, childPos, listDates.get(parentPos).getChildItems().get(childPos).getSignModelClasses(), modelClass);
                }
            } catch (Exception a) {
                a.printStackTrace();
            }
            //notifyedmethod();
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                    Log.v("Response", "" + response);
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonSaveRes = new JSONObject(String.valueOf(response.body()));
                            if (jsonSaveRes.getString("success").equalsIgnoreCase("true") && jsonSaveRes.getString("msg").isEmpty()) {
                                outboxUtil.deleteOfflineCalls(cusCode, cusName, date);
                                listDates.get(parentPos).getChildItems().get(childPos).getOutBoxCallLists().remove(CurrentPos);
                            } else if (jsonSaveRes.getString("success").equalsIgnoreCase("false") && jsonSaveRes.getString("msg").equalsIgnoreCase("Call Already Exists")) {
                                outboxUtil.updateOfflineUpdateStatusEC(date, cusCode, 5, Constants.DUPLICATE_CALL, 1);
                                outBoxCallList.setStatus(Constants.DUPLICATE_CALL);
                                outBoxCallList.setSyncCount(5);
                                UpdateEcData(date, cusCode, cusName, Constants.DUPLICATE_CALL, 1);
                                DeleteUpdateDcrTable(date, cusCode, cusType);
                            } else if (jsonSaveRes.getString("success").equalsIgnoreCase("false")) {
                                if (jsonSaveRes.has("msg")) {
                                    outboxUtil.updateOfflineUpdateStatusEC(date, cusCode, 5, jsonSaveRes.getString("msg"), 1);
                                    outBoxCallList.setStatus(jsonSaveRes.getString("msg"));
                                    outBoxCallList.setSyncCount(5);
                                    UpdateEcData(date, cusCode, cusName, jsonSaveRes.getString("msg"), 1);
                                    DeleteUpdateDcrTable(date, cusCode, cusType);
                                } else if (jsonSaveRes.has("Msg")) {
                                    outboxUtil.updateOfflineUpdateStatusEC(date, cusCode, 5, jsonSaveRes.getString("Msg"), 1);
                                    outBoxCallList.setStatus(jsonSaveRes.getString("Msg"));
                                    outBoxCallList.setSyncCount(5);
                                    UpdateEcData(date, cusCode, cusName, jsonSaveRes.getString("Msg"), 1);
                                    DeleteUpdateDcrTable(date, cusCode, cusType);
                                }
                            }
                            CallOfflineCalls(parentPos, childPos, listDates.get(parentPos).getChildItems().get(childPos).getOutBoxCallLists(), modelClass);
                        } catch (Exception e) {
                            outboxUtil.updateOfflineUpdateStatusEC(date, cusCode, 5, Constants.EXCEPTION_ERROR, 0);
                            outBoxCallList.setStatus(Constants.EXCEPTION_ERROR);
                            outBoxCallList.setSyncCount(5);
                            UpdateEcData(date, cusCode, cusName, Constants.EXCEPTION_ERROR, 0);
                            if (listDates.size() > parentPos && listDates.get(parentPos).getChildItems().size() > childPos) {
                                CallOfflineCalls(parentPos, childPos, listDates.get(parentPos).getChildItems().get(childPos).getOutBoxCallLists(), modelClass);
                            } else {
                                CallOfflineCalls(parentPos, childPos, new ArrayList<>(), modelClass);
                            }
                            Log.v("SendOutboxCall", "---" + e);
                        }
                    }
                    //notifyedmethod();
                }

                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    Log.v("CallsResponse", "" + t.getMessage().toString());
                    outboxUtil.updateOfflineUpdateStatusEC(date, cusCode, syncCount + 1, Constants.CALL_FAILED, 1);
                    outBoxCallList.setStatus(Constants.DUPLICATE_CALL);
                    outBoxCallList.setSyncCount(syncCount + 1);
                    UpdateEcData(date, cusCode, cusName, Constants.CALL_FAILED, 1);
                    CallOfflineCalls(parentPos, childPos, listDates.get(parentPos).getChildItems().get(childPos).getOutBoxCallLists(), modelClass);
                    //notifyedmethod();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
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

            MasterDataTable mData = new MasterDataTable();
            mData.setMasterKey(Constants.CALL_SYNC);
            mData.setMasterValues(jsonArray.toString());
            mData.setSyncStatus(0);
            MasterDataTable Checked = masterDataDao.getMasterSyncDataByKey(Constants.CALL_SYNC);
            if (Checked != null) {
                masterDataDao.updateData(Constants.CALL_SYNC, jsonArray.toString());
            } else {
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void CallSendActivityAPI(int parentPos, int childPos, GroupModelClass groupModelClass, ActivityModelClass activityModelClass, int outBoxListIndex) {
        JSONObject jsonSaveActivity;
        try {
            jsonSaveActivity = new JSONObject(activityModelClass.getJsonData());
            Map<String, String> mapString = new HashMap<>();
            mapString.put("axn", "save/activity");
            Call<JsonElement> activitySaveDcr = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonSaveActivity.toString());
            activitySaveDcr.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonSaveRes = new JSONObject(String.valueOf(response.body()));
                            if (jsonSaveRes.getString("success").equalsIgnoreCase("true")) {
                                activityOfflineDataDao.deleteOfflineActivity(activityModelClass.getId());
                                listDates.get(parentPos).getChildItems().get(childPos).getActivityModelClasses().remove(activityModelClass);
                            } else {
                                outboxUtil.updateStatusActivity(activityModelClass.getId(), 5, Constants.FAILED);
                                activityModelClass.setSyncStatus(Constants.FAILED);
                                activityModelClass.setSyncCount(5);
                                listDates.get(parentPos).getChildItems().get(childPos).getActivityModelClasses().set(outBoxListIndex, activityModelClass);
                            }
                            CallAPIOfflineActivity(parentPos, childPos, listDates.get(parentPos).getChildItems().get(4).getActivityModelClasses(), groupModelClass);
                        } catch (Exception e) {
                            outboxUtil.updateStatusActivity(activityModelClass.getId(), 5, Constants.EXCEPTION_ERROR);
                            activityModelClass.setSyncStatus(Constants.EXCEPTION_ERROR);
                            activityModelClass.setSyncCount(5);
                            listDates.get(parentPos).getChildItems().get(childPos).getActivityModelClasses().set(outBoxListIndex, activityModelClass);
                            Log.v("SendOutboxCall", "---" + e);
                            e.printStackTrace();
                            CallAPIOfflineActivity(parentPos, childPos, listDates.get(parentPos).getChildItems().get(4).getActivityModelClasses(), groupModelClass);
                        }
                    }
                    //notifyedmethod();
                }

                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    outboxUtil.updateStatusActivity(activityModelClass.getId(), activityModelClass.getSyncCount() + 1, Constants.FAILED);
                    activityModelClass.setSyncStatus(Constants.FAILED);
                    activityModelClass.setSyncCount(activityModelClass.getSyncCount() + 1);
                    listDates.get(parentPos).getChildItems().get(childPos).getActivityModelClasses().set(outBoxListIndex, activityModelClass);
                    CallAPIOfflineActivity(parentPos, childPos, listDates.get(parentPos).getChildItems().get(4).getActivityModelClasses(), groupModelClass);
                    //notifyedmethod();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void CallSendActivityUploadAPI(int parentPos, int childPos, GroupModelClass groupModelClass, ActivityUploadModelClass activityUploadModelClass, int outBoxListIndex) {
        try {
            File file = new File(activityUploadModelClass.getFilePath());
            MultipartBody.Part img = convertImg("ActivityFile", String.valueOf(file));
            HashMap<String, RequestBody> values = field(activityUploadModelClass.getJsonData());
            Call<JsonObject> saveAttachment = apiInterface.SaveImg(values, img);
            saveAttachment.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonSaveRes = new JSONObject(String.valueOf(response.body()));
                            if (jsonSaveRes.getString("success").equalsIgnoreCase("true")) {
                                activityUploadDataDao.deleteUploadActivity(activityUploadModelClass.getId(), activityUploadModelClass.getActivityID());
                                listDates.get(parentPos).getChildItems().get(childPos).getActivityUploadModelClasses().remove(outBoxListIndex);
                            } else {
                                outboxUtil.updateStatusActivity(activityUploadModelClass.getActivityID(), 5, Constants.FAILED);
                                activityUploadModelClass.setSyncStatus(Constants.FAILED);
                                activityUploadModelClass.setSyncCount(5);
                                listDates.get(parentPos).getChildItems().get(childPos).getActivityUploadModelClasses().set(outBoxListIndex, activityUploadModelClass);
                            }
                            CallAPIActivityUpload(parentPos, childPos, listDates.get(parentPos).getChildItems().get(4).getActivityUploadModelClasses(), groupModelClass);
                        } catch (Exception e) {
                            outboxUtil.updateStatusActivity(activityUploadModelClass.getActivityID(), 5, Constants.EXCEPTION_ERROR);
                            activityUploadModelClass.setSyncStatus(Constants.EXCEPTION_ERROR);
                            activityUploadModelClass.setSyncCount(5);
                            listDates.get(parentPos).getChildItems().get(childPos).getActivityUploadModelClasses().set(outBoxListIndex, activityUploadModelClass);
                            Log.v("SendOutboxCall", "---" + e);
                            e.printStackTrace();
                            CallAPIActivityUpload(parentPos, childPos, listDates.get(parentPos).getChildItems().get(4).getActivityUploadModelClasses(), groupModelClass);
                        }
                    }
                    //notifyedmethod();
                }

                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable throwable) {
                    outboxUtil.updateStatusActivity(activityUploadModelClass.getActivityID(), activityUploadModelClass.getSyncCount() + 1, Constants.FAILED);
                    activityUploadModelClass.setSyncStatus(Constants.FAILED);
                    activityUploadModelClass.setSyncCount(activityUploadModelClass.getSyncCount() + 1);
                    listDates.get(parentPos).getChildItems().get(childPos).getActivityUploadModelClasses().set(outBoxListIndex, activityUploadModelClass);
                    CallAPIActivityUpload(parentPos, childPos, listDates.get(parentPos).getChildItems().get(4).getActivityUploadModelClasses(), groupModelClass);
                    //notifyedmethod();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
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
                        //notifyedmethod();
                    } catch (Exception e) {
                        offlineCheckInOutDataDao.deleteOfflineCheckInOut(checkClass.getDates(), checkClass.getCheckCount());
                        listDates.get(ParentPos).getChildItems().get(ChildPos).getCheckInOutModelClasses().remove(CurrentPos);
                        CallCheckInOut(ParentPos, ChildPos, listDates.get(ParentPos).getChildItems().get(ChildPos).getCheckInOutModelClasses(), modelClass);
                        //notifyedmethod();
                    }


                } else {
                    checkClass.setCheckStatus(1);
                    CallCheckInOut(ParentPos, ChildPos, listDates.get(ParentPos).getChildItems().get(ChildPos).getCheckInOutModelClasses(), modelClass);
                    //notifyedmethod();
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                offlineCheckInOutDataDao.updateCheckInOutStatus(checkClass.getId(), 1);
                checkClass.setCheckStatus(1);
                CallCheckInOut(ParentPos, ChildPos, listDates.get(ParentPos).getChildItems().get(ChildPos).getCheckInOutModelClasses(), modelClass);
                //notifyedmethod();
            }
        });
    }

    private void CallSendWorkPlan(int ParentPos, WorkPlanModelClass workPlanModelClass, int ChildPos, String jsonValues, GroupModelClass modelClass) {
        Map<String, String> mapString = new HashMap<>();
        if (SharedPref.getSfType(requireContext()).equalsIgnoreCase("1")) {
            mapString.put("axn", "edetsave/dayplan");
        } else {
            mapString.put("axn", "multihqsave/dayplan");
        }
        Call<JsonElement> saveMyDayPlan = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonValues);
        saveMyDayPlan.enqueue(new Callback<JsonElement>() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                Log.v("DayPlan", response.body() + "--" + response.isSuccessful());
                if (response.isSuccessful()) {
                    try {
                        JSONObject json = new JSONObject(Objects.requireNonNull(response.body()).toString());
                        if (json.getString("success").equalsIgnoreCase("true")) {
                            offlineWorkTypeDataDao.delete(workPlanModelClass.getDate());
                            listDates.get(ParentPos).getChildItems().get(ChildPos).setWorkPlanModelClass(null);
                        } else {
                            offlineWorkTypeDataDao.updateWorkTypeStatus(workPlanModelClass.getId(), 1);
                            workPlanModelClass.setSyncStatus(1);
                        }
                        CallAPIWorkPlan(ParentPos, ChildPos, listDates.get(ParentPos).getChildItems().get(ChildPos).getWorkPlanModelClass(), modelClass);
                        //notifyedmethod();
                    } catch (Exception ignored) {
                        offlineWorkTypeDataDao.delete(workPlanModelClass.getDate());
                        listDates.get(ParentPos).getChildItems().get(ChildPos).setWorkPlanModelClass(null);
                        CallAPIWorkPlan(ParentPos, ChildPos, listDates.get(ParentPos).getChildItems().get(ChildPos).getWorkPlanModelClass(), modelClass);
                        //notifyedmethod();
                    }
                } else {
                    workPlanModelClass.setSyncStatus(1);
                    CallAPIWorkPlan(ParentPos, ChildPos, listDates.get(ParentPos).getChildItems().get(ChildPos).getWorkPlanModelClass(), modelClass);
                    //notifyedmethod();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                offlineWorkTypeDataDao.updateWorkTypeStatus(workPlanModelClass.getId(), 1);
                workPlanModelClass.setSyncStatus(1);
                CallAPIWorkPlan(ParentPos, ChildPos, listDates.get(ParentPos).getChildItems().get(ChildPos).getWorkPlanModelClass(), modelClass);
                //notifyedmethod();
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
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonObject = new JSONObject(response.body().toString());
                        if (jsonObject.getString("success").equalsIgnoreCase("true")) {
                            offlineDaySubmitDao.delete(daySubmitModelClass.getDate());
                            listDates.get(ParentPos).getChildItems().get(ChildPos).setDaySubmitModelClass(null);
                        } else {
                            offlineDaySubmitDao.updateDaySubmitStatus(daySubmitModelClass.getDate(), 1);
                            daySubmitModelClass.setSyncStatus(1);
                        }
                        CallAPIDaySubmit(ParentPos, ChildPos, listDates.get(ParentPos).getChildItems().get(ChildPos).getDaySubmitModelClass(), modelClass);
                        //notifyedmethod();
                    } catch (Exception ignored) {
                        offlineDaySubmitDao.delete(daySubmitModelClass.getDate());
                        offlineDaySubmitDao.updateDaySubmitStatus(daySubmitModelClass.getDate(), 1);
                        CallAPIDaySubmit(ParentPos, ChildPos, listDates.get(ParentPos).getChildItems().get(ChildPos).getDaySubmitModelClass(), modelClass);
                        //notifyedmethod();
                    }
                } else {
                    daySubmitModelClass.setSyncStatus(1);
                    CallAPIDaySubmit(ParentPos, ChildPos, listDates.get(ParentPos).getChildItems().get(ChildPos).getDaySubmitModelClass(), modelClass);
                    //notifyedmethod();
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

    private void InsertImage(final String ImageUrl, Context context) {
        File imageFile = new File(ImageUrl);
        Log.d("AWS_s3", "fileToUpload" + "--" + imageFile);
        String fileName = new File(ImageUrl).getName();
        new AWSBuckets(context, fileName, imageFile, "");
        //here imageFile is the filePath & fileName is the image Name
    }

    private void InsertImageSign(final String ImageUrl, Context context) {
        File imageFile = new File(ImageUrl);
        Log.d("AWS_s3", "fileToUpload" + "--" + imageFile);
        String fileName = new File(ImageUrl).getName();
        new AWSBucketsSign(context, fileName, imageFile, "");
        //here imageFile is the filePath & fileName is the image Name
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

        if (IsFromDCR) {
            String status = NetworkUtil.getConnectivityStatusString(requireContext());
            OutboxFragment.NetworkConnectCallHomeDashBoard(status);
            IsFromDCR = false;
        }


//        new Handler().postDelayed(this::refreshPendingFunction, 200);
    }


    public static void notifyedmethod() {
        outBoxHeaderAdapter.notifyDataSetChanged();
        if (listDates.size() > 0) {
            outBoxBinding.rvOutBoxHead.setVisibility(View.VISIBLE);
            outBoxBinding.outboxEmtyImage.setVisibility(View.GONE);
        } else {
            outBoxBinding.rvOutBoxHead.setVisibility(View.GONE);
            outBoxBinding.outboxEmtyImage.setVisibility(View.VISIBLE);
        }
    }
}