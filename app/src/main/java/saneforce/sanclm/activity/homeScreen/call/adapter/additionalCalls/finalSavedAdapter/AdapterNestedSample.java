package saneforce.sanclm.activity.homeScreen.call.adapter.additionalCalls.finalSavedAdapter;

import android.content.Context;
import android.util.Log;
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

public class AdapterNestedSample extends RecyclerView.Adapter<AdapterNestedSample.ViewHolder> {
    ArrayList<AddSampleAdditionalCall> nestedAddSampleCallDetails;
    Context context;

    public AdapterNestedSample(Context context, ArrayList<AddSampleAdditionalCall> nestedAddSampleCallDetails) {
        this.context = context;
        this.nestedAddSampleCallDetails = nestedAddSampleCallDetails;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_nested_sample, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.v("countnested", "--sam-" + nestedAddSampleCallDetails.size());
        holder.tv_sam_name.setText(nestedAddSampleCallDetails.get(position).getPrd_name());
        holder.tv_sam_qty.setText(nestedAddSampleCallDetails.get(position).getSample_qty());
        if (nestedAddSampleCallDetails.size() > 1) {
            holder.viewBottom.setVisibility(View.VISIBLE);
        } else {
            holder.viewBottom.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return nestedAddSampleCallDetails.size();
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
