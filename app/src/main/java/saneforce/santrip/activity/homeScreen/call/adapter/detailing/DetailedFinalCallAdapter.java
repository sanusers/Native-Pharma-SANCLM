package saneforce.santrip.activity.homeScreen.call.adapter.detailing;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.call.pojo.detailing.CallDetailingList;
import saneforce.santrip.commonClasses.CommonUtilsMethods;

public class DetailedFinalCallAdapter extends RecyclerView.Adapter<DetailedFinalCallAdapter.ViewHolder> {
    Context context;
    Activity activity;
    ArrayList<CallDetailingList> callDetailingLists;
    CommonUtilsMethods commonUtilsMethods;

    public DetailedFinalCallAdapter(Activity activity, Context context, ArrayList<CallDetailingList> callDetailingLists) {
        this.activity = activity;
        this.context = context;
        this.callDetailingLists = callDetailingLists;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.adapter_detailed_call, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        commonUtilsMethods = new CommonUtilsMethods(context);
        holder.tv_brand_name.setText(callDetailingLists.get(position).getBrandName());
        holder.tv_timeline.setText(callDetailingLists.get(position).getSt_end_time());
        holder.ratingBar.setRating(Float.parseFloat(String.valueOf(callDetailingLists.get(position).getRating())));
        holder.tv_brand_name.setOnClickListener(view -> commonUtilsMethods.displayPopupWindow(activity, context, view, callDetailingLists.get(position).getBrandName()));
    }

    @Override
    public int getItemCount() {
        return callDetailingLists.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_brand_name, tv_timeline;
        RatingBar ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_brand_name = itemView.findViewById(R.id.tv_brand_name);
            tv_timeline = itemView.findViewById(R.id.tv_timeline);
            ratingBar = itemView.findViewById(R.id.rating_bar);
        }
    }
}
