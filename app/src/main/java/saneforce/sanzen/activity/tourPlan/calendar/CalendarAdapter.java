package saneforce.sanzen.activity.tourPlan.calendar;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.tourPlan.TourPlanActivity;
import saneforce.sanzen.activity.tourPlan.model.ModelClass;
import saneforce.sanzen.activity.tourPlan.model.OneBuildModelClass;
import saneforce.sanzen.storage.SharedPref;

public class CalendarAdapter extends RecyclerView.Adapter<CalendarAdapter.MyViewHolder> {

    ArrayList<ModelClass> inputData = new ArrayList<>();
    ArrayList<OneBuildModelClass> OneBuildInputData = new ArrayList<>();
     OnDayClickInterface onDayClickInterface;
     OnDayClickOneBuildInterface onDayClickOneBuildInterface;
    Context context;
//    private int OneBuildSetup = 0;

    public CalendarAdapter () {
    }

    public CalendarAdapter (ArrayList<ModelClass> inputData, Context context, OnDayClickInterface onDayClickInterface) {
        this.inputData = inputData;
        this.context = context;
        this.onDayClickInterface = onDayClickInterface;

    }
    public CalendarAdapter ( Context context, ArrayList<OneBuildModelClass> inputDataOneBuild,OnDayClickOneBuildInterface onDayClickOneBuildInterface) {
        this.context = context;
        this.OneBuildInputData = inputDataOneBuild;
        this.onDayClickOneBuildInterface = onDayClickOneBuildInterface;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tp_calendar_cell, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder (@NonNull MyViewHolder holder, int position) {
        String fwFlag = "";
        if(SharedPref.getOneBuild(context).equalsIgnoreCase("0")){
            OneBuildModelClass oneBuildModelClass = OneBuildInputData.get(holder.getAbsoluteAdapterPosition());
            String date = oneBuildModelClass.getDayNo();
            holder.dateNo.setText(date);
            if (!date.isEmpty() && oneBuildModelClass.getSessionList() != null && !oneBuildModelClass.getSessionList().isEmpty() && oneBuildModelClass.getSessionList().get(0).getWorkType() != null  && !oneBuildModelClass.getSessionList().get(0).getWorkType().getName().isEmpty()) { //if work type is not empty means tour plan added for the date
                holder.cornerImage.setVisibility(View.VISIBLE);
                fwFlag = oneBuildModelClass.getSessionList().get(0).getWorkType().getFWFlg();
                for (OneBuildModelClass.SessionList sessionList: oneBuildModelClass.getSessionList()) {
                    if (sessionList.getWorkType().getFWFlg().equalsIgnoreCase("F")) {
                        fwFlag = sessionList.getWorkType().getFWFlg();
                        break;
                    }
                }
            } else {
                holder.cornerImage.setVisibility(View.GONE);
            }
            if(!date.isEmpty()){
                if(Integer.valueOf(date) < TourPlanActivity.JoningDate && Integer.valueOf(oneBuildModelClass.getMonth()) == TourPlanActivity.JoiningMonth  && Integer.valueOf(oneBuildModelClass.getYear())==TourPlanActivity.JoinYear ) {
                    TourPlanActivity.binding.calendarPrevButton.setEnabled(false);
                    holder.mainLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.bg_pink10));
                    TourPlanActivity.binding.calendarPrevButton.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.less_than_gray, null));
                    holder.itemView.setEnabled(false);
                }else {
                    holder.itemView.setEnabled(true);
                }
            }
            holder.itemView.setOnClickListener(v -> onDayClickOneBuildInterface.onDayClickedOneBuild(holder.getAbsoluteAdapterPosition(), date, OneBuildInputData.get(holder.getAbsoluteAdapterPosition())));

        }else {
            ModelClass modelClass = inputData.get(holder.getAbsoluteAdapterPosition());
            String date = modelClass.getDayNo();
            holder.dateNo.setText(date);
            if (!date.isEmpty() && modelClass.getSessionList() != null && !modelClass.getSessionList().isEmpty() && modelClass.getSessionList().get(0).getWorkType() != null && !modelClass.getSessionList().get(0).getWorkType().getName().isEmpty()) { //if work type is not empty means tour plan added for the date
                holder.cornerImage.setVisibility(View.VISIBLE);
                fwFlag = modelClass.getSessionList().get(0).getWorkType().getFWFlg();
                for (ModelClass.SessionList sessionList: modelClass.getSessionList()) {
                    if (sessionList.getWorkType().getFWFlg().equalsIgnoreCase("F")) {
                        fwFlag = sessionList.getWorkType().getFWFlg();
                        break;
                    }
                }
            } else {
                holder.cornerImage.setVisibility(View.GONE);
            }
            if (!date.isEmpty()) {
                if (Integer.valueOf(date) < TourPlanActivity.JoningDate && Integer.valueOf(modelClass.getMonth()) == TourPlanActivity.JoiningMonth && Integer.valueOf(modelClass.getYear()) == TourPlanActivity.JoinYear) {
                    TourPlanActivity.binding.calendarPrevButton.setEnabled(false);
                    holder.mainLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.bg_pink10));
                    TourPlanActivity.binding.calendarPrevButton.setImageDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.less_than_gray, null));
                    holder.itemView.setEnabled(false);
                } else {
                    holder.itemView.setEnabled(true);
                }
            }

            holder.itemView.setOnClickListener(v -> onDayClickInterface.onDayClicked(holder.getAbsoluteAdapterPosition(), date, inputData.get(holder.getAbsoluteAdapterPosition())));
        }

        GradientDrawable drawable = (GradientDrawable) context.getResources().getDrawable(R.drawable.event_point_background);
        if (fwFlag.equalsIgnoreCase("F")) {
            drawable.setColor(context.getResources().getColor(R.color.green_60));
            holder.cornerImage.setVisibility(View.VISIBLE);
        } else if (fwFlag.equalsIgnoreCase("L")) {
            drawable.setColor(context.getResources().getColor(R.color.red_60));
            holder.cornerImage.setVisibility(View.VISIBLE);
        } else if (fwFlag.equalsIgnoreCase("N")) {
            drawable.setColor(context.getResources().getColor(R.color.blue_60));
            holder.cornerImage.setVisibility(View.VISIBLE);
        } else if (fwFlag.equalsIgnoreCase("M")) {
            drawable.setColor(context.getResources().getColor(R.color.Hilo_bay_60));
            holder.cornerImage.setVisibility(View.VISIBLE);
        } else if (fwFlag.equalsIgnoreCase("RE")) {
            drawable.setColor(context.getResources().getColor(R.color.pink_60));
            holder.cornerImage.setVisibility(View.VISIBLE);
        } else if (fwFlag.equalsIgnoreCase("R")) {
            drawable.setColor(context.getResources().getColor(R.color.brown_60));
            holder.cornerImage.setVisibility(View.VISIBLE);
        } else if (fwFlag.equalsIgnoreCase("W")) {
            drawable.setColor(context.getResources().getColor(R.color.yellow_60));
            holder.cornerImage.setVisibility(View.VISIBLE);
        } else if (fwFlag.equalsIgnoreCase("H")) {
            drawable.setColor(context.getResources().getColor(R.color.lustylavender_60));
            holder.cornerImage.setVisibility(View.VISIBLE);
        } else {
            holder.cornerImage.setVisibility(View.GONE);
        }
        holder.cornerImage.setImageDrawable(drawable);
    }

    @Override
    public int getItemCount () {
        if(SharedPref.getOneBuild(context).equalsIgnoreCase("0"))
            return OneBuildInputData.size();
        else return inputData.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView dateNo;
        ImageView cornerImage;

        ConstraintLayout mainLayout;

        public MyViewHolder (@NonNull View itemView) {
            super(itemView);
            dateNo = itemView.findViewById(R.id.dateNo);
            cornerImage = itemView.findViewById(R.id.img_event_point);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
}
