package saneforce.sanzen.activity.call.fragments.detailing;

import android.content.Context;
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

import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.fragments.BadgeUpdateListener;
import saneforce.sanzen.activity.call.fragments.product.ProductFragment;
import saneforce.sanzen.activity.call.pojo.CallCommonCheckedList;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.activity.call.pojo.detailing.CallDetailingList;
import saneforce.sanzen.activity.call.adapter.detailing.DetailedFinalCallAdapter;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;

public class DetailedFragment extends Fragment {
    public static ArrayList<CallDetailingList> callDetailingLists = new ArrayList<>();
    RecyclerView rv_detailing_list;
    DetailedFinalCallAdapter detailedFinalCallAdapter;
    CommonUtilsMethods commonUtilsMethods;
    private BadgeUpdateListener badgeListener;
    private static final String ARG_POSITION = "position";
    private int position;

    public static DetailedFragment newInstance(int position) {
        DetailedFragment fragment = new DetailedFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_POSITION, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        badgeListener = (BadgeUpdateListener) context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_detailied, container, false);
        Log.v("fragment", "detailed");
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION, 0);
        }
        rv_detailing_list = view.findViewById(R.id.rv_detailing_list);
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        dummyAdapter();
        countDetailed();
        return view;
    }

    private void countDetailed() {
        if (badgeListener != null) {
            badgeListener.onBadgeCountChanged(position, callDetailingLists.size());
        }
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