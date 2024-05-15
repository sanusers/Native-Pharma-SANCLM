package saneforce.santrip.activity.reports.dayReport.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import saneforce.santrip.R;
import saneforce.santrip.activity.reports.dayReport.model.EventCaptureModelClass;
import saneforce.santrip.storage.SharedPref;

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
