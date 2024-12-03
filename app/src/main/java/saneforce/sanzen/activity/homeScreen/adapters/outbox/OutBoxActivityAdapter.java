package saneforce.sanzen.activity.homeScreen.adapters.outbox;

import static saneforce.sanzen.activity.call.DCRCallActivity.CallActivityCustDetails;
import static saneforce.sanzen.activity.homeScreen.fragment.CallAnalysisFragment.Chemist_list;
import static saneforce.sanzen.activity.homeScreen.fragment.CallAnalysisFragment.Doctor_list;
import static saneforce.sanzen.activity.homeScreen.fragment.CallAnalysisFragment.Stockiest_list;
import static saneforce.sanzen.activity.homeScreen.fragment.CallAnalysisFragment.callAnalysisBinding;
import static saneforce.sanzen.activity.homeScreen.fragment.CallAnalysisFragment.cip_list;
import static saneforce.sanzen.activity.homeScreen.fragment.CallAnalysisFragment.hos_list;
import static saneforce.sanzen.activity.homeScreen.fragment.CallAnalysisFragment.unlistered_list;
import static saneforce.sanzen.activity.homeScreen.fragment.OutboxFragment.listDates;
import static saneforce.sanzen.activity.homeScreen.fragment.OutboxFragment.outBoxBinding;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonElement;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.DCRCallActivity;
import saneforce.sanzen.activity.homeScreen.modelClass.ActivityModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.EcModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.OutBoxCallList;
import saneforce.sanzen.activity.map.custSelection.CustList;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.roomdatabase.ActivityOfflineTableDetails.ActivityOfflineDataDao;
import saneforce.sanzen.roomdatabase.ActivityUploadTableDetails.ActivityUploadDataDao;
import saneforce.sanzen.roomdatabase.CallDataRestClass;
import saneforce.sanzen.roomdatabase.CallOfflineECTableDetails.CallOfflineECDataDao;
import saneforce.sanzen.roomdatabase.CallOfflineTableDetails.CallOfflineDataDao;
import saneforce.sanzen.roomdatabase.CallTableDetails.CallTableDao;
import saneforce.sanzen.roomdatabase.CallsUtil;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.sanzen.roomdatabase.OfflineDaySubmit.OfflineDaySubmitDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class OutBoxActivityAdapter extends RecyclerView.Adapter<OutBoxActivityAdapter.ViewHolder> {
    private final Context context;
    private final ArrayList<ActivityModelClass> activityModelClassList;
    OutBoxHeaderAdapter outBoxHeaderAdapter;
    private final CommonUtilsMethods commonUtilsMethods;
    private final Activity activity;
    private final ApiInterface apiInterface;
    ProgressDialog progressDialog;
    private final RoomDB roomDB;
    private final MasterDataDao masterDataDao;
    private final CallOfflineECDataDao callOfflineECDataDao;
    private final CallOfflineDataDao callOfflineDataDao;
    private final OfflineDaySubmitDao offlineDaySubmitDao;
    private final CallTableDao callTableDao;
    private final ActivityOfflineDataDao activityOfflineDataDao;
    private final ActivityUploadDataDao activityUploadDataDao;
    private final CallsUtil callsUtil;

    public OutBoxActivityAdapter(Activity activity, Context context, ArrayList<ActivityModelClass> activityModelClassList, ApiInterface apiInterface) {
        this.context = context;
        this.activity = activity;
        this.activityModelClassList = activityModelClassList;
        this.apiInterface = apiInterface;
        commonUtilsMethods = new CommonUtilsMethods(context);
        roomDB=RoomDB.getDatabase(context);
        masterDataDao=roomDB.masterDataDao();
        callOfflineECDataDao = roomDB.callOfflineECDataDao();
        callOfflineDataDao = roomDB.callOfflineDataDao();
        offlineDaySubmitDao = roomDB.offlineDaySubmitDao();
        activityOfflineDataDao = roomDB.activityOfflineDataDao();
        activityUploadDataDao = roomDB.activityUploadDataDao();
        callTableDao = roomDB.callTableDao();
        callsUtil = new CallsUtil(context);
    }

    @NonNull
    @Override
    public OutBoxActivityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.outbox_child_view, parent, false);
        return new OutBoxActivityAdapter.ViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull OutBoxActivityAdapter.ViewHolder holder, int position) {
        holder.tvName.setText(String.format("%s (Doctor) ", activityModelClassList.get(position).getName()));
        holder.imgPic.setVisibility(View.GONE);

        holder.tvInOut.setText(String.format("%s %s", activityModelClassList.get(position).getActivityDate(), activityModelClassList.get(position).getActivityTime()));
        String status = activityModelClassList.get(position).getStatus();
        if(status.equalsIgnoreCase(Constants.WAITING_FOR_SYNC)) {
            holder.tvStatus.setText(context.getString(R.string.waiting_for_sync));
        } else if (status.equalsIgnoreCase(Constants.CALL_FAILED)) {
            holder.tvStatus.setText(context.getString(R.string.call_failed));
        } else if (status.equalsIgnoreCase(Constants.DUPLICATE_CALL)) {
            holder.tvStatus.setText(context.getString(R.string.duplicate_call));
        } else if (status.equalsIgnoreCase(Constants.EXCEPTION_ERROR)) {
            holder.tvStatus.setText(context.getString(R.string.exception_error));
        } else {
            holder.tvStatus.setText(status);
        }

        holder.tvMenu.setOnClickListener(v -> {
            Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
            final PopupMenu popup = new PopupMenu(wrapper, v, Gravity.END);
            popup.inflate(R.menu.call_menu);
            MenuItem editMenu = popup.getMenu().findItem(R.id.menuEdit);
            MenuItem deleteMenu = popup.getMenu().findItem(R.id.menuDelete);
            editMenu.setVisible(false);
            deleteMenu.setVisible(offlineDaySubmitDao.getDaySubmit(activityModelClassList.get(position).getActivityDate()) == null);
            popup.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.menuSync) {
                    if (UtilityClass.isNetworkAvailable(context)) {
                        ActivityModelClass activityModelClass = activityModelClassList.get(position);
//                        CallAPI(holder.getAbsoluteAdapterPosition(), activityModelClass, activityModelClassList.get(position).getJsonData(), activityModelClassList.get(position).getCusCode(), activityModelClassList.get(position).getCusName(), activityModelClassList.get(position).getActivityDate(), activityModelClassList.get(position).getSyncCount());
                    } else {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.no_network));
                    }
                } else if (menuItem.getItemId() == R.id.menuDelete) {

                    Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.dcr_cancel_alert);
                    dialog.setCancelable(false);
                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                    TextView btn_yes=dialog.findViewById(R.id.btn_yes);
                    TextView btn_no=dialog.findViewById(R.id.btn_no);
                    TextView titte=dialog.findViewById(R.id.ed_alert_msg);
                    titte.setText(R.string.are_you_sure_to_delete);

                    btn_yes.setOnClickListener(view -> {
                        dialog.dismiss();
//                        UpdateInputSample(activityModelClassList.get(position).getJsonData());
//                        if (callOfflineECDataDao.isAvailableEc(activityModelClassList.get(position).getDates(), activityModelClassList.get(position).getCusCode())) {
//                            for (int i = 0; i < listDates.size(); i++) {
//                                if (listDates.get(i).getGroupName().equalsIgnoreCase(activityModelClassList.get(position).getDates())) {
//                                    for (int j = 0; j < listDates.get(i).getChildItems().get(3).getEcModelClasses().size(); j++) {
//                                        EcModelClass ecModelClass = listDates.get(i).getChildItems().get(3).getEcModelClasses().get(j);
//                                        if (ecModelClass.getDates().equalsIgnoreCase(activityModelClassList.get(position).getDates()) && ecModelClass.getCusCode().equalsIgnoreCase(activityModelClassList.get(position).getCusCode()) && ecModelClass.getCusName().equalsIgnoreCase(activityModelClassList.get(position).getCusName())) {
//                                            listDates.get(i).getChildItems().get(3).getEcModelClasses().remove(j);
//                                            j--;
//                                        }
//                                    }
//                                }
//                            }
//                        }
//                        callsUtil.deleteOfflineCalls(activityModelClassList.get(position).getCusCode(), activityModelClassList.get(position).getCusName(), activityModelClassList.get(position).getDates());
//                        try {
//                            if (!activityModelClassList.get(position).getStatus().equalsIgnoreCase(Constants.DUPLICATE_CALL)) {
//                                JSONArray jsonArray = new JSONArray(masterDataDao.getDataByKey(Constants.CALL_SYNC));
//                                for (int i = 0; i < jsonArray.length(); i++) {
//                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
//                                    if (jsonObject.getString("Dcr_dt").equalsIgnoreCase(activityModelClassList.get(position).getDates()) && jsonObject.getString("CustCode").equalsIgnoreCase(activityModelClassList.get(position).getCusCode())) {
//                                        jsonArray.remove(i);
//                                        break;
//                                    }
//                                }
//                                MasterDataTable mData =new MasterDataTable();
//                                mData.setMasterKey(Constants.CALL_SYNC);
//                                mData.setMasterValues(jsonArray.toString());
//                                mData.setSyncStatus(0);
//                                MasterDataTable Checked = masterDataDao.getMasterSyncDataByKey(Constants.CALL_SYNC);
//                                if(Checked !=null){
//                                    masterDataDao.updateData(Constants.CALL_SYNC, jsonArray.toString());
//                                }else {
//                                    masterDataDao.insert(mData);
//                                }
//                                CallDataRestClass.resetcallValues(context);
//                                if (activityModelClassList.get(position).getCusType().equalsIgnoreCase("1")) {
//                                    JSONObject json = new JSONObject(activityModelClassList.get(position).getJsonData());
//                                    JSONArray jsonAdditional = json.getJSONArray("AdCuss");
//                                    for (int aw = 0; aw < jsonAdditional.length(); aw++) {
//                                        JSONObject jsAw = jsonAdditional.getJSONObject(aw);
//                                        for (int i = 0; i < jsonArray.length(); i++) {
//                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
//                                            if (jsonObject.getString("Dcr_dt").equalsIgnoreCase(activityModelClassList.get(position).getDates()) && jsonObject.getString("CustCode").equalsIgnoreCase(jsAw.getString("Code"))) {
//                                                jsonArray.remove(i);
//                                            }
//                                        }
//                                    }
//                                }
//                            }
//                        } catch (Exception e) {
//                            Log.e("Outbox Delete call", "onBindViewHolder: " + e.getMessage());
//                            e.printStackTrace();
//                        }
//                        if(!callOfflineDataDao.isAvailableCallOnDate(activityModelClassList.get(position).getDates())) {
//                            SharedPref.setLastCallDate(context, "");
//                        }
                        removeAt(position);
                    });

                    btn_no.setOnClickListener(view -> {
                        dialog.dismiss();
                    });
                }
                return true;
            });
            popup.show();
        });

    }

    private void CallAPI(int pos, OutBoxCallList activityModelClass, String jsonData, String cusCode, String cusName, String date, int syncCount) {
        JSONObject jsonSaveDcr;
        try {
            progressDialog = CommonUtilsMethods.createProgressDialog(context);
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
                                removeAt(pos);
                                //   CallsFragment.CallTodayCallsAPI(context, apiInterface, false);
                                commonUtilsMethods.showToastMessage(context, context.getString(R.string.call_saved_successfully));
                            } else if (jsonSaveRes.getString("success").equalsIgnoreCase("false") && jsonSaveRes.getString("msg").equalsIgnoreCase("Call Already Exists")) {
                                callsUtil.updateOfflineUpdateStatusEC(date, cusCode, 5, Constants.DUPLICATE_CALL, 1);
                                activityModelClass.setStatus(Constants.DUPLICATE_CALL);
                                activityModelClass.setSyncCount(5);
                                commonUtilsMethods.showToastMessage(context, context.getString(R.string.call_already_exist));
                            } else if(jsonSaveRes.getString("success").equalsIgnoreCase("false")) {
                                if(jsonSaveRes.has("msg")) {
                                    callsUtil.updateOfflineUpdateStatusEC(date, cusCode, 5, jsonSaveRes.getString("msg"), 1);
                                    activityModelClass.setStatus(jsonSaveRes.getString("msg"));
                                    activityModelClass.setSyncCount(5);
                                    commonUtilsMethods.showToastMessage(context, jsonSaveRes.getString("msg"));
                                } else if(jsonSaveRes.has("Msg")) {
                                    callsUtil.updateOfflineUpdateStatusEC(date, cusCode, 5, jsonSaveRes.getString("Msg"), 1);
                                    activityModelClass.setStatus(jsonSaveRes.getString("Msg"));
                                    activityModelClass.setSyncCount(5);
                                    commonUtilsMethods.showToastMessage(context, jsonSaveRes.getString("Msg"));
                                }
                            }
                            progressDialog.dismiss();
                        } catch (Exception e) {
                            callsUtil.updateOfflineUpdateStatusEC(date, cusCode, 5, Constants.EXCEPTION_ERROR, 0);
                            activityModelClass.setStatus(Constants.EXCEPTION_ERROR);
                            activityModelClass.setSyncCount(5);
                            Log.v("SendOutboxCall", "---" + e);
                            progressDialog.dismiss();
                        }
                    }
                }

                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    callsUtil.updateOfflineUpdateStatusEC(date, cusCode, syncCount + 1, Constants.CALL_FAILED, 1);
                    activityModelClass.setStatus(Constants.CALL_FAILED);
                    activityModelClass.setSyncCount(syncCount + 1);
                    commonUtilsMethods.showToastMessage(context, context.getString(R.string.call_failed));
                    progressDialog.dismiss();
                }
            });

        } catch (JSONException e) {
            progressDialog.dismiss();
        }
    }

    @Override
    public int getItemCount() {
        return activityModelClassList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void removeAt(int position) {
        activityModelClassList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, activityModelClassList.size());
        outBoxHeaderAdapter = new OutBoxHeaderAdapter(activity, context, listDates);
        commonUtilsMethods.recycleTestWithDivider(outBoxBinding.rvOutBoxHead);
        outBoxBinding.rvOutBoxHead.setAdapter(outBoxHeaderAdapter);
        outBoxHeaderAdapter.notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvInOut, tvStatus, tvMenu;
        ImageView imgPic;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.textViewLabel1);
            tvInOut = itemView.findViewById(R.id.textViewLabel2);
            tvStatus = itemView.findViewById(R.id.tv_call_status);
            tvMenu = itemView.findViewById(R.id.optionview);
            imgPic = itemView.findViewById(R.id.profile_icon);
        }
    }
}
