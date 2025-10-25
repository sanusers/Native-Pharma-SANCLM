package saneforce.sanzen.activity.forms.birthdayAnniversary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanzen.R;

public class anniversaryAdapter extends RecyclerView.Adapter<anniversaryAdapter.ViewHolder> {

    ArrayList<anniversaryModel>anniversaryList;
    Context context;

    public anniversaryAdapter(ArrayList<anniversaryModel> anniversaryList, Context context) {
        this.anniversaryList = anniversaryList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_anniversary_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        anniversaryModel model = anniversaryList.get(position);
        holder.doctorName1.setText(model.getDoctorName());
        holder.anniversaryDate.setText(model.getAnniversaryDate());
//        try {
//            holder.back_clr1.setBackgroundColor(Color.parseColor(model.getColor()));
//            holder.viewclr_2.setBackgroundColor(Color.parseColor(model.getColor()));
//            holder.line_11.setBackgroundColor(Color.parseColor(model.getColor()));
//            holder.line_22.setBackgroundColor(Color.parseColor(model.getColor()));
//            holder.line_33.setBackgroundColor(Color.parseColor(model.getColor()));
//        } catch (Exception e) {
//            // fallback color if parsing fails
//            holder.back_clr1.setBackgroundColor(Color.parseColor("#E8F9FC"));
//        }
    }

    @Override
    public int getItemCount() {
        return anniversaryList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView doctorName1, anniversaryDate;
        LinearLayout doctorsName1,line_11,line_22,line_33,back_clr1;
        View viewclr_2;
        @SuppressLint("WrongViewCast")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            doctorName1 = itemView.findViewById(R.id.doctorName1);
            anniversaryDate = itemView.findViewById(R.id.anniversaryDate);
            doctorsName1 = itemView.findViewById(R.id.doctorsName1);
            line_11 = itemView.findViewById(R.id.line_11);
            line_22 = itemView.findViewById(R.id.line_22);
            line_33 = itemView.findViewById(R.id.line_33);
            viewclr_2 = itemView.findViewById(R.id.viewclr_2);
            back_clr1=itemView.findViewById(R.id.back_clr1);

        }
    }
}
