package saneforce.sanzen.activity.myresource.setupdetails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.myresource.callstatusview.callstatus_model;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class Tour_plan_setup extends Fragment {
    RecyclerView recyclerView;
    setupDetailsAdapter adapter;
    ArrayList<callstatus_model> list = new ArrayList<>();
    MasterDataDao masterDataDao;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        masterDataDao = RoomDB.getDatabase(requireContext()).masterDataDao();
        View view = inflater.inflate(R.layout.activity_tourplan_setup, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        loadFromSharedPreferences();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new setupDetailsAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);
        return view;
    }

    private void loadFromSharedPreferences() {
        list.clear();
        try {

            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.TP_SETUP).getMasterSyncDataJsonArray();

            if (jsonArray != null && jsonArray.length() > 0) {
                JSONObject jsonObject = jsonArray.optJSONObject(0);

                addSetupItem("TP Mandatory", "TP_MANDATORY_NEED", SharedPref.getTpMandatoryNeed(requireContext()),"0");
                addSetupItem("TP Based DCR", "TPBASED_DCR", SharedPref.getTpbasedDcr(requireContext()),"0");
                addSetupItem("TPBased DCR Deviation", "TPDCR_DEVIATION", SharedPref.getTpdcrDeviation(requireContext()),"0");

                // Sharedpref setup
                addSetupItem(SharedPref.getDrCap(requireContext()) + " Need", SharedPref.DR_NEED, jsonObject.optString("DrNeed"), "0");
                addSetupItem(SharedPref.getChmCap(requireContext()) + " Need", SharedPref.CHM_NEED, jsonObject.optString("ChmNeed"), "0");
                addSetupItem(SharedPref.getStkCap(requireContext()) + " Need", SharedPref.STK_NEED, jsonObject.optString("StkNeed"), "0");
                addSetupItem(SharedPref.getUNLcap(requireContext()) + " Need", SharedPref.UNL_NEED, jsonObject.optString("UnDrNeed"), "0");


                // JW and Hospital setup
                addSetupItem("Joint Work Need", "JWNeed", jsonObject.optString("JWNeed"), "0");
                addSetupItem("Hospital Need", "HospNeed", jsonObject.optString("HospNeed"), "0");
                addSetupItem("Cip Need", "Cip_Need", jsonObject.optString("Cip_Need"), "0");
                addSetupItem("Add Session Need", "AddsessionNeed", jsonObject.optString("AddsessionNeed"), "0");
                addSetupItem("FW Meetup Mandatory", "FW_meetup_mandatory", jsonObject.optString("FW_meetup_mandatory"), "0");
                addSetupItem("Holiday Edit", "Holiday_Editable", jsonObject.optString("Holiday_Editable"), "0");
                addSetupItem("WeekOff Edit", "Weeklyoff_Editable", jsonObject.optString("Weeklyoff_Editable"), "0");
                addSetupItem("Remarks Need", "tp_objective_mandatory", jsonObject.optString("tp_objective_mandatory"), "0");
                addSetupItem("Plan All Doctors", "Plan_All_Drs", jsonObject.optString("Plan_All_Drs"), "0");
                addSetupItem("Visit Frequency Need", "visit_freq_need", jsonObject.optString("visit_freq_need"), "0");
                //addSetupItem("Plan All Doctors","Plan_All_Drs",jsonObject.optString("Plan_All_Drs"),"0");

                //Numberic values
                addSetupItem(SharedPref.getDrCap(requireContext()) + " Min Count", "min_doc", jsonObject.optString("min_doc", "0") + " Count", "2");
                addSetupItem(SharedPref.getDrCap(requireContext()) + " Max Count", "max_doc", jsonObject.optString("max_doc", "0") + " Count", "2");
                addSetupItem("Minimum Gap (Days)", "min_gap_need", jsonObject.optString("min_gap_need", "0") + " Count", "2");
                addSetupItem("Add session Count Limit", "AddsessionCount", jsonObject.optString("AddsessionCount", "0") + " Count", "2");


                //Remainder popup
                addSetupItem("Remainder Time", "RemainderTime", SharedPref.getDoctorRemainingShownDate(requireContext()),"2");

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (adapter != null) adapter.notifyDataSetChanged();
    }

    private void addSetupItem(String label, String key, String value, String chkType) {
        callstatus_model model = new callstatus_model();
        model.setCustName(label);
        model.setChkflk(chkType);

        if (chkType.equals("0")) {
            model.setSpKey(key);
            model.setPromoted(value != null && value.equals("0"));
        } else if (chkType.equals("2")) {
            model.setSpKey("GENERIC_NUM");
            model.setDcr_flag(value);

        } else {
            model.setSpKey(key);
        }

        list.add(model);
    }
}
//    private void addSetupItem(String label, String key, String value, String chkType) {
//        callstatus_model model = new callstatus_model();
//        model.setCustName(label);
//        model.setSpKey(key);
//        model.setChkflk(chkType);
//        if (chkType.equals("0")) {
//            model.setPromoted(value != null && value.equals("0"));
//        } else {
//            model.setTown_code(value);
//            model.setTown_name("");
//        }
//
//        list.add(model);
//    }


//    private void loadFromSharedPreferences() {
//        list.clear();
//        String tourPlan = SharedPref.getTpNeed(requireContext());
//        callstatus_model model = new callstatus_model();
//        //  model.setCustName(SharedPref.GEOTAG_NEED);
//        model.setCustName("Tour Plan");
//        model.setChkflk("0");
//        model.setSpKey(SharedPref.TP_NEED);
//        model.setPromoted(tourPlan.equals("0"));
//        list.add(model);
//
//
//        String tpBased = SharedPref.getDayplanTpBased(requireContext());
//        callstatus_model model1 = new callstatus_model();
//        //  model.setCustName(SharedPref.GEOTAG_NEED);
//        model1.setCustName("Day Plan TP Based");
//        model1.setChkflk("0");
//        model1.setSpKey(SharedPref.DAYPLAN_TP_BASED);
//        model1.setPromoted(tpBased.equals("0"));
//        list.add(model1);
//
//        String tpBasedMandatory = SharedPref.getTpMandatoryNeed(requireContext());
//        callstatus_model model2 = new callstatus_model();
//        //  model.setCustName(SharedPref.GEOTAG_NEED);
//        model2.setCustName("TP Mandatory Need");
//        model2.setChkflk("0");
//        model2.setSpKey(SharedPref.TP_MANDATORY_NEED);
//        model2.setPromoted(tpBasedMandatory.equals("0"));
//        list.add(model2);
//
/// /        String tpBasedDcr = SharedPref.getTpbasedDcr(requireContext());
/// /        callstatus_model model3 = new callstatus_model();
/// /        //  model.setCustName(SharedPref.GEOTAG_NEED);
/// /        model3.setCustName("TP Based DCR");
/// /        model3.setChkflk("0");
/// /        model3.setSpKey(SharedPref.TPBASED_DCR);
/// /        model3.setPromoted(tpBasedDcr.equals("0"));
/// /        list.add(model3);
//
//        String tpDeviation = SharedPref.getTpdcrDeviation(requireContext());
//        callstatus_model model3 = new callstatus_model();
//        //  model.setCustName(SharedPref.GEOTAG_NEED);
//        model3.setCustName("TP DCR Deviation");
//        model3.setChkflk("0");
//        model3.setSpKey(SharedPref.TPDCR_DEVIATION);
//        model3.setPromoted(tpDeviation.equals("0"));
//        list.add(model3);
//
//
//        callstatus_model model4 = new callstatus_model();
//        model4.setCustName("TP Start & End  Date");
//        model4.setChkflk("2");
//        model4.setSpKey(SharedPref.TP_START_DATE);
//        model4.setTown_code(SharedPref.getTpStartDate(requireContext())); // From date
//        model4.setTown_name(SharedPref.getTpEndDate(requireContext()));   // To date
//        list.add(model4);
//
//        String jwSelectionNeed = SharedPref.getJwAutoSelectionNeed(requireContext());
//        callstatus_model model5 = new callstatus_model();
//        model5.setCustName("TP JointWork Selection");
//        model5.setChkflk("0");
//        model5.setSpKey(SharedPref.JW_AUTO_SELECTION_NEED);
//        model5.setPromoted(jwSelectionNeed.equals("1"));
//        list.add(model5);
//
//        String holidayEdit = SharedPref.getEditHoliday(requireContext());
//        callstatus_model model8 = new callstatus_model();
//        model8.setCustName("Holiday Edit");
//        model8.setChkflk("0");
//        model8.setSpKey(SharedPref.EDIT_HOLIDAY);
//        model8.setPromoted(holidayEdit.equals("0"));
//        list.add(model8);
//



