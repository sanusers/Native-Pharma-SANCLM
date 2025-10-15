package saneforce.sanzen.activity.reports;

import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import androidx.appcompat.app.AppCompatActivity;

import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

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
        trustAllCert();
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

        // manejo de seleccion de archivo
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
        url = SharedPref.getTagImageUrl(ReportWebActivity.this) + "MasterFiles/Dashboard_Menu.aspx" + "?sfcode=" + SharedPref.getSfCode(this) + "&cMnth=" + CommonUtilsMethods.getCurrentInstance("MM") + "&cYr=" + CommonUtilsMethods.getCurrentInstance("yyyy") + "&div_code=" + CommonUtilsMethods.removeLastComma(SharedPref.getDivisionCode(this))+ "&sf_type=" + SharedPref.getSfType(this)+ "&SF=" + SharedPref.getSfCode(this) + "&Mode=" + Constants.APP_MODE;
        Log.e("URL", "PopulateWebView: " + url );
        binding.webView.getSettings().setJavaScriptEnabled(true);
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

            @Override
            public void onReceivedSslError(WebView view, android.webkit.SslErrorHandler handler, android.net.http.SslError error) {
                handler.proceed();
            }
        });
    }

    private class MyWebChromeClient extends WebChromeClient {

        @Override
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, FileChooserParams fileChooserParams) {


            if (mUploadMessage != null) {
                mUploadMessage.onReceiveValue(null);
            }

            mUploadMessage = filePathCallback;

            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
            i.setType("*/*");

            ReportWebActivity.this.startActivityForResult(Intent.createChooser(i, "File Chooser"), ReportWebActivity.FILECHOOSER_RESULTCODE);
            return true;
        }
    }
    public void trustAllCert(){
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new javax.net.ssl.X509TrustManager() {
                        public java.security.cert.X509Certificate[] getAcceptedIssuers() { return new java.security.cert.X509Certificate[]{}; }
                        public void checkClientTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                        public void checkServerTrusted(java.security.cert.X509Certificate[] certs, String authType) {}
                    }
            };

            javax.net.ssl.SSLContext sslContext = javax.net.ssl.SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            javax.net.ssl.HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

            // Disable hostname verification (accept all hostnames)
            javax.net.ssl.HostnameVerifier allHostsValid = (hostname, session) -> true;
            javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
