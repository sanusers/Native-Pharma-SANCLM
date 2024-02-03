package saneforce.santrip.activity.homeScreen.fragment;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import static saneforce.santrip.activity.homeScreen.HomeDashBoard.CheckInOutNeed;

import android.annotation.SuppressLint;
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

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import id.zelory.compressor.Compressor;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.santrip.activity.homeScreen.adapters.OutBoxHeaderAdapter;
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
    SQLite sqLite;
    static NetworkCheckInterface mCheckNetwork;
    ApiInterface apiInterface;
    boolean isCallAvailable;
    CommonUtilsMethods commonUtilsMethods;

    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.v("fragment", "OutBox");
        outBoxBinding = OutboxFragmentBinding.inflate(inflater, container, false);
        View v = outBoxBinding.getRoot();
        sqLite = new SQLite(requireContext());
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        SetupOutBoxAdapter(sqLite,requireContext());

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

            sqLite.deleteOfflineCalls();
            listDates.clear();
            outBoxHeaderAdapter = new OutBoxHeaderAdapter(requireContext(), listDates);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(requireContext());
            outBoxBinding.rvOutBoxHead.setLayoutManager(mLayoutManager);
            outBoxBinding.rvOutBoxHead.setAdapter(outBoxHeaderAdapter);
        });

        return v;
    }

    public static void NetworkConnectCallHomeDashBoard(String log) {
        if (!TextUtils.isEmpty(log)) {
            if (!log.equalsIgnoreCase("NOT_CONNECT")) {
                if (mCheckNetwork != null)
                    mCheckNetwork.checkNetwork();
            }
        }
    }

    public static void SendOfflineData(NetworkCheckInterface mCheckNetworkData) {
        mCheckNetwork = mCheckNetworkData;
    }

    private void refreshPendingFunction() {
        SendOfflineData(this::sendingOfflineCalls);
    }

    private void sendingOfflineCalls() {
        Log.v("SendOutboxCall", "---OutboxFragment--connected");
        apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));

        //Call Data
        ArrayList<OutBoxCallList> outBoxCallLists = sqLite.getOutBoxCallsFullList("Call Failed", "Waiting for Sync");
        CallApiList(outBoxCallLists);
    }

    private void CallApiList(ArrayList<OutBoxCallList> outBoxCallLists) {
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
            SetupOutBoxAdapter(sqLite,requireContext());
            CallAnalysisFragment.SetcallDetailsInLineChart(sqLite, context);
        }
    }

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

    private void CallSendAPIImage(ArrayList<EcModelClass> ecModelClasses, int position, String id, String jsonValues, String filePath) {
        ApiInterface apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getTagApiImageUrl(context));
        MultipartBody.Part img = convertImg("EventImg", filePath);
        HashMap<String, RequestBody> values = field(jsonValues.toString());
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

    @SuppressLint("NotifyDataSetChanged")
    public static void SetupOutBoxAdapter(SQLite sqLite,Context context) {
        if (CheckInOutNeed.equalsIgnoreCase("0")) {
            listDates = sqLite.getOutBoxDate(true);
        } else {
            listDates = sqLite.getOutBoxDate(false);
        }

        outBoxHeaderAdapter = new OutBoxHeaderAdapter(context, listDates);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        outBoxBinding.rvOutBoxHead.setLayoutManager(mLayoutManager);
        outBoxBinding.rvOutBoxHead.setAdapter(outBoxHeaderAdapter);
        outBoxHeaderAdapter.notifyDataSetChanged();
    }
}