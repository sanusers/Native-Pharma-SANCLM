package saneforce.sanclm.activity.homeScreen.call.fragments;

import static saneforce.sanclm.activity.homeScreen.call.DCRCallActivity.StockSample;
import static saneforce.sanclm.activity.homeScreen.call.DCRCallActivity.dcrCallBinding;
import static saneforce.sanclm.activity.homeScreen.call.adapter.additionalCalls.sideView.AdapterSampleAdditionalCall.addedProductList;
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
import saneforce.sanclm.activity.homeScreen.call.pojo.additionalCalls.AddSampleAdditionalCall;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.databinding.FragmentAcSelectProductSideBinding;

public class AddCallSelectPrdSide extends Fragment {
    public static ArrayList<CallCommonCheckedList> callSampleList;
    public static FragmentAcSelectProductSideBinding selectProductSideBinding;
    SelectACProductAdapter selectACProductAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        selectProductSideBinding = FragmentAcSelectProductSideBinding.inflate(inflater);
        View v = selectProductSideBinding.getRoot();
        selectProductSideBinding.tvDummy.setOnClickListener(view -> {
        });

        selectACProductAdapter = new SelectACProductAdapter(requireContext(), callSampleList);
        RecyclerView.LayoutManager mLayoutManagerChe = new LinearLayoutManager(getActivity());
        selectProductSideBinding.selectListView.setLayoutManager(mLayoutManagerChe);
        selectProductSideBinding.selectListView.setItemAnimator(new DefaultItemAnimator());
        selectProductSideBinding.selectListView.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));
        selectProductSideBinding.selectListView.setAdapter(selectACProductAdapter);

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
        public static int pos;
        Context context;
        ArrayList<CallCommonCheckedList> callSampleList;
        CommonUtilsMethods commonUtilsMethods;
        boolean isAvailable;

        public SelectACProductAdapter(Context context, ArrayList<CallCommonCheckedList> callSampleList) {
            this.context = context;
            this.callSampleList = callSampleList;
        }

        @NonNull
        @Override
        public SelectACProductAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.single_item, parent, false);
            return new ViewHolder(view);
        }

        @SuppressLint({"NotifyDataSetChanged", "UseCompatLoadingForDrawables"})
        @Override
        public void onBindViewHolder(@NonNull SelectACProductAdapter.ViewHolder holder, int position) {
            commonUtilsMethods = new CommonUtilsMethods(context);
            holder.tv_name.setText(callSampleList.get(position).getName());
            holder.tv_category.setText(callSampleList.get(position).getCategory());

            if (callSampleList.get(position).getCategory().equalsIgnoreCase("Sample")) {
                holder.tv_category.setText("SM");
            } else if (callSampleList.get(position).getCategory().equalsIgnoreCase("Sale/Sample")) {
                holder.tv_category.setText("SL/SM");
            }

            if (holder.tv_category.getText().toString().contains("P")) {
                holder.tv_category.setTextColor(context.getResources().getColor(R.color.txt_priority));
                holder.tv_category.setBackground(context.getResources().getDrawable(R.drawable.bg_priority));
            } else if (holder.tv_category.getText().toString().equalsIgnoreCase("SM")) {
                holder.tv_category.setTextColor(context.getResources().getColor(R.color.txt_sample));
                holder.tv_category.setBackground(context.getResources().getDrawable(R.drawable.bg_sample));
            } else if (holder.tv_category.getText().toString().equalsIgnoreCase("SL/SM")) {
                holder.tv_category.setTextColor(context.getResources().getColor(R.color.txt_sale_sample));
                holder.tv_category.setBackground(context.getResources().getDrawable(R.drawable.bg_sale_sample));
            }

            holder.tv_name.setOnClickListener(view -> {
                if (DCRCallActivity.SampleValidation.equalsIgnoreCase("1")) {
                    for (int i = 0; i < StockSample.size(); i++) {
                        if (StockSample.get(i).getStockCode().equalsIgnoreCase(callSampleList.get(position).getCode())) {
                            callSampleList.set(position, new CallCommonCheckedList(callSampleList.get(position).getName(), callSampleList.get(position).getCode(), StockSample.get(i).getCurrentStock(), false, callSampleList.get(position).getCategory(), callSampleList.get(position).getCategoryExtra()));
                        }
                    }
                    if (callSampleList.get(position).getCategoryExtra().equalsIgnoreCase("Sale/Sample")) {
                        SelectContent(holder.getBindingAdapterPosition());
                    } else if (callSampleList.get(position).getCategoryExtra().equalsIgnoreCase("Sample")) {
                        if (Integer.parseInt(callSampleList.get(position).getStock_balance()) > 0) {
                            SelectContent(holder.getBindingAdapterPosition());
                        } else {
                            Toast.makeText(context, "No Qty Available in this Product", Toast.LENGTH_SHORT).show();
                        }
                    }
                } else {
                    SelectContent(holder.getBindingAdapterPosition());
                }
            });

        }

        @SuppressLint("NotifyDataSetChanged")
        private void SelectContent(int adapterPos) {
            for (int i = 0; i < addedProductList.size(); i++) {
                if (!callSampleList.get(adapterPos).getCode().equalsIgnoreCase(addedProductList.get(i).getPrd_code())) {
                    isAvailable = false;
                } else {
                    isAvailable = true;
                    break;
                }
            }

            if (!isAvailable) {
                addedProductList.set(pos, new AddSampleAdditionalCall(addedProductList.get(pos).getCust_name(), addedProductList.get(pos).getCust_code(), callSampleList.get(adapterPos).getName(), callSampleList.get(adapterPos).getCode(), callSampleList.get(adapterPos).getStock_balance(), callSampleList.get(adapterPos).getStock_balance(), addedProductList.get(pos).getSample_qty(), callSampleList.get(adapterPos).getCategory()));
                commonUtilsMethods.recycleTestWithoutDivider(callDetailsSideBinding.rvAddSampleAdditional);
                callDetailsSideBinding.rvAddSampleAdditional.setAdapter(AdditionalCallDetailedSide.adapterSampleAdditionalCall);
                AdditionalCallDetailedSide.adapterSampleAdditionalCall.notifyDataSetChanged();
                dcrCallBinding.fragmentAcSelectProductSide.setVisibility(View.GONE);
            } else {
                Toast.makeText(context, "You Already Select this Product", Toast.LENGTH_SHORT).show();
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

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_name = itemView.findViewById(R.id.tv_name);
                tv_category = itemView.findViewById(R.id.tv_category);
            }
        }
    }
}
