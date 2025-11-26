package saneforce.sanzen.activity.ViewModel;

import static saneforce.sanzen.activity.leave.Leave_Application.AvailableLeave;
import static saneforce.sanzen.activity.leave.Leave_Application.leavebinding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.response.LoginResponse;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

@SuppressLint("StaticFieldLeak")
public class LeaveViewModel extends ViewModel {
    Call<JsonElement> leaveStatus;
    Call<JsonElement> leaveBalance;
    private final Context context;
    MasterDataDao masterDataDao;
    public LeaveViewModel(Context context) {
        this.context = context;
        RoomDB roomDB = RoomDB.getDatabase(context);
        masterDataDao = roomDB.masterDataDao();
    }

    public void updateLeaveStatusMasterSync() {

        ApiInterface apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));
        JSONObject leaveStatusObject = CommonUtilsMethods.CommonObjectParameter(context);

        try {
            leaveStatusObject.put("tableName", "getleavestatus");
            leaveStatusObject.put("sfcode", SharedPref.getSfCode(context));
            leaveStatusObject.put("division_code", SharedPref.getDivisionCode(context));
            leaveStatusObject.put("Rsf", SharedPref.getHqCode(context));
            leaveStatusObject.put("ReqDt", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_22));

//            System.out.println("leaveStatusObject--->" + leaveStatusObject);
        } catch (Exception exception) {
            exception.printStackTrace();
            leavebinding.progressBar.setVisibility(View.GONE);
            AvailableLeave(context);
        }
        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "get/leave");
        leaveStatus = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, leaveStatusObject.toString());
        leaveStatus.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, retrofit2.Response<JsonElement> response) {
                if (response.isSuccessful()) {
                    try {
                        JsonElement jsonElement = response.body();
                        JSONArray jsonArray = new JSONArray();
                        assert jsonElement != null;
                        if (!jsonElement.isJsonNull()) {
                            if (jsonElement.isJsonArray()) {
                                JsonArray jsonArray1 = jsonElement.getAsJsonArray();
                                jsonArray = new JSONArray(jsonArray1.toString());
                                masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.LEAVE_STATUS, jsonArray.toString(), 2));
                            } else if (jsonElement.isJsonObject()) {
                                JsonObject jsonObject = jsonElement.getAsJsonObject();
                                JSONObject jsonObject1 = new JSONObject(jsonObject.toString());
                                if (!jsonObject1.has("success")) {
                                    jsonArray.put(jsonObject1);
                                    masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.LEAVE_STATUS, jsonArray.toString(), 2));
                                }
                            }
                        }

                        leavebinding.progressBar.setVisibility(View.GONE);
                        AvailableLeave(context);
                    } catch (JSONException e) {
                        e.printStackTrace();
                        leavebinding.progressBar.setVisibility(View.GONE);
                        AvailableLeave(context);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                t.printStackTrace();
                leavebinding.progressBar.setVisibility(View.GONE);
                AvailableLeave(context);
            }
        });
    }

    public void syncLeaveStatus() {

      //  ApiInterface apiInterface = RetrofitClient.getRetrofit(context, "http://edetailing.sanffa.info/iOSServer/");
        ApiInterface apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));
        JSONObject requestObject = CommonUtilsMethods.CommonObjectParameter(context);

        try {
            requestObject.put("tableName", "getleavebalance");
            requestObject.put("sfcode", SharedPref.getSfCode(context));
            requestObject.put("division_code", SharedPref.getDivisionCode(context));
            requestObject.put("Rsf", SharedPref.getHqCode(context));
            requestObject.put("ReqDt", TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_22));

        } catch (Exception e) {
            e.printStackTrace();
            leavebinding.progressBar.setVisibility(View.GONE);
            AvailableLeave(context);
        }

//        Map<String, String> mapString = new HashMap<>();
//        mapString.put("axn", "get/leave");
//
//        Call<JsonElement> call = apiInterface.getJSONElement(
//                "http://edetailing.sanffa.info/iOSServer/db_api.php/",
//                mapString,
//                requestObject.toString()
//        );
        Map<String, String> mapString = new HashMap<>();
        mapString.put("axn", "get/leave");
        leaveBalance = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, requestObject.toString());
        leaveBalance.enqueue(new Callback<JsonElement>() {
       // call.enqueue(new Callback<JsonElement>() {
            @Override
            public void onResponse(Call<JsonElement> call, Response<JsonElement> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {

                        JsonElement jsonElement = response.body();
                        JSONArray jsonArray = new JSONArray(jsonElement.toString());

                        masterDataDao.saveMasterSyncData(
                                new MasterDataTable(Constants.LEAVE_STATUS, jsonArray.toString(), 2)
                        );
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                leavebinding.progressBar.setVisibility(View.GONE);
                AvailableLeave(context);
            }

            @Override
            public void onFailure(Call<JsonElement> call, Throwable t) {
                t.printStackTrace();
                leavebinding.progressBar.setVisibility(View.GONE);
                AvailableLeave(context);
            }
        });
    }

}

