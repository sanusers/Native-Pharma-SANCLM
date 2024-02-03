package saneforce.santrip.activity.homeScreen.call.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.call.adapter.additionalCalls.AdditionalCusListAdapter;
import saneforce.santrip.activity.homeScreen.call.adapter.additionalCalls.finalSavedAdapter.FinalAdditionalCallAdapter;
import saneforce.santrip.activity.homeScreen.call.pojo.CallCommonCheckedList;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.WrapContentLinearLayoutManager;

public class AdditionalCallFragment extends Fragment {
    public static RecyclerView rv_list_data, rv_add_call_list;
    public static ArrayList<CallCommonCheckedList> custListArrayList;
    AdditionalCusListAdapter additionalCusListAdapter;
    EditText editTextSearch;
    FinalAdditionalCallAdapter finalAdditionalCallAdapter;
    CommonUtilsMethods commonUtilsMethods;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_additional_call, container, false);
        rv_list_data = v.findViewById(R.id.rv_check_data_list);
        rv_add_call_list = v.findViewById(R.id.rv_list_additional);
        editTextSearch = v.findViewById(R.id.search_add_call);
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        dummyAdapter();

        editTextSearch.addTextChangedListener(new TextWatcher() {
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
        rv_list_data.setLayoutManager(mLayoutManager);
        rv_list_data.setItemAnimator(new DefaultItemAnimator());
        rv_list_data.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));
        rv_list_data.setAdapter(additionalCusListAdapter);

        finalAdditionalCallAdapter = new FinalAdditionalCallAdapter(getActivity(), getContext(), AdditionalCusListAdapter.saveAdditionalCallArrayList, custListArrayList);
        rv_add_call_list.setLayoutManager(new WrapContentLinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false));
        rv_add_call_list.setItemAnimator(new DefaultItemAnimator());
        rv_add_call_list.setAdapter(finalAdditionalCallAdapter);
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
