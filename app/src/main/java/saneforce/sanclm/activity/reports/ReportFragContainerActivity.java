package saneforce.sanclm.activity.reports;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.reports.dayReport.DataViewModel;
import saneforce.sanclm.activity.reports.dayReport.fragment.DayReportFragment;
import saneforce.sanclm.databinding.ActivityReportFragContainerBinding;

public class ReportFragContainerActivity extends AppCompatActivity {

    ActivityReportFragContainerBinding binding;
    DataViewModel dataViewModel;
    public TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportFragContainerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        title = binding.title;
        initialization();

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    public void initialization(){
        dataViewModel = new ViewModelProvider(this).get(DataViewModel.class);
        String fragmentStr = "";
        Bundle bundle = getIntent().getBundleExtra("reportBundle");
        if(bundle != null){
            dataViewModel.saveSummaryData(bundle.getString("data"));
            dataViewModel.saveDate(bundle.getString("date"));
            fragmentStr = bundle.getString("fragment");
        }

        switch (fragmentStr.toUpperCase()){
            case "DAY REPORT" : {
                loadFragment(new DayReportFragment());

                break;
            }
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

    public void loadFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.frameLayout,fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frameLayout);
        if(fragment instanceof DayReportFragment){
            Intent intent = new Intent(ReportFragContainerActivity.this,ReportsActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }
        super.onBackPressed();
    }

}