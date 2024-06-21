package saneforce.sanzen.activity.myresource.callstatusview;

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
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.approvals.geotagging.GeoTaggingModelList;

public class call_statusadapter extends RecyclerView.Adapter<call_statusadapter.ViewHolder> {
    Context context;
    ArrayList<callstatus_model> listeduser;


    public call_statusadapter(Context context, ArrayList<callstatus_model> listeduser) {
        this.context = context;
        this.listeduser = listeduser;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.callstat_adpter, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final callstatus_model app_adapt = listeduser.get(position);
        holder.Date_view.setText(app_adapt.getDcr_dt());
        holder.cust_type.setText(app_adapt.getDcrname());
        holder.calls_town.setText(app_adapt.getTown_name());

        if (app_adapt.getChkflk().equals("1")) {
            holder.cs_line.setVisibility(View.VISIBLE);
        } else {
            holder.cs_line.setVisibility(View.GONE);
        }
        if (app_adapt.getDcrname().equals("")) {
            holder.CustName.setText(app_adapt.getWorkType());
            holder.cust_type.setVisibility(View.GONE);
            holder.calls_town.setVisibility(View.GONE);
        } else {
           /* List<String> valuesList = new ArrayList<>();
            for (int bean = 0;bean<listeduser.size();bean++){
                String custName = listeduser.get(bean).getCustName();
                System.out.println("custName--->"+custName);
                valuesList.add(custName);
            }
            Collections.sort(valuesList);*/
           /* Collections.sort(listeduser, Comparator.comparing(callstatus_model::getDcr_dt));
            for (callstatus_model model : listeduser) {
                System.out.println("dcrDate: " + model.getTime());
            }*/
            holder.CustName.setText(app_adapt.getCustName());
            holder.cust_type.setText(app_adapt.getDcrname());
            holder.calls_town.setVisibility(View.VISIBLE);
        }

        /*
        if(app_adapt.getCustType().equals("0")){

        }else if(app_adapt.getCustType().equals("1")){
            holder.cust_type.setText("Doctor");
            holder.calls_town.setVisibility(View.VISIBLE);
        }else if(app_adapt.getCustType().equals("2")){
            holder.cust_type.setText("Chemist");
            holder.calls_town.setVisibility(View.VISIBLE);
        }else if(app_adapt.getCustType().equals("3")){
            holder.cust_type.setText("Stockist");
            holder.calls_town.setVisibility(View.VISIBLE);
        }else if(app_adapt.getCustType().equals("4")){
            holder.cust_type.setText("Unlisted Doctor");
            holder.calls_town.setVisibility(View.VISIBLE);
        }
*/
        Log.d("logcheck", app_adapt.getDcrname());


    }

    @Override
    public int getItemCount() {
        return listeduser.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView Date_view, cust_type, CustName, calls_town;
        LinearLayout cs_line;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Date_view = (itemView).findViewById(R.id.Date_view);
            cust_type = (itemView).findViewById(R.id.cust_type);
            CustName = (itemView).findViewById(R.id.CustName);
            calls_town = (itemView).findViewById(R.id.calls_town);
            cs_line = (itemView).findViewById(R.id.cs_line);


        }
    }
}
