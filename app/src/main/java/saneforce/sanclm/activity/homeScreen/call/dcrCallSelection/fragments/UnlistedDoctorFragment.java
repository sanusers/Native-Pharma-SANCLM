package saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.adapter.AdapterDCRCallSelection;
import saneforce.sanclm.R;
import saneforce.sanclm.activity.map.custSelection.CustList;


public class UnlistedDoctorFragment extends Fragment {
    RecyclerView rv_list;
    ArrayList<CustList> custListArrayList = new ArrayList<>();
    AdapterDCRCallSelection adapterDCRCallSelection;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_unlisted_doctor, container, false);
        rv_list = v.findViewById(R.id.rv_cust_list_selection);

        custListArrayList.clear();
        custListArrayList.add(new CustList("Mohammed Ameer BashaKhan", "Category", "Cardio Surgion", "Trichy"));
        custListArrayList.add(new CustList("Baskar Kumar Reddy", "Category", "Neurolgist", "Trichy"));
        custListArrayList.add(new CustList("Aasik", "Category", "MBBS", "Trichy"));
        custListArrayList.add(new CustList("Umar Kathab Manzoor Ali", "Category", "Ortho Specialist", "Kanyakumari"));
        custListArrayList.add(new CustList("Aravindh", "Category", "Ortho", "Vellore"));
        custListArrayList.add(new CustList("Surya Vignesh Kumar ", "Category", "Dermotologist", "Kerala"));
        custListArrayList.add(new CustList("Jahir Basha", "Category", "Gynocologist", "Andhra"));
        custListArrayList.add(new CustList("Vamshi Kannan", "Category", "Neurolgist", "Jammu"));
        custListArrayList.add(new CustList("Madhan", "Category", "Cardiogilist", "Kanyakumari"));

        adapterDCRCallSelection = new AdapterDCRCallSelection(getActivity(),getContext(), custListArrayList);
        rv_list.setItemAnimator(new DefaultItemAnimator());
        rv_list.setLayoutManager(new GridLayoutManager(getContext(), 4, GridLayoutManager.VERTICAL, false));
        rv_list.setAdapter(adapterDCRCallSelection);
        return v;
    }
}