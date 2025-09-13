package saneforce.sanzen.activity.homeScreen.adapters.outbox;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;
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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
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
import saneforce.sanzen.activity.homeScreen.modelClass.ActivityModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.ActivityUploadModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.CheckInOutModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.ChildListModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.DaySubmitModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.EcModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.OutBoxCallList;
//import saneforce.sanzen.activity.homeScreen.modelClass.SignModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.SignModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.WorkPlanModelClass;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.Keys;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.roomdatabase.ActivityOfflineTableDetails.ActivityOfflineDataDao;
import saneforce.sanzen.roomdatabase.ActivityUploadTableDetails.ActivityUploadDataDao;
import saneforce.sanzen.roomdatabase.CallDataRestClass;
import saneforce.sanzen.roomdatabase.CallOfflineECTableDetails.CallOfflineECDataDao;
//import saneforce.sanzen.roomdatabase.CallOfflineSignTableDetails.CallOfflineSignDataDao;
import saneforce.sanzen.roomdatabase.CallOfflineSignTableDetails.CallOfflineSignDataDao;
import saneforce.sanzen.roomdatabase.CallOfflineWorkTypeTableDetails.CallOfflineWorkTypeDataDao;
import saneforce.sanzen.roomdatabase.CallsUtil;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.sanzen.roomdatabase.OfflineCheckInOutTableDetails.OfflineCheckInOutDataDao;
import saneforce.sanzen.roomdatabase.OfflineDaySubmit.OfflineDaySubmitDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class OutBoxContentAdapter extends RecyclerView.Adapter<OutBoxContentAdapter.listDataViewholider> {
    Context context;
    ArrayList<ChildListModelClass> childListModelClasses;
    OutBoxCallAdapter outBoxCallAdapter;
    ApiInterface apiInterface;
    OutBoxCheckInOutAdapter outBoxCheckInOutAdapter;
    OutBoxHeaderAdapter outBoxHeaderAdapter;
    OutBoxECAdapter outBoxECAdapter;
    OutBoxSignAdapter outBoxSignAdapter;
    OutBoxActivityAdapter outBoxActivityAdapter;
    OutBoxActivityUploadAdapter outBoxActivityUploadAdapter;
    ProgressDialog progressDialog;
    boolean isAvailable;
    CommonUtilsMethods commonUtilsMethods;
    Activity activity;
    RoomDB roomDB;

    MasterDataDao masterDataDao;
    private final OfflineCheckInOutDataDao offlineCheckInOutDataDao;
    private final CallOfflineECDataDao callOfflineECDataDao;
    private final CallOfflineSignDataDao callOfflineSignDataDao;
    private final OfflineDaySubmitDao offlineDaySubmitDao;
    private final CallOfflineWorkTypeDataDao callOfflineWorkTypeDataDao;
    private final ActivityOfflineDataDao activityOfflineDataDao;
    private final ActivityUploadDataDao activityUploadDataDao;
    private final CallsUtil callsUtil;
    Util util;
    boolean isCallAvailable;

    public OutBoxContentAdapter(Activity activity, Context context, ArrayList<ChildListModelClass> groupModelClasses) {
        this.activity = activity;
        this.context = context;
        this.childListModelClasses = groupModelClasses;
        apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));
        commonUtilsMethods = new CommonUtilsMethods(context);

        roomDB=RoomDB.getDatabase(context);
        masterDataDao=roomDB.masterDataDao();
        offlineCheckInOutDataDao = roomDB.offlineCheckInOutDataDao();
        callOfflineECDataDao = roomDB.callOfflineECDataDao();
        callOfflineSignDataDao = roomDB.callOfflineSignDataDao();
        callOfflineWorkTypeDataDao = roomDB.callOfflineWorkTypeDataDao();
        offlineDaySubmitDao = roomDB.offlineDaySubmitDao();
        activityOfflineDataDao = roomDB.activityOfflineDataDao();
        activityUploadDataDao = roomDB.activityUploadDataDao();
        callsUtil = new CallsUtil(context);
        util = new Util();
    }

    @NonNull
    @Override
    public OutBoxContentAdapter.listDataViewholider onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.outbox_content_view, parent, false);
        return new OutBoxContentAdapter.listDataViewholider(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull OutBoxContentAdapter.listDataViewholider holder, int position) {
        ChildListModelClass contentList = childListModelClasses.get(position);
        Log.v("outBox", "---" + contentList.getChildName() + "--count--" + childListModelClasses.size() + "----" + contentList.isAvailableList() + "---" + contentList.getCounts());

        holder.tvContentList.setText(contentList.getChildName());

        if (contentList.isAvailableList()) {
            holder.expandContentView.setEnabled(true);
            holder.img_expand_child.setVisibility(View.VISIBLE);
            holder.tvCount.setVisibility(View.VISIBLE);
        } else {
            holder.expandContentView.setEnabled(false);
            holder.constraintRv.setVisibility(View.GONE);
            holder.img_expand_child.setVisibility(View.GONE);
            holder.tvCount.setVisibility(View.GONE);
            holder.tvCount.setText("-");
        }

        switch (contentList.getChildId()) {
            case 0:
                if (contentList.getCheckInOutModelClasses() == null) {
                    holder.expandContentView.setVisibility(View.GONE);
                } else {
                    holder.expandContentView.setVisibility(View.VISIBLE);
                }
                holder.tvCount.setText(String.valueOf(contentList.getCheckInOutModelClasses().size()));
                if (contentList.isExpanded() && !contentList.getCheckInOutModelClasses().isEmpty()) {
                    SetupVisibleData(holder.constraintRv, contentList.getChildId(), holder.rv_outbox_list, holder.img_expand_child, contentList.getOutBoxCallLists(), contentList.getCheckInOutModelClasses(), contentList.getEcModelClasses(), contentList.getSignModelClasses(),contentList.getActivityModelClasses(), contentList.getActivityUploadModelClasses());
                } else {
                    holder.constraintRv.setVisibility(View.GONE);
                    holder.img_expand_child.setImageResource(R.drawable.down_arrow);
                }
                break;
            case 1:
                if (contentList.getWorkPlanModelClass() == null) {
                    holder.expandContentView.setVisibility(View.GONE);
                } else {
                    holder.expandContentView.setVisibility(View.VISIBLE);
                }
                break;
            case 2:
                if (contentList.getOutBoxCallLists() == null) {
                    holder.expandContentView.setVisibility(View.GONE);
                } else {
                    holder.expandContentView.setVisibility(View.VISIBLE);
                }
                holder.tvCount.setText(String.valueOf(contentList.getOutBoxCallLists().size()));
                if (contentList.isExpanded() && !contentList.getOutBoxCallLists().isEmpty()) {
                    SetupVisibleData(holder.constraintRv, contentList.getChildId(), holder.rv_outbox_list, holder.img_expand_child, contentList.getOutBoxCallLists(), contentList.getCheckInOutModelClasses(), contentList.getEcModelClasses(), contentList.getSignModelClasses(),contentList.getActivityModelClasses(), contentList.getActivityUploadModelClasses());
                } else {
                    holder.constraintRv.setVisibility(View.GONE);
                    holder.img_expand_child.setImageResource(R.drawable.down_arrow);
                }
                break;
            case 3:
                if (contentList.getEcModelClasses() == null) {
                    holder.expandContentView.setVisibility(View.GONE);
                } else {
                    holder.expandContentView.setVisibility(View.VISIBLE);
                }
                holder.tvCount.setText(String.valueOf(contentList.getEcModelClasses().size()));
                if (contentList.isExpanded() && !contentList.getEcModelClasses().isEmpty()) {
                    SetupVisibleData(holder.constraintRv, contentList.getChildId(), holder.rv_outbox_list, holder.img_expand_child, contentList.getOutBoxCallLists(), contentList.getCheckInOutModelClasses(), contentList.getEcModelClasses(),contentList.getSignModelClasses(),contentList.getActivityModelClasses(), contentList.getActivityUploadModelClasses());
                } else {
                    holder.constraintRv.setVisibility(View.GONE);
                    holder.img_expand_child.setImageResource(R.drawable.down_arrow);
                }
                break;
            case 4:

                if (contentList.getSignModelClasses() == null) {
                    holder.expandContentView.setVisibility(View.GONE);
                } else {
                    holder.expandContentView.setVisibility(View.VISIBLE);
                }
                holder.tvCount.setText(String.valueOf(contentList.getSignModelClasses().size()));
                if(contentList.isExpanded() && !contentList.getSignModelClasses().isEmpty()){
                    SetupVisibleData(holder.constraintRv, contentList.getChildId(), holder.rv_outbox_list, holder.img_expand_child, contentList.getOutBoxCallLists(), contentList.getCheckInOutModelClasses(), contentList.getEcModelClasses(), contentList.getSignModelClasses(),contentList.getActivityModelClasses(), contentList.getActivityUploadModelClasses());
                }else{
                    holder.constraintMain.setVisibility(View.GONE);
                    holder.img_expand_child.setImageResource(R.drawable.down_arrow);
                }
                break;
            case 5:
                if (contentList.getActivityModelClasses() == null) {
                    holder.expandContentView.setVisibility(View.GONE);
                } else {
                    holder.expandContentView.setVisibility(View.VISIBLE);
                }
                holder.tvCount.setText(String.valueOf(contentList.getActivityModelClasses().size()));
                if(contentList.isExpanded() && !contentList.getActivityModelClasses().isEmpty()) {
                    SetupVisibleData(holder.constraintRv, contentList.getChildId(), holder.rv_outbox_list, holder.img_expand_child, contentList.getOutBoxCallLists(), contentList.getCheckInOutModelClasses(), contentList.getEcModelClasses(), contentList.getSignModelClasses(),contentList.getActivityModelClasses(), contentList.getActivityUploadModelClasses());
                } else {
                    holder.constraintRv.setVisibility(View.GONE);
                    holder.img_expand_child.setImageResource(R.drawable.down_arrow);
                }
                break;
            case 6:
                if (contentList.getActivityUploadModelClasses() == null) {
                    holder.expandContentView.setVisibility(View.GONE);
                } else {
                    holder.expandContentView.setVisibility(View.VISIBLE);
                }
                holder.tvCount.setText(String.valueOf(contentList.getActivityUploadModelClasses().size()));
                if(contentList.isExpanded() && !contentList.getActivityUploadModelClasses().isEmpty()) {
                    SetupVisibleData(holder.constraintRv, contentList.getChildId(), holder.rv_outbox_list, holder.img_expand_child, contentList.getOutBoxCallLists(), contentList.getCheckInOutModelClasses(), contentList.getEcModelClasses(), contentList.getSignModelClasses(),contentList.getActivityModelClasses(), contentList.getActivityUploadModelClasses());
                } else {
                    holder.constraintRv.setVisibility(View.GONE);
                    holder.img_expand_child.setImageResource(R.drawable.down_arrow);
                }
                break;
            case 7:
                if (contentList.getDaySubmitModelClass() == null) {
                    holder.expandContentView.setVisibility(View.GONE);
                } else {
                    holder.expandContentView.setVisibility(View.VISIBLE);
                }
            default:
                break;
        }

        if (holder.tvCount.getText().toString().equalsIgnoreCase("0")) {
            holder.constraintMain.setVisibility(View.GONE);
        } else {
            holder.constraintMain.setVisibility(View.VISIBLE);
        }

        holder.sync.setOnClickListener(v -> {
            if (UtilityClass.isNetworkAvailable(context)) {
                progressDialog = CommonUtilsMethods.createProgressDialog(context);
                switch (contentList.getChildId()) {
                    case 0:
                        CallAPICheckInOut(position);
                        break;
                    case 1:
                        CallAPIWorkPlan(position);
                        break;
                    case 2:
                        CallAPIList(position);
                        break;
                    case 3:
                        CallAPIListImage(position);
                        break;
                    case 4:
                        CallApiSignImage(position);
                        break;
                    case 5:
                        CallAPIActivity(position);
                        break;
                    case 6:
                        CallAPIActivityUpload(position);
                        break;
                    case 7:
                        CallAPIDaySubmit(position);
                        break;
                }
            } else {
                commonUtilsMethods.showToastMessage(context, context.getString(R.string.no_network));
            }
        });

        holder.expandContentView.setOnClickListener(v -> {
            contentList.setExpanded(Objects.equals(holder.img_expand_child.getDrawable().getConstantState(), Objects.requireNonNull(ContextCompat.getDrawable(context, R.drawable.down_arrow)).getConstantState()));
            notifyDataSetChanged();
        });
    }

    private void SetupVisibleData(ConstraintLayout constraintRv, int childId, RecyclerView rvOutboxList, ImageView imgExpandChild, ArrayList<OutBoxCallList> outBoxCallLists, ArrayList<CheckInOutModelClass> checkInOutModelClasses, ArrayList<EcModelClass> ecModelClasses, ArrayList<SignModelClass>signModelClasses, ArrayList<ActivityModelClass> activityModelClasses, ArrayList<ActivityUploadModelClass> activityUploadModelClasses) {
        constraintRv.setVisibility(View.VISIBLE);
        RecyclerView.LayoutManager mLayoutManager;
        switch (childId) {
            case 0:
                outBoxCheckInOutAdapter = new OutBoxCheckInOutAdapter(activity, context, checkInOutModelClasses);
                mLayoutManager = new GridLayoutManager(context, 2);
                rvOutboxList.setLayoutManager(mLayoutManager);
                rvOutboxList.setAdapter(outBoxCheckInOutAdapter);
                break;
            case 2:
                outBoxCallAdapter = new OutBoxCallAdapter(activity, context, outBoxCallLists, apiInterface);
                mLayoutManager = new LinearLayoutManager(context);
                rvOutboxList.setLayoutManager(mLayoutManager);
                rvOutboxList.setAdapter(outBoxCallAdapter);
                break;
            case 3:
                outBoxECAdapter = new OutBoxECAdapter(activity, context, ecModelClasses);
                mLayoutManager = new LinearLayoutManager(context);
                rvOutboxList.setLayoutManager(mLayoutManager);
                rvOutboxList.setAdapter(outBoxECAdapter);
                break;
            case 4:
                outBoxSignAdapter = new OutBoxSignAdapter(activity,context,signModelClasses);
                mLayoutManager = new LinearLayoutManager(context);
                rvOutboxList.setLayoutManager(mLayoutManager);
                rvOutboxList.setAdapter(outBoxSignAdapter);

                break;
            case 5:
                outBoxActivityAdapter = new OutBoxActivityAdapter(activity, context, activityModelClasses, apiInterface);
                mLayoutManager = new LinearLayoutManager(context);
                rvOutboxList.setLayoutManager(mLayoutManager);
                rvOutboxList.setAdapter(outBoxActivityAdapter);
                break;
            case 6:
                outBoxActivityUploadAdapter = new OutBoxActivityUploadAdapter(activity, context, activityUploadModelClasses, apiInterface);
                mLayoutManager = new LinearLayoutManager(context);
                rvOutboxList.setLayoutManager(mLayoutManager);
                rvOutboxList.setAdapter(outBoxActivityUploadAdapter);
                break;
        }
        imgExpandChild.setImageResource(R.drawable.top_vector);
    }

    private void CallAPICheckInOut(int position) {
        if (!childListModelClasses.get(position).getCheckInOutModelClasses().isEmpty()) {
            isAvailable = false;
            for (int i = 0; i < childListModelClasses.get(position).getCheckInOutModelClasses().size(); i++) {
                CheckInOutModelClass checkInOutModelClass = childListModelClasses.get(position).getCheckInOutModelClasses().get(i);
                Log.v("SendOutboxCall", "--checkInOut--" + checkInOutModelClass.getDates() + "---" + checkInOutModelClass.getCheckInTime() + "----" + checkInOutModelClass.getCheckOutTime());
                if (checkInOutModelClass.getCheckStatus() == 0) {
                    isAvailable = true;
                    if (checkInOutModelClass.getJsonOutValues().isEmpty()) {
                        CallSendAPICheckInOut(position, i, checkInOutModelClass, checkInOutModelClass.getJsonInValues());
                    } else {
                        CallSendAPICheckInOut(position, i, checkInOutModelClass, checkInOutModelClass.getJsonOutValues());
                    }
                }
                break;
            }
        } else {
            isAvailable = false;
        }

        if (!isAvailable) {
            progressDialog.dismiss();
            RefreshAdapter();
        }
    }

    private void CallSendAPICheckInOut(int position, int i, CheckInOutModelClass checkClass, String jsonValues) {
        String address = "";
        JSONObject obj = new JSONObject();
        try {
            obj = new JSONObject(jsonValues);
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
                            childListModelClasses.get(position).getCheckInOutModelClasses().remove(i);
                        } else {
                            offlineCheckInOutDataDao.updateCheckInOutStatus(checkClass.getId(), 1);
                            checkClass.setCheckStatus(1);
                        }
                        CallAPICheckInOut(position);

                    } catch (Exception e) {
                        offlineCheckInOutDataDao.deleteOfflineCheckInOut(checkClass.getDates(), checkClass.getCheckCount());
                        childListModelClasses.get(position).getCheckInOutModelClasses().remove(i);
                        CallAPICheckInOut(position);
                    }
                } else {
                    checkClass.setCheckStatus(1);
                    CallAPICheckInOut(position);
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                offlineCheckInOutDataDao.updateCheckInOutStatus(checkClass.getId(), 1);
                checkClass.setCheckStatus(1);
                CallAPICheckInOut(position);
            }
        });
    }

    private void CallAPIWorkPlan(int childPos) {
        if(childListModelClasses.get(childPos).getWorkPlanModelClass() != null) {
            isAvailable = false;
            WorkPlanModelClass workPlanModelClass = childListModelClasses.get(childPos).getWorkPlanModelClass();
            if(workPlanModelClass.getSyncStatus() == 0) {
                isAvailable = true;
                Log.v("SendOutboxCall", "--WorkPlan--" + workPlanModelClass.getDate() + "---" + workPlanModelClass.getWtName() + "---" + workPlanModelClass.getWtCode());
                if(!workPlanModelClass.getJsonValues().isEmpty()) {
                    CallSendWorkPlan(workPlanModelClass, childPos, workPlanModelClass.getJsonValues());
                }
            }
        }else {
            isAvailable = false;
        }

        if(!isAvailable) {
            progressDialog.dismiss();
        }
    }

    private void CallSendWorkPlan(WorkPlanModelClass workPlanModelClass, int childPos, String jsonValues) {
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
                            callOfflineWorkTypeDataDao.delete(workPlanModelClass.getDate());
                            childListModelClasses.get(childPos).setWorkPlanModelClass(null);
                        }else {
                            callOfflineWorkTypeDataDao.updateWorkTypeStatus(workPlanModelClass.getId(), 1);
                            workPlanModelClass.setSyncStatus(1);
                        }
                        CallAPIWorkPlan(childPos);
                        notifyDataSetChanged();
                    } catch (Exception ignored) {
                        callOfflineWorkTypeDataDao.delete(workPlanModelClass.getDate());
                        childListModelClasses.get(childPos).setWorkPlanModelClass(null);
                        CallAPIWorkPlan(childPos);
                        notifyDataSetChanged();
                    }
                }else {
                    workPlanModelClass.setSyncStatus(1);
                    CallAPIWorkPlan(childPos);
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                callOfflineWorkTypeDataDao.updateWorkTypeStatus(workPlanModelClass.getId(), 1);
                workPlanModelClass.setSyncStatus(1);
                CallAPIWorkPlan(childPos);
            }
        });
    }

    private void CallAPIDaySubmit(int childPos) {
        if(childListModelClasses.get(childPos).getDaySubmitModelClass() != null) {
            isAvailable = false;
            DaySubmitModelClass daySubmitModelClass = childListModelClasses.get(childPos).getDaySubmitModelClass();
            if(daySubmitModelClass.getSyncStatus() == 0) {
                isAvailable = true;
                Log.v("SendOutboxCall", "--Day Submit--" + daySubmitModelClass.getDate() + "---" + daySubmitModelClass.getJsonValues());
                if(!daySubmitModelClass.getJsonValues().isEmpty()) {
                    CallSendDaySubmit(daySubmitModelClass, childPos, daySubmitModelClass.getJsonValues());
                }
            }
        }else {
            isAvailable = false;
        }
        if (!isAvailable) {
            progressDialog.dismiss();
        }
    }

    private void CallSendDaySubmit(DaySubmitModelClass daySubmitModelClass, int childPos, String jsonValues) {
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
                            childListModelClasses.get(childPos).setDaySubmitModelClass(null);
                        }else {
                            offlineDaySubmitDao.updateDaySubmitStatus(daySubmitModelClass.getDate(), 1);
                            daySubmitModelClass.setSyncStatus(1);
                        }
                        CallAPIDaySubmit(childPos);
                        notifyDataSetChanged();
                    } catch (Exception ignored) {
                        offlineDaySubmitDao.delete(daySubmitModelClass.getDate());
                        offlineDaySubmitDao.updateDaySubmitStatus(daySubmitModelClass.getDate(), 1);
                        CallAPIDaySubmit(childPos);
                        notifyDataSetChanged();
                    }
                }else {
                    daySubmitModelClass.setSyncStatus(1);
                    CallAPIDaySubmit(childPos);
                    notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                offlineDaySubmitDao.updateDaySubmitStatus(daySubmitModelClass.getDate(), 1);
                daySubmitModelClass.setSyncStatus(1);
                CallAPIDaySubmit(childPos);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void CallAPIListImage(int position) {
        if (!childListModelClasses.get(position).getEcModelClasses().isEmpty()) {
            isAvailable = false;
            for (int i = 0; i < childListModelClasses.get(position).getEcModelClasses().size(); i++) {
                EcModelClass ecModelClass = childListModelClasses.get(position).getEcModelClasses().get(i);
                if (ecModelClass.getSynced() == 0) {
                    Log.v("SendOutboxCall", "--image--" + ecModelClass.getDates() + "---" + ecModelClass.getImg_name());
                    Log.v("SendOutboxCall-----", "--image--" + ecModelClass.getDates() + "---" + ecModelClass.getJson_values());
                    if(SharedPref.getS3BucketNeed(context).equalsIgnoreCase("0")) {
                        CallSendAPIImageS3(position, i, ecModelClass, ecModelClass.getJson_values(), ecModelClass.getFilePath(), String.valueOf(ecModelClass.getId()));
                    }else{
                        CallSendAPIImage(position, i, ecModelClass, ecModelClass.getJson_values(), ecModelClass.getFilePath(), String.valueOf(ecModelClass.getId()));
                    }
                    CallSendAPIImage(position, i, ecModelClass, ecModelClass.getJson_values(), ecModelClass.getFilePath(), String.valueOf(ecModelClass.getId()));

                }
                break;
            }
        } else {
            isAvailable = false;
        }
        if (!isAvailable) {
            progressDialog.dismiss();
        }
    }
    private void CallApiSignImage(int position){
        if(!childListModelClasses.get(position).getSignModelClasses().isEmpty()){
            isCallAvailable = false;
            for(int i = 0; i < childListModelClasses.get(position).getSignModelClasses().size();i++){
                SignModelClass signModelClass = childListModelClasses.get(position).getSignModelClasses().get(i);
                if(signModelClass.getSynced() == 0){
                    if(SharedPref.getS3BucketNeed(context).equalsIgnoreCase("0")){
                        CallSendSignImageS3(position,i,signModelClass,signModelClass.getJson_values(),signModelClass.getFilePath(), String.valueOf(signModelClass.getId()));
                    }
                    CallSendSignImage(position,i,signModelClass,signModelClass.getJson_values(),signModelClass.getFilePath(), String.valueOf(signModelClass.getId()));
                }
                break;
            }
        }else{
            isCallAvailable = false;
        }
        if (!isCallAvailable){
            progressDialog.dismiss();
            RefreshAdapter();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void RefreshAdapter() {
        outBoxHeaderAdapter = new OutBoxHeaderAdapter(activity, context, listDates);
        CommonUtilsMethods commonUtilsMethods = new CommonUtilsMethods(context);
        commonUtilsMethods.recycleTestWithDivider(outBoxBinding.rvOutBoxHead);
        outBoxBinding.rvOutBoxHead.setAdapter(outBoxHeaderAdapter);
        outBoxHeaderAdapter.notifyDataSetChanged();
    }

    private void CallSendAPIImage(int position, int i, EcModelClass ecModelClass, String jsonValues, String filePath, String id) {
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
                            DeleteCacheFile(filePath, id, i, position);
                        } else {
                            ecModelClass.setSynced(1);
                            ecModelClass.setSync_status(Constants.DUPLICATE_CALL);
                            callOfflineECDataDao.updateECStatus(id, Constants.DUPLICATE_CALL, 1);
                            CallAPIListImage(position);
                        }
                        if (!childListModelClasses.get(position).getEcModelClasses().isEmpty()) {
                            RefreshAdapter();
                        }
                    } catch (Exception e) {
                        Log.v("SendOutboxCall", "-error-ec--" + e);
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
            }
        });
    }
private void CallSendAPIImageS3(int position,int i,EcModelClass ecModelClass,String jsonValues, String filePath, String id) {
    try {
        util.getS3Client(context);
        String bucketName = "san-edet";
        File fileToUpload = new File(filePath);
        Log.d("fileToUpload", "CallImageAPI: " + fileToUpload.getAbsolutePath());
        if (!fileToUpload.exists()) {
            Log.d("fileToUpload", "not exists: " + filePath);
        } else {


                String s3Key = SharedPref.getDivisionCode(context).replace(",", "/") + "Event_Capture" + "/" + fileToUpload.getName();
                Log.d("TAG", "CallSendAPIImage: " + s3Key);

            /*String UploadUrl = "https://" + "s3." +"eu-north-1." + "amazonaws.com/" + bucketName + "/" + s3Key;
            Log.d("S3UploadUrl", "CallSendAPIImage: " + UploadUrl);*/

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
                            DeleteCacheFile(filePath, id, i, position);
                            Log.d("S3 Upload", "Upload Successful: " + s3Key);
                            try {
                                CallAPIListImage(position);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else if (state == TransferState.FAILED) {

                            Log.e("S3 Upload", "Upload Failed");
                            InsertImage(ecModelClass.getFilePath(), context);

                            ecModelClass.setSynced(1);
                            ecModelClass.setSync_status(Constants.CALL_FAILED);
                            try {
                                callOfflineECDataDao.updateECStatus(id, Constants.CALL_FAILED, 1);
                                CallAPIListImage(position);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            ecModelClass.setSynced(1);
                            ecModelClass.setSync_status(Constants.DUPLICATE_CALL);
                            callOfflineECDataDao.updateECStatus(id, Constants.DUPLICATE_CALL, 1);
                            CallAPIListImage(position);
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
                            CallAPIListImage(position);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

            }
        } catch (Exception e) {
            Log.v("img_tag", e.toString());
            ecModelClass.setSynced(1);
            ecModelClass.setSync_status(Constants.EXCEPTION_ERROR);
            callOfflineECDataDao.updateECStatus(id, Constants.EXCEPTION_ERROR, 1);
            try {
                CallAPIListImage(position);
            } catch (Exception a) {
                a.printStackTrace();
            }
        }

}

    private void InsertImage(final String ImageUrl, Context context) {
        File imageFile = new File(ImageUrl);
        Log.d("AWS_s3", "fileToUpload" + "--" + imageFile);
        String fileName = new File(ImageUrl).getName();
        new AWSBuckets(context,fileName,imageFile,"");
    }


    @SuppressLint("NotifyDataSetChanged")
    private void DeleteCacheFile(String filePath, String id, int i, int position) {
        File fileDelete = new File(filePath);
        if (fileDelete.exists()) {
            if (fileDelete.delete()) {
//                System.out.println("file Deleted :" + filePath);
            } else {
//                System.out.println("file not Deleted :" + filePath);
            }
        }
        callOfflineECDataDao.deleteOfflineEC(id);
        childListModelClasses.get(position).getEcModelClasses().remove(i);
        CallAPIListImage(position);
    }

    private void CallSendSignImageS3(int position,int i ,SignModelClass signModelClass, String jsonValues, String filePath, String id) {
        try {
            util.getS3Client(context);
            String bucketName = "san-edet";
            if (!filePath.isEmpty()) {
                File fileToUpload = new File(filePath);
                Log.d("fileToUpload", "CallImageAPI: " + fileToUpload.getAbsolutePath());
                if (fileToUpload.toString().isEmpty()) {
                    Log.d("fileToUploadSignContent", "not exists: " + filePath);
                } else {
                    String s3Key = SharedPref.getDivisionCode(context).replace(",", "/") + "Signature" + "/" + fileToUpload.getName();
                    if (s3Key.contains(null)) {
                        Log.d("s3Key", "CallSendSignImage: " + "s3key is null");
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
                                DeleteCacheFileSign(filePath, id,i, position);
                                Log.d("S3 Upload", "Upload Successful: " + s3Key);
                                try {
                                    CallApiSignImage(position);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else if (state == TransferState.FAILED) {

                                Log.e("S3 Upload", "Upload Failed");
                                InsertImage(signModelClass.getFilePath(), context);
                                signModelClass.setSynced(1);
                                signModelClass.setSync_status(Constants.CALL_FAILED);
                                try {
                                    callOfflineSignDataDao.updateSignStatus(id, Constants.CALL_FAILED, 1);
                                    CallApiSignImage(position);
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
                                CallApiSignImage(position);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });

                }
            }

        }catch(Exception e){
            Log.v("img_tagOCA", e.toString());
            signModelClass.setSynced(1);
            signModelClass.setSync_status(Constants.EXCEPTION_ERROR);
            callOfflineSignDataDao.updateSignStatus(id, Constants.EXCEPTION_ERROR, 1);
            try {
                CallApiSignImage(position);
            } catch (Exception a) {
                a.printStackTrace();
            }
        }
    }

    private void CallSendSignImage(int position,int i ,SignModelClass signModelClass, String jsonValues, String filePath, String id) {
        ApiInterface apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getTagApiImageUrl(context));
        MultipartBody.Part img = convertImg("sign_path", filePath);
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
                            DeleteCacheFileSign(filePath, id,i, position);
                        } else {
                            signModelClass.setSynced(1);
                            signModelClass.setSync_status(Constants.DUPLICATE_CALL);
                            callOfflineSignDataDao.updateSignStatus(id, Constants.DUPLICATE_CALL, 1);
                            CallApiSignImage(position);
                        }
                        if (!childListModelClasses.get(position).getSignModelClasses().isEmpty()) {
                            RefreshAdapter();
                        }
                    } catch (Exception e) {
                        Log.v("SendOutboxCall", "-error-ec--" + e);
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
                callOfflineSignDataDao.updateSignStatus(id, Constants.DUPLICATE_CALL, 1);
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
    private void DeleteCacheFileSign(String filePath, String id, int i, int position) {
        File fileDelete = new File(filePath);
        if (fileDelete.exists()) {
            if (fileDelete.delete()) {
                System.out.println("file Deleted :" + filePath);
            } else {
                System.out.println("file not Deleted :" + filePath);
            }
        }
        callOfflineSignDataDao.deleteOfflineSignId(id);
        childListModelClasses.get(position).getSignModelClasses().remove(i);
        CallApiSignImage(position);
    }


//    private void CallSendSignImage(int position,int i, SignModelClass signModelClass, String jsonValues, String filePath, String id){
//        ApiInterface apiInterface = RetrofitClient.getRetrofit(context,baseUrl);
//        MultipartBody.Part signImg = convertImg("SignatureImage",filePath);
//        HashMap<String,RequestBody> signValues = field(jsonValues);
//        Call<JsonObject> saveSignImg = apiInterface.SignUpload(signValues,signImg);
//
//        saveSignImg.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                if(response.isSuccessful()){
//                    try{
//                        assert response.body() != null;
//                        JSONObject json = new JSONObject(response.body().toString());
//                        if(json.getString("success").equalsIgnoreCase("true") && json.getString("msg").equalsIgnoreCase("Signature Recorded")){
//                            DeleteCacheFile(filePath,id,i,position);
//                        }else{
//                            signModelClass.setSynced(1);
//                            signModelClass.setSync_status(Constants.CALL_FAILED);
//                            callOfflineSignDataDao.updateSignStatus(id, Constants.DUPLICATE_CALL, 1);
//                            CallApiSignImage(position);
//                        }
//
//                        if (!childListModelClasses.get(position).getEcModelClasses().isEmpty()) {
//                            RefreshAdapter();
//                        }
//
//                    } catch (Exception e) {
//                        Log.v("SendOutboxCall", "-error---" + e);
//                        signModelClass.setSynced(1);
//                        signModelClass.setSync_status(Constants.EXCEPTION_ERROR);
//                        callOfflineSignDataDao.updateSignStatus(id, Constants.EXCEPTION_ERROR, 1);
//                        CallApiSignImage(position);
//                    }
//                }
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                signModelClass.setSynced(1);
//                signModelClass.setSync_status(Constants.EXCEPTION_ERROR);
//                callOfflineSignDataDao.updateSignStatus(id, Constants.EXCEPTION_ERROR, 1);
//                CallApiSignImage(position);
//            }
//        });
//    }

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

    @Override
    public int getItemCount() {
        return childListModelClasses.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void CallAPIList(int position) {
        if (!childListModelClasses.get(position).getOutBoxCallLists().isEmpty()) {
            isAvailable = false;
            for (int i = 0; i < childListModelClasses.get(position).getOutBoxCallLists().size(); i++) {
                OutBoxCallList outBoxCallList = childListModelClasses.get(position).getOutBoxCallLists().get(i);
                if (outBoxCallList.getStatus().equalsIgnoreCase(Constants.WAITING_FOR_SYNC) || outBoxCallList.getStatus().equalsIgnoreCase(Constants.CALL_FAILED)) {
                    if (outBoxCallList.getSyncCount() <= 4) {
                        isAvailable = true;
                        Log.v("SendOfflineCall", "---" + outBoxCallList.getCusName());
                        CallSendAPI(outBoxCallList, position, i, outBoxCallList.getDates(), outBoxCallList.getCusName(), outBoxCallList.getCusCode(), outBoxCallList.getJsonData(), outBoxCallList.getCusType(), outBoxCallList.getSyncCount());
                        break;
                    }
                }
            }
        } else {
            isAvailable = false;
        }

        if (!isAvailable) {
       //     CallsFragment.CallTodayCallsAPI(context, apiInterface, false);
            progressDialog.dismiss();
            notifyDataSetChanged();
        }
    }

    private void CallSendAPI(OutBoxCallList outBoxCallList, int position, int outBoxList, String date, String cusName, String cusCode, String jsonData, String cusType, int SyncCount) {
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
                                childListModelClasses.get(position).getOutBoxCallLists().remove(outBoxList);
                            } else if (jsonSaveRes.getString("success").equalsIgnoreCase("false") && jsonSaveRes.getString("msg").equalsIgnoreCase("Call Already Exists")) {
                                callsUtil.updateOfflineUpdateStatusEC(date, cusCode, 5, Constants.DUPLICATE_CALL, 1);
                                childListModelClasses.get(position).getOutBoxCallLists().set(outBoxList, new OutBoxCallList(cusName, cusCode, date, outBoxCallList.getIn(), outBoxCallList.getOut(), jsonData, outBoxCallList.getCusType(), Constants.DUPLICATE_CALL, 5));
                                DeleteUpdateDcrTable(date, cusCode, cusType);
                                UpdateEcData(date, cusCode, cusName, Constants.DUPLICATE_CALL, 1);
                            }
                            CallAPIList(position);
                            notifyDataSetChanged();
                        } catch (Exception e) {
                            callsUtil.updateOfflineUpdateStatusEC(date, cusCode, 5, Constants.EXCEPTION_ERROR, 0);
                            UpdateEcData(date, cusCode, cusName, Constants.EXCEPTION_ERROR, 0);
                            childListModelClasses.get(position).getOutBoxCallLists().set(outBoxList, new OutBoxCallList(cusName, cusCode, date, outBoxCallList.getIn(), outBoxCallList.getOut(), jsonData, outBoxCallList.getCusType(), Constants.EXCEPTION_ERROR, 5));
                            CallAPIList(position);
                            notifyDataSetChanged();
                            Log.v("SendOutboxCall", "---" + e);
                        }
                    }
                }

                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    callsUtil.updateOfflineUpdateStatusEC(date, cusCode, SyncCount + 1, Constants.CALL_FAILED, 1);
                    childListModelClasses.get(position).getOutBoxCallLists().set(outBoxList, new OutBoxCallList(cusName, cusCode, date, outBoxCallList.getIn(), outBoxCallList.getOut(), jsonData, outBoxCallList.getCusType(), Constants.DUPLICATE_CALL, SyncCount + 1));
                    UpdateEcData(date, cusCode, cusName, Constants.CALL_FAILED, 1);
                    CallAPIList(position);
                    notifyDataSetChanged();
                }
            });

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    private void CallAPIActivity(int position) {
        if (!childListModelClasses.get(position).getActivityModelClasses().isEmpty()) {
            isAvailable = false;
            for (int i = 0; i < childListModelClasses.get(position).getActivityModelClasses().size(); i++) {
                ActivityModelClass activityModelClass = childListModelClasses.get(position).getActivityModelClasses().get(i);
                if (activityModelClass.getSyncStatus().equalsIgnoreCase(Constants.WAITING_FOR_SYNC) || activityModelClass.getSyncStatus().equalsIgnoreCase(Constants.FAILED)) {
                    if (activityModelClass.getSyncCount() <= 4) {
                        isAvailable = true;
                        Log.v("SendOfflineCall", "---" + activityModelClass.getName() + " -> " + activityModelClass.getActivityDate() + " - " + activityModelClass.getActivityTime());
                        CallSendActivityAPI(activityModelClass, position, i);
                        break;
                    }
                }
            }
        } else {
            isAvailable = false;
        }

        if (!isAvailable) {
            progressDialog.dismiss();
            notifyDataSetChanged();
        }
    }

    private void CallSendActivityAPI(ActivityModelClass activityModelClass, int position, int outBoxListIndex) {
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
                                childListModelClasses.get(position).getActivityModelClasses().remove(outBoxListIndex);
                            } else {
                                callsUtil.updateStatusActivity(activityModelClass.getId(), 5, Constants.FAILED);
                                activityModelClass.setSyncStatus(Constants.FAILED);
                                activityModelClass.setSyncCount(5);
                                childListModelClasses.get(position).getActivityModelClasses().set(outBoxListIndex, activityModelClass);
                            }
                            CallAPIActivity(position);
                            notifyDataSetChanged();
                        } catch (Exception e) {
                            callsUtil.updateStatusActivity(activityModelClass.getId(), 5, Constants.EXCEPTION_ERROR);
                            activityModelClass.setSyncStatus(Constants.EXCEPTION_ERROR);
                            activityModelClass.setSyncCount(5);
                            childListModelClasses.get(position).getActivityModelClasses().set(outBoxListIndex, activityModelClass);
                            Log.v("SendOutboxCall", "---" + e);
                            e.printStackTrace();
                            CallAPIActivity(position);
                            notifyDataSetChanged();
                        }
                    }
                }

                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    callsUtil.updateStatusActivity(activityModelClass.getId(), activityModelClass.getSyncCount() + 1, Constants.FAILED);
                    activityModelClass.setSyncStatus(Constants.FAILED);
                    activityModelClass.setSyncCount(activityModelClass.getSyncCount() + 1);
                    childListModelClasses.get(position).getActivityModelClasses().set(outBoxListIndex, activityModelClass);
                    CallAPIActivity(position);
                    notifyDataSetChanged();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void CallAPIActivityUpload(int position) {
        if (!childListModelClasses.get(position).getActivityUploadModelClasses().isEmpty()) {
            isAvailable = false;
            for (int i = 0; i < childListModelClasses.get(position).getActivityUploadModelClasses().size(); i++) {
                ActivityUploadModelClass activityUploadModelClass = childListModelClasses.get(position).getActivityUploadModelClasses().get(i);
                if (activityUploadModelClass.getSyncStatus().equalsIgnoreCase(Constants.WAITING_FOR_SYNC) || activityUploadModelClass.getSyncStatus().equalsIgnoreCase(Constants.FAILED)) {
                    if (activityUploadModelClass.getSyncCount() <= 4) {
                        isAvailable = true;
                        Log.v("SendOfflineCall", "---" + activityUploadModelClass.getName() + " -> " + activityUploadModelClass.getActivityDate() + " - " + activityUploadModelClass.getActivityTime());
                        CallSendActivityUploadAPI(activityUploadModelClass, position, i);
                        break;
                    }
                }
            }
        } else {
            isAvailable = false;
        }

        if (!isAvailable) {
            progressDialog.dismiss();
            notifyDataSetChanged();
        }
    }

    private void CallSendActivityUploadAPI(ActivityUploadModelClass activityUploadModelClass, int position, int outBoxListIndex) {
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
                                childListModelClasses.get(position).getActivityUploadModelClasses().remove(outBoxListIndex);
                            }else {
                                callsUtil.updateStatusActivity(activityUploadModelClass.getActivityID(), 5, Constants.FAILED);
                                activityUploadModelClass.setSyncStatus(Constants.FAILED);
                                activityUploadModelClass.setSyncCount(5);
                                childListModelClasses.get(position).getActivityUploadModelClasses().set(outBoxListIndex, activityUploadModelClass);
                            }
                            CallAPIActivityUpload(position);
                            notifyDataSetChanged();
                        } catch (Exception e) {
                            callsUtil.updateStatusActivity(activityUploadModelClass.getActivityID(), 5, Constants.EXCEPTION_ERROR);
                            activityUploadModelClass.setSyncStatus(Constants.EXCEPTION_ERROR);
                            activityUploadModelClass.setSyncCount(5);
                            Log.v("SendOutboxCall", "---" + e);
                            e.printStackTrace();
                            CallAPIActivityUpload(position);
                            notifyDataSetChanged();
                        }
                    }
                }

                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable throwable) {
                    callsUtil.updateStatusActivity(activityUploadModelClass.getActivityID(), activityUploadModelClass.getSyncCount() + 1, Constants.FAILED);
                    activityUploadModelClass.setSyncStatus(Constants.FAILED);
                    activityUploadModelClass.setSyncCount(activityUploadModelClass.getSyncCount() + 1);
                    childListModelClasses.get(position).getActivityUploadModelClasses().set(outBoxListIndex, activityUploadModelClass);
                    CallAPIActivityUpload(position);
                    notifyDataSetChanged();
                }
            });

        } catch (Exception e) {
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

    public static class listDataViewholider extends RecyclerView.ViewHolder {
        TextView tvContentList, tvCount;
        ImageView sync, img_expand_child;
        ConstraintLayout constraintRv, constraintMain;
        CardView expandContentView;
        RecyclerView rv_outbox_list;


        public listDataViewholider(@NonNull View view) {
            super(view);
            tvContentList = view.findViewById(R.id.textViewLabel1);
            tvCount = view.findViewById(R.id.textViewcount);
            sync = view.findViewById(R.id.img_sync);
            img_expand_child = view.findViewById(R.id.img_expand);
            expandContentView = view.findViewById(R.id.card_content_view);
            rv_outbox_list = view.findViewById(R.id.rv_outbox_list);
            constraintRv = view.findViewById(R.id.constraint_rv);
            constraintMain = view.findViewById(R.id.constraint_top);
        }
    }
}

