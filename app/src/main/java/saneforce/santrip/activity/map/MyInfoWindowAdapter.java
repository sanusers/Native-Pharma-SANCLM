package saneforce.santrip.activity.map;

import static saneforce.santrip.activity.map.MapsActivity.GeoTagImageNeed;
import static saneforce.santrip.activity.map.MapsActivity.mapsBinding;
import static saneforce.santrip.activity.map.MapsActivity.taggedMapListArrayList;
import static saneforce.santrip.activity.map.MapsActivity.taggingAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.Objects;

import saneforce.santrip.R;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.storage.SQLiteHandler;

public class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    Context context;
    ViewTagModel viewTagModels;
    SQLiteHandler db;
    TextView txtView;
    TextView txtAdd, txtImage;
    ImageView img_tag;
    ConstraintLayout constraint_view_img;
    CommonUtilsMethods commonUtilsMethods;
    String DrCode, Lat, Lng, lat, lng, code;
    private View customView = null;


    public MyInfoWindowAdapter(ViewTagModel mm, Context context) {
        try {
            customView = View.inflate(context, R.layout.map_info_window, null);
            this.viewTagModels = mm;
            this.context = context;
            commonUtilsMethods = new CommonUtilsMethods(context);
        } catch (Exception ignored) {

        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View getInfoContents(final Marker marker) {
        db = new SQLiteHandler(context);
        img_tag = customView.findViewById(R.id.img_tag);
        txtView = customView.findViewById(R.id.txt_drName);
        txtAdd = customView.findViewById(R.id.txt_address);
        txtImage = customView.findViewById(R.id.btn_view_image);
        constraint_view_img = customView.findViewById(R.id.constraint_view_img);

        txtView.setText(Objects.requireNonNull(marker.getSnippet()).substring(0, marker.getSnippet().indexOf("&")));
        String add = marker.getSnippet().substring(marker.getSnippet().lastIndexOf("&") + 1);
        txtAdd.setText(add.trim().substring(0, add.lastIndexOf("$")));

        code = marker.getSnippet().substring(marker.getSnippet().lastIndexOf("$") + 1);
        code = code.trim().substring(0, code.lastIndexOf("%"));

        lat = marker.getSnippet().substring(marker.getSnippet().lastIndexOf("%") + 1);
        lat = lat.trim().substring(0, lat.lastIndexOf("#"));

        lng = marker.getSnippet().substring(marker.getSnippet().lastIndexOf("#") + 1);
        lng = lng.trim().substring(0, lng.lastIndexOf("^"));

        if (GeoTagImageNeed.equalsIgnoreCase("0")) {
            constraint_view_img.setVisibility(View.VISIBLE);
            if (marker.getSnippet().substring(marker.getSnippet().lastIndexOf("^") + 1).isEmpty()) {
                constraint_view_img.setVisibility(View.GONE);
            } else {
                constraint_view_img.setVisibility(View.VISIBLE);
            }
        } else {
            constraint_view_img.setVisibility(View.GONE);
        }

        for (int i = 0; i < MapsActivity.taggedMapListArrayList.size(); i++) {
            if (MapsActivity.taggedMapListArrayList.get(i).getCode().equalsIgnoreCase(code) && MapsActivity.taggedMapListArrayList.get(i).getLat().equalsIgnoreCase(lat) && MapsActivity.taggedMapListArrayList.get(i).getLng().equalsIgnoreCase(lng)) {
                Log.v("getCode", "true---" + MapsActivity.taggedMapListArrayList.get(i).getCode() + "---" + taggedMapListArrayList.get(i).getLat() + "----" + taggedMapListArrayList.get(i).getLng());
                MapsActivity.taggedMapListArrayList.set(i, new TaggedMapList(MapsActivity.taggedMapListArrayList.get(i).getName(), MapsActivity.taggedMapListArrayList.get(i).getType(), MapsActivity.taggedMapListArrayList.get(i).getAddr(), MapsActivity.taggedMapListArrayList.get(i).getCode(), true, MapsActivity.taggedMapListArrayList.get(i).getLat(), MapsActivity.taggedMapListArrayList.get(i).getLng(), MapsActivity.taggedMapListArrayList.get(i).getImageName(), MapsActivity.taggedMapListArrayList.get(i).getMeters()));
                DrCode = code;
                Lat = MapsActivity.taggedMapListArrayList.get(i).getLat();
                Lng = MapsActivity.taggedMapListArrayList.get(i).getLng();
                if (taggedMapListArrayList.get(i).getType().equalsIgnoreCase("1")) {
                    img_tag.setImageDrawable(context.getResources().getDrawable(R.drawable.map_dr_img));
                } else if (taggedMapListArrayList.get(i).getType().equalsIgnoreCase("2")) {
                    img_tag.setImageDrawable(context.getResources().getDrawable(R.drawable.map_chemist_img));
                } else if (taggedMapListArrayList.get(i).getType().equalsIgnoreCase("3")) {
                    img_tag.setImageDrawable(context.getResources().getDrawable(R.drawable.map_stockist_img));
                } else if (taggedMapListArrayList.get(i).getType().equalsIgnoreCase("4")) {
                    img_tag.setImageDrawable(context.getResources().getDrawable(R.drawable.map_unlistdr_img));
                }
            } else {
                MapsActivity.taggedMapListArrayList.set(i, new TaggedMapList(MapsActivity.taggedMapListArrayList.get(i).getName(), MapsActivity.taggedMapListArrayList.get(i).getType(), MapsActivity.taggedMapListArrayList.get(i).getAddr(), MapsActivity.taggedMapListArrayList.get(i).getCode(), false, MapsActivity.taggedMapListArrayList.get(i).getLat(), MapsActivity.taggedMapListArrayList.get(i).getLng(), MapsActivity.taggedMapListArrayList.get(i).getImageName(), MapsActivity.taggedMapListArrayList.get(i).getMeters()));
            }
        }

        taggingAdapter = new TaggingAdapter(context.getApplicationContext(), MapsActivity.taggedMapListArrayList);
        commonUtilsMethods.recycleTestWithDivider(mapsBinding.rvList);
        mapsBinding.rvList.setAdapter(taggingAdapter);
        int position = taggingAdapter.getItemPosition(DrCode, Lat, Lng);
        if (position >= 0) {
            mapsBinding.rvList.scrollToPosition(position);
        }
        return customView;
    }

    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        return null;
    }

}
