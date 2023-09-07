package saneforce.sanclm.utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileOutputStream;

public class ImageStorage {
    public static String saveImage (Bitmap bitmap,String filePath, String filename) {

        String stored = null;
        File folder = new File(filePath, "images");  //the dot makes this directory hidden to the user
        folder.mkdir();
        File file = new File(folder.getAbsoluteFile(), filename + ".png");
        if (file.exists())
            return null;

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
            stored = "success";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stored;
    }

    public static File getImage(String filepath,String imageName) {
        File mediaImage = null;
        try {

            File myDir = new File(filepath,imageName);
            if (!myDir.exists()){
                return null;
            }

            mediaImage = new File(myDir.getPath());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mediaImage;
    }

    public static boolean checkIfImageExists(String filePath,String imageName) {

        Bitmap b = null;
        File file = ImageStorage.getImage(filePath + "/images/", imageName );
        if (file != null){
            String path = file.getAbsolutePath();
            if (path != null)
                b = BitmapFactory.decodeFile(path);

            if (b == null || b.equals("")) {
                return false;
            }
            return true;

        }

        return false;
    }
}