package saneforce.sanzen.activity.previewPresentation.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.presentation.SupportClass;
import saneforce.sanzen.activity.presentation.createPresentation.BrandModelClass;

public class SlidePopupAdapter extends RecyclerView.Adapter<SlidePopupAdapter.SlideViewHolder> {

    Context context;
    ArrayList<BrandModelClass.Product> productList;

    public SlidePopupAdapter(Context context, ArrayList<BrandModelClass.Product> productList) {
        this.context = context;
        this.productList = productList;
    }

    @NonNull
    @Override
    public SlideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_slide_popup, parent, false);
        return new SlideViewHolder(view);
    }

   // @Override
//    public void onBindViewHolder(@NonNull SlideViewHolder holder, int position) {
//        BrandModelClass.Product p = productList.get(position);
//        holder.slide_name.setText(p.getSlideName());
//        holder.tv_number.setText((position + 1) + ". ");
//
//    }
    @Override
    public void onBindViewHolder(@NonNull SlideViewHolder holder, int position) {
        BrandModelClass.Product p = productList.get(position);

        holder.slide_name.setText(p.getSlideName());
        holder.tv_number.setText((position + 1) + ". ");

        if ("0".equals(p.getMandatorySlide())) {
            holder.slide_name.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.green_2));
            holder.tv_number.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.green_2));
        } else {
            holder.slide_name.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.black)
            );
            holder.tv_number.setTextColor(
                    ContextCompat.getColor(holder.itemView.getContext(), R.color.black)
            );
        }
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    static class SlideViewHolder extends RecyclerView.ViewHolder {
        TextView slide_name,tv_number;


        public SlideViewHolder(@NonNull View itemView) {
            super(itemView);
            slide_name = itemView.findViewById(R.id.slide_name);
            tv_number = itemView.findViewById(R.id.tv_number);
        }
    }
}
