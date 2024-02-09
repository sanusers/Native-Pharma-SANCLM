package saneforce.santrip.activity.homeScreen.fragment;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;
import static saneforce.santrip.activity.homeScreen.HomeDashBoard.CheckInOutNeed;
import static saneforce.santrip.activity.homeScreen.fragment.CallAnalysisFragment.Chemist_list;
import static saneforce.santrip.activity.homeScreen.fragment.CallAnalysisFragment.Doctor_list;
import static saneforce.santrip.activity.homeScreen.fragment.CallAnalysisFragment.Stockiest_list;
import static saneforce.santrip.activity.homeScreen.fragment.CallAnalysisFragment.callAnalysisBinding;
import static saneforce.santrip.activity.homeScreen.fragment.CallAnalysisFragment.cip_list;
import static saneforce.santrip.activity.homeScreen.fragment.CallAnalysisFragment.hos_list;
import static saneforce.santrip.activity.homeScreen.fragment.CallAnalysisFragment.unlistered_list;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.santrip.activity.homeScreen.adapters.OutBoxHeaderAdapter;
import saneforce.santrip.activity.homeScreen.modelClass.CheckInOutModelClass;
import saneforce.santrip.activity.homeScreen.modelClass.EcModelClass;
import saneforce.santrip.activity.homeScreen.modelClass.GroupModelClass;
import saneforce.santrip.activity.homeScreen.modelClass.OutBoxCallList;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.databinding.OutboxFragmentBinding;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.storage.SharedPref;
import saneforce.santrip.utility.NetworkCheckInterface;


public class OutboxFragment extends Fragment {

    @SuppressLint("StaticFieldLeak")
    public static OutboxFragmentBinding outBoxBinding;
    public static ArrayList<GroupModelClass> listDates = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    public static OutBoxHeaderAdapter outBoxHeaderAdapter;
    static NetworkCheckInterface mCheckNetwork;
    SQLite sqLite;
    ApiInterface apiInterface;
    boolean isCallAvailable;
    CommonUtilsMethods commonUtilsMethods;

    public static void NetworkConnectCallHomeDashBoard(String log) {
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
    public static void SetupOutBoxAdapter(Activity activity, SQLite sqLite, Context context) {
        if (CheckInOutNeed.equalsIgnoreCase("1")) {
            listDates = sqLite.getOutBoxDate(true);
        } else {
            listDates = sqLite.getOutBoxDate(false);
        }

        outBoxHeaderAdapter = new OutBoxHeaderAdapter(activity, context, listDates);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        outBoxBinding.rvOutBoxHead.setLayoutManager(mLayoutManager);
        outBoxBinding.rvOutBoxHead.setAdapter(outBoxHeaderAdapter);
        outBoxHeaderAdapter.notifyDataSetChanged();
    }

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.v("fragment", "OutBox");
        outBoxBinding = OutboxFragmentBinding.inflate(inflater, container, false);
        View v = outBoxBinding.getRoot();
        sqLite = new SQLite(requireContext());
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        SetupOutBoxAdapter(requireActivity(), sqLite, requireContext());

        new Handler().postDelayed(this::refreshPendingFunction, 200);

        outBoxBinding.clearCalls.setOnClickListener(v1 -> {
            ArrayList<OutBoxCallList> outBoxCallLists = sqLite.getOutBoxCallsFullList();
            try {
                if (outBoxCallLists.size() > 0) {
                    JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.DCR);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        for (int j = 0; j < outBoxCallLists.size(); j++) {
                            if (jsonObject.getString("Dcr_dt").equalsIgnoreCase(outBoxCallLists.get(j).getDates()) && jsonObject.getString("CustCode").equalsIgnoreCase(outBoxCallLists.get(j).getCusCode())) {
                                jsonArray.remove(i);
                                i--;
                            }
                        }
                    }
                    sqLite.saveMasterSyncData(Constants.DCR, jsonArray.toString(), 0);
                    CallAnalysisFragment.SetcallDetailsInLineChart(sqLite, requireContext());
                }
            } catch (Exception ignored) {

            }

            if (CheckInOutNeed.equalsIgnoreCase("1")) {
                if (sqLite.getCountCheckInOut(CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd")) > 0) {
                    SharedPref.setCheckInTime(requireContext(), "");
                    SharedPref.setCheckDateTodayPlan(requireContext(), "");
                }
            }
            sqLite.deleteOfflineCalls();
            listDates.clear();
            outBoxHeaderAdapter = new OutBoxHeaderAdapter(requireActivity(), requireContext(), listDates);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(requireContext());
            outBoxBinding.rvOutBoxHead.setLayoutManager(mLayoutManager);
            outBoxBinding.rvOutBoxHead.setAdapter(outBoxHeaderAdapter);
        });

        return v;
    }

    private void refreshPendingFunction() {
        SendOfflineData(this::sendingOfflineCalls);
    }

    private void sendingOfflineCalls() {
        apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));

        //CheckInOutData
        if (listDates.size() > 0) {
            for (int i = 0; i < listDates.size(); i++) {
                CallCheckInOut(i, 0, listDates.get(i).getChildItems().get(0).getCheckInOutModelClasses());
                break;
            }
        } else {
            SetupOutBoxAdapter(requireActivity(), sqLite, requireContext());
        }
    }


       /* //Call Data
        ArrayList<OutBoxCallList> outBoxCallLists = sqLite.getOutBoxCallsFullList("Call Failed", "Waiting for Sync");
        CallApiList(outBoxCallLists);*/

    private void CallCheckInOut(int ParentPos, int ChildPos, ArrayList<CheckInOutModelClass> checkInOutModelClasses) {
        if (checkInOutModelClasses.size() > 0) {
            for (int m = 0; m < checkInOutModelClasses.size(); m++) {
                CheckInOutModelClass checkInOutModelClass = checkInOutModelClasses.get(m);
                Log.v("SendOutboxCall", "--checkInOut--" + checkInOutModelClass.getDates() + "---" + checkInOutModelClass.getCheckInTime() + "----" + checkInOutModelClass.getCheckOutTime());
                if (checkInOutModelClass.getJsonOutValues().isEmpty()) {
                    CallCheckInOutAPI(ParentPos, ChildPos, m, checkInOutModelClass.getJsonInValues(), checkInOutModelClass.getDates(), checkInOutModelClass.getCheckCount());
                } else {
                    CallCheckInOutAPI(ParentPos, ChildPos, m, checkInOutModelClass.getJsonOutValues(), checkInOutModelClass.getDates(), checkInOutModelClass.getCheckCount());
                }
                break;
            }
        } else {
            CallOfflineCalls(ParentPos, 2, listDates.get(ParentPos).getChildItems().get(2).getOutBoxCallLists());
        }
    }

    private void CallOfflineCalls(int ParentPos, int ChildPos, ArrayList<OutBoxCallList> outBoxCallLists) {
        if (outBoxCallLists.size() > 0) {
            isCallAvailable = false;
            for (int m = 0; m < outBoxCallLists.size(); m++) {
                OutBoxCallList outBoxCallList = outBoxCallLists.get(m);
                if (outBoxCallList.getStatus().equalsIgnoreCase("Waiting for Sync") || outBoxCallList.getStatus().equalsIgnoreCase("Call Failed")) {
                    if (outBoxCallList.getSyncCount() <= 4) {
                        isCallAvailable = true;
                        Log.v("SendOutboxCall", "---" + outBoxCallList.getCusName());
                        CallSendAPI(outBoxCallList, ParentPos, ChildPos, m, outBoxCallList.getDates(), outBoxCallList.getCusName(), outBoxCallList.getCusCode(), outBoxCallList.getJsonData(), outBoxCallList.getCusType(), outBoxCallList.getSyncCount());
                        break;
                    }
                }
            }
        } else {
            isCallAvailable = false;
        }

        if (!isCallAvailable) {
            if (CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd").equalsIgnoreCase(listDates.get(ParentPos).getGroupName())) {
                CallsFragment.CallTodayCallsAPI(context, apiInterface, sqLite, false);
            }
            CallOfflineImage(ParentPos, 3, listDates.get(ParentPos).getChildItems().get(3).getEcModelClasses());
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void CallOfflineImage(int ParentPos, int ChildPos, ArrayList<EcModelClass> ecModelClasses) {
        if (ecModelClasses.size() > 0) {
            for (int i = 0; i < ecModelClasses.size(); i++) {
                EcModelClass ecModelClass = ecModelClasses.get(i);
                Log.v("SendOutboxCall", "--image--" + ecModelClass.getDates() + "---" + ecModelClass.getName());
                CallSendAPIImage(ParentPos, ChildPos, i, ecModelClass.getJson_values(), ecModelClass.getFilePath(), ecModelClass.getId());
                break;
            }
        } else {
            listDates.remove(ParentPos);
            outBoxHeaderAdapter.notifyDataSetChanged();
            sendingOfflineCalls();
        }
    }

    private void CallSendAPIImage(int parentPos, int childPos, int CurrentPos, String jsonValues, String filePath, String id) {
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
                        Log.v("SendOutboxCall", "-imageRes---" + json);
                        if (json.getString("success").equalsIgnoreCase("true") && json.getString("msg").equalsIgnoreCase("Photo Has Been Updated")) {
                            DeleteCacheFile(filePath, id, CurrentPos, parentPos, childPos);
                        } else {
                            DeleteCacheFile(filePath, id, CurrentPos, parentPos, childPos);
                        }
                    } catch (Exception e) {
                        Log.v("SendOutboxCall", "-error---" + e);
                        DeleteCacheFile(filePath, id, CurrentPos, parentPos, childPos);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                DeleteCacheFile(filePath, id, CurrentPos, parentPos, childPos);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void DeleteCacheFile(String filePath, String id, int currentPos, int parentPos, int childPos) {
        File fileDelete = new File(filePath);
        if (fileDelete.exists()) {
            if (fileDelete.delete()) {
                System.out.println("file Deleted :" + filePath);
            } else {
                System.out.println("file not Deleted :" + filePath);
            }
        }
        sqLite.deleteOfflineEC(id);
        listDates.get(parentPos).getChildItems().get(childPos).getEcModelClasses().remove(currentPos);
        outBoxHeaderAdapter.notifyDataSetChanged();
        CallOfflineImage(parentPos, childPos, listDates.get(parentPos).getChildItems().get(childPos).getEcModelClasses());
    }

    private void CallSendAPI(OutBoxCallList outBoxCallList, int parentPos, int childPos, int CurrentPos, String date, String cusName, String cusCode, String jsonData, String cusType, int syncCount) {
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
                                sqLite.deleteOfflineCalls(cusCode, cusName, date);
                                listDates.get(parentPos).getChildItems().get(childPos).getOutBoxCallLists().remove(CurrentPos);
                            } else if (jsonSaveRes.getString("success").equalsIgnoreCase("false") && jsonSaveRes.getString("msg").equalsIgnoreCase("Call Already Exists")) {
                                sqLite.saveOfflineUpdateStatus(date, cusCode, String.valueOf(5), "Duplicate Call");
                                outBoxCallList.setStatus("Duplicate Call");
                                outBoxCallList.setSyncCount(5);
                                //    childListModelClasses.get(position).getOutBoxCallLists().set(outBoxList, new OutBoxCallList(cusName, cusCode, date, outBoxCallList.getIn(), outBoxCallList.getOut(), jsonData, outBoxCallList.getCusType(), "Duplicate Call", 5));
                                DeleteUpdateDcrTable(date, cusCode, cusType);
                            }
                            CallOfflineCalls(parentPos, childPos, listDates.get(parentPos).getChildItems().get(childPos).getOutBoxCallLists());
                        } catch (Exception e) {
                            sqLite.saveOfflineUpdateStatus(date, cusCode, String.valueOf(5), "Exception Error");
                            outBoxCallList.setStatus("Exception Error");
                            outBoxCallList.setSyncCount(5);
                    /*        listDates.get(parentPos).getChildItems().get(childPos).getOutBoxCallLists().remove(CurrentPos);
                            sqLite.deleteOfflineCalls(cusCode, cusName, date);*/
                            CallOfflineCalls(parentPos, childPos, listDates.get(parentPos).getChildItems().get(childPos).getOutBoxCallLists());
                            Log.v("SendOutboxCall", "---" + e);
                        }
                    }
                    outBoxHeaderAdapter.notifyDataSetChanged();
                }

                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    sqLite.saveOfflineUpdateStatus(date, cusCode, String.valueOf(syncCount + 1), "Call Failed");
                    outBoxCallList.setStatus("Duplicate Call");
                    outBoxCallList.setSyncCount(syncCount + 1);
                    CallOfflineCalls(parentPos, childPos, listDates.get(parentPos).getChildItems().get(childPos).getOutBoxCallLists());
                    outBoxHeaderAdapter.notifyDataSetChanged();
                }
            });

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void DeleteUpdateDcrTable(String date, String cusCode, String cusType) {
        try {
            JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.DCR);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (jsonObject.getString("Dcr_dt").equalsIgnoreCase(date) && jsonObject.getString("CustCode").equalsIgnoreCase(cusCode)) {
                    jsonArray.remove(i);
                    break;
                }
            }

            sqLite.saveMasterSyncData(Constants.DCR, jsonArray.toString(), 0);
            sqLite.deleteLineChart(cusCode, date);
            switch (cusType) {
                case "1":
                    int doc_current_callcount = sqLite.getcurrentmonth_calls_count("1");
                    callAnalysisBinding.txtDocCount.setText(String.format("%d / %d", doc_current_callcount, Doctor_list.length()));
                    break;
                case "2":
                    int che_current_callcount = sqLite.getcurrentmonth_calls_count("2");
                    callAnalysisBinding.txtCheCount.setText(String.format("%d / %d", che_current_callcount, Chemist_list.length()));
                    break;
                case "3":
                    int stockiest_current_callcount = sqLite.getcurrentmonth_calls_count("3");
                    callAnalysisBinding.txtStockCount.setText(String.format("%d / %d", stockiest_current_callcount, Stockiest_list.length()));
                    break;
                case "4":
                    int unlistered_current_callcount = sqLite.getcurrentmonth_calls_count("4");
                    callAnalysisBinding.txtUnlistCount.setText(String.format("%d / %d", unlistered_current_callcount, unlistered_list.length()));
                    break;
                case "5":
                    int cip_current_callcount = sqLite.getcurrentmonth_calls_count("5");
                    callAnalysisBinding.txtCipCount.setText(String.format("%d / %d", cip_current_callcount, cip_list.length()));
                    break;
                case "6":
                    int hos_current_callcount = sqLite.getcurrentmonth_calls_count("6");
                    callAnalysisBinding.txtHosCount.setText(String.format("%d / %d", hos_current_callcount, hos_list.length()));
                    break;
            }
        } catch (Exception ignored) {

        }
    }

    private void CallCheckInOutAPI(int ParentPos, int ChildPos, int CurrentPos, String jsonOutValues, String dates, String checkCount) {
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
                if (response.isSuccessful()) {
                    sqLite.deleteOfflineCheckInOut(dates, checkCount);
                    listDates.get(ParentPos).getChildItems().get(ChildPos).getCheckInOutModelClasses().remove(CurrentPos);
                } else {
                    sqLite.deleteOfflineCheckInOut(dates, checkCount);
                    listDates.get(ParentPos).getChildItems().get(ChildPos).getCheckInOutModelClasses().remove(CurrentPos);
                }
                CallCheckInOut(ParentPos, ChildPos, listDates.get(ParentPos).getChildItems().get(ChildPos).getCheckInOutModelClasses());
                outBoxHeaderAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                sqLite.deleteOfflineCheckInOut(dates, checkCount);
                listDates.get(ParentPos).getChildItems().get(ChildPos).getCheckInOutModelClasses().remove(CurrentPos);
                CallCheckInOut(ParentPos, ChildPos, listDates.get(ParentPos).getChildItems().get(ChildPos).getCheckInOutModelClasses());
                outBoxHeaderAdapter.notifyDataSetChanged();
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
                                sqLite.saveOfflineUpdateStatus(dates, cusCode, String.valueOf(5), "Duplicate Call");
                                outBoxCallList.set(position, new OutBoxCallList(cusName, cusCode, dates, jsonData, "Duplicate Call", 5));
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
                    sqLite.saveOfflineUpdateStatus(dates, cusCode, String.valueOf(syncCount + 1), "Call Failed");
                    outBoxCallList.set(position, new OutBoxCallList(cusName, cusCode, dates, jsonData, "Call Failed", syncCount + 1));
                    CallApiList(outBoxCallList);
                }
            });

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
*/
}