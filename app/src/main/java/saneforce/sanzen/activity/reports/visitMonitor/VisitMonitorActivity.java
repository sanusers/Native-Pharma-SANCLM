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
        }

        // REMOVE all RecyclerView and adapter logic from the Activity
        // This is the responsibility of the Fragment now
        // RoomDB roomDB = RoomDB.getDatabase(this);
        // masterDataDao = roomDB.masterDataDao();
        // RecyclerView recyclerView = binding.recyclerView;
        // recyclerView.setLayoutManager(new LinearLayoutManager(this));
        // DoctorFragment doctorFragment = new DoctorFragment();
        // DoctorStatsAdapter adapter = new DoctorStatsAdapter(dataList);
        // recyclerView.setAdapter(adapter);
    }
}