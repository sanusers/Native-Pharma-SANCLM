package saneforce.sanzen.activity.approvals.dcr.detailView.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.pojo.input.SaveCallInputList;

public class InputAdapter extends RecyclerView.Adapter<InputAdapter.ViewHolder> {
    Context context;
    ArrayList<SaveCallInputList> getInputList;

    public  InputAdapter(Context context, ArrayList<SaveCallInputList> getInputList) {
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
        holder.tv_name.setOnClickListener(v -> {
            popUp(holder.tv_name,getInputList.get(position).getInput_name());

        });
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

    private void popUp(View v, String name) {
        PopupWindow popup = new PopupWindow(context);
        View layout = LayoutInflater.from(context).inflate(R.layout.popup_text, null);
        popup.setContentView(layout);
        popup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        TextView tv_name = layout.findViewById(R.id.tv_name);
        tv_name.setText(name);
        popup.setOutsideTouchable(true);
        popup.showAsDropDown(v);
    }
}
