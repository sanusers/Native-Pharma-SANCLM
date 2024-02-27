package saneforce.santrip.activity.homeScreen.call.fragments;

import android.os.Bundle;
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
import java.util.Collections;

import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.call.adapter.detailing.DetailedFinalCallAdapter;
import saneforce.santrip.activity.homeScreen.call.pojo.detailing.CallDetailingList;
import saneforce.santrip.commonClasses.CommonUtilsMethods;

public class DetailedFragment extends Fragment {
    public static ArrayList<CallDetailingList> callDetailingLists;
    RecyclerView rv_detailing_list;
    DetailedFinalCallAdapter detailedFinalCallAdapter;
    CommonUtilsMethods commonUtilsMethods;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detailied, container, false);
        Log.v("fragment", "detailed");
        rv_detailing_list = v.findViewById(R.id.rv_detailing_list);
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        dummyAdapter();
        return v;
    }

    private void dummyAdapter() {
        detailedFinalCallAdapter = new DetailedFinalCallAdapter(getActivity(), getContext(), callDetailingLists);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rv_detailing_list.setLayoutManager(mLayoutManager);
        rv_detailing_list.setItemAnimator(new DefaultItemAnimator());
        rv_detailing_list.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));
        rv_detailing_list.setAdapter(detailedFinalCallAdapter);
        try {
            Collections.sort(callDetailingLists);
        } catch (Exception ignored) {

        }
    }
}