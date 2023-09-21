package saneforce.sanclm;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import saneforce.sanclm.Utils.TimeUtils;
import saneforce.sanclm.Activities.HomeScreen.HomeDashBoard;

public class Leave_Application extends AppCompatActivity {
    TextView Fromdate, Todate;
    TextView Head_Ltype, Head_fromdate, Head_todate,c_val,c_val1,c_val2,c_val_tol,c_val_tol_1,c_val_tol_2;
    String from_dt, To_dt, CURRENT_DATE;
    Date dateBefore;
    PieChart pieChart, pieChart1, pieChart2;
    PieDataSet pieDataSet, pieDataSet1, pieDataSet2;
    PieData pieData, pieData1, pieData2;
    ListView leave_request;
    ImageView back_btn;

     String[] Array = {"Sep 13, 2023","Sep 14, 2023","Sep 15, 2023"};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        setContentView(R.layout.activity_leave_application);

//        setContentView(R.layout.activity_main);
        Fromdate = findViewById(R.id.et_from_date);
        Todate = findViewById(R.id.et_to_date);
        pieChart = findViewById(R.id.piechart);
        pieChart1 = findViewById(R.id.piechart1);
        pieChart2 = findViewById(R.id.piechart2);
        Head_Ltype = findViewById(R.id.Head_Ltype);
        Head_todate = findViewById(R.id.Head_todate);
        Head_fromdate = (TextView) findViewById(R.id.Head_fromdate);
        c_val = (TextView) findViewById(R.id.c_val);
        c_val_tol = (TextView) findViewById(R.id.c_val_tol);
        c_val1 =  findViewById(R.id.c_val_1);
        c_val_tol_1 =  findViewById(R.id.c_val_tol_1);
        c_val2 = findViewById(R.id.c_val_2);
        c_val_tol_2 = findViewById(R.id.c_val_tol_2);
        back_btn = findViewById(R.id.back_btn);

        back_btn.setOnClickListener( v->{
            Intent l = new Intent(Leave_Application.this, HomeDashBoard.class);
            startActivity(l);

        });

        String colorText = "<font color=\"#85929e\">" + "Leave Date From" + "</font>"
                + " " + "<font color=\"#F1536E\">" + "*";
        String colorText1 = "<font color=\"#85929e\">" + "Leave Date To" + "</font>"
                + " " + "<font color=\"#F1536E\">" + "*";
        String colorText2 = "<font color=\"#85929e\">" + "Leave Type" + "</font>"
                + " " + "<font color=\"#F1536E\">" + "*";

        Head_fromdate.setText(Html.fromHtml(colorText));
        Head_todate.setText(Html.fromHtml(colorText1));
        Head_Ltype.setText(Html.fromHtml(colorText2));

        CURRENT_DATE = TimeUtils.GetCurrentDateTime(TimeUtils.FORMAT_4);
        Fromdate.setOnClickListener(v -> {
            final Calendar c = Calendar.getInstance(Locale.getDefault());
            int mYear = c.get(Calendar.YEAR);
            int mMonth = c.get(Calendar.MONTH);
            int mDay = c.get(Calendar.DAY_OF_MONTH);
            String currentDatest;
            SimpleDateFormat sddf = new SimpleDateFormat("dd/MM/yyyy");
            currentDatest = sddf.format(new Date());
            DatePickerDialog datePickerDialog = new DatePickerDialog(Leave_Application.this,
                    (view, year, monthOfYear, dayOfMonth) -> {
                        if ((monthOfYear + 1) < 10) {
                            if (dayOfMonth < 10) {
                                from_dt = year + "-0" + (monthOfYear + 1) + "-0" + dayOfMonth;
                            } else {
                                from_dt = year + "-0" + (monthOfYear + 1) + "-" + dayOfMonth;
                            }
                        } else {
                            if (dayOfMonth < 10) {
                                from_dt = year + "-" + (monthOfYear + 1) + "-0" + dayOfMonth;
                            } else {
                                from_dt = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                            }
                        }
                        DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                        DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                        String inputDateStr = from_dt;
                        Date date = null;
                        try {
                            date = inputFormat.parse(inputDateStr);
                            dateBefore = inputFormat.parse(from_dt);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                        String outputDateStr1 = outputFormat.format(date);
                        Fromdate.setText(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_12, outputDateStr1));
                        Todate.setText("");
                    }, mYear, mMonth, mDay);
            datePickerDialog.show();

        });


        Todate.setOnClickListener(v -> {
            if (Fromdate.getText().toString().equals("")) {
                Toast.makeText(Leave_Application.this, "Select from date", Toast.LENGTH_LONG).show();
            } else {
                if (CURRENT_DATE.equalsIgnoreCase(from_dt)) {//tv_to_date
                    final Calendar c = Calendar.getInstance(Locale.getDefault());
                    DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                    try {
                        c.setTime(inputFormat.parse(from_dt));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    int mYear = c.get(Calendar.YEAR);
                    int mMonth = c.get(Calendar.MONTH);
                    int mDay = c.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                            new DatePickerDialog.OnDateSetListener() {
                                @Override
                                public void onDateSet(DatePicker view, int year,
                                                      int monthOfYear, int dayOfMonth) {
                                    To_dt = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                    DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                                    String inputDateStr = To_dt;
                                    Date date = null;
                                    try {
                                        date = inputFormat.parse(inputDateStr);
                                        dateBefore = inputFormat.parse("");
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    String outputDateStr = outputFormat.format(date);
                                    Todate.setText(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_12, outputDateStr));
                                    Log.d("today_date", outputDateStr);
                                }
                            }, mYear, mMonth, mDay);
                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
                    datePickerDialog.show();
                } else {
                    final Calendar c = Calendar.getInstance(Locale.getDefault());
                    int mYear = c.get(Calendar.YEAR);
                    int mMonth = c.get(Calendar.MONTH);
                    int mDay = c.get(Calendar.DAY_OF_MONTH);
                    String currentDatest;
                    SimpleDateFormat sddf = new SimpleDateFormat("dd/MM/yyyy");
                    currentDatest = sddf.format(new Date());


                    DatePickerDialog datePickerDialog = new DatePickerDialog(Leave_Application.this,
                            (view, year, monthOfYear, dayOfMonth) -> {
                                if ((monthOfYear + 1) < 10) {
                                    if (dayOfMonth < 10) {
                                        To_dt = year + "-0" + (monthOfYear + 1) + "-0" + dayOfMonth;
                                    } else {
                                        To_dt = year + "-0" + (monthOfYear + 1) + "-" + dayOfMonth;
                                    }
                                } else {
                                    if (dayOfMonth < 10) {
                                        To_dt = year + "-" + (monthOfYear + 1) + "-0" + dayOfMonth;
                                    } else {
                                        To_dt = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                                    }
                                }
                                DateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                                DateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
                                String inputDateStr = To_dt;
                                Date date = null;

                                try {
                                    date = inputFormat.parse(inputDateStr);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                String outputDateStr = outputFormat.format(date);
                                Todate.setText(TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_12, outputDateStr));



                                Log.d("today_date", outputDateStr);
                            }, mYear, mMonth, mDay);

                    long milliseconds = dateBefore.getTime();
                    datePickerDialog.getDatePicker().setMinDate(milliseconds);


                    datePickerDialog.show();
                }
            }
        });

        Showcasual_PieChart("5", "4", "3");
        Showcasual_PieChart2("8", "1", "5");
        Showcasual_PieChart3("4", "1", "8");
    }



    public void Showcasual_PieChart(String achie, String Target, String Sale) {
        ArrayList<Integer> colors = new ArrayList<>();
        String Achievment = achie;
        if (Achievment.contains("-")) {
            int F1 = Integer.parseInt((Achievment));
            if (F1 > 12) {
                Achievment = "12";
            } else if (F1 < -12) {
                Achievment = "12";
            } else {
                Achievment = Achievment.replace("-", "");
            }
            colors.add(Color.rgb(217, 217, 217));
            colors.add(Color.rgb(0, 198, 137));
        } else {
            int F1 = Integer.parseInt((Achievment));
            if (F1 > 12) {
                Achievment = "12";
            }
            colors.add(Color.rgb(217, 217, 217));
            colors.add(Color.rgb(0, 198, 137));
        }
        int countdata1 = Integer.parseInt((Achievment));
        int countdata2 = Integer.parseInt((achie));
        String centertxt = String.valueOf(countdata2);
        int seconddata = (12 - countdata1);
        ArrayList<PieEntry> values = new ArrayList<PieEntry>();
        PieEntry pieEntry = new PieEntry(countdata1, "");
        values.add(pieEntry);
        PieEntry pieEntry1 = new PieEntry(seconddata, "");
        values.add(pieEntry1);

        pieDataSet = new PieDataSet(values, "");
        pieData = new PieData(pieDataSet);
        //pieData_1.setValueFormatter(new PercentFormatter());
        pieChart.setData(pieData);
        pieChart.setUsePercentValues(false);
        pieChart.setDrawHoleEnabled(true);
        pieChart.setCenterTextSize(15f);
        pieChart.setCenterTextColor(Color.rgb(0, 0, 0));
        pieChart.setTransparentCircleRadius(40f);
        pieChart.setHoleRadius(90f);
        pieDataSet.setColors(colors);
        pieData.setValueTextSize(0f);
        pieData.setValueTextColor(Color.WHITE);
        pieChart.animateXY(1400, 1400);
        c_val.setText(centertxt);
        c_val_tol.setText("/12");



//        pieChart.setCenterText(centertxt );
//        pieChart.setCenterTextSize(17);
        Description description = pieChart.getDescription();
        description.setEnabled(false);
        Legend legend = pieChart.getLegend();
        legend.setEnabled(false);

        Double D1 = Double.parseDouble(Target);
        Double D2 = Double.parseDouble(Sale);
        double scale3 = Math.pow(10, 3);

        String mTarget = "₹ " + Math.round(D1 * scale3) / scale3 + "L";
        String mSale = "₹ " + Math.round(D2 * scale3) / scale3 + "L";


        String Total = mSale + "/ " + mTarget;
    }


    public void Showcasual_PieChart2(String achie, String Target, String Sale) {
        ArrayList<Integer> colors = new ArrayList<>();
        String Achievment = achie;
        if (Achievment.contains("-")) {
            int F1 = Integer.parseInt((Achievment));
            if (F1 > 12) {
                Achievment = "12";
            } else if (F1 < -12) {
                Achievment = "12";
            } else {
                Achievment = Achievment.replace("-", "");
            }
            colors.add(Color.rgb(217, 217, 217));
            colors.add(Color.rgb(61, 165, 244));
        } else {
            int F1 = Integer.parseInt((Achievment));
            if (F1 > 12) {
                Achievment = "12";
            }
            colors.add(Color.rgb(217, 217, 217));
            colors.add(Color.rgb(61, 165, 244));
        }
        int countdata1 = Integer.parseInt((Achievment));
        int countdata2 = Integer.parseInt((achie));
        String centertxt = String.valueOf(countdata2);
        int seconddata = (12 - countdata1);
        ArrayList<PieEntry> values = new ArrayList<PieEntry>();
        PieEntry pieEntry = new PieEntry(countdata1, "");
        values.add(pieEntry);
        PieEntry pieEntry1 = new PieEntry(seconddata, "");
        values.add(pieEntry1);

        pieDataSet1 = new PieDataSet(values, "");
        pieData1 = new PieData(pieDataSet1);
        //pieData_1.setValueFormatter(new PercentFormatter());
        pieChart1.setData(pieData1);
        pieChart1.setUsePercentValues(false);
        pieChart1.setDrawHoleEnabled(true);
        pieChart1.setCenterTextSize(15f);
        pieChart1.setCenterTextColor(Color.rgb(0, 0, 0));
        pieChart1.setTransparentCircleRadius(40f);
        pieChart1.setHoleRadius(90f);
        pieDataSet1.setColors(colors);
        pieData1.setValueTextSize(0f);
        pieData1.setValueTextColor(Color.WHITE);
        pieChart1.animateXY(1400, 1400);

        c_val1.setText(centertxt);
        c_val_tol_1.setText("/12");

//        pieChart1.setCenterText(centertxt);
//        pieChart1.setCenterTextSize(18);
        Description description = pieChart1.getDescription();
        description.setEnabled(false);
        Legend legend = pieChart1.getLegend();
        legend.setEnabled(false);

        Double D1 = Double.parseDouble(Target);
        Double D2 = Double.parseDouble(Sale);
        double scale3 = Math.pow(10, 3);

        String mTarget = "₹ " + Math.round(D1 * scale3) / scale3 + "L";
        String mSale = "₹ " + Math.round(D2 * scale3) / scale3 + "L";


        String Total = mSale + "/ " + mTarget;
    }

    public void Showcasual_PieChart3(String achie, String Target, String Sale) {
        ArrayList<Integer> colors = new ArrayList<>();
        String Achievment = achie;
        if (Achievment.contains("-")) {
            int F1 = Integer.parseInt((Achievment));
            if (F1 > 8) {
                Achievment = "8";
            } else if (F1 < -8) {
                Achievment = "8";
            } else {
                Achievment = Achievment.replace("-", "");
            }
            colors.add(Color.rgb(217, 217, 217));
            colors.add(Color.rgb(241, 83, 110));
        } else {
            int F1 = Integer.parseInt((Achievment));
            if (F1 > 8) {
                Achievment = "8";
            }
            colors.add(Color.rgb(217, 217, 217));
            colors.add(Color.rgb(241, 83, 110));
        }
        int countdata1 = Integer.parseInt((Achievment));
        int countdata2 = Integer.parseInt((achie));
        String centertxt = String.valueOf(countdata2);
        int seconddata = (8 - countdata1);
        ArrayList<PieEntry> values = new ArrayList<PieEntry>();
        PieEntry pieEntry = new PieEntry(countdata1, "");
        values.add(pieEntry);
        PieEntry pieEntry1 = new PieEntry(seconddata, "");
        values.add(pieEntry1);

        pieDataSet2 = new PieDataSet(values, "");
        pieData2 = new PieData(pieDataSet2);
        //pieData_1.setValueFormatter(new PercentFormatter());
        pieChart2.setData(pieData2);
        pieChart2.setUsePercentValues(false);
        pieChart2.setDrawHoleEnabled(true);
        pieChart2.setCenterTextSize(15f);
        pieChart2.setCenterTextColor(Color.rgb(0, 0, 0));
        pieChart2.setTransparentCircleRadius(40f);
        pieChart2.setHoleRadius(90f);
        pieDataSet2.setColors(colors);
        pieData2.setValueTextSize(0f);
        pieData2.setValueTextColor(Color.WHITE);
        pieChart2.animateXY(1400, 1400);



        c_val2.setText(centertxt);
        c_val_tol_2.setText("/8");


//        pieChart2.setCenterText(centertxt);
//        pieChart2.setCenterTextSize(18);
        Description description = pieChart2.getDescription();
        description.setEnabled(false);
        Legend legend = pieChart2.getLegend();
        legend.setEnabled(false);

        Double D1 = Double.parseDouble(Target);
        Double D2 = Double.parseDouble(Sale);
        double scale3 = Math.pow(10, 3);

        String mTarget = "₹ " + Math.round(D1 * scale3) / scale3 + "L";
        String mSale = "₹ " + Math.round(D2 * scale3) / scale3 + "L";


        String Total = mSale + "/ " + mTarget;
    }
}