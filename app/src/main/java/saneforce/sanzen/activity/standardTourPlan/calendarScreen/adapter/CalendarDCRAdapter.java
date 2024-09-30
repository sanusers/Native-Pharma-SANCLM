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
import saneforce.sanzen.activity.standardTourPlan.calendarScreen.model.DCRModel;

public class CalendarDCRAdapter extends RecyclerView.Adapter<CalendarDCRAdapter.MyViewHolder> {

    private Context context;
    private List<DCRModel> dcrModelList;

    public CalendarDCRAdapter() {
    }

    public CalendarDCRAdapter(Context context, List<DCRModel> dcrModelList) {
        this.context = context;
        this.dcrModelList = dcrModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stp_dcr_list, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DCRModel dcrModel = dcrModelList.get(position);
        holder.dcrImg.setImageResource(dcrModel.getImgID());
        holder.count.setText(String.valueOf(dcrModel.getCount()));
    }

    @Override
    public int getItemCount() {
        return dcrModelList.size();
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
