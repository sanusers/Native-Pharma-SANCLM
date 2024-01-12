package saneforce.santrip.activity.approvals.dcr.call.adapter;

import static saneforce.santrip.activity.approvals.dcr.call.DcrDetailViewActivity.dcrDetailViewBinding;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.santrip.R;
import saneforce.santrip.activity.approvals.OnItemClickListenerApproval;
import saneforce.santrip.activity.approvals.dcr.call.DcrDetailViewActivity;
import saneforce.santrip.activity.approvals.dcr.pojo.DcrDetailModelList;
import saneforce.santrip.activity.homeScreen.call.pojo.input.SaveCallInputList;
import saneforce.santrip.activity.homeScreen.call.pojo.product.SaveCallProductList;
import saneforce.santrip.commonClasses.CommonUtilsMethods;

public class AdapterCusSingleList extends RecyclerView.Adapter<AdapterCusSingleList.ViewHolder> {
    public static ArrayList<SaveCallProductList> ProductList;
    public static ArrayList<SaveCallInputList> InputList;
    Context context;
    ArrayList<DcrDetailModelList> dcrApprovalNames;
    ArrayList<SaveCallProductList> ProductListNew;
    ArrayList<SaveCallInputList> InputListNew;
    ProductAdapter productAdapter;
    InputAdapter inputAdapter;
    CommonUtilsMethods commonUtilsMethods;
    OnItemClickListenerApproval listenerApproval;

    public AdapterCusSingleList(Context context, ArrayList<DcrDetailModelList> dcrApprovalNames, OnItemClickListenerApproval listenerApproval) {
        this.context = context;
        this.dcrApprovalNames = dcrApprovalNames;
        this.listenerApproval = listenerApproval;
    }

    @NonNull
    @Override
    public AdapterCusSingleList.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_dcr_appr_list, parent, false);
        return new AdapterCusSingleList.ViewHolder(view);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull AdapterCusSingleList.ViewHolder holder, int position) {
        commonUtilsMethods = new CommonUtilsMethods(context);

        if (DcrDetailViewActivity.SelectedCode.equalsIgnoreCase(dcrApprovalNames.get(position).getCode())) {
            ProductListNew = new ArrayList<>();
            InputListNew = new ArrayList<>();
            holder.constraint_main.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_purple));
            holder.tv_name.setTextColor(ContextCompat.getColor(context,R.color.white));
            holder.tv_date.setBackground(ContextCompat.getDrawable(context,R.drawable.selector_box));
            holder.list_arrow.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.greater_than_white));

            //ProductAdapter
            for (int i = 0; i < ProductList.size(); i++) {
                if (ProductList.get(i).getCode().equalsIgnoreCase(dcrApprovalNames.get(position).getName())) {
                    ProductListNew.add(new SaveCallProductList(ProductList.get(i).getCode(), ProductList.get(i).getName(), ProductList.get(i).getSample_qty(), ProductList.get(i).getRx_qty(), ProductList.get(i).getRcpa_qty(), ProductList.get(i).getPromoted()));
                }
            }

            Log.v("size", "--prd--" + ProductListNew.size());
            if (ProductListNew.size() > 0) {
                dcrDetailViewBinding.constraintMainSample.setVisibility(View.VISIBLE);
                productAdapter = new ProductAdapter(context, ProductListNew);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(context);
                dcrDetailViewBinding.rvSamplePrd.setLayoutManager(mLayoutManager);
                commonUtilsMethods.recycleTestWithDivider(dcrDetailViewBinding.rvSamplePrd);
                dcrDetailViewBinding.rvSamplePrd.setNestedScrollingEnabled(false);
                dcrDetailViewBinding.rvSamplePrd.setAdapter(productAdapter);
            } else {
                dcrDetailViewBinding.constraintMainSample.setVisibility(View.GONE);
            }

            //InputAdapter
            for (int j = 0; j < InputList.size(); j++) {
                if (InputList.get(j).getInp_code().equalsIgnoreCase(dcrApprovalNames.get(position).getName())) {
                    InputListNew.add(new SaveCallInputList(InputList.get(j).getInp_code(), InputList.get(j).getInput_name(), InputList.get(j).getInp_qty()));
                }
            }

            Log.v("size", "--inp--" + InputListNew.size());
            if (InputListNew.size() > 0) {
                dcrDetailViewBinding.constraintMainInput.setVisibility(View.VISIBLE);
                inputAdapter = new InputAdapter(context, InputListNew);
                RecyclerView.LayoutManager mLayoutManager1 = new LinearLayoutManager(context);
                dcrDetailViewBinding.rvInput.setLayoutManager(mLayoutManager1);
                commonUtilsMethods.recycleTestWithDivider(dcrDetailViewBinding.rvInput);
                dcrDetailViewBinding.rvInput.setNestedScrollingEnabled(false);
                dcrDetailViewBinding.rvInput.setAdapter(inputAdapter);
            } else {
                dcrDetailViewBinding.constraintMainInput.setVisibility(View.GONE);
            }
        } else {
            holder.constraint_main.setBackground(ContextCompat.getDrawable(context,R.drawable.selector_box));
            holder.tv_name.setTextColor(ContextCompat.getColor(context,R.color.dark_purple));
            holder.tv_date.setBackground(ContextCompat.getDrawable(context,R.drawable.bg_light_grey_1));
            holder.list_arrow.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.greater_than_purple));
        }

        holder.tv_date.setVisibility(View.INVISIBLE);
        holder.tv_name.setText(dcrApprovalNames.get(position).getName());

        holder.constraint_main.setOnClickListener(view -> {
            listenerApproval.onClickDcrDetail(new DcrDetailModelList(dcrApprovalNames.get(position).getHq_name(), dcrApprovalNames.get(position).getName(), dcrApprovalNames.get(position).getCode(), dcrApprovalNames.get(position).getTypeCust(), dcrApprovalNames.get(position).getType(), dcrApprovalNames.get(position).getSdp_name(), dcrApprovalNames.get(position).getPob(), dcrApprovalNames.get(position).getRemark(), dcrApprovalNames.get(position).getJointWork(), dcrApprovalNames.get(position).getCall_feedback(), dcrApprovalNames.get(position).getModTime(), dcrApprovalNames.get(position).getVisitTime()));
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return dcrApprovalNames.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<DcrDetailModelList> filteredNames) {
        this.dcrApprovalNames = filteredNames;
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_date;
        ConstraintLayout constraint_main;
        ImageView list_arrow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_date = itemView.findViewById(R.id.tv_date);
            constraint_main = itemView.findViewById(R.id.constraint_main);
            list_arrow = itemView.findViewById(R.id.listArrow);
        }
    }
}
