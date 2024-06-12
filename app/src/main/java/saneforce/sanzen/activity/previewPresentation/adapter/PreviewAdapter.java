package saneforce.sanzen.activity.previewPresentation.adapter;

import static saneforce.sanzen.activity.previewPresentation.PreviewActivity.from_where;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import java.util.Collections;
import java.util.Comparator;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.adapter.detailing.PlaySlideDetailing;
import saneforce.sanzen.activity.presentation.SupportClass;
import saneforce.sanzen.activity.presentation.createPresentation.BrandModelClass;
import saneforce.sanzen.activity.presentation.playPreview.PlaySlidePreviewActivity;

public class PreviewAdapter extends RecyclerView.Adapter<PreviewAdapter.MyViewHolder> {
    Context context;
    ArrayList<BrandModelClass> arrayList;
    ArrayList<BrandModelClass.Product> products = new ArrayList<>();
    Intent intent;


    public PreviewAdapter(Context context, ArrayList<BrandModelClass> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override
    public PreviewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.preview_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PreviewAdapter.MyViewHolder holder, int position) {

        holder.name.setText(arrayList.get(position).getBrandName());
        products = arrayList.get(position).getProductArrayList();

        if (!products.isEmpty()) SupportClass.setThumbnail(context, products.get(0).getSlideName(), holder.imageView);

        if (products.size() > 1) holder.count.setText(products.size() + " Asserts");
        else holder.count.setText(products.size() + " Assert");


        holder.cardView.setOnClickListener(view -> {
            int SelectedPos = 0;

            int count = products.size();
            if (count > 0) {
                ArrayList<BrandModelClass.Product> productsList = new ArrayList<>();
                if (from_where.equalsIgnoreCase("call")) {
                    for (int i = 0; i < arrayList.size(); i++) {
                        for (int j = 0; j < arrayList.get(i).getProductArrayList().size(); j++) {
                            productsList.add(new BrandModelClass.Product(arrayList.get(i).getBrandCode(), arrayList.get(i).getBrandName(), arrayList.get(i).getProductArrayList().get(j).getSlideId()
                                    , arrayList.get(i).getProductArrayList().get(j).getSlideName(), arrayList.get(i).getProductArrayList().get(j).getPriority(), arrayList.get(i).getProductArrayList().get(j).isImageSelected()));
                        }
                    }

                    for (int i = 0; i < productsList.size(); i++) {
                        if (productsList.get(i).getBrandCode().equalsIgnoreCase(arrayList.get(position).getBrandCode())) {
                            SelectedPos = i;
                            break;
                        }
                    }
                    intent = new Intent(context, PlaySlideDetailing.class);
                } else {
                    productsList = arrayList.get(position).getProductArrayList();
                    intent = new Intent(context, PlaySlidePreviewActivity.class);
                }
                for (BrandModelClass.Product product : products) {
                    Log.d("TAG2", "onBindViewHolder: "+ product.getBrandName() + " " + product.getSlideName() + ": " + product.getPriority());
                }
                for (BrandModelClass.Product product : productsList) {
                    Log.d("TAG1", "onBindViewHolder: "+ product.getBrandName() + " " + product.getSlideName() + ": " + product.getPriority());
                }
                Collections.sort(productsList, Comparator.comparingInt(p -> Integer.parseInt(p.getPriority())));
                for (BrandModelClass.Product product : productsList) {
                    Log.d("TAG3", "onBindViewHolder: "+ product.getBrandName() + " " + product.getSlideName() + ": " + product.getPriority());
                }
                String data = new Gson().toJson(productsList);
                Bundle bundle = new Bundle();
                bundle.putString("slideBundle", data);
                bundle.putString("position", String.valueOf(SelectedPos));
                intent.putExtra("bundle", bundle);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return arrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, count;
        ImageView imageView, playButton;
        CardView cardView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            count = itemView.findViewById(R.id.assertCount);
            name = itemView.findViewById(R.id.presentationName);
            imageView = itemView.findViewById(R.id.imageView);
            playButton = itemView.findViewById(R.id.play_button);
            cardView = itemView.findViewById(R.id.card_view_top);
        }
    }
}
