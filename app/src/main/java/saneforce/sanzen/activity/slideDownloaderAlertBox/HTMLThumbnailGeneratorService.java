package saneforce.sanzen.activity.slideDownloaderAlertBox;

import android.app.Service;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;

public class HTMLThumbnailGeneratorService extends Service {
    private Handler handler;

    public HTMLThumbnailGeneratorService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        handler = new Handler(Looper.getMainLooper());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        String sourceFilePath = intent.getStringExtra("HTML PATH");
        handler.post(() -> {
            Bitmap bitmap = generateBitmap(sourceFilePath);
            sendThumbnail(bitmap);
        });

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private Bitmap generateBitmap(String sourceFilePath) {
        Bitmap bitmap = null;
        try {
            WebView webView = new WebView(getApplicationContext());
            webView.getSettings().setBuiltInZoomControls(false);
            webView.getSettings().setDisplayZoomControls(false);
            webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.getSettings().setPluginState(WebSettings.PluginState.ON);
            webView.getSettings().setLoadsImagesAutomatically(true);
            webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            webView.getSettings().setAllowFileAccess(true);
            webView.setHorizontalScrollBarEnabled(false);
            webView.setVerticalScrollBarEnabled(false);
            webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
            webView.getSettings().setDatabaseEnabled(true);
            webView.setDrawingCacheEnabled(true);
            webView.setInitialScale(1);
            webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
            webView.loadUrl("file://" + sourceFilePath);
            webView.buildDrawingCache();
            bitmap = webView.getDrawingCache();
            webView.destroyDrawingCache();
            webView.destroy();
        } catch (Exception e) {
            Log.e("Thumbnail Creation", "getBitmapFromHTML: " + e.getMessage());
        }
        Log.d("TAG", "generateBitmap: " + (bitmap != null));
        return bitmap;
    }

    private void sendThumbnail(Bitmap bitmap) {
        Intent intent = new Intent("preview_generated");
        intent.putExtra("IMAGE", bitmap);
        Log.d("TAG", "sendThumbnail: intent");
        sendBroadcast(intent);
    }
}