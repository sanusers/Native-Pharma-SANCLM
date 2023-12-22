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
import saneforce.sanclm.activity.homeScreen.call.pojo.additionalCalls.AddInputAdditionalCall;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;

public class AdapterNestedInput extends RecyclerView.Adapter<AdapterNestedInput.ViewHolder> {
    ArrayList<AddInputAdditionalCall> nestedInput;
    Context context;
    Activity activity;
    CommonUtilsMethods commonUtilsMethods;

    public AdapterNestedInput(Activity activity,Context context, ArrayList<AddInputAdditionalCall> nestedInput) {
        this.context = context;
        this.nestedInput = nestedInput;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_nested_input, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        commonUtilsMethods = new CommonUtilsMethods(context);
        holder.tv_inp_name.setText(nestedInput.get(position).getInput_name());

        holder.tv_inp_name.setOnClickListener(view -> commonUtilsMethods.displayPopupWindow(activity, context, view, nestedInput.get(holder.getBindingAdapterPosition()).getInput_name()));

        if (nestedInput.get(position).getInp_qty().isEmpty()) {
            holder.tv_inp_qty.setText("0");
        } else {
            holder.tv_inp_qty.setText(nestedInput.get(position).getInp_qty());
        }
        if (nestedInput.size() > 1) {
            holder.viewBottom.setVisibility(View.VISIBLE);
        } else {
            holder.viewBottom.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return nestedInput.size();
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