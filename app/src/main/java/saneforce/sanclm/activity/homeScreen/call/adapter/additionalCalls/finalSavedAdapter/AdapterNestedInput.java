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
import saneforce.sanclm.activity.homeScreen.call.pojo.additionalCalls.NestedAddInputCallDetails;

public class AdapterNestedInput extends RecyclerView.Adapter<AdapterNestedInput.ViewHolder> {
    Context context;
    ArrayList<NestedAddInputCallDetails> nestedAddInputCallDetails;
    int pos;

    public AdapterNestedInput(Context context, ArrayList<NestedAddInputCallDetails> nestedAddInputCallDetails) {
        this.context = context;
        this.nestedAddInputCallDetails = nestedAddInputCallDetails;
    }

    public AdapterNestedInput(Context context, ArrayList<NestedAddInputCallDetails> nestedAddInputCallDetails, int pos) {
        this.context = context;
        this.nestedAddInputCallDetails = nestedAddInputCallDetails;
        this.pos = pos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_nested_input, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Log.v("countnested", "--inp--" + nestedAddInputCallDetails.size());

        if (nestedAddInputCallDetails.get(position).getInp_name().equalsIgnoreCase("^") ||
                nestedAddInputCallDetails.get(position).getInp_name().equalsIgnoreCase("Select") ||
                nestedAddInputCallDetails.get(position).getInp_name().isEmpty()) {
            holder.constraintMainInput.setVisibility(View.GONE);
            holder.viewBottom.setVisibility(View.GONE);
        } else {
            holder.constraintMainInput.setVisibility(View.VISIBLE);
            holder.tv_inp_name.setText(nestedAddInputCallDetails.get(position).getInp_name());
            holder.tv_inp_qty.setText(nestedAddInputCallDetails.get(position).getInp_qty());

          /*  if (nestedAddInputCallDetails.get(position).getInp_name().isEmpty() || nestedAddInputCallDetails.get(position).getInp_name().equalsIgnoreCase("Select")) {
                holder.tv_inp_name.setVisibility(View.GONE);
            }
            if (nestedAddInputCallDetails.get(position).getInp_qty().isEmpty()) {
                holder.tv_inp_qty.setVisibility(View.GONE);
            }
            if (nestedAddInputCallDetails.get(position).getInp_qty().isEmpty() && !nestedAddInputCallDetails.get(position).getInp_name().equalsIgnoreCase("Select")) {
                holder.tv_inp_qty.setVisibility(View.VISIBLE);
            }*/
        }
    }

    @Override
    public int getItemCount() {
        return nestedAddInputCallDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout constraintMainInput;
        TextView tv_inp_name, tv_inp_qty;
        View viewBottom;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintMainInput = itemView.findViewById(R.id.constraint_main_input);
            tv_inp_name = itemView.findViewById(R.id.tv_input);
            tv_inp_qty = itemView.findViewById(R.id.tv_input_qty);
            viewBottom = itemView.findViewById(R.id.view_bottom);
        }
    }
}
