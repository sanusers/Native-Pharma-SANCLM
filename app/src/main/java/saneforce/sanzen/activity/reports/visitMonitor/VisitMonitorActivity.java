package saneforce.sanzen.activity.reports.visitMonitor;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.dcrCallSelection.fragments.ChemistFragment;
import saneforce.sanzen.databinding.ActivityVisitMonitorBinding;
import saneforce.sanzen.storage.SharedPref;

public class VisitMonitorActivity extends AppCompatActivity {
    ActivityVisitMonitorBinding binding;

    // Remove the constructor
    // public VisitMonitorActivity(List<DoctorStatsModel> dataList) {
    //     this.dataList = dataList;
    // }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVisitMonitorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize views
        LinearLayout backArrow = binding.backArrow;
        TextView title = binding.title;
        TextView note = binding.tvNote;
        TextView self = binding.self;
        TextView live = binding.live;
        EditText search = binding.searchCust;
        FrameLayout doctor_fragment_container = binding.doctorFragmentContainer;

        backArrow.setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());

        live.setOnClickListener(view -> {
            // Your logic for the 'live' button click
        });

        if ("2".equalsIgnoreCase(SharedPref.getSfType(this))) {
            search.setVisibility(View.VISIBLE);
        } else {
            search.setVisibility(View.VISIBLE);
        }

        // Add the DoctorFragment to the FrameLayout
        if (savedInstanceState == null) {
           DoctorFragment doctorFragment = new DoctorFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.doctor_fragment_container, doctorFragment);
            fragmentTransaction.commit();

            ChemistVisitFragment chemistFragment = new ChemistVisitFragment();
            FragmentManager fragmentManager1 = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction1 = fragmentManager1.beginTransaction();
            fragmentTransaction1.replace(R.id.doctor_fragment_container, chemistFragment);
            fragmentTransaction1.commit();

            StockiestVisitFragment StockiestFragment = new StockiestVisitFragment();
            FragmentManager fragmentManager2 = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction2 = fragmentManager2.beginTransaction();
            fragmentTransaction2.replace(R.id.doctor_fragment_container, StockiestFragment);
            fragmentTransaction2.commit();

            UnlistedVisitFragment UnlistedFragment = new UnlistedVisitFragment();
            FragmentManager fragmentManager3 = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction3 = fragmentManager3.beginTransaction();
            fragmentTransaction3.replace(R.id.doctor_fragment_container, UnlistedFragment);
            fragmentTransaction3.commit();
        }

    }
}