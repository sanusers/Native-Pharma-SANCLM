package saneforce.sanclm.activity.presentation.createPresentation.brand;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.presentation.createPresentation.BrandModelClass;

public class BrandNameAdapter extends RecyclerView.Adapter<BrandNameAdapter.MyViewHolder> {

    Context context;
    ArrayList<BrandModelClass> arrayList;
    BrandNameInterFace brandNameInterFace;

    public BrandNameAdapter () {
    }

    public BrandNameAdapter (Context context, ArrayList<BrandModelClass> arrayList,BrandNameInterFace brandNameInterFace) {
        this.context = context;
        this.arrayList = arrayList;
        this.brandNameInterFace = brandNameInterFace;
    }

    @NonNull
    @Override
    public BrandNameAdapter.MyViewHolder onCreateViewHolder (@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.presentation_brand_name_item,parent,false);
        return new MyViewHolder(view);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder (@NonNull BrandNameAdapter.MyViewHolder holder, int position) {
        BrandModelClass modelClass = arrayList.get(holder.getAbsoluteAdapterPosition());
        holder.setIsRecyclable(false);
        holder.brandName.setText(modelClass.getBrandName());

        if (modelClass.isBrandSelected()){
            holder.itemView.setSelected(true);
            holder.arrow.setImageDrawable(context.getResources().getDrawable(R.drawable.greater_than_black));
        }

        int selectedImageCount = 0;
        for (int i = 0; i<modelClass.getProductArrayList().size(); i++){
            BrandModelClass.Product product = modelClass.getProductArrayList().get(i);
            if (product.isImageSelected()){
                selectedImageCount++;
            }
        }

        if (selectedImageCount > 0)
            holder.count.setText(String.valueOf(selectedImageCount));
        else
            holder.count.setVisibility(View.INVISIBLE);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                for (int i=0;i<arrayList.size();i++){
                    arrayList.get(i).setBrandSelected(false);
                }
                arrayList.get(holder.getAbsoluteAdapterPosition()).setBrandSelected(true);
                brandNameInterFace.onBrandClick(arrayList,holder.getAbsoluteAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount () {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView brandName,count;
        ImageView arrow;

        public MyViewHolder (@NonNull View itemView) {
            super(itemView);
            brandName = itemView.findViewById(R.id.brandName);
            count = itemView.findViewById(R.id.count);
            arrow = itemView.findViewById(R.id.arrow);
        }
    }
}
