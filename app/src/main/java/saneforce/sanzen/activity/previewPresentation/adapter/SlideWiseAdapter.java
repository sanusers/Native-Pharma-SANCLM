package saneforce.sanzen.activity.previewPresentation.adapter;

import static saneforce.sanzen.activity.previewPresentation.PreviewActivity.from_where;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.adapter.detailing.PlaySlideDetailing;
import saneforce.sanzen.activity.presentation.SupportClass;
import saneforce.sanzen.activity.presentation.createPresentation.BrandModelClass;
import saneforce.sanzen.activity.presentation.playPreview.PlaySlidePreviewActivity;
import saneforce.sanzen.commonClasses.SafeClickListener;

public class SlideWiseAdapter extends RecyclerView.Adapter<SlideWiseAdapter.MyViewHolder> {
    Context context;
    ArrayList<BrandModelClass> arrayList;
    ArrayList<BrandModelClass.Product> products = new ArrayList<>();
    HashMap<Integer, Integer> productToBrandPosition = new HashMap<>();
    Intent intent;

    public SlideWiseAdapter(Context context, ArrayList<BrandModelClass> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
        productToBrandPosition = new HashMap<>();
        products = new ArrayList<>();
        int k = 0;
        for (int i = 0; i < arrayList.size(); i++) {
            BrandModelClass brand = arrayList.get(i);
            products.addAll(brand.getProductArrayList());
            for (int j = 0; j < brand.getProductArrayList().size(); j++) {
                productToBrandPosition.put(k++, i);
            }
        }
    }

    @NonNull
    @Override
    public SlideWiseAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.preview_item, parent, false);
        return new SlideWiseAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SlideWiseAdapter.MyViewHolder holder, int position) {
        BrandModelClass.Product product = products.get(position);
        holder.name.setText(product.getBrandName());
        holder.count.setText(product.getSlideName());
        holder.info.setVisibility(View.GONE);

        if (!products.isEmpty()) {
            SupportClass.setThumbnail(context, product.getSlideName(), holder.imageView);
        }

        holder.cardView.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                int SelectedPos = 0;
                int count = products.size();
                if (count > 0) {
                    ArrayList<BrandModelClass.Product> productsList = new ArrayList<>(products);
                    if (from_where.equalsIgnoreCase("call")) {
                        SelectedPos = position;
                        intent = new Intent(context, PlaySlideDetailing.class);
                    } else {
                        if (productToBrandPosition.containsKey(position)) {
                            productsList = arrayList.get(productToBrandPosition.get(position)).getProductArrayList();
                        }
                        intent = new Intent(context, PlaySlidePreviewActivity.class);
                    }
                    String data = new Gson().toJson(productsList);
                    Bundle bundle = new Bundle();
                    bundle.putString("slideBundle", data);
                    bundle.putString("position", String.valueOf(SelectedPos));
                    intent.putExtra("bundle", bundle);
                    context.startActivity(intent);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, count;
        
        ImageView imageView, playButton,info;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            count = itemView.findViewById(R.id.assertCount);
            name = itemView.findViewById(R.id.presentationName);
            imageView = itemView.findViewById(R.id.imageView);
            playButton = itemView.findViewById(R.id.play_button);
            cardView = itemView.findViewById(R.id.card_view_top);
            info=itemView.findViewById(R.id.info);
        }
    }
}
