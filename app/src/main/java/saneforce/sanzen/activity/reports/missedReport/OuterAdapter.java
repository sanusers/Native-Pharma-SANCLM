package saneforce.sanzen.activity.reports.missedReport;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.reports.missedReport.InnerAdapter;
import saneforce.sanzen.activity.reports.missedReport.MissedStatsModel;

public class OuterAdapter extends RecyclerView.Adapter<OuterAdapter.OuterViewHolder> {
    private final Context context;
    private final List<List<MissedStatsModel>> allMonthsList;

    private final String sfCode;
    private final String date;

    public OuterAdapter(Context context, List<List<MissedStatsModel>> allMonthsList,String sfCode,String date) {
        this.context = context;
        this.allMonthsList = allMonthsList;
        this.sfCode = sfCode;
        this.date = date;
    }

    @NonNull
    @Override
    public OuterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_outer, parent, false);
        return new OuterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OuterViewHolder holder, int position) {
        List<MissedStatsModel> monthList = allMonthsList.get(position);
        //calender
        switch (position) {
            case 0:
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.MONTH, 0);
                Date currentMonthDate = calendar.getTime();
                String currentMonth = new SimpleDateFormat("MMMM", Locale.getDefault()).format(currentMonthDate);
                String currentYear = new SimpleDateFormat("yyyy", Locale.getDefault()).format(currentMonthDate);
                String formattedDate = currentMonth + " " + currentYear;
                holder.monthTxt.setText(formattedDate);
                break;
            case 1:
                Calendar calendar1 = Calendar.getInstance();
                calendar1.add(Calendar.MONTH, -1);
                Date previousMonthDate = calendar1.getTime();
                String previousMonth = new SimpleDateFormat("MMMM", Locale.getDefault()).format(previousMonthDate);
                String currentYear1 = new SimpleDateFormat("yyyy", Locale.getDefault()).format(previousMonthDate);
                String formattedDate1 = previousMonth + " " + currentYear1;
                holder.monthTxt.setText(formattedDate1);
                break;
            case 2:
                Calendar calendar2 = Calendar.getInstance();
                calendar2.add(Calendar.MONTH, -2);
                Date prePreviousMonthDate = calendar2.getTime();
                String prePreviousMonth = new SimpleDateFormat("MMMM", Locale.getDefault()).format(prePreviousMonthDate);
                String currentYear2 = new SimpleDateFormat("yyyy", Locale.getDefault()).format(prePreviousMonthDate);
                String formattedDate2 = prePreviousMonth + " " + currentYear2;
                holder.monthTxt.setText(formattedDate2);
                break;
        }
        String monthName = holder.monthTxt.getText().toString();
        InnerAdapter innerAdapter = new InnerAdapter(holder.itemView.getContext(), monthList,sfCode,date,monthName);
        holder.innerRecycler.setLayoutManager(new LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false));
        holder.innerRecycler.setAdapter(innerAdapter);
    }

    @Override
    public int getItemCount() {
        return allMonthsList.size();
    }
    static class OuterViewHolder extends RecyclerView.ViewHolder {
        RecyclerView innerRecycler;
        TextView monthTxt;
        public OuterViewHolder(@NonNull View itemView) {
            super(itemView);
            innerRecycler = itemView.findViewById(R.id.innerRecycler);
            monthTxt = itemView.findViewById(R.id.month);
        }
    }
}

//package saneforce.sanzen.activity.reports.missedReport;
//
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.LinearLayoutManager;
//import androidx.recyclerview.widget.RecyclerView;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import saneforce.sanzen.R;
//
//public class OuterAdapter extends RecyclerView.Adapter<OuterAdapter.OuterViewHolder> {
//
//    private List<MissedReportGraph> cardList;
//
//    public OuterAdapter() {
//        cardList = new ArrayList<>();
//    }
//
//    public void setData(List<MissedReportGraph> data) {
//        cardList.clear();
//        cardList.addAll(data);
//        notifyDataSetChanged();
//    }
//
//    @NonNull
//    @Override
//    public OuterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext())
//                .inflate(R.layout.item_outer, parent, false);
//        return new OuterViewHolder(view);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull OuterViewHolder holder, int position) {
//        MissedReportGraph card = cardList.get(position);
//        holder.tvType.setText(card.getType());
//        holder.tvTotal.setText(String.valueOf(card.getTotal()));
//        holder.tvVisited.setText(String.valueOf(card.getVisited()));
//        holder.tvMissed.setText(String.valueOf(card.getMissed()));
//
//        // Setup inner RecyclerView
//        InnerAdapter innerAdapter = new InnerAdapter();
//        holder.innerRecycler.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
//        holder.innerRecycler.setAdapter(innerAdapter);
//
//        if (card.getInnerList() != null) {
//            innerAdapter.setData(card.getInnerList());
//        }
//    }
//
//    @Override
//    public int getItemCount() {
//        return cardList.size();
//    }
//
//    static class OuterViewHolder extends RecyclerView.ViewHolder {
//        TextView tvType, tvTotal, tvVisited, tvMissed;
//        RecyclerView innerRecycler;
//
//        public OuterViewHolder(@NonNull View itemView) {
//            super(itemView);
//            tvType = itemView.findViewById(R.id.tvCardType);
//            tvTotal = itemView.findViewById(R.id.totalCount);
//            tvVisited = itemView.findViewById(R.id.visitedCount);
//            tvMissed = itemView.findViewById(R.id.missedCount);
//            innerRecycler = itemView.findViewById(R.id.innerRecycler);
//        }
//    }
//}
