package saneforce.sanclm.utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.net.URL;
import java.net.URLConnection;

import saneforce.sanclm.activity.setting.AsyncInterface;


public class DownloaderClass extends AsyncTask<Object,Object,Object> {

    private final String requestUrl,imageName,filePath;
    private Bitmap bitmap;
    public AsyncInterface asyncInterface ;


    public DownloaderClass (String requestUrl, String filepath, String imageName,AsyncInterface asyncInterface) {
        this.requestUrl = requestUrl;
        this.filePath = filepath;
        this.imageName = imageName;
        this.asyncInterface = asyncInterface;
    }

    @Override
    protected Object doInBackground (Object... objects) {
        try {
            URL url = new URL(requestUrl);
            URLConnection conn = url.openConnection();
            bitmap = BitmapFactory.decodeStream(conn.getInputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        if (!ImageStorage.checkIfImageExists(filePath, imageName)) {
            String status = ImageStorage.saveImage(bitmap, filePath, imageName);
            if (status != null && status.equalsIgnoreCase("success")){
                asyncInterface.taskCompleted(true);
            }
            Log.e("test","logo image downloaded");
        }
    }
}
