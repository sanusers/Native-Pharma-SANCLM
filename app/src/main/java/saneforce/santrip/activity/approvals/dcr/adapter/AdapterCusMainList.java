package saneforce.santrip.activity.approvals.dcr.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.santrip.R;
import saneforce.santrip.activity.approvals.dcr.detailView.DcrDetailViewActivity;
import saneforce.santrip.activity.approvals.dcr.detailView.adapter.AdapterCusSingleList;
import saneforce.santrip.activity.approvals.dcr.pojo.DcrDetailModelList;
import saneforce.santrip.activity.call.pojo.input.SaveCallInputList;
import saneforce.santrip.activity.call.pojo.product.SaveCallProductList;

public class AdapterCusMainList extends RecyclerView.Adapter<AdapterCusMainList.ViewHolder> {
    Context context;
    ArrayList<DcrDetailModelList> dcrApprovalLists;
    ArrayList<SaveCallProductList> ProductList;
    ArrayList<SaveCallInputList> InputList;

    public AdapterCusMainList(Context context, ArrayList<DcrDetailModelList> dcrApprovalLists, ArrayList<SaveCallProductList> productList, ArrayList<SaveCallInputList> inputList) {
        this.context = context;
        this.dcrApprovalLists = dcrApprovalLists;
        this.ProductList = productList;
        this.InputList = inputList;
    }


    @NonNull
    @Override
    public AdapterCusMainList.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_dcr_detailed_list, parent, false);
        return new AdapterCusMainList.ViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull AdapterCusMainList.ViewHolder holder, int position) {
        holder.tv_name.setText(dcrApprovalLists.get(position).getName());
        holder.tv_cluster_top.setText(dcrApprovalLists.get(position).getSdp_name());

        switch (dcrApprovalLists.get(position).getType()) {
            case "1":
                holder.img_cus.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.map_dr_img));
                break;
            case "2":
                holder.img_cus.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.map_chemist_img));
                break;
            case "3":
                holder.img_cus.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.map_stockist_img));
                break;
            case "4":
                holder.img_cus.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.map_unlistdr_img));
                break;
        }

        holder.card_view.setOnClickListener(view -> {
            DcrDetailViewActivity.dcrDetailModelLists = new ArrayList<>();
            for (int i = 0; i < dcrApprovalLists.size(); i++) {
                DcrDetailViewActivity.dcrDetailModelLists.add(new DcrDetailModelList(dcrApprovalLists.get(i).getHq_name(), dcrApprovalLists.get(i).getName(), dcrApprovalLists.get(i).getCode(), dcrApprovalLists.get(i).getTypeCust(), dcrApprovalLists.get(i).getType(), dcrApprovalLists.get(i).getSdp_name(), dcrApprovalLists.get(i).getPob(), dcrApprovalLists.get(i).getRemark(), dcrApprovalLists.get(i).getJointWork(), dcrApprovalLists.get(i).getCall_feedback(), dcrApprovalLists.get(i).getModTime(), dcrApprovalLists.get(i).getVisitTime(),dcrApprovalLists.get(position).getDct_id(),dcrApprovalLists.get(position).getDcr_detial_id()));
            }
            DcrDetailViewActivity.SelectedCode = dcrApprovalLists.get(position).getCode();
            Intent intent = new Intent(context, DcrDetailViewActivity.class);
            intent.putExtra("hq_name", dcrApprovalLists.get(position).getHq_name());
            intent.putExtra("cus_cluster", dcrApprovalLists.get(position).getSdp_name());
            intent.putExtra("cus_pob", dcrApprovalLists.get(position).getPob());
            intent.putExtra("cus_jw", dcrApprovalLists.get(position).getJointWork());
            intent.putExtra("cus_type", dcrApprovalLists.get(position).getType());
            intent.putExtra("cus_fb", dcrApprovalLists.get(position).getCall_feedback());
            intent.putExtra("cus_remark", dcrApprovalLists.get(position).getRemark());
            intent.putExtra("cus_mod_time", dcrApprovalLists.get(position).getModTime());
            intent.putExtra("cus_visit_time", dcrApprovalLists.get(position).getVisitTime());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            AdapterCusSingleList.ProductList = new ArrayList<>();
            for (int i = 0; i < ProductList.size(); i++) {
                AdapterCusSingleList.ProductList.add(new SaveCallProductList(ProductList.get(i).getCode(), ProductList.get(i).getName(), ProductList.get(i).getSample_qty(), ProductList.get(i).getRx_qty(), ProductList.get(i).getRcpa_qty(), ProductList.get(i).getPromoted()));
            }

            AdapterCusSingleList.InputList = new ArrayList<>();
            for (int j = 0; j < InputList.size(); j++) {
                AdapterCusSingleList.InputList.add(new SaveCallInputList(InputList.get(j).getInp_code(), InputList.get(j).getInput_name(), InputList.get(j).getInp_qty()));
            }
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return dcrApprovalLists.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<DcrDetailModelList> filteredNames) {
        this.dcrApprovalLists = filteredNames;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_arrow, img_cus;
        CardView card_view;
        TextView tv_name, tv_cluster_top;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_arrow = itemView.findViewById(R.id.listArrow);
            img_cus = itemView.findViewById(R.id.img_cust);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_cluster_top = itemView.findViewById(R.id.tv_cluster_top);
            card_view = itemView.findViewById(R.id.card_view_top);
        }
    }
}
