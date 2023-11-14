package saneforce.sanclm.activity.approvals.dcr.dcrdetaillist.adapter;

import static saneforce.sanclm.activity.approvals.dcr.dcrdetaillist.DcrDetailViewActivity.dcrDetailViewBinding;

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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.approvals.dcr.dcrdetaillist.DcrDetailViewActivity;
import saneforce.sanclm.activity.approvals.dcr.pojo.DcrDetailModelList;
import saneforce.sanclm.activity.homeScreen.call.pojo.input.SaveCallInputList;
import saneforce.sanclm.activity.homeScreen.call.pojo.product.SaveCallProductList;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;

public class AdapterCustSingleList extends RecyclerView.Adapter<AdapterCustSingleList.ViewHolder> {
    public static int selectedPosition = -1;
    public static ArrayList<SaveCallProductList> ProductList;
    public static ArrayList<SaveCallInputList> InputList;
    Context context;
    ArrayList<DcrDetailModelList> dcrApprovalNames;
    ArrayList<SaveCallProductList> ProductListNew;
    ArrayList<SaveCallInputList> InputListNew;
    ProductAdapter productAdapter;
    InputAdapter inputAdapter;
    int lastSelectedPosition = -1;
    CommonUtilsMethods commonUtilsMethods;

    public AdapterCustSingleList(Context context, ArrayList<DcrDetailModelList> dcrApprovalNames) {
        this.context = context;
        this.dcrApprovalNames = dcrApprovalNames;
    }

    @NonNull
    @Override
    public AdapterCustSingleList.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_dcr_appr_list, parent, false);
        return new AdapterCustSingleList.ViewHolder(view);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull AdapterCustSingleList.ViewHolder holder, int position) {
        commonUtilsMethods = new CommonUtilsMethods(context);

        if (DcrDetailViewActivity.SelectedName.equalsIgnoreCase(dcrApprovalNames.get(position).getName())) {
            ProductListNew = new ArrayList<>();
            InputListNew = new ArrayList<>();
            holder.constraint_main.setBackground(context.getResources().getDrawable(R.drawable.bg_purple));
            holder.tv_name.setTextColor(context.getResources().getColor(R.color.white));
            holder.tv_date.setBackground(context.getResources().getDrawable(R.drawable.selector_box));
            holder.list_arrow.setImageDrawable(context.getResources().getDrawable(R.drawable.greater_than_white));

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
            holder.constraint_main.setBackground(context.getResources().getDrawable(R.drawable.selector_box));
            holder.tv_name.setTextColor(context.getResources().getColor(R.color.dark_purple));
            holder.tv_date.setBackground(context.getResources().getDrawable(R.drawable.bg_light_grey_1));
            holder.list_arrow.setImageDrawable(context.getResources().getDrawable(R.drawable.greater_than_purple));
        }

      /*  if (selectedPosition == holder.getBindingAdapterPosition()) {
            ProductListNew = new ArrayList<>();
            InputListNew = new ArrayList<>();
            holder.constraint_main.setBackground(context.getResources().getDrawable(R.drawable.bg_purple));
            holder.tv_name.setTextColor(context.getResources().getColor(R.color.white));
            holder.tv_date.setBackground(context.getResources().getDrawable(R.drawable.selector_box));
            holder.list_arrow.setImageDrawable(context.getResources().getDrawable(R.drawable.greater_than_white));

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
            holder.constraint_main.setBackground(context.getResources().getDrawable(R.drawable.selector_box));
            holder.tv_name.setTextColor(context.getResources().getColor(R.color.dark_purple));
            holder.tv_date.setBackground(context.getResources().getDrawable(R.drawable.bg_light_grey_1));
            holder.list_arrow.setImageDrawable(context.getResources().getDrawable(R.drawable.greater_than_purple));
        }
*/
        holder.tv_date.setVisibility(View.INVISIBLE);
        holder.tv_name.setText(dcrApprovalNames.get(position).getName());

        holder.constraint_main.setOnClickListener(view -> {
        /*    lastSelectedPosition = selectedPosition;
            selectedPosition = holder.getBindingAdapterPosition();
            notifyItemChanged(lastSelectedPosition);
            notifyItemChanged(selectedPosition);*/

            dcrDetailViewBinding.tvName.setText(dcrApprovalNames.get(position).getName());
            DcrDetailViewActivity.SelectedName = dcrApprovalNames.get(position).getName();

            switch (dcrApprovalNames.get(position).getType()) {
                case "1":
                    dcrDetailViewBinding.imgCust.setImageDrawable(context.getResources().getDrawable(R.drawable.map_dr_img));
                    break;
                case "2":
                    dcrDetailViewBinding.imgCust.setImageDrawable(context.getResources().getDrawable(R.drawable.map_chemist_img));
                    break;
                case "3":
                    dcrDetailViewBinding.imgCust.setImageDrawable(context.getResources().getDrawable(R.drawable.map_stockist_img));
                    break;
                case "4":
                    dcrDetailViewBinding.imgCust.setImageDrawable(context.getResources().getDrawable(R.drawable.map_unlistdr_img));
                    break;
            }

            if (!dcrApprovalNames.get(position).getSdp_name().isEmpty()) {
                dcrDetailViewBinding.tvClusterTop.setText(dcrApprovalNames.get(position).getSdp_name());
            } else {
                dcrDetailViewBinding.tvClusterTop.setText(context.getResources().getText(R.string.no_cluster));
            }
            if (!dcrApprovalNames.get(position).getPob().isEmpty()) {
                dcrDetailViewBinding.tvPob.setText(dcrApprovalNames.get(position).getPob());
            } else {
                dcrDetailViewBinding.tvPob.setText("0");
            }
            if (!dcrApprovalNames.get(position).getJointWork().isEmpty()) {
                dcrDetailViewBinding.tvJw.setText(dcrApprovalNames.get(position).getJointWork());
            } else {
                dcrDetailViewBinding.tvJw.setText(context.getResources().getText(R.string.no_jw));
            }
            if (!dcrApprovalNames.get(position).getRemark().isEmpty()) {
                dcrDetailViewBinding.tvOverallRemarks.setText(dcrApprovalNames.get(position).getRemark());
            } else {
                dcrDetailViewBinding.tvOverallRemarks.setText(context.getResources().getText(R.string.no_remarks));
            }
            if (!dcrApprovalNames.get(position).getCall_feedback().isEmpty()) {
                dcrDetailViewBinding.tvOverallFeedback.setText(dcrApprovalNames.get(position).getCall_feedback());
            } else {
                dcrDetailViewBinding.tvOverallFeedback.setText(context.getResources().getText(R.string.no_feedback));
            }
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return dcrApprovalNames.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void filterList(ArrayList<DcrDetailModelList> filterdNames) {
        this.dcrApprovalNames = filterdNames;
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
