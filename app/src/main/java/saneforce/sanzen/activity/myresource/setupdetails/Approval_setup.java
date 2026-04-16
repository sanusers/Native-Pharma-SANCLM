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

public class Approval_setup extends Fragment {
    RecyclerView recyclerView;
    setupDetailsAdapter adapter;
    ArrayList<callstatus_model> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.activity_dcr_setup, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadFromSharedPreferences();

        adapter = new setupDetailsAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void loadFromSharedPreferences() {
        list.clear();
        String DCR = SharedPref.getDcrApprovalNeed(requireContext());
        callstatus_model dcrApproval = new callstatus_model();
        dcrApproval.setCustName("DCR Approval");
        dcrApproval.setChkflk("0");
        dcrApproval.setSpKey(SharedPref.DCR_APPROVAL_NEED);
        dcrApproval.setPromoted(DCR.equals("0"));
        list.add(dcrApproval);

        String geotag = SharedPref.getGeotagApprovalNeed(requireContext());
        callstatus_model geoTagApproval = new callstatus_model();
        geoTagApproval.setCustName("GeoTag Approval");
        geoTagApproval.setChkflk("0");
        geoTagApproval.setSpKey(SharedPref.GEOTAG_APPROVAL_NEED);
        geoTagApproval.setPromoted(geotag.equals("0"));
        list.add(geoTagApproval);

        String tourPlan  = SharedPref.getTpdcrMgrappr(requireContext());
        callstatus_model tpApproval = new callstatus_model();
        tpApproval.setCustName("TPDCR Deviation Approval");
        tpApproval.setChkflk("0");
        tpApproval.setSpKey(SharedPref.TPDCR_MGRAPPR);
        tpApproval.setPromoted(tourPlan.equals("0"));
        list.add(tpApproval);

        String stp  = SharedPref.getStpApprNeed(requireContext());
        callstatus_model stpApproval = new callstatus_model();
        stpApproval.setCustName("STP Approval");
        stpApproval.setChkflk("0");
        stpApproval.setSpKey(SharedPref.STP_APPR_NEED);
        stpApproval.setPromoted(stp.equals("0"));
        list.add(stpApproval);

//        String stp  = SharedPref.getStpApprNeed(requireContext());
//        callstatus_model stpApproval = new callstatus_model();
//        stpApproval.setCustName("STP Approval");
//        stpApproval.setChkflk("0");
//        stpApproval.setSpKey(SharedPref.TPDCR_DEVIATION_APPR_STATUS);
//        stpApproval.setPromoted(stp.equals("1"));
//        list.add(stpApproval);

    }
}
