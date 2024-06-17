package saneforce.sanzen.activity.leave;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;

import saneforce.sanzen.R;


public class Piechart_adapter extends RecyclerView.Adapter<Piechart_adapter.ViewHolder> {

    Context context;
    ArrayList<Leave_modelclass> chart_list ;
    PieDataSet pieDataSet,pieDataSet1,pieDataSet2,pieDataSet3;
    PieData pieData,pieData1,pieData2,pieData3;
    ArrayList<Integer> colors = new ArrayList<>();
    ArrayList<Integer> colors1 = new ArrayList<>();
    ArrayList<Integer> colors2 = new ArrayList<>();
    ArrayList<Integer> colors3 = new ArrayList<>();

    public Piechart_adapter(Context context, ArrayList<Leave_modelclass> chart_list) {
        this.context = context;
        this.chart_list = chart_list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.piechart_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        final Leave_modelclass pie_value = chart_list.get(position);
        String L_Taken = pie_value.getTakenleave();
        String L_Elgable = pie_value.getEligable();
        String L_Avalable = pie_value.getAvaolable();

        Log.d("chart", pie_value + "---" + L_Taken +"--"+L_Elgable+"--"+L_Avalable);



        Chartview(holder.pieChart,pie_value.getL_type(),pie_value.getEligable(),pie_value.getAvaolable(),pie_value.getTakenleave(),holder.c_val,holder.c_val_tol, holder.ltype_name,pie_value.getLtype(),
                holder. LOP,holder.chartview_value);
//pie_value.getEligable(),pie_value.getTakenleave()

    }

    @Override
    public int getItemCount() {
        return chart_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        PieChart pieChart,piechart1,piechart2,piechart3;
        LinearLayout Daycount;
        RelativeLayout chartview_value,chartview1,chartview2,chartview3;
        TextView c_val, c_val_tol, ltype_name,ltype,LOP;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            pieChart = itemView.findViewById(R.id.piechart);
            c_val = itemView.findViewById(R.id.c_val);
            c_val_tol = itemView.findViewById(R.id.c_val_tol);
            ltype_name = itemView.findViewById(R.id.ltype_name);
            LOP = itemView.findViewById(R.id.LOP);
            chartview_value = itemView.findViewById(R.id.chartview);
        }
    }


    @SuppressLint("SetTextI18n")
    public void Chartview(PieChart chart, String L_Available, String  L_Elgable, String L_taken, String taken, TextView c_val, TextView c_val_tol, TextView ltype_name, String pie_value,
                          TextView Ltype, RelativeLayout chartview_value) {
        int countdata1=0,countdata2=0;
        if(L_Available.equals("LOP")){
            chartview_value.setVisibility(View.GONE);
            Ltype.setVisibility(View.VISIBLE);

        }




        if(L_Available.equals("CL")){
            colors.clear();

            colors.add(Color.rgb(217, 217, 217));
            colors.add(Color.rgb(0, 198, 137));



            int Total_dates = Integer.parseInt(L_Elgable);
            int Apply_dates = Integer.parseInt(taken);
            int Balance_date = Total_dates-Apply_dates;
            if (Balance_date<0){
                Balance_date = 0;
            }
            ArrayList<PieEntry> lve_list = new ArrayList<PieEntry>();
            PieEntry pieEntry = new PieEntry(Apply_dates, "");
            lve_list.add(pieEntry);
            PieEntry pieEntry1 = new PieEntry(Balance_date, "");
            lve_list.add(pieEntry1);
            pieDataSet = new PieDataSet(lve_list, "");
            pieDataSet.setColors(colors);
            pieData = new PieData(pieDataSet);
            pieData.setValueTextSize(0f);
            pieData.setValueTextColor(Color.WHITE);
            chart.setData(pieData);
            chart.setUsePercentValues(false);
            chart.setDrawHoleEnabled(true);



            chart.setCenterTextSize(15f);
            chart.setCenterTextColor(Color.rgb(0, 0, 0));
            chart.setTransparentCircleRadius(40f);
            chart.setHoleRadius(89f);
            chart.animateXY(1400, 1400);
            String bal_val= String.valueOf(Balance_date);
            c_val.setText(bal_val);
            c_val_tol.setText("/"+Total_dates);
            ltype_name.setText(pie_value);

        } else if (L_Available.equals("PL")) {
            colors1.clear();

            colors1.add(Color.rgb(217, 217, 217));
            colors1.add(Color.rgb(133, 193, 233));

            int Total_dates = Integer.parseInt(L_Elgable);
            int Apply_dates = Integer.parseInt(taken);

            int Balance_date = Total_dates-Apply_dates;
            if (Balance_date<0){
                Balance_date = 0;
            }
            ArrayList<PieEntry> lve_list = new ArrayList<PieEntry>();
            PieEntry pieEntry = new PieEntry(Apply_dates, "");
            lve_list.add(pieEntry);
            PieEntry pieEntry1 = new PieEntry(Balance_date, "");
            lve_list.add(pieEntry1);

            pieDataSet1 = new PieDataSet(lve_list, "");
            pieDataSet1.setColors(colors1);
            pieData1 = new PieData(pieDataSet1);
            pieData1.setValueTextSize(0f);
            pieData1.setValueTextColor(Color.WHITE);
            chart.setData(pieData1);
            chart.setUsePercentValues(false);
            chart.setDrawHoleEnabled(true);
            chart.setCenterTextSize(15f);
            chart.setCenterTextColor(Color.rgb(0, 0, 0));
            chart.setTransparentCircleRadius(40f);
            chart.setHoleRadius(89f);
            chart.animateXY(1400, 1400);
            String bal_val= String.valueOf(Balance_date);
            c_val.setText(bal_val);
            c_val_tol.setText("/"+Total_dates);
            ltype_name.setText(pie_value);

        }else if (L_Available.equals("SL")) {
            colors2.clear();
            colors2.add(Color.rgb(217, 217, 217));
            colors2.add(Color.rgb(241, 83, 110));

            int Total_dates = Integer.parseInt(L_Elgable);
            int Apply_dates = Integer.parseInt(taken);
            int Balance_date = Total_dates-Apply_dates;
            if (Balance_date<0){
                Balance_date = 0;
            }

            ArrayList<PieEntry> lve_list = new ArrayList<PieEntry>();
            PieEntry pieEntry = new PieEntry(Apply_dates, "");
            lve_list.add(pieEntry);
            PieEntry pieEntry1 = new PieEntry(Balance_date, "");
            lve_list.add(pieEntry1);

            pieDataSet2 = new PieDataSet(lve_list, "");
            pieDataSet2.setColors(colors2);
            pieData2 = new PieData(pieDataSet2);
            pieData2.setValueTextSize(0f);
            pieData2.setValueTextColor(Color.WHITE);
            chart.setData(pieData2);
            chart.setUsePercentValues(false);
            chart.setDrawHoleEnabled(true);
            chart.setCenterTextSize(15f);
            chart.setCenterTextColor(Color.rgb(0, 0, 0));
            chart.setTransparentCircleRadius(40f);
            chart.setHoleRadius(89f);
            chart.animateXY(1400, 1400);
            String bal_val= String.valueOf(Balance_date);
            c_val.setText(bal_val);
            c_val_tol.setText("/"+Total_dates);
            ltype_name.setText(pie_value);

        }
        else if(L_Available.equals("LOP")) {
            colors3.clear();
            chartview_value.setVisibility(View.GONE);
            Ltype.setVisibility(View.VISIBLE);
            String centertxt = String.valueOf(countdata2);
            int seconddata = (0);
            ArrayList<PieEntry> values = new ArrayList<PieEntry>();
            PieEntry pieEntry = new PieEntry(countdata1, "");
            values.add(pieEntry);
            PieEntry pieEntry1 = new PieEntry(seconddata, "");
            values.add(pieEntry1);
            pieDataSet3 = new PieDataSet(values, "");
            pieData3 = new PieData(pieDataSet3);
            pieData3.setValueFormatter(new PercentFormatter());
            chart.setData(pieData3);
            chart.setUsePercentValues(false);
            chart.setDrawHoleEnabled(true);
            chart.setCenterTextSize(15f);
            chart.setCenterTextColor(Color.rgb(0, 0, 0));
            chart.setTransparentCircleRadius(40f);
            chart.setHoleRadius(89f);
            pieDataSet3.setColors(colors3);
            pieData3.setValueTextSize(0f);
            pieData3.setValueTextColor(Color.WHITE);
            chart.animateXY(1400, 1400);
            int lopTaken =   Integer.parseInt(taken);
            if (lopTaken<0){
                lopTaken = 0;
            }
            Ltype.setText(String.valueOf(lopTaken));
            ltype_name.setText(pie_value);
        }

        Description description = chart.getDescription();
        description.setEnabled(false);
        Legend legend = chart.getLegend();
        legend.setEnabled(false);

    }

}
