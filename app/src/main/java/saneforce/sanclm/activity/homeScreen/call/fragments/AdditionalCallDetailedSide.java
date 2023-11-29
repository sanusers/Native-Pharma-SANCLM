package saneforce.sanclm.activity.homeScreen.call.fragments;

import static saneforce.sanclm.activity.homeScreen.call.DCRCallActivity.InputValidation;
import static saneforce.sanclm.activity.homeScreen.call.DCRCallActivity.SampleValidation;
import static saneforce.sanclm.activity.homeScreen.call.DCRCallActivity.dcrcallBinding;
import static saneforce.sanclm.activity.homeScreen.call.adapter.input.CallInputListAdapter.saveCallInputListArrayList;
import static saneforce.sanclm.activity.homeScreen.call.fragments.InputFragment.callCommonCheckedListArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
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

import saneforce.sanclm.activity.homeScreen.call.adapter.additionalCalls.finalSavedAdapter.SaveAdditionalCallAdapter;
import saneforce.sanclm.activity.homeScreen.call.adapter.additionalCalls.sideView.AdapterInputAdditionalCall;
import saneforce.sanclm.activity.homeScreen.call.adapter.additionalCalls.sideView.AdapterSampleAdditionalCall;
import saneforce.sanclm.activity.homeScreen.call.adapter.input.SaveInputCallAdapter;
import saneforce.sanclm.activity.homeScreen.call.pojo.CallCommonCheckedList;
import saneforce.sanclm.activity.homeScreen.call.pojo.additionalCalls.AddInputAdditionalCall;
import saneforce.sanclm.activity.homeScreen.call.pojo.additionalCalls.AddSampleAdditionalCall;
import saneforce.sanclm.activity.homeScreen.call.pojo.input.SaveCallInputList;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.databinding.FragmentAddCallDetailsSideBinding;
import saneforce.sanclm.storage.SQLite;

public class AdditionalCallDetailedSide extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static FragmentAddCallDetailsSideBinding callDetailsSideBinding;
    public static ArrayList<AddInputAdditionalCall> addInputAdditionalCallArrayList;
    //public static ArrayList<CallCommonCheckedList> callInputList;
    public static ArrayList<AddSampleAdditionalCall> addSampleAdditionalCallArrayList;
    // public static ArrayList<CallCommonCheckedList> callSampleList;
    @SuppressLint("StaticFieldLeak")
    public static AdapterInputAdditionalCall adapterInputAdditionalCall;
    @SuppressLint("StaticFieldLeak")
    public static AdapterSampleAdditionalCall adapterSampleAdditionalCall;
    CommonUtilsMethods commonUtilsMethods;
    SaveAdditionalCallAdapter saveAdditionalCallAdapter;
    SaveInputCallAdapter saveInputCallAdapter;
    SQLite sqLite;
    int lastPos;

    @SuppressLint("NotifyDataSetChanged")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        callDetailsSideBinding = FragmentAddCallDetailsSideBinding.inflate(getLayoutInflater());
        View v = callDetailsSideBinding.getRoot();

        sqLite = new SQLite(requireContext());
        commonUtilsMethods = new CommonUtilsMethods(getActivity());

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

        callDetailsSideBinding.imgClose.setOnClickListener(view -> dcrcallBinding.fragmentAddCallDetailsSide.setVisibility(View.GONE));

        callDetailsSideBinding.btnAddInput.setOnClickListener(view -> {
            HideKeyboard();
            Log.v("sdsd", "----" + addInputAdditionalCallArrayList.size());
            if (addInputAdditionalCallArrayList.size() > 1) {
                lastPos = addInputAdditionalCallArrayList.size() - 1;
                if (AddCallSelectInpSide.callInputList.size() > addInputAdditionalCallArrayList.size()) {
                    if (!addInputAdditionalCallArrayList.get(lastPos).getInput_name().equalsIgnoreCase("Select") && !addInputAdditionalCallArrayList.get(lastPos).getInput_name().isEmpty()) {
                        AddNewInputData();
                    } else {
                        Toast.makeText(requireContext(), "Select the Input before add new Input", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "There is no Extra Input Available to add", Toast.LENGTH_SHORT).show();
                }
            } else {
                AddNewInputData();
            }
        });

        callDetailsSideBinding.btnAddSample.setOnClickListener(view -> {
            HideKeyboard();
            if (addSampleAdditionalCallArrayList.size() > 1) {
                lastPos = addSampleAdditionalCallArrayList.size() - 1;
                if (AddCallSelectPrdSide.callSampleList.size() > addSampleAdditionalCallArrayList.size()) {
                    if (!addSampleAdditionalCallArrayList.get(lastPos).getPrd_name().equalsIgnoreCase("Select") && !addSampleAdditionalCallArrayList.get(lastPos).getPrd_name().isEmpty()) {
                        AddNewSampleData();
                    } else {
                        Toast.makeText(requireContext(), "Select the Product before add new Product", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "There is no Extra Product Available to add", Toast.LENGTH_SHORT).show();
                }
            } else {
                AddNewSampleData();
            }
        });

        callDetailsSideBinding.btnSave.setOnClickListener(view -> {
            HideKeyboard();
            if (SaveAdditionalCallAdapter.New_Edit.equalsIgnoreCase("New")) {
                AddSampleInputDatas();

            } else if (SaveAdditionalCallAdapter.New_Edit.equalsIgnoreCase("Edit")) {
                for (int j = 0; j < SaveAdditionalCallAdapter.nestedAddInputCallDetails.size(); j++) {
                    if (SaveAdditionalCallAdapter.nestedAddInputCallDetails.get(j).getCust_code().equalsIgnoreCase(SaveAdditionalCallAdapter.Selected_code)) {
                        SaveAdditionalCallAdapter.nestedAddInputCallDetails.remove(j);
                        j--;
                    }
                }

                for (int j = 0; j < SaveAdditionalCallAdapter.nestedAddSampleCallDetails.size(); j++) {
                    if (SaveAdditionalCallAdapter.nestedAddSampleCallDetails.get(j).getCust_code().equalsIgnoreCase(SaveAdditionalCallAdapter.Selected_code)) {
                        SaveAdditionalCallAdapter.nestedAddSampleCallDetails.remove(j);
                        j--;
                    }
                }

                AddSampleInputDatas();
            }
            saveAdditionalCallAdapter = new SaveAdditionalCallAdapter(getActivity(), getContext(), SaveAdditionalCallAdapter.checked_arrayList, SaveAdditionalCallAdapter.saveAdditionalCalls, SaveAdditionalCallAdapter.nestedAddInputCallDetails, SaveAdditionalCallAdapter.nestedAddSampleCallDetails, SaveAdditionalCallAdapter.dummyNestedInput, SaveAdditionalCallAdapter.dummyNestedSample);
            commonUtilsMethods.recycleTestWithoutDivider(SaveAdditionalCallAdapter.rv_nested_calls_input_data);
            commonUtilsMethods.recycleTestWithoutDivider(SaveAdditionalCallAdapter.rv_nested_calls_sample_data);
            AdditionalCallFragment.rv_add_call_list.setAdapter(saveAdditionalCallAdapter);
            dcrcallBinding.fragmentAddCallDetailsSide.setVisibility(View.GONE);
        });
        return v;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void AddNewInputData() {
        addInputAdditionalCallArrayList.add(new AddInputAdditionalCall(SaveAdditionalCallAdapter.Selected_name, SaveAdditionalCallAdapter.Selected_code, "Select", "", "0", "0", ""));
        callDetailsSideBinding.rvAddInputsAdditional.setAdapter(adapterInputAdditionalCall);
        adapterInputAdditionalCall.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void AddNewSampleData() {
        addSampleAdditionalCallArrayList.add(new AddSampleAdditionalCall(SaveAdditionalCallAdapter.Selected_name, SaveAdditionalCallAdapter.Selected_code, "Select", "", "0", "0", "0", ""));
        callDetailsSideBinding.rvAddSampleAdditional.setAdapter(adapterSampleAdditionalCall);
        adapterSampleAdditionalCall.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void AddSampleInputDatas() {
        if (addInputAdditionalCallArrayList.size() > 0) {
            for (int i = 0; i < addInputAdditionalCallArrayList.size(); i++) {
                SaveAdditionalCallAdapter.nestedAddInputCallDetails.add(new AddInputAdditionalCall(addInputAdditionalCallArrayList.get(i).getCust_name(), addInputAdditionalCallArrayList.get(i).getCust_code(), addInputAdditionalCallArrayList.get(i).getInput_name(), addInputAdditionalCallArrayList.get(i).getInput_code(), addInputAdditionalCallArrayList.get(i).getBalance_stock(), addInputAdditionalCallArrayList.get(i).getLast_stock(), addInputAdditionalCallArrayList.get(i).getInp_qty()));

                for (int j = 0; j < callCommonCheckedListArrayList.size(); j++) {
                    if (addInputAdditionalCallArrayList.get(i).getInput_code().equalsIgnoreCase(callCommonCheckedListArrayList.get(j).getCode())) {
                        callCommonCheckedListArrayList.set(j, new CallCommonCheckedList(callCommonCheckedListArrayList.get(j).getName(), callCommonCheckedListArrayList.get(j).getCode(), addInputAdditionalCallArrayList.get(i).getBalance_stock(), false));
                    }
                }

                for (int j = 0; j < saveCallInputListArrayList.size(); j++) {
                    if (addInputAdditionalCallArrayList.get(i).getInput_code().equalsIgnoreCase(saveCallInputListArrayList.get(j).getInp_code())) {
                        saveCallInputListArrayList.set(j, new SaveCallInputList(saveCallInputListArrayList.get(j).getInput_name(), saveCallInputListArrayList.get(j).getInp_code(), saveCallInputListArrayList.get(j).getInp_qty(), addInputAdditionalCallArrayList.get(i).getBalance_stock(), saveCallInputListArrayList.get(j).getBalance_inp_stk()));
                    }
                }


                saveInputCallAdapter = new SaveInputCallAdapter(requireActivity(), requireContext(), saveCallInputListArrayList, callCommonCheckedListArrayList);
                InputFragment.fragmentInputBinding.rvListInput.setAdapter(saveInputCallAdapter);
                saveInputCallAdapter.notifyDataSetChanged();
            }
        }

        if (addSampleAdditionalCallArrayList.size() > 0) {
            for (int i = 0; i < addSampleAdditionalCallArrayList.size(); i++) {
                SaveAdditionalCallAdapter.nestedAddSampleCallDetails.add(new AddSampleAdditionalCall(addSampleAdditionalCallArrayList.get(i).getCust_name(), addSampleAdditionalCallArrayList.get(i).getCust_code(), addSampleAdditionalCallArrayList.get(i).getPrd_name(), addSampleAdditionalCallArrayList.get(i).getPrd_code(), addSampleAdditionalCallArrayList.get(i).getBalance_stock(), addSampleAdditionalCallArrayList.get(i).getLast_stock(), addSampleAdditionalCallArrayList.get(i).getSample_qty(), addSampleAdditionalCallArrayList.get(i).getCategory()));
            }
        }


      /*  if (addInputAdditionalCallArrayList.size() > 0) {
            for (int i = 0; i < addInputAdditionalCallArrayList.size(); i++) {
                SaveAdditionalCallAdapter.nestedAddInputCallDetails.add(new AddInputAdditionalCall(addInputAdditionalCallArrayList.get(i).getCust_name(), addInputAdditionalCallArrayList.get(i).getCust_code(), addInputAdditionalCallArrayList.get(i).getInput_name(),addInputAdditionalCallArrayList.get(i).getInput_code(),addInputAdditionalCallArrayList.get(i).getBalance_stock(),addInputAdditionalCallArrayList.get(i).getLast_stock(), addInputAdditionalCallArrayList.get(i).getInp_qty()));
            }
        }

        if (addSampleAdditionalCallArrayList.size() > 0) {
            for (int i = 0; i < addSampleAdditionalCallArrayList.size(); i++) {
                SaveAdditionalCallAdapter.nestedAddSampleCallDetails.add(new AddSampleAdditionalCall(addSampleAdditionalCallArrayList.get(i).getCust_name(), addSampleAdditionalCallArrayList.get(i).getCust_code(), addSampleAdditionalCallArrayList.get(i).getPrd_name(), addSampleAdditionalCallArrayList.get(i).getPrd_code(),addSampleAdditionalCallArrayList.get(i).getBalance_stock() ,addSampleAdditionalCallArrayList.get(i).getLast_stock(),addSampleAdditionalCallArrayList.get(i).getSample_qty(),addSampleAdditionalCallArrayList.get(i).getCategory()));
            }
        }*/
    }

    private void HideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(callDetailsSideBinding.tabLayout.getWindowToken(), 0);
    }

    private void setUpData() {
        //  adapterInputAdditionalCall = new AdapterInputAdditionalCall(getActivity(), addInputAdditionalCallArrayList, callInputList);
        adapterInputAdditionalCall = new AdapterInputAdditionalCall(getActivity(), addInputAdditionalCallArrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        callDetailsSideBinding.rvAddInputsAdditional.setLayoutManager(mLayoutManager);
        callDetailsSideBinding.rvAddInputsAdditional.setAdapter(adapterInputAdditionalCall);

        // adapterSampleAdditionalCall = new AdapterSampleAdditionalCall(getActivity(), addSampleAdditionalCallArrayList, callSampleList);
        adapterSampleAdditionalCall = new AdapterSampleAdditionalCall(getActivity(), addSampleAdditionalCallArrayList);
        RecyclerView.LayoutManager mLayoutManagerprd = new LinearLayoutManager(getActivity());
        callDetailsSideBinding.rvAddSampleAdditional.setLayoutManager(mLayoutManagerprd);
        callDetailsSideBinding.rvAddSampleAdditional.setAdapter(adapterSampleAdditionalCall);
    }
}
