package saneforce.sanclm.activity.homeScreen.call.fragments;

import static saneforce.sanclm.activity.homeScreen.call.DCRCallActivity.StockInput;
import static saneforce.sanclm.activity.homeScreen.call.DCRCallActivity.dcrCallBinding;
import static saneforce.sanclm.activity.homeScreen.call.adapter.additionalCalls.sideView.AdapterInputAdditionalCall.addedInpList;
import static saneforce.sanclm.activity.homeScreen.call.fragments.AdditionalCallDetailedSide.callDetailsSideBinding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.DCRCallActivity;
import saneforce.sanclm.activity.homeScreen.call.pojo.CallCommonCheckedList;
import saneforce.sanclm.activity.homeScreen.call.pojo.additionalCalls.AddInputAdditionalCall;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.databinding.FragmentAcSelectInputSideBinding;

public class AddCallSelectInpSide extends Fragment {
    public static ArrayList<CallCommonCheckedList> callInputList;
    @SuppressLint("StaticFieldLeak")
    public static FragmentAcSelectInputSideBinding selectInputSideBinding;
    SelectACInputAdapter selectACInputAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        selectInputSideBinding = FragmentAcSelectInputSideBinding.inflate(inflater);
        View v = selectInputSideBinding.getRoot();

        selectInputSideBinding.tvDummy.setOnClickListener(view -> {
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
        public static int pos;
        Context context;
        ArrayList<CallCommonCheckedList> callInputList;
        CommonUtilsMethods commonUtilsMethods;
        boolean isAvailable;

        public SelectACInputAdapter(Context context, ArrayList<CallCommonCheckedList> callInputList) {
            this.context = context;
            this.callInputList = callInputList;
        }

        @NonNull
        @Override
        public SelectACInputAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.single_item, parent, false);
            return new ViewHolder(view);
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onBindViewHolder(@NonNull SelectACInputAdapter.ViewHolder holder, int position) {
            commonUtilsMethods = new CommonUtilsMethods(context);
            holder.tv_name.setText(callInputList.get(position).getName());
            holder.tv_name.setOnClickListener(view -> {
                if (DCRCallActivity.InputValidation.equalsIgnoreCase("1")) {
                    for (int i = 0; i < StockInput.size(); i++) {
                        if (StockInput.get(i).getStockCode().equalsIgnoreCase(callInputList.get(position).getCode())) {
                            callInputList.set(position, new CallCommonCheckedList(callInputList.get(position).getName(), callInputList.get(position).getCode(), StockInput.get(i).getCurrentStock(), false));
                        }
                    }
                    if (Integer.parseInt(callInputList.get(position).getStock_balance()) > 0) {
                        SelectContent(holder.getBindingAdapterPosition());
                    } else {
                        Toast.makeText(context, "No Qty Available in this Product", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    SelectContent(holder.getBindingAdapterPosition());
                }
            });
        }

        @SuppressLint("NotifyDataSetChanged")
        private void SelectContent(int adapterPosition) {
            for (int i = 0; i < addedInpList.size(); i++) {
                if (!callInputList.get(adapterPosition).getCode().equalsIgnoreCase(addedInpList.get(i).getInput_code())) {
                    isAvailable = false;
                } else {
                    isAvailable = true;
                    break;
                }
            }

            if (!isAvailable) {
                addedInpList.set(pos, new AddInputAdditionalCall(addedInpList.get(pos).getCust_name(), addedInpList.get(pos).getCust_code(), callInputList.get(adapterPosition).getName(), callInputList.get(adapterPosition).getCode(), callInputList.get(adapterPosition).getStock_balance(), callInputList.get(adapterPosition).getStock_balance(), addedInpList.get(pos).getInp_qty()));
                commonUtilsMethods.recycleTestWithoutDivider(callDetailsSideBinding.rvAddInputsAdditional);
                callDetailsSideBinding.rvAddInputsAdditional.setAdapter(AdditionalCallDetailedSide.adapterInputAdditionalCall);
                AdditionalCallDetailedSide.adapterInputAdditionalCall.notifyDataSetChanged();
                dcrCallBinding.fragmentAcSelectInputSide.setVisibility(View.GONE);
            } else {
                Toast.makeText(context, "You Already Select this Input", Toast.LENGTH_SHORT).show();
            }

        }

        @Override
        public int getItemCount() {
            return callInputList.size();
        }

        @SuppressLint("NotifyDataSetChanged")
        public void filterList(ArrayList<CallCommonCheckedList> filteredNames) {
            this.callInputList = filteredNames;
            notifyDataSetChanged();
        }

        public static class ViewHolder extends RecyclerView.ViewHolder {
            TextView tv_name;

            public ViewHolder(View view) {
                super(view);
                tv_name = itemView.findViewById(R.id.tv_name);
            }
        }
    }
}
