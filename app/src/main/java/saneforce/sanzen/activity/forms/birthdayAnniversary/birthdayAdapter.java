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

public class birthdayAdapter extends RecyclerView.Adapter<birthdayAdapter.ViewHolder> {

    ArrayList<birthdayModel> birthdayList;
    Context context;

    public birthdayAdapter(ArrayList<birthdayModel> birthdayList, Context context) {
        this.birthdayList = birthdayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_birthday_adapter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        birthdayModel model = birthdayList.get(position);
        holder.doctorName.setText(model.getDoctorName());
        holder.birthDate.setText(model.getBirthDate());
//        try {
//            holder.back_clr.setBackgroundColor(Color.parseColor(model.getColor()));
//            holder.viewclr_1.setBackgroundColor(Color.parseColor(model.getColor()));
//            holder.line_1.setBackgroundColor(Color.parseColor(model.getColor()));
//            holder.line_2.setBackgroundColor(Color.parseColor(model.getColor()));
//            holder.line_3.setBackgroundColor(Color.parseColor(model.getColor()));
//        } catch (Exception e) {
//            // fallback color if parsing fails
//            holder.back_clr.setBackgroundColor(Color.parseColor("#E8F9FC"));
//        }
    }

    @Override
    public int getItemCount() {
        return birthdayList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView doctorName, birthDate;
        LinearLayout doctorsName,line_1,line_2,line_3,back_clr;
        View viewclr_1;
        @SuppressLint("WrongViewCast")
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            doctorName = itemView.findViewById(R.id.doctorName);
            birthDate = itemView.findViewById(R.id.birthDate);
            doctorsName = itemView.findViewById(R.id.doctorsName);
            line_1 = itemView.findViewById(R.id.line_1);
            line_2 = itemView.findViewById(R.id.line_2);
            line_3 = itemView.findViewById(R.id.line_3);
            viewclr_1 = itemView.findViewById(R.id.viewclr_1);
            back_clr=itemView.findViewById(R.id.back_clr);

        }
    }
}
