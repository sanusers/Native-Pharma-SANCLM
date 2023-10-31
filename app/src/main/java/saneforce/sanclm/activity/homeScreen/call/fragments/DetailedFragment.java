package saneforce.sanclm.activity.homeScreen.call.fragments;

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

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.adapter.DetailedCallAdapter;
import saneforce.sanclm.activity.homeScreen.call.pojo.detailing.CallDetailingList;

public class DetailedFragment extends Fragment {
    RecyclerView rv_detailing_list;
    DetailedCallAdapter detailedCallAdapter;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_detailied, container, false);
        Log.v("fragment","detailed");
        rv_detailing_list = v.findViewById(R.id.rv_detailing_list);
        dummyAdapter();
        return v;
    }

    private void dummyAdapter() {
        ArrayList<CallDetailingList> callDetailingLists = new ArrayList<>();
        CallDetailingList callDetailingList = new CallDetailingList("Paracetemol 750mg", "4","10:20:35 - 10:21:20");
        CallDetailingList callDetailingList2 = new CallDetailingList("Avastin 800", "2","10:21:35 - 10:22:56");
        callDetailingLists.add(callDetailingList);
        callDetailingLists.add(callDetailingList2);

        detailedCallAdapter = new DetailedCallAdapter(getActivity(),getContext(), callDetailingLists);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rv_detailing_list.setLayoutManager(mLayoutManager);
        rv_detailing_list.setItemAnimator(new DefaultItemAnimator());
        rv_detailing_list.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        rv_detailing_list.setAdapter(detailedCallAdapter);
    }
}