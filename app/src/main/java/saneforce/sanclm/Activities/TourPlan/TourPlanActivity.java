package saneforce.sanclm.Activities.TourPlan;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import saneforce.sanclm.databinding.ActivityTourPlanBinding;

public class TourPlanActivity extends AppCompatActivity {

    private ActivityTourPlanBinding binding;
    CalendarAdapter calendarAdapter;
    LocalDate selectedDate;


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTourPlanBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        selectedDate = LocalDate.now();
        populateAdapter();

        binding.calendarNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                Log.e("test","next month clicked");
                selectedDate = selectedDate.plusMonths(1);
                populateAdapter();
            }
        });

        binding.calendarPrevButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                Log.e("test","previous month clicked");

                selectedDate = selectedDate.minusMonths(1);
                populateAdapter();
            }
        });


    }

    private ArrayList<String> daysInMonthArray(LocalDate date)
    {
        ArrayList<String> daysInMonthArray = new ArrayList<>();
        YearMonth yearMonth = null;
            yearMonth = YearMonth.from(date);
            int daysInMonth = yearMonth.lengthOfMonth();

            LocalDate firstOfMonth = selectedDate.withDayOfMonth(1);
            int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();
            for(int i = 1; i <= 42; i++)
            {
                if(i <= dayOfWeek || i > daysInMonth + dayOfWeek)
                {
                    daysInMonthArray.add("");
                }
                else
                {
                    daysInMonthArray.add(String.valueOf(i - dayOfWeek));
                }
            }

        return  daysInMonthArray;
    }

    private String monthYearFromDate(LocalDate date)
    {
        DateTimeFormatter formatter = null;
        formatter = DateTimeFormatter.ofPattern("MMMM yyyy");

        return date.format(formatter);
    }

    public void populateAdapter(){
        binding.monthYear.setText(monthYearFromDate(selectedDate));
        ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);

        calendarAdapter = new CalendarAdapter(daysInMonth, TourPlanActivity.this, new OnDayClickInterface() {
            @Override
            public void onDayClicked (int position, String date) {
                if(!date.equals(""))
                {
                    String message = "Selected Date " + date + " " + monthYearFromDate(selectedDate);
                    Toast.makeText(TourPlanActivity.this, message, Toast.LENGTH_LONG).show();
                }
            }
        });
        RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
        binding.calendarRecView.setLayoutManager(layoutManager);
        binding.calendarRecView.setAdapter(calendarAdapter);
        calendarAdapter.notifyDataSetChanged();
    }

}