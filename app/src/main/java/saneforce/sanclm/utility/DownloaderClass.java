package saneforce.sanclm.utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.net.URL;
import java.net.URLConnection;

public class DownloaderClass extends AsyncTask<Object,Object,Object> {

    private String requestUrl, imageName,filePath;
    private Bitmap bitmap;
    static boolean downloadSate = false;

    public DownloaderClass (String requestUrl,String filepath, String imageName) {
        this.requestUrl = requestUrl;
        this.filePath = filepath;
        this.imageName = imageName;
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
            Log.e("test","images download completed");
            String state = "";
            state =  ImageStorage.saveImage(bitmap,filePath,imageName);
            if (state != null && state.equalsIgnoreCase("success")){
               downloadSate = true;
           }
        }
    }
}
