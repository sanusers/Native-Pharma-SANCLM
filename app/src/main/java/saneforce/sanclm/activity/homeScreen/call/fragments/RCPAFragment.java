package saneforce.sanclm.activity.homeScreen.call.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.DCRCallActivity;
import saneforce.sanclm.activity.homeScreen.call.adapter.rcpa.RCPAAddCompAdapter;
import saneforce.sanclm.activity.homeScreen.call.adapter.rcpa.RCPAChemistAdapter;


public class RCPAFragment extends Fragment {
    public static RecyclerView rv_add_rcpa_list;
    Button btn_add_rcpa;
    RCPAChemistAdapter rcpaChemistAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_rcpa, container, false);
        Log.v("fragment", "rcpa");
        btn_add_rcpa = v.findViewById(R.id.btn_add_rcpa);
        rv_add_rcpa_list = v.findViewById(R.id.rv_add_rcpa_call_list);

        rcpaChemistAdapter = new RCPAChemistAdapter(getContext(), RCPAFragmentSide.chemistNames, RCPAFragmentSide.rcpaAddedProdListArrayList, RCPAFragmentSide.rcpa_Added_list);
        RecyclerView.LayoutManager mLayoutManagerMain = new LinearLayoutManager(getActivity());
        rv_add_rcpa_list.setLayoutManager(mLayoutManagerMain);
        rv_add_rcpa_list.setAdapter(rcpaChemistAdapter);

        btn_add_rcpa.setOnClickListener(view -> {

            RCPAFragmentSide.RCPAAddCompSideViewArrayList.clear();
            RCPAAddCompAdapter rcpaAddCompAdapter = new RCPAAddCompAdapter(getActivity(), RCPAFragmentSide.RCPAAddCompSideViewArrayList);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            RCPAFragmentSide.rv_comp_list.setLayoutManager(mLayoutManager);
            RCPAFragmentSide.rv_comp_list.setAdapter(rcpaAddCompAdapter);
            DCRCallActivity.fragment_add_rcpa_side.setVisibility(View.VISIBLE);
        });

        return v;
    }
}