package saneforce.sanzen.activity.previewPresentation.adapter;

import static saneforce.sanzen.activity.previewPresentation.PreviewActivity.from_where;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.adapter.detailing.TimelineAdapter;
import saneforce.sanzen.commonClasses.SafeClickListener;
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

        if (arrayList.get(position).getBrandName().equalsIgnoreCase("Welcome")) {
            holder.info.setVisibility(View.GONE);
        } else {
            holder.info.setVisibility(View.VISIBLE);
        }

        holder.cardView.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                int SelectedPos = 0;
                int count = products.size();
                if (count > 0) {
                    ArrayList<BrandModelClass.Product> productsList = new ArrayList<>();
                    if (from_where.equalsIgnoreCase("call")) {
                        for (int i = 0; i < arrayList.size(); i++) {
                            for (int j = 0; j < arrayList.get(i).getProductArrayList().size(); j++) {
                                productsList.add(new BrandModelClass.Product(arrayList.get(i).getBrandCode(), arrayList.get(i).getBrandName(), arrayList.get(i).getProductArrayList().get(j).getSlideId(), arrayList.get(i).getProductArrayList().get(j).getSlideName(), arrayList.get(i).getProductArrayList().get(j).getPriority(), arrayList.get(i).getProductArrayList().get(j).isImageSelected(), arrayList.get(i).getProductArrayList().get(j).getProductCode(), arrayList.get(i).getProductArrayList().get(j).getMandatorySlide()));
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
                    String data = new Gson().toJson(productsList);
                    Bundle bundle = new Bundle();
                    bundle.putString("slideBundle", data);
                    bundle.putString("position", String.valueOf(SelectedPos));
                    intent.putExtra("bundle", bundle);
                    context.startActivity(intent);
                }
            }
        });

        holder.info.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                showSlidePopUp(view,
                        arrayList.get(position).getProductArrayList());
            }
        });

    }
    private void showSlidePopUp(View anchor,  ArrayList<BrandModelClass.Product> products) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View popupView = layoutInflater.inflate(R.layout.slide_popup, null);
        RecyclerView viewPagerRecyclerview = popupView.findViewById(R.id.view_pager);
        int recyclerHeight = 150;
        if (products.size() < 4) {
            recyclerHeight = WindowManager.LayoutParams.WRAP_CONTENT;
        }
        if (recyclerHeight != WindowManager.LayoutParams.WRAP_CONTENT) {
            recyclerHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, recyclerHeight, context.getResources().getDisplayMetrics());
        }

        ViewGroup.LayoutParams params = viewPagerRecyclerview.getLayoutParams();
        params.height = recyclerHeight;
        viewPagerRecyclerview.setLayoutParams(params);
        GridLayoutManager layoutManager = new GridLayoutManager(context, 4);
        viewPagerRecyclerview.setLayoutManager(layoutManager);
//        viewPagerRecyclerview.setHasFixedSize(true);
        SlidePopupAdapter adapter = new SlidePopupAdapter(context, products);
        viewPagerRecyclerview.setAdapter(adapter);
        RecyclerView rvSlides = popupView.findViewById(R.id.view_pager);
        rvSlides.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));

//        SlidePopupAdapter adapter = new SlidePopupAdapter(context, products);
//        rvSlides.setAdapter(adapter);

        PopupWindow popupWindow = new PopupWindow(popupView, WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT, true);

        int[] location = new int[2];
        anchor.getLocationOnScreen(location);
        popupWindow.setOutsideTouchable(true);

        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        int width = popupView.getMeasuredWidth();
        int height = popupView.getMeasuredHeight();

        popupWindow.showAtLocation(anchor, Gravity.NO_GRAVITY, location[0] - width + 25, location[1] - height + 5);
    }


    @Override
    public int getItemCount() {
        return arrayList.size();
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
