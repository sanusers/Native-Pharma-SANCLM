package saneforce.sanclm.activity.reports.dayReport;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.reports.ReportFragContainerActivity;
import saneforce.sanclm.activity.reports.ReportsActivity;
import saneforce.sanclm.activity.reports.ReportsAdapter;
import saneforce.sanclm.databinding.FragmentDayReportBinding;
import saneforce.sanclm.utility.TimeUtils;


public class DayReportFragment extends Fragment {

    FragmentDayReportBinding binding;
    DayReportAdapter dayReportAdapter;
    ArrayList<DayReportModel> arrayList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentDayReportBinding.inflate(inflater,container,false);

        initialisation();
        populateAdapter();

        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        binding.calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(), new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        binding.date.setText(day + " " + TimeUtils.GetConvertedDate(TimeUtils.FORMAT_8, TimeUtils.FORMAT_9, String.valueOf(month + 1)) + " " + year);
                    }
                },year,month,day);
                datePickerDialog.show();
            }
        });

        return binding.getRoot();
    }

    public void initialisation(){
        Bundle bundle = this.getArguments();
        if(bundle != null){
            Log.e("test","day report data : " + bundle.getString("reportData"));
            Type type = new TypeToken<ArrayList<DayReportModel>>(){}.getType();
            arrayList = new Gson().fromJson(bundle.getString("reportData"),type);
        }
    }

    public void populateAdapter(){
        dayReportAdapter = new DayReportAdapter(arrayList, getContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.dayReportRecView.setLayoutManager(layoutManager);
        binding.dayReportRecView.setAdapter(dayReportAdapter);
    }


}