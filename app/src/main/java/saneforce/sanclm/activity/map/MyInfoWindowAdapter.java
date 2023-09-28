package saneforce.sanclm.activity.map;

import static saneforce.sanclm.activity.map.MapsActivity.mapsBinding;
import static saneforce.sanclm.activity.map.MapsActivity.taggingAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import saneforce.sanclm.R;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.storage.SQLiteHandler;
import saneforce.sanclm.storage.SharedPref;

public class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    Context context;
    ViewTagModel viewTagModels;
    SQLiteHandler db;
    TextView txtview;
    TextView txtadd, txtImage;
    ConstraintLayout constraint_view_img;
    CommonUtilsMethods commonUtilsMethods;
    String Drcode;
    private View customView = null;


    public MyInfoWindowAdapter(ViewTagModel mm, Context context) {
        try {
            customView = View.inflate(context, R.layout.map_info_window, null);
            this.viewTagModels = mm;
            this.context = context;
            commonUtilsMethods = new CommonUtilsMethods(context);
        } catch (Exception e) {

        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public View getInfoContents(final Marker marker) {
        db = new SQLiteHandler(context);
        txtview = customView.findViewById(R.id.txt_drName);
        txtadd = customView.findViewById(R.id.txt_address);
        txtImage = customView.findViewById(R.id.btn_view_image);
        constraint_view_img = customView.findViewById(R.id.constraint_view_img);

        String add = marker.getSnippet().substring(marker.getSnippet().lastIndexOf("&") + 1);

        txtview.setText(marker.getSnippet().substring(0, marker.getSnippet().indexOf("&")));
        //  txtadd.setText(add.trim().substring(0, add.lastIndexOf("^")));
        txtadd.setText(add.trim().substring(0, add.lastIndexOf("$")));

        String code = marker.getSnippet().substring(marker.getSnippet().lastIndexOf("$") + 1);
        code = code.trim().substring(0, code.lastIndexOf("^"));


        if (SharedPref.getGeotagImage(context).equalsIgnoreCase("0")) {
            constraint_view_img.setVisibility(View.VISIBLE);
            if (marker.getSnippet().substring(marker.getSnippet().lastIndexOf("^") + 1).isEmpty()) {
                constraint_view_img.setVisibility(View.GONE);
            } else {
                constraint_view_img.setVisibility(View.VISIBLE);
            }
        } else {
            constraint_view_img.setVisibility(View.GONE);
        }
        Log.v("getCode", "bf---" + code);

        for (int i = 0; i < MapsActivity.taggedMapListArrayList.size(); i++) {
            if (MapsActivity.taggedMapListArrayList.get(i).getCode().equalsIgnoreCase(code)) {
                Log.v("getCode", MapsActivity.taggedMapListArrayList.get(i).getCode());
                MapsActivity.taggedMapListArrayList.set(i, new TaggedMapList(MapsActivity.taggedMapListArrayList.get(i).getName(), MapsActivity.taggedMapListArrayList.get(i).getAddr(), MapsActivity.taggedMapListArrayList.get(i).getCode(), true,MapsActivity.taggedMapListArrayList.get(i).getMeters()));
                Drcode = code;
            } else {
                MapsActivity.taggedMapListArrayList.set(i, new TaggedMapList(MapsActivity.taggedMapListArrayList.get(i).getName(), MapsActivity.taggedMapListArrayList.get(i).getAddr(), MapsActivity.taggedMapListArrayList.get(i).getCode(), false,MapsActivity.taggedMapListArrayList.get(i).getMeters()));
            }
        }

        taggingAdapter = new TaggingAdapter(context.getApplicationContext(), MapsActivity.taggedMapListArrayList);
        commonUtilsMethods.recycleTestWithDivider(mapsBinding.rvList);
        mapsBinding.rvList.setAdapter(taggingAdapter);
        int position = taggingAdapter.getItemPosition(Drcode);
        if (position >= 0) {
            mapsBinding.rvList.scrollToPosition(position);
        }
        return customView;
    }

    private void scrollToPosition() {
        String eventId = "someId";
        int position = taggingAdapter.getItemPosition(eventId);
        if (position >= 0) {
            mapsBinding.rvList.scrollToPosition(position);
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

}
