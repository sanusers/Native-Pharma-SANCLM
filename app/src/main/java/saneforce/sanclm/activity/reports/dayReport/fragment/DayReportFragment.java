package saneforce.sanclm.activity.reports.dayReport.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanclm.R;
import saneforce.sanclm.activity.reports.CalendarAdapter;
import saneforce.sanclm.activity.reports.ReportFragContainerActivity;
import saneforce.sanclm.activity.reports.dayReport.DataViewModel;
import saneforce.sanclm.activity.reports.dayReport.DayReportModel;
import saneforce.sanclm.activity.reports.dayReport.adapter.DayReportAdapter;
import saneforce.sanclm.activity.tourPlan.calendar.OnDayClickInterface;
import saneforce.sanclm.activity.tourPlan.model.ModelClass;
import saneforce.sanclm.commonClasses.UtilityClass;
import saneforce.sanclm.databinding.FragmentDayReportBinding;
import saneforce.sanclm.network.ApiInterface;
import saneforce.sanclm.network.RetrofitClient;
import saneforce.sanclm.response.LoginResponse;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.utility.TimeUtils;


public class DayReportFragment extends Fragment {

    FragmentDayReportBinding binding;
    ApiInterface apiInterface;
    SQLite sqLite;
    LocalDate localDate;
    LoginResponse loginResponse;
    DayReportAdapter dayReportAdapter;
    CalendarAdapter calendarAdapter;
    AlertDialog calendarDialog;
    ArrayList<DayReportModel> arrayList = new ArrayList<>();
    ArrayList<String> daysArrayList = new ArrayList<>();
    DataViewModel dataViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDayReportBinding.inflate(inflater,container,false);
        dataViewModel = new ViewModelProvider(requireActivity()).get(DataViewModel.class);
        dataViewModel.getDate().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                binding.date.setText(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4,TimeUtils.FORMAT_19,s));
            }
        });

        initialisation();
        populateAdapter();

        binding.calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendarDialog();
            }
        });

        binding.searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.length()>0) {
                   binding.searchClearIcon.setVisibility(View.VISIBLE);
                }else {
                    binding.searchClearIcon.setVisibility(View.GONE);
                }
                dayReportAdapter.getFilter().filter(charSequence);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.searchClearIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.searchET.setText("");
            }
        });

        return binding.getRoot();
    }

    @SuppressLint("SetTextI18n")
    public void initialisation(){
        sqLite = new SQLite(getContext());
        localDate = LocalDate.now();
        loginResponse = sqLite.getLoginData();
        daysArrayList = daysInMonthArray(localDate);

        ReportFragContainerActivity activity = (ReportFragContainerActivity) getActivity();
        Objects.requireNonNull(activity).title.setText("Day Report");
        Type type = new TypeToken<ArrayList<DayReportModel>>(){}.getType();
        arrayList = new Gson().fromJson(dataViewModel.getSummaryData().getValue(), type);

    }

    private ArrayList<String> daysInMonthArray(LocalDate date) {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = YearMonth.from(date);
        int daysInMonth = yearMonth.lengthOfMonth();

        LocalDate firstOfMonth = localDate.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();
        switch (dayOfWeek){
            case 1:{
                dayOfWeek = 2;
                break;
            }
            case 2:{
                dayOfWeek = 3;
                break;
            }
            case 3:{
                dayOfWeek = 4;
                break;
            }
            case 4:{
                dayOfWeek = 5;
                break;
            }
            case 5:{
                dayOfWeek = 6;
                break;
            }
            case 6:{
                dayOfWeek = 7;
                break;
            }
            case 7:{
                dayOfWeek = 1;
                break;
            }
        }

        for (int i = 1; i<=42; i++) {
            if (i<dayOfWeek) {
                daysInMonthArray.add("");
            }else {
                if (i<daysInMonth + dayOfWeek) {
                    daysInMonthArray.add(String.valueOf((i + 1) - dayOfWeek));
                }
            }
        }

        //To eliminate the excess empty dates which comes with the LocalDate library
        if (daysInMonthArray.size()>=22 && daysInMonthArray.size()<=28) {
            for (int i = daysInMonthArray.size(); i<28; i++) {
                daysInMonthArray.add("");
            }
        }else if (daysInMonthArray.size()>=29 && daysInMonthArray.size()<=35) {
            for (int i = daysInMonthArray.size(); i<35; i++) {
                daysInMonthArray.add("");
            }
        }else if (daysInMonthArray.size()>=36 && daysInMonthArray.size()<=42) {
            for (int i = daysInMonthArray.size(); i<42; i++) {
                daysInMonthArray.add("");
            }
        }

        return daysInMonthArray;
    }

    private String monthYearFromDate(LocalDate date,String requiredFormat) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(requiredFormat);
        return date.format(formatter);
    }

    public void calendarDialog(){

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        LayoutInflater layoutInflater = this.getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.day_report_date_picker_layout,null);
        alertDialog.setView(view);

        RecyclerView recyclerView = view.findViewById(R.id.dayPickerRecView);
        TextView monthYear = view.findViewById(R.id.monthYear);
        ImageView prevArrow = view.findViewById(R.id.calendar_prev_button);
        ImageView nextArrow = view.findViewById(R.id.calendar_next_button);

        monthYear.setText(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_19,TimeUtils.FORMAT_23,binding.date.getText().toString()));
        localDate = LocalDate.parse(binding.date.getText().toString(), DateTimeFormatter.ofPattern(TimeUtils.FORMAT_19));
        nextArrow.setEnabled(false);
        nextArrow.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.greater_than_gray, null));
        prevArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextArrow.setEnabled(true);
                nextArrow.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.greater_than_black, null));
                localDate = localDate.minusMonths(1);
                monthYear.setText(monthYearFromDate(localDate,TimeUtils.FORMAT_23));
                daysArrayList = daysInMonthArray(localDate);
                populateCalendarAdapter(recyclerView);
            }
        });

        nextArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                localDate = localDate.plusMonths(1);
                monthYear.setText(monthYearFromDate(localDate,TimeUtils.FORMAT_23));
                daysArrayList = daysInMonthArray(localDate);
                populateCalendarAdapter(recyclerView);
                if(LocalDate.now().equals(localDate)){
                    nextArrow.setEnabled(false);
                    nextArrow.setImageDrawable(ResourcesCompat.getDrawable(getResources(), R.drawable.greater_than_gray, null));
                }
            }
        });
        populateCalendarAdapter(recyclerView);
        calendarDialog = alertDialog.create();
        calendarDialog.show();

    }

    public void populateCalendarAdapter(RecyclerView recyclerView){
        calendarAdapter = new CalendarAdapter(daysArrayList, getContext(), new OnDayClickInterface() {
            @Override
            public void onDayClicked(int position, String date, ModelClass modelClass) {
                calendarDialog.cancel();
                getData(monthYearFromDate(localDate,TimeUtils.FORMAT_24) + "-" + date);
            }
        });
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getContext(),7);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(calendarAdapter);

    }

    public void getData(String date){

        binding.progressBar.setVisibility(View.VISIBLE);
        if(UtilityClass.isNetworkAvailable(requireContext())){

//                apiInterface = RetrofitClient.getRetrofit(ReportsActivity.this, SharedPref.getCallApiUrl(ReportsActivity.this));
            apiInterface = RetrofitClient.getRetrofit(getContext(), "http://sanffa.info/server/db_native_app.php/?");

            Map<String,String> map = new HashMap<>();
//                map.put("divisionCode", loginResponse.getDivision_Code());
//                map.put("rptSF", loginResponse.getSF_Code());
//                map.put("rSF", loginResponse.getSF_Code());
//                map.put("axn", "get/DayReports");
//                map.put("sfCode", loginResponse.getSF_Code());
            map.put("divisionCode", "25");
            map.put("rptSF", "MGR2240");
            map.put("rSF", "MGR2240");
            map.put("sfCode", "MGR2240");
            map.put("axn", "get/DayReports");
            map.put("rptDt", date);

            Call<JsonElement> call = apiInterface.getDayReport("", map);
            call.enqueue(new Callback<JsonElement>() {
                @Override
                public void onResponse(@NonNull Call<JsonElement> call, @NonNull Response<JsonElement> response) {
                    binding.progressBar.setVisibility(View.GONE);
                    try {
                        if(response.isSuccessful() && response.body() != null){
                            JsonElement jsonElement = response.body();
                            JSONArray jsonArray = new JSONArray();
                            if(jsonElement.isJsonArray()){
                                jsonArray = new JSONArray(jsonElement.getAsJsonArray().toString());
                            }
                            Type type = new TypeToken<ArrayList<DayReportModel>>(){}.getType();
                            arrayList = new Gson().fromJson(jsonArray.toString(),type);
                            dataViewModel.saveSummaryData(jsonArray.toString());
                            dataViewModel.saveDate(date);
                            populateAdapter();
                        }
                    }catch (JSONException e){
                        throw new RuntimeException(e);
                    }

                }

                @Override
                public void onFailure(@NonNull Call<JsonElement> call, @NonNull Throwable t) {
                    binding.progressBar.setVisibility(View.GONE);

                }
            });
        }else{
            binding.progressBar.setVisibility(View.GONE);
            Toast.makeText(getContext(), "Internet is not available", Toast.LENGTH_SHORT).show();
        }

    }

    public void populateAdapter(){
        dayReportAdapter = new DayReportAdapter(arrayList, getContext());
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
        binding.dayReportRecView.setLayoutManager(layoutManager);
        binding.dayReportRecView.setAdapter(dayReportAdapter);
    }

}