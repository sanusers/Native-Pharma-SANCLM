package saneforce.sanzen.activity.call.profile.preCallAnalysis;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.LinkedHashMap;
import java.util.LinkedList;

import saneforce.sanzen.R;

public class DetailedSlidesAdapter extends RecyclerView.Adapter<DetailedSlidesAdapter.ViewHolder> {
    private Context context;
    private LinkedHashMap<String, LinkedList<String>> detailedSlidesMap;
    private LinkedList<String> brands;

    public DetailedSlidesAdapter(Context context, LinkedList<String> brands, LinkedHashMap<String, LinkedList<String>> detailedSlidesMap) {
        this.context = context;
        this.brands = brands;
        this.detailedSlidesMap = detailedSlidesMap;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_detailed_slides, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String brand = brands.get(position);
        holder.tvBrandName.setText(brand);
        StringBuilder slides = new StringBuilder();
        try {
            for (int i = 0; i < detailedSlidesMap.get(brand).size(); i++) {
                String slide = detailedSlidesMap.get(brand).get(i);
                slides.append(slide);
                if (i != (detailedSlidesMap.get(brand).size() - 1)) {
                    slides.append("\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        holder.tvSlideName.setText(slides);
    }

    @Override
    public int getItemCount() {
        return brands.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvBrandName, tvSlideName;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvBrandName = itemView.findViewById(R.id.brand_name);
            tvSlideName = itemView.findViewById(R.id.slide_name);
        }
    }
}
