package saneforce.santrip.activity.homeScreen.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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

import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.HomeDashBoard;
import saneforce.santrip.activity.homeScreen.view.CustomMarkerView;
import saneforce.santrip.activity.homeScreen.view.ImageLineChartRenderer;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.databinding.CallAnalysisFagmentBinding;
import saneforce.santrip.response.LoginResponse;
import saneforce.santrip.storage.SQLite;
import saneforce.santrip.storage.SharedPref;
import saneforce.santrip.utility.TimeUtils;

public class CallAnalysisFragment extends Fragment implements View.OnClickListener {
    @SuppressLint("StaticFieldLeak")
    public static CallAnalysisFagmentBinding callAnalysisBinding;
    public static String SfType, SfCode, SfName, DivCode, Designation, StateCode, SubDivisionCode, DrNeed, ChemistNeed, CipNeed, StockistNeed, UnDrNeed, CapDr, CapChemist, CapStockist, CapCip, CapUnDr, CapHos, HospNeed;
    public static String key;
    public static JSONArray dcrdatas;
    public static JSONArray Doctor_list, Chemist_list, Stockiest_list, unlistered_list, cip_list, hos_list;
    SQLite sqLite;
    LoginResponse loginResponse;
    Context context;
    CommonUtilsMethods commonUtilsMethods;



    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.v("fragment", "callanalysis");
        callAnalysisBinding = CallAnalysisFagmentBinding.inflate(inflater);
        View v = callAnalysisBinding.getRoot();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        context = requireContext();
        sqLite = new SQLite(requireContext());

        callAnalysisBinding.imgDownTriangleDoc.setVisibility(View.VISIBLE);
        callAnalysisBinding.imgDownTriangleChe.setVisibility(View.GONE);
        callAnalysisBinding.imgDownTriangleStockiest.setVisibility(View.GONE);
        callAnalysisBinding.imgDownTriangleUnlistered.setVisibility(View.GONE);
        callAnalysisBinding.imgDownTriangleCip.setVisibility(View.GONE);
        callAnalysisBinding.imgDownTriangleHos.setVisibility(View.GONE);

        callAnalysisBinding.llDocChild.setOnClickListener(this);
        callAnalysisBinding.llCheChild.setOnClickListener(this);
        callAnalysisBinding.llStockChild.setOnClickListener(this);
        callAnalysisBinding.llUnliChild.setOnClickListener(this);
        callAnalysisBinding.llHosChild.setOnClickListener(this);
        callAnalysisBinding.llCipChild.setOnClickListener(this);

//        if(SharedPref.getMonthForClearCalls(getActivity())!=Integer.parseInt(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_8))){
//           ClearOldCalls();
//        }
        getRequiredData();
        HiddenVisibleFunctions();
        SetcallDetailsInLineChart(sqLite, context);

        ViewTreeObserver vto = callAnalysisBinding.callAnalysisLayout.getViewTreeObserver();

        vto.addOnGlobalLayoutListener(() -> {

            int getwidhtlayout = callAnalysisBinding.callAnalysisLayout.getMeasuredWidth();
            int getlayoutlayout = callAnalysisBinding.callAnalysisLayout.getMeasuredHeight();


            int width = (getwidhtlayout / 3 - 8);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(width, getlayoutlayout - getResources().getDimensionPixelSize(R.dimen._22sdp));
            param.setMargins(0, 5, 10, 0);
            callAnalysisBinding.llDocChild.setLayoutParams(param);
            callAnalysisBinding.llCheChild.setLayoutParams(param);
            callAnalysisBinding.llStockChild.setLayoutParams(param);
            callAnalysisBinding.llUnliChild.setLayoutParams(param);
            callAnalysisBinding.llHosChild.setLayoutParams(param);
            callAnalysisBinding.llCipChild.setLayoutParams(param);
        });


        callAnalysisBinding.llCallsLayout.setOnTouchListener((v12, event) -> {
            HomeDashBoard.binding.viewPager1.setScrollEnabled(false);
            return false;
        });


        callAnalysisBinding.inChart.lineChart.setOnTouchListener((v1, event) -> {
            HomeDashBoard.binding.viewPager1.setScrollEnabled(true);
            return false;
        });


        callAnalysisBinding.inChart.lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {

            }

            @Override
            public void onNothingSelected() {

            }

        });

        return v;
    }
    public static int computePercent(int current, int total) {
        int percent = 0;
        if (total > 0) percent = current * 100 / total;
        return percent;
    }

    public static void SetcallDetailsInLineChart(SQLite sqLite, Context context) {
        sqLite.clearLinecharTable();
        try {
            Doctor_list = sqLite.getMasterSyncDataByKey(Constants.DOCTOR + SharedPref.getHqCode(context));
            Chemist_list = sqLite.getMasterSyncDataByKey(Constants.CHEMIST + SharedPref.getHqCode(context));
            Stockiest_list = sqLite.getMasterSyncDataByKey(Constants.STOCKIEST + SharedPref.getHqCode(context));
            unlistered_list = sqLite.getMasterSyncDataByKey(Constants.UNLISTED_DOCTOR + SharedPref.getHqCode(context));
            cip_list = sqLite.getMasterSyncDataByKey(Constants.CIP + SharedPref.getHqCode(context));
            hos_list = sqLite.getMasterSyncDataByKey(Constants.HOSPITAL + SharedPref.getHqCode(context));

            dcrdatas = sqLite.getMasterSyncDataByKey(Constants.CALL_SYNC);
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


                if (!Designation.equalsIgnoreCase("MR")) {
                    callAnalysisBinding.txtDocCount.setText(String.valueOf(doc_current_callcount));
                    callAnalysisBinding.txtCheCount.setText(String.valueOf(che_current_callcount));
                    callAnalysisBinding.txtStockCount.setText(String.valueOf(stockiest_current_callcount));
                    callAnalysisBinding.txtUnlistCount.setText(String.valueOf(unlistered_current_callcount));
                    callAnalysisBinding.txtCipCount.setText(String.valueOf(cip_current_callcount));
                    callAnalysisBinding.txtHosCount.setText(String.valueOf(hos_current_callcount));

                    callAnalysisBinding.imgDoc.setVisibility(View.VISIBLE);
                    callAnalysisBinding.imgChe.setVisibility(View.VISIBLE);
                    callAnalysisBinding.imgStock.setVisibility(View.VISIBLE);
                    callAnalysisBinding.imgUnlist.setVisibility(View.VISIBLE);
                    callAnalysisBinding.imgCip.setVisibility(View.VISIBLE);
                    callAnalysisBinding.imgHos.setVisibility(View.VISIBLE);

                    callAnalysisBinding.FlDocProgress.setVisibility(View.GONE);
                    callAnalysisBinding.FlCheProgress.setVisibility(View.GONE);
                    callAnalysisBinding.FlStockProgress.setVisibility(View.GONE);
                    callAnalysisBinding.FlUnistProgress.setVisibility(View.GONE);
                    callAnalysisBinding.FlCipProgress.setVisibility(View.GONE);
                    callAnalysisBinding.FlHosProgress.setVisibility(View.GONE);

                } else {
                    callAnalysisBinding.txtDocCount.setText(doc_current_callcount + " / " + Doctor_list.length());
                    callAnalysisBinding.txtCheCount.setText(che_current_callcount + " / " + Chemist_list.length());
                    callAnalysisBinding.txtStockCount.setText(stockiest_current_callcount + " / " + Stockiest_list.length());
                    callAnalysisBinding.txtUnlistCount.setText(unlistered_current_callcount + " / " + unlistered_list.length());
                    callAnalysisBinding.txtCipCount.setText(cip_current_callcount + " / " + cip_list.length());
                    callAnalysisBinding.txtHosCount.setText(hos_current_callcount + " / " + hos_list.length());

                    int doc_progress_value, che_progress_value, stockiest_progress_value, unlistered_progress_value, cip_progress_value, hos_progress_value;

                    doc_progress_value = computePercent(doc_current_callcount, Doctor_list.length());
                    che_progress_value = computePercent(che_current_callcount, Chemist_list.length());
                    stockiest_progress_value = computePercent(stockiest_current_callcount, Stockiest_list.length());
                    unlistered_progress_value = computePercent(unlistered_current_callcount, unlistered_list.length());
                    cip_progress_value = computePercent(cip_current_callcount, cip_list.length());
                    hos_progress_value = computePercent(hos_current_callcount, hos_list.length());

                    callAnalysisBinding.txtDocValue.setText(doc_progress_value + "%");
                    callAnalysisBinding.txtCheValue.setText(che_progress_value + "%");
                    callAnalysisBinding.txtStockValue.setText(stockiest_progress_value + "%");
                    callAnalysisBinding.txtUnlistedValue.setText(unlistered_progress_value + "%");
                    callAnalysisBinding.txtCipValue.setText(cip_progress_value + "%");
                    callAnalysisBinding.txtHosValue.setText(hos_progress_value + "%");

                    callAnalysisBinding.docProgressBar.setProgress(doc_progress_value);
                    callAnalysisBinding.cheProgressBar.setProgress(che_progress_value);
                    callAnalysisBinding.stockProgressBar.setProgress(stockiest_progress_value);
                    callAnalysisBinding.unlistProgressBar.setProgress(unlistered_progress_value);
                    callAnalysisBinding.cipProgressBar.setProgress(cip_progress_value);
                    callAnalysisBinding.hosProgressBar.setProgress(hos_progress_value);

                    callAnalysisBinding.FlDocProgress.setVisibility(View.VISIBLE);
                    callAnalysisBinding.FlCheProgress.setVisibility(View.VISIBLE);
                    callAnalysisBinding.FlStockProgress.setVisibility(View.VISIBLE);
                    callAnalysisBinding.FlUnistProgress.setVisibility(View.VISIBLE);
                    callAnalysisBinding.FlCipProgress.setVisibility(View.VISIBLE);
                    callAnalysisBinding.FlHosProgress.setVisibility(View.VISIBLE);

                    callAnalysisBinding.imgDoc.setVisibility(View.GONE);
                    callAnalysisBinding.imgChe.setVisibility(View.GONE);
                    callAnalysisBinding.imgStock.setVisibility(View.GONE);
                    callAnalysisBinding.imgUnlist.setVisibility(View.GONE);
                    callAnalysisBinding.imgCip.setVisibility(View.GONE);
                    callAnalysisBinding.imgHos.setVisibility(View.GONE);

                }
                callAnalysisBinding.inChart.lineChart.clear();
                callAnalysisBinding.inChart.llMonthlayout.setVisibility(View.VISIBLE);

                if (DrNeed.equalsIgnoreCase("0")) {
                    callAnalysisBinding.imgDownTriangleDoc.setVisibility(View.VISIBLE);
                    callAnalysisBinding.imgDownTriangleChe.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleStockiest.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleUnlistered.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleCip.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleHos.setVisibility(View.GONE);
                    setLineChartData("1", sqLite, context);
                } else if (ChemistNeed.equalsIgnoreCase("0")) {
                    callAnalysisBinding.imgDownTriangleDoc.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleChe.setVisibility(View.VISIBLE);
                    callAnalysisBinding.imgDownTriangleStockiest.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleUnlistered.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleCip.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleHos.setVisibility(View.GONE);
                    setLineChartData("2", sqLite, context);
                } else if (StockistNeed.equalsIgnoreCase("0")) {
                    callAnalysisBinding.imgDownTriangleDoc.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleChe.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleStockiest.setVisibility(View.VISIBLE);
                    callAnalysisBinding.imgDownTriangleUnlistered.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleCip.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleHos.setVisibility(View.GONE);
                    setLineChartData("3", sqLite, context);
                } else if (UnDrNeed.equalsIgnoreCase("0")) {
                    callAnalysisBinding.imgDownTriangleDoc.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleChe.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleStockiest.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleUnlistered.setVisibility(View.VISIBLE);
                    callAnalysisBinding.imgDownTriangleCip.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleHos.setVisibility(View.GONE);
                    setLineChartData("4", sqLite, context);
                } else if (CipNeed.equalsIgnoreCase("0")) {
                    callAnalysisBinding.imgDownTriangleDoc.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleChe.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleStockiest.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleUnlistered.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleCip.setVisibility(View.VISIBLE);
                    callAnalysisBinding.imgDownTriangleHos.setVisibility(View.GONE);
                    setLineChartData("5", sqLite, context);
                } else if (HospNeed.equalsIgnoreCase("0")) {
                    callAnalysisBinding.imgDownTriangleDoc.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleChe.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleStockiest.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleUnlistered.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleCip.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleHos.setVisibility(View.VISIBLE);
                    setLineChartData("6", sqLite, context);
                }
            } else {
                callAnalysisBinding.inChart.llMonthlayout.setVisibility(View.GONE);
                callAnalysisBinding.llDocChild.setOnClickListener(null);
                callAnalysisBinding.llCheChild.setOnClickListener(null);
                callAnalysisBinding.llStockChild.setOnClickListener(null);
                callAnalysisBinding.llUnliChild.setOnClickListener(null);
                callAnalysisBinding.llHosChild.setOnClickListener(null);
                callAnalysisBinding.llCipChild.setOnClickListener(null);


                if (!Designation.equalsIgnoreCase("MR")) {
                    callAnalysisBinding.txtDocCount.setText("0");
                    callAnalysisBinding.txtCheCount.setText("0");
                    callAnalysisBinding.txtStockCount.setText("0");
                    callAnalysisBinding.txtUnlistCount.setText("0");
                    callAnalysisBinding.txtCipCount.setText("0");
                    callAnalysisBinding.txtHosCount.setText("0");
                } else {
                    callAnalysisBinding.txtDocCount.setText("0 / " + Doctor_list.length());
                    callAnalysisBinding.txtCheCount.setText("0 / " + Chemist_list.length());
                    callAnalysisBinding.txtStockCount.setText(" 0/ " + Stockiest_list.length());
                    callAnalysisBinding.txtUnlistCount.setText(" 0/ " + unlistered_list.length());
                    callAnalysisBinding.txtCipCount.setText("0 / " + cip_list.length());
                    callAnalysisBinding.txtHosCount.setText("0 / " + hos_list.length());

                    callAnalysisBinding.txtDocValue.setText("0%");
                    callAnalysisBinding.txtCheValue.setText("0%");
                    callAnalysisBinding.txtStockValue.setText("0%");
                    callAnalysisBinding.txtUnlistedValue.setText("0%");
                    callAnalysisBinding.txtCipValue.setText("0%");
                    callAnalysisBinding.txtHosValue.setText("0%");
                }
            }

        } catch (Exception a) {
            a.printStackTrace();
        }


    }

    public static void setLineChartData(String Custype, SQLite sqLite, Context context) {


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
        String sixteenthDateStr = sdfss.format(sixteenthDate);
        String enddate = sdfss.format(lastDate);
        String month = String.valueOf(calendar.get(Calendar.MONTH) + 1);


        Calendar calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.MONTH, -1);
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
        String sixteenthDatepastmonth = sdfss.format(sixteenthDate1);
        String enddatepastmonth = sdfss.format(lastDate1);
        String month1 = String.valueOf(calendar1.get(Calendar.MONTH) + 1);


        Calendar calendar2 = Calendar.getInstance();
        calendar2.set(Calendar.DAY_OF_MONTH, 1);
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
        String sixteenthDatecurrent = sdfss.format(sixteenthDate2);
        String enddatecurrent = sdfss.format(lastDate2);


        ArrayList<Entry> entries = new ArrayList<>();

        boolean ispast2month = sqLite.isMonthDataAvailableForCustType(Custype, month);
        boolean ispast1month = sqLite.isMonthDataAvailableForCustType(Custype, month1);


        List<Integer> listYrange = new ArrayList<>();
        if (ispast2month) {
            key = "3";
            callAnalysisBinding.inChart.txtMonthOne.setText(sdfs.format(calendar.getTime()));
            callAnalysisBinding.inChart.txtMonthTwo.setText(sdfs.format(calendar1.getTime()));
            callAnalysisBinding.inChart.txtMonthThree.setText(sdfs.format(calendar2.getTime()));
            callAnalysisBinding.inChart.txtMonthOne.setVisibility(View.VISIBLE);
            callAnalysisBinding.inChart.txtMonthTwo.setVisibility(View.VISIBLE);
            callAnalysisBinding.inChart.txtMonthThree.setVisibility(View.VISIBLE);
            callAnalysisBinding.textDate.setText(String.format("%s %d - %s %d", sdfs.format(calendar.getTime()), calendar.get(Calendar.YEAR), sdfs.format(calendar2.getTime()), calendar2.get(Calendar.YEAR)));


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
            if (Integer.valueOf(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_7)) > 15) {
                entries.add(new Entry(6, xaxis6));
            }

        } else if (ispast1month) {
            key = "2";
            callAnalysisBinding.inChart.txtMonthOne.setText(sdfs.format(calendar1.getTime()));
            callAnalysisBinding.inChart.txtMonthTwo.setText(sdfs.format(calendar2.getTime()));
            callAnalysisBinding.inChart.txtMonthThree.setVisibility(View.INVISIBLE);
            callAnalysisBinding.inChart.txtMonthOne.setVisibility(View.VISIBLE);
            callAnalysisBinding.inChart.txtMonthTwo.setVisibility(View.VISIBLE);
            callAnalysisBinding.textDate.setText(String.format("%s %d - %s %d", sdfs.format(calendar1.getTime()), calendar1.get(Calendar.YEAR), sdfs.format(calendar2.getTime()), calendar2.get(Calendar.YEAR)));

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
            if (Integer.valueOf(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_7)) > 15) {
                entries.add(new Entry(4, xaxis6));
            }

        } else {
            key = "1";
            callAnalysisBinding.inChart.txtMonthOne.setText(sdfs.format(calendar2.getTime()));
            callAnalysisBinding.inChart.txtMonthTwo.setVisibility(View.INVISIBLE);
            callAnalysisBinding.inChart.txtMonthThree.setVisibility(View.INVISIBLE);
            callAnalysisBinding.inChart.txtMonthOne.setVisibility(View.VISIBLE);
            callAnalysisBinding.textDate.setText(String.format("%s %d", sdfs.format(calendar2.getTime()), calendar2.get(Calendar.YEAR)));

            int xaxis5 = sqLite.getcalls_count_by_range(firstDatecurrent, fifteenthDatecurrent, Custype);
            int xaxis6 = sqLite.getcalls_count_by_range(firstDatecurrent, enddatecurrent, Custype);

            listYrange.add(xaxis5);
            listYrange.add(xaxis6);


            entries.add(new Entry(1, xaxis5));
            if (Integer.valueOf(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_7)) > 15) {
                entries.add(new Entry(2, xaxis5));
            }


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
        callAnalysisBinding.inChart.lineChart.getLegend().setEnabled(false);
        LineData lineData1 = new LineData(dataSet);
        callAnalysisBinding.inChart.lineChart.setData(lineData1);

        Typeface customTypeface = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            customTypeface = context.getResources().getFont(R.font.satoshi_medium);
        }


        XAxis xAxis = callAnalysisBinding.inChart.lineChart.getXAxis();
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
                        if(Integer.valueOf(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_7))>15){
                            return "1" + getSuperscript("s") + " - " + cutrrentlastdate + getSuperscript("t");}
                        else {return "";}                    } else {
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
                        if(Integer.valueOf(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_7))>15){
                            return "1" + getSuperscript("s") + " - " + cutrrentlastdate + getSuperscript("t");}
                        else {return "";}                    } else {
                        return "";
                    }

                } else {

                    if (value == 0f) {
                        return "";

                    } else if (value == 1f) {
                        return "1" + getSuperscript("s") + " - 15" + getSuperscript("t");
                    } else if (value == 2f) {
                        if(Integer.valueOf(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_7))>15){
                            return "1" + getSuperscript("s") + " - " + cutrrentlastdate + getSuperscript("t");}
                        else {return "";}
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


        callAnalysisBinding.inChart.lineChart.getXAxis().setEnabled(true);
        callAnalysisBinding.inChart.lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftYAxis = callAnalysisBinding.inChart.lineChart.getAxisLeft();
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
            leftYAxis.setAxisMaximum(1000);
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
        leftYAxis.setGridColor(context.getResources().getColor(R.color.charline_color));

        leftYAxis.setDrawZeroLine(true);
        leftYAxis.setZeroLineColor(context.getResources().getColor(R.color.gray_45));
        leftYAxis.setZeroLineWidth(1.2f);


        CustomMarkerView mv = new CustomMarkerView(context, R.layout.linechartpopup, Custype, firstDateStr, fifteenthDateStr, enddate, firstDatepastmonth, fifteenthDatepastmonth, enddatepastmonth, firstDatecurrent, fifteenthDatecurrent, enddatecurrent, key);
        mv.setChartView(callAnalysisBinding.inChart.lineChart);

        callAnalysisBinding.inChart.lineChart.setMarkerView(mv);
        YAxis rightAxis = callAnalysisBinding.inChart.lineChart.getAxisRight();

        rightAxis.setAxisLineColor(context.getResources().getColor(R.color.white));
        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawLabels(false);
        callAnalysisBinding.inChart.lineChart.setDrawMarkerViews(true);


        callAnalysisBinding.inChart.lineChart.getAxisRight().setEnabled(false);
        callAnalysisBinding.inChart.lineChart.getDescription().setEnabled(false);
        callAnalysisBinding.inChart.lineChart.setPinchZoom(true);
        callAnalysisBinding.inChart.lineChart.setDoubleTapToZoomEnabled(false);
        callAnalysisBinding.inChart.lineChart.invalidate();
        callAnalysisBinding.inChart.lineChart.setTouchEnabled(true);
        callAnalysisBinding.inChart.lineChart.setDragEnabled(true);
        callAnalysisBinding.inChart.lineChart.setScaleEnabled(true);
        callAnalysisBinding.inChart.lineChart.setOnChartValueSelectedListener(null);
        callAnalysisBinding.inChart.lineChart.setHighlightPerTapEnabled(true);
        callAnalysisBinding.inChart.lineChart.getLegend().setEnabled(false);
        callAnalysisBinding.inChart.lineChart.setScaleMinima(0f, 0f);
        callAnalysisBinding.inChart.lineChart.fitScreen();
        callAnalysisBinding.inChart.lineChart.setScaleEnabled(false);
        callAnalysisBinding.inChart.lineChart.setExtraBottomOffset(5f);

        Bitmap starBitmap;
        if (Custype.equalsIgnoreCase("1")) {
            starBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.circular_img_doctor);

        } else if (Custype.equalsIgnoreCase("2")) {

            starBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.circular_img_chemist);

        } else if (Custype.equalsIgnoreCase("3")) {
            starBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.circular_img_stockiest);

        } else if (Custype.equalsIgnoreCase("4")) {
            starBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.circular_img_unlistered);

        } else if (Custype.equalsIgnoreCase("5")) {
            starBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.circular_img_cip);

        } else if (Custype.equalsIgnoreCase("6")) {
            starBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.circular_img_hospital);

        } else {
            starBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.circular_img_doctor);
        }


        callAnalysisBinding.inChart.lineChart.setRenderer(new ImageLineChartRenderer(callAnalysisBinding.inChart.lineChart, callAnalysisBinding.inChart.lineChart.getAnimator(), callAnalysisBinding.inChart.lineChart.getViewPortHandler(), starBitmap));

    }



    private void HiddenVisibleFunctions() {
        if (DrNeed.equalsIgnoreCase("0")) {
            callAnalysisBinding.llDocHead.setVisibility(View.VISIBLE);
            callAnalysisBinding.txtDocName.setText(CapDr);
            callAnalysisBinding.textAverage.setText(String.format("%s %s %s", getString(R.string.average), callAnalysisBinding.txtDocName.getText().toString(), getString(R.string.calls)));
        }
        if (ChemistNeed.equalsIgnoreCase("0")) {
            callAnalysisBinding.llChemHead.setVisibility(View.VISIBLE);
            callAnalysisBinding.txtCheName.setText(CapChemist);
        }
        if (StockistNeed.equalsIgnoreCase("0")) {
            callAnalysisBinding.llStockHead.setVisibility(View.VISIBLE);
            callAnalysisBinding.txtStockName.setText(CapStockist);
        }
        if (UnDrNeed.equalsIgnoreCase("0")) {
            callAnalysisBinding.llUnliHead.setVisibility(View.VISIBLE);
            callAnalysisBinding.txtUnliName.setText(CapUnDr);
        }
        if (CipNeed.equalsIgnoreCase("0")) {
            callAnalysisBinding.llCipHead.setVisibility(View.VISIBLE);
            callAnalysisBinding.txtCipName.setText(CapCip);
        }
        if (HospNeed.equalsIgnoreCase("0")) {
            callAnalysisBinding.llHosHead.setVisibility(View.VISIBLE);
            callAnalysisBinding.txtHosName.setText(CapHos);
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

    @Override
    public void onClick(View v) {


        switch (v.getId()) {

            case R.id.ll_doc_child:
                callAnalysisBinding.textAverage.setText(String.format("%s %s %s", getString(R.string.average), callAnalysisBinding.txtDocName.getText().toString(), getString(R.string.calls)));
                callAnalysisBinding.imgDownTriangleDoc.setVisibility(View.VISIBLE);
                callAnalysisBinding.imgDownTriangleChe.setVisibility(View.GONE);
                callAnalysisBinding.imgDownTriangleStockiest.setVisibility(View.GONE);
                callAnalysisBinding.imgDownTriangleUnlistered.setVisibility(View.GONE);
                callAnalysisBinding.imgDownTriangleCip.setVisibility(View.GONE);
                callAnalysisBinding.imgDownTriangleHos.setVisibility(View.GONE);
                callAnalysisBinding.inChart.lineChart.clear();

                setLineChartData("1", sqLite, context);


                break;
            case R.id.ll_che_child:
                callAnalysisBinding.textAverage.setText(String.format("%s %s %s", getString(R.string.average), callAnalysisBinding.txtCheName.getText().toString(), getString(R.string.calls)));
                callAnalysisBinding.imgDownTriangleDoc.setVisibility(View.GONE);
                callAnalysisBinding.imgDownTriangleChe.setVisibility(View.VISIBLE);
                callAnalysisBinding.imgDownTriangleStockiest.setVisibility(View.GONE);
                callAnalysisBinding.imgDownTriangleUnlistered.setVisibility(View.GONE);
                callAnalysisBinding.imgDownTriangleCip.setVisibility(View.GONE);
                callAnalysisBinding.imgDownTriangleHos.setVisibility(View.GONE);
                callAnalysisBinding.inChart.lineChart.clear();

                setLineChartData("2", sqLite, context);


                break;
            case R.id.ll_stock_child:
                callAnalysisBinding.textAverage.setText(String.format("%s %s %s", getString(R.string.average), callAnalysisBinding.txtStockName.getText().toString(), getString(R.string.calls)));
                callAnalysisBinding.imgDownTriangleDoc.setVisibility(View.GONE);
                callAnalysisBinding.imgDownTriangleChe.setVisibility(View.GONE);
                callAnalysisBinding.imgDownTriangleStockiest.setVisibility(View.VISIBLE);
                callAnalysisBinding.imgDownTriangleUnlistered.setVisibility(View.GONE);
                callAnalysisBinding.imgDownTriangleCip.setVisibility(View.GONE);
                callAnalysisBinding.imgDownTriangleHos.setVisibility(View.GONE);
                callAnalysisBinding.inChart.lineChart.clear();

                setLineChartData("3", sqLite, context);


                break;
            case R.id.ll_unli_child:
                callAnalysisBinding.textAverage.setText(String.format("%s %s %s", getString(R.string.average), callAnalysisBinding.txtUnliName.getText().toString(), getString(R.string.calls)));
                callAnalysisBinding.imgDownTriangleDoc.setVisibility(View.GONE);
                callAnalysisBinding.imgDownTriangleChe.setVisibility(View.GONE);
                callAnalysisBinding.imgDownTriangleStockiest.setVisibility(View.GONE);
                callAnalysisBinding.imgDownTriangleUnlistered.setVisibility(View.VISIBLE);
                callAnalysisBinding.imgDownTriangleCip.setVisibility(View.GONE);
                callAnalysisBinding.imgDownTriangleHos.setVisibility(View.GONE);
                callAnalysisBinding.inChart.lineChart.clear();

                setLineChartData("4", sqLite, context);


                break;
            case R.id.ll_cip_child:
                callAnalysisBinding.textAverage.setText(String.format("%s %s %s", getString(R.string.average), callAnalysisBinding.txtCipName.getText().toString(), getString(R.string.calls)));
                callAnalysisBinding.imgDownTriangleDoc.setVisibility(View.GONE);
                callAnalysisBinding.imgDownTriangleChe.setVisibility(View.GONE);
                callAnalysisBinding.imgDownTriangleStockiest.setVisibility(View.GONE);
                callAnalysisBinding.imgDownTriangleUnlistered.setVisibility(View.GONE);
                callAnalysisBinding.imgDownTriangleCip.setVisibility(View.VISIBLE);
                callAnalysisBinding.imgDownTriangleHos.setVisibility(View.GONE);
                callAnalysisBinding.inChart.lineChart.clear();

                setLineChartData("5", sqLite, context);


                break;
            case R.id.ll_hos_child:
                callAnalysisBinding.textAverage.setText(String.format("%s %s %s", getString(R.string.average), callAnalysisBinding.txtHosName.getText().toString(), getString(R.string.calls)));
                callAnalysisBinding.imgDownTriangleDoc.setVisibility(View.GONE);
                callAnalysisBinding.imgDownTriangleChe.setVisibility(View.GONE);
                callAnalysisBinding.imgDownTriangleStockiest.setVisibility(View.GONE);
                callAnalysisBinding.imgDownTriangleUnlistered.setVisibility(View.GONE);
                callAnalysisBinding.imgDownTriangleCip.setVisibility(View.GONE);
                callAnalysisBinding.imgDownTriangleHos.setVisibility(View.VISIBLE);
                callAnalysisBinding.inChart.lineChart.clear();
                setLineChartData("6", sqLite, context);
                break;

        }

    }


    public void onDestroyView() {

        super.onDestroyView();
    }


    private void ClearOldCalls(){

        JSONArray jsonArray = sqLite.getMasterSyncDataByKey(Constants.CALL_SYNC);
        if (jsonArray.length() > 0) {

            JSONArray filteredArray = new JSONArray();

        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentYear = calendar.get(Calendar.YEAR);


        Calendar pastMonthCalender = Calendar.getInstance();
        pastMonthCalender.add(Calendar.MONTH, -1);
        int previousMonth = pastMonthCalender.get(Calendar.MONTH) + 1;
        int previousYear = pastMonthCalender.get(Calendar.YEAR);

        Calendar pastSecondMonthCalender = Calendar.getInstance();
        pastSecondMonthCalender.add(Calendar.MONTH, -1);
        int previousSecoundMonth = pastSecondMonthCalender.get(Calendar.MONTH) + 1;
        int previousSecoundYear = pastSecondMonthCalender.get(Calendar.YEAR);

        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String dcrDate = jsonObject.getString("Dcr_dt");
                String[] dateParts = dcrDate.split("-");
                if (dateParts.length == 3) {
                    int year = Integer.parseInt(dateParts[0]);
                    int month = Integer.parseInt(dateParts[1]);
                    if (month == currentMonth && currentYear == year) {
                        filteredArray.put(jsonObject);
                    } else if (month == previousMonth && currentYear == previousYear) {
                        filteredArray.put(jsonObject);
                    } else if (month == previousSecoundMonth && currentYear == previousSecoundYear) {
                        filteredArray.put(jsonObject);
                    }
                }
            }
        } catch (Exception a) {
            a.printStackTrace();
        }
        SharedPref.putMonth(getActivity(),currentYear);
        sqLite.saveMasterSyncData(Constants.CALL_SYNC,filteredArray.toString(),0);

    }else {
            sqLite.saveMasterSyncData(Constants.CALL_SYNC,"[]",0);
        }
    }

}


