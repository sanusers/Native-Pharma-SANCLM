package saneforce.sanclm.activity.homeScreen.fragment;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
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

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.HomeDashBoard;
import saneforce.sanclm.activity.homeScreen.view.CustomMarkerView;
import saneforce.sanclm.activity.homeScreen.view.ImageLineChartRenderer;
import saneforce.sanclm.commonClasses.Constants;
import saneforce.sanclm.databinding.CallAnalysisFagmentBinding;
import saneforce.sanclm.response.LoginResponse;
import saneforce.sanclm.storage.SQLite;
import saneforce.sanclm.storage.SharedPref;

public class CallAnalysisFragment extends Fragment implements View.OnClickListener {
    CallAnalysisFagmentBinding binding;
    SQLite sqLite;
    String SfType, SfCode, SfName, DivCode, Designation, StateCode, SubDivisionCode, DrNeed, ChemistNeed, CipNeed, StockistNeed, UnDrNeed, CapDr, CapChemist, CapStockist, CapCip, CapUnDr, CapHos, HospNeed;
    String key;
    JSONArray dcrdatas;
    LoginResponse loginResponse;

    public static int computePercent(int current, int total) {
        int percent = 0;
        if (total > 0) percent = current * 100 / total;
        return percent;
    }

    @SuppressLint("MissingInflatedId")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = CallAnalysisFagmentBinding.inflate(inflater);
        View v = binding.getRoot();

        sqLite = new SQLite(requireContext());


        binding.imgDownTriangleDoc.setVisibility(View.VISIBLE);
        binding.imgDownTriangleChe.setVisibility(View.GONE);
        binding.imgDownTriangleStockiest.setVisibility(View.GONE);
        binding.imgDownTriangleUnlistered.setVisibility(View.GONE);
        binding.imgDownTriangleCip.setVisibility(View.GONE);
        binding.imgDownTriangleHos.setVisibility(View.GONE);

        binding.llDocChild.setOnClickListener(this);
        binding.llCheChild.setOnClickListener(this);
        binding.llStockChild.setOnClickListener(this);
        binding.llUnliChild.setOnClickListener(this);
        binding.llHosChild.setOnClickListener(this);
        binding.llCipChild.setOnClickListener(this);

        getRequiredData();
        HiddenVisibleFunctions();
        SetcallDetailsInLineChart();

        ViewTreeObserver vto = binding.callAnalysisLayout.getViewTreeObserver();

        vto.addOnGlobalLayoutListener(() -> {

            int getwidhtlayout = binding.callAnalysisLayout.getMeasuredWidth();
            int getlayoutlayout = binding.callAnalysisLayout.getMeasuredHeight();


            int width = (getwidhtlayout / 3 - 8);
            LinearLayout.LayoutParams param = new LinearLayout.LayoutParams(width, (int) (getlayoutlayout - getResources().getDimensionPixelSize(R.dimen._22sdp)));
            param.setMargins(0, 5, 10, 0);
            binding.llDocChild.setLayoutParams(param);
            binding.llCheChild.setLayoutParams(param);
            binding.llStockChild.setLayoutParams(param);
            binding.llUnliChild.setLayoutParams(param);
            binding.llHosChild.setLayoutParams(param);
            binding.llCipChild.setLayoutParams(param);


        });


        binding.llCallsLayout.setOnTouchListener((v12, event) -> {

            HomeDashBoard.binding.viewPager1.setScrollEnabled(false);
            return false;
        });


        binding.inChart.lineChart.setOnTouchListener((v1, event) -> {
            HomeDashBoard.binding.viewPager1.setScrollEnabled(true);
            return false;
        });


        binding.inChart.lineChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
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
            binding.llDocHead.setVisibility(View.VISIBLE);
            binding.txtDocName.setText(String.format("%s Calls", CapDr));
            binding.textAverage.setText(String.format("Average %s", binding.txtDocName.getText().toString()));
        }
        if (ChemistNeed.equalsIgnoreCase("0")) {
            binding.llChemHead.setVisibility(View.VISIBLE);
            binding.txtCheName.setText(String.format("%s Calls", CapChemist));
        }
        if (StockistNeed.equalsIgnoreCase("0")) {
            binding.llStockHead.setVisibility(View.VISIBLE);
            binding.txtStockName.setText(String.format("%s Calls", CapStockist));
        }
        if (UnDrNeed.equalsIgnoreCase("0")) {
            binding.llUnliHead.setVisibility(View.VISIBLE);
            binding.txtUnliName.setText(String.format("%s Calls", CapUnDr));
        }
        if (CipNeed.equalsIgnoreCase("0")) {
            binding.llCipHead.setVisibility(View.VISIBLE);
            binding.txtCipName.setText(String.format("%s Calls", CapCip));
        }
        if (HospNeed.equalsIgnoreCase("0")) {
            binding.llHosHead.setVisibility(View.VISIBLE);
            binding.txtHosName.setText(String.format("%s Calls", CapHos));
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

    private void SetcallDetailsInLineChart() {
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


                binding.txtDocCount.setText(doc_current_callcount + " / " + Doctor_list.length());
                binding.txtCheCount.setText(che_current_callcount + " / " + Chemist_list.length());
                binding.txtStockCount.setText(stockiest_current_callcount + " / " + Stockiest_list.length());
                binding.txtUnlistCount.setText(unlistered_current_callcount + " / " + unlistered_list.length());
                binding.txtCipCount.setText(cip_current_callcount + " / " + cip_list.length());
                binding.txtHosCount.setText(hos_current_callcount + " / " + hos_list.length());


                int doc_progress_value, che_progress_value, stockiest_progress_value, unlistered_progress_value, cip_progress_value, hos_progress_value;

                doc_progress_value = computePercent(doc_current_callcount, Doctor_list.length());
                che_progress_value = computePercent(che_current_callcount, Chemist_list.length());
                stockiest_progress_value = computePercent(stockiest_current_callcount, Stockiest_list.length());
                unlistered_progress_value = computePercent(unlistered_current_callcount, unlistered_list.length());
                cip_progress_value = computePercent(cip_current_callcount, cip_list.length());
                hos_progress_value = computePercent(hos_current_callcount, hos_list.length());

                binding.txtDocValue.setText(doc_progress_value + "%");
                binding.txtCheValue.setText(che_progress_value + "%");
                binding.txtStockValue.setText(stockiest_progress_value + "%");
                binding.txtUnlistedValue.setText(unlistered_progress_value + "%");
                binding.txtCipValue.setText(stockiest_progress_value + "%");
                binding.txtHosValue.setText(unlistered_progress_value + "%");

                binding.docProgressBar.setProgress(doc_progress_value);
                binding.cheProgressBar.setProgress(che_progress_value);
                binding.stockProgressBar.setProgress(stockiest_progress_value);
                binding.unlistProgressBar.setProgress(unlistered_progress_value);
                binding.cipProgressBar.setProgress(cip_progress_value);
                binding.hosProgressBar.setProgress(hos_progress_value);


                binding.inChart.lineChart.clear();
                setLineChartData("1");
                binding.inChart.llMonthlayout.setVisibility(View.VISIBLE);

            } else {
                binding.inChart.llMonthlayout.setVisibility(View.GONE);
                binding.llDocChild.setOnClickListener(null);
                binding.llCheChild.setOnClickListener(null);
                binding.llStockChild.setOnClickListener(null);
                binding.llUnliChild.setOnClickListener(null);
                binding.llHosChild.setOnClickListener(null);
                binding.llCipChild.setOnClickListener(null);


                binding.txtDocValue.setText("0%");
                binding.txtCheValue.setText("0%");
                binding.txtStockValue.setText("0%");
                binding.txtUnlistedValue.setText("0%");
                binding.txtCipValue.setText("0%");
                binding.txtHosValue.setText("0%");

                binding.txtDocCount.setText("0 / " + Doctor_list.length());
                binding.txtCheCount.setText("0 / " + Chemist_list.length());
                binding.txtStockCount.setText(" 0/ " + Stockiest_list.length());
                binding.txtUnlistCount.setText(" 0/ " + unlistered_list.length());
                binding.txtCipCount.setText("0 / " + cip_list.length());
                binding.txtHosCount.setText("0 / " + hos_list.length());

            }


        } catch (Exception a) {
            a.printStackTrace();
        }


    }

    void setLineChartData(String Custype) {


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
            binding.inChart.txtMonthOne.setText(sdfs.format(calendar.getTime()));
            binding.inChart.txtMonthTwo.setText(sdfs.format(calendar1.getTime()));
            binding.inChart.txtMonthThree.setText(sdfs.format(calendar2.getTime()));
            binding.inChart.txtMonthOne.setVisibility(View.VISIBLE);
            binding.inChart.txtMonthTwo.setVisibility(View.VISIBLE);
            binding.inChart.txtMonthThree.setVisibility(View.VISIBLE);
            binding.textDate.setText(String.format("%s %d - %s %d", sdfs.format(calendar.getTime()), calendar.get(Calendar.YEAR), sdfs.format(calendar2.getTime()), calendar2.get(Calendar.YEAR)));


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
            binding.inChart.txtMonthOne.setText(sdfs.format(calendar1.getTime()));
            binding.inChart.txtMonthTwo.setText(sdfs.format(calendar2.getTime()));
            binding.inChart.txtMonthThree.setVisibility(View.INVISIBLE);
            binding.inChart.txtMonthOne.setVisibility(View.VISIBLE);
            binding.inChart.txtMonthTwo.setVisibility(View.VISIBLE);
            binding.textDate.setText(String.format("%s %d - %s %d", sdfs.format(calendar1.getTime()), calendar1.get(Calendar.YEAR), sdfs.format(calendar2.getTime()), calendar2.get(Calendar.YEAR)));


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
            binding.inChart.txtMonthOne.setText(sdfs.format(calendar2.getTime()));
            binding.inChart.txtMonthTwo.setVisibility(View.INVISIBLE);
            binding.inChart.txtMonthThree.setVisibility(View.INVISIBLE);
            binding.inChart.txtMonthOne.setVisibility(View.VISIBLE);
            binding.textDate.setText(String.format("%s %d", sdfs.format(calendar2.getTime()), calendar2.get(Calendar.YEAR)));

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
        binding.inChart.lineChart.getLegend().setEnabled(false);
        LineData lineData1 = new LineData(dataSet);
        binding.inChart.lineChart.setData(lineData1);

        Typeface customTypeface = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            customTypeface = getResources().getFont(R.font.satoshi_medium);
        }


        XAxis xAxis = binding.inChart.lineChart.getXAxis();
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


        binding.inChart.lineChart.getXAxis().setEnabled(true);
        binding.inChart.lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        YAxis leftYAxis = binding.inChart.lineChart.getAxisLeft();
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
        leftYAxis.setGridColor(getResources().getColor(R.color.charline_color));

        leftYAxis.setDrawZeroLine(true);
        leftYAxis.setZeroLineColor(getResources().getColor(R.color.gray_45));
        leftYAxis.setZeroLineWidth(1.2f);


        CustomMarkerView mv = new CustomMarkerView(requireContext(), R.layout.linechartpopup, Custype, firstDateStr, fifteenthDateStr, enddate, firstDatepastmonth, fifteenthDatepastmonth, enddatepastmonth, firstDatecurrent, fifteenthDatecurrent, enddatecurrent, key);
        mv.setChartView(binding.inChart.lineChart);

        binding.inChart.lineChart.setMarkerView(mv);
        YAxis rightAxis = binding.inChart.lineChart.getAxisRight();

        rightAxis.setAxisLineColor(getResources().getColor(R.color.white));
        rightAxis.setDrawAxisLine(false);
        rightAxis.setDrawLabels(false);
        binding.inChart.lineChart.setDrawMarkerViews(true);


        binding.inChart.lineChart.getAxisRight().setEnabled(false);
        binding.inChart.lineChart.getDescription().setEnabled(false);
        binding.inChart.lineChart.setPinchZoom(true);
        binding.inChart.lineChart.setDoubleTapToZoomEnabled(false);
        binding.inChart.lineChart.invalidate();
        binding.inChart.lineChart.setTouchEnabled(true);
        binding.inChart.lineChart.setDragEnabled(true);
        binding.inChart.lineChart.setScaleEnabled(true);
        binding.inChart.lineChart.setOnChartValueSelectedListener(null);
        binding.inChart.lineChart.setHighlightPerTapEnabled(true);
        binding.inChart.lineChart.getLegend().setEnabled(false);
        binding.inChart.lineChart.setScaleMinima(0f, 0f);
        binding.inChart.lineChart.fitScreen();
        binding.inChart.lineChart.setScaleEnabled(false);
        binding.inChart.lineChart.setExtraBottomOffset(5f);

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


        binding.inChart.lineChart.setRenderer(new ImageLineChartRenderer(binding.inChart.lineChart, binding.inChart.lineChart.getAnimator(), binding.inChart.lineChart.getViewPortHandler(), starBitmap));

    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {

            case R.id.ll_doc_child:
                binding.textAverage.setText(String.format("Average %s", binding.txtDocName.getText().toString()));
                binding.imgDownTriangleDoc.setVisibility(View.VISIBLE);
                binding.imgDownTriangleChe.setVisibility(View.GONE);
                binding.imgDownTriangleStockiest.setVisibility(View.GONE);
                binding.imgDownTriangleUnlistered.setVisibility(View.GONE);
                binding.imgDownTriangleCip.setVisibility(View.GONE);
                binding.imgDownTriangleHos.setVisibility(View.GONE);
                binding.inChart.lineChart.clear();

                setLineChartData("1");


                break;
            case R.id.ll_che_child:
                binding.textAverage.setText(String.format("Average %s", binding.txtCheName.getText().toString()));
                binding.imgDownTriangleDoc.setVisibility(View.GONE);
                binding.imgDownTriangleChe.setVisibility(View.VISIBLE);
                binding.imgDownTriangleStockiest.setVisibility(View.GONE);
                binding.imgDownTriangleUnlistered.setVisibility(View.GONE);
                binding.imgDownTriangleCip.setVisibility(View.GONE);
                binding.imgDownTriangleHos.setVisibility(View.GONE);
                binding.inChart.lineChart.clear();

                setLineChartData("2");


                break;
            case R.id.ll_stock_child:
                binding.textAverage.setText(String.format("Average %s", binding.txtStockName.getText().toString()));
                binding.imgDownTriangleDoc.setVisibility(View.GONE);
                binding.imgDownTriangleChe.setVisibility(View.GONE);
                binding.imgDownTriangleStockiest.setVisibility(View.VISIBLE);
                binding.imgDownTriangleUnlistered.setVisibility(View.GONE);
                binding.imgDownTriangleCip.setVisibility(View.GONE);
                binding.imgDownTriangleHos.setVisibility(View.GONE);
                binding.inChart.lineChart.clear();

                setLineChartData("3");


                break;
            case R.id.ll_unli_child:
                binding.textAverage.setText(String.format("Average %s", binding.txtUnliName.getText().toString()));
                binding.imgDownTriangleDoc.setVisibility(View.GONE);
                binding.imgDownTriangleChe.setVisibility(View.GONE);
                binding.imgDownTriangleStockiest.setVisibility(View.GONE);
                binding.imgDownTriangleUnlistered.setVisibility(View.VISIBLE);
                binding.imgDownTriangleCip.setVisibility(View.GONE);
                binding.imgDownTriangleHos.setVisibility(View.GONE);
                binding.inChart.lineChart.clear();

                setLineChartData("4");


                break;
            case R.id.ll_cip_child:
                binding.textAverage.setText(String.format("Average %s", binding.txtCipName.getText().toString()));
                binding.imgDownTriangleDoc.setVisibility(View.GONE);
                binding.imgDownTriangleChe.setVisibility(View.GONE);
                binding.imgDownTriangleStockiest.setVisibility(View.GONE);
                binding.imgDownTriangleUnlistered.setVisibility(View.GONE);
                binding.imgDownTriangleCip.setVisibility(View.VISIBLE);
                binding.imgDownTriangleHos.setVisibility(View.GONE);
                binding.inChart.lineChart.clear();

                setLineChartData("5");


                break;
            case R.id.ll_hos_child:
                binding.textAverage.setText(String.format("Average %s Calls", binding.txtHosName.getText().toString()));
                binding.imgDownTriangleDoc.setVisibility(View.GONE);
                binding.imgDownTriangleChe.setVisibility(View.GONE);
                binding.imgDownTriangleStockiest.setVisibility(View.GONE);
                binding.imgDownTriangleUnlistered.setVisibility(View.GONE);
                binding.imgDownTriangleCip.setVisibility(View.GONE);
                binding.imgDownTriangleHos.setVisibility(View.VISIBLE);
                binding.inChart.lineChart.clear();
                setLineChartData("6");
                break;

        }

    }


    public void onDestroyView() {

        super.onDestroyView();
    }


}


