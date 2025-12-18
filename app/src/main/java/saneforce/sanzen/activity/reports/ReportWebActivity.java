package saneforce.sanzen.activity.reports;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.databinding.ActivityWebReportsBinding;
import saneforce.sanzen.storage.SharedPref;

public class ReportWebActivity extends AppCompatActivity {
    private final static int FILECHOOSER_RESULTCODE = 1;
    public static ProgressDialog progressDialog;
    ActivityWebReportsBinding binding;

    String url;
    CommonUtilsMethods commonUtilsMethods;
    private ValueCallback<Uri[]> mUploadMessage;

    //To Hide the bottomNavigation When popup
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            binding.getRoot().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWebReportsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
//        trustAllCert();
        commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());
        commonUtilsMethods.setUpLanguage(getApplicationContext());

        binding.backArrow.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                onBackPressed();
            }
        });
        progressDialog = CommonUtilsMethods.createProgressDialog(this);
        PopulateWebView();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == FILECHOOSER_RESULTCODE) {
            try {
                if (null == mUploadMessage || intent == null || resultCode != RESULT_OK) {
                    return;
                }
                Uri[] result = null;
                String dataString = intent.getDataString();
                ClipData clipData = intent.getClipData();
                if (clipData != null) {
                    result = new Uri[clipData.getItemCount()];
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        ClipData.Item item = clipData.getItemAt(i);
                        result[i] = item.getUri();
                    }
                }
                if (dataString != null) {
                    result = new Uri[]{Uri.parse(dataString)};
                }
                mUploadMessage.onReceiveValue(result);
                mUploadMessage = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void PopulateWebView() {
        url = SharedPref.getTagImageUrl(ReportWebActivity.this) + "MasterFiles/Dashboard_Menu.aspx" + "?sfcode=" + SharedPref.getSfCode(this) + "&cMnth=" + CommonUtilsMethods.getCurrentInstance("MM") + "&cYr=" + CommonUtilsMethods.getCurrentInstance("yyyy") + "&div_code=" + CommonUtilsMethods.removeLastComma(SharedPref.getDivisionCode(this)) + "&sf_type=" + SharedPref.getSfType(this) + "&SF=" + SharedPref.getSfCode(this) + "&Mode=" + Constants.APP_MODE;
        Log.e("URL", "PopulateWebView: " + url);
        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.getSettings().setDomStorageEnabled(true);
        binding.webView.getSettings().setAllowFileAccess(true);
        binding.webView.getSettings().setAllowContentAccess(true);
        binding.webView.getSettings().setLoadsImagesAutomatically(true);
        binding.webView.setVerticalScrollBarEnabled(true);
        binding.webView.setHorizontalScrollBarEnabled(true);
        binding.webView.setWebChromeClient(new MyWebChromeClient());
        binding.webView.loadUrl(url);
        binding.webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressDialog.dismiss();
            }

        });

        binding.webView.setDownloadListener(
                (url, userAgent, contentDisposition, mimeType, contentLength) -> {
                    if (url.startsWith("blob:")) {
                        fetchBlobViaJs(url);
                    } else if (url.startsWith("data:image")) {
                        handleBase64Image(url, false);
                    } else {
                        downloadUsingDownloadManager(url, userAgent, contentDisposition, mimeType);
                    }
                }
        );

        binding.webView.addJavascriptInterface(
                new Object() {
                    @JavascriptInterface
                    public void onBlobData(String base64) {
                        runOnUiThread(() -> handleBase64Image(base64, true));
                    }
                },
                "AndroidBlob"
        );
    }

    private void fetchBlobViaJs(String blobUrl) {
        String js = "(function() {"
                + "fetch('" + blobUrl + "')"
                + ".then(r => r.blob())"
                + ".then(b => {"
                + "  const reader = new FileReader();"
                + "  reader.onloadend = function() {"
                + "    AndroidBlob.onBlobData(reader.result);"
                + "  };"
                + "  reader.readAsDataURL(b);"
                + "});"
                + "})();";
        binding.webView.evaluateJavascript(js, null);
    }

    private void downloadUsingDownloadManager(String url, String userAgent, String contentDisposition, String mimeType) {
        try {
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
            request.setMimeType(mimeType);
            request.addRequestHeader("User-Agent", userAgent);
            request.setDescription("Downloading file...");
            request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, URLUtil.guessFileName(url, contentDisposition, mimeType));
            DownloadManager downloadManager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            downloadManager.enqueue(request);
            CommonUtilsMethods.showToastMessage(ReportWebActivity.this, getString(R.string.downloading));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void handleBase64Image(String dataUrl, boolean isShare) {
        try {
            String base64Data = dataUrl.substring(dataUrl.indexOf(",") + 1);
            byte[] imageBytes = Base64.decode(base64Data, Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
            Uri savedUri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                savedUri = saveImageToGallery(bitmap);
            } else {
                savedUri = saveImageToGalleryLegacy(bitmap);
            }
            CommonUtilsMethods.showToastMessage(ReportWebActivity.this, getString(R.string.image_saved));
//            openImage(savedUri);
            if (isShare) {
                shareImage(savedUri);
            }
        } catch (Exception e) {
            e.printStackTrace();
            CommonUtilsMethods.showToastMessage(ReportWebActivity.this, getString(R.string.failed_to_save_image));
        }
    }

    private Uri saveImageToGallery(Bitmap bitmap) throws IOException {
        String fileName = "ecard_" + System.currentTimeMillis() + ".png";
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");
        values.put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/SanZen");
        Uri uri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        OutputStream os = getContentResolver().openOutputStream(uri);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
        os.close();
        return uri;
    }

    private Uri saveImageToGalleryLegacy(Bitmap bitmap) throws IOException {
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "SanZen");
        if (!dir.exists()) dir.mkdirs();
        File file = new File(dir, "ecard_" + System.currentTimeMillis() + ".png");
        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.close();
        MediaScannerConnection.scanFile(this, new String[]{file.getAbsolutePath()}, new String[]{"image/png"}, null);
        return Uri.fromFile(file);
    }

    private void openImage(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "image/png");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent);
    }

    private void shareImage(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Share Image"));
    }

    private File saveBitmap(Bitmap bitmap) throws IOException {
        String fileName = "ecard_" + System.currentTimeMillis() + ".png";
        File directory;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            directory = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        } else {
            directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        }
        File file = new File(directory, fileName);
        FileOutputStream fos = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
        fos.flush();
        fos.close();
        return file;
    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(null);
            }

            mUploadMessage = filePathCallback;

            Intent chooseFile = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
            chooseFile.setType("*/*");
            chooseFile.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            chooseFile = Intent.createChooser(chooseFile, "Choose a file");
            ReportWebActivity.this.startActivityForResult(Intent.createChooser(chooseFile, "File Chooser"), ReportWebActivity.FILECHOOSER_RESULTCODE);
            return true;
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            new AlertDialog.Builder(view.getContext())
                    .setMessage(message)
                    .setPositiveButton(ReportWebActivity.this.getString(R.string.ok), (dialog, which) -> {
                        result.confirm();
                    })
                    .setCancelable(false)
                    .show();
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            new AlertDialog.Builder(view.getContext())
                    .setMessage(message)
                    .setPositiveButton(ReportWebActivity.this.getString(R.string.ok), (d, w) -> result.confirm())
                    .setNegativeButton(ReportWebActivity.this.getString(R.string.cancel), (d, w) -> result.cancel())
                    .show();
            return true;
        }

        @Override
        public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
            final EditText input = new EditText(view.getContext());
            input.setText(defaultValue);
            new AlertDialog.Builder(view.getContext())
                    .setMessage(message)
                    .setView(input)
                    .setPositiveButton(ReportWebActivity.this.getString(R.string.ok), (d, w) -> result.confirm(input.getText().toString()))
                    .setNegativeButton(ReportWebActivity.this.getString(R.string.cancel), (d, w) -> result.cancel())
                    .show();
            return true;
        }
    }

}
