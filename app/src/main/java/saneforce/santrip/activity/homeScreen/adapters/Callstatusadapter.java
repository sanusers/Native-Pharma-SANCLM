package saneforce.santrip.activity.homeScreen.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.HomeDashBoard;

import saneforce.santrip.activity.homeScreen.modelClass.EventCalenderModelClass;


public class Callstatusadapter extends RecyclerView.Adapter<Callstatusadapter.CalendarViewHolder> {
    private ArrayList<EventCalenderModelClass> days;
    private Context context;
    LocalDate selectedMonth;


    public Callstatusadapter(ArrayList<EventCalenderModelClass> days, Context context, LocalDate selectedMonth) {
        this.days = days;
        this.context = context;
        this.selectedMonth = selectedMonth;
    }

    @Override
    public CalendarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.call_calderdate_layout, parent, false);
        return new CalendarViewHolder(view);
    }

    @SuppressLint({"Range"})
    @Override
    public void onBindViewHolder(@NonNull CalendarViewHolder holder, int position) {
        EventCalenderModelClass list = days.get(position);
        holder.dayTextView.setText(list.getDateID());


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM", Locale.ENGLISH);
        DateTimeFormatter formatter1 = DateTimeFormatter.ofPattern("yyy", Locale.ENGLISH);
        String fullMonthName = selectedMonth.format(formatter);
        String year = selectedMonth.format(formatter1);

        // set Event
        GradientDrawable drawable = (GradientDrawable) context.getResources().getDrawable(R.drawable.event_point_background);
        if (list.getWorktypeFlog().equalsIgnoreCase("F")) {
            drawable.setColor(context.getResources().getColor(R.color.green_60));
            holder.imageView.setVisibility(View.VISIBLE);
        } else if (list.getWorktypeFlog().equalsIgnoreCase("W")) {
            drawable.setColor(context.getResources().getColor(R.color.yellow_60));
            holder.imageView.setVisibility(View.VISIBLE);
        } else if (list.getWorktypeFlog().equalsIgnoreCase("H")) {
            drawable.setColor(context.getResources().getColor(R.color.lustylavender_60));
            holder.imageView.setVisibility(View.VISIBLE);
        } else if (list.getWorktypeFlog().equalsIgnoreCase("L")) {
            drawable.setColor(context.getResources().getColor(R.color.pink_60));
            holder.imageView.setVisibility(View.VISIBLE);
        } else if (list.getWorktypeFlog().equalsIgnoreCase("N")) {
            drawable.setColor(context.getResources().getColor(R.color.blue_60));
            holder.imageView.setVisibility(View.VISIBLE);
        } else {
            holder.imageView.setVisibility(View.GONE);
        }
        holder.imageView.setImageDrawable(drawable);


        // set SqureBox for background
        if (position == 0) {
            holder.linearLayout.setBackgroundResource(R.drawable.calender_background_a);
        } else if (position <= 6) {
            holder.linearLayout.setBackgroundResource(R.drawable.calender_background_b);

        } else if (position == 7 || position == 14 || position == 21 || position == 28 || position == 35) {
            holder.linearLayout.setBackgroundResource(R.drawable.calender_background_c);

        } else {
            holder.linearLayout.setBackgroundResource(R.drawable.calender_background_d);
        }

        holder.linearLayout.setOnClickListener(v -> {
            if (!list.getDateID().equalsIgnoreCase("")) {
                HomeDashBoard.binding.textDate.setText(String.format("%s %s, %s", fullMonthName, list.getDateID(), year));
                HomeDashBoard.binding.viewCalerderLayout.getRoot().setVisibility(View.GONE);
                //  HomeDashBoard.binding.tabLayout.getRoot().setVisibility(View.VISIBLE);
                HomeDashBoard.binding.tabLayout.setVisibility(View.VISIBLE);
                HomeDashBoard.binding.viewPager.setVisibility(View.VISIBLE);
            }
        });

    }

    @Override
    public int getItemCount() {
        return days.size();
    }

    public class CalendarViewHolder extends RecyclerView.ViewHolder {
        TextView dayTextView;

        ImageView imageView;

        LinearLayout linearLayout;

        public CalendarViewHolder(View itemView) {
            super(itemView);
            dayTextView = itemView.findViewById(R.id.cellDayText);
            imageView = itemView.findViewById(R.id.img_event_point);
            linearLayout = itemView.findViewById(R.id.day_bgd);
        }
    }


}


