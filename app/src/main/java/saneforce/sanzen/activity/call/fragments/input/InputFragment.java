package saneforce.sanzen.activity.call.fragments.input;

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

import saneforce.sanzen.activity.call.adapter.input.CheckInputListAdapter;
import saneforce.sanzen.activity.call.adapter.input.FinalInputCallAdapter;
import saneforce.sanzen.activity.call.DCRCallActivity;
import saneforce.sanzen.activity.call.pojo.CallCommonCheckedList;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.WrapContentLinearLayoutManager;
import saneforce.sanzen.databinding.FragmentInputBinding;
import saneforce.sanzen.storage.SharedPref;

public class InputFragment extends Fragment {
    @SuppressLint("StaticFieldLeak")
    public static FragmentInputBinding fragmentInputBinding;
    public static ArrayList<CallCommonCheckedList> checkedInputList = new ArrayList<>();
    CheckInputListAdapter checkInputListAdapter;
    FinalInputCallAdapter finalInputCallAdapter;
    CommonUtilsMethods commonUtilsMethods;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentInputBinding = FragmentInputBinding.inflate(inflater);
        View v = fragmentInputBinding.getRoot();
        dummyAdapter();
        changeCaption();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
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
        checkInputListAdapter = new CheckInputListAdapter(getActivity(), getContext(), checkedInputList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        fragmentInputBinding.rvCheckDataList.setLayoutManager(mLayoutManager);
        fragmentInputBinding.rvCheckDataList.setItemAnimator(new DefaultItemAnimator());
        fragmentInputBinding.rvCheckDataList.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));
        fragmentInputBinding.rvCheckDataList.setAdapter(checkInputListAdapter);

        finalInputCallAdapter = new FinalInputCallAdapter(getActivity(), getContext(), CheckInputListAdapter.saveCallInputListArrayList, checkedInputList);
        fragmentInputBinding.rvListInput.setLayoutManager(new WrapContentLinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        fragmentInputBinding.rvListInput.setItemAnimator(new DefaultItemAnimator());
        fragmentInputBinding.rvListInput.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));
        fragmentInputBinding.rvListInput.setAdapter(finalInputCallAdapter);
    }

    private void filter(String text) {
        ArrayList<CallCommonCheckedList> filteredNames = new ArrayList<>();
        for (CallCommonCheckedList s : checkedInputList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase())) {
                filteredNames.add(s);
            }
        }
        checkInputListAdapter.filterList(filteredNames);
    }
    private void changeCaption(){
        switch (DCRCallActivity.CallActivityCustDetails.get(0).getType()) {
            case "1":
                if (SharedPref.getDocInputCaption(requireContext()).isEmpty() || SharedPref.getDocInputCaption(requireContext()).equalsIgnoreCase(null)){
                    fragmentInputBinding.tagInputName.setText("Input Name");
                }else{
                    fragmentInputBinding.tagInputName.setText(SharedPref.getDocInputCaption(requireContext()));
                }
                break;
            case "2":
                if (SharedPref.getChmInputCaption(requireContext()).isEmpty() || SharedPref.getChmInputCaption(requireContext()).equalsIgnoreCase(null)){
                    fragmentInputBinding.tagInputName.setText("Input Name");
                }else{
                    fragmentInputBinding.tagInputName.setText(SharedPref.getChmInputCaption(requireContext()));
                }
                break;
            case "3":
                if (SharedPref.getStkInputCaption(requireContext()).isEmpty() || SharedPref.getStkInputCaption(requireContext()).equalsIgnoreCase(null)){
                    fragmentInputBinding.tagInputName.setText("Input Name");
                }else{
                    fragmentInputBinding.tagInputName.setText(SharedPref.getStkInputCaption(requireContext()));
                }
                break;
            case "4":
                if (SharedPref.getUlInputCaption(requireContext()).isEmpty() || SharedPref.getUlInputCaption(requireContext()).equalsIgnoreCase(null)){
                    fragmentInputBinding.tagInputName.setText("Input Name");
                }else{
                    fragmentInputBinding.tagInputName.setText(SharedPref.getUlInputCaption(requireContext()));
                }
                break;

        }
    }
}