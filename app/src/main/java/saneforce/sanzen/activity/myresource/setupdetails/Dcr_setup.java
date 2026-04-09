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

public class Dcr_setup extends Fragment {
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
        String holiday = SharedPref.getHolidayAutoPostNeed(requireContext());
        callstatus_model model = new callstatus_model();
        //  model.setCustName(SharedPref.HOLIDAY_AUTOPOST_NEED);
        model.setCustName("Holiday (Autopost)");
        model.setChkflk("0");
        model.setSpKey(SharedPref.HOLIDAY_AUTOPOST_NEED);
        model.setPromoted(holiday.equals("1"));
        list.add(model);

        String weekOff = SharedPref.getWeekoffAutoPostNeed(requireContext());
        callstatus_model model1 = new callstatus_model();
        //  model.setCustName(SharedPref.HOLIDAY_AUTOPOST_NEED);
        model1.setCustName("WeekOff (Autopost)");
        model1.setChkflk("0");
        model1.setSpKey(SharedPref.WEEKOFF_AUTOPOST_NEED);
        model1.setPromoted(weekOff.equals("1"));
        list.add(model1);

    }
    }