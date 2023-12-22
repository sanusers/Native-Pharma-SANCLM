package saneforce.sanclm.activity.homeScreen.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.HomeDashBoard;
import saneforce.sanclm.activity.homeScreen.view.CustomMarkerView;
import saneforce.sanclm.activity.homeScreen.view.ImageLineChartRenderer;
import saneforce.sanclm.commonClasses.Constants;
import saneforce.sanclm.response.LoginResponse;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SharedPref;

public class CallAnalysisFragment extends Fragment implements View.OnClickListener {

    LinearLayout ll_doc_head, ll_chem_head, ll_stk_head, ll_ulDr_head, ll_cip_head, ll_hos_head, doc_layout, che_layout, stockiest_layout, unListed_layout, cip_layout, hos_layout, ll_calls_layout, rl_linrchartmap, ll_monthlayout;
    String SfType, SfCode, SfName, DivCode, Designation, StateCode, SubDivisionCode, DrNeed, ChemistNeed, CipNeed, StockistNeed, UnDrNeed, CapDr, CapChemist, CapStockist, CapCip, CapUnDr, CapHos, HospNeed;
    LineChart lineChart;
    TextView tvDocCap, tvChemCap, tvStkCap, tvUnDrCap, tvCipCap, tvHosCap, tvAverageTag, tvFromToMonth;
    SQLite sqLite;
    TextView doc_call_count, che_call_count, stockiest_call_count, unlisted_call_count, cip_call_count, hospital_call_count, txt_month_one, txt_month_two, txt_month_three, txt_doc_progress_value, txt_che_progress_value, txt_stockiest_progress_value, txt_Unlistered_progress_value, txt_cip_progress_value, txt_hos_progress_value;
    ProgressBar doc_progress_bar, che_progress_bar, stock_progress_bar, unlist_progress_bar, cip_progress_bar, hos_progress_bar;
    RelativeLayout rl_relative_layout;
    LoginResponse loginResponse;

    String key;
    ImageView doc_img_down_triangle, che_img_down_triangle, stock_img_down_triangle, unlisred_img_down_triangle, cip_img_down_triangle, hos_img_down_triangle;

    JSONArray dcrdatas;

    public static int computePercent(int current, int total) {
        int percent = 0;
        if (total > 0) percent = current * 100 / total;
        return percent;
    }

    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.call_analysis_fagment, container, false);
        Log.v("fragment", "callanalysis");

        doc_layout = v.findViewById(R.id.ll_doc_child);
        che_layout = v.findViewById(R.id.ll_che_child);
        stockiest_layout = v.findViewById(R.id.ll_stock_child);
        unListed_layout = v.findViewById(R.id.ll_unli_child);
        cip_layout = v.findViewById(R.id.ll_cip_child);
        hos_layout = v.findViewById(R.id.ll_hos_child);
        ll_monthlayout = v.findViewById(R.id.ll_monthlayout);
        ll_calls_layout = v.findViewById(R.id.ll_calls_layout);
        rl_linrchartmap = v.findViewById(R.id.rl_linrchartmap);
        lineChart = v.findViewById(R.id.lineChart);

        ll_doc_head = v.findViewById(R.id.ll_doc_head);
        ll_chem_head = v.findViewById(R.id.ll_chem_head);
        ll_stk_head = v.findViewById(R.id.ll_stock_head);
        ll_ulDr_head = v.findViewById(R.id.ll_unli_head);
        ll_cip_head = v.findViewById(R.id.ll_cip_head);
        ll_hos_head = v.findViewById(R.id.ll_hos_head);

        tvDocCap = v.findViewById(R.id.txt_doc_name);
        tvChemCap = v.findViewById(R.id.txt_che_name);
        tvStkCap = v.findViewById(R.id.txt_stock_name);
        tvUnDrCap = v.findViewById(R.id.txt_unli_name);
        tvCipCap = v.findViewById(R.id.txt_cip_name);
        tvHosCap = v.findViewById(R.id.txt_hos_name);
        tvAverageTag = v.findViewById(R.id.text_average_);
        tvFromToMonth = v.findViewById(R.id.text_date);

        doc_call_count = v.findViewById(R.id.txt_doc_count);
        che_call_count = v.findViewById(R.id.txt_che_count);
        stockiest_call_count = v.findViewById(R.id.txt_stock_count);
        unlisted_call_count = v.findViewById(R.id.txt_unlist_count);
        cip_call_count = v.findViewById(R.id.txt_cip__count);
        hospital_call_count = v.findViewById(R.id.txt_hos_count);

        rl_relative_layout = v.findViewById(R.id.call_analysis_layout);

        doc_img_down_triangle = v.findViewById(R.id.img_down_triangle_doc);
        che_img_down_triangle = v.findViewById(R.id.img_down_triangle_che);
        stock_img_down_triangle = v.findViewById(R.id.img_down_triangle_stockiest);
        unlisred_img_down_triangle = v.findViewById(R.id.img_down_triangle_unlistered);
        cip_img_down_triangle = v.findViewById(R.id.img_down_triangle_cip_);
        hos_img_down_triangle = v.findViewById(R.id.img_down_triangle_hos);

        txt_month_one = v.findViewById(R.id.text_past2_month);
        txt_month_two = v.findViewById(R.id.text_past_month);
        txt_month_three = v.findViewById(R.id.text_current_month);
        txt_doc_progress_value = v.findViewById(R.id.txt_doc_value);
        txt_che_progress_value = v.findViewById(R.id.txt_che_value);
        txt_stockiest_progress_value = v.findViewById(R.id.txt_stock_value);
        txt_Unlistered_progress_value = v.findViewById(R.id.txt_unlisted_value);
        txt_cip_progress_value = v.findViewById(R.id.txt_cip__value);
        txt_hos_progress_value = v.findViewById(R.id.txt_hos_value);

        doc_progress_bar = v.findViewById(R.id.doc_progress_bar);
        che_progress_bar = v.findViewById(R.id.che_progress_bar);
        stock_progress_bar = v.findViewById(R.id.stock_progress_bar);
        unlist_progress_bar = v.findViewById(R.id.unlist_progress_bar);
        cip_progress_bar = v.findViewById(R.id.cip__progress_bar);
        hos_progress_bar = v.findViewById(R.id.hos_progress_bar);

        doc_img_down_triangle.setVisibility(View.VISIBLE);
        che_img_down_triangle.setVisibility(View.GONE);
        stock_img_down_triangle.setVisibility(View.GONE);
        unlisred_img_down_triangle.setVisibility(View.GONE);
        cip_img_down_triangle.setVisibility(View.GONE);
        hos_img_down_triangle.setVisibility(View.GONE);
        sqLite = new SQLite(requireContext());

        doc_layout.setOnClickListener(this);
        che_layout.setOnClickListener(this);
        stockiest_layout.setOnClickListener(this);
        unListed_layout.setOnClickListener(this);
        cip_layout.setOnClickListener(this);
        hos_layout.setOnClickListener(this);

        getRequiredData();
        HiddenVisibleFunctions();
        callDetails();

        ViewTreeObserver vto = rl_relative_layout.getViewTreeObserver();

        vto.addOnGlobalLayoutListener(() -> {

            int getwidhtlayout = rl_relative_layout.getMeasuredWidth();
            int getlayoutlayout = rl_relative_layout.getMeasuredHeight();


            int width = (getwidhtlayout / 3 - 8);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(width, getlayoutlayout - getResources().getDimensionPixelSize(R.dimen._22sdp));
            param.setMargins(0, 5, 10, 0);
            doc_layout.setLayoutParams(param);
            che_layout.setLayoutParams(param);
            stockiest_layout.setLayoutParams(param);
            unListed_layout.setLayoutParams(param);
            cip_layout.setLayoutParams(param);
            hos_layout.setLayoutParams(param);


        });


        ll_calls_layout.setOnTouchListener((v1, event) -> {
            HomeDashBoard.viewPager1.setScrollEnabled(false);
            return false;
        });


        lineChart.setOnTouchListener((v12, event) -> {
            HomeDashBoard.viewPager1.setScrollEnabled(true);
            return false;
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

    private void HiddenVisibleFunctions() {
        if (DrNeed.equalsIgnoreCase("0")) {
            tvDocCap.setText(String.format("%s Calls", CapDr));
            tvAverageTag.setText(String.format("Average %s", tvDocCap.getText().toString()));
        }
        if (ChemistNeed.equalsIgnoreCase("0")) {
            ll_chem_head.setVisibility(View.VISIBLE);
            tvChemCap.setText(String.format("%s Calls", CapChemist));
        }
        if (StockistNeed.equalsIgnoreCase("0")) {
            ll_stk_head.setVisibility(View.VISIBLE);
            tvStkCap.setText(String.format("%s Calls", CapStockist));
        }
        if (UnDrNeed.equalsIgnoreCase("0")) {
            ll_ulDr_head.setVisibility(View.VISIBLE);
            tvUnDrCap.setText(String.format("%s Calls", CapUnDr));
        }
        if (CipNeed.equalsIgnoreCase("0")) {
            ll_cip_head.setVisibility(View.VISIBLE);
            tvCipCap.setText(String.format("%s Calls", CapCip));
        }
        if (HospNeed.equalsIgnoreCase("0")) {
            ll_hos_head.setVisibility(View.VISIBLE);
            tvHosCap.setText(String.format("%s Calls", CapHos));
        }
    }

    private void getRequiredData() {
        loginResponse = new LoginResponse();
        loginResponse = sqLite.getLoginData();

        SfType = loginResponse.getSf_type();
        SfCode = loginResponse.getSF_Code();
        SfName = loginResponse.getSF_Name();
        DivCode = loginResponse.getDivision_Code();
        Designation = loginResponse.getDesig();
        StateCode = loginResponse.getState_Code();
        SubDivisionCode = loginResponse.getSubdivision_code();

        CapDr = loginResponse.getDrCap();
        CapChemist = loginResponse.getChmCap();
        CapStockist = loginResponse.getStkCap();
        CapUnDr = loginResponse.getNLCap();
        CapCip = loginResponse.getCIP_Caption();
        CapHos = loginResponse.getHosp_caption();

        CipNeed = loginResponse.getCip_need();
        DrNeed = loginResponse.getDrNeed();
        ChemistNeed = loginResponse.getChmNeed();
        StockistNeed = loginResponse.getStkNeed();
        UnDrNeed = loginResponse.getUNLNeed();
        HospNeed = loginResponse.getHosp_need();
    }

    @SuppressLint("SetTextI18n")
    private void callDetails() {
        sqLite.clearLinecharTable();
        try {

            JSONArray Doctor_list = sqLite.getMasterSyncDataByKey(Constants.DOCTOR + SharedPref.getHqCode(requireContext()));
            JSONArray Chemist_list = sqLite.getMasterSyncDataByKey(Constants.CHEMIST + SharedPref.getHqCode(requireContext()));
            JSONArray Stockiest_list = sqLite.getMasterSyncDataByKey(Constants.STOCKIEST + SharedPref.getHqCode(requireContext()));
            JSONArray unlistered_list = sqLite.getMasterSyncDataByKey(Constants.UNLISTED_DOCTOR + SharedPref.getHqCode(requireContext()));
            JSONArray cip_list = sqLite.getMasterSyncDataByKey(Constants.CIP + SharedPref.getHqCode(requireContext()));
            JSONArray hos_list = sqLite.getMasterSyncDataByKey(Constants.HOSPITAL + SharedPref.getHqCode(requireContext()));

            dcrdatas = sqLite.getMasterSyncDataByKey(Constants.DCR);
            if (dcrdatas.length() > 0) {
                for (int i = 0; i < dcrdatas.length(); i++) {

                    JSONObject jsonObject = dcrdatas.getJSONObject(i);
                    String CustCode = jsonObject.optString("CustCode");
                    String CustType = jsonObject.optString("CustType");
                    String Dcr_dt = jsonObject.optString("Dcr_dt");
                    String month_name = jsonObject.optString("month_name");
                    String Mnth = jsonObject.optString("Mnth");
                    String Yr = jsonObject.optString("Yr");
                    String CustName = jsonObject.optString("CustName");
                    String town_code = jsonObject.optString("town_code");
                    String town_name = jsonObject.optString("town_name");
                    String Dcr_flag = jsonObject.optString("Dcr_flag");
                    String SF_Code = jsonObject.optString("SF_Code");
                    String Trans_SlNo = jsonObject.optString("Trans_SlNo");
                    String FW_Indicator = jsonObject.optString("FW_Indicator");
                    String AMSLNo = jsonObject.optString("AMSLNo");
                    sqLite.insertLinecharData(CustCode, CustType, Dcr_dt, month_name, Mnth, Yr, CustName, town_code, town_name, Dcr_flag, SF_Code, Trans_SlNo, AMSLNo, FW_Indicator);

                }


                int doc_current_callcount = sqLite.getcurrentmonth_calls_count("1");
                int che_current_callcount = sqLite.getcurrentmonth_calls_count("2");
                int stockiest_current_callcount = sqLite.getcurrentmonth_calls_count("3");
                int unlistered_current_callcount = sqLite.getcurrentmonth_calls_count("4");
                int cip_current_callcount = sqLite.getcurrentmonth_calls_count("5");
                int hos_current_callcount = sqLite.getcurrentmonth_calls_count("6");


                doc_call_count.setText(doc_current_callcount + " / " + Doctor_list.length());
                che_call_count.setText(che_current_callcount + " / " + Chemist_list.length());
                stockiest_call_count.setText(stockiest_current_callcount + " / " + Stockiest_list.length());
                unlisted_call_count.setText(unlistered_current_callcount + " / " + unlistered_list.length());
                cip_call_count.setText(cip_current_callcount + " / " + cip_list.length());
                hospital_call_count.setText(hos_current_callcount + " / " + hos_list.length());


                int doc_progress_value, che_progress_value, stockiest_progress_value, unlistered_progress_value, cip_progress_value, hos_progress_value;

                doc_progress_value = computePercent(doc_current_callcount, Doctor_list.length());
                che_progress_value = computePercent(che_current_callcount, Chemist_list.length());
                stockiest_progress_value = computePercent(stockiest_current_callcount, Stockiest_list.length());
                unlistered_progress_value = computePercent(unlistered_current_callcount, unlistered_list.length());
                cip_progress_value = computePercent(cip_current_callcount, cip_list.length());
                hos_progress_value = computePercent(hos_current_callcount, hos_list.length());

                txt_doc_progress_value.setText(doc_progress_value + "%");
                txt_che_progress_value.setText(che_progress_value + "%");
                txt_stockiest_progress_value.setText(stockiest_progress_value + "%");
                txt_Unlistered_progress_value.setText(unlistered_progress_value + "%");
                txt_cip_progress_value.setText(stockiest_progress_value + "%");
                txt_hos_progress_value.setText(unlistered_progress_value + "%");

                doc_progress_bar.setProgress(doc_progress_value);
                che_progress_bar.setProgress(che_progress_value);
                stock_progress_bar.setProgress(stockiest_progress_value);
                unlist_progress_bar.setProgress(unlistered_progress_value);
                cip_progress_bar.setProgress(cip_progress_value);
                hos_progress_bar.setProgress(hos_progress_value);


                lineChart.clear();
                setLineChartData("1");
                ll_monthlayout.setVisibility(View.VISIBLE);

            } else {
                ll_monthlayout.setVisibility(View.GONE);
                doc_layout.setOnClickListener(null);
                che_layout.setOnClickListener(null);
                stockiest_layout.setOnClickListener(null);
                unListed_layout.setOnClickListener(null);
                cip_layout.setOnClickListener(null);
                hos_layout.setOnClickListener(null);


                txt_doc_progress_value.setText("0%");
                txt_che_progress_value.setText("0%");
                txt_stockiest_progress_value.setText("0%");
                txt_Unlistered_progress_value.setText("0%");
                txt_cip_progress_value.setText("0%");
                txt_hos_progress_value.setText("0%");

                doc_call_count.setText("0 / " + Doctor_list.length());
                che_call_count.setText("0 / " + Chemist_list.length());
                stockiest_call_count.setText(" 0/ " + Stockiest_list.length());
                unlisted_call_count.setText(" 0/ " + unlistered_list.length());
                cip_call_count.setText("0 / " + cip_list.length());
                hospital_call_count.setText("0 / " + hos_list.length());

            }


        } catch (Exception a) {
            a.printStackTrace();
        }


    }

    void setLineChartData(String Custype) {


        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("dd");

        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfs = new SimpleDateFormat("MMMM");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat sdfss = new SimpleDateFormat("yyyy-MM-dd");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -2);

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDate = calendar.getTime();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date lastDate = calendar.getTime();
        String Last_Date_of_Two_Months_Ago = sdf.format(lastDate);

        calendar.set(Calendar.DAY_OF_MONTH, 15);
        Date fifteenthDate = calendar.getTime();

        calendar.set(Calendar.DAY_OF_MONTH, 16);

        String firstDateStr = sdfss.format(firstDate);
        String fifteenthDateStr = sdfss.format(fifteenthDate);
        String enddate = sdfss.format(lastDate);
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);


        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.MONTH, -1);
        calendar1.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDate1 = calendar1.getTime();
        calendar1.set(Calendar.DAY_OF_MONTH, calendar1.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date lastDate1 = calendar1.getTime();
        String Last_Date_of_pastmonth = sdf.format(lastDate1);
        calendar1.set(Calendar.DAY_OF_MONTH, 15);
        Date fifteenthDate1 = calendar1.getTime();
        calendar1.set(Calendar.DAY_OF_MONTH, 16);
        String firstDatepastmonth = sdfss.format(firstDate1);
        String fifteenthDatepastmonth = sdfss.format(fifteenthDate1);
        String enddatepastmonth = sdfss.format(lastDate1);
        String month1 = String.valueOf(calendar1.get(Calendar.MONTH) + 1);


        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.DAY_OF_MONTH, 1);
        Date firstDate2 = calendar2.getTime();
        calendar2.set(Calendar.DAY_OF_MONTH, calendar2.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date lastDate2 = calendar2.getTime();
        String cutrrentlastdate = sdf.format(lastDate2);
        calendar2.set(Calendar.DAY_OF_MONTH, 15);
        Date fifteenthDate2 = calendar2.getTime();

        calendar2.set(Calendar.DAY_OF_MONTH, 16);

        String firstDatecurrent = sdfss.format(firstDate2);
        String fifteenthDatecurrent = sdfss.format(fifteenthDate2);
        String enddatecurrent = sdfss.format(lastDate2);


        ArrayList<Entry> entries = new ArrayList<>();

        boolean ispast2month = sqLite.isMonthDataAvailableForCustType(Custype, month);
        boolean ispast1month = sqLite.isMonthDataAvailableForCustType(Custype, month1);


        List<Integer> listYrange = new ArrayList<>();
        if (ispast2month) {
            key = "3";
            txt_month_one.setText(sdfs.format(calendar.getTime()));
            txt_month_two.setText(sdfs.format(calendar1.getTime()));
            txt_month_three.setText(sdfs.format(calendar2.getTime()));

            tvFromToMonth.setText(String.format("%s %d - %s %d", sdfs.format(calendar.getTime()), calendar.get(Calendar.YEAR), sdfs.format(calendar2.getTime()), calendar2.get(Calendar.YEAR)));

            txt_month_one.setVisibility(View.VISIBLE);
            txt_month_two.setVisibility(View.VISIBLE);
            txt_month_three.setVisibility(View.VISIBLE);

            int xaxis1 = sqLite.getcalls_count_by_range(firstDateStr, fifteenthDateStr, Custype);
            int xaxis2 = sqLite.getcalls_count_by_range(firstDateStr, enddate, Custype);
            int xaxis3 = sqLite.getcalls_count_by_range(firstDatepastmonth, fifteenthDatepastmonth, Custype);
            int xaxis4 = sqLite.getcalls_count_by_range(firstDatepastmonth, enddatepastmonth, Custype);
            int xaxis5 = sqLite.getcalls_count_by_range(firstDatecurrent, fifteenthDatecurrent, Custype);
            int xaxis6 = sqLite.getcalls_count_by_range(firstDatecurrent, enddatecurrent, Custype);

            listYrange.add(xaxis1);
            listYrange.add(xaxis2);
            listYrange.add(xaxis3);
            listYrange.add(xaxis4);
            listYrange.add(xaxis5);
            listYrange.add(xaxis6);

            entries.add(new Entry(1, xaxis1));
            entries.add(new Entry(2, xaxis2));
            entries.add(new Entry(3, xaxis3));
            entries.add(new Entry(4, xaxis4));
            entries.add(new Entry(5, xaxis5));
            entries.add(new Entry(6, xaxis6));


        } else if (ispast1month) {
            key = "2";
            txt_month_one.setText(sdfs.format(calendar1.getTime()));
            txt_month_two.setText(sdfs.format(calendar2.getTime()));

            tvFromToMonth.setText(String.format("%s %d - %s %d", sdfs.format(calendar1.getTime()), calendar1.get(Calendar.YEAR), sdfs.format(calendar2.getTime()), calendar2.get(Calendar.YEAR)));

            txt_month_three.setVisibility(View.INVISIBLE);
            txt_month_one.setVisibility(View.VISIBLE);
            txt_month_two.setVisibility(View.VISIBLE);

            int xaxis3 = sqLite.getcalls_count_by_range(firstDatepastmonth, fifteenthDatepastmonth, Custype);
            int xaxis4 = sqLite.getcalls_count_by_range(firstDatepastmonth, enddatepastmonth, Custype);
            int xaxis5 = sqLite.getcalls_count_by_range(firstDatecurrent, fifteenthDatecurrent, Custype);
            int xaxis6 = sqLite.getcalls_count_by_range(firstDatecurrent, enddatecurrent, Custype);

            listYrange.add(xaxis3);
            listYrange.add(xaxis4);
            listYrange.add(xaxis5);
            listYrange.add(xaxis6);

            entries.add(new Entry(1, xaxis3));
            entries.add(new Entry(2, xaxis4));
            entries.add(new Entry(3, xaxis5));
            entries.add(new Entry(4, xaxis6));
        } else {
            key = "1";
            txt_month_one.setText(sdfs.format(calendar2.getTime()));

            tvFromToMonth.setText(String.format("%s %d", sdfs.format(calendar2.getTime()), calendar2.get(Calendar.YEAR)));

            txt_month_two.setVisibility(View.INVISIBLE);
            txt_month_three.setVisibility(View.INVISIBLE);
            txt_month_one.setVisibility(View.VISIBLE);

            int xaxis5 = sqLite.getcalls_count_by_range(firstDatecurrent, fifteenthDatecurrent, Custype);
            int xaxis6 = sqLite.getcalls_count_by_range(firstDatecurrent, enddatecurrent, Custype);

            listYrange.add(xaxis5);
            listYrange.add(xaxis6);

            entries.add(new Entry(1, xaxis5));
            entries.add(new Entry(2, xaxis5));
        }

        LineDataSet dataSet = new LineDataSet(entries, "");
        dataSet.setDrawValues(false);
        dataSet.setColor(Color.BLACK);
        dataSet.setCircleColor(Color.BLACK);
        dataSet.setCircleHoleColor(Color.BLACK);
        dataSet.setDrawCircles(true);
        dataSet.setCircleRadius(3f);
        dataSet.setHighlightEnabled(true);
        dataSet.setDrawCircles(true);
        dataSet.setLineWidth(1f);
        dataSet.setDrawHighlightIndicators(false);
        dataSet.disableDashedHighlightLine();
        lineChart.getLegend().setEnabled(false);
        LineData lineData1 = new LineData(dataSet);
        lineChart.setData(lineData1);

        Typeface customTypeface = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            customTypeface = getResources().getFont(R.font.satoshi_medium);
        }


        XAxis xAxis = lineChart.getXAxis();
        xAxis.setAxisMinimum(0f);
        xAxis.setAxisMaximum(7f);
        xAxis.setLabelCount(8, true);
        xAxis.disableGridDashedLine();
        xAxis.setDrawGridLines(false);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setAvoidFirstLastClipping(true);
        xAxis.setTypeface(customTypeface);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setEnabled(false);
        xAxis.setTextSize(12);
        xAxis.setDrawAxisLine(false);


        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getAxisLabel(float value, AxisBase axis) {


                if (ispast2month) {

                    if (value == 0f) {
                        return "";
                    } else if (value == 1f) {
                        return "1" + getSuperscript("s") + " - 15" + getSuperscript("t");
                    } else if (value == 2f) {
                        return "1" + getSuperscript("s") + " - " + Last_Date_of_Two_Months_Ago + getSuperscript("t");

                    } else if (value == 3f) {
                        return "1" + getSuperscript("s") + " - 15" + getSuperscript("t");
                    } else if (value == 4f) {
                        return "1" + getSuperscript("s") + " - " + Last_Date_of_pastmonth + getSuperscript("t");

                    } else if (value == 5f) {
                        return "1" + getSuperscript("s") + " - 15" + getSuperscript("t");
                    } else if (value == 6f) {
                        return "1" + getSuperscript("s") + " - " + cutrrentlastdate + getSuperscript("t");
                    } else {
                        return "";
                    }


                } else if (ispast1month) {
                    if (value == 0f) {
                        return "";
                    } else if (value == 1f) {
                        return "1" + getSuperscript("s") + " - 15" + getSuperscript("t");
                    } else if (value == 2f) {
                        return "1" + getSuperscript("s") + " - " + Last_Date_of_pastmonth + getSuperscript("t");
                    } else if (value == 3f) {
                        return "1" + getSuperscript("s") + " - 15" + getSuperscript("t");

                    } else if (value == 4f) {
                        return "1" + getSuperscript("s") + " - " + cutrrentlastdate + getSuperscript("t");
                    } else {
                        return "";
                    }

                } else {

                    if (value == 0f) {
                        return "";

                    } else if (value == 1f) {
                        return "1" + getSuperscript("s") + " - 15" + getSuperscript("t");
                    } else if (value == 2f) {
                        return "1" + getSuperscript("s") + " - " + cutrrentlastdate + getSuperscript("t");
                    } else {
                        return "";
                    }
                }


            }

            private String getSuperscript(String text) {
                String[] superscripts = {"", "ˢᵗ", "ᵗʰ"};
                StringBuilder result = new StringBuilder();

                for (char c : text.toCharArray()) {
                    if (c == 's') {
                        result.append(superscripts[1]);
                    } else if (c == 't') {
                        result.append(superscripts[2]);
                    } else {
                        result.append(c);
                    }
                }
                return result.toString();
            }
        });


        lineChart.getXAxis().setEnabled(true);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftYAxis = lineChart.getAxisLeft();
        leftYAxis.removeAllLimitLines();
        leftYAxis.setAxisMinimum(0);

        int maxvalue = Collections.max(listYrange);

        if (maxvalue >= 100 && maxvalue < 200) {
            leftYAxis.setAxisMaximum(200);
        } else if (maxvalue >= 200 && maxvalue < 300) {
            leftYAxis.setAxisMaximum(300);
        } else if (maxvalue >= 300 && maxvalue < 400) {
            leftYAxis.setAxisMaximum(400);
        } else if (maxvalue >= 400 && maxvalue < 500) {
            leftYAxis.setAxisMaximum(400);
        } else if (maxvalue >= 500 && maxvalue < 1000) {
            leftYAxis.setAxisMaximum(400);
        } else if (maxvalue >= 1000) {
            leftYAxis.setAxisMaximum(5000);
        } else {
            leftYAxis.setAxisMaximum(100);
        }


        leftYAxis.setLabelCount(6, true);
        leftYAxis.setDrawLimitLinesBehindData(true);
        leftYAxis.setDrawLabels(true);
        leftYAxis.enableGridDashedLine(5f, 5f, 0f);
        leftYAxis.setDrawAxisLine(false);
        leftYAxis.setDrawGridLines(true);
        leftYAxis.setDrawLimitLinesBehindData(true);
        leftYAxis.setCenterAxisLabels(true);
        leftYAxis.setEnabled(true);
        leftYAxis.setTextSize(12);
        leftYAxis.setGridColor(ContextCompat.getColor(requireContext(), R.color.charline_color));

        leftYAxis.setDrawZeroLine(true);
        leftYAxis.setZeroLineColor(ContextCompat.getColor(requireContext(), R.color.gray_45));
        leftYAxis.setZeroLineWidth(1.2f);


        CustomMarkerView mv = new CustomMarkerView(requireContext(), R.layout.linechartpopup, Custype, firstDateStr, fifteenthDateStr, enddate, firstDatepastmonth, fifteenthDatepastmonth, enddatepastmonth, firstDatecurrent, fifteenthDatecurrent, enddatecurrent, key);
        mv.setChartView(lineChart);

        lineChart.setMarkerView(mv);
        YAxis rightAxis = lineChart.getAxisRight();

        rightAxis.setAxisLineColor(ContextCompat.getColor(requireContext(), R.color.white));
        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawLabels(false);
        lineChart.setDrawMarkerViews(true);


        lineChart.getAxisRight().setEnabled(false);
        lineChart.getDescription().setEnabled(false);
        lineChart.setPinchZoom(true);
        lineChart.setDoubleTapToZoomEnabled(false);
        lineChart.invalidate();
        lineChart.setTouchEnabled(true);
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setOnChartValueSelectedListener(null);
        lineChart.setHighlightPerTapEnabled(true);
        lineChart.getLegend().setEnabled(false);
        lineChart.setScaleMinima(0f, 0f);
        lineChart.fitScreen();
        lineChart.setScaleEnabled(false);
        lineChart.setExtraBottomOffset(5f);

        Bitmap starBitmap;
        if (Custype.equalsIgnoreCase("1")) {
            starBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.circular_img_doctor);

        } else if (Custype.equalsIgnoreCase("2")) {

            starBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.circular_img_chemist);

        } else if (Custype.equalsIgnoreCase("3")) {
            starBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.circular_img_stockiest);

        } else if (Custype.equalsIgnoreCase("4")) {
            starBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.circular_img_unlistered);

        } else if (Custype.equalsIgnoreCase("5")) {
            starBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.circular_img_cip);

        } else if (Custype.equalsIgnoreCase("6")) {
            starBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.circular_img_hospital);

        } else {
            starBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.circular_img_doctor);
        }


        lineChart.setRenderer(new ImageLineChartRenderer(lineChart, lineChart.getAnimator(), lineChart.getViewPortHandler(), starBitmap));

    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View v) {


        switch (v.getId()) {

            case R.id.ll_doc_child:
                tvAverageTag.setText(String.format("Average %s", tvDocCap.getText().toString()));
                doc_img_down_triangle.setVisibility(View.VISIBLE);
                che_img_down_triangle.setVisibility(View.GONE);
                stock_img_down_triangle.setVisibility(View.GONE);
                unlisred_img_down_triangle.setVisibility(View.GONE);
                cip_img_down_triangle.setVisibility(View.GONE);
                hos_img_down_triangle.setVisibility(View.GONE);
                lineChart.clear();

                setLineChartData("1");


                break;
            case R.id.ll_che_child:
                tvAverageTag.setText(String.format("Average %s", tvChemCap.getText().toString()));
                doc_img_down_triangle.setVisibility(View.GONE);
                che_img_down_triangle.setVisibility(View.VISIBLE);
                stock_img_down_triangle.setVisibility(View.GONE);
                unlisred_img_down_triangle.setVisibility(View.GONE);
                cip_img_down_triangle.setVisibility(View.GONE);
                hos_img_down_triangle.setVisibility(View.GONE);
                lineChart.clear();

                setLineChartData("2");


                break;
            case R.id.ll_stock_child:
                tvAverageTag.setText(String.format("Average %s", tvStkCap.getText().toString()));
                doc_img_down_triangle.setVisibility(View.GONE);
                che_img_down_triangle.setVisibility(View.GONE);
                stock_img_down_triangle.setVisibility(View.VISIBLE);
                unlisred_img_down_triangle.setVisibility(View.GONE);
                cip_img_down_triangle.setVisibility(View.GONE);
                hos_img_down_triangle.setVisibility(View.GONE);
                lineChart.clear();

                setLineChartData("3");


                break;
            case R.id.ll_unli_child:
                tvAverageTag.setText(String.format("Average %s", tvUnDrCap.getText().toString()));
                doc_img_down_triangle.setVisibility(View.GONE);
                che_img_down_triangle.setVisibility(View.GONE);
                stock_img_down_triangle.setVisibility(View.GONE);
                unlisred_img_down_triangle.setVisibility(View.VISIBLE);
                cip_img_down_triangle.setVisibility(View.GONE);
                hos_img_down_triangle.setVisibility(View.GONE);
                lineChart.clear();

                setLineChartData("4");


                break;
            case R.id.ll_cip_child:
                tvAverageTag.setText(String.format("Average %s", tvCipCap.getText().toString()));
                doc_img_down_triangle.setVisibility(View.GONE);
                che_img_down_triangle.setVisibility(View.GONE);
                stock_img_down_triangle.setVisibility(View.GONE);
                unlisred_img_down_triangle.setVisibility(View.GONE);
                cip_img_down_triangle.setVisibility(View.VISIBLE);
                hos_img_down_triangle.setVisibility(View.GONE);
                lineChart.clear();

                setLineChartData("5");


                break;
            case R.id.ll_hos_child:
                tvAverageTag.setText(String.format("Average %s Calls", tvHosCap.getText().toString()));
                doc_img_down_triangle.setVisibility(View.GONE);
                che_img_down_triangle.setVisibility(View.GONE);
                stock_img_down_triangle.setVisibility(View.GONE);
                unlisred_img_down_triangle.setVisibility(View.GONE);
                cip_img_down_triangle.setVisibility(View.GONE);
                hos_img_down_triangle.setVisibility(View.VISIBLE);
                lineChart.clear();
                setLineChartData("6");
                break;

        }

    }


    public void onDestroyView() {

        super.onDestroyView();
    }


}


