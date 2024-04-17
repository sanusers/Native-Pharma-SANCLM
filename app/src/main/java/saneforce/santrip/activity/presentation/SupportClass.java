package saneforce.santrip.activity.presentation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.pdf.PdfRenderer;
import android.media.MediaMetadataRetriever;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;

import androidx.annotation.RequiresApi;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Objects;

import saneforce.santrip.R;
import saneforce.santrip.activity.slideDownloaderAlertBox.ThumbnailTask;

public class SupportClass {

    public static String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
    }

    private static Bitmap pdfToBitmap(File pdfFile) {
        Bitmap bitmap = null;
        try {
            PdfRenderer renderer = new PdfRenderer(ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY));
            final int pageCount = renderer.getPageCount();
            if(pageCount>0) {
                PdfRenderer.Page page = renderer.openPage(0);
                int width = (int) (page.getWidth());
                int height = (int) (page.getHeight());
                bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                canvas.drawColor(Color.WHITE);
                page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
                page.close();
            }
            renderer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return bitmap;
    }

    private static Bitmap getBitmapFromZip(Context context, String filePath) {
        Bitmap bitmap = null;
        String path = filePath.replaceAll(".zip", "");
        File file = new File(path);
        File[] imgFiles = file.listFiles(((dir, filename) -> filename.contains(".png") || filename.contains(".jpg") || filename.contains(".jpeg")));
        File[] mp4Files = file.listFiles(((dir, filename) -> filename.contains(".mp4") || filename.contains(".avi")));
        File[] pdfFiles = file.listFiles(((dir, filename) -> filename.contains(".pdf")));
        File[] htmlFiles = file.listFiles(((dir, filename) -> filename.contains(".html")));

        if(imgFiles != null && imgFiles.length>0)
            bitmap = generateBitmap(context, imgFiles[0], getFileExtension(imgFiles[0].getAbsolutePath()));
        else if(mp4Files != null && mp4Files.length>0)
            bitmap = generateBitmap(context, mp4Files[0], getFileExtension(mp4Files[0].getAbsolutePath()));
        else if(pdfFiles != null && pdfFiles.length>0)
            bitmap = generateBitmap(context, pdfFiles[0], getFileExtension(pdfFiles[0].getAbsolutePath()));
        else if(htmlFiles != null && htmlFiles.length>0) {
            bitmap = generateBitmap(context, htmlFiles[0], getFileExtension(htmlFiles[0].getAbsolutePath()));
        }
        return bitmap;
    }

    public static String getFileFromZip(String filePath, String fileType) {

        String path = filePath.replaceAll(".zip", "");
        File file = new File(path);
        File[] files = file.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                if(fileType.equalsIgnoreCase("image")) {
                    return filename.contains(".png") || filename.contains(".jpg");
                }else if(fileType.equalsIgnoreCase("html")) {
                    return filename.contains(".html");
                }
                return false;
            }
        });

        if(files != null && files.length>0)
            return files[0].getAbsolutePath();

        return "";
    }

    private static Bitmap getBitmapFromHTML(Context context, String sourceFilePath) {
//        Bitmap[] bitmaps = {null};
//        try {
//            new Handler(Looper.getMainLooper()).post(new Runnable() {
//                @Override
//                public void run() {
//                    WebView webView = new WebView(context.getApplicationContext());
//                    webView.getSettings().setJavaScriptEnabled(true);
//                    webView.setWebViewClient(new WebViewClient() {
//                        @Override
//                        public void onPageFinished(WebView view, String url) {
//                            super.onPageFinished(view, url);
//                            int width = (webView.getWidth() >0) ? webView.getWidth() : 100, height = (webView.getHeight() >0) ? webView.getHeight() : 100;
//                            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
//                            Canvas canvas = new Canvas(bitmap);
//                            canvas.drawColor(Color.WHITE);
//                            webView.draw(canvas);
//                            bitmaps[0] = bitmap;
//                        }
//                    });
//                    webView.loadUrl("file://" + sourceFilePath);
//                }
//            });
//
//            if(bitmaps[0] == null) {
//                try {
//                    Thread.sleep(100);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

//        Intent intent = new Intent(context, HTMLThumbnailGeneratorService.class);
//        intent.putExtra("HTML PATH", sourceFilePath);
//        context.startService(intent);
//
//
//        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context1, Intent intent) {
//                if(intent.getAction() != null) {
//                    if(intent.getAction().equalsIgnoreCase("preview_generated")) {
//                        bitmaps[0] = intent.getParcelableExtra("IMAGE");
//                        Log.d("TAG", "onReceive: " + (bitmaps[0] != null));
//                        context.unregisterReceiver(this);
//                    }
//                }
//            }
//        };
//
//        context.registerReceiver(broadcastReceiver, new IntentFilter("preview_generated"));

//        ((Activity) context).runOnUiThread(() -> {
//            try {
//                WebView webView = new WebView(context.getApplicationContext());
//                webView.getSettings().setBuiltInZoomControls(false);
//                webView.getSettings().setDisplayZoomControls(false);
//                webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
//                webView.getSettings().setJavaScriptEnabled(true);
//                webView.getSettings().setLoadWithOverviewMode(true);
//                webView.getSettings().setUseWideViewPort(true);
//                webView.getSettings().setPluginState(WebSettings.PluginState.ON);
//                webView.getSettings().setLoadsImagesAutomatically(true);
//                webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
//                webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//                webView.getSettings().setAllowFileAccess(true);
//                webView.setHorizontalScrollBarEnabled(false);
//                webView.setVerticalScrollBarEnabled(false);
//                webView.getSettings().setDomStorageEnabled(true);
//                webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//                webView.getSettings().setDatabaseEnabled(true);
//                webView.setDrawingCacheEnabled(true);
//                webView.setInitialScale(1);
//                webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
//                webView.loadUrl("file://" + sourceFilePath);
//                webView.buildDrawingCache();
//                bitmaps[0] = webView.getDrawingCache();
//                webView.destroyDrawingCache();
//                webView.destroy();
//            } catch (Exception e) {
//                Log.e("Thumbnail Creation", "getBitmapFromHTML: " + e.getMessage());
//            }
//        });
//        Log.i("TAG", "getBitmapFromHTML: " + (bitmaps[0]!=null));

//        if(webView != null) {
//            return Bitmap.createBitmap(webView.getDrawingCache());
//        } else {
//            try {
//                webView = new WebView(context);
//                webView.loadData("<html><body bgColor=\"white\"><p>Unable to create preview!</p></body></html>", "text/html", "UTF-8");
//            } catch (Exception e) {
//                Log.e("Thumbnail Creation", "getBitmapFromHTML: " + e.getMessage());
//            }
//            if(webView != null) {
//                return Bitmap.createBitmap(webView.getDrawingCache());
//            }
//        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    public static Size getThumbSize(String path) {
        int width = 100, height = 100;
        try (final MediaMetadataRetriever dataRetriever = new MediaMetadataRetriever()) {
            dataRetriever.setDataSource(path);
            width = Integer.parseInt(Objects.requireNonNull(dataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)));
            height = Integer.parseInt(Objects.requireNonNull(dataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)));
            dataRetriever.getFrameAtIndex(0);
        } catch (Exception e) {
            Log.e("Thumbnail Creation", "getThumbSize: " + e.getMessage());
        }
        return new Size(width, height);
    }

    public static Bitmap generateBitmap(Context context, File sourceFile, String fileFormat) {
        Bitmap bitmap = null;
        String sourceFilePath = sourceFile.getAbsolutePath();
        switch (fileFormat){
            case "jpg":
            case "png":
            case "jpeg":
            case "gif":{
                bitmap = BitmapFactory.decodeFile(sourceFilePath);
                break;
            }
            case "avi":
            case "mp4":{
                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q) {
                    try {
                        bitmap = ThumbnailUtils.createVideoThumbnail(sourceFile, SupportClass.getThumbSize(sourceFilePath), null);
                    } catch (IOException e) {
                        Log.e("Thumbnail Conversion", "doInBackground: " + e.getMessage());
                    }
                }
//                if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.Q) {
//                    try (final MediaMetadataRetriever dataRetriever = new MediaMetadataRetriever()) {
//                        dataRetriever.setDataSource(sourceFilePath);
//                        bitmap = dataRetriever.getFrameAtIndex(0);
//                    } catch (Exception e) {
//                        Log.e("Thumbnail Creation", "doInBackground: " + e.getMessage());
//                    }
//                }
                if(bitmap == null) {
                    bitmap = ThumbnailUtils.createVideoThumbnail(sourceFilePath, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
                }
                break;
            }
            case "pdf":{
                bitmap = pdfToBitmap(sourceFile.getAbsoluteFile());
                break;
            }
            case "zip":{
                bitmap = getBitmapFromZip(context, sourceFilePath);
                break;
            }
            case "html":{
                bitmap = getBitmapFromHTML(context, sourceFilePath);
                break;
//                String html = "<html><body bgColor=\"white\"><p>Unable to create preview!</p></body></html>";
//                try {
//                    Document document = Jsoup.parse(sourceFile, "UTF-8");
//                    html = document.outerHtml();
//                    Log.d("Thumbnail Creation", "generateBitmap: " + html);
//                    bitmap = new Html2Bitmap.Builder(activity, WebViewContent.html(html)).build().getBitmap();
//                } catch (Exception e) {
//                    Log.e("Thumbnail Creation", "generateBitmap: " + e.getMessage());
//                }
            }
            default:
                Log.e("Thumbnail Creation", "Unsupported file format : " + fileFormat);
                break;
        }
        return bitmap;
    }

    public static void setThumbnail(Context context, String fileName, ImageView imageView) {
        String fileFormat = SupportClass.getFileExtension(fileName);
        File thumbnail = new File(context.getExternalFilesDir(null) + "/Thumbnails/", fileName.replace(fileFormat, "jpeg"));
        if(thumbnail.exists()) {
            setThumbnail(context, fileFormat, thumbnail, imageView);
        }else {
            new ThumbnailTask(context, fileName, () -> setThumbnail(context, fileFormat, thumbnail, imageView));
        }
    }

    private static void setThumbnail(Context context, String fileFormat, File thumbnail, ImageView imageView) {
        switch (fileFormat){
            case "jpg":
            case "png":
            case "jpeg":
            case "avi":
            case "mp4":
            case "pdf":
            case "gif":
            case "html":
            case "zip":{
                if(thumbnail.exists()) {
                    Glide.with(context).load(new File(thumbnail.getAbsolutePath())).downsample(DownsampleStrategy.CENTER_INSIDE).placeholder(R.drawable.no_image).into(imageView);
                }
                break;
            }
            default:
                Log.e("SET THUMBNAIL", "Not supported file format : " + fileFormat);
        }
    }

}
