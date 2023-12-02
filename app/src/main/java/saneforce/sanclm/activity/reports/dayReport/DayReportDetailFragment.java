package saneforce.sanclm.activity.reports.dayReport;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.databinding.FragmentDayReportBinding;
import saneforce.sanclm.databinding.FragmentDayReportDetailBinding;


public class DayReportDetailFragment extends Fragment {

    FragmentDayReportDetailBinding binding;
    DayReportModel dayReportModel;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDayReportDetailBinding.inflate(inflater,container,false);
        initialisation();


        return binding.getRoot();
    }

    public void initialisation(){
        Bundle bundle = this.getArguments();
        if(bundle != null){
            Type type = new TypeToken<DayReportModel>(){}.getType();
            dayReportModel = new Gson().fromJson(bundle.getString("reportData"), type);
        }
    }
}