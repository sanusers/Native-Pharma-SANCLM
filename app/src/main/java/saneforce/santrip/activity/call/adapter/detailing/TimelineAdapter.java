package saneforce.santrip.activity.call.adapter.detailing;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import saneforce.santrip.R;

public class TimelineAdapter extends RecyclerView.Adapter<TimelineAdapter.ViewHolder> {

    private final Context context;
    private final List<String> timelineList;

    public TimelineAdapter(Context context, List<String> timelineList) {
        this.context = context;
        this.timelineList = timelineList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_timeline, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String[] timeLine = timelineList.get(position).split("~");
        String slideName = timeLine[0];
        String startTime = timeLine[1];
        String endTime = timeLine[2];
        holder.slideName.setText(slideName);
        holder.startTime.setText(startTime);
        holder.endTime.setText(endTime);
    }

    @Override
    public int getItemCount() {
        return timelineList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView slideName, startTime, endTime;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            slideName = itemView.findViewById(R.id.slide_name);
            startTime = itemView.findViewById(R.id.start_time);
            endTime = itemView.findViewById(R.id.end_time);
        }
    }
}
