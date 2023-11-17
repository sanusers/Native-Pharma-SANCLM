package saneforce.sanclm.activity.homeScreen.call.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.activity.homeScreen.call.DCRCallActivity;
import saneforce.sanclm.activity.homeScreen.call.adapter.product.CallProductListAdapter;
import saneforce.sanclm.activity.homeScreen.call.adapter.product.SaveProductCallAdapter;
import saneforce.sanclm.activity.homeScreen.call.pojo.CallCommonCheckedList;
import saneforce.sanclm.databinding.FragmentProductsBinding;
import saneforce.sanclm.storage.SQLite;

public class ProductFragment extends Fragment {

    @SuppressLint("StaticFieldLeak")
    public static FragmentProductsBinding productsBinding;
    public static ArrayList<CallCommonCheckedList> callCommonCheckedListArrayList;
    CallProductListAdapter callProductListAdapter;
    SaveProductCallAdapter saveProductCallAdapter;
    SQLite sqLite;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        productsBinding = FragmentProductsBinding.inflate(inflater);
        View v = productsBinding.getRoot();
        sqLite = new SQLite(getContext());

        if (DCRCallActivity.SampleValidation.equalsIgnoreCase("1")) {
            productsBinding.tagStock.setVisibility(View.VISIBLE);
        } else {
            productsBinding.tagStock.setVisibility(View.GONE);
        }

        HiddenVisibleFunction();

       /* if (DCRCallActivity.CallActivityCustDetails.get(0).getType().equalsIgnoreCase("1") && DCRCallActivity.PrdSamNeed.equalsIgnoreCase("1")) {
            productsBinding.tagSamples.setVisibility(View.VISIBLE);
        } else if (DCRCallActivity.CallActivityCustDetails.get(0).getType().equalsIgnoreCase("2") && DCRCallActivity.PrdSamNeed.equalsIgnoreCase("0")) {
            productsBinding.tagSamples.setVisibility(View.VISIBLE);
        } else if (DCRCallActivity.CallActivityCustDetails.get(0).getType().equalsIgnoreCase("3")) {
            productsBinding.tagSamples.setVisibility(View.VISIBLE);
        } else if (DCRCallActivity.CallActivityCustDetails.get(0).getType().equalsIgnoreCase("3")) {
            productsBinding.tagSamples.setVisibility(View.VISIBLE);
        } else {
            productsBinding.tagSamples.setVisibility(View.GONE);
        }

        if (DCRCallActivity.CallActivityCustDetails.get(0).getType().equalsIgnoreCase("1") && DCRCallActivity.PrdRxNeed.equalsIgnoreCase("1")) {
            productsBinding.tagRxQty.setVisibility(View.VISIBLE);
        } else if (DCRCallActivity.CallActivityCustDetails.get(0).getType().equalsIgnoreCase("2") && DCRCallActivity.PrdRxNeed.equalsIgnoreCase("0")) {
            productsBinding.tagRxQty.setVisibility(View.VISIBLE);
        } else if (DCRCallActivity.CallActivityCustDetails.get(0).getType().equalsIgnoreCase("3") && DCRCallActivity.PrdRxNeed.equalsIgnoreCase("0")) {
            productsBinding.tagRxQty.setVisibility(View.VISIBLE);
        } else if (DCRCallActivity.CallActivityCustDetails.get(0).getType().equalsIgnoreCase("4") && DCRCallActivity.PrdRxNeed.equalsIgnoreCase("0")) {
            productsBinding.tagRxQty.setVisibility(View.VISIBLE);
        } else {
            productsBinding.tagRxQty.setVisibility(View.GONE);
        }*/


        AddProductList();

        productsBinding.searchPrd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });
        return v;
    }

    private void HiddenVisibleFunction() {
        productsBinding.tagSamples.setText(DCRCallActivity.CapSamQty);
        productsBinding.tagRxQty.setText(DCRCallActivity.CapRxqty);
        switch (DCRCallActivity.CallActivityCustDetails.get(0).getType()) {
            case "1":
                if (DCRCallActivity.PrdSamNeed.equalsIgnoreCase("1"))
                    productsBinding.tagSamples.setVisibility(View.VISIBLE);
                if (DCRCallActivity.PrdRxNeed.equalsIgnoreCase("1"))
                    productsBinding.tagRxQty.setVisibility(View.VISIBLE);
                break;
            case "2":
            case "3":
            case "4":
                if (DCRCallActivity.PrdSamNeed.equalsIgnoreCase("0"))
                    productsBinding.tagSamples.setVisibility(View.VISIBLE);
                if (DCRCallActivity.PrdRxNeed.equalsIgnoreCase("0"))
                    productsBinding.tagRxQty.setVisibility(View.VISIBLE);
                break;
            default:
                productsBinding.tagSamples.setVisibility(View.GONE);
                productsBinding.tagRxQty.setVisibility(View.GONE);
        }
    }

    private void AddProductList() {
        callProductListAdapter = new CallProductListAdapter(getActivity(), getContext(), callCommonCheckedListArrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        productsBinding.rvCheckDataList.setLayoutManager(mLayoutManager);
        productsBinding.rvCheckDataList.setItemAnimator(new DefaultItemAnimator());
        productsBinding.rvCheckDataList.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        productsBinding.rvCheckDataList.setAdapter(callProductListAdapter);

        saveProductCallAdapter = new SaveProductCallAdapter(getActivity(), getContext(), CallProductListAdapter.saveCallProductListArrayList, callCommonCheckedListArrayList);
        RecyclerView.LayoutManager mLayoutManagerprd = new LinearLayoutManager(getActivity());
        productsBinding.rvListPrd.setLayoutManager(mLayoutManagerprd);
        productsBinding.rvListPrd.setItemAnimator(new DefaultItemAnimator());
        productsBinding.rvListPrd.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        productsBinding.rvListPrd.setAdapter(saveProductCallAdapter);
    }

    private void filter(String text) {
        ArrayList<CallCommonCheckedList> filterdNames = new ArrayList<>();

      /*  if (text.toLowerCase().equalsIgnoreCase("SL")) {
            if (s.getCategory().equalsIgnoreCase("Sale")) {
                sale = s.getCategory();
            }*/
        for (CallCommonCheckedList s : callCommonCheckedListArrayList) {
            String Category = "";

            if (s.getCategory().equalsIgnoreCase("Sale")) {
                Category = "SL";
            }

            if (s.getCategory().equalsIgnoreCase("Sample")) {
                Category = "SM";
            }

            if (s.getCategory().equalsIgnoreCase("Sale/Sample")) {
                Category = "SL/SM";
            }

            if (s.getName().toLowerCase().contains(text.toLowerCase()) || s.getCategory().toLowerCase().contains(text.toLowerCase()) || Category.toLowerCase().contains(text.toLowerCase())) {
                filterdNames.add(s);
            }
        }
        callProductListAdapter.filterList(filterdNames);
    }
}
