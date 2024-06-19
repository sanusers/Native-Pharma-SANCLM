package saneforce.sanzen.activity.reports.dayReport.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.sql.Time;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.reports.dayReport.model.SlideRatingDetalisModelClass;
import saneforce.sanzen.utility.TimeUtils;

public class DayReportSlideDetailsAdapter extends RecyclerView.Adapter<DayReportSlideDetailsAdapter.ViewHolder> {

   ArrayList<SlideRatingDetalisModelClass> callDetailingLists;
   Context context;

    public DayReportSlideDetailsAdapter(ArrayList<SlideRatingDetalisModelClass> callDetailingLists, Context context) {
        this.callDetailingLists = callDetailingLists;
        this.context = context;
    }

    @NonNull
    @Override
    public DayReportSlideDetailsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.day_report_slide_details, parent, false);
        return new DayReportSlideDetailsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayReportSlideDetailsAdapter.ViewHolder holder, int position) {
        holder.txtbrandName.setText(callDetailingLists.get(position).getProduct_Name());
        holder.TxtDuration.setText(calculateDuration(callDetailingLists.get(position).getStartTime(),callDetailingLists.get(position).getEndTime()));
        holder.TxtFeedback.setText(callDetailingLists.get(position).getFeedbk());
        holder.rating_bar.setRating(Float.parseFloat(String.valueOf(callDetailingLists.get(position).getRating())));
        holder.rating_bar.setEnabled(false);

        
        if(!calculateDuration(callDetailingLists.get(position).getStartTime(),callDetailingLists.get(position).getEndTime()).equalsIgnoreCase("00:00:00")){
            holder.imgView.setVisibility(View.VISIBLE);
        }else {
            holder.imgView.setVisibility(View.GONE);
        }
        holder.imgView.setOnClickListener(v -> {
            CharSequence initialGuideTex = Html.fromHtml(" Start Time : " +TimeUtils.timeConverter(callDetailingLists.get(position).getStartTime())  +"<br><br>"+ " End Time  : " +TimeUtils.timeConverter(callDetailingLists.get(position).getEndTime()));
            showTimelinePopUp(v,initialGuideTex);
        });

        holder.TxtFeedback.setOnClickListener(v -> {
            popUp(holder.TxtFeedback,callDetailingLists.get(position).getFeedbk());
        });

        holder.txtbrandName.setOnClickListener(v -> {
            popUp(holder.txtbrandName,callDetailingLists.get(position).getProduct_Name());
        });
    }


    @Override
    public int getItemCount() {
        return callDetailingLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView txtbrandName, TxtDuration,TxtFeedback;
        RatingBar rating_bar;

        ImageView imgView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtbrandName=itemView.findViewById(R.id.txt_brandName);
            TxtDuration=itemView.findViewById(R.id.txt_duration);
            TxtFeedback=itemView.findViewById(R.id.txt_feedback);
            rating_bar=itemView.findViewById(R.id.rating_bar);
            imgView=itemView.findViewById(R.id.img_duration);

        }
    }
    public String calculateDuration(String fromTime, String toTime) {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());

            Date fromDate = format.parse(fromTime);
            Date toDate = format.parse(toTime);

            long difference = toDate.getTime() - fromDate.getTime();

            long seconds = difference / 1000 % 60;
            long minutes = difference / (1000 * 60) % 60;
            long hours = difference / (1000 * 60 * 60);

            return String.format("%02d:%02d",minutes, seconds);
        } catch (ParseException e) {
            e.printStackTrace();
            return "00:00:00";
        }
    }
    private void showTimelinePopUp(View view, CharSequence timeline) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.timeline_popup_top, null);
        TextView timelineTV = popupView.findViewById(R.id.timeline);
        timelineTV.setText(timeline);
        PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, false);
        popupWindow.setOutsideTouchable(true);
        popupWindow.showAsDropDown(view);
        ImageView close = popupView.findViewById(R.id.img_close);
        close.setOnClickListener(closeView -> {
            popupWindow.dismiss();
        });
    }
    private void popUp(View v, String name) {
        PopupWindow popup = new PopupWindow(context);
        View layout = LayoutInflater.from(context).inflate(R.layout.popup_text, null);
        popup.setContentView(layout);
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView tv_name = layout.findViewById(R.id.tv_name);
        tv_name.setText(name);
        popup.setOutsideTouchable(true);
        popup.showAsDropDown(v);
    }
}