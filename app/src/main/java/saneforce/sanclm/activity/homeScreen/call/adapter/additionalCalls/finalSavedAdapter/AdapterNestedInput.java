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
import saneforce.sanclm.activity.homeScreen.call.pojo.additionalCalls.AddInputAdditionalCall;

public class AdapterNestedInput extends RecyclerView.Adapter<AdapterNestedInput.ViewHolder> {
    ArrayList<AddInputAdditionalCall> nestedAddInputCallDetails;
    Context context;

    public AdapterNestedInput(Context context, ArrayList<AddInputAdditionalCall> nestedAddInputCallDetails) {
        this.context = context;
        this.nestedAddInputCallDetails = nestedAddInputCallDetails;
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
        holder.tv_inp_name.setText(nestedAddInputCallDetails.get(position).getInput_name());
        holder.tv_inp_qty.setText(nestedAddInputCallDetails.get(position).getInp_qty());
        if (nestedAddInputCallDetails.size() > 1) {
            holder.viewBottom.setVisibility(View.VISIBLE);
        } else {
            holder.viewBottom.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return nestedAddInputCallDetails.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
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
