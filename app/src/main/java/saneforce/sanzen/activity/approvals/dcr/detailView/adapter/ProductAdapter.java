package saneforce.sanzen.activity.approvals.dcr.detailView.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.pojo.product.SaveCallProductList;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.storage.SharedPref;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ViewHolder> {
    Context context;
    ArrayList<SaveCallProductList> getProductList;
    String category;
    CommonUtilsMethods commonUtilsMethods;
    public ProductAdapter(Context context, ArrayList<SaveCallProductList> getProductList,String category) {
        commonUtilsMethods=new CommonUtilsMethods(context);
        this.context = context;
        this.getProductList = getProductList;
        this.category = category;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_dcr_prod, parent, false);
        return new ViewHolder(view);
    }

    @SuppressLint({"ClickableViewAccessibility", "UseCompatLoadingForDrawables"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.tv_name.setText(getProductList.get(position).getName());
        holder.tv_samQty.setText(getProductList.get(position).getSample_qty());
        holder.tv_rxQty.setText(getProductList.get(position).getRx_qty());
        holder.tv_rcpa.setText(getProductList.get(position).getRcpa_qty());
        if(category.equalsIgnoreCase("chemist_") || category.equalsIgnoreCase("stockiest_") || category.equalsIgnoreCase("chemist") || category.equalsIgnoreCase("stockiest")){
            holder.img_promoted.setVisibility(View.INVISIBLE);
        }
        if(getProductList.get(position).getPromoted().equalsIgnoreCase("Yes")){
            holder.img_promoted.setImageDrawable(context.getResources().getDrawable(R.drawable.tick_icone));
        }else {
            holder.img_promoted.setImageDrawable(context.getResources().getDrawable(R.drawable.gray_cross_icon));
        }
        holder.tv_name.setOnClickListener(view -> {
            commonUtilsMethods.displayPopupWindow(context, view, getProductList.get(position).getName());
       });
        switch (category){
            case Constants.DOCTOR:
            case "DOCTOR":
                if (SharedPref.getRcpaQtyNeed(context).equals("0")){
                    holder.tv_rcpa.setVisibility(View.VISIBLE);
                }else{
                    holder.tv_rcpa.setVisibility(View.INVISIBLE);
                }
                if(SharedPref.getDrRxNd(context).equalsIgnoreCase("0")) {
                    holder.tv_rxQty.setVisibility(View.INVISIBLE);
                } else {
                    holder.tv_rxQty.setVisibility(View.VISIBLE);
                }
                holder.img_promoted.setVisibility(View.VISIBLE);
                break;
            case Constants.CHEMIST:
            case "CHEMIST":
                if(SharedPref.getChmRxQty(context).equalsIgnoreCase("1")) {
                    holder.tv_rxQty.setVisibility(View.INVISIBLE);
                } else {
                    holder.tv_rxQty.setVisibility(View.VISIBLE);
                }
                holder.tv_rcpa.setVisibility(View.INVISIBLE);
                holder.img_promoted.setVisibility(View.INVISIBLE);
                break;
            case Constants.STOCKIEST:
            case "STOCKIST":
                if(SharedPref.getStkPobNeed(context).equalsIgnoreCase("1")) {
                    holder.tv_rxQty.setVisibility(View.INVISIBLE);
                } else {
                    holder.tv_rxQty.setVisibility(View.VISIBLE);
                }
                holder.tv_rcpa.setVisibility(View.INVISIBLE);
                holder.img_promoted.setVisibility(View.INVISIBLE);
                break;
            case Constants.UNLISTED_DOCTOR:
            case "ULDOCTOR":
                if(SharedPref.getUlPobNeed(context).equalsIgnoreCase("1")) {
                    holder.tv_rxQty.setVisibility(View.INVISIBLE);
                } else {
                    holder.tv_rxQty.setVisibility(View.VISIBLE);
                }
                holder.tv_rcpa.setVisibility(View.INVISIBLE);
                holder.img_promoted.setVisibility(View.VISIBLE);
                break;
            default:
                holder.tv_rcpa.setVisibility(View.INVISIBLE);
                holder.tv_rxQty.setVisibility(View.INVISIBLE);
                holder.img_promoted.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return getProductList.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_samQty, tv_rxQty, tv_rcpa;

        ImageView img_promoted;

        public ViewHolder(@NonNull View v) {
            super(v);
            tv_name = v.findViewById(R.id.tv_prd_name);
            img_promoted = v.findViewById(R.id.img_promoted);
            tv_samQty = v.findViewById(R.id.tv_samples);
            tv_rxQty = v.findViewById(R.id.tv_rx_qty);
            tv_rcpa = v.findViewById(R.id.tv_rcpa);
        }
    }
}
