package saneforce.santrip.activity.slideDownloaderAlertBox;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import saneforce.santrip.activity.presentation.SupportClass;

public class ThumbnailTask {

    private final Context context;
    private final String fileName;
    private final TaskCallback taskCallback;

    public ThumbnailTask(Context context, String fileName, TaskCallback taskCallback) {
        this.fileName = fileName;
        this.context = context;
        this.taskCallback = taskCallback;
        new ThumbnailCreatingTask().execute();
    }

    private class ThumbnailCreatingTask extends AsyncTask<Void, Void, Boolean> {

        @Override
        protected Boolean doInBackground(Void... voids) {
            String fileFormat = SupportClass.getFileExtension(fileName);
            File sourceFile = new File(context.getExternalFilesDir(null) + "/Slides/", fileName);
            File thumbnailStorage;
            if(Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
                thumbnailStorage = new File(context.getExternalFilesDir(null) + "/Thumbnails/");
            }else {
                return false;
            }
            if(!thumbnailStorage.exists()) {
                if(!thumbnailStorage.mkdirs()) {
                    Log.e("Thumbnail Conversion", "Directory Creation Failed.");
                    return false;
                }
            }
            File destinationFile = new File(thumbnailStorage, fileName.replace(fileFormat, "jpeg"));
            String destinationFilePath = destinationFile.getAbsolutePath();
            if(sourceFile.exists()) {
                Bitmap bitmap = SupportClass.generateBitmap(context, sourceFile, fileFormat);
                if(bitmap != null) {
                    try {
                        if(!destinationFile.createNewFile()) {
                            Log.e("Thumbnail Conversion", "Destination File Creation Failed.");
                            return false;
                        }
                        FileOutputStream fileOutputStream = new FileOutputStream(destinationFile);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, fileOutputStream);
                        fileOutputStream.close();
                        return true;
                    } catch (IOException e) {
                        Log.e("Thumbnail Conversion", Objects.requireNonNull(e.getMessage()));
                    }
                }else Log.e("Thumbnail Creation", "Bitmap not generated");
            }
            return false;
        }

        @Override
        protected void onPostExecute(Boolean isCompleted) {
            super.onPostExecute(isCompleted);
            Log.d("Thumbnail Conversion", fileName + " returned: " + isCompleted);
            //if(taskCallback != null) taskCallback.onTaskCompleted();
        }


    }

}
