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

import org.json.JSONArray;

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
import saneforce.santrip.roomdatabase.CallTableDetails.CallTableDao;
import saneforce.santrip.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.santrip.roomdatabase.RoomDB;
import saneforce.santrip.storage.SharedPref;
import saneforce.santrip.utility.TimeUtils;

public class CallAnalysisFragment extends Fragment implements View.OnClickListener {
    @SuppressLint("StaticFieldLeak")
    public static CallAnalysisFagmentBinding callAnalysisBinding;
    public static String key;
    public static JSONArray Doctor_list, Chemist_list, Stockiest_list, unlistered_list, cip_list, hos_list;
     public static Context context;
    CommonUtilsMethods commonUtilsMethods;

    public static int DrCallsCount, CheCallsCount, StkCallsCount, UnlCallSCount, CipCallsCount, HosCallsCount;

    static CallTableDao callTableDao;
    RoomDB db;
    private MasterDataDao masterDataDao;
    @Override
    public void onResume() {
        super.onResume();
        Log.d("Fragment_STATUS","OnResume");

    }
    @SuppressLint({"MissingInflatedId", "ClickableViewAccessibility"})
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("Fragment_STATUS","OnResume");
        callAnalysisBinding = CallAnalysisFagmentBinding.inflate(inflater);
        View v = callAnalysisBinding.getRoot();
        setScreenDesign();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        context = requireContext();
        db = RoomDB.getDatabase(getActivity());
        callTableDao=db.callTableDao();
        masterDataDao = db.masterDataDao();
        HiddenVisibleFunctions();
        SetcallDetailsInLineChart();
        return v;
    }
    public static int computePercent(int current, int total) {
        int percent = 0;
        if (total > 0) percent = current * 100 / total;
        return percent;
    }

    public static void SetcallDetailsInLineChart() {
        try {
            Calendar calendar = Calendar.getInstance();
            int month = calendar.get(Calendar.MONTH)+1;

                DrCallsCount = callTableDao.getCurrentMonthCallsCount(  String.valueOf(month), "1");
                CheCallsCount = callTableDao.getCurrentMonthCallsCount(  String.valueOf(month), "2");
                StkCallsCount = callTableDao.getCurrentMonthCallsCount(  String.valueOf(month), "3");
                UnlCallSCount = callTableDao.getCurrentMonthCallsCount(  String.valueOf(month), "4");
                CipCallsCount = callTableDao.getCurrentMonthCallsCount(  String.valueOf(month), "5");
                HosCallsCount = callTableDao.getCurrentMonthCallsCount(  String.valueOf(month), "6");
                callAnalysisBinding.txtDocCount.setText(String.valueOf(DrCallsCount));
                callAnalysisBinding.txtCheCount.setText(String.valueOf(CheCallsCount));
                callAnalysisBinding.txtStockCount.setText(String.valueOf(StkCallsCount));
                callAnalysisBinding.txtUnlistCount.setText(String.valueOf(UnlCallSCount));
                callAnalysisBinding.txtCipCount.setText(String.valueOf(CipCallsCount));
                callAnalysisBinding.txtHosCount.setText(String.valueOf(HosCallsCount));

                if (!SharedPref.getDesig(context).equalsIgnoreCase("MR")) {
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
                    callAnalysisBinding.txtDocCount.setText(DrCallsCount + " / " + Doctor_list.length());
                    callAnalysisBinding.txtCheCount.setText(CheCallsCount + " / " + Chemist_list.length());
                    callAnalysisBinding.txtStockCount.setText(StkCallsCount + " / " + Stockiest_list.length());
                    callAnalysisBinding.txtUnlistCount.setText(UnlCallSCount + " / " + unlistered_list.length());
                    callAnalysisBinding.txtCipCount.setText(CipCallsCount + " / " + cip_list.length());
                    callAnalysisBinding.txtHosCount.setText(HosCallsCount + " / " + hos_list.length());

                    int doc_progress_value, che_progress_value, stockiest_progress_value, unlistered_progress_value, cip_progress_value, hos_progress_value;
                    doc_progress_value = computePercent(DrCallsCount, Doctor_list.length());
                    che_progress_value = computePercent(CheCallsCount, Chemist_list.length());
                    stockiest_progress_value = computePercent(StkCallsCount, Stockiest_list.length());
                    unlistered_progress_value = computePercent(UnlCallSCount, unlistered_list.length());
                    cip_progress_value = computePercent(CipCallsCount, cip_list.length());
                    hos_progress_value = computePercent(HosCallsCount, hos_list.length());

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

                if (SharedPref.getDrNeed(context).equalsIgnoreCase("0")) {
                    callAnalysisBinding.imgDownTriangleDoc.setVisibility(View.VISIBLE);
                    callAnalysisBinding.imgDownTriangleChe.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleStockiest.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleUnlistered.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleCip.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleHos.setVisibility(View.GONE);
                    setLineChartData("1", context);
                } else if (SharedPref.getChmNeed(context).equalsIgnoreCase("0")) {
                    callAnalysisBinding.imgDownTriangleDoc.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleChe.setVisibility(View.VISIBLE);
                    callAnalysisBinding.imgDownTriangleStockiest.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleUnlistered.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleCip.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleHos.setVisibility(View.GONE);
                    setLineChartData("2", context);
                } else if (SharedPref.getStkNeed(context).equalsIgnoreCase("0")) {
                    callAnalysisBinding.imgDownTriangleDoc.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleChe.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleStockiest.setVisibility(View.VISIBLE);
                    callAnalysisBinding.imgDownTriangleUnlistered.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleCip.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleHos.setVisibility(View.GONE);
                    setLineChartData("3", context);
                } else if (SharedPref.getUnlNeed(context).equalsIgnoreCase("0")) {
                    callAnalysisBinding.imgDownTriangleDoc.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleChe.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleStockiest.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleUnlistered.setVisibility(View.VISIBLE);
                    callAnalysisBinding.imgDownTriangleCip.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleHos.setVisibility(View.GONE);
                    setLineChartData("4", context);
                } else if (SharedPref.getCipNeed(context).equalsIgnoreCase("0")) {
                    callAnalysisBinding.imgDownTriangleDoc.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleChe.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleStockiest.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleUnlistered.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleCip.setVisibility(View.VISIBLE);
                    callAnalysisBinding.imgDownTriangleHos.setVisibility(View.GONE);
                    setLineChartData("5",  context);
                } else if (SharedPref.getHospNeed(context).equalsIgnoreCase("0")) {
                    callAnalysisBinding.imgDownTriangleDoc.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleChe.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleStockiest.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleUnlistered.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleCip.setVisibility(View.GONE);
                    callAnalysisBinding.imgDownTriangleHos.setVisibility(View.VISIBLE);
                    setLineChartData("6", context);
                }


//            } else {
//                callAnalysisBinding.inChart.llMonthlayout.setVisibility(View.GONE);
//                callAnalysisBinding.llDocChild.setOnClickListener(null);
//                callAnalysisBinding.llCheChild.setOnClickListener(null);
//                callAnalysisBinding.llStockChild.setOnClickListener(null);
//                callAnalysisBinding.llUnliChild.setOnClickListener(null);
//                callAnalysisBinding.llHosChild.setOnClickListener(null);
//                callAnalysisBinding.llCipChild.setOnClickListener(null);
//
//
//                if (!SharedPref.getDesig(context).equalsIgnoreCase("MR")) {
//                    callAnalysisBinding.txtDocCount.setText("0");
//                    callAnalysisBinding.txtCheCount.setText("0");
//                    callAnalysisBinding.txtStockCount.setText("0");
//                    callAnalysisBinding.txtUnlistCount.setText("0");
//                    callAnalysisBinding.txtCipCount.setText("0");
//                    callAnalysisBinding.txtHosCount.setText("0");
//                } else {
//                    callAnalysisBinding.txtDocCount.setText("0 / " + Doctor_list.length());
//                    callAnalysisBinding.txtCheCount.setText("0 / " + Chemist_list.length());
//                    callAnalysisBinding.txtStockCount.setText(" 0/ " + Stockiest_list.length());
//                    callAnalysisBinding.txtUnlistCount.setText(" 0/ " + unlistered_list.length());
//                    callAnalysisBinding.txtCipCount.setText("0 / " + cip_list.length());
//                    callAnalysisBinding.txtHosCount.setText("0 / " + hos_list.length());
//
//                    callAnalysisBinding.txtDocValue.setText("0%");
//                    callAnalysisBinding.txtCheValue.setText("0%");
//                    callAnalysisBinding.txtStockValue.setText("0%");
//                    callAnalysisBinding.txtUnlistedValue.setText("0%");
//                    callAnalysisBinding.txtCipValue.setText("0%");
//                    callAnalysisBinding.txtHosValue.setText("0%");
//                }
//
//
//            }

        } catch (Exception a) {
            a.printStackTrace();
        }


    }

    public static void setLineChartData(String Custype, Context context) {

        //   A  -  CurrentMonth  A1  is = 1-15 ,A2  is = 1-31
        //   B  -  PastMonth     B1  is = 1-15 ,B2  is = 1-31
        //  C -  PastMonth      C1  is = 1-15 ,C2  is = 1-31


        SimpleDateFormat sdf = new SimpleDateFormat("dd");
        SimpleDateFormat sdfs = new SimpleDateFormat("MMMM");

        Calendar MonthC = Calendar.getInstance();
        MonthC.add(Calendar.MONTH, -2);
        MonthC.set(Calendar.DAY_OF_MONTH, 1);
        MonthC.set(Calendar.DAY_OF_MONTH, MonthC.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date lastDate = MonthC.getTime();
        String MonthCLastDate = sdf.format(lastDate);
        String month = String.valueOf(MonthC.get(Calendar.MONTH) + 1);


        Calendar MonthB = Calendar.getInstance();
        MonthB.add(Calendar.MONTH, -1);
        MonthB.set(Calendar.DAY_OF_MONTH, 1);

        MonthB.set(Calendar.DAY_OF_MONTH, MonthB.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date lastDate1 = MonthB.getTime();
        String MonthBLastDate = sdf.format(lastDate1);

        String month1 = String.valueOf(MonthB.get(Calendar.MONTH) + 1);


        Calendar MonthA = Calendar.getInstance();
        MonthA.set(Calendar.DAY_OF_MONTH, 1);
        MonthA.set(Calendar.DAY_OF_MONTH, MonthA.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date lastDate2 = MonthA.getTime();
        String MonthALastDate = sdf.format(lastDate2);

        ArrayList<Entry> entries = new ArrayList<>();
        boolean isMonthC = callTableDao.isMonthDataAvailableForCustType(Custype, month);
        boolean isMonthB = callTableDao.isMonthDataAvailableForCustType(Custype, month1);



        List<Integer> listYrange = new ArrayList<>();
        if (isMonthC) {
            key = "3";
            callAnalysisBinding.inChart.txtMonthOne.setText(sdfs.format(MonthC.getTime()));
            callAnalysisBinding.inChart.txtMonthTwo.setText(sdfs.format(MonthB.getTime()));
            callAnalysisBinding.inChart.txtMonthThree.setText(sdfs.format(MonthA.getTime()));
            callAnalysisBinding.inChart.txtMonthOne.setVisibility(View.VISIBLE);
            callAnalysisBinding.inChart.txtMonthTwo.setVisibility(View.VISIBLE);
            callAnalysisBinding.inChart.txtMonthThree.setVisibility(View.VISIBLE);
            callAnalysisBinding.textDate.setText(String.format("%s %d - %s %d", sdfs.format(MonthC.getTime()), MonthC.get(Calendar.YEAR), sdfs.format(MonthA.getTime()), MonthA.get(Calendar.YEAR)));

            int xaxis1 = callTableDao.getCallsCountByRange("C1", Custype);
            int xaxis2 = callTableDao.getCallsCountByRange("C2", Custype);
            int xaxis3 = callTableDao.getCallsCountByRange("B1", Custype);
            int xaxis4 = callTableDao.getCallsCountByRange("B2", Custype);
            int xaxis5 = callTableDao.getCallsCountByRange("A1", Custype);
            int xaxis6 = callTableDao.getCallsCountByRange("A2", Custype);

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

        } else if (isMonthB) {
            key = "2";
            callAnalysisBinding.inChart.txtMonthOne.setText(sdfs.format(MonthB.getTime()));
            callAnalysisBinding.inChart.txtMonthTwo.setText(sdfs.format(MonthA.getTime()));
            callAnalysisBinding.inChart.txtMonthThree.setVisibility(View.INVISIBLE);
            callAnalysisBinding.inChart.txtMonthOne.setVisibility(View.VISIBLE);
            callAnalysisBinding.inChart.txtMonthTwo.setVisibility(View.VISIBLE);
            callAnalysisBinding.textDate.setText(String.format("%s %d - %s %d", sdfs.format(MonthB.getTime()), MonthB.get(Calendar.YEAR), sdfs.format(MonthA.getTime()), MonthA.get(Calendar.YEAR)));

            int xaxis3 = callTableDao.getCallsCountByRange("B1", Custype);
            int xaxis4 = callTableDao.getCallsCountByRange("B2", Custype);
            int xaxis5 = callTableDao.getCallsCountByRange("A1", Custype);
            int xaxis6 = callTableDao.getCallsCountByRange("A2", Custype);

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
            callAnalysisBinding.inChart.txtMonthOne.setText(sdfs.format(MonthA.getTime()));
            callAnalysisBinding.inChart.txtMonthTwo.setVisibility(View.INVISIBLE);
            callAnalysisBinding.inChart.txtMonthThree.setVisibility(View.INVISIBLE);
            callAnalysisBinding.inChart.txtMonthOne.setVisibility(View.VISIBLE);
            callAnalysisBinding.textDate.setText(String.format("%s %d", sdfs.format(MonthA.getTime()), MonthA.get(Calendar.YEAR)));

            int xaxis5 = callTableDao.getCallsCountByRange("A1", Custype);
            int xaxis6 = callTableDao.getCallsCountByRange("A2", Custype);

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


                if (isMonthC) {

                    if (value == 0f) {
                        return "";
                    } else if (value == 1f) {
                        return "1" + getSuperscript("s") + " - 15" + getSuperscript("t");
                    } else if (value == 2f) {
                        return "1" + getSuperscript("s") + " - " + MonthCLastDate + getSuperscript("t");

                    } else if (value == 3f) {
                        return "1" + getSuperscript("s") + " - 15" + getSuperscript("t");
                    } else if (value == 4f) {
                        return "1" + getSuperscript("s") + " - " + MonthBLastDate + getSuperscript("t");

                    } else if (value == 5f) {
                        return "1" + getSuperscript("s") + " - 15" + getSuperscript("t");
                    } else if (value == 6f) {
                        if (Integer.valueOf(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_7)) > 15) {
                            return "1" + getSuperscript("s") + " - " + MonthALastDate + getSuperscript("t");
                        } else {
                            return "";
                        }
                    } else {
                        return "";
                    }


                } else if (isMonthB) {
                    if (value == 0f) {
                        return "";
                    } else if (value == 1f) {
                        return "1" + getSuperscript("s") + " - 15" + getSuperscript("t");
                    } else if (value == 2f) {
                        return "1" + getSuperscript("s") + " - " + MonthBLastDate + getSuperscript("t");
                    } else if (value == 3f) {
                        return "1" + getSuperscript("s") + " - 15" + getSuperscript("t");

                    } else if (value == 4f) {
                        if (Integer.valueOf(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_7)) > 15) {
                            return "1" + getSuperscript("s") + " - " + MonthALastDate + getSuperscript("t");
                        } else {
                            return "";
                        }
                    } else {
                        return "";
                    }

                } else {

                    if (value == 0f) {
                        return "";

                    } else if (value == 1f) {
                        return "1" + getSuperscript("s") + " - 15" + getSuperscript("t");
                    } else if (value == 2f) {
                        if (Integer.valueOf(TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_7)) > 15) {
                            return "1" + getSuperscript("s") + " - " + MonthALastDate + getSuperscript("t");
                        } else {
                            return "";
                        }
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


        CustomMarkerView mv = new CustomMarkerView(context, R.layout.linechartpopup, Custype, callTableDao, key);
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
        if (SharedPref.getDrNeed(requireContext()).equalsIgnoreCase("0")) {
            callAnalysisBinding.llDocHead.setVisibility(View.VISIBLE);
            callAnalysisBinding.txtDocName.setText(SharedPref.getDrCap(requireContext()));
            callAnalysisBinding.textAverage.setText(String.format("%s %s %s", getString(R.string.average), callAnalysisBinding.txtDocName.getText().toString(), getString(R.string.calls)));
        }
        if (SharedPref.getChmNeed(requireContext()).equalsIgnoreCase("0")) {
            callAnalysisBinding.llChemHead.setVisibility(View.VISIBLE);
            callAnalysisBinding.txtCheName.setText(SharedPref.getChmCap(requireContext()));
        }
        if (SharedPref.getStkNeed(requireContext()).equalsIgnoreCase("0")) {
            callAnalysisBinding.llStockHead.setVisibility(View.VISIBLE);
            callAnalysisBinding.txtStockName.setText(SharedPref.getStkCap(requireContext()));
        }
        if (SharedPref.getUnlNeed(requireContext()).equalsIgnoreCase("0")) {
            callAnalysisBinding.llUnliHead.setVisibility(View.VISIBLE);
            callAnalysisBinding.txtUnliName.setText(SharedPref.getUNLcap(requireContext()));
        }
        if (SharedPref.getCipNeed(requireContext()).equalsIgnoreCase("0")) {
            callAnalysisBinding.llCipHead.setVisibility(View.VISIBLE);
            callAnalysisBinding.txtCipName.setText(SharedPref.getCipCaption(requireContext()));
        }
        if (SharedPref.getHospNeed(requireContext()).equalsIgnoreCase("0")) {
            callAnalysisBinding.llHosHead.setVisibility(View.VISIBLE);
            callAnalysisBinding.txtHosName.setText(SharedPref.getHospCaption(requireContext()));
        }

        callAnalysisBinding.llCallsLayout.setOnTouchListener((v12, event) -> {
            HomeDashBoard.binding.viewPager1.setScrollEnabled(false);
            return false;
        });

        callAnalysisBinding.inChart.lineChart.setOnTouchListener((v1, event) -> {
            HomeDashBoard.binding.viewPager1.setScrollEnabled(true);
            return false;
        });



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

        Doctor_list = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR + SharedPref.getHqCode(context)).getMasterSyncDataJsonArray();
        Chemist_list = masterDataDao.getMasterDataTableOrNew(Constants.CHEMIST + SharedPref.getHqCode(context)).getMasterSyncDataJsonArray();
        Stockiest_list = masterDataDao.getMasterDataTableOrNew(Constants.STOCKIEST + SharedPref.getHqCode(context)).getMasterSyncDataJsonArray();
        unlistered_list = masterDataDao.getMasterDataTableOrNew(Constants.UNLISTED_DOCTOR + SharedPref.getHqCode(context)).getMasterSyncDataJsonArray();
        cip_list = masterDataDao.getMasterDataTableOrNew(Constants.CIP + SharedPref.getHqCode(context)).getMasterSyncDataJsonArray();
        hos_list = masterDataDao.getMasterDataTableOrNew(Constants.HOSPITAL + SharedPref.getHqCode(context)).getMasterSyncDataJsonArray();
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

                setLineChartData("1",context);


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

                setLineChartData("2", context);


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

                setLineChartData("3", context);


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

                setLineChartData("4", context);


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

                setLineChartData("5", context);


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
                setLineChartData("6",  context);
                break;

        }

    }


    public void onDestroyView() {

        super.onDestroyView();
    }


    void setScreenDesign (){
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


    }



}


