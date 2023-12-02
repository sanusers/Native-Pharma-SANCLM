package saneforce.sanclm.activity;


import android.os.Bundle;
import android.util.Log;



import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import saneforce.sanclm.R;
import saneforce.sanclm.activity.reports.dayReport.DayReportFragment;
import saneforce.sanclm.databinding.ActivityMainBinding;


public class MainActivity extends AppCompatActivity{

    ActivityMainBinding binding;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }


}