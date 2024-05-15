package saneforce.santrip.activity.reports.dayReport.adapter;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.santrip.R;
import saneforce.santrip.activity.reports.dayReport.model.DayReportRcpaModelClass;
import saneforce.santrip.commonClasses.CommonUtilsMethods;

public class ReoportRcpaAdapter extends RecyclerView.Adapter<ReoportRcpaAdapter.ViewHolder> {

  ArrayList<DayReportRcpaModelClass>  rcpaList=new ArrayList<>();

  Context context;
    CommonUtilsMethods commonUtilsMethods;


    public ReoportRcpaAdapter(ArrayList<DayReportRcpaModelClass> rcpaList, Context context) {
        this.rcpaList = rcpaList;
        this.context = context;
    }

    @NonNull
    @Override
    public ReoportRcpaAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.report_rcpa_child_table_item, parent, false);
        return new ReoportRcpaAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReoportRcpaAdapter.ViewHolder holder, int position) {
        commonUtilsMethods = new CommonUtilsMethods(context);
        holder.prdName.setText(rcpaList.get(position).getOPName().length()>16?rcpaList.get(position).getOPName().substring(0,16)+"...":rcpaList.get(position).getOPName());
        holder.prdqty.setText(rcpaList.get(position).getOPQty());
        holder.CompetitorName.setText(rcpaList.get(position).getChmName().length()>16?rcpaList.get(position).getChmName().substring(0,16)+"...":rcpaList.get(position).getChmName());
        holder.CompetitorProductName.setText(rcpaList.get(position).getCompPName().length()>16?rcpaList.get(position).getCompPName().substring(0,16)+"...":rcpaList.get(position).getCompPName());
        holder.ComprdQty.setText(rcpaList.get(position).getCPQty());
        holder.comName.setText(rcpaList.get(position).getCompName().length()>16?rcpaList.get(position).getCompName().substring(0,16)+"...":rcpaList.get(position).getCompName());
        holder.prdName.setOnClickListener(v -> {popUp(v,rcpaList.get(position).getOPName());});
        holder.CompetitorName.setOnClickListener(v -> {popUp(v,rcpaList.get(position).getChmName());});
        holder.CompetitorProductName.setOnClickListener(v -> {popUp(v,rcpaList.get(position).getCompPName());});
        holder.comName.setOnClickListener(v -> {popUp(v,rcpaList.get(position).getCompName());});

    }

    @Override
    public int getItemCount() {
        return rcpaList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        TextView prdName,prdqty,CompetitorName,CompetitorProductName,ComprdQty,comName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            prdName=itemView.findViewById(R.id.productname);
            prdqty=itemView.findViewById(R.id.productqty);
            CompetitorName=itemView.findViewById(R.id.competitorname);
            CompetitorProductName=itemView.findViewById(R.id.competitorproductname);
            ComprdQty=itemView.findViewById(R.id.competitorproductnameqty);
            comName = itemView.findViewById(R.id.chemistName);
        }
    }
 private void popUp(View v,String name){
     PopupWindow popup = new PopupWindow(context);
     View layout = LayoutInflater.from(context).inflate(R.layout.popup_text, null);
     popup.setContentView(layout);
     popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
     TextView tv_name = layout.findViewById(R.id.tv_name);
     tv_name.setText(name);
     popup.setOutsideTouchable(true);
     popup.showAsDropDown(v);
 }
}
