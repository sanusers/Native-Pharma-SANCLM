package saneforce.sanzen.activity.call.adapter.product;

import static saneforce.sanzen.activity.call.DCRCallActivity.StockSample;

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

import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.DCRCallActivity;
import saneforce.sanzen.activity.call.fragments.product.ProductFragment;
import saneforce.sanzen.activity.call.pojo.CallCommonCheckedList;
import saneforce.sanzen.activity.call.pojo.product.SaveCallProductList;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;

public class CheckProductListAdapter extends RecyclerView.Adapter<CheckProductListAdapter.ViewHolder> {
    public static int pos;
    public static ArrayList<SaveCallProductList> saveCallProductListArrayList;
    public static boolean isCheckedPrd, noProductSelected = false;
    public static String UnSelectedPrdCode = "";
    Context context;
    ArrayList<CallCommonCheckedList> callCommonCheckedListArrayList;
    FinalProductCallAdapter finalProductCallAdapter;
    CommonUtilsMethods commonUtilsMethods;
    Activity activity;
//    private ViewHolder noProductHolder;

    public CheckProductListAdapter(Activity activity, Context context, ArrayList<CallCommonCheckedList> callCommonCheckedListArrayList) {
        this.activity = activity;
        this.context = context;
        this.callCommonCheckedListArrayList = callCommonCheckedListArrayList;
    }

    public CheckProductListAdapter(Activity activity, Context context, ArrayList<CallCommonCheckedList> callCommonCheckedListArrayList, ArrayList<SaveCallProductList> saveCallProductListArrayList) {
        this.activity = activity;
        this.context = context;
        this.callCommonCheckedListArrayList = callCommonCheckedListArrayList;
        CheckProductListAdapter.saveCallProductListArrayList = saveCallProductListArrayList;
        checkAndSetNoProductCheckedOrUnchecked();
    }

    public static int getPosition() {
        return pos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_checked_data, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"NotifyDataSetChanged", "UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        commonUtilsMethods = new CommonUtilsMethods(context);
        holder.tv_name.setText(callCommonCheckedListArrayList.get(position).getName());
        holder.tv_category.setVisibility(View.VISIBLE);
        holder.tv_category.setText(callCommonCheckedListArrayList.get(position).getCategory());
//        if(callCommonCheckedListArrayList.get(position).getCode().equalsIgnoreCase("-10")) {
//            noProductHolder = holder;
//            if(DCRCallActivity.PrdMandatory != null && DCRCallActivity.PrdMandatory.equalsIgnoreCase("1")) {
//            noProductHolder.checkBox.setChecked(false);
//            }else {
//                checkAndSetNoProductCheckedOrUnchecked();
//            }
//        }
        if(callCommonCheckedListArrayList.get(position).getCategory().equalsIgnoreCase("Sale")) {
            holder.tv_category.setText("SL");
        }else if(callCommonCheckedListArrayList.get(position).getCategory().equalsIgnoreCase("Sample")) {
            holder.tv_category.setText("SM");
        }else if(callCommonCheckedListArrayList.get(position).getCategory().equalsIgnoreCase("Sale/Sample")) {
            holder.tv_category.setText("SL/SM");
        }
        if(callCommonCheckedListArrayList.get(position).isCheckedItem()) {
            if(callCommonCheckedListArrayList.get(position).getCode().equalsIgnoreCase("-10")) {
                noProductSelected = true;
            }
            holder.checkBox.setChecked(true);
            holder.tv_name.setTextColor(ContextCompat.getColor(context, R.color.cheked_txt_color));
            holder.checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green_2)));
        }else {
            if(callCommonCheckedListArrayList.get(position).getCode().equalsIgnoreCase("-10")) {
                noProductSelected = false;
            }
            holder.checkBox.setChecked(false);
            holder.tv_name.setTextColor(ContextCompat.getColor(context, R.color.bg_txt_color));
            holder.checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.bg_txt_color)));
        }
        if(holder.tv_category.getText().toString().contains("P")) {
            holder.tv_category.setTextColor(ContextCompat.getColor(context, R.color.txt_priority));
            holder.tv_category.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_priority));
        }else if(holder.tv_category.getText().toString().equalsIgnoreCase("SM")) {
            holder.tv_category.setTextColor(ContextCompat.getColor(context, R.color.txt_sample));
            holder.tv_category.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_sample));
        }else if(holder.tv_category.getText().toString().equalsIgnoreCase("SL")) {
            holder.tv_category.setTextColor(ContextCompat.getColor(context, R.color.txt_sale));
            holder.tv_category.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_sale));
        }else if(holder.tv_category.getText().toString().equalsIgnoreCase("SL/SM")) {
            holder.tv_category.setTextColor(ContextCompat.getColor(context, R.color.txt_sale_sample));
            holder.tv_category.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_sale_sample));
        }else {
            holder.tv_category.setTextColor(ContextCompat.getColor(context, R.color.white));
            holder.tv_category.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_white_without_border));
        }
        holder.tv_name.setOnClickListener(view -> commonUtilsMethods.displayPopupWindow(context, view, callCommonCheckedListArrayList.get(position).getName()));

        holder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
            if(holder.checkBox.isPressed()) {
                if(!callCommonCheckedListArrayList.get(position).getCode().equalsIgnoreCase("-10")) {
                    if(DCRCallActivity.SampleValidation.equalsIgnoreCase("1")) {
                        for (int i = 0; i<StockSample.size(); i++) {
                            if(StockSample.get(i).getStockCode().equalsIgnoreCase(callCommonCheckedListArrayList.get(position).getCode())) {
                                callCommonCheckedListArrayList.set(position, new CallCommonCheckedList(callCommonCheckedListArrayList.get(position).getName(), callCommonCheckedListArrayList.get(position).getCode(), StockSample.get(i).getCurrentStock(), holder.checkBox.isChecked(), callCommonCheckedListArrayList.get(position).getCategory(), callCommonCheckedListArrayList.get(position).getCategoryExtra()));
                                break;
                            }
                        }
                        if(callCommonCheckedListArrayList.get(position).getCategoryExtra().equalsIgnoreCase("Sale") || callCommonCheckedListArrayList.get(position).getCategoryExtra().equalsIgnoreCase("Sale/Sample")) {
                            CheckBoxContents(holder.checkBox, holder.tv_name, holder.getBindingAdapterPosition());
                        }else if(callCommonCheckedListArrayList.get(position).getCategoryExtra().equalsIgnoreCase("Sample")) {
                            if(Integer.parseInt(callCommonCheckedListArrayList.get(position).getStock_balance())>0) {
                                CheckBoxContents(holder.checkBox, holder.tv_name, holder.getBindingAdapterPosition());
                            }else {
                                callCommonCheckedListArrayList.set(position, new CallCommonCheckedList(callCommonCheckedListArrayList.get(position).getName(), callCommonCheckedListArrayList.get(position).getCode(), callCommonCheckedListArrayList.get(position).getStock_balance(), false, callCommonCheckedListArrayList.get(position).getCategory(), callCommonCheckedListArrayList.get(position).getCategoryExtra()));
                                holder.checkBox.setChecked(false);
                                commonUtilsMethods.showToastMessage(context, context.getString(R.string.no_qty_prd));
                            }
                        }
                    }else {
                        CheckBoxContents(holder.checkBox, holder.tv_name, holder.getBindingAdapterPosition());
                    }
                }else {
                    if(DCRCallActivity.PrdMandatory != null && DCRCallActivity.PrdMandatory.equalsIgnoreCase("1")) {
                        for (int i = 0; i<callCommonCheckedListArrayList.size(); i++) {
                            if(callCommonCheckedListArrayList.get(i).getCode().equalsIgnoreCase("-10")) {
                                callCommonCheckedListArrayList.get(i).setCheckedItem(false);
                                notifyItemChanged(i);
                                break;
                            }
                        }
//                        noProductHolder.checkBox.setChecked(false);
                        commonUtilsMethods.showToastMessage(context, "Product selection is mandatory!");
                    }else {
//                    CheckBoxContents(holder.checkBox, holder.tv_name, holder.getBindingAdapterPosition());
                        if(holder.checkBox.isChecked() && checkAnyProductSelected()) {
                            holder.checkBox.setChecked(false);
                            CheckBoxContents(holder.checkBox, holder.tv_name, holder.getBindingAdapterPosition());
                            commonUtilsMethods.showToastMessage(context, "Please deselect the selected Products!");
                        }else {
                            if(noProductSelected) {
                                commonUtilsMethods.showToastMessage(context, "Cannot deselect No Product!");
                            }
                            holder.checkBox.setChecked(true);
                            CheckBoxContents(holder.checkBox, holder.tv_name, holder.getBindingAdapterPosition());
                        }
                    }
                }
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void CheckBoxContents(CheckBox checkBox, TextView tv_name, int adapterPosition) {
        if(checkBox.isChecked()) {
            tv_name.setTextColor(ContextCompat.getColor(context, R.color.cheked_txt_color));
            checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green_2)));
            isCheckedPrd = false;
            callCommonCheckedListArrayList.get(adapterPosition).setCheckedItem(true);
            if(!callCommonCheckedListArrayList.get(adapterPosition).getCode().equalsIgnoreCase("-10")) {
                checkAndSetNoProductCheckedOrUnchecked();
                SaveCallProductList saveCallProductList = new SaveCallProductList(callCommonCheckedListArrayList.get(adapterPosition).getName(), callCommonCheckedListArrayList.get(adapterPosition).getCode(), callCommonCheckedListArrayList.get(adapterPosition).getCategoryExtra(), callCommonCheckedListArrayList.get(adapterPosition).getStock_balance(), callCommonCheckedListArrayList.get(adapterPosition).getStock_balance(), "", "", "", "1", true);
                if(!saveCallProductListArrayList.contains(saveCallProductList)) {
                    saveCallProductListArrayList.add(saveCallProductList);
                }
                AssignRecyclerView(activity, context, saveCallProductListArrayList, callCommonCheckedListArrayList);
            }else {
                noProductSelected = true;
            }
        }else {
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
            isCheckedPrd = true;
            UnSelectedPrdCode = callCommonCheckedListArrayList.get(adapterPosition).getCode();
            callCommonCheckedListArrayList.get(adapterPosition).setCheckedItem(false);
            if(!callCommonCheckedListArrayList.get(adapterPosition).getCode().equalsIgnoreCase("-10")) {
                checkAndSetNoProductCheckedOrUnchecked();
                commonUtilsMethods.recycleTestWithDivider(ProductFragment.productsBinding.rvListPrd);
                AssignRecyclerView(activity, context, saveCallProductListArrayList, callCommonCheckedListArrayList);
                finalProductCallAdapter.notifyDataSetChanged();
            }else {
                noProductSelected = false;
            }
        }
    }

    private void AssignRecyclerView(Activity activity, Context context, ArrayList<SaveCallProductList> saveCallProductListArrayList, ArrayList<CallCommonCheckedList> callCommonCheckedListArrayList) {
        finalProductCallAdapter = new FinalProductCallAdapter(activity, context, saveCallProductListArrayList, callCommonCheckedListArrayList);
        commonUtilsMethods.recycleTestWithDivider(ProductFragment.productsBinding.rvListPrd);
        ProductFragment.productsBinding.rvListPrd.setAdapter(finalProductCallAdapter);
    }

    @Override
    public int getItemCount() {
        return callCommonCheckedListArrayList.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<CallCommonCheckedList> filteredNames) {
        this.callCommonCheckedListArrayList = filteredNames;
        for (int i = 0; i<filteredNames.size(); i++) {
            CallCommonCheckedList callCommonCheckedList = filteredNames.get(i);
            boolean isSaved = false;
            for (SaveCallProductList saveCallProductList : saveCallProductListArrayList) {
                if(saveCallProductList.getCode().equalsIgnoreCase(callCommonCheckedList.getCode())) {
                    isSaved = true;
                    break;
                }
            }
            callCommonCheckedList.setCheckedItem(isSaved);
            callCommonCheckedListArrayList.set(i, callCommonCheckedList);
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_category;
        CheckBox checkBox;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_data_name);
            tv_category = itemView.findViewById(R.id.tv_tag_category);
            checkBox = itemView.findViewById(R.id.chk_box);
        }
    }

    private boolean checkAnyProductSelected() {
        for (CallCommonCheckedList callCommonCheckedList : callCommonCheckedListArrayList) {
            if(callCommonCheckedList.isCheckedItem() && !callCommonCheckedList.getCode().equalsIgnoreCase("-10")) {
                return true;
            }
        }
        return false;
    }

    private void checkAndSetNoProductCheckedOrUnchecked() {
//        if(!(DCRCallActivity.PrdMandatory != null && DCRCallActivity.PrdMandatory.equals("1"))) {
        if(!checkAnyProductSelected() && !DCRCallActivity.PrdMandatory.equalsIgnoreCase("1")) {
            if(callCommonCheckedListArrayList.get(0).getCode().equalsIgnoreCase("-10")) {
                callCommonCheckedListArrayList.get(0).setCheckedItem(true);
                for (int i = 0; i<callCommonCheckedListArrayList.size(); i++) {
                    if(callCommonCheckedListArrayList.get(i).getCode().equalsIgnoreCase("-10")) {
                        callCommonCheckedListArrayList.get(i).setCheckedItem(true);
                        noProductSelected = true;
                        notifyItemChanged(i);
                        break;
                    }
                }
//                    noProductHolder.checkBox.setChecked(true);
//                    noProductHolder.tv_name.setTextColor(ContextCompat.getColor(context, R.color.cheked_txt_color));
//                    noProductHolder.checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green_2)));
            }
        }else {
            if(callCommonCheckedListArrayList.get(0).getCode().equalsIgnoreCase("-10")) {
                callCommonCheckedListArrayList.get(0).setCheckedItem(false);
//                    if(noProductHolder != null) {
                for (int i = 0; i<callCommonCheckedListArrayList.size(); i++) {
                    if(callCommonCheckedListArrayList.get(i).getCode().equalsIgnoreCase("-10")) {
                        callCommonCheckedListArrayList.get(i).setCheckedItem(false);
                        noProductSelected = false;
                        notifyItemChanged(i);
                        break;
                    }
                }
//                        noProductHolder.checkBox.setChecked(false);
//                        noProductHolder.tv_name.setTextColor(ContextCompat.getColor(context, R.color.bg_txt_color));
//                        noProductHolder.checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.bg_txt_color)));
//                    }
            }
        }
//        }
    }
}
