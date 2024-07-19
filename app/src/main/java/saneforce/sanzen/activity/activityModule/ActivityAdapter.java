package saneforce.sanzen.activity.activityModule;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Objects;

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

                if (DynamicActivity.isEdited) {
                    Dialog dialog = new Dialog(context);
                    dialog.setContentView(R.layout.dcr_cancel_alert);
                    dialog.setCancelable(false);
                    Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                    TextView btn_yes = dialog.findViewById(R.id.btn_yes);
                    TextView alertText = dialog.findViewById(R.id.ed_alert_msg);
                    TextView btn_no = dialog.findViewById(R.id.btn_no);
                    alertText.setText("Are sure Clear Details");
                    btn_yes.setOnClickListener(view12 -> {
                        dialog.dismiss();
                        rowindex = position;
                        notifyDataSetChanged();
                        activityView.ChooseActivity(DataList.get(position));
                    });

                    btn_no.setOnClickListener(view12 -> {
                        dialog.dismiss();
                    });
                }else {
                    rowindex = position;
                    notifyDataSetChanged();
                    activityView.ChooseActivity(DataList.get(position));
                }


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
