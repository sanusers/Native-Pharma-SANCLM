package saneforce.santrip.activity.presentation.playPreview;


import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.net.Uri;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;
import android.widget.RelativeLayout;


import androidx.annotation.NonNull;

import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;


import java.io.File;
import java.util.ArrayList;

import saneforce.santrip.R;

import saneforce.santrip.activity.presentation.SupportClass;
import saneforce.santrip.activity.presentation.createPresentation.BrandModelClass;

public class PlaySlidePagerAdapter extends PagerAdapter {

    private final Context context;
    private final ArrayList<BrandModelClass.Product> productArrayList;

    public PlaySlidePagerAdapter (Context context, ArrayList<BrandModelClass.Product> productArrayList) {
        this.context = context;
        this.productArrayList = productArrayList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View sliderLayout = inflater.inflate(R.layout.presentation_preview_item, null);

        ImageView imageView = sliderLayout.findViewById(R.id.imageView);
        RelativeLayout rl_rightView = sliderLayout.findViewById(R.id.rightArrow);
        rl_rightView.setVisibility(View.GONE);
        SupportClass.setThumbnail(context, productArrayList.get(position).getSlideName(), imageView);
        container.addView(sliderLayout);

        return sliderLayout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return productArrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

}