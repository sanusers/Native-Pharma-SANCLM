package saneforce.sanclm.activity.tourPlan.summary;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.tourPlan.ModelClass;

public class SummaryAdapter extends RecyclerView.Adapter<SummaryAdapter.MyViewHolder> {


    ArrayList<ModelClass> arrayList = new ArrayList<>();
    Context context;
    SummaryInterface summaryInterface;
    int clusterCount = 0,jwCount = 0,drCount = 0, chemistCount = 0,stockiestCount = 0,unListedDrCount = 0,cipCount = 0,hospitalCount = 0;

    public SummaryAdapter () {
    }


    public SummaryAdapter (ArrayList<ModelClass> arrayList, Context context,SummaryInterface summaryInterface) {
        this.arrayList = arrayList;
        this.context = context;
        this.summaryInterface = summaryInterface;
    }

    @NonNull
    @Override
    public SummaryAdapter.MyViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tp_summary_single_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder (@NonNull SummaryAdapter.MyViewHolder holder, int position) {
        ModelClass modelClasses = arrayList.get(holder.getAbsoluteAdapterPosition());
        ModelClass.SessionList modelClass = new ModelClass.SessionList();
        for (int i=0;i<modelClasses.getSessionList().size();i++){
            modelClass = modelClasses.getSessionList().get(i);
            clusterCount += modelClass.getCluster().size();
            jwCount += modelClass.getJC().size();
            drCount += modelClass.getListedDr().size();
            chemistCount += modelClass.getChemist().size();
            stockiestCount += modelClass.getStockiest().size();
            unListedDrCount += modelClass.getUnListedDr().size();
            cipCount += modelClass.getCip().size();
            hospitalCount += modelClass.getHospital().size();
        }

        holder.date.setText(modelClasses.getDate());
        holder.workType.setText(modelClasses.getSessionList().get(0).getWorkType().getName());
        holder.hqName.setText(modelClasses.getSessionList().get(0).getHQ().getName());
        holder.cluster.setText(String.valueOf(clusterCount));
        holder.jointWork.setText(String.valueOf(jwCount));
        holder.listedDr.setText(String.valueOf(drCount));
        holder.chemist.setText(String.valueOf(chemistCount));
        holder.stockiest.setText(String.valueOf(stockiestCount));
        holder.unListedDr.setText(String.valueOf(unListedDrCount));
        holder.cip.setText(String.valueOf(cipCount));
        holder.hospital.setText(String.valueOf(hospitalCount));

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
//                TourPlanActivity activity = (TourPlanActivity) context;
//                ((TourPlanActivity) context).populateSessionAdapter(modelClasses);
                summaryInterface.onClick(arrayList.get(holder.getAbsoluteAdapterPosition()),holder.getAbsoluteAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount () {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView date,workType, hqName,cluster,jointWork,listedDr,chemist,stockiest,unListedDr,cip,hospital;

        public MyViewHolder (@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            workType = itemView.findViewById(R.id.workTypeLabel);
            hqName = itemView.findViewById(R.id.hqName);
            cluster = itemView.findViewById(R.id.clusterCount);
            jointWork = itemView.findViewById(R.id.jwCount);
            listedDr = itemView.findViewById(R.id.listedDrCount);
            chemist = itemView.findViewById(R.id.chemistCount);
            stockiest = itemView.findViewById(R.id.stockiestCount);
            unListedDr = itemView.findViewById(R.id.unListedDrCount);
            cip = itemView.findViewById(R.id.cipCount);
            hospital = itemView.findViewById(R.id.hospCount);

        }
    }


}
