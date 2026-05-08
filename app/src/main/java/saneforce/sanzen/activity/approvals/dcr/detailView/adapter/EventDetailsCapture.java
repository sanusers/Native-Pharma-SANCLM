package saneforce.sanzen.activity.approvals.dcr.detailView.adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import saneforce.sanzen.AWS.AWSBuckets;
import saneforce.sanzen.AWS.S3DownloadFiles;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.reports.dayReport.model.EventCaptureModelClass;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.storage.SharedPref;

public class EventDetailsCapture extends RecyclerView.Adapter<EventDetailsCapture.ViewHolder> {
    ArrayList<EventCaptureModelClass> List;
    Context context;
    CommonUtilsMethods commonUtilsMethods;
    ProgressDialog progressDialog;

    public EventDetailsCapture(ArrayList<EventCaptureModelClass> list, Context context) {
        List = list;
        this.context = context;
        commonUtilsMethods = new CommonUtilsMethods(context);
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

        holder.Imageview.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                ProgressDialog existingPB = progressDialog;
                progressDialog = CommonUtilsMethods.createProgressDialog(context);
                progressDialog.show();
                if (existingPB != null && existingPB.isShowing()) {
                    new Handler().postDelayed(existingPB::dismiss, 500);
                }
//                 ProgressDialog progressDialog = new ProgressDialog(context);
                if (SharedPref.getS3BucketNeed(context).equalsIgnoreCase("0")) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    View view1 = LayoutInflater.from(context).inflate(R.layout.eventimageitem, null);
                    dialog.setView(view1);
                    TextView tiitle = view1.findViewById(R.id.tv_txt);
                    tiitle.setVisibility(View.GONE);
                    TextView tiitle1 = view1.findViewById(R.id.tv_txt1);
                    tiitle1.setVisibility(View.GONE);
                    ImageView imageView = view1.findViewById(R.id.image);
                    AlertDialog dialog1 = dialog.create();
//             dialog1.show();
                    String imageName = List.get(position).getEventimg().replace("photos/", "");
                    String fileName = imageName;


                    if (Objects.requireNonNull(fileName).isEmpty()) {

                    } else {
                        File file = new File(context.getFilesDir(), fileName);
                        Log.d("TAG", "onBindViewHolder: " + file.getAbsolutePath());

                        /*String getFile = SharedPref.getDivisionName((Activity) context);*/
                        new AWSBuckets(context, fileName, file, 0, "", new S3DownloadFiles() {
                            @Override
                            public void fileDataAdd(int pos, Bitmap bitmap) {
                                if (bitmap != null) {
                                    Log.d("bitmap image", "Image successfully loaded.");
                                    holder.Imageview.setImageBitmap(bitmap);
                                    holder.Imageview.setVisibility(View.VISIBLE);
                                    imageView.setImageBitmap(bitmap);
                                    dialog.show();
                                    progressDialog.dismiss();
                                } else {
                                    Log.d("bitmap image", "Failed to load image, bitmap is null.");
                                    holder.Imageview.setVisibility(View.VISIBLE);
                                    progressDialog.dismiss();
                                }
                            }

                            @Override
                            public void onFailure(int pos) {
                                Log.d("bitmap image", "Failed to load image, bitmap is null.");
                                commonUtilsMethods.showToastMessage(context, context.getString(R.string.image_not_found), true);
                                holder.Imageview.setVisibility(View.VISIBLE);
                                progressDialog.dismiss();
                            }
                        });
                    }
                } else {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    View view1 = LayoutInflater.from(context).inflate(R.layout.eventimageitem, null);
                    dialog.setView(view1);
                    TextView tiitle = view1.findViewById(R.id.tv_txt);
                    tiitle.setVisibility(View.GONE);
                    TextView tiitle1 = view1.findViewById(R.id.tv_txt1);
                    tiitle1.setVisibility(View.GONE);
                    ImageView imageView = view1.findViewById(R.id.image);
                    AlertDialog dialog1 = dialog.create();
                    dialog1.show();
                    Glide.with(context).load(SharedPref.getTagImageUrl(context) + List.get(position).getEventimg()).fitCenter().into(imageView);
                    progressDialog.dismiss();
                }

//             Glide.with(context).load(SharedPref.getTagImageUrl(context) + List.get(position).getEventimg()).fitCenter().into(imageView);
//             }
            }
        });
    }


    @Override
    public int getItemCount() {
        return List.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView Imageview;
        TextView Name, Description;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Imageview = itemView.findViewById(R.id.imageView);
            Name = itemView.findViewById(R.id.tv_name);
            Description = itemView.findViewById(R.id.tv_description);


        }
    }
}
