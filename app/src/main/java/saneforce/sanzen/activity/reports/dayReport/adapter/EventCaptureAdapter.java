package saneforce.sanzen.activity.reports.dayReport.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import saneforce.sanzen.AWS.AWSBuckets;
import saneforce.sanzen.AWS.S3DownloadFiles;
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

        String imageName = EventList.get(position).getEventimg().replace("photos/","");
        String fileName  = imageName;


        if (Objects.requireNonNull(fileName).isEmpty()) {

        }else {
            String bucketName = "san.one";
            String region = "ap-south-1";
            String s3Key = "uploads/"+fileName;
            String imageUrl = "https://s3." + region + ".amazonaws.com/" + bucketName + "/" + s3Key;
            Log.d("Image URL", "Fetching image from: " + imageUrl);

            File file = new File(context.getCacheDir(),fileName);
            Log.d("TAG", "onBindViewHolder: " + file.getAbsolutePath());

            String getFile = SharedPref.getDivisionName((Activity) context);
            new AWSBuckets(context, fileName, file, 0, "", new S3DownloadFiles() {
                @Override
                public void fileDataAdd(int pos, Bitmap bitmap) {
                    if (bitmap != null) {
                        Log.d("bitmap image", "Image successfully loaded.");
                        holder.imageView.setImageBitmap(bitmap);
                        holder.imageView.setVisibility(View.VISIBLE);
                    } else {
                        Log.d("bitmap image", "Failed to load image, bitmap is null.");
                        holder.imageView.setVisibility(View.GONE);
                    }
                }
            });
        }
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
