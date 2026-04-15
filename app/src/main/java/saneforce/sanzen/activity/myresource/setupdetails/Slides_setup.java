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
        model.setCustName("Practice Need");
        model.setChkflk("0");
        model.setSpKey(SharedPref.PRACTICE_NEED);
        model.setPromoted(practiceNd.equals("0"));
        list.add(model);


        String presentNd = SharedPref.getPresentationNeed(requireContext());
        callstatus_model present = new callstatus_model();
        present.setCustName("Presentation Need");
        present.setChkflk("0");
        present.setSpKey(SharedPref.PRESENTATION_NEED);
        present.setPromoted(presentNd.equals("0"));
        list.add(present);


        String zoomIn = SharedPref.getZoomEnabled(requireContext());
        callstatus_model zoom = new callstatus_model();
        zoom.setCustName("Zooming option Need For Slides");
        zoom.setChkflk("0");
        zoom.setSpKey(SharedPref.ZOOM_FLAG);
        zoom.setPromoted(zoomIn.equals("0"));
        list.add(zoom);

        String html = SharedPref.getSlideAutoPlay(requireContext());
        callstatus_model htmlPlay = new callstatus_model();
        htmlPlay.setCustName("Html File Play");
        htmlPlay.setChkflk("0");
        htmlPlay.setSpKey(SharedPref.SLIDE_AUTO_PLAY);
        htmlPlay.setPromoted(html.equals("0"));
        list.add(htmlPlay);


        String detailing = SharedPref.getDetailingIdleDuration(requireContext());
        callstatus_model detailingDuration = new callstatus_model();
        detailingDuration.setCustName("Detailing Idle Duration");
        detailingDuration.setChkflk("0");
        detailingDuration.setSpKey(SharedPref.DETAILING_IDLE_DURATION);
        detailingDuration.setPromoted(detailing.equals("0"));
        list.add(detailingDuration);





    }

}
