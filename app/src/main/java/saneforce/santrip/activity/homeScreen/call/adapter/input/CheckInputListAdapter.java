package saneforce.santrip.activity.homeScreen.call.adapter.input;


import static saneforce.santrip.activity.homeScreen.call.DCRCallActivity.StockInput;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.call.DCRCallActivity;
import saneforce.santrip.activity.homeScreen.call.fragments.InputFragment;
import saneforce.santrip.activity.homeScreen.call.pojo.CallCommonCheckedList;
import saneforce.santrip.activity.homeScreen.call.pojo.input.SaveCallInputList;
import saneforce.santrip.commonClasses.CommonUtilsMethods;


public class CheckInputListAdapter extends RecyclerView.Adapter<CheckInputListAdapter.ViewHolder> {
    public static ArrayList<SaveCallInputList> saveCallInputListArrayList;
    public static boolean isCheckedInp;
    public static String UnSelectedInpCode = "";
    Context context;
    ArrayList<CallCommonCheckedList> checked_arrayList;
    @SuppressLint("StaticFieldLeak")
    FinalInputCallAdapter finalInputCallAdapter;
    CommonUtilsMethods commonUtilsMethods;
    Activity activity;

    public CheckInputListAdapter(Activity activity, Context context, ArrayList<CallCommonCheckedList> checked_arrayList) {
        this.activity = activity;
        this.context = context;
        this.checked_arrayList = checked_arrayList;
    }

    public CheckInputListAdapter(Activity activity, Context context, ArrayList<CallCommonCheckedList> checked_arrayList, ArrayList<SaveCallInputList> saveCallInputLists) {
        this.activity = activity;
        this.context = context;
        this.checked_arrayList = checked_arrayList;
        saveCallInputListArrayList = saveCallInputLists;
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

        if (checked_arrayList.get(position).isCheckedItem()) {
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
                if (DCRCallActivity.InputValidation.equalsIgnoreCase("1")) {
                    for (int i = 0; i < StockInput.size(); i++) {
                        if (StockInput.get(i).getStockCode().equalsIgnoreCase(checked_arrayList.get(position).getCode())) {
                            checked_arrayList.set(position, new CallCommonCheckedList(checked_arrayList.get(position).getName(), checked_arrayList.get(position).getCode(), StockInput.get(i).getCurrentStock(), false));
                        }
                    }
                    if (Integer.parseInt(checked_arrayList.get(position).getStock_balance()) > 0) {
                        CheckBoxContents(holder.checkBox, holder.tv_name, holder.getBindingAdapterPosition());
                    } else {
                        holder.checkBox.setChecked(false);
                        commonUtilsMethods.ShowToast(context, context.getString(R.string.no_qty_input), 100);
                    }
                } else {
                    CheckBoxContents(holder.checkBox, holder.tv_name, holder.getBindingAdapterPosition());
                }
            }
        });
    }


    @SuppressLint("NotifyDataSetChanged")
    private void CheckBoxContents(CheckBox checkBox, TextView tv_name, int adapterPosition) {
        if (checkBox.isChecked()) {
            tv_name.setTextColor(ContextCompat.getColor(context, R.color.cheked_txt_color));
            checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green_2)));
            isCheckedInp = false;
            checked_arrayList.get(adapterPosition).setCheckedItem(true);
            saveCallInputListArrayList.add(new SaveCallInputList(checked_arrayList.get(adapterPosition).getName(), checked_arrayList.get(adapterPosition).getCode(), "", checked_arrayList.get(adapterPosition).getStock_balance(), checked_arrayList.get(adapterPosition).getStock_balance()));
            AssignRecyclerView(activity, context, saveCallInputListArrayList, checked_arrayList);
        } else {
            new CountDownTimer(80, 80) {
                public void onTick(long millisUntilFinished) {
                    checkBox.setEnabled(false);
                }

                public void onFinish() {
                    checkBox.setEnabled(true);
                }
            }.start();
            tv_name.setTextColor(ContextCompat.getColor(context, R.color.bg_txt_color));
            checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.bg_txt_color)));
            isCheckedInp = true;
            UnSelectedInpCode = checked_arrayList.get(adapterPosition).getCode();
            checked_arrayList.get(adapterPosition).setCheckedItem(false);
            AssignRecyclerView(activity, context, saveCallInputListArrayList, checked_arrayList);
            finalInputCallAdapter.notifyDataSetChanged();
        }
    }

    private void AssignRecyclerView(Activity activity, Context context, ArrayList<SaveCallInputList> saveCallInputListArrayList, ArrayList<CallCommonCheckedList> callCommonCheckedListArrayList) {
        finalInputCallAdapter = new FinalInputCallAdapter(activity, context, saveCallInputListArrayList, callCommonCheckedListArrayList);
        commonUtilsMethods.recycleTestWithDivider(InputFragment.fragmentInputBinding.rvListInput);
        InputFragment.fragmentInputBinding.rvListInput.setAdapter(finalInputCallAdapter);
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