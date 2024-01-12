package saneforce.santrip.activity.presentation;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;

import java.io.File;
import java.io.FilenameFilter;

public class SupportClass {

    public static String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
    }

    public static Bitmap pdfToBitmap(File pdfFile) {
        Bitmap bitmap = null;
        try {
            PdfRenderer renderer = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                renderer = new PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY));
                final int pageCount = renderer.getPageCount();
                if(pageCount > 0){
                    PdfRenderer.Page page = renderer.openPage(0);
                    int width = (int) (page.getWidth());
                    int height = (int) (page.getHeight());
                    bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

                    page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                    page.close();
                    renderer.close();
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return bitmap;
    }

    public static Bitmap getFileFromZip(String filePath){
        Bitmap bitmap = null;

        String path = filePath.replaceAll(".zip","");
        File file = new File(path);
        File[] files=file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                return filename.contains(".png")||filename.contains(".jpg");
            }
        });

        if (files != null && files.length > 0)
            bitmap = BitmapFactory.decodeFile(files[0].getAbsolutePath());

        return bitmap;
    }

    public static String getFileFromZip(String filePath,String fileType){

        String path = filePath.replaceAll(".zip","");
        File file = new File(path);
        File[] files=file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if (fileType.equalsIgnoreCase("image")){
                    return filename.contains(".png")||filename.contains(".jpg");
                }else if (fileType.equalsIgnoreCase("html")) {
                    return filename.contains(".html");
                }
                return false;
            }
        });

        if (files != null && files.length > 0)
            return files[0].getAbsolutePath();

        return "";
    }
}
