package saneforce.sanzen.activity.Quiz.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.Quiz.model.QuizQuesNoModel;

public class QuizCountAdapter extends RecyclerView.Adapter<QuizCountAdapter.ViewHolder> {

    ArrayList<QuizQuesNoModel> quizQuesNoModels;
    Context context;

    public QuizCountAdapter(ArrayList<QuizQuesNoModel> quizQuesNoModels, Context context) {
        this.quizQuesNoModels = quizQuesNoModels;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quizcount_item, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuizQuesNoModel quizQuesNoModel = quizQuesNoModels.get(position);
        holder.txt_questno.setText(String.valueOf(quizQuesNoModel.getQuestionNumber()));

        if(quizQuesNoModel.isSelected()) {
            ViewCompat.setBackgroundTintList(holder.linearLayout, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.dark_purple)));
            holder.txt_questno.setTextColor(ContextCompat.getColor(context, R.color.white));
        }else if(quizQuesNoModel.isFinished()) {
            ViewCompat.setBackgroundTintList(holder.linearLayout, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.light_grey_3)));
            holder.txt_questno.setTextColor(ContextCompat.getColor(context, R.color.black));
        }else {
            ViewCompat.setBackgroundTintList(holder.linearLayout, ColorStateList.valueOf(ContextCompat.getColor(context, R.color.white)));
            holder.txt_questno.setTextColor(ContextCompat.getColor(context, R.color.black));
        }
    }

    @Override
    public int getItemCount() {
        return quizQuesNoModels.size();
    }

    public void setSelected(int questionNumber) {
        for (int i = 0; i<quizQuesNoModels.size(); i++) {
            QuizQuesNoModel quizQuesNoModel = quizQuesNoModels.get(i);
            if(quizQuesNoModel.getQuestionNumber() == questionNumber+1) {
                quizQuesNoModel.setSelected(true);
                quizQuesNoModels.set(i, quizQuesNoModel);
            }else {
                quizQuesNoModel.setSelected(false);
                quizQuesNoModels.set(i, quizQuesNoModel);
            }
        }
        notifyDataSetChanged();
    }

    public void setFinished(int questionNumber) {
        for (int i = 0; i<quizQuesNoModels.size(); i++) {
            QuizQuesNoModel quizQuesNoModel = quizQuesNoModels.get(i);
            if(quizQuesNoModel.getQuestionNumber() == questionNumber+1) {
                quizQuesNoModel.setFinished(true);
                quizQuesNoModels.set(i, quizQuesNoModel);
//            }else {
//                quizQuesNoModel.setFinished(false);
//                quizQuesNoModels.set(i, quizQuesNoModel);
            }
        }
//        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView txt_questno;
        LinearLayout linearLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            linearLayout = itemView.findViewById(R.id.day_bgd);
            txt_questno=itemView.findViewById(R.id.txt_questno);
        }
    }
}
