package saneforce.sanzen.activity.homeScreen.adapters.outbox;

import static saneforce.sanzen.activity.homeScreen.fragment.OutboxFragment.listDates;
import static saneforce.sanzen.activity.homeScreen.fragment.OutboxFragment.outBoxBinding;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
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
import java.util.Objects;

import id.zelory.compressor.Compressor;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.homeScreen.fragment.CallsFragment;
import saneforce.sanzen.activity.homeScreen.modelClass.CheckInOutModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.DaySubmitModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.EcModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.GroupModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.OutBoxCallList;
import saneforce.sanzen.activity.homeScreen.modelClass.WorkPlanModelClass;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.UtilityClass;
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

public class OutBoxHeaderAdapter extends RecyclerView.Adapter<OutBoxHeaderAdapter.listDataViewholider> {
    Context context;
    ArrayList<GroupModelClass> groupModelClasses;
    OutBoxContentAdapter outBoxContentAdapter;
    boolean isCallAvailable;
    ProgressDialog progressDialog;
    ApiInterface apiInterface;
    OutBoxHeaderAdapter outBoxHeaderAdapter;
    CommonUtilsMethods commonUtilsMethods;
    Activity activity;
     RoomDB db;
     MasterDataDao masterDataDao;
     private final OfflineCheckInOutDataDao offlineCheckInOutDataDao;
     private final CallOfflineECDataDao callOfflineECDataDao;
     private final CallOfflineWorkTypeDataDao offlineWorkTypeDataDao;
     private final OfflineDaySubmitDao offlineDaySubmitDao;
     private final CallsUtil callsUtil;

    public OutBoxHeaderAdapter(Activity activity, Context context, ArrayList<GroupModelClass> groupModelClasses) {
        this.activity = activity;
        this.context = context;
        this.groupModelClasses = groupModelClasses;
        apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));
        commonUtilsMethods = new CommonUtilsMethods(context);
        db=RoomDB.getDatabase(context);
        masterDataDao=db.masterDataDao();
        offlineCheckInOutDataDao = db.offlineCheckInOutDataDao();
        callOfflineECDataDao = db.callOfflineECDataDao();
        offlineWorkTypeDataDao = db.callOfflineWorkTypeDataDao();
        offlineDaySubmitDao = db.offlineDaySubmitDao();
        callsUtil = new CallsUtil(context);
    }

    @NonNull
    @Override
    public OutBoxHeaderAdapter.listDataViewholider onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.outbox_group_view, parent, false);
        return new OutBoxHeaderAdapter.listDataViewholider(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull OutBoxHeaderAdapter.listDataViewholider holder, int position) {

        GroupModelClass groupModelClass = groupModelClasses.get(position);
        holder.tvDate.setText(CommonUtilsMethods.setConvertDate("yyyy-MM-dd", "dd MMM yyyy", groupModelClass.getGroupName()));

        if (groupModelClass.isExpanded()) {
            holder.constraintContent.setVisibility(View.VISIBLE);
            outBoxContentAdapter = new OutBoxContentAdapter(activity, context, groupModelClass.getChildItems());
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
            holder.rvContentList.setLayoutManager(mLayoutManager);
            holder.rvContentList.setAdapter(outBoxContentAdapter);
            holder.ivExpand.setImageResource(R.drawable.top_vector);
        } else {
            holder.constraintContent.setVisibility(View.GONE);
            holder.ivExpand.setImageResource(R.drawable.down_arrow);
        }

        holder.ivSync.setOnClickListener(v -> {
            if (UtilityClass.isNetworkAvailable(context)) {
                progressDialog = CommonUtilsMethods.createProgressDialog(context);
                CallOfflineData(groupModelClass, 0);
            } else {
                commonUtilsMethods.showToastMessage(context, context.getString(R.string.no_network));
            }
        });


        holder.cardView.setOnClickListener(v -> {
            groupModelClass.setExpanded(Objects.equals(holder.ivExpand.getDrawable().getConstantState(), Objects.requireNonNull(ContextCompat.getDrawable(context, R.drawable.down_arrow)).getConstantState()));
            notifyDataSetChanged();
        });
    }

    private void CallOfflineData(GroupModelClass groupModelClass, int childPos) {
        if (!groupModelClass.getChildItems().get(childPos).getCheckInOutModelClasses().isEmpty()) {
            isCallAvailable = false;
            for (int i = 0; i < groupModelClass.getChildItems().get(childPos).getCheckInOutModelClasses().size(); i++) {
                CheckInOutModelClass checkInOutModelClass = groupModelClass.getChildItems().get(childPos).getCheckInOutModelClasses().get(i);
                if (checkInOutModelClass.getCheckStatus() == 0) {
                    isCallAvailable = true;
                    Log.v("SendOutboxCall", "--CheckInOut--" + checkInOutModelClass.getDates() + "---" + checkInOutModelClass.getCheckInTime() + "---" + checkInOutModelClass.getCheckOutTime());
                    if (!checkInOutModelClass.getJsonOutValues().isEmpty()) {
                        CallSendCheckInOut(groupModelClass, checkInOutModelClass, childPos, i, checkInOutModelClass.getJsonOutValues());
                    } else {
                        CallSendCheckInOut(groupModelClass, checkInOutModelClass, childPos, i, checkInOutModelClass.getJsonInValues());
                    }
                }
                break;
            }
        } else {
            isCallAvailable = false;
        }

        if (!isCallAvailable) {
            CallAPIWorkPlan(groupModelClass, 1);
        }
    }

    private void CallSendCheckInOut(GroupModelClass groupModelClass, CheckInOutModelClass checkClass, int childPos, int i, String jsonOutValues) {
        String address = "";
        JSONObject obj = new JSONObject();
        try {
            obj = new JSONObject(jsonOutValues);
            address = CommonUtilsMethods.gettingAddress(activity, Double.parseDouble(obj.getString("lat")), Double.parseDouble(obj.getString("long")), false);
            obj.put("address", address);
        } catch (JSONException e) {
            Log.v("getAddress", "----" + e);
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
                            groupModelClass.getChildItems().get(childPos).getCheckInOutModelClasses().remove(i);
                        } else {
                            offlineCheckInOutDataDao.updateCheckInOutStatus(checkClass.getId(), 1);
                            checkClass.setCheckStatus(1);
                        }
                        CallOfflineData(groupModelClass, childPos);
                        notifyDataSetChanged();

                    } catch (Exception e) {
                        offlineCheckInOutDataDao.deleteOfflineCheckInOut(checkClass.getDates(), checkClass.getCheckCount());
                        groupModelClass.getChildItems().get(childPos).getCheckInOutModelClasses().remove(i);
                        CallOfflineData(groupModelClass, childPos);
                        notifyDataSetChanged();
                    }
                } else {
                    checkClass.setCheckStatus(1);
                    CallOfflineData(groupModelClass, childPos);
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                offlineCheckInOutDataDao.updateCheckInOutStatus(checkClass.getId(), 1);
                checkClass.setCheckStatus(1);
                CallOfflineData(groupModelClass, childPos);
            }
        });
    }

    private void CallAPIWorkPlan(GroupModelClass groupModelClass, int childPos) {
        if(groupModelClass.getChildItems().get(childPos).getWorkPlanModelClass() != null) {
            isCallAvailable = false;
            WorkPlanModelClass workPlanModelClass = groupModelClass.getChildItems().get(childPos).getWorkPlanModelClass();
            if(workPlanModelClass.getSyncStatus() == 0) {
                isCallAvailable = true;
                Log.v("SendOutboxCall", "--WorkPlan--" + workPlanModelClass.getDate() + "---" + workPlanModelClass.getWtName() + "---" + workPlanModelClass.getWtCode());
                if(!workPlanModelClass.getJsonValues().isEmpty()) {
                    CallSendWorkPlan(groupModelClass, workPlanModelClass, childPos, workPlanModelClass.getJsonValues());
                }
            }
        }else {
            isCallAvailable = false;
        }

        if(!isCallAvailable) {
            CallAPIOfflineCalls(groupModelClass, 2);
        }
    }

    private void CallSendWorkPlan(GroupModelClass groupModelClass, WorkPlanModelClass workPlanModelClass, int childPos, String jsonValues) {
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
                            groupModelClass.getChildItems().get(childPos).setWorkPlanModelClass(null);
                        }else {
                            offlineWorkTypeDataDao.updateWorkTypeStatus(workPlanModelClass.getId(), 1);
                            workPlanModelClass.setSyncStatus(1);
                        }
                        CallAPIWorkPlan(groupModelClass, childPos);
                        notifyDataSetChanged();
                    } catch (Exception ignored) {
                        offlineWorkTypeDataDao.delete(workPlanModelClass.getDate());
                        groupModelClass.getChildItems().get(childPos).setWorkPlanModelClass(null);
                        CallAPIWorkPlan(groupModelClass, childPos);
                        notifyDataSetChanged();
                    }
                }else {
                    workPlanModelClass.setSyncStatus(1);
                    CallAPIWorkPlan(groupModelClass, childPos);
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                Log.e("VALUES", String.valueOf(t));
                offlineWorkTypeDataDao.updateWorkTypeStatus(workPlanModelClass.getId(), 1);
                workPlanModelClass.setSyncStatus(1);
                CallAPIWorkPlan(groupModelClass, childPos);
            }
        });
    }

    private void CallAPIOfflineCalls(GroupModelClass groupModelClass, int childPos) {
        if (!groupModelClass.getChildItems().get(childPos).getOutBoxCallLists().isEmpty()) {
            isCallAvailable = false;
            for (int i = 0; i < groupModelClass.getChildItems().get(childPos).getOutBoxCallLists().size(); i++) {
                OutBoxCallList outBoxCallList = groupModelClass.getChildItems().get(childPos).getOutBoxCallLists().get(i);
                if (outBoxCallList.getStatus().equalsIgnoreCase(Constants.WAITING_FOR_SYNC) || outBoxCallList.getStatus().equalsIgnoreCase(Constants.CALL_FAILED)) {
                    if (outBoxCallList.getSyncCount() <= 4) {
                        isCallAvailable = true;
                        Log.v("SendOutboxCall", "---" + outBoxCallList.getCusName());
                        CallSendAPI(groupModelClass, outBoxCallList, childPos, i, outBoxCallList.getDates(), outBoxCallList.getCusName(), outBoxCallList.getCusCode(), outBoxCallList.getJsonData(), outBoxCallList.getSyncCount());
                        break;
                    }
                }
            }
        } else {
            isCallAvailable = false;
        }
        if (!isCallAvailable) {
            notifyDataSetChanged();
            CallAPIListImage(groupModelClass, 3);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void CallAPIListImage(GroupModelClass groupModelClass, int childPos) {
        if (!groupModelClass.getChildItems().get(childPos).getEcModelClasses().isEmpty()) {
            isCallAvailable = true;
            for (int i = 0; i < groupModelClass.getChildItems().get(childPos).getEcModelClasses().size(); i++) {
                EcModelClass ecModelClass = groupModelClass.getChildItems().get(childPos).getEcModelClasses().get(i);
                if (ecModelClass.getSynced() == 0) {
                    isCallAvailable = true;
                    Log.v("SendOutboxCall", "--image--" + ecModelClass.getDates() + "---" + ecModelClass.getImg_name());
                    Log.v("SendOutboxCall_______", "--image--" + ecModelClass.getJson_values());


                    CallSendAPIImage(groupModelClass, ecModelClass, childPos, i, ecModelClass.getJson_values(), ecModelClass.getFilePath(), String.valueOf(ecModelClass.getId()));
                    break;
                }
            }
        } else {
            isCallAvailable = false;
        }

        if (!isCallAvailable) {
            CallAPIDaySubmit(groupModelClass, 4);
        }
    }

    private void CallAPIDaySubmit(GroupModelClass groupModelClass, int childPos) {
        if(groupModelClass.getChildItems().get(childPos).getDaySubmitModelClass() != null) {
            isCallAvailable = false;
            DaySubmitModelClass daySubmitModelClass = groupModelClass.getChildItems().get(childPos).getDaySubmitModelClass();
            if(daySubmitModelClass.getSyncStatus() == 0) {
                isCallAvailable = true;
                Log.v("SendOutboxCall", "--Day Submit--" + daySubmitModelClass.getDate() + "---" + daySubmitModelClass.getJsonValues());
                if(!daySubmitModelClass.getJsonValues().isEmpty()) {
                    CallSendDaySubmit(groupModelClass, daySubmitModelClass, childPos, daySubmitModelClass.getJsonValues());
                }
            }
        }else {
            isCallAvailable = false;
        }

        if (!isCallAvailable) {
            Log.v("SendOutboxCall", "--finallyOut--");
            progressDialog.dismiss();
            if (CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd").equalsIgnoreCase(groupModelClass.getGroupName())) {
                CallsFragment.CallTodayCallsAPI(context, apiInterface, false);
            }
            CallDataRestClass.resetcallValues(context);
            RefreshAdapter();
        }
    }

    private void CallSendDaySubmit(GroupModelClass groupModelClass, DaySubmitModelClass daySubmitModelClass, int childPos, String jsonValues) {
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
                            groupModelClass.getChildItems().get(childPos).setDaySubmitModelClass(null);
                        }else {
                            offlineDaySubmitDao.updateDaySubmitStatus(daySubmitModelClass.getDate(), 1);
                            daySubmitModelClass.setSyncStatus(1);
                        }
                        CallAPIDaySubmit(groupModelClass, childPos);
                        notifyDataSetChanged();
                    } catch (Exception ignored) {
                        offlineDaySubmitDao.delete(daySubmitModelClass.getDate());
                        offlineDaySubmitDao.updateDaySubmitStatus(daySubmitModelClass.getDate(), 1);
                        CallAPIDaySubmit(groupModelClass, childPos);
                        notifyDataSetChanged();
                    }
                }else {
                    daySubmitModelClass.setSyncStatus(1);
                    CallAPIDaySubmit(groupModelClass, childPos);
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                offlineDaySubmitDao.updateDaySubmitStatus(daySubmitModelClass.getDate(), 1);
                daySubmitModelClass.setSyncStatus(1);
                CallAPIDaySubmit(groupModelClass, childPos);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void RefreshAdapter() {
        outBoxHeaderAdapter = new OutBoxHeaderAdapter(activity, context, listDates);
        CommonUtilsMethods commonUtilsMethods = new CommonUtilsMethods(context);
        commonUtilsMethods.recycleTestWithDivider(outBoxBinding.rvOutBoxHead);
        outBoxBinding.rvOutBoxHead.setAdapter(outBoxHeaderAdapter);
        outBoxHeaderAdapter.notifyDataSetChanged();
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

    private void CallSendAPIImage(GroupModelClass groupModelClass, EcModelClass ecModelClass, int childPos, int i, String jsonValues, String filePath, String id) {
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
                            DeleteCacheFile(groupModelClass, groupModelClass.getChildItems().get(childPos).getEcModelClasses().get(i).getFilePath(), id, i, childPos);
                        } else {
                            ecModelClass.setSynced(1);
                            ecModelClass.setSync_status(Constants.DUPLICATE_CALL);
                            callOfflineECDataDao.updateECStatus(id, Constants.DUPLICATE_CALL, 1);
                            CallAPIListImage(groupModelClass, childPos);
                        }
                    } catch (Exception e) {
                        Log.v("SendOutboxCall", "-error---" + e);
                        ecModelClass.setSynced(1);
                        ecModelClass.setSync_status(Constants.EXCEPTION_ERROR);
                        callOfflineECDataDao.updateECStatus(id, Constants.EXCEPTION_ERROR, 1);
                        CallAPIListImage(groupModelClass, childPos);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                ecModelClass.setSynced(1);
                ecModelClass.setSync_status(Constants.CALL_FAILED);
                callOfflineECDataDao.updateECStatus(id, Constants.CALL_FAILED, 1);
                CallAPIListImage(groupModelClass, childPos);
            }
        });
    }


    @SuppressLint("NotifyDataSetChanged")
    private void DeleteCacheFile(GroupModelClass groupModelClass, String filePath, String id, int i, int childPos) {
        File fileDelete = new File(filePath);
        if (fileDelete.exists()) {
            if (fileDelete.delete()) {
                System.out.println("file Deleted :" + filePath);
            } else {
                System.out.println("file not Deleted :" + filePath);
            }
        }
        callOfflineECDataDao.deleteOfflineEC(id);
        groupModelClass.getChildItems().get(childPos).getEcModelClasses().remove(i);
        CallAPIListImage(groupModelClass, childPos);
    }


    private void CallSendAPI(GroupModelClass groupModelClass, OutBoxCallList outBoxCallList, int childPos, int outBoxList, String date, String cusName, String cusCode, String jsonData, int SyncCount) {
        JSONObject jsonSaveDcr;
        try {
            jsonSaveDcr = new JSONObject(jsonData);
            Map<String, String> mapString = new HashMap<>();
            mapString.put("axn", "save/dcr");
            Call<JsonElement> callSaveDcr = apiInterface.getJSONElement(SharedPref.getCallApiUrl(context), mapString, jsonSaveDcr.toString());

            callSaveDcr.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    if (response.isSuccessful()) {
                        try {
                            JSONObject jsonSaveRes = new JSONObject(String.valueOf(response.body()));
                            if (jsonSaveRes.getString("success").equalsIgnoreCase("true") && jsonSaveRes.getString("msg").isEmpty()) {
                                callsUtil.deleteOfflineCalls(cusCode, cusName, date);
                                groupModelClass.getChildItems().get(childPos).getOutBoxCallLists().remove(outBoxList);
                            } else if (jsonSaveRes.getString("success").equalsIgnoreCase("false") && jsonSaveRes.getString("msg").equalsIgnoreCase("Call Already Exists")) {
                                callsUtil.updateOfflineUpdateStatusEC(date, cusCode, 5, Constants.DUPLICATE_CALL, 1);
                                groupModelClass.getChildItems().get(childPos).getOutBoxCallLists().set(outBoxList, new OutBoxCallList(cusName, cusCode, date, outBoxCallList.getIn(), outBoxCallList.getOut(), jsonData, outBoxCallList.getCusType(), Constants.DUPLICATE_CALL, 5));
                                JSONArray jsonArray = new JSONArray(masterDataDao.getDataByKey(Constants.CALL_SYNC));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    if (jsonObject.getString("Dcr_dt").equalsIgnoreCase(date) && jsonObject.getString("CustCode").equalsIgnoreCase(cusCode)) {
                                        jsonArray.remove(i);
                                        break;
                                    }
                                }
                                UpdateEcData(date, cusCode, cusName, Constants.DUPLICATE_CALL, 1);

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

                            }
                            CallAPIOfflineCalls(groupModelClass, childPos);
                            notifyDataSetChanged();
                        } catch (Exception e) {
                            callsUtil.updateOfflineUpdateStatusEC(date, cusCode, 5, Constants.EXCEPTION_ERROR, 0);
                            groupModelClass.getChildItems().get(childPos).getOutBoxCallLists().set(outBoxList, new OutBoxCallList(cusName, cusCode, date, outBoxCallList.getIn(), outBoxCallList.getOut(), jsonData, outBoxCallList.getCusType(), Constants.EXCEPTION_ERROR, 5));
                            UpdateEcData(date, cusCode, cusName, Constants.EXCEPTION_ERROR, 0);
                            CallAPIOfflineCalls(groupModelClass, childPos);
                            Log.v("SendOutboxCall", "---" + e);
                            notifyDataSetChanged();
                        }
                    }
                }

                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    callsUtil.updateOfflineUpdateStatusEC(date, cusCode, SyncCount + 1, Constants.CALL_FAILED, 1);
                    groupModelClass.getChildItems().get(childPos).getOutBoxCallLists().set(outBoxList, new OutBoxCallList(cusName, cusCode, date, outBoxCallList.getIn(), outBoxCallList.getOut(), jsonData, outBoxCallList.getCusType(), Constants.DUPLICATE_CALL, SyncCount + 1));
                    UpdateEcData(date, cusCode, cusName, Constants.CALL_FAILED, 1);
                    CallAPIOfflineCalls(groupModelClass, childPos);
                    notifyDataSetChanged();
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

    @Override
    public int getItemCount() {
        return groupModelClasses.size();
    }

    public static class listDataViewholider extends RecyclerView.ViewHolder {
        TextView tvDate;
        ImageView ivSync, ivExpand;
        ConstraintLayout constraintContent;
        RecyclerView rvContentList;
        CardView cardView;

        public listDataViewholider(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.text_date);
            ivSync = itemView.findViewById(R.id.img_sync_all);
            ivExpand = itemView.findViewById(R.id.txt_expand_status);
            constraintContent = itemView.findViewById(R.id.constraint_rv);
            rvContentList = itemView.findViewById(R.id.rv_outbox_list);
            cardView = itemView.findViewById(R.id.card_view_top);
        }
    }
}