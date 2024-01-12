package saneforce.santrip.activity.approvals.dcr.call.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.call.pojo.input.SaveCallInputList;

public class InputAdapter extends RecyclerView.Adapter<InputAdapter.ViewHolder> {
    Context context;
    ArrayList<SaveCallInputList> getInputList;

    public InputAdapter(Context context, ArrayList<SaveCallInputList> getInputList) {
        this.context = context;
        this.getInputList = getInputList;
    }

    @NonNull
    @Override
    public InputAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_dcr_inp, parent, false);
        return new InputAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InputAdapter.ViewHolder holder, int position) {
        holder.tv_name.setText(getInputList.get(position).getInput_name());
        holder.tv_qty.setText(getInputList.get(position).getInp_qty());
    }

    @Override
    public int getItemCount() {
        return getInputList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_qty;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_input_name);
            tv_qty = itemView.findViewById(R.id.tv_inp_qty);
        }
    }
}
