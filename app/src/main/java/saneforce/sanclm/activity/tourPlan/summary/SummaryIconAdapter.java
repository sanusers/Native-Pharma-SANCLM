package saneforce.sanclm.activity.tourPlan.summary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.tourPlan.model.ModelClass;


public class SummaryIconAdapter extends RecyclerView.Adapter<SummaryIconAdapter.MyViewHolder> {
    ArrayList<ModelClass.CountModel> modelClass;
    Context context;

    public SummaryIconAdapter (ArrayList<ModelClass.CountModel> modelClass, Context context) {
        this.modelClass = modelClass;
        this.context = context;
    }

    public SummaryIconAdapter () {
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tp_summary_icon_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder (@NonNull MyViewHolder holder, int position) {
        ModelClass.CountModel model = modelClass.get(holder.getAbsoluteAdapterPosition());

        switch (model.getName().toUpperCase()){
            case "CLUSTER" : {
                holder.iconImage.setImageDrawable(context.getResources().getDrawable(R.drawable.tp_cluster_location_ic));
                break;
            }
            case "JW" : {
                holder.iconImage.setImageDrawable(context.getResources().getDrawable(R.drawable.tp_joint_work_ic));
                break;
            }
            case "DR" : {
                holder.iconImage.setImageDrawable(context.getResources().getDrawable(R.drawable.tp_dr_icon));
                break;
            }
            case "CHEMIST" : {
                holder.iconImage.setImageDrawable(context.getResources().getDrawable(R.drawable.tp_chemist_icon));
                break;
            }
            case "STOCKIEST" : {
                holder.iconImage.setImageDrawable(context.getResources().getDrawable(R.drawable.tp_stockiest_icon));
                break;
            }
            case "UNLISTEDDR" : {
                holder.iconImage.setImageDrawable(context.getResources().getDrawable(R.drawable.tp_unlist_dr_icon));
                break;
            }
            case "CIP" : {
                holder.iconImage.setImageDrawable(context.getResources().getDrawable(R.drawable.tp_cip_icon));
                break;
            }
            case "HOSP" : {
                holder.iconImage.setImageDrawable(context.getResources().getDrawable(R.drawable.tp_hospital_icon));
                break;
            }
        }
        holder.count.setText(String.valueOf(model.getCount()));

    }

    @Override
    public int getItemCount () {
        return modelClass.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView iconImage;
        TextView count;
        public MyViewHolder (@NonNull View itemView) {
            super(itemView);
            iconImage = itemView.findViewById(R.id.icon);
            count = itemView.findViewById(R.id.countTxtView);

        }
    }
}
