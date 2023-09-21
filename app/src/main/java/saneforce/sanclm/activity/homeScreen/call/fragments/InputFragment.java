package saneforce.sanclm.activity.homeScreen.call.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
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

import saneforce.sanclm.commonClasses.CommonSharedPreference;
import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.adapter.input.CallInputListAdapter;
import saneforce.sanclm.activity.homeScreen.call.adapter.input.SaveInputCallAdapter;
import saneforce.sanclm.activity.homeScreen.call.pojo.CallCommonCheckedList;

public class InputFragment extends Fragment {
    public static RecyclerView rv_list_data,rv_list_input;
    CallInputListAdapter callInputListAdapter;

    SaveInputCallAdapter saveInputCallAdapter;
    EditText editTextSearch;
    public static ArrayList<CallCommonCheckedList> callCommonCheckedListArrayList = new ArrayList<>();
    //ArrayList<SaveCallInputList> inputListArrayList = new ArrayList<>();
    CommonSharedPreference mCommonSharedPreference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_input, container, false);
        Log.v("fragment","inputs");
        rv_list_data = v.findViewById(R.id.rv_check_data_list);
        rv_list_input = v.findViewById(R.id.rv_list_input);
        editTextSearch = v.findViewById(R.id.search_input);
        mCommonSharedPreference = new CommonSharedPreference(getActivity());
        mCommonSharedPreference.setValueToPreference("checked_input", true);
        mCommonSharedPreference.setValueToPreference("unselect_data_inp", "");
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

        callInputListAdapter = new CallInputListAdapter(getActivity(),getContext(), callCommonCheckedListArrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rv_list_data.setLayoutManager(mLayoutManager);
        rv_list_data.setItemAnimator(new DefaultItemAnimator());
        rv_list_data.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        rv_list_data.setAdapter(callInputListAdapter);

        saveInputCallAdapter = new SaveInputCallAdapter(getActivity(),getContext(), CallInputListAdapter.saveCallInputListArrayList, callCommonCheckedListArrayList);
        RecyclerView.LayoutManager mLayoutManagerinp = new LinearLayoutManager(getActivity());
        rv_list_input.setLayoutManager(mLayoutManagerinp);
        rv_list_input.setItemAnimator(new DefaultItemAnimator());
        rv_list_input.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        rv_list_input.setAdapter(saveInputCallAdapter);
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