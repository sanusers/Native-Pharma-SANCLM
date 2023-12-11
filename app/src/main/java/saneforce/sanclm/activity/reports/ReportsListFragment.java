package saneforce.sanclm.activity.reports;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import saneforce.sanclm.databinding.FragmentReportsListBinding;


public class ReportsListFragment extends Fragment {

    FragmentReportsListBinding binding;
    ReportsAdapter reportsAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentReportsListBinding.inflate(inflater,container,false);

        populateAdapter();


        return binding.getRoot();
    }

    public void populateAdapter(){
        ArrayList<String> arrayList = new ArrayList<>();
        arrayList.add("Day Report");
        arrayList.add("Monthly Report");
        arrayList.add("Day Check In Report");
        arrayList.add("Customer Check In Report");
        arrayList.add("Visit Monitor");

        reportsAdapter = new ReportsAdapter(arrayList,requireContext());
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(requireContext(),4);
        binding.recView.setLayoutManager(layoutManager);
        binding.recView.setAdapter(reportsAdapter);
    }
}