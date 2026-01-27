package saneforce.sanzen.activity.call.fragments.jwOthers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashSet;

import saneforce.sanzen.R;

public class PopupNameAdapter extends RecyclerView.Adapter<PopupNameAdapter.ViewHolder> {

    private ArrayList<String> names;
    private Context context;
    public static HashSet<String> selectedNames = new HashSet<>();

    public PopupNameAdapter(Context context, ArrayList<String> names) {
        this.context = context;
        this.names = names;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_checked_data, parent, false);
        return new ViewHolder(view);
    }

    //    @Override
//    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
//        String name = names.get(position);
//
//        holder.tvName.setText(name);
//        holder.tvName.setSingleLine(false);
//
//        holder.checkBox.setOnCheckedChangeListener(null);
//        holder.checkBox.setChecked(selectedNames.contains(name));
//
//        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
//            if (isChecked) selectedNames.add(name);
//            else selectedNames.remove(name);
//        });
//
//        // clicking name toggles checkbox
//        holder.tvName.setOnClickListener(v -> holder.checkBox.performClick());
//    }
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String name = names.get(position);

        holder.tvName.setText(name);
        holder.tvName.setSingleLine(false);

        // detach listener
        holder.checkBox.setOnCheckedChangeListener(null);

        // set checked state
        boolean isChecked = selectedNames.contains(name);
        holder.checkBox.setChecked(isChecked);

        // set checkbox color immediately
        holder.checkBox.setButtonTintList(
                android.content.res.ColorStateList.valueOf(
                        isChecked ?
                                androidx.core.content.ContextCompat.getColor(context, R.color.green_2) :
                                androidx.core.content.ContextCompat.getColor(context, R.color.bg_txt_color)
                )
        );

        // attach listener
        holder.checkBox.setOnCheckedChangeListener((buttonView, checked) -> {
            if (checked) selectedNames.add(name);
            else selectedNames.remove(name);

            // update checkbox tint immediately
            holder.checkBox.setButtonTintList(
                    android.content.res.ColorStateList.valueOf(
                            checked ?
                                    androidx.core.content.ContextCompat.getColor(context, R.color.green_2) :
                                    androidx.core.content.ContextCompat.getColor(context, R.color.bg_txt_color)
                    )
            );
        });

        // clicking name toggles checkbox
       // holder.tvName.setOnClickListener(v -> holder.checkBox.performClick());
    }

    @Override
    public int getItemCount() {
        return names.size();
    }

    public static HashSet<String> getSelectedNames() {
        return selectedNames;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView tvName;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.chk_box);
            tvName = itemView.findViewById(R.id.tv_data_name);
        }
    }
}
