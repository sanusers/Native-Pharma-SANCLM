package saneforce.santrip.activity.reports.dayReport.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.santrip.R;
import saneforce.santrip.activity.reports.dayReport.model.DayReportRcpaModelClass;

public class ReoportRcpaAdapter extends RecyclerView.Adapter<ReoportRcpaAdapter.ViewHolder> {

  ArrayList<DayReportRcpaModelClass>  rcpaList=new ArrayList<>();

  Context context;

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
        holder.prdName.setText(rcpaList.get(position).getOPName());
        holder.prdqty.setText(rcpaList.get(position).getOPQty());
        holder.CompetitorName.setText(rcpaList.get(position).getChmName());
        holder.CompetitorProductName.setText(rcpaList.get(position).getCompPName());
        holder.ComprdQty.setText(rcpaList.get(position).getCPQty());

    }

    @Override
    public int getItemCount() {
        return rcpaList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {


        TextView prdName,prdqty,CompetitorName,CompetitorProductName,ComprdQty;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            prdName=itemView.findViewById(R.id.productname);
            prdqty=itemView.findViewById(R.id.productqty);
            CompetitorName=itemView.findViewById(R.id.competitorname);
            CompetitorProductName=itemView.findViewById(R.id.competitorproductname);
            ComprdQty=itemView.findViewById(R.id.competitorproductnameqty);


        }
    }
}
