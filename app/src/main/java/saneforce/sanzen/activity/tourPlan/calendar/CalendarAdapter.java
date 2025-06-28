package saneforce.sanzen.activity.tourPlan.calendar;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
        if(SharedPref.getOneBuild(context).equalsIgnoreCase("0")){
            OneBuildModelClass oneBuildModelClass = OneBuildInputData.get(holder.getAbsoluteAdapterPosition());
            String date = oneBuildModelClass.getDayNo();
            holder.dateNo.setText(date);
            if (!date.isEmpty() && !oneBuildModelClass.getSessionList().get(0).getWorkType().getName().isEmpty()) //if work type is not empty means tour plan added for the date
                holder.cornerImage.setVisibility(View.VISIBLE);

            else holder.cornerImage.setVisibility(View.GONE);
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
            if (!date.isEmpty() && !modelClass.getSessionList().get(0).getWorkType().getName().isEmpty()) //if work type is not empty means tour plan added for the date
                holder.cornerImage.setVisibility(View.VISIBLE);

            else holder.cornerImage.setVisibility(View.GONE);
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
            cornerImage = itemView.findViewById(R.id.cornerImage);
            mainLayout = itemView.findViewById(R.id.mainLayout);
        }
    }
}
