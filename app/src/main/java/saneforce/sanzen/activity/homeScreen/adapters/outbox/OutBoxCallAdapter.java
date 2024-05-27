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
import android.os.CountDownTimer;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
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
import saneforce.sanzen.activity.homeScreen.fragment.CallsFragment;
import saneforce.sanzen.activity.homeScreen.modelClass.EcModelClass;
import saneforce.sanzen.activity.homeScreen.modelClass.OutBoxCallList;
import saneforce.sanzen.activity.map.custSelection.CustList;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.roomdatabase.CallDataRestClass;
import saneforce.sanzen.roomdatabase.CallOfflineECTableDetails.CallOfflineECDataDao;
import saneforce.sanzen.roomdatabase.CallTableDetails.CallTableDao;
import saneforce.sanzen.roomdatabase.CallsUtil;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class OutBoxCallAdapter extends RecyclerView.Adapter<OutBoxCallAdapter.ViewHolder> {
    Context context;
    ArrayList<OutBoxCallList> outBoxCallLists;
    OutBoxHeaderAdapter outBoxHeaderAdapter;
    CommonUtilsMethods commonUtilsMethods;
    Activity activity;
    ApiInterface apiInterface;
    ProgressDialog progressDialog;
    RoomDB roomDB;

    MasterDataDao masterDataDao;
    private CallOfflineECDataDao callOfflineECDataDao;
    private CallTableDao callTableDao;
    private CallsUtil callsUtil;

    public OutBoxCallAdapter(Activity activity, Context context, ArrayList<OutBoxCallList> outBoxCallLists, ApiInterface apiInterface) {
        this.context = context;
        this.activity = activity;
        this.outBoxCallLists = outBoxCallLists;
        this.apiInterface = apiInterface;
        commonUtilsMethods = new CommonUtilsMethods(context);
        roomDB=RoomDB.getDatabase(context);
        masterDataDao=roomDB.masterDataDao();
        callOfflineECDataDao = roomDB.callOfflineECDataDao();
        callTableDao = roomDB.callTableDao();
        callsUtil = new CallsUtil(context);
    }

    @NonNull
    @Override
    public OutBoxCallAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.outbox_child_view, parent, false);
        return new OutBoxCallAdapter.ViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull OutBoxCallAdapter.ViewHolder holder, int position) {

        String type = outBoxCallLists.get(position).getCusType();
        if (type.equalsIgnoreCase("1")) {
            holder.tvName.setText(String.format("%s (Doctor) ", outBoxCallLists.get(position).getCusName()));
            holder.imgPic.setImageResource(R.drawable.map_dr_img);
        } else if (type.equalsIgnoreCase("2")) {
            holder.tvName.setText(String.format("%s (Chemist) ", outBoxCallLists.get(position).getCusName()));
            holder.imgPic.setImageResource(R.drawable.map_chemist_img);
        } else if (type.equalsIgnoreCase("3")) {
            holder.tvName.setText(String.format("%s (Stockiest) ", outBoxCallLists.get(position).getCusName()));
            holder.imgPic.setImageResource(R.drawable.map_stockist_img);
        } else if (type.equalsIgnoreCase("4")) {
            holder.tvName.setText(String.format("%s (UnDr) ", outBoxCallLists.get(position).getCusName()));
            holder.imgPic.setImageResource(R.drawable.map_unlistdr_img);
        } else if (type.equalsIgnoreCase("5")) {
            holder.tvName.setText(String.format("%s (CIP) ", outBoxCallLists.get(position).getCusName()));
            holder.imgPic.setImageResource(R.drawable.map_cip_img);
        } else if (type.equalsIgnoreCase("6")) {
            holder.tvName.setText(String.format("%s (HOS) ", outBoxCallLists.get(position).getCusName()));
            holder.imgPic.setImageResource(R.drawable.tp_hospital_icon);
        }

        holder.tvInOut.setText(String.format("IN - %s OUT - %s", outBoxCallLists.get(position).getIn(), outBoxCallLists.get(position).getOut()));
        String status = outBoxCallLists.get(position).getStatus();
        if (status.equalsIgnoreCase(Constants.WAITING_FOR_SYNC)) {
            holder.tvStatus.setText(context.getString(R.string.waiting_for_sync));
        } else if (status.equalsIgnoreCase(Constants.CALL_FAILED)) {
            holder.tvStatus.setText(context.getString(R.string.call_failed));
        } else if (status.equalsIgnoreCase(Constants.DUPLICATE_CALL)) {
            holder.tvStatus.setText(context.getString(R.string.duplicate_call));
        } else if (status.equalsIgnoreCase(Constants.EXCEPTION_ERROR)) {
            holder.tvStatus.setText(context.getString(R.string.exception_error));
        }

        holder.tvMenu.setOnClickListener(v -> {
            Context wrapper = new ContextThemeWrapper(context, R.style.popupMenuStyle);
            final PopupMenu popup = new PopupMenu(wrapper, v, Gravity.END);
            popup.inflate(R.menu.call_menu);
            popup.setOnMenuItemClickListener(menuItem -> {
                if (menuItem.getItemId() == R.id.menuSync) {
                    if (UtilityClass.isNetworkAvailable(context)) {
                        OutBoxCallList outBoxCallList = outBoxCallLists.get(position);
                        CallAPI(holder.getAbsoluteAdapterPosition(), outBoxCallList, outBoxCallLists.get(position).getJsonData(), outBoxCallLists.get(position).getCusCode(), outBoxCallLists.get(position).getCusName(), outBoxCallLists.get(position).getDates(), outBoxCallLists.get(position).getSyncCount());
                    } else {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.no_network));
                    }
                } else if (menuItem.getItemId() == R.id.menuEdit) {
                    Intent intent = new Intent(context, DCRCallActivity.class);
                    DCRCallActivity.clickedLocalDate = outBoxCallLists.get(position).getDates();
                    CallActivityCustDetails = new ArrayList<>();
                    CallActivityCustDetails.add(0, new CustList(outBoxCallLists.get(position).getCusName(), outBoxCallLists.get(position).getCusCode(), type, "", "", "", outBoxCallLists.get(position).getJsonData()));
                    intent.putExtra(Constants.DETAILING_REQUIRED, "false");
                    intent.putExtra(Constants.DCR_FROM_ACTIVITY, "edit_local");
                    intent.putExtra("remainder_save", "0");
                    intent.putExtra("hq_code", "" );

                    context.startActivity(intent);
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
                        UpdateInputSample(outBoxCallLists.get(position).getJsonData());
                        callsUtil.deleteOfflineCalls(outBoxCallLists.get(position).getCusCode(), outBoxCallLists.get(position).getCusName(), outBoxCallLists.get(position).getDates());
                        try {
                            if (!outBoxCallLists.get(position).getStatus().equalsIgnoreCase(Constants.DUPLICATE_CALL)) {
                                JSONArray jsonArray = new JSONArray(masterDataDao.getDataByKey(Constants.CALL_SYNC));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    if (jsonObject.getString("Dcr_dt").equalsIgnoreCase(outBoxCallLists.get(position).getDates()) && jsonObject.getString("CustCode").equalsIgnoreCase(outBoxCallLists.get(position).getCusCode())) {
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
                                if (outBoxCallLists.get(position).getCusType().equalsIgnoreCase("1")) {
                                    JSONObject json = new JSONObject(outBoxCallLists.get(position).getJsonData());
                                    JSONArray jsonAdditional = json.getJSONArray("AdCuss");
                                    for (int aw = 0; aw < jsonAdditional.length(); aw++) {
                                        JSONObject jsAw = jsonAdditional.getJSONObject(aw);
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                            if (jsonObject.getString("Dcr_dt").equalsIgnoreCase(outBoxCallLists.get(position).getDates()) && jsonObject.getString("CustCode").equalsIgnoreCase(jsAw.getString("Code"))) {
                                                jsonArray.remove(i);
                                            }
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Log.e("Outbox Delete call", "onBindViewHolder: " + e.getMessage());
                            e.printStackTrace();
                        }
                        if (callOfflineECDataDao.isAvailableEc(outBoxCallLists.get(position).getDates(), outBoxCallLists.get(position).getCusCode())) {
                            for (int i = 0; i < listDates.size(); i++) {
                                if (listDates.get(i).getGroupName().equalsIgnoreCase(outBoxCallLists.get(position).getDates())) {
                                    for (int j = 0; j < listDates.get(i).getChildItems().get(3).getEcModelClasses().size(); j++) {
                                        EcModelClass ecModelClass = listDates.get(i).getChildItems().get(3).getEcModelClasses().get(j);
                                        if (ecModelClass.getDates().equalsIgnoreCase(outBoxCallLists.get(position).getDates()) && ecModelClass.getCusCode().equalsIgnoreCase(outBoxCallLists.get(position).getCusCode()) && ecModelClass.getCusName().equalsIgnoreCase(outBoxCallLists.get(position).getCusName())) {
                                            listDates.get(i).getChildItems().get(3).getEcModelClasses().remove(j);
                                            j--;
                                        }
                                    }
                                }
                            }
                        }
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

    @SuppressLint("DefaultLocale")
    private void AssignCallAnalysis(String sfType, String docNameID) {
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        switch (docNameID) {
            case "1":
                String doc_current_callcount = String.valueOf(callTableDao.getCurrentMonthCallsCount(String.valueOf(currentMonth),"1"));
                if (sfType.equalsIgnoreCase("1")) {
                    callAnalysisBinding.txtDocCount.setText(String.format("%s / %d", doc_current_callcount, Doctor_list.length()));
                } else {
                    callAnalysisBinding.txtDocCount.setText(doc_current_callcount);
                }
                break;
            case "2":
                String che_current_callcount = String.valueOf(callTableDao.getCurrentMonthCallsCount(String.valueOf(currentMonth), "2"));
                if (sfType.equalsIgnoreCase("1")) {
                    callAnalysisBinding.txtDocCount.setText(String.format("%s / %d", che_current_callcount, Chemist_list.length()));
                } else {
                    callAnalysisBinding.txtDocCount.setText(che_current_callcount);
                }
                break;
            case "3":
                String stockiest_current_callcount = String.valueOf(callTableDao.getCurrentMonthCallsCount(String.valueOf(currentMonth), "3"));
                if (sfType.equalsIgnoreCase("1")) {
                    callAnalysisBinding.txtStockCount.setText(String.format("%s / %d", stockiest_current_callcount, Stockiest_list.length()));
                } else {
                    callAnalysisBinding.txtDocCount.setText(stockiest_current_callcount);
                }
                break;
            case "4":
                String unlistered_current_callcount = String.valueOf(callTableDao.getCurrentMonthCallsCount(String.valueOf(currentMonth), "4"));
                if (sfType.equalsIgnoreCase("1")) {
                    callAnalysisBinding.txtUnlistCount.setText(String.format("%s / %d", unlistered_current_callcount, unlistered_list.length()));
                } else {
                    callAnalysisBinding.txtDocCount.setText(unlistered_current_callcount);
                }
                break;
            case "5":
                String cip_current_callcount = String.valueOf(callTableDao.getCurrentMonthCallsCount(String.valueOf(currentMonth), "5"));
                if (sfType.equalsIgnoreCase("1")) {
                    callAnalysisBinding.txtCipCount.setText(String.format("%s / %d", cip_current_callcount, cip_list.length()));
                } else {
                    callAnalysisBinding.txtDocCount.setText(cip_current_callcount);
                }
                break;
            case "6":
                String hos_current_callcount = String.valueOf(callTableDao.getCurrentMonthCallsCount(String.valueOf(currentMonth), "6"));
                if (sfType.equalsIgnoreCase("1")) {
                    callAnalysisBinding.txtHosCount.setText(String.format("%s / %d", hos_current_callcount, hos_list.length()));
                } else {
                    callAnalysisBinding.txtDocCount.setText(hos_current_callcount);
                }
                break;
        }
    }

    private void CallAPI(int pos, OutBoxCallList outBoxCallList, String jsonData, String cusCode, String cusName, String date, int syncCount) {
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
                                CallsFragment.CallTodayCallsAPI(context, apiInterface, false);
                                commonUtilsMethods.showToastMessage(context, context.getString(R.string.call_saved_successfully));
                            } else if (jsonSaveRes.getString("success").equalsIgnoreCase("false") && jsonSaveRes.getString("msg").equalsIgnoreCase("Call Already Exists")) {
                                callsUtil.updateOfflineUpdateStatusEC(date, cusCode, 5, Constants.DUPLICATE_CALL, 1);
                                outBoxCallList.setStatus(Constants.DUPLICATE_CALL);
                                outBoxCallList.setSyncCount(5);
                                commonUtilsMethods.showToastMessage(context, context.getString(R.string.call_already_exist));
                            }
                            progressDialog.dismiss();
                        } catch (Exception e) {
                            callsUtil.updateOfflineUpdateStatusEC(date, cusCode, 5, Constants.EXCEPTION_ERROR, 0);
                            outBoxCallList.setStatus(Constants.EXCEPTION_ERROR);
                            outBoxCallList.setSyncCount(5);
                            Log.v("SendOutboxCall", "---" + e);
                            progressDialog.dismiss();
                        }
                    }
                }

                @SuppressLint("NotifyDataSetChanged")
                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    callsUtil.updateOfflineUpdateStatusEC(date, cusCode, syncCount + 1, Constants.CALL_FAILED, 1);
                    outBoxCallList.setStatus(Constants.CALL_FAILED);
                    outBoxCallList.setSyncCount(syncCount + 1);
                    commonUtilsMethods.showToastMessage(context, context.getString(R.string.call_failed));
                    progressDialog.dismiss();
                }
            });

        } catch (JSONException e) {
            progressDialog.dismiss();
        }
    }


    private void UpdateInputSample(String jsonArray) {
        try {
            JSONObject json = new JSONObject(jsonArray);
            //Input
            if (SharedPref.getInputValidation(context).equalsIgnoreCase("1")) {
                JSONArray jsonArrayInpStk = masterDataDao.getMasterDataTableOrNew(Constants.INPUT_BALANCE).getMasterSyncDataJsonArray();
                JSONArray jsonInput = json.getJSONArray("Inputs");
                Log.v("input_wrk", String.valueOf(jsonInput));
                if (jsonInput.length() > 0) {
                    for (int i = 0; i < jsonInput.length(); i++) {
                        JSONObject jsIp = jsonInput.getJSONObject(i);
                        //InputStockChange
                        for (int j = 0; j < jsonArrayInpStk.length(); j++) {
                            JSONObject jsonObject = jsonArrayInpStk.getJSONObject(j);
                            Log.v("chkInpStk", jsIp.getString("Code") + "-----" + jsonObject.getString("Code"));
                            if (jsIp.getString("Code").equalsIgnoreCase(jsonObject.getString("Code"))) {
                                int EnterQty = Integer.parseInt(jsIp.getString("IQty"));
                                int BalanceStock = Integer.parseInt(jsonObject.getString("Balance_Stock"));
                                int FinalStock = EnterQty + BalanceStock;
                                jsonObject.remove("Balance_Stock");
                                jsonObject.put("Balance_Stock", FinalStock);
                                break;
                            }
                        }
                    }
                    masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.INPUT_BALANCE, jsonArrayInpStk.toString(), 0));
                }
            }

            //Sample
            if (SharedPref.getSampleValidation(context).equalsIgnoreCase("1")) {
                JSONArray jsonArraySamStk = masterDataDao.getMasterDataTableOrNew(Constants.STOCK_BALANCE).getMasterSyncDataJsonArray();
                JSONArray jsonPrdArray = new JSONArray(json.getString("Products"));
                Log.v("sample_wrk", String.valueOf(jsonPrdArray));
                if (jsonPrdArray.length() > 0) {
                    //InputStockChange
                    for (int i = 0; i < jsonPrdArray.length(); i++) {
                        JSONObject js = jsonPrdArray.getJSONObject(i);
                        if (js.getString("Group").equalsIgnoreCase("0")) {
                            for (int j = 0; j < jsonArraySamStk.length(); j++) {
                                JSONObject jsonObject = jsonArraySamStk.getJSONObject(j);
                                if (js.getString("Code").equalsIgnoreCase(jsonObject.getString("Code"))) {
                                    int EnterQty = Integer.parseInt(js.getString("SmpQty"));
                                    int BalanceStock = Integer.parseInt(jsonObject.getString("Balance_Stock"));
                                    int FinalStock = EnterQty + BalanceStock;
                                    jsonObject.remove("Balance_Stock");
                                    jsonObject.put("Balance_Stock", FinalStock);
                                    break;
                                }
                            }
                        }
                    }
                    masterDataDao.saveMasterSyncData(new MasterDataTable(Constants.STOCK_BALANCE, jsonArraySamStk.toString(), 0));
                }
            }


        } catch (Exception e) {

        }
    }

    @Override
    public int getItemCount() {
        return outBoxCallLists.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void removeAt(int position) {
        outBoxCallLists.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, outBoxCallLists.size());
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
