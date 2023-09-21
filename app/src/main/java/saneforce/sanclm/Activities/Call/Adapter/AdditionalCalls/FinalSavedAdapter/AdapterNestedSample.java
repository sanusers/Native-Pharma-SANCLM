package saneforce.sanclm.Activities.Call.Adapter.AdditionalCalls.FinalSavedAdapter;

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
import saneforce.sanclm.Activities.Call.Pojo.AdditionalCalls.NestedAddSampleCallDetails;

public class AdapterNestedSample extends RecyclerView.Adapter<AdapterNestedSample.ViewHolder> {
    Context context;
    ArrayList<NestedAddSampleCallDetails> nestedAddSampleCallDetails;
    int pos;

    public AdapterNestedSample(Context context, ArrayList<NestedAddSampleCallDetails> nestedAddSampleCallDetails) {
        this.context = context;
        this.nestedAddSampleCallDetails = nestedAddSampleCallDetails;
    }

    public AdapterNestedSample(Context context, ArrayList<NestedAddSampleCallDetails> nestedAddSampleCallDetails, int pos) {
        this.context = context;
        this.nestedAddSampleCallDetails = nestedAddSampleCallDetails;
        this.pos = pos;
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

        if (nestedAddSampleCallDetails.get(position).getSam_name().equalsIgnoreCase("^") ||
                nestedAddSampleCallDetails.get(position).getSam_name().equalsIgnoreCase("Select") ||
                nestedAddSampleCallDetails.get(position).getSam_name().isEmpty()) {
            holder.constraintMainSample.setVisibility(View.GONE);
            holder.viewBottom.setVisibility(View.GONE);
        } else {
            holder.constraintMainSample.setVisibility(View.VISIBLE);
            holder.tv_sam_name.setText(nestedAddSampleCallDetails.get(position).getSam_name());
            holder.tv_sam_qty.setText(nestedAddSampleCallDetails.get(position).getSam_qty());

     /*       if (nestedAddSampleCallDetails.get(position).getSam_name().isEmpty() ||
                    nestedAddSampleCallDetails.get(position).getSam_name().equalsIgnoreCase("Select")) {
                holder.tv_sam_name.setVisibility(View.GONE);
            }
            if (nestedAddSampleCallDetails.get(position).getSam_qty().isEmpty()) {
                holder.tv_sam_qty.setVisibility(View.GONE);
            }
            if (nestedAddSampleCallDetails.get(position).getSam_qty().isEmpty() && !nestedAddSampleCallDetails.get(position).getSam_name().equalsIgnoreCase("Select")) {
                holder.tv_sam_qty.setVisibility(View.VISIBLE);
            }*/
        }

    }

    @Override
    public int getItemCount() {
        return nestedAddSampleCallDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
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
