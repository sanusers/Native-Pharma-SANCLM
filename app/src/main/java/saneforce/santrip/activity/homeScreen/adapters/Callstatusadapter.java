package saneforce.santrip.activity.homeScreen.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.HomeDashBoard;

import saneforce.santrip.activity.homeScreen.fragment.worktype.WorkPlanFragment;
import saneforce.santrip.activity.homeScreen.modelClass.EventCalenderModelClass;
import saneforce.santrip.commonClasses.CommonUtilsMethods;


public class Callstatusadapter extends RecyclerView.Adapter<Callstatusadapter.CalendarViewHolder> {
    private ArrayList<EventCalenderModelClass> days;
    private Context context;
    LocalDate selectedMonth;
    CommonUtilsMethods commonUtilsMethods;


    public Callstatusadapter(ArrayList<EventCalenderModelClass> days, Context context, LocalDate selectedMonth) {
        this.days = days;
        this.context = context;
        this.selectedMonth = selectedMonth;
        commonUtilsMethods = new CommonUtilsMethods(context);
    }

    @NonNull
    @Override
    public CalendarViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.call_calderdate_layout, parent, false);
        return new CalendarViewHolder(view);
    }

    @SuppressLint({"Range", "UseCompatLoadingForDrawables"})
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
                @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy");
                Date strDate = null;
                try {
                    strDate = sdf.parse(String.format("%s %s, %s", fullMonthName, list.getDateID(), year));
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
               if (new Date().after(strDate)) {
                    HomeDashBoard.binding.textDate.setText(String.format("%s %s, %s", fullMonthName, list.getDateID(), year));
                    HomeDashBoard.binding.viewCalerderLayout.getRoot().setVisibility(View.GONE);
                    HomeDashBoard.binding.tabLayout.setVisibility(View.VISIBLE);
                    HomeDashBoard.binding.viewPager.setVisibility(View.VISIBLE);
                    HomeDashBoard.binding.viewDummy.setVisibility(View.VISIBLE);
                    HomeDashBoard.binding.imgDoubleVecer.setImageDrawable(context.getDrawable(R.drawable.arrow_bot_top_img));
                } else {
                    commonUtilsMethods.ShowToast(context, context.getString(R.string.not_chose_after_date), 100);
                }
            }
           /* if (list.getDateID().equalsIgnoreCase(CommonUtilsMethods.getCurrentDateDashBoard()) || list.getDateID() > CommonUtilsMethods.getCurrentDateDashBoard()) {
                if (!list.getDateID().equalsIgnoreCase("")) {
                    HomeDashBoard.binding.textDate.setText(String.format("%s %s, %s", fullMonthName, list.getDateID(), year));
                    HomeDashBoard.binding.viewCalerderLayout.getRoot().setVisibility(View.GONE);
                    //  HomeDashBoard.binding.tabLayout.getRoot().setVisibility(View.VISIBLE);
                    HomeDashBoard.binding.tabLayout.setVisibility(View.VISIBLE);
                    HomeDashBoard.binding.viewPager.setVisibility(View.VISIBLE);
                    HomeDashBoard.binding.viewDummy.setVisibility(View.VISIBLE);
                    HomeDashBoard.binding.imgDoubleVecer.setImageDrawable(context.getDrawable(R.drawable.arrow_bot_top_img));

                }
            } else {
                commonUtilsMethods.ShowToast(context, context.getString(R.string.submit_checkin), 100);
            }*/
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