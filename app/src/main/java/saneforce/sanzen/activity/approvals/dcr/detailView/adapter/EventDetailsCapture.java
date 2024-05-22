package saneforce.sanzen.activity.approvals.dcr.detailView.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.reports.dayReport.model.EventCaptureModelClass;
import saneforce.sanzen.storage.SharedPref;

public class EventDetailsCapture extends RecyclerView.Adapter<EventDetailsCapture.ViewHolder> {

    ArrayList<EventCaptureModelClass>  List;
    Context context;

    public EventDetailsCapture(ArrayList<EventCaptureModelClass> list, Context context) {
        List = list;
        this.context = context;
    }

    @NonNull
    @Override
    public EventDetailsCapture.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.event_detail_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventDetailsCapture.ViewHolder holder, int position) {
         holder.Name.setText(List.get(position).getTitle());
         holder.Description.setText(List.get(position).getRemarks());

         holder.Imageview.setOnClickListener(view -> {

             AlertDialog.Builder dialog = new AlertDialog.Builder(context);
             View view1  = LayoutInflater.from(context).inflate(R.layout.eventimageitem, null);
             dialog.setView(view1);
             TextView tiitle=view1.findViewById(R.id.tv_txt);
             tiitle.setVisibility(View.GONE);
             TextView tiitle1=view1.findViewById(R.id.tv_txt1);
             tiitle1.setVisibility(View.GONE);
             ImageView imageView=view1.findViewById(R.id.image);
             AlertDialog dialog1=dialog.create();
             dialog1.show();
             Glide.with(context).load(SharedPref.getTagImageUrl(context) + List.get(position).getEventimg()).fitCenter().into(imageView);
         });


    }

    @Override
    public int getItemCount() {
        return List.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView Imageview;
        TextView Name,Description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Imageview=itemView.findViewById(R.id.imageView);
            Name=itemView.findViewById(R.id.tv_name);
            Description=itemView.findViewById(R.id.tv_description);


        }
    }
}
