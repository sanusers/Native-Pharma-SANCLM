package saneforce.sanclm.activity.homeScreen.call.adapter.additionalCalls;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.adapter.additionalCalls.finalSavedAdapter.FinalAdditionalCallAdapter;
import saneforce.sanclm.activity.homeScreen.call.fragments.AdditionalCallFragment;
import saneforce.sanclm.activity.homeScreen.call.pojo.CallCommonCheckedList;
import saneforce.sanclm.activity.homeScreen.call.pojo.additionalCalls.SaveAdditionalCall;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;

public class AdditionalCusListAdapter extends RecyclerView.Adapter<AdditionalCusListAdapter.ViewHolder> {
    public static ArrayList<SaveAdditionalCall> saveAdditionalCallArrayList;
    public static boolean isCheckedAddCall;
    public static String UnSelectedDrCode = "";
    Context context;
    Activity activity;
    ArrayList<CallCommonCheckedList> checked_arrayList;
    FinalAdditionalCallAdapter AdapterSaveAdditionalCall;
    CommonUtilsMethods commonUtilsMethods;

    public AdditionalCusListAdapter(Activity activity, Context context, ArrayList<CallCommonCheckedList> checked_arrayList, ArrayList<SaveAdditionalCall> saveAdditionalCallArrayList) {
        this.activity = activity;
        this.context = context;
        this.checked_arrayList = checked_arrayList;
        AdditionalCusListAdapter.saveAdditionalCallArrayList = saveAdditionalCallArrayList;
    }


    public AdditionalCusListAdapter(Activity activity, Context context, ArrayList<CallCommonCheckedList> checked_arrayList) {
        this.activity = activity;
        this.context = context;
        this.checked_arrayList = checked_arrayList;
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
        commonUtilsMethods = new CommonUtilsMethods(context);
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
                }
            });
    }

    private void AssignRecyclerView(Activity activity, Context context, ArrayList<SaveAdditionalCall> saveAdditionalCallArrayList, ArrayList<CallCommonCheckedList> cusListArrayList) {
        AdapterSaveAdditionalCall = new FinalAdditionalCallAdapter(activity, context, saveAdditionalCallArrayList, cusListArrayList);
        commonUtilsMethods.recycleTestWithoutDivider(AdditionalCallFragment.rv_add_call_list);
        AdditionalCallFragment.rv_add_call_list.setAdapter(AdapterSaveAdditionalCall);
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
