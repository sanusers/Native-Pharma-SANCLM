package saneforce.sanzen.activity.standardTourPlan.calendarScreen.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.model.SelectedDCRModel;

public class CalendarDCRAdapter extends RecyclerView.Adapter<CalendarDCRAdapter.MyViewHolder> {

    private Context context;
    private List<SelectedDCRModel> selectedDcrModelList;

    public CalendarDCRAdapter() {
    }

    public CalendarDCRAdapter(Context context, List<SelectedDCRModel> selectedDcrModelList) {
        this.context = context;
        this.selectedDcrModelList = selectedDcrModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stp_dcr_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        SelectedDCRModel selectedDcrModel = selectedDcrModelList.get(position);
        holder.dcrImg.setImageResource(selectedDcrModel.getImgID());
        holder.count.setText(String.valueOf(selectedDcrModel.getCount()));
    }

    @Override
    public int getItemCount() {
        return selectedDcrModelList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView count;
        ImageView dcrImg;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            dcrImg = itemView.findViewById(R.id.dcr_type_img);
            count = itemView.findViewById(R.id.dcr_type_count);
        }
    }
}
