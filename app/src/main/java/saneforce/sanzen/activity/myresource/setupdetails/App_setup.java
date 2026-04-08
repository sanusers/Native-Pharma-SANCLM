package saneforce.sanzen.activity.myresource.setupdetails;

import android.content.Context;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.myresource.callstatusview.callstatus_model;
import saneforce.sanzen.storage.SharedPref;

public class App_setup extends Fragment {

    RecyclerView recyclerView;
    setupDetailsAdapter adapter;
    ArrayList<callstatus_model> list = new ArrayList<>();


    public static boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            return locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
                    || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_app_setup, container, false);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        DividerItemDecoration divider = new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
//        recyclerView.addItemDecoration(divider);

        loadFromSharedPreferences();

        adapter = new setupDetailsAdapter(getContext(), list);
        recyclerView.setAdapter(adapter);

        return view;
    }

    private void loadFromSharedPreferences() {
        list.clear();

        boolean isGpsEnabled = App_setup.isLocationEnabled(requireContext());

        callstatus_model model1 = new callstatus_model();
        model1.setCustName(SharedPref.LOCATION_TRACK);
        model1.setChkflk("0");
        model1.setSpKey(SharedPref.LOCATION_TRACK);
        model1.setPromoted(isGpsEnabled);
        list.add(model1);

        String taggedDcr = SharedPref.getTaggedDcrCustomers(requireContext());
        callstatus_model model2 = new callstatus_model();
        model2.setCustName(SharedPref.TAGGED_DCR_CUSTOMERS);
        model2.setChkflk("0");
        model2.setSpKey(SharedPref.TAGGED_DCR_CUSTOMERS);
        model2.setPromoted(taggedDcr.equals("1"));
        list.add(model2);
    }
}
