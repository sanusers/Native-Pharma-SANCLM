package saneforce.santrip.activity.homeScreen.call.fragments;

import static saneforce.santrip.activity.homeScreen.call.DCRCallActivity.PrdSamNeed;

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

import saneforce.santrip.activity.homeScreen.call.adapter.additionalCalls.AdditionalCusListAdapter;
import saneforce.santrip.activity.homeScreen.call.adapter.additionalCalls.finalSavedAdapter.FinalAdditionalCallAdapter;
import saneforce.santrip.activity.homeScreen.call.pojo.CallCommonCheckedList;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.WrapContentLinearLayoutManager;
import saneforce.santrip.databinding.FragmentAdditionalCallBinding;

public class AdditionalCallFragment extends Fragment {

    public static ArrayList<CallCommonCheckedList> custListArrayList;
    @SuppressLint("StaticFieldLeak")
    public static FragmentAdditionalCallBinding additionalCallBinding;
    AdditionalCusListAdapter additionalCusListAdapter;
    FinalAdditionalCallAdapter finalAdditionalCallAdapter;
    CommonUtilsMethods commonUtilsMethods;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        additionalCallBinding = FragmentAdditionalCallBinding.inflate(getLayoutInflater());
        View v = additionalCallBinding.getRoot();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        dummyAdapter();

        if (PrdSamNeed.equalsIgnoreCase("1")) {
            additionalCallBinding.tagSamQty.setVisibility(View.VISIBLE);
        }

        additionalCallBinding.searchAddCall.addTextChangedListener(new TextWatcher() {
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

    private void dummyAdapter() {

        additionalCusListAdapter = new AdditionalCusListAdapter(getActivity(), getContext(), custListArrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        additionalCallBinding.rvCheckDataList.setLayoutManager(mLayoutManager);
        additionalCallBinding.rvCheckDataList.setItemAnimator(new DefaultItemAnimator());
        additionalCallBinding.rvCheckDataList.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));
        additionalCallBinding.rvCheckDataList.setAdapter(additionalCusListAdapter);

        finalAdditionalCallAdapter = new FinalAdditionalCallAdapter(getActivity(), getContext(), AdditionalCusListAdapter.saveAdditionalCallArrayList, custListArrayList);
        additionalCallBinding.rvListAdditional.setLayoutManager(new WrapContentLinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        additionalCallBinding.rvListAdditional.setItemAnimator(new DefaultItemAnimator());
        additionalCallBinding.rvListAdditional.setAdapter(finalAdditionalCallAdapter);
    }

    private void filter(String text) {
        ArrayList<CallCommonCheckedList> filteredNames = new ArrayList<>();
        for (CallCommonCheckedList s : custListArrayList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredNames.add(s);
            }
        }
        additionalCusListAdapter.filterList(filteredNames);
    }
}
