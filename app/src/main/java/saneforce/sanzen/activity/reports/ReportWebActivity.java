package saneforce.sanzen.activity.reports;

import static java.util.Collections.synchronizedSet;

import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.CookieManager;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.MimeTypeMap;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.android.material.snackbar.Snackbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.databinding.ActivityWebReportsBinding;
import saneforce.sanzen.storage.SharedPref;

public class ReportWebActivity extends AppCompatActivity {
    private static final int FILECHOOSER_RESULTCODE = 1;
    private static final String CHANNEL_ID = "image_download_channel";
    private static final String TAG = "ReportWebActivity";

    public static ProgressDialog progressDialog;
    ActivityWebReportsBinding binding;

    private String url;
    private String lastPostData = null;
//    private String lastPostDataAction = null;
    private CommonUtilsMethods commonUtilsMethods;
    private static ValueCallback<Uri[]> mUploadMessage;
    private boolean isDownloadInProgress = false;
    private static final long BLOCKER_CLICK_TIMEOUT_MS = 4000;
    private static final long BLOCKER_DOWNLOAD_TIMEOUT_MS = 150000;
    private final Runnable blockerAutoDismissRunnable = this::hideDownloadBlocker;

//    private final ConcurrentLinkedQueue<String> postDataQueue = new ConcurrentLinkedQueue<>();
//    private final Set<String> activeDownloadKeys = synchronizedSet(new HashSet<>());

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            binding.getRoot().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                            View.SYSTEM_UI_FLAG_FULLSCREEN |
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
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
        createNotificationChannel();

        binding.backArrow.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                onBackPressed();
            }
        });
        progressDialog = CommonUtilsMethods.createProgressDialog(this);
        populateWebView();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (mUploadMessage == null) return;

            Uri[] result = null;
            if (resultCode == RESULT_OK && intent != null) {
                try {
                    String dataString = intent.getDataString();
                    ClipData clipData = intent.getClipData();
                    if (clipData != null) {
                        result = new Uri[clipData.getItemCount()];
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            result[i] = clipData.getItemAt(i).getUri();
                        }
                    } else if (dataString != null) {
                        result = new Uri[]{Uri.parse(dataString)};
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;
        }
    }

    private void populateWebView() {
        url = SharedPref.getTagImageUrl(ReportWebActivity.this)
                + "MasterFiles/Dashboard_Menu.aspx"
                + "?sfcode=" + SharedPref.getSfCode(this)
                + "&cMnth=" + CommonUtilsMethods.getCurrentInstance("MM")
                + "&cYr=" + CommonUtilsMethods.getCurrentInstance("yyyy")
                + "&div_code=" + CommonUtilsMethods.removeLastComma(SharedPref.getDivisionCode(this))
                + "&sf_type=" + SharedPref.getSfType(this)
                + "&SF=" + SharedPref.getSfCode(this)
                + "&Mode=" + Constants.APP_MODE;

        Log.e(TAG, "populateWebView url: " + url);

        CookieManager.getInstance().setAcceptCookie(true);
        CookieManager.getInstance().setAcceptThirdPartyCookies(binding.webView, true);

        binding.webView.getSettings().setJavaScriptEnabled(true);
        binding.webView.getSettings().setDomStorageEnabled(true);
        binding.webView.getSettings().setAllowFileAccess(true);
        binding.webView.getSettings().setAllowContentAccess(true);
        binding.webView.getSettings().setLoadsImagesAutomatically(true);
        binding.webView.setVerticalScrollBarEnabled(true);
        binding.webView.setHorizontalScrollBarEnabled(true);
        binding.webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        binding.webView.setWebChromeClient(new MyWebChromeClient());

        binding.webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

//            @Override
//            public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
//                if (!request.getMethod().equals("POST")) return null;
//
//                String postData = lastPostData;
//                if (postData == null) return null;
//
//                String requestUrl = request.getUrl().toString();
//
//                String lastAction = lastPostDataAction;
//                if (lastAction == null || !requestUrl.equals(lastAction)) {
//                    Log.e(TAG, "shouldIntercept skipped — URL mismatch: " + requestUrl);
//                    return null;
//                }
//                lastPostData = null;
//                lastPostDataAction = null;
//
//                try {
//                    CookieManager.getInstance().flush();
//                    String cookies = CookieManager.getInstance().getCookie(requestUrl);
//
//                    Log.e(TAG, "shouldIntercept POST url: " + requestUrl);
//                    Log.e(TAG, "shouldIntercept cookies: " + cookies);
//                    Log.e(TAG, "shouldIntercept postData length: " + postData.length());
//
//                    OkHttpClient client = new OkHttpClient.Builder()
//                            .connectTimeout(30, TimeUnit.SECONDS)
//                            .readTimeout(120, TimeUnit.SECONDS)
//                            .protocols(Arrays.asList(okhttp3.Protocol.HTTP_1_1))
//                            .build();
//
//                    RequestBody body = RequestBody.create(postData, MediaType.parse("application/x-www-form-urlencoded"));
//
//                    Request.Builder requestBuilder = new Request.Builder()
//                            .url(requestUrl)
//                            .post(body)
//                            .addHeader("Content-Type", "application/x-www-form-urlencoded");
//
//                    for (Map.Entry<String, String> header : request.getRequestHeaders().entrySet()) {
//                        String key = header.getKey();
//                        if (key.equalsIgnoreCase("Content-Type")) continue;
//                        if (key.equalsIgnoreCase("Content-Length")) continue;
//                        requestBuilder.addHeader(key, header.getValue());
//                    }
//
//                    if (cookies != null) {
//                        requestBuilder.header("Cookie", cookies);
//                    }
//
//                    Response response = client.newCall(requestBuilder.build()).execute();
//                    String contentType = response.header("Content-Type", "");
//                    String contentDisposition = response.header("Content-Disposition", "");
//
//                    Log.e(TAG, "shouldIntercept response code: " + response.code());
//                    Log.e(TAG, "shouldIntercept Content-Type: " + contentType);
//                    Log.e(TAG, "shouldIntercept Content-Length: " + response.header("Content-Length"));
//                    Log.e(TAG, "shouldIntercept Content-Disposition: " + contentDisposition);
//
//                    if (contentType.contains("text/html") || !contentDisposition.toLowerCase().contains("attachment")) {
//                        response.body().close();
//                        return null;
//                    }
//
//                    if (isDownloadInProgress) {
//                        response.body().close();
//                        return null;
//                    }
//                    isDownloadInProgress = true;
//                    lastPostData = null;
//
//                    byte[] fileBytes = response.body().bytes();
//                    response.body().close();
//
//                    String fileName = extractFileName(contentDisposition, postData, contentType);
//                    Log.e(TAG, "shouldIntercept saving: " + fileName);
//
//                    final String finalContentType = contentType;
//                    final String finalFileName = fileName;
//                    final byte[] finalBytes = fileBytes;
//
//                    Executors.newSingleThreadExecutor().execute(() -> {
//                        try {
//                            runOnUiThread(() -> {
//                                binding.downloadProgress.setProgress(0);
//                                binding.downloadProgress.setVisibility(View.VISIBLE);
//                                CommonUtilsMethods.showToastMessage(ReportWebActivity.this, getString(R.string.downloading), true);
//                            });
//
//                            Uri savedUri;
//                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                                savedUri = saveFileToDownloads(finalBytes, finalFileName, finalContentType);
//                            } else {
//                                savedUri = saveFileToDownloadsLegacy(finalBytes, finalFileName);
//                            }
//
//                            final Uri finalUri = savedUri;
//                            runOnUiThread(() -> {
//                                isDownloadInProgress = false;
//                                binding.downloadProgress.setProgress(100);
//                                binding.downloadProgress.setVisibility(View.GONE);
//                                showFileDownloadNotification(finalUri, finalContentType, finalFileName);
//                            });
//
//                        } catch (Exception e) {
//                            e.printStackTrace();
//                            runOnUiThread(() -> {
//                                isDownloadInProgress = false;
//                                binding.downloadProgress.setVisibility(View.GONE);
//                                CommonUtilsMethods.showToastMessage(ReportWebActivity.this, "Download failed: " + e.getMessage(), true);
//                            });
//                        }
//                    });
//
//                    return null;
//                } catch (Exception e) {
//                    e.printStackTrace();
//                    runOnUiThread(() -> {
//                        isDownloadInProgress = false;
//                        binding.downloadProgress.setVisibility(View.GONE);
//                        CommonUtilsMethods.showToastMessage(ReportWebActivity.this, "Download failed: " + e.getMessage(), true);
//                    });
//                    return null;
//                }
//            }

            @Override
            public void onPageFinished(WebView view, String url) {
                progressDialog.dismiss();

                if (!isDownloadInProgress) {
                    hideDownloadBlocker();
                }
                isDownloadInProgress = false;
                lastPostData = null;

//                lastPostDataAction = null;
//                postDataQueue.clear();

                String js = "(function() {"
                        + "  if (window.__downloadListenerAttached) return;"
                        + "  window.__downloadListenerAttached = true;"
                        + "  document.addEventListener('click', function(e) {"
                        + "    var t = e.target;"
                        + "    while (t && t !== document.body) {"
                        + "      var tag = t.tagName ? t.tagName.toUpperCase() : '';"
                        + "      if (tag === 'A' || tag === 'INPUT' || tag === 'BUTTON') break;"
                        + "      t = t.parentElement;"
                        + "    }"
                        + "    if (!t || t === document.body) return;"
                        + "    var form = document.forms[0];"
                        + "    if (!form) return;"
                        + "    var evtTarget = '';"
                        + "    var href = t.href || t.getAttribute('href') || '';"
                        + "    var doPostBackMatch = href.match(/__doPostBack\\('([^']+)'/);"
                        + "    if (doPostBackMatch) {"
                        + "      evtTarget = doPostBackMatch[1];"
                        + "    } else {"
                        + "      evtTarget = t.name || t.id || '';"
                        + "    }"
                        + "    var params = '';"
                        + "    var inputs = form.getElementsByTagName('input');"
                        + "    for (var i = 0; i < inputs.length; i++) {"
                        + "      var el = inputs[i];"
                        + "      var type = el.type ? el.type.toLowerCase() : '';"
                        + "      if (el.name === '__EVENTTARGET' || el.name === '__EVENTARGUMENT') continue;"
                        + "      if (type === 'checkbox' || type === 'radio') {"
                        + "        if (el.checked && el.name) {"
                        + "          params += encodeURIComponent(el.name) + '=' + encodeURIComponent(el.value) + '&';"
                        + "        }"
                        + "      } else if (type !== 'submit' && type !== 'image') {"
                        + "        if (el.name) {"
                        + "          params += encodeURIComponent(el.name) + '=' + encodeURIComponent(el.value) + '&';"
                        + "        }"
                        + "      }"
                        + "    }"
                        + "    var selects = form.getElementsByTagName('select');"
                        + "    for (var j = 0; j < selects.length; j++) {"
                        + "      if (selects[j].name) {"
                        + "        params += encodeURIComponent(selects[j].name) + '=' + encodeURIComponent(selects[j].value) + '&';"
                        + "      }"
                        + "    }"
                        + "    params += '__EVENTTARGET=' + encodeURIComponent(evtTarget) + '&';"
                        + "    params += '__EVENTARGUMENT=&';"
                        + "    AndroidBlob.onCapturePostData(window.location.href, params);"
                        + "  }, true);"
                        + "})();";

                view.evaluateJavascript(js, null);
            }
        });

        binding.webView.setDownloadListener((downloadUrl, userAgent, contentDisposition, mimeType, contentLength) -> {
                    Log.e(TAG, "DownloadListener url: " + downloadUrl);
                    Log.e(TAG, "DownloadListener mimeType: " + mimeType);
                    Log.e(TAG, "DownloadListener contentLength: " + contentLength);

                    if (downloadUrl.startsWith("blob:")) {
                        fetchBlobViaJs(downloadUrl, mimeType);
                    } else if (downloadUrl.startsWith("data:image")) {
                        handleBase64Image(downloadUrl, false);
//                    } else if (isDownloadInProgress || lastPostData == null) {
//                        Log.e(TAG, "DownloadListener skipped — already handled by shouldInterceptRequest");
//                    } else {
//                        downloadUsingDownloadManager(downloadUrl, userAgent, contentDisposition, mimeType);
//                    }

//                    } else if (!isDownloadInProgress) {
//                        isDownloadInProgress = true;
//                        CookieManager.getInstance().flush();
//                        String cookies = CookieManager.getInstance().getCookie(downloadUrl);
//                        if (lastPostData != null) {
//                            downloadViaPost(downloadUrl, lastPostData, cookies);
//                        } else {
//                            downloadUsingDownloadManager(downloadUrl, userAgent, contentDisposition, mimeType);
//                        }
                    } else {
                        String postData = lastPostData;
                        lastPostData = null;

                        CookieManager.getInstance().flush();
                        String cookies = CookieManager.getInstance().getCookie(downloadUrl);

                        if (postData != null) {
                            downloadViaPost(downloadUrl, postData, cookies);
                        } else {
                            downloadUsingDownloadManager(downloadUrl, userAgent, contentDisposition, mimeType);
                            hideDownloadBlocker();
                        }
                    }
                }
        );

        binding.webView.addJavascriptInterface(
                new Object() {
                    @JavascriptInterface
                    public void onCapturePostData(String formAction, String formData) {
                        Log.e(TAG, "onCapturePostData action: " + formAction);
                        Log.e(TAG, "onCapturePostData length: " + formData.length());
                        lastPostData = formData;
//                        lastPostDataAction = formAction;
//                        showDownloadBlocker("Please wait...", BLOCKER_CLICK_TIMEOUT_MS);
                    }

                    @JavascriptInterface
                    public void onBlobData(String base64, String mimeType) {
                        runOnUiThread(() -> {
                            if (mimeType.startsWith("image/")) {
                                handleBase64Image(base64, false);
                            } else {
                                handleBase64File(base64, mimeType);
                            }
                        });
                    }

                    @JavascriptInterface
                    public void onClickDebug(String info) {
                        Log.e(TAG, "CLICK_DEBUG: " + info);
                    }

                    @JavascriptInterface
                    public void onProgress(int percent) {
                        runOnUiThread(() -> {
                            if (percent == -1) {
                                binding.downloadProgress.setVisibility(View.GONE);
                                CommonUtilsMethods.showToastMessage(ReportWebActivity.this, "Download failed", true);
                            } else {
//                                binding.downloadProgress.setVisibility(View.VISIBLE);
                                binding.downloadProgress.setProgress(percent);
                            }
                        });
                    }
                },
                "AndroidBlob"
        );

        binding.webView.loadUrl(url);
    }

    private void showDownloadBlocker(String message, long timeoutMs) {
        runOnUiThread(() -> {
            binding.downloadBlockerText.setText(message);
            binding.downloadBlockerOverlay.setVisibility(View.VISIBLE);
            binding.downloadBlockerOverlay.removeCallbacks(blockerAutoDismissRunnable);
            binding.downloadBlockerOverlay.postDelayed(blockerAutoDismissRunnable, timeoutMs);
        });
    }

    private void hideDownloadBlocker() {
        runOnUiThread(() -> {
            binding.downloadBlockerOverlay.removeCallbacks(blockerAutoDismissRunnable);
            binding.downloadBlockerOverlay.setVisibility(View.GONE);
        });
    }

    private void fetchBlobViaJs(String inputUrl, String mimeType) {
        String fetchTarget = inputUrl.startsWith("fetch_url:")
                ? inputUrl.replace("fetch_url:", "")
                : inputUrl;

        String js = "(function() {"
                + "  var xhr = new XMLHttpRequest();"
                + "  xhr.open('GET', '" + fetchTarget + "', true);"
                + "  xhr.withCredentials = true;"
                + "  xhr.responseType = 'blob';"
                + "  xhr.onprogress = function(e) {"
                + "    if (e.lengthComputable) {"
                + "      var pct = Math.round((e.loaded / e.total) * 100);"
                + "      AndroidBlob.onProgress(pct);"
                + "    }"
                + "  };"
                + "  xhr.onload = function() {"
                + "    if (xhr.status === 200) {"
                + "      var reader = new FileReader();"
                + "      reader.onloadend = function() {"
                + "        AndroidBlob.onBlobData(reader.result, '" + mimeType + "');"
                + "      };"
                + "      reader.readAsDataURL(xhr.response);"
                + "    } else {"
                + "      AndroidBlob.onProgress(-1);"
                + "    }"
                + "  };"
                + "  xhr.onerror = function() {"
                + "    AndroidBlob.onProgress(-1);"
                + "  };"
                + "  xhr.send();"
                + "})();";

        binding.webView.evaluateJavascript(js, null);
    }

    private void downloadViaPost(String postUrl, String formData, String cookies) {
        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                Log.e(TAG, "downloadViaPost url: " + postUrl);
                Log.e(TAG, "downloadViaPost cookies: " + cookies);
//                showDownloadBlocker("Downloading...");

                OkHttpClient client = new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(120, TimeUnit.SECONDS)
                        .protocols(Arrays.asList(okhttp3.Protocol.HTTP_1_1))
                        .build();

                RequestBody body = RequestBody.create(formData, MediaType.parse("application/x-www-form-urlencoded"));

                Request.Builder requestBuilder = new Request.Builder()
                        .url(postUrl)
                        .post(body)
                        .addHeader("User-Agent", System.getProperty("http.agent"))
                        .addHeader("Referer", postUrl)
                        .addHeader("Content-Type", "application/x-www-form-urlencoded");

                if (cookies != null) requestBuilder.addHeader("Cookie", cookies);

                Response response = client.newCall(requestBuilder.build()).execute();

                String contentType = response.header("Content-Type", "");
                String contentDisposition = response.header("Content-Disposition", "");
                long contentLength = response.body().contentLength();

                Log.e(TAG, "downloadViaPost response code: " + response.code());
                Log.e(TAG, "downloadViaPost Content-Type: " + contentType);
                Log.e(TAG, "downloadViaPost Content-Length: " + contentLength);
                Log.e(TAG, "downloadViaPost Content-Disposition: " + contentDisposition);

                if (contentType.contains("text/html") || !contentDisposition.toLowerCase().contains("attachment")) {
                    response.body().close();
                    hideDownloadBlocker();
                    isDownloadInProgress = false;
                    return;
                }

                isDownloadInProgress = true;
                showDownloadBlocker("Downloading...", BLOCKER_DOWNLOAD_TIMEOUT_MS);

                String fileName = extractFileName(contentDisposition, formData, contentType);
                Log.e(TAG, "Saving as: " + fileName);

//                runOnUiThread(() -> {
//                    binding.downloadProgress.setProgress(0);
////                    binding.downloadProgress.setVisibility(View.VISIBLE);
//                    CommonUtilsMethods.showToastMessage(ReportWebActivity.this, getString(R.string.downloading), true);
//                });

                Uri savedUri = streamToDownloads(response.body().byteStream(), fileName, contentType, contentLength);
                response.body().close();

                final Uri finalUri = savedUri;
                final String finalMime = contentType;
                final String finalFileName = fileName;
                runOnUiThread(() -> {
                    isDownloadInProgress = false;
//                    binding.downloadProgress.setProgress(100);
//                    binding.downloadProgress.setVisibility(View.GONE);
                    hideDownloadBlocker();
                    showFileDownloadNotification(finalUri, finalMime, finalFileName);
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    isDownloadInProgress = false;
//                    binding.downloadProgress.setVisibility(View.GONE);
                    hideDownloadBlocker();
                    CommonUtilsMethods.showToastMessage(ReportWebActivity.this, "Download failed: " + e.getMessage(), true);
                });
            }
        });
    }

    private Uri streamToDownloads(InputStream inputStream, String fileName, String mimeType, long contentLength) throws IOException {
        OutputStream outputStream;
        Uri uri;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
            values.put(MediaStore.Downloads.MIME_TYPE, mimeType);
            values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
            uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
            outputStream = getContentResolver().openOutputStream(uri);
        } else {
            File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!dir.exists()) dir.mkdirs();
            File file = new File(dir, fileName);
            uri = Uri.fromFile(file);
            outputStream = new FileOutputStream(file);
            MediaScannerConnection.scanFile(this, new String[]{file.getAbsolutePath()}, null, null);
        }

        try {
            byte[] buffer = new byte[8 * 1024];
            long downloaded = 0;
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
                downloaded += bytesRead;
                if (contentLength > 0) {
                    int progress = (int) ((downloaded * 100L) / contentLength);
//                    runOnUiThread(() -> binding.downloadProgress.setProgress(progress));
                }
            }
            outputStream.flush();
        } finally {
            outputStream.close();
            inputStream.close();
        }

        return uri;
    }

    private void handleBase64File(String dataUrl, String mimeType) {
        ProgressBar progressBar = binding.downloadProgress;
        progressBar.setProgress(0);
//        progressBar.setVisibility(View.VISIBLE);

        Executors.newSingleThreadExecutor().execute(() -> {
            try {
                String base64Data = dataUrl.contains(",")
                        ? dataUrl.substring(dataUrl.indexOf(",") + 1)
                        : dataUrl;
                byte[] decoded = Base64.decode(base64Data, Base64.DEFAULT);

                String ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
                if (ext == null) ext = "bin";
                String fileName = "download_" + System.currentTimeMillis() + "." + ext;

                Uri savedUri;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    savedUri = saveFileToDownloads(decoded, fileName, mimeType);
                } else {
                    savedUri = saveFileToDownloadsLegacy(decoded, fileName);
                }

                final Uri finalUri = savedUri;
                final String finalMime = mimeType;
                runOnUiThread(() -> {
                    progressBar.setProgress(100);
                    progressBar.setVisibility(View.GONE);
//                    showFileDownloadedSnackBar(finalUri, finalMime);
                });

            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> {
                    progressBar.setVisibility(View.GONE);
                    CommonUtilsMethods.showToastMessage(ReportWebActivity.this, "Download failed", true);
                });
            }
        });
    }

    private Uri saveFileToDownloads(byte[] data, String fileName, String mimeType) throws IOException {
        ContentValues values = new ContentValues();
        values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
        values.put(MediaStore.Downloads.MIME_TYPE, mimeType);
        values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);
        Uri uri = getContentResolver().insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
        OutputStream os = getContentResolver().openOutputStream(uri);
        os.write(data);
        os.close();
        return uri;
    }

    private Uri saveFileToDownloadsLegacy(byte[] data, String fileName) throws IOException {
        File dir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!dir.exists()) dir.mkdirs();
        File file = new File(dir, fileName);
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(data);
        fos.close();
        MediaScannerConnection.scanFile(this, new String[]{file.getAbsolutePath()}, null, null);
        return Uri.fromFile(file);
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
            CommonUtilsMethods.showToastMessage(ReportWebActivity.this, getString(R.string.image_saved), true);
            showDownloadNotification(savedUri);
            if (isShare) shareImage(savedUri);
        } catch (Exception e) {
            e.printStackTrace();
            CommonUtilsMethods.showToastMessage(ReportWebActivity.this, getString(R.string.failed_to_save_image), true);
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

    private void downloadUsingDownloadManager(String downloadUrl, String userAgent, String contentDisposition, String mimeType) {
        try {
            String fileName = URLUtil.guessFileName(downloadUrl, contentDisposition, mimeType);
            if (!fileName.contains(".")) {
                String ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType);
                if (ext != null) fileName = fileName + "." + ext;
            }

            CookieManager.getInstance().flush();
            String cookies = CookieManager.getInstance().getCookie(downloadUrl);

            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(downloadUrl));
            request.setMimeType(mimeType);
            request.addRequestHeader("User-Agent", userAgent);
            if (cookies != null) request.addRequestHeader("Cookie", cookies);
            request.addRequestHeader("Referer", this.url);
            request.setDescription("Downloading file...");
            request.setTitle(fileName);
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);

            DownloadManager dm = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            dm.enqueue(request);

            CommonUtilsMethods.showToastMessage(ReportWebActivity.this, getString(R.string.downloading), true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String extractFileName(String contentDisposition, String formData, String contentType) {
        if (contentDisposition != null && contentDisposition.contains("filename")) {
            try {
                String[] parts = contentDisposition.split(";");
                for (String part : parts) {
                    part = part.trim();
                    if (part.toLowerCase().startsWith("filename")) {
                        String name = part.substring(part.indexOf("=") + 1)
                                .trim()
                                .replace("\"", "")
                                .replace("'", "");
                        if (!name.isEmpty()) {
                            Log.e(TAG, "Filename from Content-Disposition: " + name);
                            return name;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (formData != null) {
            try {
                String[] pairs = formData.split("&");
                for (String pair : pairs) {
                    String[] kv = pair.split("=", 2);
                    if (kv.length == 2) {
                        String key = java.net.URLDecoder.decode(kv[0], "UTF-8");
                        String value = java.net.URLDecoder.decode(kv[1], "UTF-8");
                        if (key.toLowerCase().contains("file")
                                || key.toLowerCase().contains("path")
                                || key.toLowerCase().contains("doc")) {
                            if (value.contains("/") || value.contains("\\")) {
                                String name = value.contains("/")
                                        ? value.substring(value.lastIndexOf("/") + 1)
                                        : value.substring(value.lastIndexOf("\\") + 1);
                                if (!name.isEmpty()) {
                                    Log.e(TAG, "Filename from formData key '" + key + "': " + name);
                                    return name;
                                }
                            }
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String ext = MimeTypeMap.getSingleton().getExtensionFromMimeType(contentType);
        if (ext == null) ext = "bin";
        String fallback = "download_" + System.currentTimeMillis() + "." + ext;
        Log.e(TAG, "Filename fallback: " + fallback);
        return fallback;
    }

    private void showFileDownloadedSnackBar(Uri fileUri, String mimeType) {
        Snackbar.make(binding.getRoot(), "File downloaded successfully", Snackbar.LENGTH_LONG)
                .setAction("OPEN", v -> {
                    try {
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.setDataAndType(fileUri, mimeType);
                        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(intent);
                    } catch (Exception e) {
                        CommonUtilsMethods.showToastMessage(ReportWebActivity.this, "No app found to open this file", true);
                    }
                })
                .show();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Downloads", NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("File download notifications");
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }
    }

    private void showFileDownloadNotification(Uri fileUri, String mimeType, String fileName) {
        Intent openIntent = new Intent(Intent.ACTION_VIEW);
        openIntent.setDataAndType(fileUri, mimeType);
        openIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), openIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentTitle(fileName)
                .setContentText("Download complete. Tap to open.")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS) == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                notificationManager.notify((int) System.currentTimeMillis(), notification);
            }
        } else {
            notificationManager.notify((int) System.currentTimeMillis(), notification);
        }
    }

    private void showDownloadNotification(Uri uri) {
        Intent openIntent = new Intent(Intent.ACTION_VIEW);
        openIntent.setDataAndType(uri, "image/*");
        openIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, openIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentTitle("E-Card downloaded")
                .setContentText("Tap to view E-Card")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();
        NotificationManagerCompat.from(this).notify((int) System.currentTimeMillis(), notification);
    }

    private void shareImage(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("image/png");
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(intent, "Share Image"));
    }

    private class MyWebChromeClient extends WebChromeClient {
        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {
            if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(null);
                mUploadMessage = null;
            }
            mUploadMessage = filePathCallback;

            Intent chooseFile = new Intent(Intent.ACTION_GET_CONTENT);
            chooseFile.addCategory(Intent.CATEGORY_OPENABLE);
            chooseFile.setType("*/*");
            chooseFile.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            startActivityForResult(Intent.createChooser(chooseFile, "Choose a file"), FILECHOOSER_RESULTCODE);
            return true;
        }

        @Override
        public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
            new AlertDialog.Builder(view.getContext())
                    .setMessage(message)
                    .setPositiveButton(getString(R.string.ok), (d, w) -> result.confirm())
                    .setCancelable(false)
                    .show();
            return true;
        }

        @Override
        public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
            new AlertDialog.Builder(view.getContext())
                    .setMessage(message)
                    .setPositiveButton(getString(R.string.ok), (d, w) -> result.confirm())
                    .setNegativeButton(getString(R.string.cancel), (d, w) -> result.cancel())
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
                    .setPositiveButton(getString(R.string.ok), (d, w) -> result.confirm(input.getText().toString()))
                    .setNegativeButton(getString(R.string.cancel), (d, w) -> result.cancel())
                    .show();
            return true;
        }
    }
}