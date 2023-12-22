package saneforce.sanclm.activity.homeScreen.call.adapter.additionalCalls.finalSavedAdapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.pojo.additionalCalls.AddSampleAdditionalCall;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;

public class AdapterNestedProduct extends RecyclerView.Adapter<AdapterNestedProduct.ViewHolder> {
    ArrayList<AddSampleAdditionalCall> nestedProduct;
    Context context;
    CommonUtilsMethods commonUtilsMethods;
    Activity activity;

    public AdapterNestedProduct(Activity activity,Context context, ArrayList<AddSampleAdditionalCall> nestedProduct) {
      this.activity = activity;
        this.context = context;
        this.nestedProduct = nestedProduct;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_nested_sample, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        commonUtilsMethods = new CommonUtilsMethods(context);
        holder.tv_sam_name.setText(nestedProduct.get(position).getPrd_name());

        holder.tv_sam_name.setOnClickListener(view -> commonUtilsMethods.displayPopupWindow(activity, context, view, nestedProduct.get(position).getPrd_name()));

        if (nestedProduct.get(position).getSample_qty().isEmpty()) {
            holder.tv_sam_qty.setText("0");
        } else {
            holder.tv_sam_qty.setText(nestedProduct.get(position).getSample_qty());
        }

        if (nestedProduct.size() > 1) {
            holder.viewBottom.setVisibility(View.VISIBLE);
        } else {
            holder.viewBottom.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return nestedProduct.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout constraintMainSample;
        TextView tv_sam_name, tv_sam_qty;
        View viewBottom;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintMainSample = itemView.findViewById(R.id.constraint_main_sample);
            tv_sam_name = itemView.findViewById(R.id.tv_sample);
            tv_sam_qty = itemView.findViewById(R.id.tv_sam_qty);
            viewBottom = itemView.findViewById(R.id.view_bottom);
        }
    }
}
