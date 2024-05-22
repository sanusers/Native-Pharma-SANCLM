package saneforce.sanzen.activity.call.fragments.additionalCall;

import static saneforce.sanzen.activity.call.DCRCallActivity.InputValidation;
import static saneforce.sanzen.activity.call.DCRCallActivity.PrdSamNeed;
import static saneforce.sanzen.activity.call.DCRCallActivity.SampleValidation;
import static saneforce.sanzen.activity.call.DCRCallActivity.StockInput;
import static saneforce.sanzen.activity.call.DCRCallActivity.StockSample;
import static saneforce.sanzen.activity.call.DCRCallActivity.dcrCallBinding;
import static saneforce.sanzen.activity.call.adapter.product.CheckProductListAdapter.saveCallProductListArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.adapter.input.CheckInputListAdapter;
import saneforce.sanzen.activity.call.adapter.input.FinalInputCallAdapter;
import saneforce.sanzen.activity.call.fragments.input.InputFragment;
import saneforce.sanzen.activity.call.fragments.product.ProductFragment;
import saneforce.sanzen.activity.call.pojo.additionalCalls.AddInputAdditionalCall;
import saneforce.sanzen.activity.call.pojo.additionalCalls.AddSampleAdditionalCall;
import saneforce.sanzen.activity.call.pojo.product.SaveCallProductList;
import saneforce.sanzen.activity.call.adapter.additionalCalls.finalSavedAdapter.FinalAdditionalCallAdapter;
import saneforce.sanzen.activity.call.adapter.additionalCalls.sideView.AdapterInputAdditionalCall;
import saneforce.sanzen.activity.call.adapter.additionalCalls.sideView.AdapterSampleAdditionalCall;
import saneforce.sanzen.activity.call.adapter.product.FinalProductCallAdapter;
import saneforce.sanzen.activity.call.pojo.CallCommonCheckedList;
import saneforce.sanzen.activity.call.pojo.input.SaveCallInputList;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.databinding.FragmentAddCallDetailsSideBinding;

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
    int lastPos;

    @SuppressLint("NotifyDataSetChanged")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        callDetailsSideBinding = FragmentAddCallDetailsSideBinding.inflate(getLayoutInflater());
        View v = callDetailsSideBinding.getRoot();

        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());

        callDetailsSideBinding.tabLayout.addTab(callDetailsSideBinding.tabLayout.newTab().setText("Product"));
        callDetailsSideBinding.tabLayout.addTab(callDetailsSideBinding.tabLayout.newTab().setText("Input"));

        setUpData();


        if (InputValidation.equalsIgnoreCase("1")) {
            callDetailsSideBinding.tagInputStock.setVisibility(View.VISIBLE);
        }

        if (PrdSamNeed.equalsIgnoreCase("1")) {
            callDetailsSideBinding.tagSamQty.setVisibility(View.VISIBLE);
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
                        commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.sel_input_before_add_new));
                    }
                } else {
                    commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_extra_input));
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
                        commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.sel_prd_before_add_new));
                    }
                } else {
                    commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_extra_prd));
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
                if (!addInputAdditionalCallArrayList.isEmpty()) {
                    for (int i = 0; i<addInputAdditionalCallArrayList.size(); i++) {
                        if(addInputAdditionalCallArrayList.get(i).getInp_qty().isEmpty()) {
                            commonUtilsMethods.showToastMessage(requireContext(), "Qty cannot be empty!");
                            callDetailsSideBinding.tabLayout.selectTab(callDetailsSideBinding.tabLayout.getTabAt(1));
                            return;
                        }
                    }
                }
                AddSampleInputData();

            } else if (FinalAdditionalCallAdapter.New_Edit.equalsIgnoreCase("Edit")) {
                if(!addInputAdditionalCallArrayList.isEmpty()) {
                    for (int i = 0; i<addInputAdditionalCallArrayList.size(); i++) {
                        if(addInputAdditionalCallArrayList.get(i).getInp_qty().isEmpty()) {
                            commonUtilsMethods.showToastMessage(requireContext(), "Qty cannot be empty!");
                            callDetailsSideBinding.tabLayout.selectTab(callDetailsSideBinding.tabLayout.getTabAt(1));
                            return;
                        }
                    }
                }
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
            AdditionalCallFragment.additionalCallBinding.rvListAdditional.setAdapter(finalAdditionalCallAdapter);
            dcrCallBinding.fragmentAddCallDetailsSide.setVisibility(View.GONE);
        });
        return v;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void AddNewInputData() {
        for (int i = 0; i < AddCallSelectInpSide.SelectACInputAdapter.callInputListAdapter.size(); i++) {
            if (AddCallSelectInpSide.SelectACInputAdapter.callInputListAdapter.get(i).isCheckedItem()) {
                AddCallSelectInpSide.SelectACInputAdapter.callInputListAdapter.get(i).setCheckedItem(false);
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
        for (int i = 0; i < AddCallSelectPrdSide.SelectACProductAdapter.callSampleListAdapter.size(); i++) {
            if (AddCallSelectPrdSide.SelectACProductAdapter.callSampleListAdapter.get(i).isCheckedItem()) {
                AddCallSelectPrdSide.SelectACProductAdapter.callSampleListAdapter.get(i).setCheckedItem(false);
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

                    for (int j = 0; j < InputFragment.checkedInputList.size(); j++) {
                        if (addInputAdditionalCallArrayList.get(i).getInput_code().equalsIgnoreCase(InputFragment.checkedInputList.get(j).getCode())) {
                            InputFragment.checkedInputList.set(j, new CallCommonCheckedList(InputFragment.checkedInputList.get(j).getName(), InputFragment.checkedInputList.get(j).getCode(), addInputAdditionalCallArrayList.get(i).getBalance_stock(), false));
                        }
                    }

                    if (CheckInputListAdapter.saveCallInputListArrayList.size() > 0) {
                        for (int j = 0; j < CheckInputListAdapter.saveCallInputListArrayList.size(); j++) {
                            int final_value;
                            for (int k = 0; k < StockInput.size(); k++) {
                                if (StockInput.get(k).getStockCode().equalsIgnoreCase(CheckInputListAdapter.saveCallInputListArrayList.get(j).getInp_code())) {
                                    if (CheckInputListAdapter.saveCallInputListArrayList.get(j).getInp_qty().equalsIgnoreCase("0") || CheckInputListAdapter.saveCallInputListArrayList.get(j).getInp_qty().isEmpty()) {
                                        final_value = Integer.parseInt(StockInput.get(k).getCurrentStock());
                                    } else {
                                        final_value = Integer.parseInt(StockInput.get(k).getCurrentStock()) + Integer.parseInt(CheckInputListAdapter.saveCallInputListArrayList.get(j).getInp_qty());
                                    }
                                    CheckInputListAdapter.saveCallInputListArrayList.set(j, new SaveCallInputList(CheckInputListAdapter.saveCallInputListArrayList.get(j).getInput_name(), CheckInputListAdapter.saveCallInputListArrayList.get(j).getInp_code(), CheckInputListAdapter.saveCallInputListArrayList.get(j).getInp_qty(), StockInput.get(k).getCurrentStock(), String.valueOf(final_value)));
                                }
                            }
                        }
                        finalInputCallAdapter = new FinalInputCallAdapter(requireActivity(), requireContext(), CheckInputListAdapter.saveCallInputListArrayList, InputFragment.checkedInputList);
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

                    for (int j = 0; j < ProductFragment.checkedPrdList.size(); j++) {
                        if (addProductAdditionalCallArrayList.get(i).getPrd_code().equalsIgnoreCase(ProductFragment.checkedPrdList.get(j).getCode())) {
                            ProductFragment.checkedPrdList.set(j, new CallCommonCheckedList(ProductFragment.checkedPrdList.get(j).getName(), ProductFragment.checkedPrdList.get(j).getCode(), addProductAdditionalCallArrayList.get(i).getBalance_stock(), false, ProductFragment.checkedPrdList.get(j).getCategory(), ProductFragment.checkedPrdList.get(j).getCategoryExtra()));
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
                        finalProductCallAdapter = new FinalProductCallAdapter(requireActivity(), requireContext(), saveCallProductListArrayList, ProductFragment.checkedPrdList);
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