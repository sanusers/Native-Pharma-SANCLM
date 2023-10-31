package saneforce.sanclm.activity.homeScreen.call.adapter.product;

import static saneforce.sanclm.activity.homeScreen.call.fragments.ProductFragment.productsBinding;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.pojo.CallCommonCheckedList;
import saneforce.sanclm.activity.homeScreen.call.pojo.product.SaveCallProductList;
import saneforce.sanclm.commonClasses.CommonSharedPreference;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;

public class CallProductListAdapter extends RecyclerView.Adapter<CallProductListAdapter.ViewHolder> {
    public static int pos;
    public static ArrayList<SaveCallProductList> saveCallProductListArrayList;
    public static boolean isCheckedPrd;
    public static String UnSelectedPrdCode = "";
    Context context;
    ArrayList<CallCommonCheckedList> callCommonCheckedListArrayList;
    SaveProductCallAdapter saveProductCallAdapter;
    CommonSharedPreference mcommonSharedPreference;
    CommonUtilsMethods commonUtilsMethods;
    Activity activity;


    public CallProductListAdapter(Activity activity, Context context, ArrayList<CallCommonCheckedList> callCommonCheckedListArrayList) {
        this.activity = activity;
        this.context = context;
        this.callCommonCheckedListArrayList = callCommonCheckedListArrayList;
    }

    public CallProductListAdapter(Activity activity, Context context, ArrayList<CallCommonCheckedList> callCommonCheckedListArrayList, ArrayList<SaveCallProductList> saveCallProductListArrayList) {
        this.activity = activity;
        this.context = context;
        this.callCommonCheckedListArrayList = callCommonCheckedListArrayList;
        CallProductListAdapter.saveCallProductListArrayList = saveCallProductListArrayList;
    }

    public CallProductListAdapter(Context context) {
        this.context = context;
    }

    public static int getPosition() {
        return pos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_checked_data_prd, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"NotifyDataSetChanged", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        mcommonSharedPreference = new CommonSharedPreference(context);
        commonUtilsMethods = new CommonUtilsMethods(context);
        holder.tv_name.setText(callCommonCheckedListArrayList.get(position).getName());
        holder.checkBox.setChecked(callCommonCheckedListArrayList.get(position).isCheckedItem());

        holder.tv_category.setVisibility(View.VISIBLE);
        holder.tv_category.setText(callCommonCheckedListArrayList.get(position).getCategory());

        if (callCommonCheckedListArrayList.get(position).getCategory().equalsIgnoreCase("Sale")) {
            holder.tv_category.setText("SL");
        } else if (callCommonCheckedListArrayList.get(position).getCategory().equalsIgnoreCase("Sample")) {
            holder.tv_category.setText("SM");
        } else if (callCommonCheckedListArrayList.get(position).getCategory().equalsIgnoreCase("Sale/Sample")) {
            holder.tv_category.setText("SL/SM");
        }


        holder.checkBox.setChecked(callCommonCheckedListArrayList.get(position).isCheckedItem());

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


        if (holder.tv_category.getText().toString().equalsIgnoreCase("P1") || holder.tv_category.getText().toString().equalsIgnoreCase("P2")) {
            holder.tv_category.setTextColor(context.getResources().getColor(R.color.txt_priority));
            holder.tv_category.setBackground(context.getResources().getDrawable(R.drawable.bg_priority));
        } else if (holder.tv_category.getText().toString().equalsIgnoreCase("SM")) {
            holder.tv_category.setTextColor(context.getResources().getColor(R.color.txt_sample));
            holder.tv_category.setBackground(context.getResources().getDrawable(R.drawable.bg_sample));
        } else if (holder.tv_category.getText().toString().equalsIgnoreCase("SL")) {
            holder.tv_category.setTextColor(context.getResources().getColor(R.color.txt_sale));
            holder.tv_category.setBackground(context.getResources().getDrawable(R.drawable.bg_sale));
        } else if (holder.tv_category.getText().toString().equalsIgnoreCase("SL/SM")) {
            holder.tv_category.setTextColor(context.getResources().getColor(R.color.txt_sale_sample));
            holder.tv_category.setBackground(context.getResources().getDrawable(R.drawable.bg_sale_sample));
        }


        holder.tv_name.setOnClickListener(view -> {
            commonUtilsMethods.displayPopupWindow(activity, context, view, callCommonCheckedListArrayList.get(position).getName());
        });


        holder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            Log.v("sdd", "00");
            if (holder.checkBox.isPressed()) {
                Log.v("sdd", "11");
                if (holder.checkBox.isChecked()) {
                    holder.tv_name.setTextColor(context.getResources().getColor(R.color.cheked_txt_color));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.checkBox.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.green_2)));
                    }
                    isCheckedPrd = false;
                    callCommonCheckedListArrayList.get(position).setCheckedItem(true);
                    saveCallProductListArrayList.add(new SaveCallProductList(callCommonCheckedListArrayList.get(position).getName(), callCommonCheckedListArrayList.get(position).getCode(), "20", "", "", "", "1", true));
                    AssignRecyclerView(activity, context, saveCallProductListArrayList, callCommonCheckedListArrayList);
                } else {
                    holder.tv_name.setTextColor(context.getResources().getColor(R.color.bg_txt_color));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        holder.checkBox.setButtonTintList(ColorStateList.valueOf(context.getResources().getColor(R.color.bg_txt_color)));
                    }
                    isCheckedPrd = true;
                    UnSelectedPrdCode = callCommonCheckedListArrayList.get(position).getCode();
                    callCommonCheckedListArrayList.get(position).setCheckedItem(false);
                    commonUtilsMethods.recycleTestWithDivider(productsBinding.rvListPrd);
                    AssignRecyclerView(activity, context, saveCallProductListArrayList, callCommonCheckedListArrayList);
                    saveProductCallAdapter.notifyDataSetChanged();
                }
            }
        });
    }

 /*   private void displayPopupWindow(View view, String name) {
        PopupWindow popup = new PopupWindow(context);
        View layout = activity.getLayoutInflater().inflate(popup_text, null);
        popup.setContentView(layout);
        popup.setBackgroundDrawable(new ColorDrawable(
                android.graphics.Color.TRANSPARENT));
        TextView tv_name = layout.findViewById(R.id.tv_name);
        tv_name.setText(name);
        popup.setOutsideTouchable(true);
        popup.showAsDropDown(view);
    }*/

    private void AssignRecyclerView(Activity activity, Context context, ArrayList<SaveCallProductList> saveCallProductListArrayList, ArrayList<CallCommonCheckedList> callCommonCheckedListArrayList) {
        saveProductCallAdapter = new SaveProductCallAdapter(activity, context, saveCallProductListArrayList, callCommonCheckedListArrayList);
        commonUtilsMethods.recycleTestWithDivider(productsBinding.rvListPrd);
        productsBinding.rvListPrd.setAdapter(saveProductCallAdapter);
    }

    @Override
    public int getItemCount() {
        return callCommonCheckedListArrayList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<CallCommonCheckedList> filterdNames) {
        this.callCommonCheckedListArrayList = filterdNames;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_category;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_data_name);
            tv_category = itemView.findViewById(R.id.tv_tag_category);
            checkBox = itemView.findViewById(R.id.chk_box);
        }
    }
}
