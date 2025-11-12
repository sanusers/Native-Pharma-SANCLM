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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

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
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.activity.homeScreen.fragment.CallsFragment;
import saneforce.sanzen.activity.homeScreen.fragment.OutboxFragment;
import saneforce.sanzen.activity.homeScreen.modelClass.ActivityModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.ActivityUploadModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.CheckInOutModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.ChildListModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.DaySubmitModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.EcModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.GroupModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.OutBoxCallList;
//import saneforce.sanzen.activity.homeScreen.modelClass.SignModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.SignModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.WorkPlanModelClass;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.network.ApiCallback;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.ActivityOfflineTableDetails.ActivityOfflineDataDao;
import saneforce.sanzen.roomdatabase.ActivityUploadTableDetails.ActivityUploadDataDao;
import saneforce.sanzen.roomdatabase.CallDataRestClass;
import saneforce.sanzen.roomdatabase.CallOfflineECTableDetails.CallOfflineECDataDao;
//import saneforce.sanzen.roomdatabase.CallOfflineSignTableDetails.CallOfflineSignDataDao;
import saneforce.sanzen.roomdatabase.CallOfflineSignTableDetails.CallOfflineSignDataDao;
import saneforce.sanzen.roomdatabase.CallOfflineWorkTypeTableDetails.CallOfflineWorkTypeDataDao;
import saneforce.sanzen.roomdatabase.OutboxUtil;
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
    private final CallOfflineSignDataDao callOfflineSignDataDao;
     private final CallOfflineWorkTypeDataDao offlineWorkTypeDataDao;
     private final OfflineDaySubmitDao offlineDaySubmitDao;
     private final ActivityOfflineDataDao activityOfflineDataDao;
     private final ActivityUploadDataDao activityUploadDataDao;
     private final OutboxUtil outboxUtil;
     Util util;
    private int callSyncCount = 0;

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
        callOfflineSignDataDao = db.callOfflineSignDataDao();
        offlineWorkTypeDataDao = db.callOfflineWorkTypeDataDao();
        offlineDaySubmitDao = db.offlineDaySubmitDao();
        activityOfflineDataDao = db.activityOfflineDataDao();
        activityUploadDataDao = db.activityUploadDataDao();
        outboxUtil = new OutboxUtil(context);
        util = new Util();
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
        if (position == 0) {
            holder.view.setVisibility(View.GONE);
        } else {
            holder.view.setVisibility(View.VISIBLE);
        }
        GroupModelClass groupModelClass = groupModelClasses.get(position);
        if (!outboxUtil.checkIsDataAvailable(groupModelClass)) {
            outBoxBinding.rvOutBoxHead.post(() -> {
                listDates.remove(groupModelClass);
                notifyDataSetChanged();
            });
        } else {
            holder.tvDate.setText(CommonUtilsMethods.setConvertDate("yyyy-MM-dd", "dd MMM yyyy", groupModelClass.getGroupName()));

            if (groupModelClass.isExpanded()) {
                holder.constraintContent.setVisibility(View.VISIBLE);
                outBoxContentAdapter = new OutBoxContentAdapter(activity, context, groupModelClass.getChildItems(), groupModelClass.getGroupName());
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                holder.rvContentList.setLayoutManager(mLayoutManager);
                holder.rvContentList.setAdapter(outBoxContentAdapter);
                holder.ivExpand.setImageResource(R.drawable.top_vector);
            } else {
                holder.constraintContent.setVisibility(View.GONE);
                holder.ivExpand.setImageResource(R.drawable.down_arrow);
            }

            holder.ivSync.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View view) {
                    if (UtilityClass.isNetworkAvailable(context)) {
                        progressDialog = CommonUtilsMethods.createProgressDialog(context);
//                CallOfflineData(groupModelClass, 0);
                        processApisForDate(groupModelClass, 0, new ApiCallback() {
                            @Override
                            public void onSuccess() {
                                Log.v("SendOutboxCall", "--finallyOut--");
                                progressDialog.dismiss();
                                if (CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd").equalsIgnoreCase(groupModelClass.getGroupName())) {
                                    //      CallsFragment.CallTodayCallsAPI(context, apiInterface, false);
                                }
                                CallDataRestClass.resetcallValues(context);
                                RefreshAdapter();
                                if (callSyncCount > 0) {
                                    CallsFragment.syncCalls();
                                    callSyncCount = 0;
                                }
                                OutboxFragment.SetupOutBoxAdapter(activity, context);
                            }

                            @Override
                            public void onFailure() {
                                stopSync();
                                progressDialog.dismiss();
                            }
                        });
                    } else {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.no_network));
                    }
                }
            });


            holder.cardView.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View view) {
                    groupModelClass.setExpanded(Objects.equals(holder.ivExpand.getDrawable().getConstantState(), Objects.requireNonNull(ContextCompat.getDrawable(context, R.drawable.down_arrow)).getConstantState()));
                    notifyDataSetChanged();
                }
            });
        }
    }

    private void stopSync() {
        System.out.println("❌ Sync stopped due to API failure.");
        if (callSyncCount > 0) {
            CallsFragment.syncCalls();
            callSyncCount = 0;
        }
        OutboxFragment.SetupOutBoxAdapter(activity, context);
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
                processApisForDate(dateGroup, apiIndex + 1, callback);
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
                checkInSubmitAPI(child, 0, callback);
                break;

            case 1:
                workPlanSubmitAPI(child, callback);
                break;

            case 2:
                callSubmitAPI(child,0, 0, callback);
                break;

            case 3:
                eventCaptureSubmitAPI(child,0, callback);
                break;

            case 4:
                signatureSubmitAPI(child,0, callback);
                break;

            case 5:
                activitySubmitAPI(child,0, callback);
                break;

            case 6:
                activityUploadSubmitAPI(child,0, callback);
                break;

            case 7:
                daySubmitAPI(child, callback);
                break;

            default:
                callback.onSuccess();
                break;
        }
    }

    private void checkInSubmitAPI(ChildListModelClass child, int index, ApiCallback callback) {
        String address = "";
        ArrayList<CheckInOutModelClass> checkInOutModelClasses= child.getCheckInOutModelClasses();
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
            address = CommonUtilsMethods.gettingAddress(activity, Double.parseDouble(obj.getString("lat")), Double.parseDouble(obj.getString("long")), false);
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
                            notifyDataSetChanged();
                            checkInSubmitAPI(child, index, callback);
                        } else {
                            offlineCheckInOutDataDao.updateCheckInOutStatus(checkInOutModelClass.getId(), 1);
                            checkInOutModelClass.setCheckStatus(1);
                            notifyDataSetChanged();
                            callback.onFailure();
                        }
                    } catch (Exception e) {
                        offlineCheckInOutDataDao.deleteOfflineCheckInOut(checkInOutModelClass.getDates(), checkInOutModelClass.getCheckCount());
                        checkInOutModelClasses.remove(checkInOutModelClass);
                        notifyDataSetChanged();
                        callback.onFailure();
                    }


                } else {
                    checkInOutModelClass.setCheckStatus(1);
                    notifyDataSetChanged();
                    callback.onFailure();
                }
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                offlineCheckInOutDataDao.updateCheckInOutStatus(checkInOutModelClass.getId(), 1);
                checkInOutModelClass.setCheckStatus(1);
                notifyDataSetChanged();
                callback.onFailure();
            }
        });
    }

    private void workPlanSubmitAPI(ChildListModelClass child, ApiCallback callback) {
        WorkPlanModelClass workPlanModelClass = child.getWorkPlanModelClass();
        if (workPlanModelClass == null || workPlanModelClass.getSyncStatus() != 0) {
            callback.onSuccess();
            return;
        }
        Map<String, String> mapString = new HashMap<>();
        if (SharedPref.getSfType(context).equalsIgnoreCase("1") || SharedPref.getOneBuild(context).equalsIgnoreCase("0")) {
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
                            notifyDataSetChanged();
                            callback.onSuccess();
                        } else {
                            offlineWorkTypeDataDao.updateWorkTypeStatus(workPlanModelClass.getId(), 1);
                            workPlanModelClass.setSyncStatus(1);
                            notifyDataSetChanged();
                            callback.onFailure();
                        }
                    } catch (Exception ignored) {
                        offlineWorkTypeDataDao.updateWorkTypeStatus(workPlanModelClass.getId(), 1);
                        workPlanModelClass.setSyncStatus(1);
                        notifyDataSetChanged();
                        callback.onFailure();
                    }
                } else {
                    offlineWorkTypeDataDao.updateWorkTypeStatus(workPlanModelClass.getId(), 1);
                    workPlanModelClass.setSyncStatus(1);
                    notifyDataSetChanged();
                    callback.onFailure();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                offlineWorkTypeDataDao.updateWorkTypeStatus(workPlanModelClass.getId(), 1);
                workPlanModelClass.setSyncStatus(1);
                notifyDataSetChanged();
                callback.onFailure();
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
                                outboxUtil.deleteOfflineCalls(outBoxCallList.getCusCode(), outBoxCallList.getCusName(), outBoxCallList.getDates());
                                callsList.remove(outBoxCallList);
                                notifyDataSetChanged();
                                callSyncCount++;
                                callSubmitAPI(child, index, 0, callback);
                            } else if (jsonSaveRes.getString("success").equalsIgnoreCase("false") && jsonSaveRes.getString("msg").equalsIgnoreCase("Call Already Exists")) {
                                outboxUtil.updateOfflineUpdateStatusEC(outBoxCallList.getDates(), outBoxCallList.getCusCode(), 5, Constants.DUPLICATE_CALL, 1);
                                outBoxCallList.setStatus(Constants.DUPLICATE_CALL);
                                outBoxCallList.setSyncCount(5);
                                UpdateEcData(outBoxCallList.getDates(), outBoxCallList.getCusCode(), outBoxCallList.getCusName(), Constants.DUPLICATE_CALL, 1);
                                DeleteUpdateDcrTable(outBoxCallList.getDates(), outBoxCallList.getCusCode(), outBoxCallList.getCusType());
                                notifyDataSetChanged();
                                callback.onSuccess();
                            } else if (jsonSaveRes.getString("success").equalsIgnoreCase("false")) {
                                if (jsonSaveRes.has("msg")) {
                                    outboxUtil.updateOfflineUpdateStatusEC(outBoxCallList.getDates(), outBoxCallList.getCusCode(), 5, jsonSaveRes.getString("msg"), 1);
                                    outBoxCallList.setStatus(jsonSaveRes.getString("msg"));
                                    outBoxCallList.setSyncCount(5);
                                    UpdateEcData(outBoxCallList.getDates(), outBoxCallList.getCusCode(), outBoxCallList.getCusName(), jsonSaveRes.getString("msg"), 1);
                                    DeleteUpdateDcrTable(outBoxCallList.getDates(), outBoxCallList.getCusCode(), outBoxCallList.getCusType());
                                    notifyDataSetChanged();
                                    callback.onFailure();
                                } else if (jsonSaveRes.has("Msg")) {
                                    outboxUtil.updateOfflineUpdateStatusEC(outBoxCallList.getDates(), outBoxCallList.getCusCode(), 5, jsonSaveRes.getString("Msg"), 1);
                                    outBoxCallList.setStatus(jsonSaveRes.getString("Msg"));
                                    outBoxCallList.setSyncCount(5);
                                    UpdateEcData(outBoxCallList.getDates(), outBoxCallList.getCusCode(), outBoxCallList.getCusName(), jsonSaveRes.getString("Msg"), 1);
                                    DeleteUpdateDcrTable(outBoxCallList.getDates(), outBoxCallList.getCusCode(), outBoxCallList.getCusType());
                                    notifyDataSetChanged();
                                    callback.onFailure();
                                }
                            }
                        } catch (Exception e) {
                            outboxUtil.updateOfflineUpdateStatusEC(outBoxCallList.getDates(), outBoxCallList.getCusCode(), 5, Constants.EXCEPTION_ERROR, 0);
                            outBoxCallList.setStatus(Constants.EXCEPTION_ERROR);
                            outBoxCallList.setSyncCount(5);
                            UpdateEcData(outBoxCallList.getDates(), outBoxCallList.getCusCode(), outBoxCallList.getCusName(), Constants.EXCEPTION_ERROR, 0);
                            e.printStackTrace();
                            notifyDataSetChanged();
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
                    notifyDataSetChanged();
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

    private void eventCaptureSubmitAPI(ChildListModelClass child, int index, ApiCallback callback) {
        ArrayList<EcModelClass> callsImageList = child.getEcModelClasses();
        if (callsImageList == null || callsImageList.isEmpty() || index >= callsImageList.size()) {
            callback.onSuccess();
            return;
        }
        EcModelClass ecModelClass = callsImageList.get(index);
        if (ecModelClass.getSynced() == 0) {
            if (SharedPref.getS3BucketNeed(context).equalsIgnoreCase("0")) {
                CallSendAPIImageS3(child, index, ecModelClass, callback);
            } else {
                CallSendAPIImage(child, index, ecModelClass, callback);
            }
        } else {
            eventCaptureSubmitAPI(child, index + 1, callback);
        }
    }

    private void CallSendAPIImageS3(ChildListModelClass child, int index, EcModelClass ecModelClass, ApiCallback callback) {
        try {
            util.getS3Client(context);
            String bucketName = "san-one";
            File fileToUpload = new File(ecModelClass.getFilePath());
            Log.d("fileToUpload", "CallImageAPI: " + fileToUpload.getAbsolutePath());
            if (!fileToUpload.exists()) {
                Log.d("fileToUpload", "not exists: " + ecModelClass.getFilePath());
                callback.onSuccess();
            } else {
                String s3Key = "uploads/"+SharedPref.getDivisionSname(context)+SharedPref.getDivisionCode(context).replace(",", "/") + "Event_Capture" + "/" + fileToUpload.getName();
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
                            notifyDataSetChanged();
                            eventCaptureSubmitAPI(child, index, callback);
                        } else if (state == TransferState.FAILED) {
                            Log.e("S3 Upload", "Upload Failed");
                            InsertImage(ecModelClass.getFilePath(), context);
                            ecModelClass.setSynced(1);
                            ecModelClass.setSync_status(Constants.CALL_FAILED);
                            callOfflineECDataDao.updateECStatus(String.valueOf(ecModelClass.getId()), Constants.CALL_FAILED, 1);
                            notifyDataSetChanged();
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
                        notifyDataSetChanged();
                        callback.onFailure();
                    }
                });

            }
        } catch (Exception e) {
            Log.v("img_tag", e.toString());
            ecModelClass.setSynced(1);
            ecModelClass.setSync_status(Constants.EXCEPTION_ERROR);
            callOfflineECDataDao.updateECStatus(String.valueOf(ecModelClass.getId()), Constants.EXCEPTION_ERROR, 1);
            notifyDataSetChanged();
            callback.onFailure();
        }
    }

    private void CallSendAPIImage(ChildListModelClass child, int index, EcModelClass ecModelClass, ApiCallback callback) {
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
                            notifyDataSetChanged();
                            eventCaptureSubmitAPI(child, index, callback);
                        } else {
                            ecModelClass.setSynced(1);
                            ecModelClass.setSync_status(Constants.DUPLICATE_CALL);
                            callOfflineECDataDao.updateECStatus(String.valueOf(ecModelClass.getId()), Constants.DUPLICATE_CALL, 1);
                            notifyDataSetChanged();
                            callback.onSuccess();
                        }
                    } catch (Exception e) {
                        Log.v("SendOutboxCall", "-error---" + e);
                        ecModelClass.setSynced(1);
                        ecModelClass.setSync_status(Constants.EXCEPTION_ERROR);
                        callOfflineECDataDao.updateECStatus(String.valueOf(ecModelClass.getId()), Constants.EXCEPTION_ERROR, 1);
                        notifyDataSetChanged();
                        callback.onFailure();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                ecModelClass.setSynced(1);
                ecModelClass.setSync_status(Constants.CALL_FAILED);
                callOfflineECDataDao.updateECStatus(String.valueOf(ecModelClass.getId()), Constants.CALL_FAILED, 1);
                notifyDataSetChanged();
                callback.onFailure();
            }
        });
    }

    private void signatureSubmitAPI(ChildListModelClass child, int index, ApiCallback callback) {
        ArrayList<SignModelClass> callsSignList = child.getSignModelClasses();
        if (callsSignList == null || callsSignList.isEmpty() || index >= callsSignList.size()) {
            callback.onSuccess();
            return;
        }
        SignModelClass signModelClass = callsSignList.get(index);

        if (signModelClass.getSynced() == 0) {
            if (SharedPref.getS3BucketNeed(context).equalsIgnoreCase("0")) {
                CallSendAPIImageS3(child, index, signModelClass, callback);
            } else {
                CallSendAPISignImage(child, index, signModelClass, callback);
            }
        } else {
            signatureSubmitAPI(child, index + 1, callback);
        }
    }

    private void CallSendAPIImageS3(ChildListModelClass child, int index, SignModelClass signModelClass, ApiCallback callback) {
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
                    String s3Key = "uploads/"+SharedPref.getDivisionSname(context)+SharedPref.getDivisionCode(context).replace(",", "/") + "Signature" + "/" + fileToUpload.getName();

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
                                notifyDataSetChanged();
                                signatureSubmitAPI(child, index, callback);
                            } else if (state == TransferState.FAILED) {
                                Log.e("S3 Upload", "Upload Failed");
                                InsertImageSign(signModelClass.getFilePath(), context);
                                signModelClass.setSynced(1);
                                signModelClass.setSync_status(Constants.CALL_FAILED);
                                callOfflineSignDataDao.updateSignStatus(String.valueOf(signModelClass.getId()), Constants.CALL_FAILED, 1);
                                notifyDataSetChanged();
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
                            signModelClass.setSync_status(Constants.EXCEPTION_ERROR);
                            callOfflineSignDataDao.updateSignStatus(String.valueOf(signModelClass.getId()), Constants.EXCEPTION_ERROR, 1);
                            notifyDataSetChanged();
                            callback.onFailure();
                        }
                    });
                }

            }
        } catch (Exception e) {
            Log.v("img_tagOF", e.toString());
            signModelClass.setSynced(1);
            signModelClass.setSync_status(Constants.EXCEPTION_ERROR);
            callOfflineSignDataDao.updateSignStatus(String.valueOf(signModelClass.getId()), Constants.EXCEPTION_ERROR, 1);
            notifyDataSetChanged();
            callback.onFailure();
        }
    }

    private void CallSendAPISignImage(ChildListModelClass child, int index, SignModelClass signModelClass, ApiCallback callback) {
        ApiInterface apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getTagApiImageUrl(context));
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
                            notifyDataSetChanged();
                            signatureSubmitAPI(child, index, callback);
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
                callback.onFailure();
            }
        });
    }

    private void activitySubmitAPI(ChildListModelClass child, int index, ApiCallback callback) {
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
                                    notifyDataSetChanged();
                                    callback.onSuccess();
                                } else {
                                    outboxUtil.updateStatusActivity(activityModelClass.getId(), 5, Constants.FAILED);
                                    activityModelClass.setSyncStatus(Constants.FAILED);
                                    activityModelClass.setSyncCount(5);
                                    notifyDataSetChanged();
                                    callback.onFailure();
                                }
                            } catch (Exception e) {
                                outboxUtil.updateStatusActivity(activityModelClass.getId(), 5, Constants.EXCEPTION_ERROR);
                                activityModelClass.setSyncStatus(Constants.EXCEPTION_ERROR);
                                activityModelClass.setSyncCount(5);
                                Log.v("SendOutboxCall", "---" + e);
                                e.printStackTrace();
                                notifyDataSetChanged();
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
                        notifyDataSetChanged();
                        callback.onFailure();
                    }
                });

            }
        } catch(Exception e){
            e.printStackTrace();
            callback.onFailure();
        }
    }

    private void activityUploadSubmitAPI(ChildListModelClass child, int index, ApiCallback callback) {
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
                                notifyDataSetChanged();
                                callback.onSuccess();
                            } else {
                                outboxUtil.updateStatusActivity(activityUploadModelClass.getActivityID(), 5, Constants.FAILED);
                                activityUploadModelClass.setSyncStatus(Constants.FAILED);
                                activityUploadModelClass.setSyncCount(5);
                                notifyDataSetChanged();
                                callback.onFailure();
                            }
                        } catch (Exception e) {
                            outboxUtil.updateStatusActivity(activityUploadModelClass.getActivityID(), 5, Constants.EXCEPTION_ERROR);
                            activityUploadModelClass.setSyncStatus(Constants.EXCEPTION_ERROR);
                            activityUploadModelClass.setSyncCount(5);
                            Log.v("SendOutboxCall", "---" + e);
                            e.printStackTrace();
                            notifyDataSetChanged();
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
                    notifyDataSetChanged();
                    callback.onFailure();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            callback.onFailure();
        }
//        }
    }

    private void daySubmitAPI(ChildListModelClass child, ApiCallback callback) {
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
                                notifyDataSetChanged();
                                callback.onSuccess();
                            } else {
                                offlineDaySubmitDao.updateDaySubmitStatus(daySubmitModelClass.getDate(), 1);
                                daySubmitModelClass.setSyncStatus(1);
                                notifyDataSetChanged();
                                callback.onFailure();
                            }
                        } catch (Exception ignored) {
                            offlineDaySubmitDao.delete(daySubmitModelClass.getDate());
                            offlineDaySubmitDao.updateDaySubmitStatus(daySubmitModelClass.getDate(), 1);
                            notifyDataSetChanged();
                            callback.onFailure();
                        }
                    } else {
                        daySubmitModelClass.setSyncStatus(1);
                        notifyDataSetChanged();
                        callback.onFailure();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    offlineDaySubmitDao.updateDaySubmitStatus(daySubmitModelClass.getDate(), 1);
                    daySubmitModelClass.setSyncStatus(1);
                    callback.onFailure();
                }
            });
        } else {
            callback.onSuccess();
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
        } catch (Exception e) {
            e.printStackTrace();
        }
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
        if(SharedPref.getSfType(context).equalsIgnoreCase("1")) {
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
            isCallAvailable = false;
            for (int i = 0; i < groupModelClass.getChildItems().get(childPos).getEcModelClasses().size(); i++) {
                EcModelClass ecModelClass = groupModelClass.getChildItems().get(childPos).getEcModelClasses().get(i);
                if (ecModelClass.getSynced() == 0) {
                    isCallAvailable = true;
                    Log.v("SendOutboxCall", "--image--" + ecModelClass.getDates() + "---" + ecModelClass.getImg_name());
                    Log.v("SendOutboxCall", "--image--" + ecModelClass.getDates() + "---" + ecModelClass.getFilePath());
                    Log.v("SendOutboxCall_______", "--image--" + ecModelClass.getJson_values());
                    if(SharedPref.getS3BucketNeed(context).equalsIgnoreCase("0")) {
                        CallSendAPIImageS3(ecModelClass, childPos, i, ecModelClass.getJson_values(), ecModelClass.getFilePath(), String.valueOf(ecModelClass.getId()), groupModelClass);
                    }else {
                        CallSendAPIImage(groupModelClass, ecModelClass, childPos, i, ecModelClass.getJson_values(), ecModelClass.getFilePath(), String.valueOf(ecModelClass.getId()));
                    }
                    break;
                }
            }
        } else {
            isCallAvailable = false;
        }

        if (!isCallAvailable) {
            notifyDataSetChanged();
            CallApiSignImage(groupModelClass,4);

        }
    }

    public void CallApiSignImage(GroupModelClass groupModelClass, int childPos){
        if(!groupModelClass.getChildItems().get(childPos).getSignModelClasses().isEmpty()){
            isCallAvailable = false;
            for(int i=0; i< groupModelClass.getChildItems().get(childPos).getSignModelClasses().size(); i++){
                SignModelClass signModelClass = groupModelClass.getChildItems().get(childPos).getSignModelClasses().get(i);
                if(signModelClass.getSynced()==0){
                    isCallAvailable = true;
                    if(SharedPref.getS3BucketNeed(context).equalsIgnoreCase("0")){
                        CallSendSignImageS3(groupModelClass,signModelClass,childPos,i,signModelClass.getJson_values(),signModelClass.getFilePath(),String.valueOf(signModelClass.getId()));
                    }
                    CallSendSignImage(groupModelClass,signModelClass,childPos,i,signModelClass.getJson_values(),signModelClass.getFilePath(),String.valueOf(signModelClass.getId()));
                    break;
                }
            }
        }else{
            isCallAvailable = false;
        }
        if(!isCallAvailable){
            CallAPIOfflineActivity(groupModelClass, 5);
        }
    }

    private void CallAPIOfflineActivity(GroupModelClass groupModelClass, int childPos) {
        if (!groupModelClass.getChildItems().get(childPos).getActivityModelClasses().isEmpty()) {
            isCallAvailable = false;
            for (int i = 0; i < groupModelClass.getChildItems().get(childPos).getActivityModelClasses().size(); i++) {
                ActivityModelClass activityModelClass = groupModelClass.getChildItems().get(childPos).getActivityModelClasses().get(i);
                if (activityModelClass.getSyncStatus().equalsIgnoreCase(Constants.WAITING_FOR_SYNC) || activityModelClass.getSyncStatus().equalsIgnoreCase(Constants.FAILED)) {
                    if (activityModelClass.getSyncCount() <= 4) {
                        isCallAvailable = true;
                        Log.v("SendOutboxCall", "---" + activityModelClass.getName() + " -> " + activityModelClass.getActivityDate() + " - " + activityModelClass.getActivityTime());
                        CallSendActivityAPI(groupModelClass, activityModelClass, childPos, i);
                        break;
                    }
                }
            }
        } else {
            isCallAvailable = false;
        }
        if (!isCallAvailable) {
            CallAPIDaySubmit(groupModelClass, 7);
            notifyDataSetChanged();
            CallAPIActivityUpload(groupModelClass, 6);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void CallAPIActivityUpload(GroupModelClass groupModelClass, int childPos) {
        if (!groupModelClass.getChildItems().get(childPos).getActivityUploadModelClasses().isEmpty()) {
            isCallAvailable = true;
            for (int i = 0; i < groupModelClass.getChildItems().get(childPos).getActivityUploadModelClasses().size(); i++) {
                ActivityUploadModelClass activityUploadModelClass = groupModelClass.getChildItems().get(childPos).getActivityUploadModelClasses().get(i);
                if (activityUploadModelClass.getSyncCount() == 0) {
                    isCallAvailable = true;
                    Log.v("SendOutboxCall", "--image--" + activityUploadModelClass.getActivityDate() + "---" + activityUploadModelClass.getImageName() + activityUploadModelClass.getActivityID());
                    Log.v("SendOutboxCall_______", "--image--" + activityUploadModelClass.getJsonData());
                    CallSendActivityUploadAPI(groupModelClass, activityUploadModelClass, childPos, i);
                    break;
                }
            }
        } else {
            isCallAvailable = false;
        }
        if (!isCallAvailable) {
            CallAPIDaySubmit(groupModelClass,7);
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
          //      CallsFragment.CallTodayCallsAPI(context, apiInterface, false);
            }
            CallDataRestClass.resetcallValues(context);
            RefreshAdapter();
        }
    }

    private void CallSendActivityAPI(GroupModelClass groupModelClass, ActivityModelClass activityModelClass, int childPos, int outBoxListIndex) {
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
                                groupModelClass.getChildItems().get(childPos).getActivityModelClasses().remove(activityModelClass);
                            } else {
                                outboxUtil.updateStatusActivity(activityModelClass.getId(), 5, Constants.FAILED);
                                activityModelClass.setSyncStatus(Constants.FAILED);
                                activityModelClass.setSyncCount(5);
                                groupModelClass.getChildItems().get(childPos).getActivityModelClasses().set(outBoxListIndex, activityModelClass);
                            }
                            CallAPIOfflineActivity(groupModelClass, childPos);
                            notifyDataSetChanged();
                        } catch (Exception e) {
                            outboxUtil.updateStatusActivity(activityModelClass.getId(), 5, Constants.EXCEPTION_ERROR);
                            activityModelClass.setSyncStatus(Constants.EXCEPTION_ERROR);
                            activityModelClass.setSyncCount(5);
                            groupModelClass.getChildItems().get(childPos).getActivityModelClasses().set(outBoxListIndex, activityModelClass);
                            Log.v("SendOutboxCall", "---" + e);
                            e.printStackTrace();
                            CallAPIOfflineActivity(groupModelClass, childPos);
                            notifyDataSetChanged();
                        }
                    }
                }

                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    outboxUtil.updateStatusActivity(activityModelClass.getId(), activityModelClass.getSyncCount() + 1, Constants.FAILED);
                    activityModelClass.setSyncStatus(Constants.FAILED);
                    activityModelClass.setSyncCount(activityModelClass.getSyncCount() + 1);
                    groupModelClass.getChildItems().get(childPos).getActivityModelClasses().set(outBoxListIndex, activityModelClass);
                    CallAPIOfflineActivity(groupModelClass, childPos);
                    notifyDataSetChanged();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void CallSendActivityUploadAPI(GroupModelClass groupModelClass, ActivityUploadModelClass activityUploadModelClass, int position, int outBoxListIndex) {
        try {
            File file = new File(activityUploadModelClass.getFilePath());
            MultipartBody.Part img = convertImg("ActivityFile", String.valueOf(file));
            HashMap<String, RequestBody> values = field(activityUploadModelClass.getJsonData());
            Call<JsonObject> saveAttachment = apiInterface.SaveImg(values, img);
            saveAttachment.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                    if(response.isSuccessful()) {
                        try {
                            JSONObject jsonSaveRes = new JSONObject(String.valueOf(response.body()));
                            if(jsonSaveRes.getString("success").equalsIgnoreCase("true")) {
                                activityUploadDataDao.deleteUploadActivity(activityUploadModelClass.getId(), activityUploadModelClass.getActivityID());
                                groupModelClass.getChildItems().get(position).getActivityUploadModelClasses().remove(activityUploadModelClass);
                            }else {
                                outboxUtil.updateStatusActivity(activityUploadModelClass.getActivityID(), 5, Constants.FAILED);
                                activityUploadModelClass.setSyncStatus(Constants.FAILED);
                                activityUploadModelClass.setSyncCount(5);
                                groupModelClass.getChildItems().get(position).getActivityUploadModelClasses().set(outBoxListIndex, activityUploadModelClass);
                            }
                            CallAPIActivityUpload(groupModelClass, position);
                            notifyDataSetChanged();
                        } catch (Exception e) {
                            outboxUtil.updateStatusActivity(activityUploadModelClass.getActivityID(), 5, Constants.EXCEPTION_ERROR);
                            activityUploadModelClass.setSyncStatus(Constants.EXCEPTION_ERROR);
                            activityUploadModelClass.setSyncCount(5);
                            Log.v("SendOutboxCall", "---" + e);
                            e.printStackTrace();
                            CallAPIActivityUpload(groupModelClass, position);
                            notifyDataSetChanged();
                        }
                    }
                }

                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable throwable) {
                    outboxUtil.updateStatusActivity(activityUploadModelClass.getActivityID(), activityUploadModelClass.getSyncCount() + 1, Constants.FAILED);
                    activityUploadModelClass.setSyncStatus(Constants.FAILED);
                    activityUploadModelClass.setSyncCount(activityUploadModelClass.getSyncCount() + 1);
                    groupModelClass.getChildItems().get(position).getActivityUploadModelClasses().set(outBoxListIndex, activityUploadModelClass);
                    CallAPIActivityUpload(groupModelClass, position);
                    notifyDataSetChanged();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
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

    // Event Capture S3
    private void CallSendAPIImageS3(EcModelClass ecModelClass, int childPos, int CurrentPos,
                                  String jsonValues, String filePath, String id, GroupModelClass modelClass) {
        try {
            util.getS3Client(context);
//            String bucketName = "san-edet";
            String bucketName = "san-one";
            if(!filePath.isEmpty()) {
                File fileToUpload = new File(filePath);
                Log.d("fileToUpload", "CallImageAPI: " + fileToUpload.getAbsolutePath());
                if (!fileToUpload.exists()) {
                    Log.d("fileToUpload", "not exists: " + filePath);
                } else {
//                    String s3Key = SharedPref.getDivisionCode(context).replace(",", "/") + "Event_Capture" + "/" + fileToUpload.getName();
                    String s3Key = "uploads/"+SharedPref.getDivisionSname(context)+SharedPref.getDivisionCode(context).replace(",", "/") + "Event_Capture" + "/" + fileToUpload.getName();
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
                                    DeleteCacheFile(filePath, id, CurrentPos, childPos, modelClass);
                                    Log.d("S3 Upload", "Upload Successful: " + s3Key);
                                    try {
                                        modelClass.getChildItems().get(childPos).getEcModelClasses().remove(CurrentPos);
                                        CallAPIListImage(modelClass, childPos);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else if (state == TransferState.FAILED) {
                                    Log.e("S3 Upload", "Upload Failed");
                                    InsertImage(ecModelClass.getFilePath(), context);
                                    ecModelClass.setSynced(1);
                                    ecModelClass.setSync_status(Constants.CALL_FAILED);
                                    callOfflineECDataDao.updateECStatus(id, Constants.CALL_FAILED, 1);
                                    CallAPIListImage(modelClass, childPos);
                                    try {
                                        callOfflineECDataDao.updateECStatus(id, Constants.CALL_FAILED, 1);
                                        CallAPIListImage(modelClass, childPos);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    ecModelClass.setSynced(1);
                                    ecModelClass.setSync_status(Constants.DUPLICATE_CALL);
                                    callOfflineECDataDao.updateECStatus(id, Constants.DUPLICATE_CALL, 1);
                                    CallAPIListImage(modelClass, childPos);
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
                                    CallAPIListImage(modelClass, childPos);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                } else {
                    Log.d("Filepath", "CallSendAPIImage: " + "file path in adap is empty");
                }
            } catch (Exception e) {
                Log.v("img_tag", e.toString());
                ecModelClass.setSynced(1);
                ecModelClass.setSync_status(Constants.EXCEPTION_ERROR);
                callOfflineECDataDao.updateECStatus(id, Constants.EXCEPTION_ERROR, 1);
                try {
                    CallAPIListImage(modelClass, childPos);
                } catch (Exception a) {
                    a.printStackTrace();
                }
            }
//        }
    }

    private void InsertImage(final String ImageUrl, Context context) {
        File imageFile = new File(ImageUrl);
        Log.d("AWS_s3", "fileToUpload" + "--" + imageFile);
        String fileName = new File(ImageUrl).getName();
        new AWSBuckets(context,fileName,imageFile,"");
    }

    @SuppressLint("NotifyDataSetChanged")
    private void DeleteCacheFile(String filePath, String id, int currentPos, int childPos, GroupModelClass modelClass) {
        try {
            File fileDelete = new File(filePath);
            if(fileDelete.exists()) {
                if(fileDelete.delete()) {
//                System.out.println("file Deleted :" + filePath);
                }else {
//                System.out.println("file not Deleted :" + filePath);
                }
            }
            callOfflineECDataDao.deleteOfflineEC(id);
            try {
                modelClass.getChildItems().get(childPos).getEcModelClasses().remove(currentPos);
                CallAPIListImage(modelClass, childPos);
            } catch (Exception a) {
                a.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
//                System.out.println("file Deleted :" + filePath);
            } else {
//                System.out.println("file not Deleted :" + filePath);
            }
        }
        callOfflineECDataDao.deleteOfflineEC(id);
        groupModelClass.getChildItems().get(childPos).getEcModelClasses().remove(i);
        CallAPIListImage(groupModelClass, childPos);
    }


    public void CallSendSignImageS3(GroupModelClass groupModelClass,SignModelClass signModelClass,int childPos,int i,String jsonValues,String filePath,String id){
        try {
            util.getS3Client(context);
//            String bucketName = "san-edet";
            String bucketName = "san-one";
            if(!filePath.isEmpty()) {
                File fileToUpload = new File(filePath);
                Log.d("fileToUpload", "CallImageAPI: " + fileToUpload.getAbsolutePath());
                if (fileToUpload.toString().isEmpty()) {
                    Log.d("fileToUploadSignHeader", "not exists: " + filePath);
                } else {

//                    String s3Key = SharedPref.getDivisionCode(context).replace(",","/")+"Signature"+"/"+ fileToUpload.getName();
                    String s3Key = "uploads/"+SharedPref.getDivisionSname(context)+SharedPref.getDivisionCode(context).replace(",", "/") + "Signature" + "/" + fileToUpload.getName();

                    if(s3Key.contains(null)){
                        Log.d("s3Key", "CallSendSignImage: "+"s3key is null");
                    }
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
                                Log.d("TAG", "ecModelClass: " + filePath);
                                InsertImageSign(signModelClass.getFilePath(), context);
                                DeleteCacheFileSign(groupModelClass,filePath, id,i, childPos );
                                Log.d("S3 Upload", "Upload Successful: " + s3Key);
                                try {
                                    groupModelClass.getChildItems().get(childPos).getSignModelClasses().remove(0);
                                    CallApiSignImage(groupModelClass, childPos);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (state == TransferState.FAILED) {

                                Log.e("S3 Upload", "Upload Failed");
                                InsertImageSign(signModelClass.getFilePath(), context);
                                signModelClass.setSynced(1);
                                signModelClass.setSync_status(Constants.CALL_FAILED);
                                try {
                                    callOfflineSignDataDao.updateSignStatus(id, Constants.CALL_FAILED, 1);
                                    CallApiSignImage(groupModelClass, childPos);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
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
                                CallApiSignImage(groupModelClass, childPos);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }else{
                Log.d("Filepath", "CallSendAPIImage: "+"file path in adap is empty");
            }
        } catch(Exception e){
            Log.v("img_tagOHA", e.toString());
            signModelClass.setSynced(1);
            signModelClass.setSync_status(Constants.EXCEPTION_ERROR);
            callOfflineSignDataDao.updateSignStatus(id, Constants.EXCEPTION_ERROR, 1);
            try {
                CallApiSignImage(groupModelClass, childPos);
            } catch (Exception a) {
                a.printStackTrace();
            }
        }
    }

    private void CallSendSignImage(GroupModelClass groupModelClass,SignModelClass signModelClass,int childPos,int i,String jsonValues,String filePath,String id){
        ApiInterface apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getTagApiImageUrl(context));
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
                            DeleteCacheFileSign(groupModelClass, groupModelClass.getChildItems().get(childPos).getSignModelClasses().get(i).getFilePath(), id, i, childPos);
                        } else {
                            signModelClass.setSynced(1);
                            signModelClass.setSync_status(Constants.DUPLICATE_CALL);
                            callOfflineSignDataDao.updateSignStatus(id, Constants.CALL_FAILED, 1);
                            CallAPIListImage(groupModelClass, childPos);
                        }
                    } catch (Exception e) {
                        Log.v("SendOutboxCall", "-error---" + e);
                        signModelClass.setSynced(1);
                        signModelClass.setSync_status(Constants.EXCEPTION_ERROR);
                        callOfflineSignDataDao.updateSignStatus(id, Constants.CALL_FAILED, 1);
                        CallApiSignImage(groupModelClass, childPos);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                signModelClass.setSynced(1);
                signModelClass.setSync_status(Constants.CALL_FAILED);
                callOfflineSignDataDao.updateSignStatus(id, Constants.CALL_FAILED, 1);
                CallApiSignImage(groupModelClass, childPos);
            }
        });
    }


    private void InsertImageSign(final String ImageUrl, Context context) {
        File imageFile = new File(ImageUrl);
        Log.d("AWS_s3", "fileToUpload" + "--" + imageFile);
        String fileName = new File(ImageUrl).getName();
        new AWSBucketsSign(context,fileName,imageFile,"");
    }

    @SuppressLint("NotifyDataSetChanged")
    private void DeleteCacheFileSign(GroupModelClass groupModelClass, String filePath, String id, int i, int childPos) {
        File fileDelete = new File(filePath);
        if (fileDelete.exists()) {
            if (fileDelete.delete()) {
                System.out.println("file Deleted :" + filePath);
            } else {
                System.out.println("file not Deleted :" + filePath);
            }
        }
        callOfflineSignDataDao.deleteOfflineSignImage(filePath);
        try{
            groupModelClass.getChildItems().get(childPos).getSignModelClasses().remove(i);
        } catch (Exception e) {
            e.printStackTrace();
        }

        CallApiSignImage(groupModelClass, childPos);
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
                                outboxUtil.deleteOfflineCalls(cusCode, cusName, date);
                                groupModelClass.getChildItems().get(childPos).getOutBoxCallLists().remove(outBoxList);
                            } else if (jsonSaveRes.getString("success").equalsIgnoreCase("false") && jsonSaveRes.getString("msg").equalsIgnoreCase("Call Already Exists")) {
                                outboxUtil.updateOfflineUpdateStatusEC(date, cusCode, 5, Constants.DUPLICATE_CALL, 1);
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
                            } else if(jsonSaveRes.getString("success").equalsIgnoreCase("false")) {
                                if(jsonSaveRes.has("msg")) {
                                    outboxUtil.updateOfflineUpdateStatusEC(date, cusCode, 5, jsonSaveRes.getString("msg"), 1);
                                    groupModelClass.getChildItems().get(childPos).getOutBoxCallLists().set(outBoxList, new OutBoxCallList(cusName, cusCode, date, outBoxCallList.getIn(), outBoxCallList.getOut(), jsonData, outBoxCallList.getCusType(), jsonSaveRes.getString("msg"), 5));
                                    JSONArray jsonArray = new JSONArray(masterDataDao.getDataByKey(Constants.CALL_SYNC));
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        if (jsonObject.getString("Dcr_dt").equalsIgnoreCase(date) && jsonObject.getString("CustCode").equalsIgnoreCase(cusCode)) {
                                            jsonArray.remove(i);
                                            break;
                                        }
                                    }
                                    UpdateEcData(date, cusCode, cusName, jsonSaveRes.getString("msg"), 1);

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
                                } else if(jsonSaveRes.has("Msg")) {
                                    outboxUtil.updateOfflineUpdateStatusEC(date, cusCode, 5, jsonSaveRes.getString("Msg"), 1);
                                    groupModelClass.getChildItems().get(childPos).getOutBoxCallLists().set(outBoxList, new OutBoxCallList(cusName, cusCode, date, outBoxCallList.getIn(), outBoxCallList.getOut(), jsonData, outBoxCallList.getCusType(), jsonSaveRes.getString("msg"), 5));
                                    JSONArray jsonArray = new JSONArray(masterDataDao.getDataByKey(Constants.CALL_SYNC));
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        if (jsonObject.getString("Dcr_dt").equalsIgnoreCase(date) && jsonObject.getString("CustCode").equalsIgnoreCase(cusCode)) {
                                            jsonArray.remove(i);
                                            break;
                                        }
                                    }
                                    UpdateEcData(date, cusCode, cusName, jsonSaveRes.getString("Msg"), 1);

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
                            }
                            CallAPIOfflineCalls(groupModelClass, childPos);
                            notifyDataSetChanged();
                        } catch (Exception e) {
                            outboxUtil.updateOfflineUpdateStatusEC(date, cusCode, 5, Constants.EXCEPTION_ERROR, 0);
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
                    outboxUtil.updateOfflineUpdateStatusEC(date, cusCode, SyncCount + 1, Constants.CALL_FAILED, 1);
                    groupModelClass.getChildItems().get(childPos).getOutBoxCallLists().set(outBoxList, new OutBoxCallList(cusName, cusCode, date, outBoxCallList.getIn(), outBoxCallList.getOut(), jsonData, outBoxCallList.getCusType(), Constants.DUPLICATE_CALL, SyncCount + 1));
                    UpdateEcData(date, cusCode, cusName, Constants.CALL_FAILED, 1);
                    CallAPIOfflineCalls(groupModelClass, childPos);
                    notifyDataSetChanged();
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
        View view;

        public listDataViewholider(@NonNull View itemView) {
            super(itemView);
            tvDate = itemView.findViewById(R.id.text_date);
            ivSync = itemView.findViewById(R.id.img_sync_all);
            ivExpand = itemView.findViewById(R.id.txt_expand_status);
            constraintContent = itemView.findViewById(R.id.constraint_rv);
            rvContentList = itemView.findViewById(R.id.rv_outbox_list);
            cardView = itemView.findViewById(R.id.card_view_top);
            view = itemView.findViewById(R.id.date_divider);
        }
    }
}
