package saneforce.sanclm.activity.homeScreen.call.fragments;

import static saneforce.sanclm.activity.homeScreen.call.DCRCallActivity.dcrcallBinding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;


import java.util.ArrayList;

import saneforce.sanclm.commonClasses.CommonSharedPreference;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.adapter.additionalCalls.finalSavedAdapter.SaveAdditionalCallAdapter;
import saneforce.sanclm.activity.homeScreen.call.adapter.additionalCalls.sideView.AdapterInputAdditionalCall;
import saneforce.sanclm.activity.homeScreen.call.adapter.additionalCalls.sideView.AdapterSampleAdditionalCall;
import saneforce.sanclm.activity.homeScreen.call.DCRCallActivity;
import saneforce.sanclm.activity.homeScreen.call.pojo.additionalCalls.AddInputAdditionalCall;
import saneforce.sanclm.activity.homeScreen.call.pojo.additionalCalls.AddSampleAdditionalCall;
import saneforce.sanclm.activity.homeScreen.call.pojo.additionalCalls.NestedAddInputCallDetails;
import saneforce.sanclm.activity.homeScreen.call.pojo.additionalCalls.NestedAddSampleCallDetails;

public class AdditionalCallDetailedSide extends Fragment {
    public static RecyclerView rv_add_input_list, rv_add_sample_list;
    TabLayout tabLayout;
    ConstraintLayout constraint_input, constraint_sample;
    TextView btn_add_input, btn_add_sample;

    public static ArrayList<AddInputAdditionalCall> addInputAdditionalCallArrayList;
    public static ArrayList<AddSampleAdditionalCall> addSampleAdditionalCallArrayList;

    public static AdapterInputAdditionalCall adapterInputAdditionalCall;
    public static AdapterSampleAdditionalCall adapterSampleAdditionalCall;
    Button btn_save_data;
    CommonUtilsMethods commonUtilsMethods;
    CommonSharedPreference mCommonsharedPreference;
    SaveAdditionalCallAdapter saveAdditionalCallAdapter;
    TextView tv_dummy;
    ImageView img_close;

    @SuppressLint("NotifyDataSetChanged")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_call_details_side, container, false);
        tabLayout = v.findViewById(R.id.tab_layout);
        btn_add_input = v.findViewById(R.id.btn_add_input);
        btn_add_sample = v.findViewById(R.id.btn_add_sample);
        constraint_input = v.findViewById(R.id.constraint_main_input);
        constraint_sample = v.findViewById(R.id.constraint_main_sample);
        rv_add_input_list = v.findViewById(R.id.rv_add_inputs_additional);
        rv_add_sample_list = v.findViewById(R.id.rv_add_sample_additional);
        img_close = v.findViewById(R.id.img_close);
        btn_save_data = v.findViewById(R.id.btn_save);
        tv_dummy = v.findViewById(R.id.tv_dummy);
        commonUtilsMethods = new CommonUtilsMethods(getActivity());
        mCommonsharedPreference = new CommonSharedPreference(getActivity());
        commonUtilsMethods.FullScreencall();
        tabLayout.addTab(tabLayout.newTab().setText("Input"));
        tabLayout.addTab(tabLayout.newTab().setText("Sample"));

        DummyAdapter();

        tv_dummy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
           /*    if(mCommonsharedPreference.getValueFromPreference("tab_pos_dcr").equalsIgnoreCase("3") || mCommonsharedPreference.getValueFromPreference("tab_pos_dcr").equalsIgnoreCase("4"))
               {
                   HideKeyboard();
               }*/
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                HideKeyboard();
                if (tab.getPosition() == 0) {
                    constraint_input.setVisibility(View.VISIBLE);
                    constraint_sample.setVisibility(View.GONE);
                } else if (tab.getPosition() == 1) {
                    constraint_input.setVisibility(View.GONE);
                    constraint_sample.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dcrcallBinding.fragmentAddCallDetailsSide.setVisibility(View.GONE);

            }
        });

        btn_add_input.setOnClickListener(view -> {
            HideKeyboard();
            AdditionalCallDetailedSide.addInputAdditionalCallArrayList.add(new AddInputAdditionalCall(mCommonsharedPreference.getValueFromPreference("selected_add_call_name"), "Select", "0", ""));
            commonUtilsMethods.recycleTestWithDivider(rv_add_input_list);
            rv_add_input_list.setAdapter(adapterInputAdditionalCall);
            adapterInputAdditionalCall.notifyDataSetChanged();
        });

        btn_add_sample.setOnClickListener(view -> {
            HideKeyboard();
            AdditionalCallDetailedSide.addSampleAdditionalCallArrayList.add(new AddSampleAdditionalCall(mCommonsharedPreference.getValueFromPreference("selected_add_call_name"), "Select", "0", ""));
            commonUtilsMethods.recycleTestWithDivider(rv_add_sample_list);
            rv_add_sample_list.setAdapter(adapterSampleAdditionalCall);
            adapterSampleAdditionalCall.notifyDataSetChanged();
        });

        btn_save_data.setOnClickListener(view -> {
            HideKeyboard();
            if (addInputAdditionalCallArrayList.size() > 0) {

                Log.v("shjsah","-55--" + addInputAdditionalCallArrayList.size() + addInputAdditionalCallArrayList.get(0).getCust_name());

                //SaveAdditionalCallAdapter.nestedAddInputCallDetails = new ArrayList<>();
                //   SaveAdditionalCallAdapter.dummyNestedInput.add(new NestedAddInputCallDetails(AdditionalCallFragment.addInputAdditionalCallArrayList.get(0).getCust_name(),AdditionalCallFragment.addInputAdditionalCallArrayList.get(0).getInput_name(), AdditionalCallFragment.addInputAdditionalCallArrayList.get(0).getInp_qty()));
                for (int i = 0; i < addInputAdditionalCallArrayList.size(); i++) {
                    SaveAdditionalCallAdapter.nestedAddInputCallDetails.add(new NestedAddInputCallDetails(addInputAdditionalCallArrayList.get(i).getCust_name(),addInputAdditionalCallArrayList.get(i).getInput_name(), addInputAdditionalCallArrayList.get(i).getInp_qty()));
                }
            }

            if (addSampleAdditionalCallArrayList.size() > 0) {
                //  SaveAdditionalCallAdapter.nestedAddSampleCallDetails = new ArrayList<>();
                //  SaveAdditionalCallAdapter.dummyNestedSample.add(new NestedAddSampleCallDetails(AdditionalCallFragment.addSampleAdditionalCallArrayList.get(0).getCust_name(),AdditionalCallFragment.addSampleAdditionalCallArrayList.get(0).getPrd_name(), AdditionalCallFragment.addSampleAdditionalCallArrayList.get(0).getSample_qty(), AdditionalCallFragment.addSampleAdditionalCallArrayList.get(0).getRx_qty()));
                for (int i = 0; i < AdditionalCallDetailedSide.addSampleAdditionalCallArrayList.size(); i++) {
                    SaveAdditionalCallAdapter.nestedAddSampleCallDetails.add(new NestedAddSampleCallDetails(addSampleAdditionalCallArrayList.get(i).getCust_name(), addSampleAdditionalCallArrayList.get(i).getPrd_name(), addSampleAdditionalCallArrayList.get(i).getSample_qty(),addSampleAdditionalCallArrayList.get(i).getRx_qty()));
                }
            }

            saveAdditionalCallAdapter = new SaveAdditionalCallAdapter(getActivity(), getContext(), SaveAdditionalCallAdapter.checked_arrayList, SaveAdditionalCallAdapter.saveAdditionalCalls, SaveAdditionalCallAdapter.nestedAddInputCallDetails, SaveAdditionalCallAdapter.nestedAddSampleCallDetails, SaveAdditionalCallAdapter.dummyNestedInput, SaveAdditionalCallAdapter.dummyNestedSample);
            commonUtilsMethods.recycleTestWithDivider(AdditionalCallFragment.rv_add_call_list);
             commonUtilsMethods.recycleTestWithoutDivider(SaveAdditionalCallAdapter.rv_nested_calls_input_data);
             commonUtilsMethods.recycleTestWithoutDivider(SaveAdditionalCallAdapter.rv_nested_calls_sample_data);
            AdditionalCallFragment.rv_add_call_list.setAdapter(saveAdditionalCallAdapter);
            dcrcallBinding.fragmentAddCallDetailsSide.setVisibility(View.GONE);
        });
        return v;
    }

    private void HideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(tabLayout.getWindowToken(), 0);
    }

    private void DummyAdapter() {
        Log.v("sad",addInputAdditionalCallArrayList.size() + "---" + addSampleAdditionalCallArrayList.size());
        adapterInputAdditionalCall = new AdapterInputAdditionalCall(getActivity(), addInputAdditionalCallArrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rv_add_input_list.setLayoutManager(mLayoutManager);
        rv_add_input_list.setAdapter(adapterInputAdditionalCall);

        adapterSampleAdditionalCall = new AdapterSampleAdditionalCall(getActivity(), addSampleAdditionalCallArrayList);
        RecyclerView.LayoutManager mLayoutManagerprd = new LinearLayoutManager(getActivity());
        rv_add_sample_list.setLayoutManager(mLayoutManagerprd);
        rv_add_sample_list.setAdapter(adapterSampleAdditionalCall);
    }
}
