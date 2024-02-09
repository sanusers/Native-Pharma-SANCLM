package saneforce.santrip.activity.homeScreen.call.fragments;

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

import saneforce.santrip.activity.homeScreen.call.DCRCallActivity;
import saneforce.santrip.activity.homeScreen.call.adapter.product.CheckProductListAdapter;
import saneforce.santrip.activity.homeScreen.call.adapter.product.FinalProductCallAdapter;
import saneforce.santrip.activity.homeScreen.call.pojo.CallCommonCheckedList;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.WrapContentLinearLayoutManager;
import saneforce.santrip.databinding.FragmentProductsBinding;
import saneforce.santrip.storage.SQLite;

public class ProductFragment extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static FragmentProductsBinding productsBinding;
    public static ArrayList<CallCommonCheckedList> checkedPrdList;
    CheckProductListAdapter checkProductListAdapter;
  FinalProductCallAdapter finalProductCallAdapter;
    SQLite sqLite;
    CommonUtilsMethods commonUtilsMethods;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        productsBinding = FragmentProductsBinding.inflate(inflater);
        View v = productsBinding.getRoot();
        sqLite = new SQLite(getContext());
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        if (DCRCallActivity.SampleValidation.equalsIgnoreCase("1")) {
            productsBinding.tagStock.setVisibility(View.VISIBLE);
        } else {
            productsBinding.tagStock.setVisibility(View.GONE);
        }

        HiddenVisibleFunction();
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
        productsBinding.tagRxQty.setText(DCRCallActivity.CapRxQty);
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
        checkProductListAdapter = new CheckProductListAdapter(getActivity(), getContext(), checkedPrdList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        productsBinding.rvCheckDataList.setLayoutManager(mLayoutManager);
        productsBinding.rvCheckDataList.setItemAnimator(new DefaultItemAnimator());
        productsBinding.rvCheckDataList.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));
        productsBinding.rvCheckDataList.setAdapter(checkProductListAdapter);

        finalProductCallAdapter = new FinalProductCallAdapter(getActivity(), getContext(), CheckProductListAdapter.saveCallProductListArrayList, checkedPrdList);
        productsBinding.rvListPrd.setLayoutManager(new WrapContentLinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        productsBinding.rvListPrd.setItemAnimator(new DefaultItemAnimator());
        productsBinding.rvListPrd.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));
        productsBinding.rvListPrd.setAdapter(finalProductCallAdapter);
    }

    private void filter(String text) {
        ArrayList<CallCommonCheckedList> filteredNames = new ArrayList<>();

      /*  if (text.toLowerCase().equalsIgnoreCase("SL")) {
            if (s.getCategory().equalsIgnoreCase("Sale")) {
                sale = s.getCategory();
            }*/
        for (CallCommonCheckedList s : checkedPrdList) {
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
                filteredNames.add(s);
            }
        }
        checkProductListAdapter.filterList(filteredNames);
    }
}