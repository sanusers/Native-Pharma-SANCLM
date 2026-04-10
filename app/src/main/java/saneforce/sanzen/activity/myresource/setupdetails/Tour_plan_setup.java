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

public class Tour_plan_setup extends Fragment {
    RecyclerView recyclerView;
    setupDetailsAdapter adapter;
    ArrayList<callstatus_model> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.activity_tourplan_setup, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        loadFromSharedPreferences();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new setupDetailsAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);
        return view;
    }
    private void loadFromSharedPreferences() {
        list.clear();
        String tourPlan = SharedPref.getTpNeed(requireContext());
        callstatus_model model = new callstatus_model();
        //  model.setCustName(SharedPref.GEOTAG_NEED);
        model.setCustName("Tour Plan");
        model.setChkflk("0");
        model.setSpKey(SharedPref.TP_NEED);
        model.setPromoted(tourPlan.equals("0"));
        list.add(model);


        String tpBased = SharedPref.getDayplanTpBased(requireContext());
        callstatus_model model1 = new callstatus_model();
        //  model.setCustName(SharedPref.GEOTAG_NEED);
        model1.setCustName("Day Plan TP Based");
        model1.setChkflk("0");
        model1.setSpKey(SharedPref.DAYPLAN_TP_BASED);
        model1.setPromoted(tpBased.equals("0"));
        list.add(model1);

        String tpBasedMandatory = SharedPref.getTpMandatoryNeed(requireContext());
        callstatus_model model2 = new callstatus_model();
        //  model.setCustName(SharedPref.GEOTAG_NEED);
        model2.setCustName("TP Mandatory Need");
        model2.setChkflk("0");
        model2.setSpKey(SharedPref.TP_MANDATORY_NEED);
        model2.setPromoted(tpBasedMandatory.equals("0"));
        list.add(model2);

//        String tpBasedDcr = SharedPref.getTpbasedDcr(requireContext());
//        callstatus_model model3 = new callstatus_model();
//        //  model.setCustName(SharedPref.GEOTAG_NEED);
//        model3.setCustName("TP Based DCR");
//        model3.setChkflk("0");
//        model3.setSpKey(SharedPref.TPBASED_DCR);
//        model3.setPromoted(tpBasedDcr.equals("0"));
//        list.add(model3);

        String tpDeviation = SharedPref.getTpdcrDeviation(requireContext());
        callstatus_model model3 = new callstatus_model();
        //  model.setCustName(SharedPref.GEOTAG_NEED);
        model3.setCustName("TP DCR Deviation");
        model3.setChkflk("0");
        model3.setSpKey(SharedPref.TPDCR_DEVIATION);
        model3.setPromoted(tpDeviation.equals("0"));
        list.add(model3);


        callstatus_model model4 = new callstatus_model();
        model4.setCustName("TP Start & End  Date");
        model4.setChkflk("2");
        model4.setSpKey(SharedPref.TP_START_DATE);
        model4.setTown_code(SharedPref.getTpStartDate(requireContext())); // From date
        model4.setTown_name(SharedPref.getTpEndDate(requireContext()));   // To date
        list.add(model4);

        String jwSelectionNeed = SharedPref.getJwAutoSelectionNeed(requireContext());
        callstatus_model model5 = new callstatus_model();
        model5.setCustName("TP JointWork Selection");
        model5.setChkflk("0");
        model5.setSpKey(SharedPref.JW_AUTO_SELECTION_NEED);
        model5.setPromoted(jwSelectionNeed.equals("1"));
        list.add(model5);

        String holidayEdit = SharedPref.getEditHoliday(requireContext());
        callstatus_model model8 = new callstatus_model();
        model8.setCustName("Holiday Edit");
        model8.setChkflk("0");
        model8.setSpKey(SharedPref.EDIT_HOLIDAY);
        model8.setPromoted(holidayEdit.equals("0"));
        list.add(model8);





    }
}