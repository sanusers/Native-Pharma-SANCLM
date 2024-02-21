package saneforce.santrip.activity.reports;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import saneforce.santrip.R;
import saneforce.santrip.activity.reports.dayReport.DataViewModel;
import saneforce.santrip.activity.reports.dayReport.fragment.DayReportFragment;
import saneforce.santrip.databinding.ActivityReportFragContainerBinding;

public class ReportFragContainerActivity extends AppCompatActivity {

    public TextView title;
    ActivityReportFragContainerBinding binding;
    DataViewModel dataViewModel;

    //To Hide the bottomNavigation When popup
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            binding.getRoot().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportFragContainerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        title = binding.title;
        initialization();

        binding.backArrow.setOnClickListener(view -> onBackPressed());

    }

    public void initialization() {
        dataViewModel = new ViewModelProvider(this).get(DataViewModel.class);
        String fragmentStr = "";
        Bundle bundle = getIntent().getBundleExtra("reportBundle");
        if (bundle != null) {
            dataViewModel.saveSummaryData(bundle.getString("data"));
            dataViewModel.saveDate(bundle.getString("date"));
            fragmentStr = bundle.getString("fragment");
        }

        if (fragmentStr.toUpperCase().equals("DAY REPORT")) {
            loadFragment(new DayReportFragment());
//            case "MONTHLY REPORT" : {
//                break;
//            }
//            case "DAY CHECK IN REPORT" : {
//                break;
//            }
//            case "CUSTOMER CHECK IN REPORT" : {
//                break;
//            }
//            case "VISIT MONITOR" : {
//                break;
//            }
        }
    }

    public void loadFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frameLayout);
        if (fragment instanceof DayReportFragment) {
            Intent intent = new Intent(ReportFragContainerActivity.this, ReportsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        super.onBackPressed();
    }

}