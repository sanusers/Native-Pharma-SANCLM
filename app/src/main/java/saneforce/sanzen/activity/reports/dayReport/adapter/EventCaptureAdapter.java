package saneforce.sanzen.activity.reports.dayReport.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.reports.dayReport.model.EventCaptureModelClass;
import saneforce.sanzen.storage.SharedPref;

public class EventCaptureAdapter extends RecyclerView.Adapter<EventCaptureAdapter.Viewholder> {

    Context context;
    ArrayList<EventCaptureModelClass> EventList = new ArrayList<>();

    public EventCaptureAdapter(Context context, ArrayList<EventCaptureModelClass> EventList) {
        this.context = context;
        this.EventList = EventList;
    }

    @NonNull
    @Override
    public EventCaptureAdapter.Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.eventimageitem,null,false);
        return new EventCaptureAdapter.Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventCaptureAdapter.Viewholder holder, int position) {

        holder.ImageTittle.setText(EventList.get(position).getTitle());
        holder.Remarks.setText(EventList.get(position).getRemarks());

        String url = SharedPref.getTagImageUrl(context) +EventList.get(position).getEventimg();

        Log.e("Inmge",url);
        Picasso.get()
                .load(url)
                .into(holder.imageView);

    }

    @Override
    public int getItemCount() {
        return EventList.size();
    }

    public class Viewholder extends RecyclerView.ViewHolder {

        ImageView imageView;
        TextView ImageTittle,Remarks;
        public Viewholder(@NonNull View itemView) {
            super(itemView);

            imageView=itemView.findViewById(R.id.image);
            ImageTittle=itemView.findViewById(R.id.tv_tittle);
            Remarks=itemView.findViewById(R.id.tv_remarks);
        }
    }
}
