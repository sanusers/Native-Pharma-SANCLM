package saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.adapter.AdapterDCRCallSelection;
import saneforce.sanclm.activity.Map.CustSelection.CustList;
import saneforce.sanclm.R;


public class ChemistFragment extends Fragment {
    RecyclerView rv_list;
    ArrayList<CustList> custListArrayList = new ArrayList<>();
    AdapterDCRCallSelection adapterDCRCallSelection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_chemist, container, false);
        rv_list = v.findViewById(R.id.rv_cust_list_selection);

        custListArrayList.clear();
        custListArrayList.add(new CustList("Mohammed medicals", "Category", "Cardio Surgion", "Madurai"));
        custListArrayList.add(new CustList("Kumkar pharmaticals", "Category", "Neurolgist", "Chennai"));
        custListArrayList.add(new CustList("Aasik industried", "Category", "MBBS", "Trichy"));
        custListArrayList.add(new CustList("jaipur Med", "Category", "Ortho Specialist", "Kanyakumari"));
        custListArrayList.add(new CustList("Venkatesh  medicals", "Category", "Dermotologist", "Sivagangai"));
        custListArrayList.add(new CustList("Akash Med", "Category", "MBBS", "Madurai"));
        custListArrayList.add(new CustList("Aravindh", "Category", "Ortho", "Vellore"));
        custListArrayList.add(new CustList("Surya Vignesh Kumar indsutries ", "Category", "Dermotologist", "Kerala"));

        adapterDCRCallSelection = new AdapterDCRCallSelection(getContext(), custListArrayList);
        rv_list.setItemAnimator(new DefaultItemAnimator());
        rv_list.setLayoutManager(new GridLayoutManager(getContext(), 4, GridLayoutManager.VERTICAL, false));
        rv_list.setAdapter(adapterDCRCallSelection);

        return v;
    }
}