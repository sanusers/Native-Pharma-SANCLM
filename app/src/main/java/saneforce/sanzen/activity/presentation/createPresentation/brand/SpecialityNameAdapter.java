package saneforce.sanzen.activity.presentation.createPresentation.brand;

import android.content.Context;
import android.transition.Slide;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.presentation.createPresentation.SpecialityModelClass;

public class SpecialityNameAdapter extends RecyclerView.Adapter<SpecialityNameAdapter.ViewHolder> {

    public interface SpecialityClickListener {
        void onSpecialityClick(SpecialityModelClass speciality, int position);
    }

    private ArrayList<SpecialityModelClass> list;
    private Context context;
    private SpecialityClickListener listener;
    private int selectedPos = 0;

    public SpecialityNameAdapter(Context context, ArrayList<SpecialityModelClass> list, SpecialityClickListener listener) {
        this.context = context;
        this.list = list;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_speciality, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SpecialityModelClass speciality = list.get(holder.getAbsoluteAdapterPosition());
        holder.setIsRecyclable(false);
        holder.tvName.setText(speciality.getDocSpecialName());
        holder.itemView.setSelected(selectedPos == position);
        int selectedCount = speciality.getSelectedSlideCount();

        if (selectedCount > 0) {
            holder.count.setVisibility(View.VISIBLE);
            holder.count.setText(String.valueOf(selectedCount));
        } else {
            holder.count.setVisibility(View.INVISIBLE);
        }



        if (selectedPos == position) {
            holder.arrow.setImageDrawable(context.getResources().getDrawable(R.drawable.greater_than_black));
        } else {
            holder.arrow.setImageDrawable(context.getResources().getDrawable(R.drawable.down_arrow));
        }

        if (speciality.isSpecialitySelected()) {
            selectedPos = position; // make sure selectedPos tracks this
            notifyDataSetChanged(); // refresh to apply arrow
        }

//        if (speciality.isSpecialitySelected()){
//            holder.itemView.setSelected(true);
//            holder.arrow.setImageDrawable(context.getResources().getDrawable(R.drawable.greater_than_black));
//        }
//
//       holder.itemView.setSelected(selectedPos == position);
//        boolean isSelected = selectedPos == position;  // track selected item in adapter
//        holder.itemView.setSelected(isSelected);
//
//        holder.arrow.setImageDrawable(isSelected
//                        ? context.getResources().getDrawable(R.drawable.greater_than_black)
//                        : null // or set default arrow if needed
//        );


            holder.itemView.setOnClickListener(v -> {
                int oldPos = selectedPos;
                selectedPos = holder.getAbsoluteAdapterPosition();

                notifyItemChanged(oldPos);
                notifyItemChanged(selectedPos);

                if (listener != null) {
                    listener.onSpecialityClick(speciality, selectedPos);
                }
            });
//        holder.itemView.setOnClickListener(v -> {
//            if (listener != null) {
//                listener.onSpecialityClick(speciality, position);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvName,count;
        ImageView arrow;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvspeciality);
            arrow  = itemView.findViewById(R.id.arrow);
            count=itemView.findViewById(R.id.count);
        }
    }
}

