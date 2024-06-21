package saneforce.sanzen.activity.map;

import static saneforce.sanzen.activity.map.MapsActivity.mapsBinding;
import static saneforce.sanzen.activity.map.MapsActivity.taggedMapListArrayList;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import saneforce.sanzen.R;

public class TaggingAdapter extends RecyclerView.Adapter<TaggingAdapter.ViewHolder> {
    Context context;
    ArrayList<TaggedMapList> taggedMapLists;

    public TaggingAdapter(Context context, ArrayList<TaggedMapList> taggedMapLists) {
        this.context = context;
        this.taggedMapLists = taggedMapLists;


    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_tag_map, parent, false);
        return new ViewHolder(view);
    }


    @SuppressLint({"UseCompatLoadingForDrawables", "NotifyDataSetChanged"})
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.tv_name.setText(taggedMapLists.get(position).getName());
        holder.tv_address.setText(taggedMapLists.get(position).getAddr());
        holder.tv_meters.setText(String.format("%s Meter", taggedMapLists.get(position).getMeters()));

        if (taggedMapLists.get(position).clicked) {
            holder.img_info.setImageDrawable(context.getResources().getDrawable(R.drawable.info_icon_pink));
        } else {
            holder.img_info.setImageDrawable(context.getResources().getDrawable(R.drawable.info_icon_purple));
        }

        holder.constraint_main.setOnClickListener(view -> {
            holder.img_info.setImageDrawable(context.getResources().getDrawable(R.drawable.info_icon_pink));
            LatLng latLng = new LatLng(Double.parseDouble(taggedMapLists.get(position).getLat()), Double.parseDouble(taggedMapLists.get(position).getLng()));
            //MapsActivity.mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 20.0f));
            MapsActivity.marker = MapsActivity.mMap.addMarker(new MarkerOptions().position(latLng).snippet(taggedMapLists.get(position).getName() + "&" +
                            taggedMapLists.get(position).getAddr() + "$" + taggedMapLists.get(position).getCode() + "%" + taggedMapLists.get(position).getLat() + "#" + taggedMapLists.get(position).getLng() + "^" +
                            taggedMapLists.get(position).getImageName()).
                    icon(MapsActivity.BitmapFromVector(context, R.drawable.marker_map)));

            if (MapsActivity.marker != null) {
                MapsActivity.marker.showInfoWindow();
            }
            MapsActivity.taggedMapListArrayList.set(position, new TaggedMapList(MapsActivity.taggedMapListArrayList.get(position).getName(), MapsActivity.taggedMapListArrayList.get(position).getType(), MapsActivity.taggedMapListArrayList.get(position).getAddr(), MapsActivity.taggedMapListArrayList.get(position).getCode(), true, MapsActivity.taggedMapListArrayList.get(position).getLat(), MapsActivity.taggedMapListArrayList.get(position).getLng(), MapsActivity.taggedMapListArrayList.get(position).getImageName(), MapsActivity.taggedMapListArrayList.get(position).getMeters()));
            notifyDataSetChanged();
            mapsBinding.rvList.scrollToPosition(position);
        });
    }


    @Override
    public int getItemCount() {
        return taggedMapLists.size();
    }

    public int getItemPosition(String code, String lat, String lng) {
        for (int i = 0; i < taggedMapListArrayList.size(); i++) {
            if (taggedMapListArrayList.get(i).getCode().equalsIgnoreCase(code) && taggedMapListArrayList.get(i).getLat().equalsIgnoreCase(lat) &&
                    taggedMapListArrayList.get(i).getLng().equalsIgnoreCase(lng)) {
                return i;
            }
        }
        return -1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_name, tv_address, tv_meters;
        ImageView img_info;
        ConstraintLayout constraint_main;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_name = itemView.findViewById(R.id.tv_name);
            tv_address = itemView.findViewById(R.id.tv_address);
            tv_meters = itemView.findViewById(R.id.tv_meters);
            img_info = itemView.findViewById(R.id.img_info);
            constraint_main = itemView.findViewById(R.id.constraint_list_tagged);
        }
    }
}
