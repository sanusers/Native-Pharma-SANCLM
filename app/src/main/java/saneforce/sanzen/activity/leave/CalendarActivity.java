package saneforce.sanzen.activity.leave;

import static saneforce.sanzen.activity.leave.Leave_Application.List_LeaveDates;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.utility.TimeUtils;

public class CalendarActivity extends AppCompatActivity implements CalendarAdapter.OnItemListener {
    String frm_date;
    ArrayList<String> listdate = new ArrayList<>();
    private TextView monthYearText;
    private RecyclerView calendarRecyclerView;
    private LocalDate selectedDate, dateval, date1val, Afterdate, date;
    private LocalDate date1;
    CommonUtilsMethods commonUtilsMethods;

    @SuppressLint({"NewApi", "AppCompatMethod"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_calender);
        commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());
        commonUtilsMethods.setUpLanguage(getApplicationContext());
        Intent intent = getIntent();
        String selectefromdDate = intent.getStringExtra("selectefromdDate");
        if (!Leave_Application.leavebinding.etFromDate.getText().toString().equals("")) {
            date1 = LocalDate.parse((TimeUtils.GetConvertedDate(TimeUtils.FORMAT_18, TimeUtils.FORMAT_4, Leave_Application.leavebinding.etFromDate.getText().toString())));

        }

        frm_date = selectefromdDate;
        initWidgets();
        selectedDate = LocalDate.now();
        setMonthView();
    }

    private void initWidgets() {
        calendarRecyclerView = findViewById(R.id.calendarRecyclerView);
        monthYearText = findViewById(R.id.monthYearTV);
    }

    private void setMonthView() {
        if (frm_date.equals("1")) {
            monthYearText.setText(monthYearFromDate(selectedDate));
            ArrayList<String> daysInMonth = daysInMonthArray(selectedDate);
            CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this, selectedDate, monthYearFromDate(selectedDate), "", "1"); // Pass selectedDate
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
            calendarRecyclerView.setLayoutManager(layoutManager);
            calendarRecyclerView.setAdapter(calendarAdapter);
        } else {
            monthYearText.setText(monthYearFromDate(date1));
            ArrayList<String> daysInMonth = daysInMonthArray(date1);
            CalendarAdapter calendarAdapter = new CalendarAdapter(daysInMonth, this, date1, monthYearFromDate(date1), Leave_Application.leavebinding.etFromDate.getText().toString(), "2"); // Pass selectedDate
            RecyclerView.LayoutManager layoutManager = new GridLayoutManager(getApplicationContext(), 7);
            calendarRecyclerView.setLayoutManager(layoutManager);
            calendarRecyclerView.setAdapter(calendarAdapter);
        }
    }

    @SuppressLint("NewApi")
    private ArrayList<String> daysInMonthArray(LocalDate date) {
        ArrayList<String> daysInMonthArray = new ArrayList<>();

        YearMonth yearMonth = YearMonth.from(date);
        int daysInMonth = yearMonth.lengthOfMonth();
        LocalDate firstOfMonth = date.withDayOfMonth(1);
        int dayOfWeek = firstOfMonth.getDayOfWeek().getValue();
        LocalDate today = LocalDate.now();
        ImageView bfr_month = findViewById(R.id.bfr_month);

        int currentMonth = today.getMonthValue();
        int currentyear = today.getDayOfYear();

        if (frm_date.equals("1")) {
            bfr_month.setVisibility(View.VISIBLE);

        } else {
            if (frm_date.equals("2")) {
                LocalDate today1 = LocalDate.parse((TimeUtils.GetConvertedDate(TimeUtils.FORMAT_18, TimeUtils.FORMAT_4, Leave_Application.leavebinding.etFromDate.getText().toString())));
                int fromdate = today1.getMonthValue();
                int fromyear = today1.getYear();
                YearMonth yearMonth1 = YearMonth.from(date1);//2023-11

                if (yearMonth1.equals(yearMonth1)) {

                }

                if (date1.getMonthValue() == fromdate && date1.getYear() == fromyear) {
                    bfr_month.setVisibility(View.GONE);
                } else {
                    bfr_month.setVisibility(View.VISIBLE);
                }
            }
        }

        // Add empty cells before the start of the month
        for (int i = 1; i < dayOfWeek; i++) {
            daysInMonthArray.add("");
        }

        for (int i = 1; i <= daysInMonth; i++) {
            daysInMonthArray.add(String.valueOf(i));
        }
        // Add empty cells after the end of the month
        int totalCells = 6 * 7; // 6 rows x 7 columns
        while (daysInMonthArray.size() < totalCells) {
            daysInMonthArray.add("");
        }


        return daysInMonthArray;
    }

    @SuppressLint("NewApi")
    private String monthYearFromDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return date.format(formatter);
    }

    @SuppressLint("NewApi")
    public void previousMonthAction(View view) {

        if (frm_date.equals("1")) {
            selectedDate = selectedDate.minusMonths(1);
            setMonthView();
        } else {
            date1 = date1.minusMonths(1);
            setMonthView();
        }

    }

    @SuppressLint("NewApi")
    public void nextMonthAction(View view) {
        if (frm_date.equals("1")) {
            selectedDate = selectedDate.plusMonths(1);
            setMonthView();
        } else {
            date1 = date1.plusMonths(1);
            setMonthView();
        }


    }

    @SuppressLint("NotifyDataSetChanged")
    public void onItemClick(int position, String dayText) {
        if (!dayText.equals("")) {
            String from_val1, from_val;

            List_LeaveDates.clear();
            Leavedetails_adapter l_details = new Leavedetails_adapter(this, List_LeaveDates);
            LinearLayoutManager LayoutManagerpoc = new LinearLayoutManager(CalendarActivity.this);
            Leave_Application.leavebinding.leaveDetails.setLayoutManager(LayoutManagerpoc);
            Leave_Application.leavebinding.leaveDetails.setItemAnimator(new DefaultItemAnimator());
            Leave_Application.leavebinding.leaveDetails.setAdapter(l_details);
            l_details.notifyDataSetChanged();

            if (Leave_Application.leavebinding.etFromDate.getText().toString().equals("") || frm_date.equals("1")) {


                List_LeaveDates.clear();
                Leave_Application.leavebinding.etFromDate.setText("");
                Leave_Application.leavebinding.etToDate.setText("");
                Leave_Application.leavebinding.LeaveType.setText("");
                Leave_Application.leavebinding.lDays.setText("");
                Leave_Application.leavebinding.balanceDays.setText("");


                String date_sel = dayText + " " + monthYearFromDate(selectedDate);//yyyy-mm-dd
                from_val = (TimeUtils.GetConvertedDate(TimeUtils.FORMAT_17, TimeUtils.FORMAT_18, date_sel));
                dateval = LocalDate.parse((TimeUtils.GetConvertedDate(TimeUtils.FORMAT_17, TimeUtils.FORMAT_4, date_sel)));
                LocalDate currentDate = LocalDate.now();
                int day = Integer.parseInt(dayText);

//                if ((day == currentDate.getDayOfMonth() && (currentDate.getMonth()).equals(dateval.getMonth()) && currentDate.getDayOfYear() == dateval.getDayOfYear()) ||
//                        (day <= currentDate.getDayOfMonth() && currentDate.getMonthValue() <= dateval.getMonthValue() ) ||
//                        (day >= currentDate.getDayOfMonth() && currentDate.getMonthValue() <= dateval.getMonthValue() )) {
                Leave_Application.leavebinding.etToDate.setText("");
                Leave_Application.leavebinding.etFromDate.setText(from_val);
                finish();
//                } else {
////                        Toast.makeText(calender_screen.this, "Not Select Date", Toast.LENGTH_SHORT).show();
//                }

            } else {
                if (frm_date.equals("2")) {
                    Leave_Application.leavebinding.etToDate.setText("");
                    Leave_Application.leavebinding.LeaveType.setText("");
                    Leave_Application.leavebinding.lDays.setText("");
                    Leave_Application.leavebinding.balanceDays.setText("");
                    listdate.clear();
                    String date_sel = dayText + " " + monthYearFromDate(date1);//yyyy-mm-dd

                    date1val = LocalDate.parse((TimeUtils.GetConvertedDate(TimeUtils.FORMAT_17, TimeUtils.FORMAT_4, date_sel)));

                    String date_sel1 = dayText + " " + monthYearFromDate(date1val);//yyyy-mm-dd
                    from_val1 = (TimeUtils.GetConvertedDate(TimeUtils.FORMAT_17, TimeUtils.FORMAT_18, date_sel1));
                    int day = Integer.parseInt(dayText);
                    Afterdate = LocalDate.parse((TimeUtils.GetConvertedDate(TimeUtils.FORMAT_18, TimeUtils.FORMAT_4, Leave_Application.leavebinding.etFromDate.getText().toString())));

                    if (Afterdate.isBefore(date1val) || Afterdate.equals(date1val)) {
                        Leave_Application.leavebinding.etToDate.setText(from_val1);
                        finish();
                    } else {
//                        commonUtilsMethods.showToastMessage(CalendarActivity.this,"Unable to select" + "--" + Afterdate + "--" + date1val);
                    }
                }
            }
        }
    }

    //-----------------


}

