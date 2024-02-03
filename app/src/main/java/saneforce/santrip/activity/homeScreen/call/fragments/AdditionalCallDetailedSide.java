package saneforce.santrip.activity.homeScreen.call.fragments;

import static saneforce.santrip.activity.homeScreen.call.DCRCallActivity.InputValidation;
import static saneforce.santrip.activity.homeScreen.call.DCRCallActivity.SampleValidation;
import static saneforce.santrip.activity.homeScreen.call.DCRCallActivity.StockInput;
import static saneforce.santrip.activity.homeScreen.call.DCRCallActivity.StockSample;
import static saneforce.santrip.activity.homeScreen.call.DCRCallActivity.dcrCallBinding;
import static saneforce.santrip.activity.homeScreen.call.adapter.additionalCalls.finalSavedAdapter.FinalAdditionalCallAdapter.Selected_code;
import static saneforce.santrip.activity.homeScreen.call.adapter.additionalCalls.finalSavedAdapter.FinalAdditionalCallAdapter.Selected_name;
import static saneforce.santrip.activity.homeScreen.call.adapter.additionalCalls.sideView.AdapterInputAdditionalCall.addedInpList;
import static saneforce.santrip.activity.homeScreen.call.adapter.additionalCalls.sideView.AdapterSampleAdditionalCall.addedProductList;
import static saneforce.santrip.activity.homeScreen.call.adapter.input.CheckInputListAdapter.saveCallInputListArrayList;
import static saneforce.santrip.activity.homeScreen.call.adapter.product.CheckProductListAdapter.saveCallProductListArrayList;
import static saneforce.santrip.activity.homeScreen.call.fragments.InputFragment.checkedInputList;
import static saneforce.santrip.activity.homeScreen.call.fragments.ProductFragment.checkedPrdList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Objects;

import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.call.adapter.additionalCalls.finalSavedAdapter.FinalAdditionalCallAdapter;
import saneforce.santrip.activity.homeScreen.call.adapter.additionalCalls.sideView.AdapterInputAdditionalCall;
import saneforce.santrip.activity.homeScreen.call.adapter.additionalCalls.sideView.AdapterSampleAdditionalCall;
import saneforce.santrip.activity.homeScreen.call.adapter.input.FinalInputCallAdapter;
import saneforce.santrip.activity.homeScreen.call.adapter.product.FinalProductCallAdapter;
import saneforce.santrip.activity.homeScreen.call.pojo.CallCommonCheckedList;
import saneforce.santrip.activity.homeScreen.call.pojo.additionalCalls.AddInputAdditionalCall;
import saneforce.santrip.activity.homeScreen.call.pojo.additionalCalls.AddSampleAdditionalCall;
import saneforce.santrip.activity.homeScreen.call.pojo.input.SaveCallInputList;
import saneforce.santrip.activity.homeScreen.call.pojo.product.SaveCallProductList;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.databinding.FragmentAddCallDetailsSideBinding;
import saneforce.santrip.storage.SQLite;

public class AdditionalCallDetailedSide extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static FragmentAddCallDetailsSideBinding callDetailsSideBinding;
    public static ArrayList<AddInputAdditionalCall> addInputAdditionalCallArrayList;
    public static ArrayList<AddInputAdditionalCall> editedInpList;
    public static ArrayList<AddSampleAdditionalCall> addProductAdditionalCallArrayList;
    public static ArrayList<AddSampleAdditionalCall> editedPrdList;
    @SuppressLint("StaticFieldLeak")
    public static AdapterInputAdditionalCall adapterInputAdditionalCall;
    @SuppressLint("StaticFieldLeak")
    public static AdapterSampleAdditionalCall adapterSampleAdditionalCall;
    CommonUtilsMethods commonUtilsMethods;
    FinalAdditionalCallAdapter finalAdditionalCallAdapter;
    FinalInputCallAdapter finalInputCallAdapter;
    FinalProductCallAdapter finalProductCallAdapter;
    SQLite sqLite;
    int lastPos;

    @SuppressLint("NotifyDataSetChanged")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        callDetailsSideBinding = FragmentAddCallDetailsSideBinding.inflate(getLayoutInflater());
        View v = callDetailsSideBinding.getRoot();

        sqLite = new SQLite(requireContext());
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());

        callDetailsSideBinding.tabLayout.addTab(callDetailsSideBinding.tabLayout.newTab().setText("Product"));
        callDetailsSideBinding.tabLayout.addTab(callDetailsSideBinding.tabLayout.newTab().setText("Input"));

        setUpData();


        if (InputValidation.equalsIgnoreCase("1")) {
            callDetailsSideBinding.tagInputStock.setVisibility(View.VISIBLE);
        }

        if (SampleValidation.equalsIgnoreCase("1")) {
            callDetailsSideBinding.tagSamStock.setVisibility(View.VISIBLE);
        }

        callDetailsSideBinding.tvDummy.setOnClickListener(view -> {
        });

        callDetailsSideBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                HideKeyboard();
                if (tab.getPosition() == 0) {
                    callDetailsSideBinding.constraintMainInput.setVisibility(View.INVISIBLE);
                    callDetailsSideBinding.constraintMainSample.setVisibility(View.VISIBLE);
                } else if (tab.getPosition() == 1) {
                    callDetailsSideBinding.constraintMainInput.setVisibility(View.VISIBLE);
                    callDetailsSideBinding.constraintMainSample.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        callDetailsSideBinding.imgClose.setOnClickListener(view -> {
            dcrCallBinding.fragmentAddCallDetailsSide.setVisibility(View.GONE);
            TabLayout.Tab tab = callDetailsSideBinding.tabLayout.getTabAt(0);
            assert tab != null;
            tab.select();
        });

        callDetailsSideBinding.btnAddInput.setOnClickListener(view -> {
            HideKeyboard();
            if (addInputAdditionalCallArrayList.size() > 1) {
                lastPos = addInputAdditionalCallArrayList.size() - 1;
                if (AddCallSelectInpSide.callInputList.size() > addInputAdditionalCallArrayList.size()) {
                    if (!addInputAdditionalCallArrayList.get(lastPos).getInput_name().equalsIgnoreCase("Select") && !addInputAdditionalCallArrayList.get(lastPos).getInput_name().isEmpty()) {
                        AddNewInputData();
                    } else {
                        commonUtilsMethods.ShowToast(getContext(), requireContext().getString(R.string.sel_input_before_add_new),100);
                    }
                } else {
                    commonUtilsMethods.ShowToast(getContext(),requireContext().getString(R.string.no_extra_input),100);
                }
            } else {
                AddNewInputData();
            }
        });

        callDetailsSideBinding.btnAddSample.setOnClickListener(view -> {
            HideKeyboard();
            if (addProductAdditionalCallArrayList.size() > 1) {
                lastPos = addProductAdditionalCallArrayList.size() - 1;
                if (AddCallSelectPrdSide.callSampleList.size() > addProductAdditionalCallArrayList.size()) {
                    if (!addProductAdditionalCallArrayList.get(lastPos).getPrd_name().equalsIgnoreCase("Select") && !addProductAdditionalCallArrayList.get(lastPos).getPrd_name().isEmpty()) {
                        AddNewSampleData();
                    } else {
                        commonUtilsMethods.ShowToast(getContext(),requireContext().getString(R.string.sel_prd_before_add_new),100);
                    }
                } else {
                    commonUtilsMethods.ShowToast(getContext(),requireContext().getString(R.string.no_extra_prd),100);
                }
            } else {
                AddNewSampleData();
            }
        });

        callDetailsSideBinding.btnSave.setOnClickListener(view -> {
            HideKeyboard();
            TabLayout.Tab tab = callDetailsSideBinding.tabLayout.getTabAt(0);
            assert tab != null;
            tab.select();
            if (FinalAdditionalCallAdapter.New_Edit.equalsIgnoreCase("New")) {
                AddSampleInputData();

            } else if (FinalAdditionalCallAdapter.New_Edit.equalsIgnoreCase("Edit")) {
                for (int j = 0; j < FinalAdditionalCallAdapter.nestedInput.size(); j++) {
                    if (FinalAdditionalCallAdapter.nestedInput.get(j).getCust_code().equalsIgnoreCase(FinalAdditionalCallAdapter.Selected_code)) {
                        FinalAdditionalCallAdapter.nestedInput.remove(j);
                        j--;
                    }
                }

                for (int j = 0; j < FinalAdditionalCallAdapter.nestedProduct.size(); j++) {
                    if (FinalAdditionalCallAdapter.nestedProduct.get(j).getCust_code().equalsIgnoreCase(FinalAdditionalCallAdapter.Selected_code)) {
                        FinalAdditionalCallAdapter.nestedProduct.remove(j);
                        j--;
                    }
                }

                AddSampleInputData();
            }
            finalAdditionalCallAdapter = new FinalAdditionalCallAdapter(getActivity(), getContext(), FinalAdditionalCallAdapter.checked_arrayList, FinalAdditionalCallAdapter.saveAdditionalCalls, FinalAdditionalCallAdapter.nestedInput, FinalAdditionalCallAdapter.nestedProduct, FinalAdditionalCallAdapter.dummyNestedInput, FinalAdditionalCallAdapter.dummyNestedSample);
            commonUtilsMethods.recycleTestWithoutDivider(FinalAdditionalCallAdapter.rv_nested_calls_input_data);
            commonUtilsMethods.recycleTestWithoutDivider(FinalAdditionalCallAdapter.rv_nested_calls_sample_data);
            AdditionalCallFragment.rv_add_call_list.setAdapter(finalAdditionalCallAdapter);
            dcrCallBinding.fragmentAddCallDetailsSide.setVisibility(View.GONE);
        });
        return v;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void AddNewInputData() {
        for (int i = 0; i < AddCallSelectInpSide.SelectACInputAdapter.callInputList.size(); i++) {
            if (AddCallSelectInpSide.SelectACInputAdapter.callInputList.get(i).isCheckedItem()) {
                AddCallSelectInpSide.SelectACInputAdapter.callInputList.get(i).setCheckedItem(false);
            }
        }
        AddCallSelectInpSide.selectInputSideBinding.searchList.setText("");
        AddCallSelectInpSide.selectInputSideBinding.searchList.setHint(getResources().getString(R.string.search));
        AddCallSelectInpSide.selectInputSideBinding.selectListView.scrollToPosition(0);
        dcrCallBinding.fragmentAcSelectInputSide.setVisibility(View.VISIBLE);
      /*  addInputAdditionalCallArrayList.add(new AddInputAdditionalCall(FinalAdditionalCallAdapter.Selected_name, FinalAdditionalCallAdapter.Selected_code, "Select", "", "0", "0", ""));
        callDetailsSideBinding.rvAddInputsAdditional.setAdapter(adapterInputAdditionalCall);
        adapterInputAdditionalCall.notifyDataSetChanged();*/
    }

    @SuppressLint("NotifyDataSetChanged")
    private void AddNewSampleData() {
        for (int i = 0; i < AddCallSelectPrdSide.SelectACProductAdapter.callSampleList.size(); i++) {
            if (AddCallSelectPrdSide.SelectACProductAdapter.callSampleList.get(i).isCheckedItem()) {
                AddCallSelectPrdSide.SelectACProductAdapter.callSampleList.get(i).setCheckedItem(false);
            }
        }
        AddCallSelectPrdSide.selectProductSideBinding.searchList.setText("");
        AddCallSelectPrdSide.selectProductSideBinding.searchList.setHint(getResources().getString(R.string.search));
        AddCallSelectPrdSide.selectProductSideBinding.selectListView.scrollToPosition(0);
        dcrCallBinding.fragmentAcSelectProductSide.setVisibility(View.VISIBLE);
      /*  addProductAdditionalCallArrayList.add(new AddSampleAdditionalCall(FinalAdditionalCallAdapter.Selected_name, FinalAdditionalCallAdapter.Selected_code, "Select", "", "0", "0", "0", ""));
        callDetailsSideBinding.rvAddSampleAdditional.setAdapter(adapterSampleAdditionalCall);
        adapterSampleAdditionalCall.notifyDataSetChanged();*/
    }

    @SuppressLint("NotifyDataSetChanged")
    private void AddSampleInputData() {
        if (addInputAdditionalCallArrayList.size() > 0) {
            for (int i = 0; i < addInputAdditionalCallArrayList.size(); i++) {
                FinalAdditionalCallAdapter.nestedInput.add(new AddInputAdditionalCall(addInputAdditionalCallArrayList.get(i).getCust_name(), addInputAdditionalCallArrayList.get(i).getCust_code(), addInputAdditionalCallArrayList.get(i).getInput_name(), addInputAdditionalCallArrayList.get(i).getInput_code(), addInputAdditionalCallArrayList.get(i).getBalance_stock(), addInputAdditionalCallArrayList.get(i).getLast_stock(), addInputAdditionalCallArrayList.get(i).getInp_qty()));

                if (InputValidation.equalsIgnoreCase("1")) {
                    for (int j = 0; j < StockInput.size(); j++) {
                        if (StockInput.get(j).getStockCode().equalsIgnoreCase(addInputAdditionalCallArrayList.get(i).getInput_code())) {
                            StockInput.set(j, new CallCommonCheckedList(StockInput.get(j).getStockCode(), StockInput.get(j).getActualStock(), addInputAdditionalCallArrayList.get(i).getBalance_stock()));
                        }
                    }

                    for (int j = 0; j < checkedInputList.size(); j++) {
                        if (addInputAdditionalCallArrayList.get(i).getInput_code().equalsIgnoreCase(checkedInputList.get(j).getCode())) {
                            checkedInputList.set(j, new CallCommonCheckedList(checkedInputList.get(j).getName(), checkedInputList.get(j).getCode(), addInputAdditionalCallArrayList.get(i).getBalance_stock(), false));
                        }
                    }

                    if (saveCallInputListArrayList.size() > 0) {
                        for (int j = 0; j < saveCallInputListArrayList.size(); j++) {
                            int final_value;
                            for (int k = 0; k < StockInput.size(); k++) {
                                if (StockInput.get(k).getStockCode().equalsIgnoreCase(saveCallInputListArrayList.get(j).getInp_code())) {
                                    if (saveCallInputListArrayList.get(j).getInp_qty().equalsIgnoreCase("0") || saveCallInputListArrayList.get(j).getInp_qty().isEmpty()) {
                                        final_value = Integer.parseInt(StockInput.get(k).getCurrentStock());
                                    } else {
                                        final_value = Integer.parseInt(StockInput.get(k).getCurrentStock()) + Integer.parseInt(saveCallInputListArrayList.get(j).getInp_qty());
                                    }
                                    saveCallInputListArrayList.set(j, new SaveCallInputList(saveCallInputListArrayList.get(j).getInput_name(), saveCallInputListArrayList.get(j).getInp_code(), saveCallInputListArrayList.get(j).getInp_qty(), StockInput.get(k).getCurrentStock(), String.valueOf(final_value)));
                                }
                            }
                        }
                        finalInputCallAdapter = new FinalInputCallAdapter(requireActivity(), requireContext(), saveCallInputListArrayList, checkedInputList);
                        InputFragment.fragmentInputBinding.rvListInput.setAdapter(finalInputCallAdapter);
                        finalInputCallAdapter.notifyDataSetChanged();
                    }
                }
            }
        }

        if (addProductAdditionalCallArrayList.size() > 0) {
            for (int i = 0; i < addProductAdditionalCallArrayList.size(); i++) {
                FinalAdditionalCallAdapter.nestedProduct.add(new AddSampleAdditionalCall(addProductAdditionalCallArrayList.get(i).getCust_name(), addProductAdditionalCallArrayList.get(i).getCust_code(), addProductAdditionalCallArrayList.get(i).getPrd_name(), addProductAdditionalCallArrayList.get(i).getPrd_code(), addProductAdditionalCallArrayList.get(i).getBalance_stock(), addProductAdditionalCallArrayList.get(i).getLast_stock(), addProductAdditionalCallArrayList.get(i).getSample_qty(), addProductAdditionalCallArrayList.get(i).getCategory()));

                if (SampleValidation.equalsIgnoreCase("1")) {
                    for (int j = 0; j < StockSample.size(); j++) {
                        if (StockSample.get(j).getStockCode().equalsIgnoreCase(addProductAdditionalCallArrayList.get(i).getPrd_code())) {
                            StockSample.set(j, new CallCommonCheckedList(StockSample.get(j).getStockCode(), StockSample.get(j).getActualStock(), addProductAdditionalCallArrayList.get(i).getBalance_stock()));
                        }
                    }

                    for (int j = 0; j < checkedPrdList.size(); j++) {
                        if (addProductAdditionalCallArrayList.get(i).getPrd_code().equalsIgnoreCase(checkedPrdList.get(j).getCode())) {
                            checkedPrdList.set(j, new CallCommonCheckedList(checkedPrdList.get(j).getName(), checkedPrdList.get(j).getCode(), addProductAdditionalCallArrayList.get(i).getBalance_stock(), false, checkedPrdList.get(j).getCategory(), checkedPrdList.get(j).getCategoryExtra()));
                        }
                    }

                    if (saveCallProductListArrayList.size() > 0) {
                        for (int j = 0; j < saveCallProductListArrayList.size(); j++) {
                            int final_value;
                            for (int k = 0; k < StockSample.size(); k++) {
                                if (StockSample.get(k).getStockCode().equalsIgnoreCase(saveCallProductListArrayList.get(j).getCode())) {
                                    if (saveCallProductListArrayList.get(j).getSample_qty().equalsIgnoreCase("0") || saveCallProductListArrayList.get(j).getSample_qty().isEmpty()) {
                                        final_value = Integer.parseInt(StockSample.get(k).getCurrentStock());
                                    } else {
                                        final_value = Integer.parseInt(StockSample.get(k).getCurrentStock()) + Integer.parseInt(saveCallProductListArrayList.get(j).getSample_qty());
                                    }
                                    saveCallProductListArrayList.set(j, new SaveCallProductList(saveCallProductListArrayList.get(j).getName(), saveCallProductListArrayList.get(j).getCode(), saveCallProductListArrayList.get(j).getCategory(), StockSample.get(k).getCurrentStock(), String.valueOf(final_value), saveCallProductListArrayList.get(j).getSample_qty(), saveCallProductListArrayList.get(j).getRx_qty(), saveCallProductListArrayList.get(j).getRcpa_qty(), saveCallProductListArrayList.get(j).getPromoted(), saveCallProductListArrayList.get(j).isClicked()));
                                }
                            }
                        }
                        finalProductCallAdapter = new FinalProductCallAdapter(requireActivity(), requireContext(), saveCallProductListArrayList, checkedPrdList);
                        ProductFragment.productsBinding.rvListPrd.setAdapter(finalProductCallAdapter);
                        finalProductCallAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    private void HideKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(callDetailsSideBinding.tabLayout.getWindowToken(), 0);
    }

    private void setUpData() {
        adapterInputAdditionalCall = new AdapterInputAdditionalCall(getActivity(), addInputAdditionalCallArrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        callDetailsSideBinding.rvAddInputsAdditional.setLayoutManager(mLayoutManager);
        callDetailsSideBinding.rvAddInputsAdditional.setAdapter(adapterInputAdditionalCall);

        adapterSampleAdditionalCall = new AdapterSampleAdditionalCall(getActivity(), addProductAdditionalCallArrayList);
        RecyclerView.LayoutManager mLayoutManagerPrd = new LinearLayoutManager(getActivity());
        callDetailsSideBinding.rvAddSampleAdditional.setLayoutManager(mLayoutManagerPrd);
        callDetailsSideBinding.rvAddSampleAdditional.setAdapter(adapterSampleAdditionalCall);
    }
}