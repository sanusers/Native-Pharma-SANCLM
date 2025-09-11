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
import java.util.List;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.tourPlan.model.ModelClass;
import saneforce.sanzen.activity.tourPlan.model.OneBuildModelClass;
import saneforce.sanzen.storage.SharedPref;

public class SummaryAdapter extends RecyclerView.Adapter<SummaryAdapter.MyViewHolder> {

    public ArrayList<ModelClass> arrayList = new ArrayList<>();
    public ArrayList<OneBuildModelClass> arrayListOneBuild = new ArrayList<>();

    Context context;
    SummaryInterface summaryInterface;
    SummaryInterfaceOneBuild summaryInterfaceOneBuild;
    SummaryIconAdapter summaryIconAdapter;
//    private int OneBuildSetup = 0;

    public SummaryAdapter() {
    }

    public SummaryAdapter(ArrayList<ModelClass> arrayList, Context context, SummaryInterface summaryInterface) {
        this.arrayList = arrayList;
        this.context = context;
        this.summaryInterface = summaryInterface;
        summaryIconAdapter = new SummaryIconAdapter();
    }

    public SummaryAdapter(Context context, ArrayList<OneBuildModelClass> arrayList, SummaryInterfaceOneBuild summaryInterfaceOneBuild) {
        this.arrayListOneBuild = arrayList;
        this.context = context;
        this.summaryInterfaceOneBuild = summaryInterfaceOneBuild;
        summaryIconAdapter = new SummaryIconAdapter();
    }

    @NonNull
    @Override
    public SummaryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tp_summary_single_item, parent, false);
        return new MyViewHolder(view);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull SummaryAdapter.MyViewHolder holder, int position) {

        if (SharedPref.getOneBuild(context).equalsIgnoreCase("0")) {
            OneBuildModelClass oneBuildModelClasses = arrayListOneBuild.get(holder.getAbsoluteAdapterPosition());
            holder.setIsRecyclable(false);
            if (!oneBuildModelClasses.getDayNo().isEmpty()) {
                OneBuildModelClass.SessionList oneBuildModelClass = new OneBuildModelClass.SessionList();
                for (int i = 0; i < oneBuildModelClasses.getSessionList().size(); i++) {
                    oneBuildModelClass = oneBuildModelClasses.getSessionList().get(i);
                    holder.workTypeModelArrayOneBuild.add(oneBuildModelClass.getWorkType());
                    holder.hqModelArrayOneBuild.add(oneBuildModelClass.getHeadquarters());
                    holder.clusterCount += oneBuildModelClass.getTerritories().size();
                    holder.jwCount += oneBuildModelClass.getJointWorks().size();
                    holder.drCount += oneBuildModelClass.getDoctors().size();
                    holder.chemistCount += oneBuildModelClass.getChemists().size();
                    holder.stockiestCount += oneBuildModelClass.getStockists().size();
                    holder.unListedDrCount += oneBuildModelClass.getUnlistedDoctors().size();
                    holder.cipCount += oneBuildModelClass.getCip().size();
                    holder.hospitalCount += oneBuildModelClass.getHospitals().size();

                }
                holder.date.setText(oneBuildModelClasses.getDate());
                for (OneBuildModelClass.SessionList.WorkType workType : holder.workTypeModelArrayOneBuild) {
                    if (!workType.getName().isEmpty())
                        holder.workTypeNames.add(workType.getName());
                }

                if (holder.workTypeModelArrayOneBuild.size() == 1) {
                    holder.workType.setText(holder.workTypeModelArrayOneBuild.get(0).getName());
                    holder.hqName.setText(holder.hqModelArrayOneBuild.get(0).getName());
                    if (SharedPref.getSfType(context).equalsIgnoreCase("2")) {
                        holder.hqName.setVisibility(View.VISIBLE);
                        holder.view.setVisibility(View.VISIBLE);
                    } else {
                        holder.hqName.setVisibility(View.GONE);
                        holder.view.setVisibility(View.GONE);
                    }
                    holder.workTypeLayout2.setVisibility(View.GONE);
                    holder.workTypeLayout3.setVisibility(View.GONE);
                } else if (holder.workTypeModelArrayOneBuild.size() == 2) {
                    holder.workTypeLayout3.setVisibility(View.GONE);
                    holder.workType.setText(holder.workTypeModelArrayOneBuild.get(0).getName());
                    holder.hqName.setText(holder.hqModelArrayOneBuild.get(0).getName());
                    holder.workType2.setText(holder.workTypeModelArrayOneBuild.get(1).getName());
                    holder.hqName2.setText(holder.hqModelArrayOneBuild.get(1).getName());
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

                } else if (holder.workTypeModelArrayOneBuild.size() == 3) {
                    holder.workType.setText(holder.workTypeModelArrayOneBuild.get(0).getName());
                    holder.hqName.setText(holder.hqModelArrayOneBuild.get(0).getName());
                    holder.workType2.setText(holder.workTypeModelArrayOneBuild.get(1).getName());
                    holder.hqName2.setText(holder.hqModelArrayOneBuild.get(1).getName());
                    holder.workType3.setText(holder.workTypeModelArrayOneBuild.get(2).getName());
                    holder.hqName3.setText(holder.hqModelArrayOneBuild.get(2).getName());


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
                for (String workType : holder.workTypeNames) { // to find the work types among sessions are only holiday/weeklyOff are combinations of any other work types
                    if (workType.equalsIgnoreCase("Holiday") || workType.equalsIgnoreCase("Weekly Off") || workType.equalsIgnoreCase("Not Available")) {
                        onlyHoliday = true;
                    } else {
                        onlyHoliday = false;
                        break;
                    }
                }

                if (!onlyHoliday) {
                    ArrayList<OneBuildModelClass.CountModel> countModel = new ArrayList<>();

                    if (holder.clusterCount > 0) {
                        OneBuildModelClass.CountModel clusterModel = new OneBuildModelClass.CountModel("Cluster", holder.clusterCount);
                        countModel.add(clusterModel);
                    }

                    if (holder.jwCount > 0) {
                        OneBuildModelClass.CountModel jwModel = new OneBuildModelClass.CountModel("JW", holder.jwCount);
                        countModel.add(jwModel);
                    }

                    if (holder.drCount > 0) {
                        OneBuildModelClass.CountModel drModel = new OneBuildModelClass.CountModel("DR", holder.drCount);
                        countModel.add(drModel);
                    }

                    if (holder.chemistCount > 0) {
                        OneBuildModelClass.CountModel chemistModel = new OneBuildModelClass.CountModel("Chemist", holder.chemistCount);
                        countModel.add(chemistModel);
                    }

                    if (holder.stockiestCount > 0) {
                        OneBuildModelClass.CountModel stockModel = new OneBuildModelClass.CountModel("Stockiest", holder.stockiestCount);
                        countModel.add(stockModel);
                    }

                    if (holder.unListedDrCount > 0) {
                        OneBuildModelClass.CountModel unDrModel = new OneBuildModelClass.CountModel("UnlistedDr", holder.unListedDrCount);
                        countModel.add(unDrModel);
                    }

                    if (holder.cipCount > 0) {
                        OneBuildModelClass.CountModel cipModel = new OneBuildModelClass.CountModel("CIP", holder.cipCount);
                        countModel.add(cipModel);
                    }

                    if (holder.hospitalCount > 0) {
                        OneBuildModelClass.CountModel hospModel = new OneBuildModelClass.CountModel("Hosp", holder.hospitalCount);
                        countModel.add(hospModel);
                    }

                    summaryIconAdapter = new SummaryIconAdapter(context, countModel);
                    RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 5);
                    holder.recyclerView.setLayoutManager(layoutManager);
                    holder.recyclerView.setAdapter(summaryIconAdapter);
                }

            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    OneBuildModelClass oneBuildModelClass = new OneBuildModelClass(arrayListOneBuild.get(holder.getAbsoluteAdapterPosition()));

                    summaryInterfaceOneBuild.onClick(oneBuildModelClass, holder.getAbsoluteAdapterPosition());


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
                public void onClick(View view) {
                    OneBuildModelClass oneBuildModelClass = new OneBuildModelClass(arrayListOneBuild.get(holder.getAbsoluteAdapterPosition()));
                    summaryInterfaceOneBuild.onClick(oneBuildModelClass, holder.getAbsoluteAdapterPosition());
                }
            });
        } else {
            ModelClass modelClasses = arrayList.get(holder.getAbsoluteAdapterPosition());
            holder.setIsRecyclable(false);

            if (!modelClasses.getDayNo().isEmpty()) {
                ModelClass.SessionList modelClass = new ModelClass.SessionList();
                for (int i = 0; i < modelClasses.getSessionList().size(); i++) {
                    modelClass = modelClasses.getSessionList().get(i);
                    holder.workTypeModelArray.add(modelClass.getWorkType());
                    if (SharedPref.getSfType(context).equals("1")) {
                        holder.hqModelArray.add(modelClass.getHQ());
                        holder.clusterCount += modelClass.getCluster().size();
                        holder.jwCount += modelClass.getJC().size();
                        holder.drCount += modelClass.getListedDr().size();
                        holder.chemistCount += modelClass.getChemist().size();
                        holder.stockiestCount += modelClass.getStockiest().size();
                        holder.unListedDrCount += modelClass.getUnListedDr().size();
                        holder.cipCount += modelClass.getCip().size();
                        holder.hospitalCount += modelClass.getHospital().size();
                    } else {
                        holder.hqModelArray.add(modelClass.getHQ());
                        holder.hqsModelArray.add(modelClass.getHQs());
                        holder.clusterCount += modelClass.getClusters().stream()
                                .mapToInt(p -> p.getItemsList().size())
                                .sum();
                        holder.jwCount += modelClass.getJCs().stream()
                                .mapToInt(p -> p.getItemsList().size())
                                .sum();
                        holder.drCount += modelClass.getListedDrs().stream()
                                .mapToInt(p -> p.getItemsList().size())
                                .sum();
                        holder.chemistCount += modelClass.getChemists().stream()
                                .mapToInt(p -> p.getItemsList().size())
                                .sum();
                        holder.stockiestCount += modelClass.getStockiests().stream()
                                .mapToInt(p -> p.getItemsList().size())
                                .sum();
                        holder.unListedDrCount += modelClass.getUnListedDrs().stream()
                                .mapToInt(p -> p.getItemsList().size())
                                .sum();
                        holder.cipCount += modelClass.getCips().stream()
                                .mapToInt(p -> p.getItemsList().size())
                                .sum();
                        holder.hospitalCount += modelClass.getHospitals().stream()
                                .mapToInt(p -> p.getItemsList().size())
                                .sum();
                    }
                }

                holder.date.setText(modelClasses.getDate());
                for (ModelClass.SessionList.WorkType workType : holder.workTypeModelArray) {
                    if (!workType.getName().isEmpty())
                        holder.workTypeNames.add(workType.getName());
                }

                if (holder.workTypeModelArray.size() == 1) {
                    holder.workType.setText(holder.workTypeModelArray.get(0).getName());
                    holder.hqName.setText(holder.hqModelArray.get(0).getName());
                    if (SharedPref.getSfType(context).equalsIgnoreCase("2")) {
                        StringBuilder hqNames = new StringBuilder();
                        for (ModelClass.SessionList.SubClass hq : holder.hqsModelArray.get(0)) {
                            hqNames.append(hq.getName());
                            hqNames.append(",");
                        }
                        holder.hqName.setText(hqNames);
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
                        StringBuilder hqNames = new StringBuilder();
                        for (ModelClass.SessionList.SubClass hq : holder.hqsModelArray.get(0)) {
                            hqNames.append(hq.getName());
                            hqNames.append(",");
                        }
                        holder.hqName.setText(hqNames);
                        hqNames = new StringBuilder();
                        for (ModelClass.SessionList.SubClass hq : holder.hqsModelArray.get(1)) {
                            hqNames.append(hq.getName());
                            hqNames.append(",");
                        }
                        holder.hqName2.setText(hqNames);
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
                        StringBuilder hqNames = new StringBuilder();
                        for (ModelClass.SessionList.SubClass hq : holder.hqsModelArray.get(0)) {
                            hqNames.append(hq.getName());
                            hqNames.append(",");
                        }
                        holder.hqName.setText(hqNames);
                        hqNames = new StringBuilder();
                        for (ModelClass.SessionList.SubClass hq : holder.hqsModelArray.get(1)) {
                            hqNames.append(hq.getName());
                            hqNames.append(",");
                        }
                        holder.hqName2.setText(hqNames);
                        hqNames = new StringBuilder();
                        for (ModelClass.SessionList.SubClass hq : holder.hqsModelArray.get(2)) {
                            hqNames.append(hq.getName());
                            hqNames.append(",");
                        }
                        holder.hqName3.setText(hqNames);
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
                for (String workType : holder.workTypeNames) { // to find the work types among sessions are only holiday/weeklyOff are combinations of any other work types
                    if (workType.equalsIgnoreCase("Holiday") || workType.equalsIgnoreCase("Weekly Off") || workType.equalsIgnoreCase("Not Available")) {
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
                        ModelClass.CountModel clusterModel = new ModelClass.CountModel("Cluster", holder.clusterCount);
                        countModel.add(clusterModel);
                    }

                    if (holder.jwCount > 0) {
                        ModelClass.CountModel jwModel = new ModelClass.CountModel("JW", holder.jwCount);
                        countModel.add(jwModel);
                    }

                    if (holder.drCount > 0) {
                        ModelClass.CountModel drModel = new ModelClass.CountModel("DR", holder.drCount);
                        countModel.add(drModel);
                    }

                    if (holder.chemistCount > 0) {
                        ModelClass.CountModel chemistModel = new ModelClass.CountModel("Chemist", holder.chemistCount);
                        countModel.add(chemistModel);
                    }

                    if (holder.stockiestCount > 0) {
                        ModelClass.CountModel stockModel = new ModelClass.CountModel("Stockiest", holder.stockiestCount);
                        countModel.add(stockModel);
                    }

                    if (holder.unListedDrCount > 0) {
                        ModelClass.CountModel unDrModel = new ModelClass.CountModel("UnlistedDr", holder.unListedDrCount);
                        countModel.add(unDrModel);
                    }

                    if (holder.cipCount > 0) {
                        ModelClass.CountModel cipModel = new ModelClass.CountModel("CIP", holder.cipCount);
                        countModel.add(cipModel);
                    }

                    if (holder.hospitalCount > 0) {
                        ModelClass.CountModel hospModel = new ModelClass.CountModel("Hosp", holder.hospitalCount);
                        countModel.add(hospModel);
                    }

                    summaryIconAdapter = new SummaryIconAdapter(countModel, context);
                    RecyclerView.LayoutManager layoutManager = new GridLayoutManager(context, 5);
                    holder.recyclerView.setLayoutManager(layoutManager);
                    holder.recyclerView.setAdapter(summaryIconAdapter);
                }
//            }
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    ModelClass modelClass = new ModelClass(arrayList.get(holder.getAbsoluteAdapterPosition()));

                    summaryInterface.onClick(modelClass, holder.getAbsoluteAdapterPosition());


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
                public void onClick(View view) {
                    ModelClass modelClass = new ModelClass(arrayList.get(holder.getAbsoluteAdapterPosition()));
                    summaryInterface.onClick(modelClass, holder.getAbsoluteAdapterPosition());
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        if (SharedPref.getOneBuild(context).equalsIgnoreCase("0")) {
            return arrayListOneBuild.size();
        } else {
            return arrayList.size();
        }

    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView date, workType, workType2, workType3, hqName, hqName2, hqName3;
        View view, view2, view3;
        int clusterCount = 0, jwCount = 0, drCount = 0, chemistCount = 0, stockiestCount = 0, unListedDrCount = 0, cipCount = 0, hospitalCount = 0;
        LinearLayout workTypeLayout2, workTypeLayout3;
        RecyclerView recyclerView;
        ArrayList<ModelClass.SessionList.WorkType> workTypeModelArray = new ArrayList<>();
        ArrayList<ModelClass.SessionList.SubClass> hqModelArray = new ArrayList<>();
        ArrayList<OneBuildModelClass.SessionList.WorkType> workTypeModelArrayOneBuild = new ArrayList<>();
        ArrayList<OneBuildModelClass.SessionList.SubClass> hqModelArrayOneBuild = new ArrayList<>();
        ArrayList<List<ModelClass.SessionList.SubClass>> hqsModelArray = new ArrayList<>();

        ArrayList<String> workTypeNames = new ArrayList<>();

        public MyViewHolder(@NonNull View itemView) {
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
