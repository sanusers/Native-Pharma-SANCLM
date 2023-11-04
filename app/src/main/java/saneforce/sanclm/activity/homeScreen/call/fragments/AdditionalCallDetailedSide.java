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

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.adapter.additionalCalls.finalSavedAdapter.SaveAdditionalCallAdapter;
import saneforce.sanclm.activity.homeScreen.call.adapter.additionalCalls.sideView.AdapterInputAdditionalCall;
import saneforce.sanclm.activity.homeScreen.call.adapter.additionalCalls.sideView.AdapterSampleAdditionalCall;
import saneforce.sanclm.activity.homeScreen.call.pojo.additionalCalls.AddInputAdditionalCall;
import saneforce.sanclm.activity.homeScreen.call.pojo.additionalCalls.AddSampleAdditionalCall;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;

public class AdditionalCallDetailedSide extends Fragment {
    public static RecyclerView rv_add_input_list, rv_add_sample_list;
    public static ArrayList<AddInputAdditionalCall> addInputAdditionalCallArrayList;
    public static ArrayList<AddSampleAdditionalCall> addSampleAdditionalCallArrayList;
    @SuppressLint("StaticFieldLeak")
    public static AdapterInputAdditionalCall adapterInputAdditionalCall;
    @SuppressLint("StaticFieldLeak")
    public static AdapterSampleAdditionalCall adapterSampleAdditionalCall;
    TabLayout tabLayout;
    ConstraintLayout constraint_input, constraint_sample;
    TextView btn_add_input, btn_add_sample;
    Button btn_save_data;
    CommonUtilsMethods commonUtilsMethods;
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
        // commonUtilsMethods.FullScreencall();
        tabLayout.addTab(tabLayout.newTab().setText("Input"));
        tabLayout.addTab(tabLayout.newTab().setText("Sample"));

        DummyAdapter();

        tv_dummy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                HideKeyboard();
                if (tab.getPosition() == 0) {
                    constraint_input.setVisibility(View.VISIBLE);
                    constraint_sample.setVisibility(View.INVISIBLE);
                } else if (tab.getPosition() == 1) {
                    constraint_input.setVisibility(View.INVISIBLE);
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

        img_close.setOnClickListener(view -> dcrcallBinding.fragmentAddCallDetailsSide.setVisibility(View.GONE));

        btn_add_input.setOnClickListener(view -> {
            HideKeyboard();
            AdditionalCallDetailedSide.addInputAdditionalCallArrayList.add(new AddInputAdditionalCall(SaveAdditionalCallAdapter.Selected_name, SaveAdditionalCallAdapter.Selected_code, "Select", "0", ""));
            commonUtilsMethods.recycleTestWithDivider(rv_add_input_list);
            rv_add_input_list.setAdapter(adapterInputAdditionalCall);
            adapterInputAdditionalCall.notifyDataSetChanged();
        });

        btn_add_sample.setOnClickListener(view -> {
            HideKeyboard();
            AdditionalCallDetailedSide.addSampleAdditionalCallArrayList.add(new AddSampleAdditionalCall(SaveAdditionalCallAdapter.Selected_name, SaveAdditionalCallAdapter.Selected_code, "Select", "0", ""));
            commonUtilsMethods.recycleTestWithDivider(rv_add_sample_list);
            rv_add_sample_list.setAdapter(adapterSampleAdditionalCall);
            adapterSampleAdditionalCall.notifyDataSetChanged();
        });

        btn_save_data.setOnClickListener(view -> {
            HideKeyboard();
            if (SaveAdditionalCallAdapter.New_Edit.equalsIgnoreCase("New")) {
                AddSampleInputDatas();

            } else if (SaveAdditionalCallAdapter.New_Edit.equalsIgnoreCase("Edit")) {
                for (int j = 0; j < SaveAdditionalCallAdapter.nestedAddInputCallDetails.size(); j++) {
                    Log.v("sizeee", "---" + SaveAdditionalCallAdapter.nestedAddInputCallDetails.get(j).getCust_code() + "---" + SaveAdditionalCallAdapter.Selected_code);
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
            commonUtilsMethods.recycleTestWithDivider(AdditionalCallFragment.rv_add_call_list);
            commonUtilsMethods.recycleTestWithoutDivider(SaveAdditionalCallAdapter.rv_nested_calls_input_data);
            commonUtilsMethods.recycleTestWithoutDivider(SaveAdditionalCallAdapter.rv_nested_calls_sample_data);
            AdditionalCallFragment.rv_add_call_list.setAdapter(saveAdditionalCallAdapter);
            dcrcallBinding.fragmentAddCallDetailsSide.setVisibility(View.GONE);
        });
        return v;
    }

    private void AddSampleInputDatas() {
        if (addInputAdditionalCallArrayList.size() > 0) {
            for (int i = 0; i < addInputAdditionalCallArrayList.size(); i++) {
                SaveAdditionalCallAdapter.nestedAddInputCallDetails.add(new AddInputAdditionalCall(addInputAdditionalCallArrayList.get(i).getCust_name(), addInputAdditionalCallArrayList.get(i).getCust_code(), addInputAdditionalCallArrayList.get(i).getInput_name(), addInputAdditionalCallArrayList.get(i).getInp_qty()));
            }
        }

        if (addSampleAdditionalCallArrayList.size() > 0) {
            for (int i = 0; i < AdditionalCallDetailedSide.addSampleAdditionalCallArrayList.size(); i++) {
                SaveAdditionalCallAdapter.nestedAddSampleCallDetails.add(new AddSampleAdditionalCall(addSampleAdditionalCallArrayList.get(i).getCust_name(), addSampleAdditionalCallArrayList.get(i).getCust_code(), addSampleAdditionalCallArrayList.get(i).getPrd_name(), addSampleAdditionalCallArrayList.get(i).getPrd_stock(), addSampleAdditionalCallArrayList.get(i).getSample_qty()));
            }
        }
    }

    private void HideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(tabLayout.getWindowToken(), 0);
    }

    private void DummyAdapter() {
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
