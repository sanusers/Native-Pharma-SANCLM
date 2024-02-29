package saneforce.santrip.activity.call.fragments.additionalCall;

import static saneforce.santrip.activity.call.DCRCallActivity.StockInput;
import static saneforce.santrip.activity.call.DCRCallActivity.dcrCallBinding;
import static saneforce.santrip.activity.call.adapter.additionalCalls.finalSavedAdapter.FinalAdditionalCallAdapter.Selected_code;
import static saneforce.santrip.activity.call.adapter.additionalCalls.finalSavedAdapter.FinalAdditionalCallAdapter.Selected_name;
import static saneforce.santrip.activity.call.adapter.additionalCalls.sideView.AdapterInputAdditionalCall.addedInpList;
import static saneforce.santrip.activity.call.fragments.additionalCall.AdditionalCallDetailedSide.callDetailsSideBinding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

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
import saneforce.santrip.activity.call.pojo.additionalCalls.AddInputAdditionalCall;
import saneforce.santrip.activity.call.DCRCallActivity;
import saneforce.santrip.activity.call.pojo.CallCommonCheckedList;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.databinding.FragmentAcSelectInputSideBinding;

public class AddCallSelectInpSide extends Fragment {
    public static ArrayList<CallCommonCheckedList> callInputList;

    @SuppressLint("StaticFieldLeak")
    public static FragmentAcSelectInputSideBinding selectInputSideBinding;
    SelectACInputAdapter selectACInputAdapter;
    CommonUtilsMethods commonUtilsMethods;

    @SuppressLint("NotifyDataSetChanged")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        selectInputSideBinding = FragmentAcSelectInputSideBinding.inflate(inflater);
        View v = selectInputSideBinding.getRoot();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        selectInputSideBinding.tvDummy.setOnClickListener(view -> {
        });

        selectInputSideBinding.btnOk.setOnClickListener(view -> {
            for (int i = 0; i < SelectACInputAdapter.callInputListAdapter.size(); i++) {
                if (SelectACInputAdapter.callInputListAdapter.get(i).isCheckedItem()) {
                    addedInpList.add(new AddInputAdditionalCall(Selected_name, Selected_code, SelectACInputAdapter.callInputListAdapter.get(i).getName(), SelectACInputAdapter.callInputListAdapter.get(i).getCode(), SelectACInputAdapter.callInputListAdapter.get(i).getStock_balance(), SelectACInputAdapter.callInputListAdapter.get(i).getStock_balance(), ""));
                }
            }
            commonUtilsMethods.recycleTestWithoutDivider(callDetailsSideBinding.rvAddInputsAdditional);
            callDetailsSideBinding.rvAddInputsAdditional.setAdapter(AdditionalCallDetailedSide.adapterInputAdditionalCall);
            AdditionalCallDetailedSide.adapterInputAdditionalCall.notifyDataSetChanged();
            dcrCallBinding.fragmentAcSelectInputSide.setVisibility(View.GONE);
        });

        selectACInputAdapter = new SelectACInputAdapter(requireContext(), callInputList);
        RecyclerView.LayoutManager mLayoutManagerChe = new LinearLayoutManager(getActivity());
        selectInputSideBinding.selectListView.setLayoutManager(mLayoutManagerChe);
        selectInputSideBinding.selectListView.setItemAnimator(new DefaultItemAnimator());
        selectInputSideBinding.selectListView.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));
        selectInputSideBinding.selectListView.setAdapter(selectACInputAdapter);

        selectInputSideBinding.imgClose.setOnClickListener(view -> dcrCallBinding.fragmentAcSelectInputSide.setVisibility(View.GONE));

        selectInputSideBinding.searchList.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filterInp(editable.toString());
            }
        });

        return v;
    }

    private void filterInp(String text) {
        ArrayList<CallCommonCheckedList> filteredNames = new ArrayList<>();
        for (CallCommonCheckedList s : callInputList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredNames.add(s);
            }
        }
        selectACInputAdapter.filterList(filteredNames);
    }

    public static class SelectACInputAdapter extends RecyclerView.Adapter<SelectACInputAdapter.ViewHolder> {

        public static ArrayList<CallCommonCheckedList> callInputListAdapter;
        Context context;
        CommonUtilsMethods commonUtilsMethods;

        public SelectACInputAdapter(Context context, ArrayList<CallCommonCheckedList> callInputListAdapter) {
            this.context = context;
            SelectACInputAdapter.callInputListAdapter = callInputListAdapter;
            commonUtilsMethods = new CommonUtilsMethods(context);
        }

        @NonNull
        @Override
        public SelectACInputAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.adapter_checked_data, parent, false);
            return new ViewHolder(view);
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onBindViewHolder(@NonNull SelectACInputAdapter.ViewHolder holder, int position) {

            holder.tv_name.setText(callInputListAdapter.get(position).getName());

            if (callInputListAdapter.get(position).isCheckedItem()) {
                holder.checkBox.setChecked(true);
                holder.tv_name.setTextColor(ContextCompat.getColor(context, R.color.cheked_txt_color));
                holder.checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green_2)));
            } else {
                holder.checkBox.setChecked(false);
                holder.tv_name.setTextColor(ContextCompat.getColor(context, R.color.bg_txt_color));
                holder.checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.bg_txt_color)));
            }

            holder.checkBox.setOnCheckedChangeListener((compoundButton, b) -> {
                if (holder.checkBox.isPressed()) {
                    if (DCRCallActivity.InputValidation.equalsIgnoreCase("1")) {
                        for (int i = 0; i < StockInput.size(); i++) {
                            if (StockInput.get(i).getStockCode().equalsIgnoreCase(callInputListAdapter.get(position).getCode())) {
                                callInputListAdapter.set(position, new CallCommonCheckedList(callInputListAdapter.get(position).getName(), callInputListAdapter.get(position).getCode(), StockInput.get(i).getCurrentStock(), false));
                            }
                        }
                        if (Integer.parseInt(callInputListAdapter.get(position).getStock_balance()) > 0) {
                            SelectContent(callInputListAdapter.get(position).getCode(), holder.checkBox, holder.tv_name, holder.getBindingAdapterPosition());
                        } else {
                            holder.checkBox.setChecked(false);
                            commonUtilsMethods.showToastMessage(context, context.getString(R.string.no_qty_input));
                        }
                    } else {
                        SelectContent(callInputListAdapter.get(position).getCode(), holder.checkBox, holder.tv_name, holder.getBindingAdapterPosition());
                    }
                }
            });
        }

        @SuppressLint("NotifyDataSetChanged")
        private void SelectContent(String code, CheckBox checkBox, TextView tv_name, int adapterPos) {
            if (checkBox.isChecked()) {
                if (addedInpList.size() > 0) {
                    for (int i = 0; i < addedInpList.size(); i++) {
                        if (!addedInpList.get(i).getInput_code().equalsIgnoreCase(code)) {
                            tv_name.setTextColor(ContextCompat.getColor(context, R.color.cheked_txt_color));
                            checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green_2)));
                            callInputListAdapter.get(adapterPos).setCheckedItem(true);
                        } else {
                            tv_name.setTextColor(ContextCompat.getColor(context, R.color.bg_txt_color));
                            checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.bg_txt_color)));
                            callInputListAdapter.get(adapterPos).setCheckedItem(false);
                            checkBox.setChecked(false);
                            commonUtilsMethods.showToastMessage(context, context.getString(R.string.already_available));
                            break;
                        }
                    }
                } else {
                    tv_name.setTextColor(ContextCompat.getColor(context, R.color.cheked_txt_color));
                    checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.green_2)));
                    callInputListAdapter.get(adapterPos).setCheckedItem(true);
                }
            } else {
                tv_name.setTextColor(ContextCompat.getColor(context, R.color.bg_txt_color));
                checkBox.setButtonTintList(ColorStateList.valueOf(ContextCompat.getColor(context, R.color.bg_txt_color)));
                callInputListAdapter.get(adapterPos).setCheckedItem(false);
            }
        }

        @Override
        public int getItemCount() {
            return callInputListAdapter.size();
        }

        @SuppressLint("NotifyDataSetChanged")
        public void filterList(ArrayList<CallCommonCheckedList> filteredNames) {
            callInputListAdapter = filteredNames;
            notifyDataSetChanged();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tv_name;
            CheckBox checkBox;

            public ViewHolder(View view) {
                super(view);
                tv_name = itemView.findViewById(R.id.tv_data_name);
                checkBox = itemView.findViewById(R.id.chk_box);
            }
        }
    }
}
