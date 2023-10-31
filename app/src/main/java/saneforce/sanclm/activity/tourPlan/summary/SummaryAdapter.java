package saneforce.sanclm.activity.tourPlan.summary;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.tourPlan.ModelClass;

public class SummaryAdapter extends RecyclerView.Adapter<SummaryAdapter.MyViewHolder> {

    public ArrayList<ModelClass> arrayList = new ArrayList<>();
    Context context;
    SummaryInterface summaryInterface;

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
        holder.setIsRecyclable(false);

        if (!modelClasses.getDayNo().isEmpty()){
            ModelClass.SessionList modelClass = new ModelClass.SessionList();
            for (int i=0;i<modelClasses.getSessionList().size();i++){
                modelClass = modelClasses.getSessionList().get(i);
                holder.workTypeModelArray.add(modelClass.getWorkType());
                holder.clusterCount += modelClass.getCluster().size();
                holder.jwCount += modelClass.getJC().size();
                holder.drCount += modelClass.getListedDr().size();
                holder.chemistCount += modelClass.getChemist().size();
                holder.stockiestCount += modelClass.getStockiest().size();
                holder.unListedDrCount += modelClass.getUnListedDr().size();
                holder.cipCount += modelClass.getCip().size();
                holder.hospitalCount += modelClass.getHospital().size();
            }

            holder.date.setText(modelClasses.getDate());

            for (ModelClass.SessionList.WorkType workType : holder.workTypeModelArray){
                if (!workType.getName().isEmpty()){
                    holder.workTypeNames.add(workType.getName());
                }
            }

            if (holder.workTypeNames.size() == 0){
                holder.itemView.setVisibility(View.GONE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(0, 0));
            }else {
                holder.itemView.setVisibility(View.VISIBLE);
                holder.itemView.setLayoutParams(new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                boolean onlyHoliday = false;
                for (String workType : holder.workTypeNames){
                    if (workType.equalsIgnoreCase("Holiday") || workType.equalsIgnoreCase("Weekly Off")){
                        onlyHoliday = true;
                    } else {
                        onlyHoliday = false;
                        break;
                    }
                }

                StringBuilder workTypeNameBuilder = new StringBuilder();
                for (String workType : holder.workTypeNames){
                    if (workTypeNameBuilder.length() == 0){
                        workTypeNameBuilder = new StringBuilder(workType);
                    }else {
                        workTypeNameBuilder.append(" ,    ").append(workType);
                    }
                }

                holder.workType.setText(workTypeNameBuilder);

                if (onlyHoliday){
                    holder.iconLayout1.setVisibility(View.GONE);
                    holder.iconLayout2.setVisibility(View.GONE);
                } else {
                    holder.cluster.setText(String.valueOf(holder.clusterCount));
                    holder.jointWork.setText(String.valueOf(holder.jwCount));
                    holder.listedDr.setText(String.valueOf(holder.drCount));
                    holder.chemist.setText(String.valueOf(holder.chemistCount));
                    holder.stockiest.setText(String.valueOf(holder.stockiestCount));
                    holder.unListedDr.setText(String.valueOf(holder.unListedDrCount));
                    holder.cip.setText(String.valueOf(holder.cipCount));
                    holder.hospital.setText(String.valueOf(holder.hospitalCount));
                }
            }

        }


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {

                ModelClass modelClass = new ModelClass(arrayList.get(holder.getAbsoluteAdapterPosition()));
                summaryInterface.onClick(modelClass,holder.getAbsoluteAdapterPosition());
            }
        });

    }

    @Override
    public int getItemCount () {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView date,workType,cluster,jointWork,listedDr,chemist,stockiest,unListedDr,cip,hospital;
        int clusterCount = 0,jwCount = 0,drCount = 0, chemistCount = 0,stockiestCount = 0,unListedDrCount = 0,cipCount = 0,hospitalCount = 0;
        LinearLayout iconLayout1,iconLayout2;

        ArrayList<ModelClass.SessionList.WorkType> workTypeModelArray = new ArrayList<>();
        ArrayList<String> workTypeNames = new ArrayList<>();


        public MyViewHolder (@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            workType = itemView.findViewById(R.id.workTypeLabel);
            cluster = itemView.findViewById(R.id.clusterCount);
            jointWork = itemView.findViewById(R.id.jwCount);
            listedDr = itemView.findViewById(R.id.listedDrCount);
            chemist = itemView.findViewById(R.id.chemistCount);
            stockiest = itemView.findViewById(R.id.stockiestCount);
            unListedDr = itemView.findViewById(R.id.unListedDrCount);
            cip = itemView.findViewById(R.id.cipCount);
            hospital = itemView.findViewById(R.id.hospCount);

            iconLayout1 = itemView.findViewById(R.id.iconLayout1);
            iconLayout2 = itemView.findViewById(R.id.iconLayout2);

        }
    }


}
