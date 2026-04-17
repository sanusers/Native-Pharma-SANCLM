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
        String dcrSeq = SharedPref.getDcrSequential(requireContext());
        callstatus_model Seq = new callstatus_model();
        Seq.setCustName(getString(R.string.dcr_sequential));
        Seq.setChkflk("0");
        Seq.setSpKey(SharedPref.DCR_SEQUENTIAL);
        Seq.setPromoted(dcrSeq.equals("0"));
        list.add(Seq);

        String dcr_lock = SharedPref.getDcrLockDays(requireContext());
        callstatus_model lock = new callstatus_model();
        lock.setCustName(getString(R.string.dcr_lock));
        lock.setChkflk("0");
        lock.setSpKey(SharedPref.DCR_LOCK_DAYS);
        lock.setPromoted(dcr_lock.equals("0"));
        list.add(lock);


        callstatus_model lockDays = new callstatus_model();
        lockDays.setCustName(getString(R.string.dcr_lock_days));
        lockDays.setChkflk("2");
        lockDays.setSpKey(SharedPref.SEQ_DCR_LOCK_DAYS);
        lockDays.setMonth_name(SharedPref.getSeqDcrLockDays(requireContext()));
        list.add(lockDays);


        String holiday = SharedPref.getHolidayAutoPostNeed(requireContext());
        callstatus_model model = new callstatus_model();
        //  model.setCustName(SharedPref.HOLIDAY_AUTOPOST_NEED);
        model.setCustName(getString(R.string.holiday_auto_post));
        model.setChkflk("0");
        model.setSpKey(SharedPref.HOLIDAY_AUTOPOST_NEED);
        model.setPromoted(holiday.equals("1"));
        list.add(model);

        String weekOff = SharedPref.getWeekoffAutoPostNeed(requireContext());
        callstatus_model model1 = new callstatus_model();
        //  model.setCustName(SharedPref.HOLIDAY_AUTOPOST_NEED);
        model1.setCustName(getString(R.string.week_Off_auto_post));
        model1.setChkflk("0");
        model1.setSpKey(SharedPref.WEEKOFF_AUTOPOST_NEED);
        model1.setPromoted(weekOff.equals("1"));
        list.add(model1);

//        String jwSelectionNeed = SharedPref.getJwAutoSelectionNeed(requireContext());
//        callstatus_model model5 = new callstatus_model();
//        model5.setCustName(" JointWork Auto Selection Need");
//        model5.setChkflk("0");
//        model5.setSpKey(SharedPref.JW_AUTO_SELECTION_NEED);
//        model5.setPromoted(jwSelectionNeed.equals("0"));
//        list.add(model5);

    }
    }