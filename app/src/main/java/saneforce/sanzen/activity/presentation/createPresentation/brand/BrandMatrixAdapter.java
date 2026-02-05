package saneforce.sanzen.activity.presentation.createPresentation.brand;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.presentation.createPresentation.BrandMatrixModelClass;
import saneforce.sanzen.activity.presentation.createPresentation.BrandModelClass;
import saneforce.sanzen.activity.presentation.createPresentation.SpecialityModelClass;

public class BrandMatrixAdapter extends RecyclerView.Adapter<BrandMatrixAdapter.ViewHolder> {
  //  private ArrayList<BrandMatrixModelClass> list;
    private  Context context;
    private ArrayList<BrandMatrixModelClass> brandList;
    private  OnBrandClickListener clickListener;
    private int selectedPos = 0;
    public int getSelectedPosition() {
        return selectedPos;
    }
    public interface OnBrandClickListener {
        void onBrandClick(BrandMatrixModelClass brand, int position);
    }

    public BrandMatrixAdapter(Context context, ArrayList<BrandMatrixModelClass> brandList, OnBrandClickListener clickListener) {
        this.context = context;
        this.brandList = brandList;
        this.clickListener = clickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_brand_name, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BrandMatrixModelClass brand = brandList.get(position);
        holder.setIsRecyclable(false);
        holder.brandName.setText(brand.getCode());
        holder.itemView.setSelected(selectedPos == position);
        int selectedCount = brand.getSelectedSlideCount();


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
        holder.itemView.setOnClickListener(v -> {
            int oldPos = selectedPos;
            selectedPos = holder.getAbsoluteAdapterPosition();

            notifyItemChanged(oldPos);
            notifyItemChanged(selectedPos);

            if (clickListener != null) {
                clickListener.onBrandClick(brand, selectedPos);
            }
        });
//        holder.itemView.setOnClickListener(v -> {
//            // Mark clicked brand as selected
//            for (BrandMatrixModelClass b : brandList) b.setBrandSelected(false);
//            brand.setBrandSelected(true);
//            notifyDataSetChanged();
//
//            // Callback to populate SlideAdapter
//            clickListener.onBrandClick(brandList, position);
//        });
    }

    @Override
    public int getItemCount() {
        return brandList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView brandName,count;
        ImageView arrow;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            brandName = itemView.findViewById(R.id.tvBrandMatrix); // your TextView id
            arrow  = itemView.findViewById(R.id.arrow);
            count=itemView.findViewById(R.id.count);
        }
    }
}
