package saneforce.sanzen.activity.survey.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.survey.model.SurveyModelClass;
import saneforce.sanzen.utility.TimeUtils;

public class SurveyAdapter extends RecyclerView.Adapter<SurveyAdapter.ViewHolder> {
    private final Context context;
    private List<SurveyModelClass> surveyModelClassList;
    private final SurveyClickListener surveyClickListener;
    private int selectedPosition = -1;

    public SurveyAdapter(Context context, List<SurveyModelClass> surveyModelClassList, SurveyClickListener surveyClickListener) {
        this.context = context;
        this.surveyModelClassList = surveyModelClassList;
        this.surveyClickListener = surveyClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.survey_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SurveyModelClass surveyModelClass = surveyModelClassList.get(position);
        String formatedDate = TimeUtils.GetConvertedDate(TimeUtils.FORMAT_4, TimeUtils.FORMAT_6, surveyModelClass.getToDate());

        holder.surveyName.setText(surveyModelClass.getSurveyName());
//        holder.surveyExpireDate.setText(formatedDate);
        holder.surveyExpireDate.setVisibility(View.GONE);
        holder.layout.setOnClickListener(view -> {
            selectedPosition = position;
            notifyDataSetChanged();
            surveyClickListener.onSurveyClick(surveyModelClass, position);
        });

        if (selectedPosition == position) {
            holder.layout.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_purple));
            holder.surveyName.setTextColor(ContextCompat.getColor(context,R.color.white));
            holder.surveyExpireDate.setBackground(ContextCompat.getDrawable(context,R.drawable.selector_box));
            holder.imageView.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.greater_than_white));
        } else {
            holder.layout.setBackground(ContextCompat.getDrawable(context,R.drawable.selector_box));
            holder.surveyName.setTextColor(ContextCompat.getColor(context,R.color.dark_purple));
            holder.surveyExpireDate.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_light_grey_1));
            holder.imageView.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.greater_than_purple));
        }
    }

    public void changeSelectedPosition(int selectedPosition) {
        this.selectedPosition = selectedPosition;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return surveyModelClassList.size();
    }

    public interface SurveyClickListener {
        void onSurveyClick(SurveyModelClass surveyModelClass, int  position);
    }

    public void updateDataList(List<SurveyModelClass> surveyModelClassList) {
        this.surveyModelClassList = surveyModelClassList;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView surveyName, surveyExpireDate;
        ConstraintLayout layout;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            surveyName =itemView.findViewById(R.id.tv_name);
            surveyExpireDate =itemView.findViewById(R.id.tv_date);
            layout=itemView.findViewById(R.id.constraint_main);
            imageView=itemView.findViewById(R.id.listArrow);
        }
    }

}
