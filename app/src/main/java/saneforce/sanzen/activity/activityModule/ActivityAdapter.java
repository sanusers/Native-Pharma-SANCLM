package saneforce.sanzen.activity.activityModule;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanzen.R;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.Viewholder> {

    Context context;
    ArrayList<ActivityModelClass>  DataList=new ArrayList<>();

    ActivityView activityView;
    int rowindex=-1;

    public ActivityAdapter(Context context, ArrayList<ActivityModelClass> dataList, ActivityView activityView) {
        this.context = context;
        this.DataList = dataList;
        this.activityView = activityView;

    }

    @NonNull
    @Override
    public ActivityAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_child_item, parent, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ActivityAdapter.Viewholder holder, int position) {
        holder.activityName.setText(DataList.get(position).getActivityName());

        holder.layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rowindex = position;
                notifyDataSetChanged();
                activityView.ChooseActivity(DataList.get(position));
            }
        });

        if (rowindex == position) {
            holder.activityName.setTextColor(context.getResources().getColor(R.color.white));
            holder.layout.setBackgroundColor(context.getResources().getColor(R.color.text_dark));
            holder.imageView.setImageResource(R.drawable.greater_than_white);
        } else {
            holder.activityName.setTextColor(context.getResources().getColor(R.color.text_dark));
            holder.layout.setBackgroundColor(Color.WHITE);
            holder.imageView.setImageResource(R.drawable.right_arrow);
        }
    }

    @Override
    public int getItemCount() {
        return DataList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        TextView activityName;
        RelativeLayout layout;

        ImageView imageView;
        public Viewholder(@NonNull View itemView) {
            super(itemView);
            activityName=itemView.findViewById(R.id.txtActivityName);
            layout=itemView.findViewById(R.id.rl_layout);
            imageView=itemView.findViewById(R.id.img_arrow_1);

        }
    }

}
