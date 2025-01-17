package saneforce.sanzen.activity.Survey.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.Survey.model.SurveyModelClass;
import saneforce.sanzen.activity.activityModule.adapter.ActivityAdapter;

public class SurveyAdapter extends RecyclerView.Adapter<SurveyAdapter.ViewHolder> {
    private final Context context;
    private List<SurveyModelClass> surveyModelClassList;
    private final SurveyClickListener surveyClickListener;

    public SurveyAdapter(Context context, List<SurveyModelClass> surveyModelClassList, SurveyClickListener surveyClickListener) {
        this.context = context;
        this.surveyModelClassList = surveyModelClassList;
        this.surveyClickListener = surveyClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_child_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return surveyModelClassList.size();
    }

    public interface SurveyClickListener {
        void onSurveyClick(SurveyModelClass surveyModelClass, int  position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView activityName;
        RelativeLayout layout;
        ImageView imageView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            activityName=itemView.findViewById(R.id.txtActivityName);
            layout=itemView.findViewById(R.id.rl_layout);
            imageView=itemView.findViewById(R.id.img_arrow_1);
        }
    }

}
