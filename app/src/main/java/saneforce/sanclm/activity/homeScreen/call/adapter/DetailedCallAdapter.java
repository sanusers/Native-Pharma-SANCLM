package saneforce.sanclm.activity.homeScreen.call.adapter;

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

import saneforce.sanclm.commonclasses.CommonUtilsMethods;
import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.pojo.detailing.CallDetailingList;

public class DetailedCallAdapter extends RecyclerView.Adapter<DetailedCallAdapter.ViewHolder> {
    Context context;
    Activity activity;
    ArrayList<CallDetailingList> callDetailingLists;
    CommonUtilsMethods commonUtilsMethods;

    public DetailedCallAdapter(Activity activity, Context context, ArrayList<CallDetailingList> callDetailingLists) {
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
        holder.tv_brand_name.setText(callDetailingLists.get(position).getBrand_name());
        holder.tv_timeline.setText(callDetailingLists.get(position).getTimeline());
        holder.ratingBar.setRating(Float.parseFloat(callDetailingLists.get(position).getRating()));
        holder.tv_brand_name.setOnClickListener(view -> {
            if (holder.tv_brand_name.getText().toString().length() > 12) {
                commonUtilsMethods.displayPopupWindow(activity, context, view, callDetailingLists.get(position).getBrand_name());
            }
        });
    }

    @Override
    public int getItemCount() {
        return callDetailingLists.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
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
