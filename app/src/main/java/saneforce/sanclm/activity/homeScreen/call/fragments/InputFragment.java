package saneforce.sanclm.activity.homeScreen.call.fragments;

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

import saneforce.sanclm.activity.homeScreen.call.DCRCallActivity;
import saneforce.sanclm.activity.homeScreen.call.adapter.input.CallInputListAdapter;
import saneforce.sanclm.activity.homeScreen.call.adapter.input.SaveInputCallAdapter;
import saneforce.sanclm.activity.homeScreen.call.pojo.CallCommonCheckedList;
import saneforce.sanclm.databinding.FragmentInputBinding;

public class InputFragment extends Fragment {


    @SuppressLint("StaticFieldLeak")
    public static FragmentInputBinding fragmentInputBinding;
    public static ArrayList<CallCommonCheckedList> callCommonCheckedListArrayList = new ArrayList<>();
    CallInputListAdapter callInputListAdapter;
    SaveInputCallAdapter saveInputCallAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentInputBinding = FragmentInputBinding.inflate(inflater);
        View v = fragmentInputBinding.getRoot();
        dummyAdapter();

        if (DCRCallActivity.InputValidation.equalsIgnoreCase("1")) {
            fragmentInputBinding.tagStock.setVisibility(View.VISIBLE);
        } else {
            fragmentInputBinding.tagStock.setVisibility(View.GONE);
        }

        fragmentInputBinding.searchInput.addTextChangedListener(new TextWatcher() {
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
       /* callInputListArrayList.clear();
        callInputListArrayList.add(new CallInputList("Pen", false));
        callInputListArrayList.add(new CallInputList("Marker", false));
        callInputListArrayList.add(new CallInputList("Key Chain", false));
        callInputListArrayList.add(new CallInputList("Keyboard", false));
        callInputListArrayList.add(new CallInputList("Watch", false));
        callInputListArrayList.add(new CallInputList("Horlicks", false));
        callInputListArrayList.add(new CallInputList("Umberlla", false));
        callInputListArrayList.add(new CallInputList("Lunch Box", false));
        callInputListArrayList.add(new CallInputList("Ball", false));
        callInputListArrayList.add(new CallInputList("Jacket", false));
        callInputListArrayList.add(new CallInputList("Bat", false));
        callInputListArrayList.add(new CallInputList("Toys", false));
        callInputListArrayList.add(new CallInputList("Plastic Bar", false));*/

        callInputListAdapter = new CallInputListAdapter(getActivity(), getContext(), callCommonCheckedListArrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        fragmentInputBinding.rvCheckDataList.setLayoutManager(mLayoutManager);
        fragmentInputBinding.rvCheckDataList.setItemAnimator(new DefaultItemAnimator());
        fragmentInputBinding.rvCheckDataList.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        fragmentInputBinding.rvCheckDataList.setAdapter(callInputListAdapter);

        saveInputCallAdapter = new SaveInputCallAdapter(getActivity(), getContext(), CallInputListAdapter.saveCallInputListArrayList, callCommonCheckedListArrayList);
        RecyclerView.LayoutManager mLayoutManagerinp = new LinearLayoutManager(getActivity());
        fragmentInputBinding.rvListInput.setLayoutManager(mLayoutManagerinp);
        fragmentInputBinding.rvListInput.setItemAnimator(new DefaultItemAnimator());
        fragmentInputBinding.rvListInput.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        fragmentInputBinding.rvListInput.setAdapter(saveInputCallAdapter);
    }

    private void filter(String text) {
        ArrayList<CallCommonCheckedList> filterdNames = new ArrayList<>();
        for (CallCommonCheckedList s : callCommonCheckedListArrayList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase())) {
                filterdNames.add(s);
            }
        }
        callInputListAdapter.filterList(filterdNames);
    }
}