package saneforce.sanclm.activity.slideDownloaderAlertBox;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import saneforce.sanclm.activity.homeScreen.HomeDashBoard;

public class DownloadTask {
    private static final String TAG = "DownloadTask";
    private Activity activity;
    private String downloadUrl = "";
    private String downloadFileName = "";

    Context context;
    String progressBar_value, downloadMsg;


    SlideModelClass SlideValue;
    RecyclerView recyclerView;
    Slide_adapter adapter;
    Dialog dialog;

    String moveFlag, downloadingStatus;

    public DownloadTask(Activity activity, String downloadUrl, String filename, String progressBar_value, String downloadingStatus, String downloadMsg, SlideModelClass slideValue, Slide_adapter adapter, RecyclerView recyclerView, Dialog dialog, String moveFlag){
        this.activity = activity;
        this.downloadUrl = downloadUrl;
        this.downloadFileName = filename;
        this.progressBar_value = progressBar_value;
        this.downloadingStatus = downloadingStatus;
        this.downloadMsg = downloadMsg;
        this.SlideValue = slideValue;
        this.adapter = adapter;
        this.recyclerView = recyclerView;
        this.dialog = dialog;
        this.moveFlag = moveFlag;
        this.context=activity.getApplicationContext();
        new DownloadingTask().execute();
    }

    private class DownloadingTask extends AsyncTask<Void, Integer, Boolean> {
        File file = null;
        File outputFile = null;
        int totalSize;
        int downloadedSize = 0;
        @Override
        protected Boolean doInBackground(Void... arg0) {
            try {
                URL url = new URL(downloadUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();

                if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                    Log.e(TAG, "Server returned HTTP " + connection.getResponseCode() + " " + connection.getResponseMessage());
                    return false;
                }

                if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                    file = new File(activity.getExternalFilesDir(null)+ "/Slides/");
                } else {
                    return false;
                }

                if (!file.exists()) {
                    if (!file.mkdirs()) {
                        Log.e(TAG, "Directory Creation Failed.");
                        return false;
                    }
                }

                outputFile = new File(file, downloadFileName);

                if (outputFile.exists()) {
                    if (outputFile.delete()) {
                        Log.e(TAG, "Old file deleted.");
                    } else {
                        Log.e(TAG, "Failed to delete old file.");
                    }
                }

                if (!outputFile.createNewFile()) {
                    Log.e(TAG, "File Creation Failed.");
                    return false;
                }

                FileOutputStream fos = new FileOutputStream(outputFile);
                InputStream is = connection.getInputStream();
                totalSize = connection.getContentLength();

                byte[] buffer = new byte[1024];
                int len1;


                while ((len1 = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len1);
                    downloadedSize += len1;
                    publishProgress((int) (downloadedSize * 100 / totalSize));
                }

                fos.close();
                is.close();

                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "Download Error Exception " + e.getMessage());
                return false;
            }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            int progress = values[0];
            String progressText = String.format("%.1f MB of %.1f MB", downloadedSize / (1024.0 * 1024), totalSize / (1024.0 * 1024));
            SlideValue.setDownloadSizeStatus(progressText);
            SlideValue.setProgressValue(String.valueOf(progress));
            SlideValue.setDownloadStatus("1");
            adapter.notifyDataSetChanged();

        }

        @Override
        protected void onPostExecute(Boolean isDownloadSuccessful) {
            super.onPostExecute(isDownloadSuccessful);

            if (isDownloadSuccessful) {
                SlideValue.setDownloadSizeStatus("Download completed");
                SlideValue.setProgressValue(String.valueOf(100));
                SlideValue.setDownloadStatus("1");
                SlideDownloaderAlertBox.downloading_count++;
                SlideDownloaderAlertBox.dialogDismissCount++;
                SlideDownloaderAlertBox.txt_downloadCount.setText(String.valueOf(SlideDownloaderAlertBox.downloading_count)+"/"+String.valueOf(adapter.getItemCount()));
                recyclerView.getLayoutManager().scrollToPosition(SlideDownloaderAlertBox.downloading_count);
                adapter.notifyDataSetChanged();
                if(SlideDownloaderAlertBox.dialogDismissCount ==adapter.getItemCount()){
                    dialog.dismiss();
                    if(moveFlag.equalsIgnoreCase("1")){
                        Intent intent = new Intent(context, HomeDashBoard.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        activity.finish();
                    }
                }

            } else {
                SlideValue.setProgressValue(String.valueOf(78));
                SlideDownloaderAlertBox.txt_downloadCount.setText(String.valueOf(SlideDownloaderAlertBox.downloading_count)+"/"+ String.valueOf(adapter.getItemCount()));
                SlideValue.setDownloadSizeStatus("Download failed");
                SlideValue.setDownloadStatus("2");
                SlideDownloaderAlertBox.dialogDismissCount++;
                adapter.notifyDataSetChanged();

                 if(SlideDownloaderAlertBox.dialogDismissCount ==adapter.getItemCount()){
                     dialog.dismiss();
                     if(moveFlag.equalsIgnoreCase("1")){
                         if (moveFlag.equalsIgnoreCase("1")) {
                             Intent intent = new Intent(context, HomeDashBoard.class);
                             intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                             context.startActivity(intent);
                             activity.finish();
                         }
                     }
                 }
            }

        }
    }
}
