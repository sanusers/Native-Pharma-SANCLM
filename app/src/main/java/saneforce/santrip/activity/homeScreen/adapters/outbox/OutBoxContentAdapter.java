package saneforce.santrip.activity.homeScreen.adapters.outbox;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;
import static saneforce.santrip.activity.homeScreen.fragment.OutboxFragment.listDates;
import static saneforce.santrip.activity.homeScreen.fragment.OutboxFragment.outBoxBinding;

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
import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.HomeDashBoard;
import saneforce.santrip.activity.homeScreen.fragment.CallsFragment;
import saneforce.santrip.activity.homeScreen.modelClass.CheckInOutModelClass;
import saneforce.santrip.activity.homeScreen.modelClass.ChildListModelClass;
import saneforce.santrip.activity.homeScreen.modelClass.DaySubmitModelClass;
import saneforce.santrip.activity.homeScreen.modelClass.EcModelClass;
import saneforce.santrip.activity.homeScreen.modelClass.GroupModelClass;
import saneforce.santrip.activity.homeScreen.modelClass.OutBoxCallList;
import saneforce.santrip.activity.homeScreen.modelClass.WorkPlanModelClass;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.commonClasses.UtilityClass;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;
import saneforce.santrip.roomdatabase.CallDataRestClass;
import saneforce.santrip.roomdatabase.CallOfflineECTableDetails.CallOfflineECDataDao;
import saneforce.santrip.roomdatabase.CallOfflineWorkTypeTableDetails.CallOfflineWorkTypeDataDao;
import saneforce.santrip.roomdatabase.CallsUtil;
import saneforce.santrip.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.santrip.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.santrip.roomdatabase.OfflineCheckInOutTableDetails.OfflineCheckInOutDataDao;
import saneforce.santrip.roomdatabase.OfflineDaySubmit.OfflineDaySubmitDao;
import saneforce.santrip.roomdatabase.RoomDB;
import saneforce.santrip.storage.SharedPref;

public class OutBoxContentAdapter extends RecyclerView.Adapter<OutBoxContentAdapter.listDataViewholider> {
    Context context;
    ArrayList<ChildListModelClass> childListModelClasses;
    OutBoxCallAdapter outBoxCallAdapter;
    ApiInterface apiInterface;
    OutBoxCheckInOutAdapter outBoxCheckInOutAdapter;
    OutBoxHeaderAdapter outBoxHeaderAdapter;
    OutBoxECAdapter outBoxECAdapter;
    ProgressDialog progressDialog;
    boolean isCallAvailable;
    CommonUtilsMethods commonUtilsMethods;
    Activity activity;
    RoomDB roomDB;

    MasterDataDao masterDataDao;
    private final OfflineCheckInOutDataDao offlineCheckInOutDataDao;
    private final CallOfflineECDataDao callOfflineECDataDao;
    private final OfflineDaySubmitDao offlineDaySubmitDao;
    private final CallOfflineWorkTypeDataDao callOfflineWorkTypeDataDao;
    private final CallsUtil callsUtil;

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
        callOfflineWorkTypeDataDao = roomDB.callOfflineWorkTypeDataDao();
        offlineDaySubmitDao = roomDB.offlineDaySubmitDao();
        callsUtil = new CallsUtil(context);
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
        }

        switch (contentList.getChildId()) {
            case 0:
                holder.tvCount.setText(String.valueOf(contentList.getCheckInOutModelClasses().size()));
                if (contentList.isExpanded() && !contentList.getCheckInOutModelClasses().isEmpty()) {
                    SetupVisibleData(holder.constraintRv, contentList.getChildId(), holder.rv_outbox_list, holder.img_expand_child, contentList.getOutBoxCallLists(), contentList.getCheckInOutModelClasses(), contentList.getEcModelClasses());
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
                holder.tvCount.setText(String.valueOf(contentList.getOutBoxCallLists().size()));
                if (contentList.isExpanded() && !contentList.getOutBoxCallLists().isEmpty()) {
                    SetupVisibleData(holder.constraintRv, contentList.getChildId(), holder.rv_outbox_list, holder.img_expand_child, contentList.getOutBoxCallLists(), contentList.getCheckInOutModelClasses(), contentList.getEcModelClasses());
                } else {
                    holder.constraintRv.setVisibility(View.GONE);
                    holder.img_expand_child.setImageResource(R.drawable.down_arrow);
                }
                break;
            case 3:
                holder.tvCount.setText(String.valueOf(contentList.getEcModelClasses().size()));
                if (contentList.isExpanded() && !contentList.getEcModelClasses().isEmpty()) {
                    SetupVisibleData(holder.constraintRv, contentList.getChildId(), holder.rv_outbox_list, holder.img_expand_child, contentList.getOutBoxCallLists(), contentList.getCheckInOutModelClasses(), contentList.getEcModelClasses());
                } else {
                    holder.constraintRv.setVisibility(View.GONE);
                    holder.img_expand_child.setImageResource(R.drawable.down_arrow);
                }
                break;
            case 4:
                if (contentList.getDaySubmitModelClass() == null) {
                    holder.expandContentView.setVisibility(View.GONE);
                } else {
                    holder.expandContentView.setVisibility(View.VISIBLE);
                }
                break;
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

    private void SetupVisibleData(ConstraintLayout constraintRv, int childId, RecyclerView rvOutboxList, ImageView imgExpandChild, ArrayList<OutBoxCallList> outBoxCallLists, ArrayList<CheckInOutModelClass> checkInOutModelClasses, ArrayList<EcModelClass> ecModelClasses) {
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
        }
        imgExpandChild.setImageResource(R.drawable.top_vector);
    }

    private void CallAPICheckInOut(int position) {
        if (!childListModelClasses.get(position).getCheckInOutModelClasses().isEmpty()) {
            isCallAvailable = false;
            for (int i = 0; i < childListModelClasses.get(position).getCheckInOutModelClasses().size(); i++) {
                CheckInOutModelClass checkInOutModelClass = childListModelClasses.get(position).getCheckInOutModelClasses().get(i);
                Log.v("SendOutboxCall", "--checkInOut--" + checkInOutModelClass.getDates() + "---" + checkInOutModelClass.getCheckInTime() + "----" + checkInOutModelClass.getCheckOutTime());
                if (checkInOutModelClass.getCheckStatus() == 0) {
                    isCallAvailable = true;
                    if (checkInOutModelClass.getJsonOutValues().isEmpty()) {
                        CallSendAPICheckInOut(position, i, checkInOutModelClass, checkInOutModelClass.getJsonInValues());
                    } else {
                        CallSendAPICheckInOut(position, i, checkInOutModelClass, checkInOutModelClass.getJsonOutValues());
                    }
                }
                break;
            }
        } else {
            isCallAvailable = false;
        }

        if (!isCallAvailable) {
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
            isCallAvailable = false;
            WorkPlanModelClass workPlanModelClass = childListModelClasses.get(childPos).getWorkPlanModelClass();
            if(workPlanModelClass.getSyncStatus() == 0) {
                isCallAvailable = true;
                Log.v("SendOutboxCall", "--WorkPlan--" + workPlanModelClass.getDate() + "---" + workPlanModelClass.getWtName() + "---" + workPlanModelClass.getWtCode());
                if(!workPlanModelClass.getJsonValues().isEmpty()) {
                    CallSendWorkPlan(workPlanModelClass, childPos, workPlanModelClass.getJsonValues());
                }
            }
        }else {
            isCallAvailable = false;
        }

        if(!isCallAvailable) {
            progressDialog.dismiss();
        }
    }

    private void CallSendWorkPlan(WorkPlanModelClass workPlanModelClass, int childPos, String jsonValues) {
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
            isCallAvailable = false;
            DaySubmitModelClass daySubmitModelClass = childListModelClasses.get(childPos).getDaySubmitModelClass();
            if(daySubmitModelClass.getSyncStatus() == 0) {
                isCallAvailable = true;
                Log.v("SendOutboxCall", "--Day Submit--" + daySubmitModelClass.getDate() + "---" + daySubmitModelClass.getJsonValues());
                if(!daySubmitModelClass.getJsonValues().isEmpty()) {
                    CallSendDaySubmit(daySubmitModelClass, childPos, daySubmitModelClass.getJsonValues());
                }
            }
        }else {
            isCallAvailable = false;
        }
        if (!isCallAvailable) {
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
            isCallAvailable = false;
            for (int i = 0; i < childListModelClasses.get(position).getEcModelClasses().size(); i++) {
                EcModelClass ecModelClass = childListModelClasses.get(position).getEcModelClasses().get(i);
                if (ecModelClass.getSynced() == 0) {
                    Log.v("SendOutboxCall", "--image--" + ecModelClass.getDates() + "---" + ecModelClass.getImg_name());
                    Log.v("SendOutboxCall-----", "--image--" + ecModelClass.getDates() + "---" + ecModelClass.getJson_values());
                    CallSendAPIImage(position, i, ecModelClass, ecModelClass.getJson_values(), ecModelClass.getFilePath(), String.valueOf(ecModelClass.getId()));
                }
                break;
            }
        } else {
            isCallAvailable = false;
        }
        if (!isCallAvailable) {
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

    @SuppressLint("NotifyDataSetChanged")
    private void DeleteCacheFile(String filePath, String id, int i, int position) {
        File fileDelete = new File(filePath);
        if (fileDelete.exists()) {
            if (fileDelete.delete()) {
                System.out.println("file Deleted :" + filePath);
            } else {
                System.out.println("file not Deleted :" + filePath);
            }
        }
        callOfflineECDataDao.deleteOfflineEC(id);
        childListModelClasses.get(position).getEcModelClasses().remove(i);
        CallAPIListImage(position);
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

    @SuppressLint("NotifyDataSetChanged")
    private void CallAPIList(int position) {
        if (!childListModelClasses.get(position).getOutBoxCallLists().isEmpty()) {
            isCallAvailable = false;
            for (int i = 0; i < childListModelClasses.get(position).getOutBoxCallLists().size(); i++) {
                OutBoxCallList outBoxCallList = childListModelClasses.get(position).getOutBoxCallLists().get(i);
                if (outBoxCallList.getStatus().equalsIgnoreCase(Constants.WAITING_FOR_SYNC) || outBoxCallList.getStatus().equalsIgnoreCase(Constants.CALL_FAILED)) {
                    if (outBoxCallList.getSyncCount() <= 4) {
                        isCallAvailable = true;
                        Log.v("SendOfflineCall", "---" + outBoxCallList.getCusName());
                        CallSendAPI(outBoxCallList, position, i, outBoxCallList.getDates(), outBoxCallList.getCusName(), outBoxCallList.getCusCode(), outBoxCallList.getJsonData(), outBoxCallList.getCusType(), outBoxCallList.getSyncCount());
                        break;
                    }
                }
            }
        } else {
            isCallAvailable = false;
        }

        if (!isCallAvailable) {
            CallsFragment.CallTodayCallsAPI(context, apiInterface, false);
            progressDialog.dismiss();
            notifyDataSetChanged();
        }
    }

    @Override
    public int getItemCount() {
        return childListModelClasses.size();
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

