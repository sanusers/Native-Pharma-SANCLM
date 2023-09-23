package saneforce.sanclm.activity.map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import saneforce.sanclm.R;
import saneforce.sanclm.storage.SQLiteHandler;
import saneforce.sanclm.storage.SharedPref;

public class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private View customView = null;
    Context context;
    ViewTagModel viewTagModels;
    SQLiteHandler db;
    TextView txtview;
    TextView txtadd, txtImage;


    public MyInfoWindowAdapter(ViewTagModel mm, Context context) {
        try {
            customView = View.inflate(context, R.layout.map_info_window, null);
            this.viewTagModels = mm;
            this.context = context;
        } catch (Exception e) {

        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View getInfoContents(final Marker marker) {
        db = new SQLiteHandler(context);
       // mCommonsharedpreference = new CommonSharedPreference(context);
        String url = null;
        txtview = customView.findViewById(R.id.txt_drName);
        txtadd = customView.findViewById(R.id.txt_address);
        txtImage = customView.findViewById(R.id.btn_view_image);

        String add = marker.getSnippet().substring(marker.getSnippet().lastIndexOf("&") + 1);

        txtview.setText(marker.getSnippet().substring(0, marker.getSnippet().indexOf("&")));
        txtadd.setText(add.trim().substring(0, add.lastIndexOf("^")));
        if (SharedPref.getGeotagImage(context).equalsIgnoreCase("0")) {
            txtImage.setVisibility(View.VISIBLE);
            if (marker.getSnippet().substring(marker.getSnippet().lastIndexOf("^") + 1).isEmpty()) {
               // txtImage.setText(R.string.toast_no_img_found);
            } else {
               // txtImage.setText(R.string.click_view_image);
            }
        } else {
            txtImage.setVisibility(View.GONE);
        }


       /* if (marker.getSnippet().substring(marker.getSnippet().lastIndexOf("^") + 1).isEmpty()) {
            btnViewImage.setImageDrawable(context.getResources().getDrawable(R.drawable.baseline_image_not));
        } else {
            Glide.with(context)
                    .load(url + "photos/" + marker.getSnippet().substring(marker.getSnippet().lastIndexOf("^") + 1))
            .into(btnViewImage);
          *//*  Picasso.get()
                    .load(url + "photos/" + marker.getSnippet().substring(marker.getSnippet().lastIndexOf("^") + 1))
                    .error(R.drawable.activity_img)
                    .into(btnViewImage);*//*
        }
        if (btnViewImage.getDrawable() == null) {
            Log.v("gggg", "000");
        } else {
            Log.v("gggg", "111");
        }
*/
     /*   if (mCommonsharedpreference.getValueFromPreference("error_tag").equalsIgnoreCase("0")) {
            btnViewImage.setImageDrawable(context.getResources().getDrawable(R.drawable.san_clm_logo));
        } else {
            Picasso.get()
                    .load(url + "photos/" + marker.getSnippet().substring(marker.getSnippet().lastIndexOf("^") + 1))
                    .into(btnViewImage);
        }*/


       /* if (marker.getSnippet().substring(marker.getSnippet().lastIndexOf("^") + 1).isEmpty()) {
            btnViewImage.setImageDrawable(context.getResources().getDrawable(R.drawable.baseline_image_not));
        } else {
            Picasso.get()
                    .load(url + "photos/" + marker.getSnippet().substring(marker.getSnippet().lastIndexOf("^") + 1))
                    .into(btnViewImage);
        }*/

        return customView;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

}
