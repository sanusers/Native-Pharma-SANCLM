package saneforce.sanclm.activity.homeScreen.call.adapter.input;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.DCRCallActivity;
import saneforce.sanclm.activity.homeScreen.call.fragments.InputFragment;
import saneforce.sanclm.activity.homeScreen.call.pojo.CallCommonCheckedList;
import saneforce.sanclm.activity.homeScreen.call.pojo.input.SaveCallInputList;
import saneforce.sanclm.commonClasses.CommonSharedPreference;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;


public class CallInputListAdapter extends RecyclerView.Adapter<CallInputListAdapter.ViewHolder> {
    public static ArrayList<SaveCallInputList> saveCallInputListArrayList;
    public static boolean isCheckedInp;
    public static String UnSelectedInpCode = "";
    Context context;
    ArrayList<CallCommonCheckedList> checked_arrayList;
    SaveInputCallAdapter saveInputCallAdapter;
    CommonSharedPreference mCommonSharedPreference;
    CommonUtilsMethods commonUtilsMethods;
    Activity activity;

    public CallInputListAdapter(Activity activity, Context context, ArrayList<CallCommonCheckedList> checked_arrayList) {
        this.activity = activity;
        this.context = context;
        this.checked_arrayList = checked_arrayList;
    }

    public CallInputListAdapter(Activity activity, Context context, ArrayList<CallCommonCheckedList> checked_arrayList, ArrayList<SaveCallInputList> saveCallInputLists) {
        this.activity = activity;
        this.context = context;
        this.checked_arrayList = checked_arrayList;
        saveCallInputListArrayList = saveCallInputLists;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_checked_data_inp, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        mCommonSharedPreference = new CommonSharedPreference(context);
        commonUtilsMethods = new CommonUtilsMethods(context);
        holder.tv_name.setText(checked_arrayList.get(position).getName());
        holder.checkBox.setChecked(checked_arrayList.get(position).isCheckedItem());


        if (holder.checkBox.isChecked()) {
            holder.tv_name.setTextColor(context.getResources().getColor(R.color.cheked_txt_color));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.checkBox.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.green_2)));
            }
        } else {
            holder.tv_name.setTextColor(context.getResources().getColor(R.color.bg_txt_color));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.checkBox.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.bg_txt_color)));
            }
        }

        holder.tv_name.setOnClickListener(view -> {
            commonUtilsMethods.displayPopupWindow(activity, context, view, checked_arrayList.get(position).getName());
        });


        holder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if (holder.checkBox.isPressed()) {
                if (holder.checkBox.isChecked()) {
                    holder.tv_name.setTextColor(context.getResources().getColor(R.color.cheked_txt_color));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.checkBox.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.green_2)));
                    }
                    isCheckedInp = false;
                    // mCommonSharedPreference.setValueToPreference("checked_input", false);
                    checked_arrayList.get(position).setCheckedItem(true);
                    saveCallInputListArrayList.add(new SaveCallInputList(checked_arrayList.get(position).getName(), checked_arrayList.get(position).getCode(), "", "20"));
                    AssignRecyclerView(activity, context, saveCallInputListArrayList, checked_arrayList);
                } else {
                    holder.tv_name.setTextColor(context.getResources().getColor(R.color.bg_txt_color));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.checkBox.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.bg_txt_color)));
                    }
                    isCheckedInp = true;
                    UnSelectedInpCode = checked_arrayList.get(position).getCode();
                    // mCommonSharedPreference.setValueToPreference("checked_input", true);
                    // mCommonSharedPreference.setValueToPreference("unselect_data_inp", checked_arrayList.get(position).getName());
                    checked_arrayList.get(position).setCheckedItem(false);
                    AssignRecyclerView(activity, context, saveCallInputListArrayList, checked_arrayList);
                    saveInputCallAdapter.notifyDataSetChanged();
                }
            }
        });
    }

    private void AssignRecyclerView(Activity activity, Context context, ArrayList<SaveCallInputList> saveCallInputListArrayList, ArrayList<CallCommonCheckedList> callCommonCheckedListArrayList) {
        saveInputCallAdapter = new SaveInputCallAdapter(activity, context, saveCallInputListArrayList, callCommonCheckedListArrayList);
        commonUtilsMethods.recycleTestWithDivider(InputFragment.fragmentInputBinding.rvListInput);
        InputFragment.fragmentInputBinding.rvListInput.setAdapter(saveInputCallAdapter);
    }

    @Override
    public int getItemCount() {
        return checked_arrayList.size();
    }

    public void filterList(ArrayList<CallCommonCheckedList> filterdNames) {
        this.checked_arrayList = filterdNames;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_data_name);
            checkBox = itemView.findViewById(R.id.chk_box);
        }
    }
}
