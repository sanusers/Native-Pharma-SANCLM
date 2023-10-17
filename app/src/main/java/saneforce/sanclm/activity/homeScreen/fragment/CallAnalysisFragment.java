package saneforce.sanclm.activity.homeScreen.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanclm.R;

import saneforce.sanclm.activity.homeScreen.HomeDashBoard;
import saneforce.sanclm.activity.homeScreen.view.CustomMarkerView;
import saneforce.sanclm.network.ApiInterface;
import saneforce.sanclm.network.RetrofitClient;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SharedPref;

public class CallAnalysisFragment extends Fragment implements View.OnClickListener {

    LinearLayout doc_layout, che_layout, stokiest_layout, unlistered_layout, ll_calls_layout, rl_linrchartmap;

    ApiInterface apiInterface;
    LineChart lineChart;
    SQLite sqLite;
    TextView doc_call_count,che_call_count,stockiest_call_count,unlisted_call_count,cip_call_count,hospital_call_count,txt_month_one,txt_month_two,txt_month_three,txt_doc_progress_value,txt_che_progress_value,txt_stockiest_progress_value,txt_Unlistered_progress_value;

    ProgressBar doc_progress_bar,che_progress_bar,stock_progress_bar,unlist_progress_bar;





    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.call_analysis_fagment, container, false);


        doc_layout = v.findViewById(R.id.ll_doc_child);
        che_layout = v.findViewById(R.id.ll_che_child);
        stokiest_layout = v.findViewById(R.id.ll_stock_child);
        unlistered_layout = v.findViewById(R.id.ll_unli_child);
        ll_calls_layout = v.findViewById(R.id.ll_calls_layout);
        rl_linrchartmap = v.findViewById(R.id.rl_linrchartmap);
        lineChart = v.findViewById(R.id.lineChart);
        doc_call_count=v.findViewById(R.id.txt_doc_count);
        che_call_count=v.findViewById(R.id.txt_che_count);
        stockiest_call_count=v.findViewById(R.id.txt_stock_count);
        unlisted_call_count=v.findViewById(R.id.txt_unlist_count);

        txt_month_one=v.findViewById(R.id.text_past2_month);
        txt_month_two=v.findViewById(R.id.text_past_month);
        txt_month_three=v.findViewById(R.id.text_current_month);
        txt_doc_progress_value=v.findViewById(R.id.txt_doc_value);
        txt_che_progress_value=v.findViewById(R.id.txt_che_value);
        txt_stockiest_progress_value=v.findViewById(R.id.txt_stock_value);
        txt_Unlistered_progress_value=v.findViewById(R.id.txt_unlisted_value);



        doc_progress_bar=v.findViewById(R.id.doc_progress_bar);
        che_progress_bar=v.findViewById(R.id.che_progress_bar);
        stock_progress_bar=v.findViewById(R.id.stock_progress_bar);
        unlist_progress_bar=v.findViewById(R.id.unlist_progress_bar);



        sqLite=new SQLite(requireContext());
        int width = (int) ((((HomeDashBoard.DeviceWith / 3) * 2) / 3)-30);
        LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.MATCH_PARENT);
        param.setMargins(0, 5, 10, 0);

        doc_layout.setLayoutParams(param);
        che_layout.setLayoutParams(param);
        stokiest_layout.setLayoutParams(param);
        unlistered_layout.setLayoutParams(param);

        doc_layout.setOnClickListener(this);
        che_layout.setOnClickListener(this);
        stokiest_layout.setOnClickListener(this);
        unlistered_layout.setOnClickListener(this);

        callDetails();

        ll_calls_layout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                HomeDashBoard.viewPager1.setScrollEnabled(false);
                return false;
            }
        });



        lineChart.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                HomeDashBoard.viewPager1.setScrollEnabled(true);
                return false;
            }
        });


        lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

            }

            @Override
            public void onNothingSelected() {

            }

        });

        return v;
    }




    private void  callDetails() {


        try {

            Map<String, String> QryParam = new HashMap<>();
            JSONObject paramObject = new JSONObject();

            paramObject.put("tableName", "gethome");
            paramObject.put("sfcode", SharedPref.getSfCode(requireContext()));
            paramObject.put("division_code", SharedPref.getDivisionCode(requireContext()));
            paramObject.put("Rsf", SharedPref.getSfCode(requireContext()));
            paramObject.put("sf_type", SharedPref.getSfType(requireContext()));
            paramObject.put("Designation", SharedPref.getDesignationName(requireContext()));
            paramObject.put("state_code", SharedPref.getStateCode(requireContext()));
            paramObject.put("subdivision_code", SharedPref.getSubdivCode(requireContext()));

            apiInterface = RetrofitClient.getRetrofit(requireContext(), "http://crm.saneforce.in/iOSServer/db_api.php/");
            Call<JsonArray> call = apiInterface.getcalldetails(paramObject.toString());
            call.enqueue(new Callback<JsonArray>() {
                @Override
                public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                    if (response.code() == 200) {
                        Log.e("Response",""+response.toString());
                        parseJsonCallalysis( response.body().toString());

                    }

                }

                @Override
                public void onFailure(Call<JsonArray> call, Throwable t) {
                    Log.e("Response","Error");
                }
            });

        }catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }


    private void parseJsonCallalysis(String response){

        sqLite.clearLinecharTable();
        try {
            JSONArray jsonArray=new JSONArray(response);

            if(jsonArray.length()>0){
                for (int i = 0; i < jsonArray.length(); i++) {

                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    String CustCode= jsonObject.optString("CustCode");
                    String CustType= jsonObject.optString("CustType");
                    String Dcr_dt= jsonObject.optString("Dcr_dt");
                    String month_name= jsonObject.optString("month_name");
                    String Mnth= jsonObject.optString("Mnth");
                    String Yr= jsonObject.optString("Yr");
                    String CustName= jsonObject.optString("CustName");
                    String town_code= jsonObject.optString("town_code");
                    String town_name= jsonObject.optString("town_name");
                    String Dcr_flag= jsonObject.optString("Dcr_flag");
                    String SF_Code= jsonObject.optString("SF_Code");
                    String Trans_SlNo= jsonObject.optString("Trans_SlNo");
                    String AMSLNo= jsonObject.optString("AMSLNo");
                    sqLite.insertLinecharData(CustCode,CustType,Dcr_dt,month_name,Mnth,Yr,CustName,town_code,town_name,Dcr_flag,SF_Code,Trans_SlNo,AMSLNo);

                }
            }
            JSONArray  Doctor_list = sqLite.getMasterSyncDataByKey("Doctor_" +  SharedPref.getSfCode(requireContext()));
            JSONArray  Chemist_list = sqLite.getMasterSyncDataByKey("Chemist_" +  SharedPref.getSfCode(requireContext()));
            JSONArray  Stockiest_list = sqLite.getMasterSyncDataByKey("Stockiest_" +  SharedPref.getSfCode(requireContext()));
            JSONArray  unlistered_list = sqLite.getMasterSyncDataByKey("Unlisted_Doctor_" +  SharedPref.getSfCode(requireContext()));

            int doc_current_callcount=sqLite.getcurrentmonth_calls_count("1");
            int che_current_callcount=sqLite.getcurrentmonth_calls_count("2");
            int stockiest_current_callcount=sqLite.getcurrentmonth_calls_count("3");
            int unlistered_current_callcount=sqLite.getcurrentmonth_calls_count("4");



            doc_call_count.setText(Doctor_list.length()+" / "+String.valueOf(doc_current_callcount));
            che_call_count.setText(Chemist_list.length()+" / "+String.valueOf(che_current_callcount));
            stockiest_call_count.setText( Stockiest_list.length()+" / "+String.valueOf(stockiest_current_callcount));
            unlisted_call_count.setText(unlistered_list.length()+" / "+String.valueOf(unlistered_current_callcount));

            int doc_progress_value,che_progress_value,stockiest_progress_value,unilistered_progress_value;

            doc_progress_value=computePercent(doc_current_callcount,Doctor_list.length());
            che_progress_value=computePercent(che_current_callcount,Chemist_list.length());
            stockiest_progress_value=computePercent(stockiest_current_callcount,Stockiest_list.length());
            unilistered_progress_value=computePercent(unlistered_current_callcount,unlistered_list.length());



            txt_doc_progress_value.setText(String.valueOf(doc_progress_value)+"%");
            txt_che_progress_value.setText(String.valueOf(che_progress_value)+"%");
            txt_stockiest_progress_value.setText(String.valueOf(stockiest_progress_value)+"%");
            txt_Unlistered_progress_value.setText(String.valueOf(unilistered_progress_value)+"%");

            doc_progress_bar.setProgress(doc_progress_value);
            che_progress_bar.setProgress(che_progress_value);
            stock_progress_bar.setProgress(stockiest_progress_value);
            unlist_progress_bar.setProgress(unilistered_progress_value);



            lineChart.clear();
            LinecharLoad("1");


        }catch (Exception a){
            a.printStackTrace();
        }





    }

    public static int computePercent(int current, int total) {
        int percent = 0;
        if (total > 0)
            percent = current * 100 / total;
        return percent;
    }


    void LinecharLoad(String Custype){

        Log.e("AAAAA","");

        SimpleDateFormat sdf = new SimpleDateFormat("dd");

        SimpleDateFormat sdfs = new SimpleDateFormat("MMMM");
        SimpleDateFormat sdfss = new SimpleDateFormat("yyyy-MM-dd");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -2);

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDate = calendar.getTime();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date lastDate = calendar.getTime();
        String First_Date_of_Two_Months_Ago = sdf.format(firstDate);
        String Last_Date_of_Two_Months_Ago = sdf.format(lastDate);

        calendar.set(Calendar.DAY_OF_MONTH, 15);
        Date fifteenthDate = calendar.getTime();

        calendar.set(Calendar.DAY_OF_MONTH, 16);
        Date sixteenthDate = calendar.getTime();

        String firstDateStr = sdfss.format(firstDate);
        String fifteenthDateStr = sdfss.format(fifteenthDate);
        String sixteenthDateStr =sdfss.format(sixteenthDate);
        String enddate= sdfss.format(lastDate);
        String month= String.valueOf(calendar.get(Calendar.MONTH)+1);





        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(calendar1.MONTH, -1);
        calendar1.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDate1 = calendar1.getTime();
        calendar1.set(Calendar.DAY_OF_MONTH, calendar1.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date lastDate1 = calendar1.getTime();
        String First_Date_of_pastmonth = sdf.format(firstDate1);
        String Last_Date_of_pastmonth = sdf.format(lastDate1);
        calendar1.set(Calendar.DAY_OF_MONTH, 15);
        Date fifteenthDate1 = calendar1.getTime();
        calendar1.set(Calendar.DAY_OF_MONTH, 16);
        Date sixteenthDate1 = calendar1.getTime();
        String firstDatepastmonth = sdfss.format(firstDate1);
        String fifteenthDatepastmonth = sdfss.format(fifteenthDate1);
        String sixteenthDatepastmonth =sdfss.format(sixteenthDate1);
        String enddatepastmonth= sdfss.format(lastDate1);
        String month1= String.valueOf(calendar1.get(Calendar.MONTH)+1);


        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(calendar1.DAY_OF_MONTH, 1);
        Date firstDate2 = calendar2.getTime();
        calendar2.set(Calendar.DAY_OF_MONTH, calendar2.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date lastDate2 = calendar2.getTime();
        String currentfirstdate = sdf.format(firstDate2);
        String cutrrentlastdate = sdf.format(lastDate2);
        calendar2.set(Calendar.DAY_OF_MONTH, 15);
        Date fifteenthDate2 = calendar2.getTime();

        calendar2.set(Calendar.DAY_OF_MONTH, 16);
        Date sixteenthDate2 = calendar2.getTime();

        String firstDatecurrent = sdfss.format(firstDate2);
        String fifteenthDatecurrent = sdfss.format(fifteenthDate2);
        String sixteenthDatecurrent =sdfss.format(sixteenthDate2);
        String enddatecurrent= sdfss.format(lastDate2);



     ArrayList<Entry> entries = new ArrayList<>();

     boolean ispast2month=sqLite.isMonthDataAvailableForCustType(Custype,month);
        boolean ispast1month=sqLite.isMonthDataAvailableForCustType(Custype,month1);



        if(ispast2month){

            txt_month_one.setText(sdfs.format(calendar.getTime()));
            txt_month_two.setText(sdfs.format(calendar1.getTime()));
            txt_month_three.setText(sdfs.format(calendar2.getTime()));

            txt_month_one.setVisibility(View.VISIBLE);
            txt_month_two.setVisibility(View.VISIBLE);
            txt_month_three.setVisibility(View.VISIBLE);


            int xaxis1=sqLite.getHalfMonthDataCount(firstDateStr,fifteenthDateStr,Custype);
            int xaxis2=sqLite.getHalfMonthDataCount(sixteenthDateStr,enddate,Custype);

            int xaxis3=sqLite.getHalfMonthDataCount(firstDatepastmonth,fifteenthDatepastmonth,Custype);
            int xaxis4=sqLite.getHalfMonthDataCount(sixteenthDatepastmonth,enddatepastmonth,Custype);

            int xaxis5=sqLite.getHalfMonthDataCount(firstDatecurrent,fifteenthDatecurrent,Custype);
            int xaxis6=sqLite.getHalfMonthDataCount(sixteenthDatecurrent,enddatecurrent,Custype);

            if(xaxis1!=0){
                entries.add(new Entry(1, xaxis1));
            }

            if(xaxis2!=0){
                entries.add(new Entry(2, xaxis2));
            }


            if(xaxis3!=0){
                entries.add(new Entry(3, xaxis3));
            }

            if(xaxis4!=0){
                entries.add(new Entry(4, xaxis4));
            }

            if(xaxis5!=0){
                entries.add(new Entry(5, xaxis5));
            }

            if(xaxis6!=0){
                entries.add(new Entry(6, xaxis6));
            }

        }else if(ispast1month){

            txt_month_one.setText(sdfs.format(calendar1.getTime()));
            txt_month_two.setText(sdfs.format(calendar2.getTime()));
            txt_month_three.setVisibility(View.INVISIBLE);
            txt_month_one.setVisibility(View.VISIBLE);
            txt_month_two.setVisibility(View.VISIBLE);

            int xaxis3=sqLite.getHalfMonthDataCount(firstDatepastmonth,fifteenthDatepastmonth,Custype);
            int xaxis4=sqLite.getHalfMonthDataCount(sixteenthDateStr,enddatepastmonth,Custype);

            int xaxis5=sqLite.getHalfMonthDataCount(firstDatecurrent,fifteenthDatecurrent,Custype);
            int xaxis6=sqLite.getHalfMonthDataCount(sixteenthDatecurrent,enddatecurrent,Custype);


            if(xaxis3!=0){
                entries.add(new Entry(1, xaxis3));
            }

            if(xaxis4!=0){
                entries.add(new Entry(2, xaxis4));
            }

            if(xaxis5!=0){
                entries.add(new Entry(3, xaxis5));
            }

            if(xaxis6!=0){
                entries.add(new Entry(4, xaxis6));
            }


        }else {

            txt_month_one.setText(sdfs.format(calendar2.getTime()));
            txt_month_two.setVisibility(View.INVISIBLE);
            txt_month_three.setVisibility(View.INVISIBLE);
            txt_month_one.setVisibility(View.VISIBLE);

            int xaxis5=sqLite.getHalfMonthDataCount(firstDatecurrent,fifteenthDatecurrent,Custype);
            int xaxis6=sqLite.getHalfMonthDataCount(sixteenthDatecurrent,enddatecurrent,Custype);

            if(xaxis5!=0){
                entries.add(new Entry(1, xaxis5));
            }

            if(xaxis6!=0){
                entries.add(new Entry(2,xaxis6));
            }
        }





        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setDrawValues(false);
        dataSet.setColor(Color.BLACK);
        dataSet.setCircleColor(Color.BLACK);
        dataSet.setCircleHoleColor(Color.BLACK) ;
        dataSet.setCircleRadius(3f);
        dataSet.disableDashedHighlightLine();
        dataSet.setDrawHighlightIndicators(true);
        dataSet.setDrawCircles(true);
        dataSet.setLineWidth(1f);
        dataSet.setHighLightColor(Color.WHITE);

        lineChart.getLegend().setEnabled(false);
        LineData lineData1 = new LineData(dataSet);
        lineChart.setData(lineData1);


        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(7f);
        xAxis.setLabelCount(8,true);
        xAxis.disableGridDashedLine();
        xAxis.setDrawGridLines(false);
        xAxis.setAvoidFirstLastClipping(true);

        final ArrayList<String> xAxisLabel = new ArrayList<> ();

        xAxisLabel.add("");
        xAxisLabel.add(First_Date_of_Two_Months_Ago + "st - 15th");
        xAxisLabel.add("16th-" + Last_Date_of_Two_Months_Ago + "th");
        xAxisLabel.add("");
        xAxisLabel.add("");
        xAxisLabel.add("");
        xAxisLabel.add("");

        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {



                if (ispast2month) {
                    if (value == 0f) {
                        return "";
                    } else if (value == 1f) {
                        return First_Date_of_Two_Months_Ago + "st - 15th";
                    } else if (value == 2f) {
                        return "16th-" + Last_Date_of_Two_Months_Ago + "th";
                    } else if (value == 3f) {
                        return First_Date_of_pastmonth + "st - 15th";
                    } else if (value == 4f) {
                        return "16th-" + Last_Date_of_pastmonth + "th";

                    } else if (value == 5f) {
                        return currentfirstdate + "st - 15th";
                    } else if (value == 6f) {
                        return "16th-" + cutrrentlastdate + "th";
                    } else {
                        return "";
                    }
                }else if(ispast1month){
                    if (value == 0f) {
                        return "";
                    }
                 else if (value == 1f) {
                        return First_Date_of_pastmonth + "st - 15th";
                    } else if (value == 2f) {
                        return "16th-" + Last_Date_of_pastmonth + "th";

                    } else if (value == 3f) {
                        return currentfirstdate + "st - 15th";
                    } else if (value == 4f) {
                        return "16th-" + cutrrentlastdate + "th";
                    } else {
                        return "";
                    }

                }else {

                    if (value == 0f) {
                        return "";

                    } else if (value == 1f) {
                        return currentfirstdate + "st - 15th";
                    } else if (value == 2f) {
                        return "16th-" + cutrrentlastdate + "th";
                    } else {
                        return "";
                    }
                }


            }
        });


        YAxis leftYAxis = lineChart.getAxisLeft();
        leftYAxis.removeAllLimitLines();
        leftYAxis.setAxisMinimum(0);
        leftYAxis.setAxisMaximum(100);
        leftYAxis.setLabelCount(6,true);
        leftYAxis.setDrawLimitLinesBehindData(true);
        leftYAxis.setDrawLabels(true);
        leftYAxis.enableGridDashedLine(5f, 5f, 0f);
        leftYAxis.setDrawAxisLine(false);
        leftYAxis.setDrawGridLines(true);
        leftYAxis.setDrawLimitLinesBehindData(true);
        leftYAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {

                if (value == 0f) {
                    return "";
                } else if (value == 20f) {
                    return "20";
                } else if (value == 40f) {
                    return "40";
                } else if (value == 60f) {
                    return "60";
                } else if (value == 80f) {
                    return "80";
                } else if (value == 100f) {
                    return "100";
                } else {
                    return "";
                }

            }
        });


        CustomMarkerView mv = new CustomMarkerView(requireContext(), R.layout.linechartpopup,Custype);
        mv.setChartView(lineChart);
        lineChart.setMarkerView(mv);
        YAxis rightAxis=lineChart.getAxisRight();
        rightAxis.setAxisLineColor(getResources().getColor(R.color.white));
        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawLabels(false);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.setPinchZoom(false);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.invalidate() ;
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(false);
        lineChart.setScaleEnabled(true);
        lineChart.setOnChartValueSelectedListener(null);
       lineChart.setHighlightPerTapEnabled(true);


       ;

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.ll_doc_child :
                lineChart.clear();
                LinecharLoad("1");

                break;
            case R.id.ll_che_child :
                lineChart.clear();
                LinecharLoad("2");
                break;
            case R.id.ll_stock_child :
                lineChart.clear();
                LinecharLoad("3");
                break;
            case R.id.ll_unli_child :
                lineChart.clear();
                LinecharLoad("4");

                break;

        }





    }
}


