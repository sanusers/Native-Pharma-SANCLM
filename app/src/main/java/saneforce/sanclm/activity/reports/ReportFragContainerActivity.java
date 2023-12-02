package saneforce.sanclm.activity.reports;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.util.Log;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.reports.dayReport.DayReportFragment;
import saneforce.sanclm.databinding.ActivityReportFragContainerBinding;

public class ReportFragContainerActivity extends AppCompatActivity {

    ActivityReportFragContainerBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityReportFragContainerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String data = "";
        String fragmentStr = "";
        Bundle bundle = getIntent().getBundleExtra("reportBundle");
        if(bundle != null){
            data = bundle.getString("data");
            fragmentStr = bundle.getString("fragment");
        }

        switch (fragmentStr.toUpperCase()){
            case "DAY REPORT" : {
                loadFragment(new DayReportFragment(),data);
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

    public void loadFragment(Fragment fragment,String data){
        Bundle bundle = new Bundle();
        bundle.putString("reportData",data);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout,fragment).commit();

    }
}