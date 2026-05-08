package saneforce.sanzen.activity.Quiz.AssertDownloadAlert;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import java.util.ArrayList;
import java.util.List;

import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.activity.Quiz.QuizActivity;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.roomdatabase.QuizAssertsTable.QuizAssertsDataTable;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class AssertDownloadAdapter extends RecyclerView.Adapter<AssertDownloadAdapter.ListDataViewHolder> {
    Activity activity;
    CommonUtilsMethods commonUtilsMethods;
    private List<QuizAssertsDataTable> list = new ArrayList<>();
    RoomDB roomDB;

    public AssertDownloadAdapter(Activity activity) {
        this.activity = activity;
        commonUtilsMethods = new CommonUtilsMethods(activity);
        roomDB = RoomDB.getDatabase(activity);
    }

    @NonNull
    @Override
    public AssertDownloadAdapter.ListDataViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_slide_item, parent, false);
        return new AssertDownloadAdapter.ListDataViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AssertDownloadAdapter.ListDataViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.setIsRecyclable(false);
        // 0- failure,1-New, 2-Processing, 3- Success
        holder.txt_image_name.setText(list.get(position).getName());
        if(list.get(position).getDownloadingStatus().equalsIgnoreCase("3")) {
            holder.progressBar.setProgress(Integer.parseInt(list.get(position).getProgress()));
            holder.text_download_size.setText("Downloading Completed");
        }else if(list.get(position).getDownloadingStatus().equalsIgnoreCase("2")) {
            holder.text_download_size.setText(list.get(position).getAssertSize());
            holder.progressBar.setProgress(Integer.parseInt(list.get(position).getProgress()));

        }else if(list.get(position).getDownloadingStatus().equalsIgnoreCase("1")) {
            holder.text_download_size.setText("");
            holder.progressBar.setProgress(0);
        }else {
            holder.progressBar.setProgress(Integer.parseInt(list.get(position).getProgress()));
            holder.text_download_size.setText("Downloading Failed");
            holder.progressBar.setProgress(0);
        }

        if(list.get(position).getDownloadingStatus().equalsIgnoreCase("0")) {
            int redColor = Color.RED;
            ColorStateList colorStateList = ColorStateList.valueOf(redColor);
            holder.progressBar.setProgressTintList(colorStateList);

        }else {
            int greencolor = activity.getResources().getColor(R.color.Green_45);
            ColorStateList colorStateList = ColorStateList.valueOf(greencolor);
            holder.progressBar.setProgressTintList(colorStateList);

        }

        if(!SharedPref.getSlideDowloadingStatus(activity)) {
            holder.reload_img.setVisibility(View.GONE);
        }else {
            if(roomDB.slidesDao().getInProcessCount() != 0) {
                holder.reload_img.setVisibility(View.GONE);
            }else {
                holder.reload_img.setVisibility(View.VISIBLE);
            }
        }
    }

    public void setQuizAsserts(List<QuizAssertsDataTable> slides) {
        list.clear();
        this.list = slides;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public class ListDataViewHolder extends RecyclerView.ViewHolder {

        TextView txt_image_name, text_download_size;
        ImageView reload_img;
        public ProgressBar progressBar;

        RelativeLayout rl_title_layout;

        public ListDataViewHolder(@NonNull View itemView) {
            super(itemView);

            txt_image_name = itemView.findViewById(R.id.txt_imagename);
            progressBar = itemView.findViewById(R.id.img_download_progress);
            text_download_size = itemView.findViewById(R.id.txt_download_size);
            reload_img = itemView.findViewById(R.id.reload_img);
            rl_title_layout = itemView.findViewById(R.id.rl_calender_syn);


            reload_img.setOnClickListener(new SafeClickListener() {
                @Override
                public void onSafeClick(View view) {
                    int position = getAdapterPosition();
                    if (UtilityClass.isNetworkAvailable(activity)) {
                        QuizActivity.isSingleAssertDownloadingStatus = true;
                        text_download_size.setText("Downloading");
                        String url = "https://" + SharedPref.getBaseUrl(activity) + "/" + SharedPref.getOptionFilesUrl(activity) + list.get(position).getName();
                        Log.e("DownloadingAPI", url);
                        Data inputData = new Data.Builder()
                                .putString("Flag", "2")
                                .putString("file_url", url)
                                .putString("Assert_name", list.get(position).getName())
                                .putString("FilePosition", list.get(position).getListAssertPosition())
                                .build();

                        OneTimeWorkRequest fileDownloadRequest = new OneTimeWorkRequest.Builder(AssertDownloadWorker.class)
                                .setInputData(inputData)
                                .build();
                        WorkManager workManager = WorkManager.getInstance(activity);
                        workManager.enqueue(fileDownloadRequest);
                    } else {
                        commonUtilsMethods.showToastMessage(activity, activity.getString(R.string.no_network), true);
                    }
                }
            });
        }
    }

}
