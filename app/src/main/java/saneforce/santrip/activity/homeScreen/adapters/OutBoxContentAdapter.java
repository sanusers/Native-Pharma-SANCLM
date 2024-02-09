package saneforce.santrip.activity.homeScreen.adapters;

import static saneforce.santrip.activity.homeScreen.fragment.CallAnalysisFragment.Chemist_list;
import static saneforce.santrip.activity.homeScreen.fragment.CallAnalysisFragment.Doctor_list;
import static saneforce.santrip.activity.homeScreen.fragment.CallAnalysisFragment.Stockiest_list;
import static saneforce.santrip.activity.homeScreen.fragment.CallAnalysisFragment.callAnalysisBinding;
import static saneforce.santrip.activity.homeScreen.fragment.CallAnalysisFragment.cip_list;
import static saneforce.santrip.activity.homeScreen.fragment.CallAnalysisFragment.hos_list;
import static saneforce.santrip.activity.homeScreen.fragment.CallAnalysisFragment.unlistered_list;
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
import saneforce.santrip.activity.homeScreen.fragment.CallsFragment;
import saneforce.santrip.activity.homeScreen.modelClass.CheckInOutModelClass;
import saneforce.santrip.activity.homeScreen.modelClass.ChildListModelClass;
import saneforce.santrip.activity.homeScreen.modelClass.EcModelClass;
import saneforce.santrip.activity.homeScreen.modelClass.OutBoxCallList;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.commonClasses.UtilityClass;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.storage.SharedPref;

public class OutBoxContentAdapter extends RecyclerView.Adapter<OutBoxContentAdapter.listDataViewholider> {
    Context context;
    ArrayList<ChildListModelClass> childListModelClasses;
    OutBoxCallAdapter outBoxCallAdapter;
    SQLite sqLite;
    ApiInterface apiInterface;
    OutBoxCheckInOutAdapter outBoxCheckInOutAdapter;
    OutBoxHeaderAdapter outBoxHeaderAdapter;
    OutBoxECAdapter outBoxECAdapter;
    ProgressDialog progressDialog;
    boolean isCallAvailable;
    CommonUtilsMethods commonUtilsMethods;
    Activity activity;

    public OutBoxContentAdapter(Activity activity, Context context, ArrayList<ChildListModelClass> groupModelClasses) {
        this.activity = activity;
        this.context = context;
        this.childListModelClasses = groupModelClasses;
        sqLite = new SQLite(context);
        apiInterface = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));
        commonUtilsMethods = new CommonUtilsMethods(context);
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


        if (contentList.getChildId() == 0) {
            holder.tvCount.setText(String.valueOf(contentList.getCheckInOutModelClasses().size()));
            if (contentList.isExpanded() && contentList.getCheckInOutModelClasses().size() > 0) {
                SetupVisibleData(holder.constraintRv, contentList.getChildId(), holder.rv_outbox_list, holder.img_expand_child, contentList.getOutBoxCallLists(), contentList.getCheckInOutModelClasses(), contentList.getEcModelClasses());
            } else {
                holder.constraintRv.setVisibility(View.GONE);
                holder.img_expand_child.setImageResource(R.drawable.down_arrow);
            }
        }


        if (contentList.getChildId() == 2) {
            holder.tvCount.setText(String.valueOf(contentList.getOutBoxCallLists().size()));
            if (contentList.isExpanded() && contentList.getOutBoxCallLists().size() > 0) {
                SetupVisibleData(holder.constraintRv, contentList.getChildId(), holder.rv_outbox_list, holder.img_expand_child, contentList.getOutBoxCallLists(), contentList.getCheckInOutModelClasses(), contentList.getEcModelClasses());
            } else {
                holder.constraintRv.setVisibility(View.GONE);
                holder.img_expand_child.setImageResource(R.drawable.down_arrow);
            }
        }

        if (contentList.getChildId() == 3) {
            holder.tvCount.setText(String.valueOf(contentList.getEcModelClasses().size()));
            if (contentList.isExpanded() && contentList.getEcModelClasses().size() > 0) {
                SetupVisibleData(holder.constraintRv, contentList.getChildId(), holder.rv_outbox_list, holder.img_expand_child, contentList.getOutBoxCallLists(), contentList.getCheckInOutModelClasses(), contentList.getEcModelClasses());
            } else {
                holder.constraintRv.setVisibility(View.GONE);
                holder.img_expand_child.setImageResource(R.drawable.down_arrow);
            }
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
                    case 2:
                        CallAPIList(position);
                        break;
                    case 3:
                        CallAPIListImage(position);
                        break;
                }
            } else {
                commonUtilsMethods.ShowToast(context, context.getString(R.string.no_network), 100);
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
                mLayoutManager = new LinearLayoutManager(context);
                rvOutboxList.setLayoutManager(mLayoutManager);
                rvOutboxList.setAdapter(outBoxCheckInOutAdapter);
                break;
            case 2:
                outBoxCallAdapter = new OutBoxCallAdapter(activity, context, outBoxCallLists);
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
        if (childListModelClasses.get(position).getCheckInOutModelClasses().size() > 0) {
            for (int i = 0; i < childListModelClasses.get(position).getCheckInOutModelClasses().size(); i++) {
                CheckInOutModelClass checkInOutModelClass = childListModelClasses.get(position).getCheckInOutModelClasses().get(i);
                Log.v("SendOutboxCall", "--checkinout--" + checkInOutModelClass.getDates() + "---" + checkInOutModelClass.getCheckInTime() + "----" + checkInOutModelClass.getCheckOutTime());
                if (checkInOutModelClass.getJsonOutValues().isEmpty()) {
                    CallSendAPICheckInOut(position, i, checkInOutModelClass.getJsonInValues(), checkInOutModelClass.getDates(), checkInOutModelClass.getCheckCount());
                } else {
                    CallSendAPICheckInOut(position, i, checkInOutModelClass.getJsonOutValues(), checkInOutModelClass.getDates(), checkInOutModelClass.getCheckCount());
                }
                break;
            }
        } else {
            progressDialog.dismiss();
            RefreshAdapter();
        }
    }

    private void CallSendAPICheckInOut(int position, int i, String jsonOutValues, String dates, String checkCount) {
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
                    childListModelClasses.get(position).getCheckInOutModelClasses().remove(i);
                } else {
                    sqLite.deleteOfflineCheckInOut(dates, checkCount);
                    childListModelClasses.get(position).getCheckInOutModelClasses().remove(i);
                }
                progressDialog.dismiss();
                notifyDataSetChanged();
            }

            @Override
            public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                sqLite.deleteOfflineCheckInOut(dates, checkCount);
                childListModelClasses.get(position).getCheckInOutModelClasses().remove(i);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void CallAPIListImage(int position) {
        Log.v("SendOutboxCall", "--image--" + childListModelClasses.get(position).getEcModelClasses().size());
        if (childListModelClasses.get(position).getEcModelClasses().size() > 0) {
            for (int i = 0; i < childListModelClasses.get(position).getEcModelClasses().size(); i++) {
                EcModelClass ecModelClass = childListModelClasses.get(position).getEcModelClasses().get(i);
                Log.v("SendOutboxCall", "--image--" + ecModelClass.getDates() + "---" + ecModelClass.getName());
                CallSendAPIImage(position, i, ecModelClass.getJson_values(), ecModelClass.getFilePath(), ecModelClass.getId());
                break;
            }
        } else {
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

    private void CallSendAPIImage(int position, int i, String jsonValues, String filePath, String id) {
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
                            DeleteCacheFile(filePath, id, i, position);
                        }
                        if (childListModelClasses.get(position).getEcModelClasses().size() > 0) {
                            RefreshAdapter();
                        }
                    } catch (Exception e) {
                        Log.v("SendOutboxCall", "-error---" + e);
                        DeleteCacheFile(filePath, id, i, position);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<JsonObject> call, @NonNull Throwable t) {
                DeleteCacheFile(filePath, id, i, position);
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
        sqLite.deleteOfflineEC(id);
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
        if (childListModelClasses.get(position).getOutBoxCallLists().size() > 0) {
            isCallAvailable = false;
            for (int i = 0; i < childListModelClasses.get(position).getOutBoxCallLists().size(); i++) {
                OutBoxCallList outBoxCallList = childListModelClasses.get(position).getOutBoxCallLists().get(i);
                if (outBoxCallList.getStatus().equalsIgnoreCase("Waiting for Sync") || outBoxCallList.getStatus().equalsIgnoreCase("Call Failed")) {
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
            CallsFragment.CallTodayCallsAPI(context, apiInterface, sqLite, false);
            progressDialog.dismiss();
            RefreshAdapter();
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
            //  Log.v("SendOutboxCall", "----" + jsonSaveDcr);
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
                                childListModelClasses.get(position).getOutBoxCallLists().remove(outBoxList);
                            } else if (jsonSaveRes.getString("success").equalsIgnoreCase("false") && jsonSaveRes.getString("msg").equalsIgnoreCase("Call Already Exists")) {
                                sqLite.saveOfflineUpdateStatus(date, cusCode, String.valueOf(5), "Duplicate Call");
                                childListModelClasses.get(position).getOutBoxCallLists().set(outBoxList, new OutBoxCallList(cusName, cusCode, date, outBoxCallList.getIn(), outBoxCallList.getOut(), jsonData, outBoxCallList.getCusType(), "Duplicate Call", 5));
                                DeleteUpdateDcrTable(date, cusCode, cusType);
                                RefreshAdapter();
                            }
                            CallAPIList(position);
                        } catch (Exception e) {
                            sqLite.saveOfflineUpdateStatus(date, cusCode, String.valueOf(5), "Exception Error");
                            childListModelClasses.get(position).getOutBoxCallLists().set(outBoxList, new OutBoxCallList(cusName, cusCode, date, outBoxCallList.getIn(), outBoxCallList.getOut(), jsonData, outBoxCallList.getCusType(), "Exception Error", 5));
                          /*  childListModelClasses.get(position).getOutBoxCallLists().remove(outBoxList);
                            sqLite.deleteOfflineCalls(cusCode, cusName, date);*/
                            CallAPIList(position);
                            Log.v("SendOutboxCall", "---" + e);
                        }
                    }
                }

                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    sqLite.saveOfflineUpdateStatus(date, cusCode, String.valueOf(SyncCount + 1), "Call Failed");
                    childListModelClasses.get(position).getOutBoxCallLists().set(outBoxList, new OutBoxCallList(cusName, cusCode, date, outBoxCallList.getIn(), outBoxCallList.getOut(), jsonData, outBoxCallList.getCusType(), "Duplicate Call", SyncCount + 1));
                    CallAPIList(position);
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
