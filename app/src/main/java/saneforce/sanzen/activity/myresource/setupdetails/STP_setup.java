package saneforce.sanzen.activity.myresource.setupdetails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.myresource.callstatusview.callstatus_model;
import saneforce.sanzen.storage.SharedPref;

public class STP_setup extends Fragment {
    RecyclerView recyclerView;
    setupDetailsAdapter adapter;
    ArrayList<callstatus_model> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_stp_setup, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        loadFromSharedPreferences();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new setupDetailsAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);
        return view;
    }
    private void loadFromSharedPreferences() {
        list.clear();
        String stpNeed = SharedPref.getStpNeed(requireContext());
        callstatus_model model = new callstatus_model();
        //  model.setCustName(SharedPref.GEOTAG_NEED);
        model.setCustName("STP Need");
        model.setChkflk("0");
        model.setSpKey(SharedPref.STP_NEED);
        model.setPromoted(stpNeed.equals("0"));
        list.add(model);

        String stpApprovalNeed = SharedPref.getStpApprNeed(requireContext());
        callstatus_model model1 = new callstatus_model();
        //  model.setCustName(SharedPref.GEOTAG_NEED);
        model1.setCustName("STP Approval Need");
        model1.setChkflk("0");
        model1.setSpKey(SharedPref.STP_APPR_NEED);
        model1.setPromoted(stpApprovalNeed.equals("0"));
        list.add(model1);

        String stpMtp = SharedPref.getStpApprNeed(requireContext());
        callstatus_model model2 = new callstatus_model();
        //  model.setCustName(SharedPref.GEOTAG_NEED);
        model2.setCustName("STP Based MTP");
        model2.setChkflk("0");
        model2.setSpKey(SharedPref.STP_BASED_MTP);
        model2.setPromoted(stpMtp.equals("0"));
        list.add(model2);

    }
}