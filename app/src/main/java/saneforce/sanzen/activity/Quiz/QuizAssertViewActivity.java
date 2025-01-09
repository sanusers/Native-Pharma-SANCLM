package saneforce.sanzen.activity.Quiz;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.widget.MediaController;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;

import java.io.File;

import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.databinding.ActivityQuizAssertViewBinding;

public class QuizAssertViewActivity extends AppCompatActivity {
    private ActivityQuizAssertViewBinding binding;
    private String fileName, fileType;
    public static String BUNDLE_TAG = "FileData", FILE_NAME = "FileName", FILE_TYPE = "FileType";
    private MediaController mediaController;

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizAssertViewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mediaController = new MediaController(this);
        mediaController.setAnchorView(binding.videoView);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Bundle bundle = getIntent().getExtras();
        if(bundle != null) {
            fileName = bundle.getString(FILE_NAME);
            fileType = bundle.getString(FILE_TYPE);
        }

        setUpViews();

        binding.close.setOnClickListener(view -> {
            setResult(RESULT_OK);
            finish();
        });

    }

    private void setUpViews() {
        File file = new File(this.getExternalFilesDir(null) + "/QuizAsserts/", fileName);
        if(file.exists()) {
            if(fileType.toLowerCase().contains("image")) {
                binding.imgView.setVisibility(View.VISIBLE);
                binding.pdfView.setVisibility(View.GONE);
                binding.videoView.setVisibility(View.GONE);
                binding.webView.setVisibility(View.GONE);
                Glide.with(QuizAssertViewActivity.this).load(new File(file.getAbsolutePath())).downsample(DownsampleStrategy.FIT_CENTER).placeholder(R.drawable.baseline_cached_24).into(binding.imgView);
            }else if(fileType.toLowerCase().contains("video")) {
                binding.imgView.setVisibility(View.GONE);
                binding.pdfView.setVisibility(View.GONE);
                binding.videoView.setVisibility(View.VISIBLE);
                binding.webView.setVisibility(View.GONE);
                Uri uri = Uri.parse(file.getAbsolutePath());
                binding.videoView.setVideoURI(uri);
                binding.videoView.setMediaController(mediaController);
                binding.videoView.start();
            }else if(fileType.toLowerCase().contains("pdf")) {
                binding.imgView.setVisibility(View.GONE);
                binding.pdfView.setVisibility(View.VISIBLE);
                binding.videoView.setVisibility(View.GONE);
                binding.webView.setVisibility(View.GONE);
                loadPdf(file.getAbsolutePath());
            }else if(fileType.toLowerCase().contains("msword") || fileType.toLowerCase().contains("excel") || fileType.toLowerCase().contains("sheet") || fileType.toLowerCase().contains("ppt")) {
//                binding.imgView.setVisibility(View.GONE);
//                binding.pdfView.setVisibility(View.GONE);
//                binding.videoView.setVisibility(View.GONE);
//                binding.webView.setVisibility(View.VISIBLE);
//                binding.webView.getSettings().setBuiltInZoomControls(false);
//                binding.webView.getSettings().setDisplayZoomControls(false);
//                binding.webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
//                binding.webView.getSettings().setJavaScriptEnabled(true);
//                binding.webView.getSettings().setLoadWithOverviewMode(true);
//                binding.webView.getSettings().setUseWideViewPort(true);
//                binding.webView.getSettings().setPluginState(WebSettings.PluginState.ON);
//                binding.webView.getSettings().setLoadsImagesAutomatically(true);
//                binding.webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
//                binding.webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
//                binding.webView.getSettings().setAllowFileAccess(true);
//                binding.webView.setHorizontalScrollBarEnabled(false);
//                binding.webView.setVerticalScrollBarEnabled(false);
//                binding.webView.getSettings().setDomStorageEnabled(true);
//                binding.webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//                binding.webView.getSettings().setDatabaseEnabled(true);
//                binding.webView.setInitialScale(1);
//                binding.webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
//                try {
//                    String encodedUrl = URLEncoder.encode(file.toURI().toURL().toString(), "UTF-8");
//                    Log.d("Quiz Asserts", "file path: " + file.getAbsolutePath());
//                    Log.d("Quiz Asserts", "encoded url: " + encodedUrl);
//                    String docUrl = "https://drive.google.com/viewerng/viewer?embedded=true&url=" + encodedUrl;
//                    Log.v("Quiz Asserts", " --2222-- " + docUrl);
//                    binding.webView.loadUrl("file://" + docUrl);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
                new CommonUtilsMethods(QuizAssertViewActivity.this).showToastMessage(QuizAssertViewActivity.this, "Under Development for MS Word, Excel, PPT");
                setResult(RESULT_OK);
                finish();
            }
        }
    }

    public void loadPdf(String fileName) {
        binding.pdfView.fromFile(new File(fileName)).defaultPage(0).enableSwipe(true).swipeHorizontal(false).enableAnnotationRendering(true).scrollHandle(new DefaultScrollHandle(this)).load();
    }

}