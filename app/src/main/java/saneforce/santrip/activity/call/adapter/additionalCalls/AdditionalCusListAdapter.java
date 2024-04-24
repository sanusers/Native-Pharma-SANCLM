package saneforce.santrip.activity.call.adapter.additionalCalls;

import static saneforce.santrip.activity.call.DCRCallActivity.CallActivityCustDetails;
import static saneforce.santrip.activity.call.DCRCallActivity.clickedLocalDate;
import static saneforce.santrip.activity.call.DCRCallActivity.isFromActivity;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import saneforce.santrip.R;
import saneforce.santrip.activity.call.adapter.additionalCalls.finalSavedAdapter.FinalAdditionalCallAdapter;
import saneforce.santrip.activity.call.fragments.additionalCall.AdditionalCallFragment;
import saneforce.santrip.activity.call.pojo.additionalCalls.SaveAdditionalCall;
import saneforce.santrip.activity.call.pojo.CallCommonCheckedList;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.santrip.roomdatabase.MasterTableDetails.MasterDataTable;
import saneforce.santrip.roomdatabase.RoomDB;
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.storage.SharedPref;

public class AdditionalCusListAdapter extends RecyclerView.Adapter<AdditionalCusListAdapter.ViewHolder> {
    public static ArrayList<SaveAdditionalCall> saveAdditionalCallArrayList;
    public static boolean isCheckedAddCall;
    public static String UnSelectedDrCode = "";
    Context context;
    Activity activity;
    ArrayList<CallCommonCheckedList> checked_arrayList;
    FinalAdditionalCallAdapter AdapterSaveAdditionalCall;
    CommonUtilsMethods commonUtilsMethods;
//    SQLite sqLite;
    RoomDB roomDB;
    MasterDataDao masterDataDao;

    public AdditionalCusListAdapter(Activity activity, Context context, ArrayList<CallCommonCheckedList> checked_arrayList, ArrayList<SaveAdditionalCall> saveAdditionalCallArrayList) {
        this.activity = activity;
        this.context = context;
        this.checked_arrayList = checked_arrayList;
        AdditionalCusListAdapter.saveAdditionalCallArrayList = saveAdditionalCallArrayList;
        commonUtilsMethods = new CommonUtilsMethods(context);
//        sqLite = new SQLite(context);
        roomDB=RoomDB.getDatabase(context);
        masterDataDao= roomDB.masterDataDao();
    }


    public AdditionalCusListAdapter(Activity activity, Context context, ArrayList<CallCommonCheckedList> checked_arrayList) {
        this.activity = activity;
        this.context = context;
        this.checked_arrayList = checked_arrayList;
        commonUtilsMethods = new CommonUtilsMethods(context);
//        sqLite = new SQLite(context);
        roomDB=RoomDB.getDatabase(context);
        masterDataDao= roomDB.masterDataDao();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_checked_data, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_name.setText(checked_arrayList.get(position).getName());
        holder.checkBox.setChecked(checked_arrayList.get(position).isCheckedItem());

        if (holder.checkBox.isChecked()) {
            holder.checkBox.setChecked(true);
            holder.tv_name.setTextColor(ContextCompat.getColor(context, R.color.cheked_txt_color));
            holder.checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green_2)));
        } else {
            holder.checkBox.setChecked(false);
            holder.tv_name.setTextColor(ContextCompat.getColor(context, R.color.bg_txt_color));
            holder.checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.bg_txt_color)));
        }

        holder.tv_name.setOnClickListener(view -> commonUtilsMethods.displayPopupWindow(activity, context, view, checked_arrayList.get(position).getName()));

        holder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (holder.checkBox.isPressed()) {
                if (isValidAddCall(checked_arrayList.get(position).getCode(), checked_arrayList.get(position).getTotalVisit())) {
                    if (holder.checkBox.isChecked()) {
                        holder.tv_name.setTextColor(ContextCompat.getColor(context, R.color.cheked_txt_color));
                        holder.checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green_2)));
                        isCheckedAddCall = false;
                        checked_arrayList.get(position).setCheckedItem(true);
                        saveAdditionalCallArrayList.add(new SaveAdditionalCall(checked_arrayList.get(position).getName(), checked_arrayList.get(position).getCode(), checked_arrayList.get(position).getTown_name(), checked_arrayList.get(position).getTown_code(), true));
                        AssignRecyclerView(activity, context, saveAdditionalCallArrayList, checked_arrayList);
                    } else {
                        new CountDownTimer(80, 80) {
                            public void onTick(long millisUntilFinished) {
                                holder.checkBox.setEnabled(false);
                            }

                            public void onFinish() {
                                holder.checkBox.setEnabled(true);
                            }
                        }.start();

                        holder.tv_name.setTextColor(ContextCompat.getColor(context, R.color.bg_txt_color));
                        holder.checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.bg_txt_color)));
                        isCheckedAddCall = true;
                        UnSelectedDrCode = checked_arrayList.get(position).getCode();
                        checked_arrayList.get(position).setCheckedItem(false);
                        AssignRecyclerView(activity, context, saveAdditionalCallArrayList, checked_arrayList);
                        AdapterSaveAdditionalCall.notifyDataSetChanged();
                    }
                } else {
                    checked_arrayList.get(position).setCheckedItem(false);
                    notifyDataSetChanged();
                }
            }
        });
    }

    private boolean isValidAddCall(String cusCode, String totalVisit) {
        boolean isValid = false;
        try {
            boolean isVisitedToday = false;
            boolean isLocal = false;

            if (isFromActivity.equalsIgnoreCase("edit_local")) {


                JSONArray jsonArray = new JSONArray(masterDataDao.getDataByKey(Constants.CALL_SYNC));
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if (jsonObject.getString("Dcr_dt").equalsIgnoreCase(clickedLocalDate) && jsonObject.getString("CustCode").equalsIgnoreCase(cusCode)) {
                        jsonArray.remove(i);
                        isLocal = true;
                        isValid = true;
                        break;
                    }
                }

                MasterDataTable inputdata =new MasterDataTable();
                inputdata.setMasterKey(Constants.CALL_SYNC);
                inputdata.setMasterValues(jsonArray.toString());
                inputdata.setSyncStatus(0);
                MasterDataTable nChecked = masterDataDao.getMasterSyncDataByKey(Constants.CALL_SYNC);
                if(nChecked !=null){
                    masterDataDao.updateData(Constants.CALL_SYNC, jsonArray.toString());
                }else {
                    masterDataDao.insert(inputdata);
                }
            }

            if (!isLocal) {
                if (!cusCode.equalsIgnoreCase(CallActivityCustDetails.get(0).getCode())) {
                    JSONArray jsonArray = new JSONArray(masterDataDao.getDataByKey(Constants.CALL_SYNC));
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (jsonObject.getString("Dcr_dt").equalsIgnoreCase(CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd")) && jsonObject.getString("CustCode").equalsIgnoreCase(cusCode)) {
                            isVisitedToday = true;
                            break;
                        }
                    }

                    if (!isVisitedToday) {
                        if (SharedPref.getVstNd(context).equalsIgnoreCase("0") && SharedPref.getSfType(context).equalsIgnoreCase("1")) {
                            int count = 0;
                            JSONArray jsonVisit = new JSONArray(masterDataDao.getDataByKey(Constants.CALL_SYNC));
                            for (int i = 0; i < jsonVisit.length(); i++) {
                                JSONObject jsonObject = jsonVisit.getJSONObject(i);
                                if (jsonObject.getString("CustCode").equalsIgnoreCase(cusCode)) {
                                    count++;
                                }
                            }
                            if (count < Integer.parseInt(totalVisit)) {
                                isValid = true;
                            } else {
                                commonUtilsMethods.showToastMessage(context, context.getString(R.string.no_of_visit));
                            }
                        } else {
                            isValid = true;
                        }
                    } else {
                        commonUtilsMethods.showToastMessage(context, context.getString(R.string.already_visited));
                    }
                } else {
                    commonUtilsMethods.showToastMessage(context, context.getString(R.string.selected_same_doctor));
                }
            }
        } catch (Exception e) {
            Log.v("Call_Data", "---" + e);
        }
        return isValid;
    }

    private void AssignRecyclerView(Activity activity, Context context, ArrayList<SaveAdditionalCall> saveAdditionalCallArrayList, ArrayList<CallCommonCheckedList> cusListArrayList) {
        AdapterSaveAdditionalCall = new FinalAdditionalCallAdapter(activity, context, saveAdditionalCallArrayList, cusListArrayList);
        commonUtilsMethods.recycleTestWithoutDivider(AdditionalCallFragment.additionalCallBinding.rvListAdditional);
        AdditionalCallFragment.additionalCallBinding.rvListAdditional.setAdapter(AdapterSaveAdditionalCall);
    }

    @Override
    public int getItemCount() {
        return checked_arrayList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<CallCommonCheckedList> filteredNames) {
        this.checked_arrayList = filteredNames;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_data_name);
            checkBox = itemView.findViewById(R.id.chk_box);
        }
    }
}
