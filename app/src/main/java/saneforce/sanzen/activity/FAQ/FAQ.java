package saneforce.sanzen.activity.FAQ;

import android.net.http.SslError;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import saneforce.sanzen.activity.approvals.ApprovalsActivity;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.databinding.ActivityFakeGpsBinding;
import saneforce.sanzen.databinding.ActivityFaqBinding;

public class FAQ extends ApprovalsActivity {
    ActivityFaqBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = saneforce.sanzen.databinding.ActivityFaqBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        WebSettings settings = binding.privacyWebview.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setDomStorageEnabled(true);
        settings.setLoadWithOverviewMode(true);
        settings.setUseWideViewPort(true);
        settings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        settings.setAllowFileAccess(true);
        settings.setAllowContentAccess(true);
        settings.setBuiltInZoomControls(true);
        settings.setDisplayZoomControls(false);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);

        binding.privacyWebview.setWebViewClient(new WebViewClient() {
//            @Override
//            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
//                handler.proceed();
//                // handler.cancel();  <-- use this in production if you want to stop loading on SSL errors
//            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                Log.e("WebView", "Error: " + error.getDescription());
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.d("WebView", "Finished loading: " + url);
            }
        });

        binding.privacyWebview.setWebChromeClient(new WebChromeClient());
        binding.privacyWebview.loadUrl("https://sandesk.zohodesk.in/portal/en/kb/support-portal/san-zen-application");

        binding.backArrow.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }

}
