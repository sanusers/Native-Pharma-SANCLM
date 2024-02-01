package saneforce.santrip.activity.homeScreen.call.fragments;

import static saneforce.santrip.activity.homeScreen.call.DCRCallActivity.StockSample;
import static saneforce.santrip.activity.homeScreen.call.DCRCallActivity.dcrCallBinding;
import static saneforce.santrip.activity.homeScreen.call.adapter.additionalCalls.finalSavedAdapter.FinalAdditionalCallAdapter.Selected_code;
import static saneforce.santrip.activity.homeScreen.call.adapter.additionalCalls.finalSavedAdapter.FinalAdditionalCallAdapter.Selected_name;
import static saneforce.santrip.activity.homeScreen.call.adapter.additionalCalls.sideView.AdapterSampleAdditionalCall.addedProductList;
import static saneforce.santrip.activity.homeScreen.call.fragments.AdditionalCallDetailedSide.callDetailsSideBinding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.call.DCRCallActivity;
import saneforce.santrip.activity.homeScreen.call.pojo.CallCommonCheckedList;
import saneforce.santrip.activity.homeScreen.call.pojo.additionalCalls.AddSampleAdditionalCall;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.databinding.FragmentAcSelectProductSideBinding;

public class AddCallSelectPrdSide extends Fragment {
    public static ArrayList<CallCommonCheckedList> callSampleList;
    @SuppressLint("StaticFieldLeak")
    public static FragmentAcSelectProductSideBinding selectProductSideBinding;
    SelectACProductAdapter selectACProductAdapter;
    CommonUtilsMethods commonUtilsMethods;

    @SuppressLint("NotifyDataSetChanged")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        selectProductSideBinding = FragmentAcSelectProductSideBinding.inflate(inflater);
        View v = selectProductSideBinding.getRoot();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        selectProductSideBinding.tvDummy.setOnClickListener(view -> {
        });

        selectACProductAdapter = new SelectACProductAdapter(requireContext(), callSampleList);
        RecyclerView.LayoutManager mLayoutManagerChe = new LinearLayoutManager(getActivity());
        selectProductSideBinding.selectListView.setLayoutManager(mLayoutManagerChe);
        selectProductSideBinding.selectListView.setItemAnimator(new DefaultItemAnimator());
        selectProductSideBinding.selectListView.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));
        selectProductSideBinding.selectListView.setAdapter(selectACProductAdapter);

        selectProductSideBinding.btnOk.setOnClickListener(v1 -> {
            for (int i = 0; i < SelectACProductAdapter.callSampleList.size(); i++) {
                if (SelectACProductAdapter.callSampleList.get(i).isCheckedItem()) {
                    addedProductList.add(new AddSampleAdditionalCall(Selected_name, Selected_code, callSampleList.get(i).getName(), callSampleList.get(i).getCode(), callSampleList.get(i).getStock_balance(), callSampleList.get(i).getStock_balance(), "", callSampleList.get(i).getCategory()));
                }
            }
            commonUtilsMethods.recycleTestWithoutDivider(callDetailsSideBinding.rvAddSampleAdditional);
            callDetailsSideBinding.rvAddSampleAdditional.setAdapter(AdditionalCallDetailedSide.adapterSampleAdditionalCall);
            AdditionalCallDetailedSide.adapterSampleAdditionalCall.notifyDataSetChanged();
            dcrCallBinding.fragmentAcSelectProductSide.setVisibility(View.GONE);
        });

        selectProductSideBinding.imgClose.setOnClickListener(view -> dcrCallBinding.fragmentAcSelectProductSide.setVisibility(View.GONE));

        selectProductSideBinding.searchList.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filterPrd(editable.toString());
            }
        });

        return v;
    }

    private void filterPrd(String text) {
        ArrayList<CallCommonCheckedList> filteredNames = new ArrayList<>();
        for (CallCommonCheckedList s : callSampleList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredNames.add(s);
            }
        }
        selectACProductAdapter.filterList(filteredNames);
    }

    public static class SelectACProductAdapter extends RecyclerView.Adapter<SelectACProductAdapter.ViewHolder> {

        Context context;
        public static ArrayList<CallCommonCheckedList> callSampleList;
        CommonUtilsMethods commonUtilsMethods;

        public SelectACProductAdapter(Context context, ArrayList<CallCommonCheckedList> callSampleList) {
            this.context = context;
            SelectACProductAdapter.callSampleList = callSampleList;
        }

        @NonNull
        @Override
        public SelectACProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_checked_data, parent, false);
            return new ViewHolder(view);
        }

        @SuppressLint({"NotifyDataSetChanged", "UseCompatLoadingForDrawables", "SetTextI18n"})
        @Override
        public void onBindViewHolder(@NonNull SelectACProductAdapter.ViewHolder holder, int position) {
            commonUtilsMethods = new CommonUtilsMethods(context);
            holder.tv_name.setText(callSampleList.get(position).getName());
            holder.tv_category.setVisibility(View.VISIBLE);
            holder.tv_category.setText(callSampleList.get(position).getCategory());

            if (callSampleList.get(position).isCheckedItem()) {
                holder.checkBox.setChecked(true);
                holder.tv_name.setTextColor(ContextCompat.getColor(context, R.color.cheked_txt_color));
                holder.checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green_2)));
            } else {
                holder.checkBox.setChecked(false);
                holder.tv_name.setTextColor(ContextCompat.getColor(context, R.color.bg_txt_color));
                holder.checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.bg_txt_color)));
            }


            if (callSampleList.get(position).getCategory().equalsIgnoreCase("Sample")) {
                holder.tv_category.setText("SM");
            } else if (callSampleList.get(position).getCategory().equalsIgnoreCase("Sale/Sample")) {
                holder.tv_category.setText("SL/SM");
            }

            if (holder.tv_category.getText().toString().contains("P")) {
                holder.tv_category.setTextColor(ContextCompat.getColor(context, R.color.txt_priority));
                holder.tv_category.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_priority));
            } else if (holder.tv_category.getText().toString().equalsIgnoreCase("SM")) {
                holder.tv_category.setTextColor(ContextCompat.getColor(context, R.color.txt_sample));
                holder.tv_category.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_sample));
            } else if (holder.tv_category.getText().toString().equalsIgnoreCase("SL/SM")) {
                holder.tv_category.setTextColor(ContextCompat.getColor(context, R.color.txt_sale_sample));
                holder.tv_category.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_sale_sample));
            }


            holder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
                if (holder.checkBox.isPressed()) {
                    if (DCRCallActivity.SampleValidation.equalsIgnoreCase("1")) {
                        for (int i = 0; i < StockSample.size(); i++) {
                            if (StockSample.get(i).getStockCode().equalsIgnoreCase(callSampleList.get(position).getCode())) {
                                callSampleList.set(position, new CallCommonCheckedList(callSampleList.get(position).getName(), callSampleList.get(position).getCode(), StockSample.get(i).getCurrentStock(), false, callSampleList.get(position).getCategory(), callSampleList.get(position).getCategoryExtra()));
                            }
                        }
                        if (callSampleList.get(position).getCategoryExtra().equalsIgnoreCase("Sale/Sample")) {
                            SelectContent(callSampleList.get(position).getCode(), holder.tv_name,holder.checkBox, holder.getBindingAdapterPosition());
                        } else if (callSampleList.get(position).getCategoryExtra().equalsIgnoreCase("Sample")) {
                            if (Integer.parseInt(callSampleList.get(position).getStock_balance()) > 0) {
                                SelectContent(callSampleList.get(position).getCode(), holder.tv_name, holder.checkBox, holder.getBindingAdapterPosition());
                            } else {
                                holder.checkBox.setChecked(false);
                                commonUtilsMethods.ShowToast(context,context.getString(R.string.no_qty_prd),100);
                            }
                        }
                    } else {
                        SelectContent(callSampleList.get(position).getCode(), holder.tv_name, holder.checkBox, holder.getBindingAdapterPosition());
                    }
                }
            });
        }

        @SuppressLint("NotifyDataSetChanged")
        private void SelectContent(String code, TextView tv_name, CheckBox checkBox, int adapterPos) {
            if (checkBox.isChecked()) {
                if (addedProductList.size() > 0) {
                    for (int i = 0; i < addedProductList.size(); i++) {
                        if (!addedProductList.get(i).getPrd_code().equalsIgnoreCase(code)) {
                            tv_name.setTextColor(ContextCompat.getColor(context, R.color.cheked_txt_color));
                            checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green_2)));
                            callSampleList.get(adapterPos).setCheckedItem(true);
                        } else {
                            tv_name.setTextColor(ContextCompat.getColor(context, R.color.bg_txt_color));
                            checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.bg_txt_color)));
                            callSampleList.get(adapterPos).setCheckedItem(false);
                            checkBox.setChecked(false);
                            commonUtilsMethods.ShowToast(context,context.getString(R.string.already_available),100);
                            break;
                        }
                    }
                } else {
                    tv_name.setTextColor(ContextCompat.getColor(context, R.color.cheked_txt_color));
                    checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green_2)));
                    callSampleList.get(adapterPos).setCheckedItem(true);
                }
            } else {
                tv_name.setTextColor(ContextCompat.getColor(context, R.color.bg_txt_color));
                checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.bg_txt_color)));
                callSampleList.get(adapterPos).setCheckedItem(false);
            }
        }

        @Override
        public int getItemCount() {
            return callSampleList.size();
        }

        @SuppressLint("NotifyDataSetChanged")
        public void filterList(ArrayList<CallCommonCheckedList> filteredNames) {
            this.callSampleList = filteredNames;
            notifyDataSetChanged();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tv_name, tv_category;
            CheckBox checkBox;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_name = itemView.findViewById(R.id.tv_data_name);
                checkBox = itemView.findViewById(R.id.chk_box);
                tv_category = itemView.findViewById(R.id.tv_tag_category);
            }
        }
    }
}
