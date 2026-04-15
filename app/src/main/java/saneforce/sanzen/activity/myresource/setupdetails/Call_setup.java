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

public class Call_setup extends Fragment {
    RecyclerView recyclerView;
    setupDetailsAdapter adapter;
    ArrayList<callstatus_model> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.activity_call_setup, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        loadFromSharedPreferences();

        adapter = new setupDetailsAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void loadFromSharedPreferences() {
        list.clear();
        String doctor = SharedPref.getGeotagNeed(requireContext());
        callstatus_model model = new callstatus_model();
        //  model.setCustName(SharedPref.GEOTAG_NEED);
        model.setCustName("Doctor Fencing Need");
        model.setChkflk("0");
        model.setSpKey(SharedPref.GEOTAG_NEED);
        model.setPromoted(doctor.equals("1"));
        list.add(model);

        String chemist = SharedPref.getGeotagNeedChe(requireContext());
        callstatus_model model1 = new callstatus_model();
        model1.setCustName("Chemist Fencing Need");
//        model1.setCustName(SharedPref.GEOTAG_NEED_CHE);
        model1.setChkflk("0");
        model1.setSpKey(SharedPref.GEOTAG_NEED_CHE);
        model1.setPromoted(chemist.equals("1"));
        list.add(model1);


        String stk = SharedPref.getGeotagNeedStock(requireContext());
        callstatus_model model2 = new callstatus_model();
        //  model2.setCustName(SharedPref.GEOTAG_NEED_STOCK);
        model2.setCustName("Stockiest Fencing Need");
        model2.setChkflk("0");
        model2.setSpKey(SharedPref.GEOTAG_NEED_STOCK);
        model2.setPromoted(stk.equals("1"));
        list.add(model2);

        String unlistdr = SharedPref.getGeotagNeedUnlst(requireContext());
        callstatus_model model3 = new callstatus_model();
        //  model3.setCustName(SharedPref.GEOTAG_NEED_UNLST);
        model3.setCustName("UnListed Fencing Need");
        model3.setChkflk("0");
        model3.setSpKey(SharedPref.GEOTAG_NEED_UNLST);
        model3.setPromoted(unlistdr.equals("1"));
        list.add(model3);


        String docChck = SharedPref.getCustSrtNd(requireContext());
        callstatus_model docChckin = new callstatus_model();
        docChckin.setCustName(SharedPref.getDrCap(requireContext()) + " CheckIn");
        docChckin.setChkflk("0");
        docChckin.setSpKey(SharedPref.CUST_SRT_ND);
        docChckin.setPromoted(docChck.equals("0"));
        list.add(docChckin);


        String chmChck = SharedPref.getChmSrtNd(requireContext());
        callstatus_model chmChckin = new callstatus_model();
        chmChckin.setCustName(SharedPref.getChmCap(requireContext()) + " CheckIn");
        chmChckin.setChkflk("0");
        chmChckin.setSpKey(SharedPref.CHM_SRT_ND);
        chmChckin.setPromoted(chmChck.equals("0"));
        list.add(chmChckin);

        String unlistChck = SharedPref.getUnlistSrtNd(requireContext());
        callstatus_model unlistChckin = new callstatus_model();
        unlistChckin.setCustName(SharedPref.getUNLcap(requireContext()) + " CheckIn");
        unlistChckin.setChkflk("0");
        unlistChckin.setSpKey(SharedPref.UNLIST_SRT_ND);
        unlistChckin.setPromoted(unlistChck.equals("0"));
        list.add(unlistChckin);

        String docEvent = SharedPref.getDeNeed(requireContext());
        callstatus_model drEventCap = new callstatus_model();
        drEventCap.setCustName(SharedPref.getDrCap(requireContext()) + " Event Capture");
        drEventCap.setChkflk("0");
        drEventCap.setSpKey(SharedPref.DE_NEED);
        drEventCap.setPromoted(docEvent.equals("0"));
        list.add(drEventCap);


        String chmEvent = SharedPref.getCeNeed(requireContext());
        callstatus_model chmEventCap = new callstatus_model();
        chmEventCap.setCustName(SharedPref.getChmCap(requireContext()) + " Event Capture");
        chmEventCap.setChkflk("0");
        chmEventCap.setSpKey(SharedPref.CE_NEED);
        chmEventCap.setPromoted(chmEvent.equals("0"));
        list.add(chmEventCap);

        String stkEvent = SharedPref.getSeNeed(requireContext());
        callstatus_model stkEventCap = new callstatus_model();
        stkEventCap.setCustName(SharedPref.getStkCap(requireContext()) + " Event Capture");
        stkEventCap.setChkflk("0");
        stkEventCap.setSpKey(SharedPref.SE_NEED);
        stkEventCap.setPromoted(stkEvent.equals("0"));
        list.add(stkEventCap);


        String unEvent = SharedPref.getNeNeed(requireContext());
        callstatus_model unEventCap = new callstatus_model();
        unEventCap.setCustName(SharedPref.getUNLcap(requireContext()) + " Event Capture");
        unEventCap.setChkflk("0");
        unEventCap.setSpKey(SharedPref.NE_NEED);
        unEventCap.setPromoted(unEvent.equals("0"));
        list.add(unEventCap);
    }
}
