package saneforce.santrip.activity.homeScreen.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.adapters.Call_adapter;
import saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.santrip.activity.homeScreen.modelClass.CallsModalClass;
import saneforce.santrip.databinding.CallsFragmentBinding;


public class CallsFragment extends Fragment {

    CallsFragmentBinding binding;
    Call_adapter adapter;
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = CallsFragmentBinding.inflate(inflater,container,false);
        View v = binding.getRoot();

        adapter = new Call_adapter(getData());
        LinearLayoutManager manager = new LinearLayoutManager(getContext());
        binding.recyelerview.setNestedScrollingEnabled(false);
        binding.recyelerview.setHasFixedSize(true);
        binding.recyelerview.setLayoutManager(manager);
        binding.recyelerview.setAdapter(adapter);
        binding.tvAddCall.setOnClickListener(view -> startActivity(new Intent(getContext(), DcrCallTabLayoutActivity.class)));

        return v;
    }


    private List<CallsModalClass> getData() {
        List<CallsModalClass> list = new ArrayList<>();
        list.add(new CallsModalClass("Aravind Raj (Doctor)", "2023-08-19,00:0:49", "D"));
        list.add(new CallsModalClass("Tom Latham (Chemist)", "2023-08-19,00:0:49", "C"));
        list.add(new CallsModalClass("Dharany (CIP)", "2023-08-19,00:0:49", "CIP"));
        list.add(new CallsModalClass("Aravind Raj (Doctor)", "2023-08-19,00:0:49", "D"));
        list.add(new CallsModalClass("Tom Latham (Chemist)", "2023-08-19,00:0:49", "C"));
        list.add(new CallsModalClass("Dharany (CIP)", "2023-08-19,00:0:49", "CIP"));
        list.add(new CallsModalClass("Aravind Raj (Doctor)", "2023-08-19,00:0:49", "D"));
        list.add(new CallsModalClass("Tom Latham (Chemist)", "2023-08-19,00:0:49", "C"));
        list.add(new CallsModalClass("Dharany (CIP)", "2023-08-19,00:0:49", "CIP"));
        list.add(new CallsModalClass("Aravind Raj (Doctor)", "2023-08-19,00:0:49", "D"));
        list.add(new CallsModalClass("Tom Latham (Chemist)", "2023-08-19,00:0:49", "C"));
        list.add(new CallsModalClass("Dharany (CIP)", "2023-08-19,00:0:49", "CIP"));
        list.add(new CallsModalClass("Aravind Raj (Doctor)", "2023-08-19,00:0:49", "D"));
        list.add(new CallsModalClass("Tom Latham (Chemist)", "2023-08-19,00:0:49", "C"));
        list.add(new CallsModalClass("Dharany (CIP)", "2023-08-19,00:0:49", "CIP"));

        return list;
    }



}