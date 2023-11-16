package saneforce.sanclm.activity.approvals.dcr.adapter;

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
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.approvals.dcr.dcrdetaillist.DcrDetailViewActivity;
import saneforce.sanclm.activity.approvals.dcr.dcrdetaillist.adapter.AdapterCustSingleList;
import saneforce.sanclm.activity.approvals.dcr.dcrdetaillist.adapter.InputAdapter;
import saneforce.sanclm.activity.approvals.dcr.dcrdetaillist.adapter.ProductAdapter;
import saneforce.sanclm.activity.approvals.dcr.pojo.DcrDetailModelList;
import saneforce.sanclm.activity.homeScreen.call.pojo.input.SaveCallInputList;
import saneforce.sanclm.activity.homeScreen.call.pojo.product.SaveCallProductList;

public class AdapterCustMainList extends RecyclerView.Adapter<AdapterCustMainList.ViewHolder> {
    Context context;
    ArrayList<DcrDetailModelList> dcrApprovalLists;
    ArrayList<SaveCallProductList> ProductList;
    ArrayList<SaveCallProductList> ProductListNew;
    ArrayList<SaveCallInputList> InputList;
    ArrayList<SaveCallInputList> InputListNew;
    ProductAdapter productAdapter;
    InputAdapter inputAdapter;
    int lastSelectedPosition = -1;

    public AdapterCustMainList(Context context, ArrayList<DcrDetailModelList> dcrApprovalLists, ArrayList<SaveCallProductList> productList, ArrayList<SaveCallInputList> inputList) {
        this.context = context;
        this.dcrApprovalLists = dcrApprovalLists;
        this.ProductList = productList;
        this.InputList = inputList;
    }

    public static void HideDetailedData(Context context, ImageView img_arrow, ConstraintLayout constraint_details, View view_top, ConstraintLayout constraint_input, ConstraintLayout constraint_sample) {
        img_arrow.setImageDrawable(context.getResources().getDrawable(R.drawable.greater_than_purple));
        constraint_details.setVisibility(View.GONE);
        view_top.setVisibility(View.INVISIBLE);
        constraint_input.setVisibility(View.GONE);
        constraint_sample.setVisibility(View.GONE);
    }

    @NonNull
    @Override
    public AdapterCustMainList.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_dcr_detailed_list, parent, false);
        return new AdapterCustMainList.ViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull AdapterCustMainList.ViewHolder holder, int position) {

     /*   if (selectedPosition == holder.getBindingAdapterPosition()) {
            ProductListNew = new ArrayList<>();
            InputListNew = new ArrayList<>();
            holder.img_arrow.setImageDrawable(context.getResources().getDrawable(R.drawable.up_arrow));
            holder.constraint_details.setVisibility(View.VISIBLE);
            holder.view_top.setVisibility(View.VISIBLE);

            //ProductAdapter
            for (int i = 0; i < ProductList.size(); i++) {
                if (ProductList.get(i).getCode().equalsIgnoreCase(dcrApprovalLists.get(position).getName())) {
                    ProductListNew.add(new SaveCallProductList(ProductList.get(i).getCode(), ProductList.get(i).getName(), ProductList.get(i).getSample_qty(), ProductList.get(i).getRx_qty(), ProductList.get(i).getRcpa_qty(), ProductList.get(i).getPromoted()));
                }
            }


            if (ProductListNew.size() > 0) {
                holder.constraint_sample.setVisibility(View.VISIBLE);
                productAdapter = new ProductAdapter(context, ProductListNew);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                holder.rv_prd_list.setLayoutManager(mLayoutManager);
                holder.rv_prd_list.setItemAnimator(new DefaultItemAnimator());
                holder.rv_prd_list.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
                holder.rv_prd_list.setAdapter(productAdapter);
            } else {
                holder.constraint_sample.setVisibility(View.GONE);
            }

            //InputAdapter
            for (int j = 0; j < InputList.size(); j++) {
                if (InputList.get(j).getInp_code().equalsIgnoreCase(dcrApprovalLists.get(position).getName())) {
                    InputListNew.add(new SaveCallInputList(InputList.get(j).getInp_code(), InputList.get(j).getInput_name(), InputList.get(j).getInp_qty()));
                }
            }

            if (InputListNew.size() > 0) {
                holder.constraint_input.setVisibility(View.VISIBLE);
                inputAdapter = new InputAdapter(context, InputListNew);
                RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(context);
                holder.rv_inp_list.setLayoutManager(mLayoutManager1);
                holder.rv_inp_list.setItemAnimator(new DefaultItemAnimator());
                holder.rv_inp_list.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
                holder.rv_inp_list.setAdapter(inputAdapter);
            } else {
                holder.constraint_input.setVisibility(View.GONE);
            }
        } else {
            HideDetailedData(context, holder.img_arrow, holder.constraint_details, holder.view_top, holder.constraint_input, holder.constraint_sample);
        }

        holder.tv_name.setText(dcrApprovalLists.get(position).getName());
        holder.tv_cluster_top.setText(dcrApprovalLists.get(position).getSdp_name());
        holder.tv_jointwork.setText(dcrApprovalLists.get(position).getJointWork());
        if (!dcrApprovalLists.get(position).getRemark().isEmpty()) {
            holder.tv_remark.setText(dcrApprovalLists.get(position).getRemark());
        }

        if (!dcrApprovalLists.get(position).getCall_feedback().isEmpty()) {
            holder.tv_feedback.setText(dcrApprovalLists.get(position).getCall_feedback());
        }
*/

        holder.tv_name.setText(dcrApprovalLists.get(position).getName());
        holder.tv_cluster_top.setText(dcrApprovalLists.get(position).getSdp_name());

        switch (dcrApprovalLists.get(position).getType()) {
            case "1":
                holder.img_cust.setImageDrawable(context.getResources().getDrawable(R.drawable.map_dr_img));
                break;
            case "2":
                holder.img_cust.setImageDrawable(context.getResources().getDrawable(R.drawable.map_chemist_img));
                break;
            case "3":
                holder.img_cust.setImageDrawable(context.getResources().getDrawable(R.drawable.map_stockist_img));
                break;
            case "4":
                holder.img_cust.setImageDrawable(context.getResources().getDrawable(R.drawable.map_unlistdr_img));
                break;
        }

        holder.card_view.setOnClickListener(view -> {
            DcrDetailViewActivity.dcrDetailModelLists = new ArrayList<>();
            for (int i = 0; i < dcrApprovalLists.size(); i++) {
                DcrDetailViewActivity.dcrDetailModelLists.add(new DcrDetailModelList(dcrApprovalLists.get(i).getHq_name(), dcrApprovalLists.get(i).getName(), dcrApprovalLists.get(i).getCode(), dcrApprovalLists.get(i).getTypeCust(), dcrApprovalLists.get(i).getType(), dcrApprovalLists.get(i).getSdp_name(), dcrApprovalLists.get(i).getPob(), dcrApprovalLists.get(i).getRemark(), dcrApprovalLists.get(i).getJointWork(), dcrApprovalLists.get(i).getCall_feedback(), dcrApprovalLists.get(i).getModTime(), dcrApprovalLists.get(i).getVisitTime()));
            }
            DcrDetailViewActivity.SelectedCode = dcrApprovalLists.get(position).getCode();
            Intent intent = new Intent(context, DcrDetailViewActivity.class);
            intent.putExtra("hq_name", dcrApprovalLists.get(position).getHq_name());
            intent.putExtra("cust_cluster", dcrApprovalLists.get(position).getSdp_name());
            intent.putExtra("cust_pob", dcrApprovalLists.get(position).getPob());
            intent.putExtra("cust_jw", dcrApprovalLists.get(position).getJointWork());
            intent.putExtra("cust_type", dcrApprovalLists.get(position).getType());
            intent.putExtra("cust_fb", dcrApprovalLists.get(position).getCall_feedback());
            intent.putExtra("cust_remark", dcrApprovalLists.get(position).getRemark());
            intent.putExtra("cust_mod_time", dcrApprovalLists.get(position).getModTime());
            intent.putExtra("cust_visit_time", dcrApprovalLists.get(position).getVisitTime());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            AdapterCustSingleList.ProductList = new ArrayList<>();
            for (int i = 0; i < ProductList.size(); i++) {
                AdapterCustSingleList.ProductList.add(new SaveCallProductList(ProductList.get(i).getCode(), ProductList.get(i).getName(), ProductList.get(i).getSample_qty(), ProductList.get(i).getRx_qty(), ProductList.get(i).getRcpa_qty(), ProductList.get(i).getPromoted()));
            }

            AdapterCustSingleList.InputList = new ArrayList<>();
            for (int j = 0; j < InputList.size(); j++) {
                AdapterCustSingleList.InputList.add(new SaveCallInputList(InputList.get(j).getInp_code(), InputList.get(j).getInput_name(), InputList.get(j).getInp_qty()));
            }
            context.startActivity(intent);

          /*  if (holder.img_arrow.getDrawable().getConstantState().equals(context.getResources().getDrawable(R.drawable.greater_than_purple).getConstantState())) {
                lastSelectedPosition = selectedPosition;
                selectedPosition = holder.getBindingAdapterPosition();
                //DcrCallApprovalActivity.dcrCallApprovalBinding.constraintSelectedDetails.setVisibility(View.GONE);
                notifyItemChanged(lastSelectedPosition);
                notifyItemChanged(selectedPosition);
            } else {
                selectedPosition = -1;
                notifyItemChanged(selectedPosition);
                HideDetailedData(context, holder.img_arrow, holder.constraint_details, holder.view_top, holder.constraint_input, holder.constraint_sample);
            }*/
        });
    }

    @Override
    public int getItemCount() {
        return dcrApprovalLists.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<DcrDetailModelList> filterdNames) {
        this.dcrApprovalLists = filterdNames;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView img_arrow, img_cust;
        //  ConstraintLayout constraint_details, constraint_input, constraint_sample;
        CardView card_view;
        TextView tv_name, tv_cluster_top;
        // View view_top;
        //  TextView tv_name, tv_cluster_top, tv_jointwork, tv_remark, tv_feedback;
        //  RecyclerView rv_prd_list, rv_inp_list;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img_arrow = itemView.findViewById(R.id.listArrow);
            img_cust = itemView.findViewById(R.id.img_cust);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_cluster_top = itemView.findViewById(R.id.tv_cluster_top);
          /*  constraint_details = itemView.findViewById(R.id.constraint_detailed);
            constraint_input = itemView.findViewById(R.id.constraint_main_input);
            constraint_sample = itemView.findViewById(R.id.constraint_main_sample);
            view_top = itemView.findViewById(R.id.view_dummy);
            tv_jointwork = itemView.findViewById(R.id.tv_jw);
            tv_remark = itemView.findViewById(R.id.tv_overall_remarks);
            tv_feedback = itemView.findViewById(R.id.tv_overall_feedback);
            rv_prd_list = itemView.findViewById(R.id.rv_sample_prd);
            rv_inp_list = itemView.findViewById(R.id.rv_input);*/
            card_view = itemView.findViewById(R.id.card_view_top);
        }
    }
}
