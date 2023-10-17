package saneforce.sanclm.activity.homeScreen.call.adapter.additionalCalls;

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

import saneforce.sanclm.commonClasses.CommonSharedPreference;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.adapter.additionalCalls.finalSavedAdapter.SaveAdditionalCallAdapter;
import saneforce.sanclm.activity.homeScreen.call.fragments.AdditionalCallFragment;
import saneforce.sanclm.activity.homeScreen.call.pojo.additionalCalls.SaveAdditionalCall;
import saneforce.sanclm.activity.homeScreen.call.pojo.CallCommonCheckedList;

public class CallAddCustListAdapter extends RecyclerView.Adapter<CallAddCustListAdapter.ViewHolder> {
    Context context;
    Activity activity;
    ArrayList<CallCommonCheckedList> checked_arrayList;
    public static ArrayList<SaveAdditionalCall> saveAdditionalCallArrayList;

    SaveAdditionalCallAdapter AdaptersaveAdditionalCall;
    CommonSharedPreference mCommonSharedPreference;
    CommonUtilsMethods commonUtilsMethods;

    public CallAddCustListAdapter(Activity activity,Context context, ArrayList<CallCommonCheckedList> checked_arrayList, ArrayList<SaveAdditionalCall> saveAdditionalCallArrayList) {
        this.activity = activity;
        this.context = context;
        this.checked_arrayList = checked_arrayList;
        CallAddCustListAdapter.saveAdditionalCallArrayList = saveAdditionalCallArrayList;
    }


    public CallAddCustListAdapter(Activity activity,Context context, ArrayList<CallCommonCheckedList> checked_arrayList) {
       this.activity = activity;
        this.context = context;
        this.checked_arrayList = checked_arrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_checked_data_add_calls, parent, false);
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
            if (holder.tv_name.getText().toString().length() > 12) {
                commonUtilsMethods.displayPopupWindow(activity,context,view, checked_arrayList.get(position).getName());
            }
        });

        holder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
          //  SaveAdditionalCallAdapter.nestedAddSampleCallDetails.clear();
          //  SaveAdditionalCallAdapter.nestedAddInputCallDetails.clear();
            if (holder.checkBox.isPressed()) {
                if (holder.checkBox.isChecked()) {
                    holder.tv_name.setTextColor(context.getResources().getColor(R.color.cheked_txt_color));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.checkBox.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.green_2)));
                    }
                    mCommonSharedPreference.setValueToPreference("checked_add_call", false);
                    checked_arrayList.get(position).setCheckedItem(true);
                    saveAdditionalCallArrayList.add(new SaveAdditionalCall(checked_arrayList.get(position).getName()));
                    AssignRecyclerView(activity, context, saveAdditionalCallArrayList, checked_arrayList);
                } else {
                    holder.tv_name.setTextColor(context.getResources().getColor(R.color.bg_txt_color));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.checkBox.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.bg_txt_color)));
                    }
                    mCommonSharedPreference.setValueToPreference("checked_add_call", true);
                    mCommonSharedPreference.setValueToPreference("unselect_data_add_call", checked_arrayList.get(position).getName());
                    checked_arrayList.get(position).setCheckedItem(false);
                    AssignRecyclerView(activity, context, saveAdditionalCallArrayList, checked_arrayList);
                    AdaptersaveAdditionalCall.notifyDataSetChanged();
                }
            }
        });
    }


    private void AssignRecyclerView(Activity activity,Context context, ArrayList<SaveAdditionalCall> saveAdditionalCallArrayList, ArrayList<CallCommonCheckedList> custListArrayList) {
        AdaptersaveAdditionalCall = new SaveAdditionalCallAdapter(activity,context, saveAdditionalCallArrayList, custListArrayList);
        commonUtilsMethods.recycleTestWithoutDivider(AdditionalCallFragment.rv_add_call_list);
        AdditionalCallFragment.rv_add_call_list.setAdapter(AdaptersaveAdditionalCall);
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
