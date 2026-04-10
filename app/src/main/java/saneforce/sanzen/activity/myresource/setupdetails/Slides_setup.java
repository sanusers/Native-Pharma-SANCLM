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
        //  model.setCustName(SharedPref.GEOTAG_NEED);
        model.setCustName("Practice Need");
        model.setChkflk("0");
        model.setSpKey(SharedPref.PRACTICE_NEED);
        model.setPromoted(practiceNd.equals("0"));
        list.add(model);
    }

}
