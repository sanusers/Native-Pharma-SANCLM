package saneforce.santrip.activity.homeScreen.adapters;

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
import saneforce.santrip.activity.homeScreen.fragment.CallAnalysisFragment;
import saneforce.santrip.activity.homeScreen.fragment.CallsFragment;
import saneforce.santrip.activity.homeScreen.fragment.OutboxFragment;
import saneforce.santrip.activity.homeScreen.modelClass.CheckInOutModelClass;
import saneforce.santrip.activity.homeScreen.modelClass.EcModelClass;
import saneforce.santrip.activity.homeScreen.modelClass.GroupModelClass;
import saneforce.santrip.activity.homeScreen.modelClass.OutBoxCallList;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.commonClasses.UtilityClass;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.storage.SharedPref;

public class OutBoxHeaderAdapter extends RecyclerView.Adapter<OutBoxHeaderAdapter.listDataViewholider> {
    Context context;
    ArrayList<GroupModelClass> groupModelClasses;
    OutBoxContentAdapter outBoxContentAdapter;
    boolean isCallAvailable;
    ProgressDialog progressDialog;
    ApiInterface apiInterface;
    SQLite sqLite;
    OutBoxHeaderAdapter outBoxHeaderAdapter;
    String CheckInOutStatus = "";
    CommonUtilsMethods commonUtilsMethods;
    Activity activity;


    public OutBoxHeaderAdapter(Activity activity, Context context, ArrayList<GroupModelClass> groupModelClasses) {
        this.activity = activity;
        this.context = context;
        this.groupModelClasses = groupModelClasses;
        sqLite = new SQLite(context);
        apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));
        commonUtilsMethods = new CommonUtilsMethods(context);
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
        /*    SimpleDateFormat spf = new SimpleDateFormat("yyyy-MM-dd");
        Date newDate = null;
        try {
            newDate = spf.parse(groupModelClass.getGroupName());
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        spf = new SimpleDateFormat("dd MMM yyyy");
        String dates = spf.format(newDate);
        holder.tvDate.setText(dates);*/


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
                commonUtilsMethods.ShowToast(context, context.getString(R.string.no_network), 100);
            }
        });


        holder.cardView.setOnClickListener(v -> {
            groupModelClass.setExpanded(Objects.equals(holder.ivExpand.getDrawable().getConstantState(), Objects.requireNonNull(ContextCompat.getDrawable(context, R.drawable.down_arrow)).getConstantState()));
            notifyDataSetChanged();
        });
    }

    private void CallOfflineData(GroupModelClass groupModelClass, int childPos) {
        if (groupModelClass.getChildItems().get(childPos).getCheckInOutModelClasses().size() > 0) {
            for (int i = 0; i < groupModelClass.getChildItems().get(childPos).getCheckInOutModelClasses().size(); i++) {
                CheckInOutModelClass checkInOutModelClass = groupModelClass.getChildItems().get(childPos).getCheckInOutModelClasses().get(i);
                Log.v("SendOutboxCall", "--CheckInOut--" + checkInOutModelClass.getDates() + "---" + checkInOutModelClass.getCheckInTime() + "---" + checkInOutModelClass.getCheckOutTime());
                if (!checkInOutModelClass.getJsonOutValues().isEmpty()) {
                    CallSendCheckInOut(groupModelClass, childPos, i, checkInOutModelClass.getJsonOutValues(), checkInOutModelClass.getDates(), checkInOutModelClass.getCheckCount());
                } else {
                    CallSendCheckInOut(groupModelClass, childPos, i, checkInOutModelClass.getJsonInValues(), checkInOutModelClass.getDates(), checkInOutModelClass.getCheckCount());
                }
                break;
            }
        } else {
            CallAPIOfflineCalls(groupModelClass, 2);
        }
    }

    private void CallSendCheckInOut(GroupModelClass groupModelClass, int childPos, int i, String jsonOutValues, String dates, String checkCount) {
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
                if (response.isSuccessful()) {
                    sqLite.deleteOfflineCheckInOut(dates, checkCount);
                    groupModelClass.getChildItems().get(childPos).getCheckInOutModelClasses().remove(i);
                } else {
                    sqLite.deleteOfflineCheckInOut(dates, checkCount);
                    groupModelClass.getChildItems().get(childPos).getCheckInOutModelClasses().remove(i);
                }
                CallOfflineData(groupModelClass, childPos);
                notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                sqLite.deleteOfflineCheckInOut(dates, checkCount);
                groupModelClass.getChildItems().get(childPos).getCheckInOutModelClasses().remove(i);
                CallOfflineData(groupModelClass, childPos);
            }
        });
    }

    private void CallAPIOfflineCalls(GroupModelClass groupModelClass, int childPos) {
        if (groupModelClass.getChildItems().get(childPos).getOutBoxCallLists().size() > 0) {
            isCallAvailable = false;
            for (int i = 0; i < groupModelClass.getChildItems().get(childPos).getOutBoxCallLists().size(); i++) {
                OutBoxCallList outBoxCallList = groupModelClass.getChildItems().get(childPos).getOutBoxCallLists().get(i);
                if (outBoxCallList.getStatus().equalsIgnoreCase("Waiting for Sync") || outBoxCallList.getStatus().equalsIgnoreCase("Call Failed")) {
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
            CallsFragment.CallTodayCallsAPI(context, apiInterface, sqLite, false);
            CallAPIListImage(groupModelClass, 3);
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void CallAPIListImage(GroupModelClass groupModelClass, int childPos) {
        if (groupModelClass.getChildItems().get(childPos).getEcModelClasses().size() > 0) {
            for (int i = 0; i < groupModelClass.getChildItems().get(childPos).getEcModelClasses().size(); i++) {
                EcModelClass ecModelClass = groupModelClass.getChildItems().get(childPos).getEcModelClasses().get(i);
                Log.v("SendOutboxCall", "--image--" + ecModelClass.getDates() + "---" + ecModelClass.getName());
                CallSendAPIImage(groupModelClass, childPos, i, ecModelClass.getJson_values(), ecModelClass.getFilePath(), ecModelClass.getId());
                break;
            }
        } else {
            progressDialog.dismiss();
            CallAnalysisFragment.SetcallDetailsInLineChart(sqLite, context);
            OutboxFragment.SetupOutBoxAdapter(activity, sqLite, context);
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

    private void CallSendAPIImage(GroupModelClass groupModelClass, int childPos, int i, String jsonValues, String filePath, String id) {
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
                            DeleteCacheFile(groupModelClass, groupModelClass.getChildItems().get(childPos).getEcModelClasses().get(i).getFilePath(), id, i, childPos);
                        } else {
                            DeleteCacheFile(groupModelClass, groupModelClass.getChildItems().get(childPos).getEcModelClasses().get(i).getFilePath(), id, i, childPos);
                        }
                        if (groupModelClass.getChildItems().get(childPos).getEcModelClasses().size() > 0) {
                            RefreshAdapter();
                        }
                    } catch (Exception e) {
                        Log.v("SendOutboxCall", "-error---" + e);
                        DeleteCacheFile(groupModelClass, groupModelClass.getChildItems().get(childPos).getEcModelClasses().get(i).getFilePath(), id, i, childPos);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                DeleteCacheFile(groupModelClass, groupModelClass.getChildItems().get(childPos).getEcModelClasses().get(i).getFilePath(), id, i, childPos);
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
        sqLite.deleteOfflineEC(id);
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
                                sqLite.deleteOfflineCalls(cusCode, cusName, date);
                                groupModelClass.getChildItems().get(childPos).getOutBoxCallLists().remove(outBoxList);
                            } else if (jsonSaveRes.getString("success").equalsIgnoreCase("false") && jsonSaveRes.getString("msg").equalsIgnoreCase("Call Already Exists")) {
                                sqLite.saveOfflineUpdateStatus(date, cusCode, String.valueOf(5), "Duplicate Call");
                                groupModelClass.getChildItems().get(childPos).getOutBoxCallLists().set(outBoxList, new OutBoxCallList(cusName, cusCode, date, outBoxCallList.getIn(), outBoxCallList.getOut(), jsonData, outBoxCallList.getCusType(), "Duplicate Call", 5));
                                JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.DCR);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    if (jsonObject.getString("Dcr_dt").equalsIgnoreCase(date) && jsonObject.getString("CustCode").equalsIgnoreCase(cusCode)) {
                                        jsonArray.remove(i);
                                        break;
                                    }
                                }

                                sqLite.saveMasterSyncData(Constants.DCR, jsonArray.toString(), 0);
                                RefreshAdapter();
                            }
                            CallAPIOfflineCalls(groupModelClass, childPos);
                        } catch (Exception e) {
                            sqLite.saveOfflineUpdateStatus(date, cusCode, String.valueOf(5), "Exception Error");
                            groupModelClass.getChildItems().get(childPos).getOutBoxCallLists().set(outBoxList, new OutBoxCallList(cusName, cusCode, date, outBoxCallList.getIn(), outBoxCallList.getOut(), jsonData, outBoxCallList.getCusType(), "Exception Error", 5));
                            CallAPIOfflineCalls(groupModelClass, childPos);
                         /*   groupModelClass.getChildItems().get(childPos).getOutBoxCallLists().remove(outBoxList);
                            sqLite.deleteOfflineCalls(cusCode, cusName, date);
                            CallAPIOfflineCalls(groupModelClass, childPos);*/
                            Log.v("SendOutboxCall", "---" + e);
                        }
                    }
                }

                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    sqLite.saveOfflineUpdateStatus(date, cusCode, String.valueOf(SyncCount + 1), "Call Failed");
                    groupModelClass.getChildItems().get(childPos).getOutBoxCallLists().set(outBoxList, new OutBoxCallList(cusName, cusCode, date, outBoxCallList.getIn(), outBoxCallList.getOut(), jsonData, outBoxCallList.getCusType(), "Duplicate Call", SyncCount + 1));
                    CallAPIOfflineCalls(groupModelClass, childPos);
                }
            });

        } catch (JSONException e) {
            throw new RuntimeException(e);
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
