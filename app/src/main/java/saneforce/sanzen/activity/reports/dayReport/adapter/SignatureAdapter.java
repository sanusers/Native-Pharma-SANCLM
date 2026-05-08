package saneforce.sanzen.activity.reports.dayReport.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import saneforce.sanzen.AWS.AWSBucketsSign;
import saneforce.sanzen.AWS.S3DownloadFiles;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.reports.dayReport.model.SignatureModelClass;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.storage.SharedPref;

public class SignatureAdapter extends RecyclerView.Adapter<SignatureAdapter.Viewholder> {
    Context context;
    ArrayList<SignatureModelClass> SignatureData = new ArrayList<>();
    CommonUtilsMethods commonUtilsMethods;

    public SignatureAdapter(Context context, ArrayList<SignatureModelClass> signatureData) {
        this.context = context;
        SignatureData = signatureData;
        commonUtilsMethods = new CommonUtilsMethods(context);
    }

    @NonNull
    @Override
    public Viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.eventsignitem, null, false);
        return new Viewholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Viewholder holder, int position) {


        if (SharedPref.getS3BucketNeed(context).equalsIgnoreCase("0")) {
            String imageName = SignatureData.get(position).getSignImg().replace("photos/", "");
            String fileName = imageName;

            File file = new File(context.getFilesDir(), fileName);
            Log.d("TAG", "onBindViewHolder: " + file.getAbsolutePath());

            new AWSBucketsSign(context, fileName, file, 0, "", new S3DownloadFiles() {
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

                @Override
                public void onFailure(int pos) {
                    Log.d("bitmap image", "Failed to load image, bitmap is null.");
                    commonUtilsMethods.showToastMessage(context, context.getString(R.string.image_not_found), true);
                    holder.imageView.setVisibility(View.GONE);
                }
            });
        } else {
            String url = SharedPref.getTagImageUrl(context) + "signs/" + SignatureData.get(position).getSignImg();

            Log.e("Inmge", url);
            Picasso.get()
                    .load(url)
                    .into(holder.imageView);
        }
    }


    @Override
    public int getItemCount() {
        return SignatureData.size();
    }


    public class Viewholder extends RecyclerView.ViewHolder {

        ImageView imageView;

        public Viewholder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
        }
    }
}
