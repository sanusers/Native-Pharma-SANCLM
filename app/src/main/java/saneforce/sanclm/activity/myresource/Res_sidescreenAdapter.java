package saneforce.sanclm.activity.myresource;

import android.content.Context;
import android.content.Intent;
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

public class Res_sidescreenAdapter extends RecyclerView.Adapter<Res_sidescreenAdapter.ViewHolder> {

    public static View list_resource;
    ArrayList<Resourcemodel_class> resList;
    Context context;
    String split_val;
    String Key = "";

    public Res_sidescreenAdapter(Context context, ArrayList<Resourcemodel_class> resList, String split_val) {//
        this.context = context;
        this.resList = resList;
        this.split_val = split_val;
    }

    @NonNull
    @Override
    public Res_sidescreenAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.resource_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Res_sidescreenAdapter.ViewHolder holder, int position) {
        String count = String.valueOf((position + 1));
        final Resourcemodel_class app_adapt = resList.get(position);

        if (!app_adapt.getDcr_name().equals("") && !app_adapt.getDcr_name().equals("null")) {
            holder.Res_Name.setText(app_adapt.getDcr_name());
        }
        if (!app_adapt.getRes_custname().equals("") && !app_adapt.getRes_custname().equals("null")) {
            holder.Res_culter.setText(app_adapt.getRes_custname());
        } else {
            holder.Res_culter.setVisibility(View.GONE);
        }


        if (!app_adapt.getRes_custname().equals("") && !app_adapt.getRes_custname().equals("null")) {
            holder.Res_culter.setText(app_adapt.getRes_custname());
        } else {
            holder.Res_culter.setVisibility(View.GONE);
        }

        if (!app_adapt.getRes_Category().equals("") && !app_adapt.getRes_Category().equals("null")) {
            holder.Res_category.setText(app_adapt.getRes_Category());
        } else {
            holder.Res_category.setVisibility(View.GONE);
        }


        if (!app_adapt.getRes_Specialty().equals("") && !app_adapt.getRes_Specialty().equals("null")) {

            holder.Res_specialty.setText(app_adapt.getRes_Specialty());
        } else {
            holder.Res_specialty.setVisibility(View.GONE);
        }
        if (!app_adapt.getRes_rx().equals("") && !app_adapt.getRes_rx().equals("null")) {
            holder.Res_rx.setText("-");
        } else {
            holder.Res_rx.setText("");
        }

        if (app_adapt.getRes_Category().equals("") && app_adapt.getRes_rx().equals("") && app_adapt.getRes_Specialty().equals("")) {
            holder.Res_Table1.setVisibility(View.GONE);

            if (split_val.equals("1")) {
                holder.Res_Edit.setVisibility(View.GONE);
                holder.Res_View.setVisibility(View.GONE);

                holder.Res_Table2.setVisibility(View.GONE);
            }

        }
        if (split_val.equals("1")) {
            holder.Res_Edit.setVisibility(View.GONE);
            holder.Res_View.setVisibility(View.GONE);

            holder.Res_Table2.setVisibility(View.GONE);
        }


        holder.listcount.setText(count + " )");

        holder.Res_View.setOnClickListener(v -> {
            Log.d("latlong",app_adapt.Latitude+"--"+app_adapt.Latitude+"--"+app_adapt.getRes_id()+"--"+app_adapt.getCustoum_name());
            if(app_adapt.Latitude.equals("") || app_adapt.Latitude.equals("null")){

            }else{
                Intent click = new Intent(context, MyResource_mapview.class);
                click.putExtra("type",app_adapt.getRes_id());
                click.putExtra("cust_name",app_adapt.getCustoum_name());
                context.startActivity(click);
            }



        });


//        doSearch();
    }


    @Override
    public int getItemCount() {
        return resList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView Res_Name, Res_category, Res_specialty, Res_rx, Res_culter, listcount;
        LinearLayout Res_Edit, Res_View, Res_Table1, Res_Table2, Click_Res;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            Click_Res = itemView.findViewById(R.id.Click_Res);
            Res_Name = itemView.findViewById(R.id.Res_Name);
            Res_category = itemView.findViewById(R.id.Res_category);
            Res_specialty = itemView.findViewById(R.id.Res_specialty);
            Res_rx = itemView.findViewById(R.id.Res_rx);
            Res_culter = itemView.findViewById(R.id.Res_culter);
            listcount = itemView.findViewById(R.id.listcount);

            Res_Edit = itemView.findViewById(R.id.Res_Edit);
            Res_View = itemView.findViewById(R.id.Res_View);
            Res_Table1 = itemView.findViewById(R.id.Res_Table1);
            Res_Table2 = itemView.findViewById(R.id.Res_Table2);
        }
    }




}
