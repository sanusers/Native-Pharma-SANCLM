package saneforce.sanzen.activity.Quiz;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import saneforce.sanzen.R;

public class QuizCountAapter extends RecyclerView.Adapter<QuizCountAapter.Viewholder> {


    int coutn;
    Context context;

    public QuizCountAapter(int coutn, Context context) {
        this.coutn = coutn;
        this.context = context;
    }

    @NonNull
    @Override
    public QuizCountAapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quizcount_item, parent, false);
        return new QuizCountAapter.Viewholder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull QuizCountAapter.Viewholder holder, int position) {
        holder.txt_questno.setText(String.valueOf(position+1));

    }

    @Override
    public int getItemCount() {
        return coutn;
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        TextView txt_questno;
        public Viewholder(@NonNull View itemView) {
            super(itemView);

            txt_questno=itemView.findViewById(R.id.txt_questno);
        }
    }
}
