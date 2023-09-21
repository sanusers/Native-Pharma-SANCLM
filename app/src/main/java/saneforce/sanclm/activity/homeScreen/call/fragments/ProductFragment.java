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
import saneforce.sanclm.activity.homeScreen.call.adapter.product.CallProductListAdapter;
import saneforce.sanclm.activity.homeScreen.call.adapter.product.SaveProductCallAdapter;
import saneforce.sanclm.activity.homeScreen.call.pojo.CallCommonCheckedList;

public class ProductFragment extends Fragment {
    public static RecyclerView rv_list_data;
    public static RecyclerView rv_list_prod;
    CallProductListAdapter callProductListAdapter;
    SaveProductCallAdapter saveProductCallAdapter;
    EditText editTextSearch;
   public static ArrayList<CallCommonCheckedList> callCommonCheckedListArrayList;
    CommonSharedPreference mcommonSharedPreference;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_products, container, false);
        Log.v("fragment","products");
        rv_list_data = v.findViewById(R.id.rv_check_data_list);
        rv_list_prod = v.findViewById(R.id.rv_list_prd);
        editTextSearch = v.findViewById(R.id.search_prd);

        dummyAdapter();
        mcommonSharedPreference = new CommonSharedPreference(getContext());
        mcommonSharedPreference.setValueToPreference("unselect_data_prd", "");

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
        callProductListAdapter = new CallProductListAdapter(getActivity(),getContext(), callCommonCheckedListArrayList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rv_list_data.setLayoutManager(mLayoutManager);
        rv_list_data.setItemAnimator(new DefaultItemAnimator());
        rv_list_data.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        rv_list_data.setAdapter(callProductListAdapter);

        saveProductCallAdapter = new SaveProductCallAdapter(getActivity(),getContext(), CallProductListAdapter.saveCallProductListArrayList, callCommonCheckedListArrayList);
        RecyclerView.LayoutManager mLayoutManagerprd = new LinearLayoutManager(getActivity());
        rv_list_prod.setLayoutManager(mLayoutManagerprd);
        rv_list_prod.setItemAnimator(new DefaultItemAnimator());
        rv_list_prod.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        rv_list_prod.setAdapter(saveProductCallAdapter);
    }

    private void filter(String text) {
        ArrayList<CallCommonCheckedList> filterdNames = new ArrayList<>();
        for (CallCommonCheckedList s : callCommonCheckedListArrayList) {
            if (s.getName().toLowerCase().contains(text.toLowerCase())) {
                filterdNames.add(s);
            }
        }
        callProductListAdapter.filterList(filterdNames);
    }
}
