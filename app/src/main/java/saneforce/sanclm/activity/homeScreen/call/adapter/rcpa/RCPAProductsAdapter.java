package saneforce.sanclm.activity.homeScreen.call.adapter.rcpa;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;



import java.util.ArrayList;

import saneforce.sanclm.CommonClasses.CommonUtilsMethods;
import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.pojo.rcpa.RCPAAddedCompList;
import saneforce.sanclm.activity.homeScreen.call.pojo.rcpa.RCPAAddedProdList;

public class RCPAProductsAdapter extends RecyclerView.Adapter<RCPAProductsAdapter.ViewHolder> {
    public static RecyclerView rv_added_comp_list;
    Context context;
    ArrayList<RCPAAddedCompList> rcpa_add_list;
    ArrayList<RCPAAddedCompList> rcpa_add_list_new = new ArrayList<>();
    ArrayList<RCPAAddedProdList> rcpaFilterProdArrayListArrayList;
    RCPACompListAdapter rcpaCompListAdapter;
    CommonUtilsMethods commonUtilsMethods;

    public RCPAProductsAdapter(Context context, ArrayList<RCPAAddedProdList> rcpaFilterProdArrayListArrayList, ArrayList<RCPAAddedCompList> rcpa_add_list) {
        this.context = context;
        this.rcpaFilterProdArrayListArrayList = rcpaFilterProdArrayListArrayList;
        this.rcpa_add_list = rcpa_add_list;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_rcpa_prd, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        commonUtilsMethods = new CommonUtilsMethods(context);
        // Log.v("rcpa","---che--" + rcpaAddedProdListArrayList.get(position).getChem_names() + "--prd---" + rcpaAddedProdListArrayList.get(position).getPrd_name());
        holder.prd_name.setText(rcpaFilterProdArrayListArrayList.get(position).getPrd_name());
        holder.tv_qty.setText(rcpaFilterProdArrayListArrayList.get(position).getQty());
        holder.tv_rate.setText(rcpaFilterProdArrayListArrayList.get(position).getRate());
        holder.tv_value.setText(rcpaFilterProdArrayListArrayList.get(position).getValue());

        rv_added_comp_list = holder.rv_added_comp_list;

        for (int i = 0; i < rcpa_add_list.size(); i++) {
            if (rcpa_add_list.get(i).getChem_names().equalsIgnoreCase(rcpaFilterProdArrayListArrayList.get(position).getChem_names())
                    && rcpa_add_list.get(i).getPrd_name().equalsIgnoreCase(rcpaFilterProdArrayListArrayList.get(position).getPrd_name())) {
                rcpa_add_list_new.add(new RCPAAddedCompList(rcpa_add_list.get(i).getChem_names(), rcpa_add_list.get(i).getPrd_name(), rcpa_add_list.get(i).getComp_name(), rcpa_add_list.get(i).getComp_brand(), rcpa_add_list.get(i).getQty(),
                        rcpa_add_list.get(i).getRate(), rcpa_add_list.get(i).getValue(), rcpa_add_list.get(i).getRemarks()));
                rcpaCompListAdapter = new RCPACompListAdapter(context, rcpa_add_list_new);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                rv_added_comp_list.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
                rv_added_comp_list.setLayoutManager(mLayoutManager);
                rv_added_comp_list.setAdapter(rcpaCompListAdapter);
            }
        }

      /*  rcpaCompListAdapter = new RCPACompListAdapter(context, rcpa_add_list);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
        rv_added_comp_list.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        rv_added_comp_list.setLayoutManager(mLayoutManager);
        rv_added_comp_list.setAdapter(rcpaCompListAdapter);*/

        holder.tv_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                removeAt(holder.getAdapterPosition());
            }
        });
        holder.img_view.setOnClickListener(view -> {
            if (holder.card_list_view.getVisibility() == View.VISIBLE) {
                holder.card_list_view.setVisibility(View.GONE);
                holder.img_view.setImageDrawable(context.getResources().getDrawable(R.drawable.img_plus));
            } else {
                holder.card_list_view.setVisibility(View.VISIBLE);
                holder.img_view.setImageDrawable(context.getResources().getDrawable(R.drawable.img_minus));
            }
        });
    }

    public void removeAt(int position) {
        rcpaFilterProdArrayListArrayList.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, rcpaFilterProdArrayListArrayList.size());
    }

    @Override
    public int getItemCount() {
        return rcpaFilterProdArrayListArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView prd_name, tv_qty, tv_rate, tv_value, tv_edit, tv_delete;
        RecyclerView rv_added_comp_list;
        ImageView img_view;
        CardView card_list_view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            prd_name = itemView.findViewById(R.id.tv_prd_name);
            tv_qty = itemView.findViewById(R.id.tv_qty);
            tv_rate = itemView.findViewById(R.id.tv_rate);
            tv_value = itemView.findViewById(R.id.tv_value);
            tv_edit = itemView.findViewById(R.id.tv_edit);
            tv_delete = itemView.findViewById(R.id.tv_delete);
            rv_added_comp_list = itemView.findViewById(R.id.rv_comp_added_list);
            img_view = itemView.findViewById(R.id.img_view);
            card_list_view = itemView.findViewById(R.id.card_list);
        }
    }
}
