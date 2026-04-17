package saneforce.sanzen.activity.myresource.setupdetails;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.myresource.callstatusview.callstatus_model;
import saneforce.sanzen.storage.SharedPref;

public class Slides_setup extends Fragment {
    RecyclerView recyclerView;
    setupDetailsAdapter adapter;
    ArrayList<callstatus_model> list = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.activity_slides_setup, container, false);
        recyclerView = view.findViewById(R.id.recyclerView);
        loadFromSharedPreferences();
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new setupDetailsAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);
        return view;
    }

    private void loadFromSharedPreferences() {
        list.clear();
        String practiceNd = SharedPref.getPracticeNeed(requireContext());
        callstatus_model model = new callstatus_model();
        model.setCustName(getString(R.string.practice_need));
        model.setChkflk("0");
        model.setSpKey(SharedPref.PRACTICE_NEED);
        model.setPromoted(practiceNd.equals("0"));
        list.add(model);


        String presentNd = SharedPref.getPresentationNeed(requireContext());
        callstatus_model present = new callstatus_model();
        present.setCustName(getString(R.string.presentation_need));
        present.setChkflk("0");
        present.setSpKey(SharedPref.PRESENTATION_NEED);
        present.setPromoted(presentNd.equals("0"));
        list.add(present);


        String zoomIn = SharedPref.getZoomEnabled(requireContext());
        callstatus_model zoom = new callstatus_model();
        zoom.setCustName(getString(R.string.zooming_option_need_for_slides));
        zoom.setChkflk("0");
        zoom.setSpKey(SharedPref.ZOOM_FLAG);
        zoom.setPromoted(zoomIn.equals("0"));
        list.add(zoom);

        String html = SharedPref.getSlideAutoPlay(requireContext());
        callstatus_model htmlPlay = new callstatus_model();
        htmlPlay.setCustName(getString(R.string.html_file_play));
        htmlPlay.setChkflk("0");
        htmlPlay.setSpKey(SharedPref.SLIDE_AUTO_PLAY);
        htmlPlay.setPromoted(html.equals("0"));
        list.add(htmlPlay);


//        String detailing = SharedPref.getDetailingIdleDuration(requireContext());
//        callstatus_model detailingDuration = new callstatus_model();
//        detailingDuration.setCustName(getString(R.string.detailing_idle_duration));
//        detailingDuration.setChkflk("0");
//        detailingDuration.setSpKey(SharedPref.DETAILING_IDLE_DURATION);
//        detailingDuration.setPromoted(detailing.equals("0"));
//        list.add(detailingDuration);



        callstatus_model detailing = new callstatus_model();
        detailing.setCustName(getString(R.string.detailing_idle_duration));
        detailing.setChkflk("2");
        detailing.setSpKey(SharedPref.DETAILING_IDLE_DURATION);
        detailing.setDcrname(SharedPref.getDetailingIdleDuration(requireContext()));
        list.add(detailing);


        String drdetailing = SharedPref.getSkipDetailingDr(requireContext());
        callstatus_model skip = new callstatus_model();
        skip.setCustName(SharedPref.getDrCap(requireContext())+ " " + getString(R.string.skip_detailing));
        skip.setChkflk("0");
        skip.setSpKey(SharedPref.SKIP_DETAILING_DR);
        skip.setPromoted(drdetailing.equals("0"));
        list.add(skip);


        String chmdetailing = SharedPref.getSkipDetailingChe(requireContext());
        callstatus_model chmskip = new callstatus_model();
        chmskip.setCustName(SharedPref.getChmCap(requireContext())+ " " + getString(R.string.skip_detailing));
        chmskip.setChkflk("0");
        chmskip.setSpKey(SharedPref.SKIP_DETAILING_CHE);
        chmskip.setPromoted(chmdetailing.equals("0"));
        list.add(chmskip);

        String sktdetailing = SharedPref.getSkipDetailingStk(requireContext());
        callstatus_model sktskip = new callstatus_model();
        sktskip.setCustName(SharedPref.getStkCap(requireContext())+ " " + getString(R.string.skip_detailing));
        sktskip.setChkflk("0");
        sktskip.setSpKey(SharedPref.SKIP_DETAILING_STK);
        sktskip.setPromoted(sktdetailing.equals("0"));
        list.add(sktskip);

        String undetailing = SharedPref.getSkipDetailingUndr(requireContext());
        callstatus_model unskip = new callstatus_model();
        unskip.setCustName(SharedPref.getUNLcap(requireContext())+ " " + getString(R.string.skip_detailing));
        unskip.setChkflk("0");
        unskip.setSpKey(SharedPref.SKIP_DETAILING_UNDR);
        unskip.setPromoted(undetailing.equals("0"));
        list.add(unskip);
    }

}
