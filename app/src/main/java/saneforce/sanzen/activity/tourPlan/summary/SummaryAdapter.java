package saneforce.sanzen.activity.tourPlan.summary;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.tourPlan.model.ModelClass;
import saneforce.sanzen.storage.SharedPref;

public class SummaryAdapter extends RecyclerView.Adapter<SummaryAdapter.MyViewHolder> {

    public ArrayList<ModelClass> arrayList = new ArrayList<>();
    Context context;
    SummaryInterface summaryInterface;
    SummaryIconAdapter summaryIconAdapter;

    public SummaryAdapter () {
    }

    public SummaryAdapter (ArrayList<ModelClass> arrayList, Context context,SummaryInterface summaryInterface) {
        this.arrayList = arrayList;
        this.context = context;
        this.summaryInterface = summaryInterface;
        summaryIconAdapter = new SummaryIconAdapter();
    }

    @NonNull
    @Override
    public SummaryAdapter.MyViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tp_summary_single_item,parent,false);
        return new MyViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder (@NonNull SummaryAdapter.MyViewHolder holder, int position) {
        ModelClass modelClasses = arrayList.get(holder.getAbsoluteAdapterPosition());
        holder.setIsRecyclable(false);

        if (!modelClasses.getDayNo().isEmpty()){
            ModelClass.SessionList modelClass = new ModelClass.SessionList();
            for (int i=0;i<modelClasses.getSessionList().size();i++){
                modelClass = modelClasses.getSessionList().get(i);
                holder.workTypeModelArray.add(modelClass.getWorkType());
                holder.hqModelArray.add(modelClass.getHQ());
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
                if (!workType.getName().isEmpty())
                    holder.workTypeNames.add(workType.getName());
            }

            if (holder.workTypeModelArray.size() == 1) {
                holder.workType.setText(holder.workTypeModelArray.get(0).getName());
                holder.hqName.setText(holder.hqModelArray.get(0).getName());
                if (SharedPref.getSfType(context).equalsIgnoreCase("2")) {
                    holder.hqName.setVisibility(View.VISIBLE);
                    holder.view.setVisibility(View.VISIBLE);
                } else {
                    holder.hqName.setVisibility(View.GONE);
                    holder.view.setVisibility(View.GONE);
                }
                holder.workTypeLayout2.setVisibility(View.GONE);
                holder.workTypeLayout3.setVisibility(View.GONE);
            } else if (holder.workTypeModelArray.size() == 2) {
                holder.workTypeLayout3.setVisibility(View.GONE);
                holder.workType.setText(holder.workTypeModelArray.get(0).getName());
                holder.hqName.setText(holder.hqModelArray.get(0).getName());
                holder.workType2.setText(holder.workTypeModelArray.get(1).getName());
                holder.hqName2.setText(holder.hqModelArray.get(1).getName());
                if (SharedPref.getSfType(context).equalsIgnoreCase("2")) {
                    holder.hqName.setVisibility(View.VISIBLE);
                    holder.view.setVisibility(View.VISIBLE);
                    holder.hqName2.setVisibility(View.VISIBLE);
                    holder.view2.setVisibility(View.VISIBLE);
                } else {
                    holder.hqName.setVisibility(View.GONE);
                    holder.hqName2.setVisibility(View.GONE);
                    holder.view.setVisibility(View.GONE);
                    holder.view2.setVisibility(View.GONE);
                }


            } else if (holder.workTypeModelArray.size() == 3) {
                holder.workType.setText(holder.workTypeModelArray.get(0).getName());
                holder.hqName.setText(holder.hqModelArray.get(0).getName());
                holder.workType2.setText(holder.workTypeModelArray.get(1).getName());
                holder.hqName2.setText(holder.hqModelArray.get(1).getName());
                holder.workType3.setText(holder.workTypeModelArray.get(2).getName());
                holder.hqName3.setText(holder.hqModelArray.get(2).getName());


                if (SharedPref.getSfType(context).equalsIgnoreCase("2")) {
                    holder.hqName.setVisibility(View.VISIBLE);
                    holder.view.setVisibility(View.VISIBLE);
                    holder.hqName2.setVisibility(View.VISIBLE);
                    holder.view2.setVisibility(View.VISIBLE);
                    holder.hqName3.setVisibility(View.VISIBLE);
                    holder.view3.setVisibility(View.VISIBLE);
                } else {
                    holder.hqName.setVisibility(View.GONE);
                    holder.hqName2.setVisibility(View.GONE);
                    holder.view.setVisibility(View.GONE);
                    holder.view2.setVisibility(View.GONE);
                    holder.hqName3.setVisibility(View.GONE);
                    holder.view3.setVisibility(View.GONE);
                }

                }

                boolean onlyHoliday = false;
                for (String workType : holder.workTypeNames){ // to find the work types among sessions are only holiday/weeklyOff are combinations of any other work types
                    if (workType.equalsIgnoreCase("Holiday") || workType.equalsIgnoreCase("Weekly Off") || workType.equalsIgnoreCase("Not Available")){
                        onlyHoliday = true;
                    } else {
                        onlyHoliday = false;
                        break;
                    }
                }

                //If work type is holiday/weeklyOff then no need to show any master icons and counts(some times there may be multiple sessions includes holiday/weeklyOff)
                //else show the icon with number of quantity
                if (!onlyHoliday) {
                    ArrayList<ModelClass.CountModel> countModel = new ArrayList<>();

                    if (holder.clusterCount > 0) {
                        ModelClass.CountModel clusterModel = new ModelClass.CountModel("Cluster",holder.clusterCount);
                        countModel.add(clusterModel);
                    }

                    if (holder.jwCount > 0) {
                        ModelClass.CountModel jwModel = new ModelClass.CountModel("JW",holder.jwCount);
                        countModel.add(jwModel);
                    }

                    if (holder.drCount > 0) {
                        ModelClass.CountModel drModel = new ModelClass.CountModel("DR",holder.drCount);
                        countModel.add(drModel);
                    }

                    if (holder.chemistCount > 0) {
                        ModelClass.CountModel chemistModel = new ModelClass.CountModel("Chemist",holder.chemistCount);
                        countModel.add(chemistModel);
                    }

                    if (holder.stockiestCount > 0) {
                        ModelClass.CountModel stockModel = new ModelClass.CountModel("Stockiest",holder.stockiestCount);
                        countModel.add(stockModel);
                    }

                    if (holder.unListedDrCount > 0) {
                        ModelClass.CountModel unDrModel = new ModelClass.CountModel("UnlistedDr",holder.unListedDrCount);
                        countModel.add(unDrModel);
                    }

                    if (holder.cipCount > 0) {
                        ModelClass.CountModel cipModel = new ModelClass.CountModel("CIP",holder.cipCount);
                        countModel.add(cipModel);
                    }

                    if (holder.hospitalCount > 0) {
                        ModelClass.CountModel hospModel = new ModelClass.CountModel("Hosp",holder.hospitalCount);
                        countModel.add(hospModel);
                    }

                    summaryIconAdapter = new SummaryIconAdapter(countModel,context);
                    RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context,5);
                    holder.recyclerView.setLayoutManager(layoutManager);
                    holder.recyclerView.setAdapter(summaryIconAdapter);
                }
//            }
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                ModelClass modelClass = new ModelClass(arrayList.get(holder.getAbsoluteAdapterPosition()));

                    summaryInterface.onClick(modelClass,holder.getAbsoluteAdapterPosition());


            }
        });

        holder.recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction() == MotionEvent.ACTION_UP)
                    return view.performClick();
                else
                    return false;
            }
        });

        holder.recyclerView.setOnClickListener(new View.OnClickListener() {
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

        TextView date,workType,workType2,workType3,hqName,hqName2,hqName3;
        View view,view2,view3;
        int clusterCount = 0,jwCount = 0,drCount = 0, chemistCount = 0,stockiestCount = 0,unListedDrCount = 0,cipCount = 0,hospitalCount = 0;
        LinearLayout workTypeLayout2,workTypeLayout3;
        RecyclerView recyclerView;
        ArrayList<ModelClass.SessionList.WorkType> workTypeModelArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> hqModelArray = new ArrayList<>();

        ArrayList<String> workTypeNames = new ArrayList<>();

        public MyViewHolder (@NonNull View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.date);
            workType = itemView.findViewById(R.id.workTypeLabel);
            workType2 = itemView.findViewById(R.id.workTypeLabel2);
            workType3 = itemView.findViewById(R.id.workTypeLabel3);
            hqName = itemView.findViewById(R.id.hqName);
            hqName2 = itemView.findViewById(R.id.hqName2);
            hqName3 = itemView.findViewById(R.id.hqName3);


            view = itemView.findViewById(R.id.view_fdummy);
            view2 = itemView.findViewById(R.id.view_fdummy2);
            view3 = itemView.findViewById(R.id.view_fdummy3);



            recyclerView = itemView.findViewById(R.id.summaryIconRecView);
            workTypeLayout2 = itemView.findViewById(R.id.workType2);
            workTypeLayout3 = itemView.findViewById(R.id.workType3);

        }
    }


}
