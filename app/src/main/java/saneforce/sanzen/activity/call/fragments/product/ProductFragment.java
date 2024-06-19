package saneforce.sanzen.activity.call.fragments.product;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import saneforce.sanzen.activity.call.DCRCallActivity;
import saneforce.sanzen.activity.call.adapter.product.CheckProductListAdapter;
import saneforce.sanzen.activity.call.adapter.product.FinalProductCallAdapter;
import saneforce.sanzen.activity.call.pojo.CallCommonCheckedList;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.WrapContentLinearLayoutManager;
import saneforce.sanzen.databinding.FragmentProductsBinding;

public class ProductFragment extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static FragmentProductsBinding productsBinding;
    public static ArrayList<CallCommonCheckedList> checkedPrdList;
    CheckProductListAdapter checkProductListAdapter;
    FinalProductCallAdapter finalProductCallAdapter;
    CommonUtilsMethods commonUtilsMethods;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        productsBinding = FragmentProductsBinding.inflate(inflater);
        View v = productsBinding.getRoot();
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

        if(DCRCallActivity.CallActivityCustDetails.get(0).getType().equalsIgnoreCase("2")||DCRCallActivity.CallActivityCustDetails.get(0).getType().equalsIgnoreCase("3")){
            productsBinding.tagPromoted.setVisibility(View.GONE);
        }

        switch (DCRCallActivity.CallActivityCustDetails.get(0).getType()) {

            case "1":
                Log.d("listsize123", "logsccuess");
                if (DCRCallActivity.save_valid.equals("0")) {
                    if (DCRCallActivity.PrdSamNeed.equalsIgnoreCase("1")) {
                        productsBinding.tagSamples.setVisibility(View.VISIBLE);
                    }
                    if (DCRCallActivity.PrdRxNeed.equalsIgnoreCase("1")) {
                        productsBinding.tagRxQty.setVisibility(View.VISIBLE);
                    }
                    if (DCRCallActivity.PrdRcpaQtyNeed.equalsIgnoreCase("1")) {
                        productsBinding.tagRcpa.setVisibility(View.VISIBLE);
                    }

                } else {
                    productsBinding.tagRxQty.setText("Qty");
                    productsBinding.tagSamples.setText(" ");
                    productsBinding.tagRcpa.setText(" ");

                    if (DCRCallActivity.PrdRxNeed.equalsIgnoreCase("1")) {
                        productsBinding.tagRxQty.setVisibility(View.VISIBLE);
                    }
                    if (DCRCallActivity.PrdSamNeed.equalsIgnoreCase("1")) {
//                        productsBinding.tagRcpa.setVisibility(View.GONE);

//                        productsBinding.tagSamples.setVisibility(View.GONE);
                        productsBinding.tagPromoted.setVisibility(View.GONE);
                    }
                    if (DCRCallActivity.PrdRcpaQtyNeed.equalsIgnoreCase("1")) {
                        productsBinding.tagRcpa.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case "2":
            case "3":
            case "4":
                Log.d("listsize123", "logsccuess12");

                if (DCRCallActivity.save_valid.equals("0")) {
                    if (DCRCallActivity.PrdSamNeed.equalsIgnoreCase("0"))
                        productsBinding.tagSamples.setVisibility(View.VISIBLE);
                    if (DCRCallActivity.PrdRxNeed.equalsIgnoreCase("0"))
                        productsBinding.tagRxQty.setVisibility(View.VISIBLE);
//                    if (DCRCallActivity.PrdRcpaQtyNeed.equalsIgnoreCase("1")) {
                        productsBinding.tagRcpa.setVisibility(View.VISIBLE);
//                    }
                } else {
                    productsBinding.tagRxQty.setText("Qty");
                    productsBinding.tagSamples.setText(" ");
                    productsBinding.tagRcpa.setText(" ");


                    if (DCRCallActivity.PrdSamNeed.equalsIgnoreCase("0"))
//                        productsBinding.tagSamples.setVisibility(View.GONE);
                        if (DCRCallActivity.PrdRxNeed.equalsIgnoreCase("0"))
                            productsBinding.tagRxQty.setVisibility(View.VISIBLE);
                    if (DCRCallActivity.PrdRxNeed.equalsIgnoreCase("1"))
                            productsBinding.tagRxQty.setVisibility(View.VISIBLE);
//                    productsBinding.tagRcpa.setVisibility(View.GONE);
//                    productsBinding.tagSamples.setVisibility(View.GONE);
                    productsBinding.tagPromoted.setVisibility(View.GONE);
//                    if (DCRCallActivity.PrdRcpaQtyNeed.equalsIgnoreCase("1")) {
                        productsBinding.tagRcpa.setVisibility(View.VISIBLE);
//                    }
                }


                Log.d("listsize123", "logsccuess112");
                break;
            case "6":
                Log.d("listsize123", "logsccuess9999");
                if (DCRCallActivity.PrdSamNeed.equalsIgnoreCase("1"))
                    productsBinding.tagSamples.setVisibility(View.VISIBLE);
                if (DCRCallActivity.PrdRxNeed.equalsIgnoreCase("1"))
                    productsBinding.tagRxQty.setVisibility(View.VISIBLE);
//                if (DCRCallActivity.PrdRcpaQtyNeed.equalsIgnoreCase("1")) {
                    productsBinding.tagRcpa.setVisibility(View.VISIBLE);
//                }
                break;
            default:
                Log.d("listsize123", "logsccuess_5555");
                productsBinding.tagSamples.setVisibility(View.GONE);
                productsBinding.tagRxQty.setVisibility(View.GONE);
                productsBinding.tagRcpa.setVisibility(View.GONE);
                break;
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
