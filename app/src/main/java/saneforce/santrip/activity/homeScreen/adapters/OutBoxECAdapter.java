package saneforce.santrip.activity.homeScreen.adapters;

import static saneforce.santrip.activity.homeScreen.fragment.OutboxFragment.listDates;
import static saneforce.santrip.activity.homeScreen.fragment.OutboxFragment.outBoxBinding;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.Objects;

import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.modelClass.EcModelClass;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.storage.SQLite;

public class OutBoxECAdapter extends RecyclerView.Adapter<OutBoxECAdapter.ViewHolder> {
    Context context;
    ArrayList<EcModelClass> ecModelClasses;
    OutBoxHeaderAdapter outBoxHeaderAdapter;
    CommonUtilsMethods commonUtilsMethods;
    SQLite sqLite;

    public OutBoxECAdapter(Context context, ArrayList<EcModelClass> ecModelClasses) {
        this.context = context;
        this.ecModelClasses = ecModelClasses;
        commonUtilsMethods = new CommonUtilsMethods(context);
        sqLite = new SQLite(context);
    }

    @NonNull
    @Override
    public OutBoxECAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.outbox_ec_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OutBoxECAdapter.ViewHolder holder, int position) {
        Log.v("outBoxCall", "---" + ecModelClasses.get(position).getName());
        holder.tvImageName.setText(ecModelClasses.get(position).getName());

        File imgFile = new File(ecModelClasses.get(position).getFilePath());
        if (imgFile.exists()) {
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            holder.imgView.setImageBitmap(myBitmap);
        }

        holder.imgView.setOnClickListener(v -> {
            File imgFile1 = new File(ecModelClasses.get(position).getFilePath());
            Bitmap myBitmap = BitmapFactory.decodeFile(imgFile1.getAbsolutePath());
            showImage(myBitmap);
        });

        holder.imgDel.setOnClickListener(v -> {
            File fileDelete = new File(ecModelClasses.get(position).getFilePath());
            if (fileDelete.exists()) {
                if (fileDelete.delete()) {
                    System.out.println("file Deleted :" + ecModelClasses.get(position).getFilePath());
                } else {
                    System.out.println("file not Deleted :" + ecModelClasses.get(position).getFilePath());
                }
            }
            sqLite.deleteOfflineEC(ecModelClasses.get(position).getId());
            removeAt(position);
        });
    }

    @Override
    public int getItemCount() {
        return ecModelClasses.size();
    }

    @SuppressLint("NotifyDataSetChanged")
    public void removeAt(int position) {
        ecModelClasses.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, ecModelClasses.size());
        outBoxHeaderAdapter = new OutBoxHeaderAdapter(context, listDates);
        commonUtilsMethods.recycleTestWithDivider(outBoxBinding.rvOutBoxHead);
        outBoxBinding.rvOutBoxHead.setAdapter(outBoxHeaderAdapter);
        outBoxHeaderAdapter.notifyDataSetChanged();
    }

    public void showImage(Bitmap img_view) {
        Dialog builder = new Dialog(context);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.setCancelable(true);
        Objects.requireNonNull(builder.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        ImageView imageView = new ImageView(context);
        imageView.setImageBitmap(img_view);
        builder.addContentView(imageView, new RelativeLayout.LayoutParams((int) context.getResources().getDimension(R.dimen._300sdp), (int) context.getResources().getDimension(R.dimen._300sdp)));
        builder.show();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvImageName;
        ImageView imgView, imgDel;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvImageName = itemView.findViewById(R.id.tvImageName);
            imgView = itemView.findViewById(R.id.img_view);
            imgDel = itemView.findViewById(R.id.img_del);
        }
    }
}
