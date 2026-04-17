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
        model.setCustName(getString(R.string.doctor_fencing_need));
        model.setChkflk("0");
        model.setSpKey(SharedPref.GEOTAG_NEED);
        model.setPromoted(doctor.equals("1"));
        list.add(model);

        String chemist = SharedPref.getGeotagNeedChe(requireContext());
        callstatus_model model1 = new callstatus_model();
        model1.setCustName(getString(R.string.chemist_fencing_need));
//        model1.setCustName(SharedPref.GEOTAG_NEED_CHE);
        model1.setChkflk("0");
        model1.setSpKey(SharedPref.GEOTAG_NEED_CHE);
        model1.setPromoted(chemist.equals("1"));
        list.add(model1);


        String stk = SharedPref.getGeotagNeedStock(requireContext());
        callstatus_model model2 = new callstatus_model();
        //  model2.setCustName(SharedPref.GEOTAG_NEED_STOCK);
        model2.setCustName(getString(R.string.stockiest_fencing_need));
        model2.setChkflk("0");
        model2.setSpKey(SharedPref.GEOTAG_NEED_STOCK);
        model2.setPromoted(stk.equals("1"));
        list.add(model2);

        String unlistdr = SharedPref.getGeotagNeedUnlst(requireContext());
        callstatus_model model3 = new callstatus_model();
        //  model3.setCustName(SharedPref.GEOTAG_NEED_UNLST);
        model3.setCustName(getString(R.string.unlisted_fencing_need));
        model3.setChkflk("0");
        model3.setSpKey(SharedPref.GEOTAG_NEED_UNLST);
        model3.setPromoted(unlistdr.equals("1"));
        list.add(model3);


        String docChck = SharedPref.getCustSrtNd(requireContext());
        callstatus_model docChckin = new callstatus_model();
        docChckin.setCustName(SharedPref.getDrCap(requireContext()) + " " + getString(R.string.checkin));
        docChckin.setChkflk("0");
        docChckin.setSpKey(SharedPref.CUST_SRT_ND);
        docChckin.setPromoted(docChck.equals("0"));
        list.add(docChckin);


        String chmChck = SharedPref.getChmSrtNd(requireContext());
        callstatus_model chmChckin = new callstatus_model();
        chmChckin.setCustName(SharedPref.getChmCap(requireContext()) + " " + getString(R.string.checkin));
        chmChckin.setChkflk("0");
        chmChckin.setSpKey(SharedPref.CHM_SRT_ND);
        chmChckin.setPromoted(chmChck.equals("0"));
        list.add(chmChckin);

        String unlistChck = SharedPref.getUnlistSrtNd(requireContext());
        callstatus_model unlistChckin = new callstatus_model();
        unlistChckin.setCustName(SharedPref.getUNLcap(requireContext()) + " " + getString(R.string.checkin));
        unlistChckin.setChkflk("0");
        unlistChckin.setSpKey(SharedPref.UNLIST_SRT_ND);
        unlistChckin.setPromoted(unlistChck.equals("0"));
        list.add(unlistChckin);

        String docEvent = SharedPref.getDeNeed(requireContext());
        callstatus_model drEventCap = new callstatus_model();
        drEventCap.setCustName(SharedPref.getDrCap(requireContext()) + " " + getString(R.string.event_capture));
        drEventCap.setChkflk("0");
        drEventCap.setSpKey(SharedPref.DE_NEED);
        drEventCap.setPromoted(docEvent.equals("0"));
        list.add(drEventCap);


        String chmEvent = SharedPref.getCeNeed(requireContext());
        callstatus_model chmEventCap = new callstatus_model();
        chmEventCap.setCustName(SharedPref.getChmCap(requireContext()) + " " + getString(R.string.event_capture));
        chmEventCap.setChkflk("0");
        chmEventCap.setSpKey(SharedPref.CE_NEED);
        chmEventCap.setPromoted(chmEvent.equals("0"));
        list.add(chmEventCap);

        String stkEvent = SharedPref.getSeNeed(requireContext());
        callstatus_model stkEventCap = new callstatus_model();
        stkEventCap.setCustName(SharedPref.getStkCap(requireContext()) + " " + getString(R.string.event_capture));
        stkEventCap.setChkflk("0");
        stkEventCap.setSpKey(SharedPref.SE_NEED);
        stkEventCap.setPromoted(stkEvent.equals("0"));
        list.add(stkEventCap);


        String unEvent = SharedPref.getNeNeed(requireContext());
        callstatus_model unEventCap = new callstatus_model();
        unEventCap.setCustName(SharedPref.getUNLcap(requireContext()) + " " + getString(R.string.event_capture));
        unEventCap.setChkflk("0");
        unEventCap.setSpKey(SharedPref.NE_NEED);
        unEventCap.setPromoted(unEvent.equals("0"));
        list.add(unEventCap);

        String additional = SharedPref.getAdditionalCallNeed(requireContext());
        callstatus_model addCalls = new callstatus_model();
        addCalls.setCustName(getString(R.string.additional_call));
        addCalls.setChkflk("0");
        addCalls.setSpKey(SharedPref.ADDITIONAL_CALL_NEED);
        addCalls.setPromoted(additional.equals("0"));
        list.add(addCalls);

        String delete = SharedPref.getEditCallDelNeed(requireContext());
        callstatus_model editdeletecall = new callstatus_model();
        editdeletecall.setCustName(getString(R.string.delete_option));
        editdeletecall.setChkflk("0");
        editdeletecall.setSpKey(SharedPref.EDIT_CALL_DEL_NEED);
        editdeletecall.setPromoted(delete.equals("0"));
        list.add(editdeletecall);

        String jwSelectionNeed = SharedPref.getJwAutoSelectionNeed(requireContext());
        callstatus_model model5 = new callstatus_model();
        model5.setCustName(getString(R.string.joint_work_auto_selection_need));
        model5.setChkflk("0");
        model5.setSpKey(SharedPref.JW_AUTO_SELECTION_NEED);
        model5.setPromoted(jwSelectionNeed.equals("0"));
        list.add(model5);

        String rcpa = SharedPref.getRcpaMd(requireContext());
        callstatus_model rcpaMd  = new callstatus_model();
        rcpaMd.setCustName(getString(R.string.rcpa_mandatory));
        rcpaMd.setChkflk("0");
        rcpaMd.setSpKey(SharedPref.RCPA_MD);
        rcpaMd.setPromoted(rcpa.equals("0"));
        list.add(rcpaMd);

        String product = SharedPref.getDrPrdMd(requireContext());
        callstatus_model prodMd  = new callstatus_model();
        prodMd.setCustName(getString(R.string.product_mandatory));
        prodMd.setChkflk("0");
        prodMd.setSpKey(SharedPref.DR_PRD_MD);
        prodMd.setPromoted(product.equals("1"));
        list.add(prodMd);


        String input = SharedPref.getDrInpMd(requireContext());
        callstatus_model inputMd  = new callstatus_model();
        inputMd.setCustName(getString(R.string.input_mandatory));
        inputMd.setChkflk("0");
        inputMd.setSpKey(SharedPref.DR_INP_MD);
        inputMd.setPromoted(input.equals("1"));
        list.add(inputMd);

        String rxq = SharedPref.getDrRxQMd(requireContext());
        callstatus_model rxqMd  = new callstatus_model();
        rxqMd.setCustName(getString(R.string.rx_mandatory));
        rxqMd.setChkflk("0");
        rxqMd.setSpKey(SharedPref.DR_RX_Q_MD);
        rxqMd.setPromoted(rxq.equals("1"));
        list.add(rxqMd);

        String smp = SharedPref.getDrSmpQMd(requireContext());
        callstatus_model smpMd  = new callstatus_model();
        smpMd.setCustName(getString(R.string.sample_quantity_mandatory));
        smpMd.setChkflk("0");
        smpMd.setSpKey(SharedPref.DR_SMP_Q_MD);
        smpMd.setPromoted(smp.equals("1"));
        list.add(smpMd);

        String jw = SharedPref.getDocJointworkMandatoryNeed(requireContext());
        callstatus_model jwMd  = new callstatus_model();
        jwMd.setCustName(getString(R.string.joint_work_mandatory));
        jwMd.setChkflk("0");
        jwMd.setSpKey(SharedPref.DOC_JOINTWORK_MANDATORY_NEED);
        jwMd.setPromoted(jw.equals("0"));
        list.add(jwMd);

        String fb = SharedPref.getDrFeedMd(requireContext());
        callstatus_model fbMd  = new callstatus_model();
        fbMd.setCustName(getString(R.string.feed_back_mandatory));
        fbMd.setChkflk("0");
        fbMd.setSpKey(SharedPref.DR_FEED_MD);
        fbMd.setPromoted(fb.equals("1"));
        list.add(fbMd);

        String eventcap = SharedPref.getDrEventMd(requireContext());
        callstatus_model event  = new callstatus_model();
        event.setCustName(getString(R.string.event_capture_mandatory));
        event.setChkflk("0");
        event.setSpKey(SharedPref.DR_EVENT_MD);
        event.setPromoted(eventcap.equals("0"));
        list.add(event);

//        String remarks = SharedPref.getTempNd(requireContext());
//        callstatus_model remarksMd  = new callstatus_model();
//        remarksMd.setCustName("Remarks Mandatory");
//        remarksMd.setChkflk("0");
//        remarksMd.setSpKey(SharedPref.TEMP_ND);
//        remarksMd.setPromoted(remarks.equals("0"));
//        list.add(remarksMd);

    }
}
