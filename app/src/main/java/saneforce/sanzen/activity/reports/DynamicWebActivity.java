package saneforce.sanzen.activity.reports;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_MEDIA_AUDIO;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.READ_MEDIA_VIDEO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;


import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import android.webkit.JavascriptInterface;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;

public class DynamicWebActivity extends AppCompatActivity {
    WebView webView;
    TextView titleTextView;
    ImageView iv_back;
    String downloadURL;
    private ValueCallback<Uri[]> mUploadMessage;
    private ActivityResultLauncher<Intent> filePickerLauncher;
    ProgressDialog progressDialog=null;

    @SuppressLint("JavascriptInterface")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_web_reports);
        webView = findViewById(R.id.web_view);
        titleTextView = findViewById(R.id.toolbar_title);
        getWindow().getDecorView().setBackgroundColor(Color.WHITE);
        iv_back = findViewById(R.id.backArrow);
        if (progressDialog == null) {
            CommonUtilsMethods commonUtilsMethods=new CommonUtilsMethods(DynamicWebActivity.this);
            progressDialog = commonUtilsMethods.createProgressDialog(DynamicWebActivity.this);
            progressDialog.show();
        }
        else {
            progressDialog.dismiss();
        }
        String url = getIntent().getStringExtra("url");
        String title = getIntent().getStringExtra("title");
        titleTextView.setText(title);
        webView.setVerticalFadingEdgeEnabled(false);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        webView.getSettings().setAllowContentAccess(true);
        webView.getSettings().setGeolocationEnabled(true);
        webView.getSettings().setDatabaseEnabled(true);
        webView.getSettings().setGeolocationDatabasePath(getApplicationContext().getFilesDir().getPath());
        webView.addJavascriptInterface(new WebAppInterface(), "Android");
        webView.clearCache(true);
        webView.loadUrl(url);
//        webView.clearHistory();
//        webView.reload();
        webView.evaluateJavascript("navigator.geolocation.getCurrentPosition(function(position) { Android.sendData(position.coords.latitude + ', ' + position.coords.longitude); }, function(error) { console.log(error); });", null);
        if (Build.VERSION.SDK_INT >= 19) {
            webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        } else {
            webView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                progressDialog.dismiss();
                injectJavaScript();
            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                progressDialog.dismiss();
                return false;
            }
        });

        webView.setWebChromeClient(new MyWebChromeClient());

        webView.setDownloadListener((url1, userAgent, contentDisposition, mimetype, contentLength) -> {
            Log.d("WebView", "Download URL: " + url1);
            downloadURL = url1;
            if (!CheckStoragePermission()) {
                RequestStoragePermission();
            } else {
                downloadFile(downloadURL);
            }
        });

        iv_back.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                finish();
            }
        });

        filePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (mUploadMessage == null) return;
                    Uri[] results = null;
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            if (data.getClipData() != null) {
                                int count = data.getClipData().getItemCount();
                                results = new Uri[count];
                                for (int i = 0; i < count; i++) {
                                    results[i] = data.getClipData().getItemAt(i).getUri();
                                }
                            } else if (data.getData() != null) {
                                results = new Uri[]{data.getData()};
                            }
                        }
                    }
                    mUploadMessage.onReceiveValue(results);
                    mUploadMessage = null;
                });
    }

    public boolean CheckStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            int image = ContextCompat.checkSelfPermission(this, READ_MEDIA_IMAGES);
            int video = ContextCompat.checkSelfPermission(this, READ_MEDIA_VIDEO);
            int audio = ContextCompat.checkSelfPermission(this, READ_MEDIA_AUDIO);
            return image == PackageManager.PERMISSION_GRANTED && video == PackageManager.PERMISSION_GRANTED && audio == PackageManager.PERMISSION_GRANTED;
        } else {
            int Write = ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE);
            int Read = ContextCompat.checkSelfPermission(this, READ_EXTERNAL_STORAGE);
            return Write == PackageManager.PERMISSION_GRANTED && Read == PackageManager.PERMISSION_GRANTED;
        }
    }

    private void RequestStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ActivityCompat.requestPermissions(this,
                    new String[]{READ_MEDIA_IMAGES, READ_MEDIA_VIDEO, READ_MEDIA_AUDIO}, 101);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{WRITE_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE}, 101);
        }
    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(null);
            }
            mUploadMessage = filePathCallback;
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            intent.setType("*/*");
            filePickerLauncher.launch(Intent.createChooser(intent, "Choose a file"));
            return true;
        }
    }

    private void injectJavaScript() {
        webView.evaluateJavascript("javascript:(function() { var shareButton = document.getElementById('shareButton'); if (shareButton) { shareButton.addEventListener('click', function() { Android.captureAndShare(); }); } })()", null);
    }

    public class WebAppInterface {
        @JavascriptInterface
        public void captureAndShare() {
            runOnUiThread(() -> captureFullWebViewScreenshot());
        }
    }

    private void captureFullWebViewScreenshot() {
        webView.post(() -> {
            webView.scrollTo(0, 0);
            int webViewHeight = (int) (webView.getContentHeight() * webView.getScale());
            int webViewWidth = webView.getWidth();
            if (webViewHeight > 0) {
                Bitmap bitmap = Bitmap.createBitmap(webViewWidth, webViewHeight, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(bitmap);
                int scrollY = webView.getScrollY();
                webView.scrollTo(0, 0);
                webView.draw(canvas);
                webView.scrollTo(0, scrollY);
                Uri imageUri = saveBitmap(bitmap);
                if (imageUri != null) {
                    shareImage(imageUri);
                } else {
                    Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Failed to capture WebView screenshot", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private Uri saveBitmap(Bitmap bitmap) {
        String timeStamp = new SimpleDateFormat("HHmmss", Locale.ENGLISH).format(new Date());
        String imageName = "Screenshot_" + timeStamp + ".jpeg";
        File folder = new File(getExternalFilesDir(null), "EventCaptureImage");
        if (!folder.exists() && !folder.mkdirs()) return null;
        File file = new File(folder, imageName);
        try (FileOutputStream fos = new FileOutputStream(file)) {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            return FileProvider.getUriForFile(this, getApplicationContext().getPackageName() + ".provider", file);
        } catch (IOException e) {
            return null;
        }
    }

    private void shareImage(Uri imageUri) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("image/png");
        shareIntent.putExtra(Intent.EXTRA_STREAM, imageUri);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        List<ResolveInfo> resInfoList = getPackageManager().queryIntentActivities(shareIntent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            grantUriPermission(resolveInfo.activityInfo.packageName, imageUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        startActivity(Intent.createChooser(shareIntent, "Share Screenshot"));
    }

    private void downloadFile(String url) {
        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setMimeType("application/octet-stream"); // optional: customize as needed
            request.setTitle("Downloading file...");
            request.setDescription("Please wait...");
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, null, null));
            DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            if (dm != null) {
                dm.enqueue(request);
                Toast.makeText(getApplicationContext(), "Download started...", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Download failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }


}