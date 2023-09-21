package saneforce.sanclm.Activities.Call.Fragments;

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

import saneforce.sanclm.CommonClasses.CommonSharedPreference;
import saneforce.sanclm.R;
import saneforce.sanclm.Activities.Call.Adapter.AdditionalCalls.CallAddCustListAdapter;
import saneforce.sanclm.Activities.Call.Adapter.AdditionalCalls.FinalSavedAdapter.SaveAdditionalCallAdapter;
import saneforce.sanclm.Activities.Call.Pojo.CallCommonCheckedList;

public class AdditionalCallFragment extends Fragment {
    public static RecyclerView rv_list_data, rv_add_call_list;
    public static ArrayList<CallCommonCheckedList> custListArrayList;

    CallAddCustListAdapter callAddCustListAdapter;
    EditText editTextSearch;
    SaveAdditionalCallAdapter saveAdditionalCallAdapter;
    CommonSharedPreference mCommonSharedPreference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_additional_call, container, false);
        rv_list_data = v.findViewById(R.id.rv_check_data_list);
        rv_add_call_list = v.findViewById(R.id.rv_list_additional);
        editTextSearch = v.findViewById(R.id.search_add_call);
        mCommonSharedPreference = new CommonSharedPreference(getActivity());
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

        callAddCustListAdapter = new CallAddCustListAdapter(getActivity(),getContext(), custListArrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rv_list_data.setLayoutManager(mLayoutManager);
        rv_list_data.setItemAnimator(new DefaultItemAnimator());
        rv_list_data.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        rv_list_data.setAdapter(callAddCustListAdapter);

        saveAdditionalCallAdapter = new SaveAdditionalCallAdapter(getActivity(),getContext(), CallAddCustListAdapter.saveAdditionalCallArrayList, custListArrayList);
        RecyclerView.LayoutManager mLayoutManagerinp = new LinearLayoutManager(getActivity());
        rv_add_call_list.setLayoutManager(mLayoutManagerinp);
        rv_add_call_list.setItemAnimator(new DefaultItemAnimator());
        rv_add_call_list.setAdapter(saveAdditionalCallAdapter);
    }

    private void filter(String text) {
        ArrayList<CallCommonCheckedList> filterdNames = new ArrayList<>();
        for (CallCommonCheckedList s : custListArrayList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase())) {
                filterdNames.add(s);
            }
        }
        callAddCustListAdapter.filterList(filterdNames);
    }
}
