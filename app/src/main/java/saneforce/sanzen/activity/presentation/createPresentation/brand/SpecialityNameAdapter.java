package saneforce.sanzen.activity.presentation.createPresentation.brand;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.presentation.createPresentation.slide.SpecialityModelClass;

public class SpecialityNameAdapter extends RecyclerView.Adapter<SpecialityNameAdapter.ViewHolder> {

    public interface SpecialityClickListener {
        void onSpecialityClick(SpecialityModelClass speciality, int position);
    }

    private ArrayList<SpecialityModelClass> list;
    private Context context;
    private SpecialityClickListener listener;

    public SpecialityNameAdapter(Context context, ArrayList<SpecialityModelClass> list, SpecialityClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_speciality, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SpecialityModelClass speciality = list.get(position);
        holder.tvName.setText(speciality.getDocSpecialName());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onSpecialityClick(speciality, position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvspeciality);
        }
    }
}

