package saneforce.sanclm.activity.homeScreen.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.adapters.Call_adapter;
import saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.sanclm.activity.homeScreen.modelClass.CallsModalClass;
import saneforce.sanclm.storage.SharedPref;


public class CallsFragment extends Fragment {

    Call_adapter adapter;
    RecyclerView recyclerView;
    TextView tv_add_call;

    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.calls_fragment, container, false);
        Log.v("fragment", "CallFragment");
        recyclerView = v.findViewById(R.id.recyelerview);
        tv_add_call = v.findViewById(R.id.tv_add_call);
        adapter = new Call_adapter(getData());
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(manager);
        recyclerView.setAdapter(adapter);

        tv_add_call.setOnClickListener(view -> {
            if (SharedPref.getTodayDayPlanSfCode(requireActivity()).equalsIgnoreCase("null") || SharedPref.getTodayDayPlanSfCode(requireActivity()).isEmpty()) {
                Toast.makeText(requireActivity(), "Kindly Submit MyDayPlan", Toast.LENGTH_SHORT).show();
            } else {
                startActivity(new Intent(getContext(), DcrCallTabLayoutActivity.class));
            }
        });

        //  tv_add_call.setOnClickListener(view -> startActivity(new Intent(getContext(), DcrCallTabLayoutActivity.class)));

        return v;
    }


    private List<CallsModalClass> getData() {
        List<CallsModalClass> list = new ArrayList<>();
        list.add(new CallsModalClass("Aravind Raj (Doctor)", "2023-08-19,00:0:49", "D"));
        list.add(new CallsModalClass("Tom Latham (Chemist)", "2023-08-19,00:0:49", "C"));
        list.add(new CallsModalClass("Dharany (CIP)", "2023-08-19,00:0:49", "CIP"));

        return list;
    }


}