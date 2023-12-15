package saneforce.sanclm.activity.presentation.playPreview;


import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.MediaController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.presentation.SupportClass;
import saneforce.sanclm.activity.presentation.createPresentation.BrandModelClass;
import saneforce.sanclm.databinding.ActivityPlaySlidePreviewBinding;

public class PlaySlidePreviewActivity extends AppCompatActivity {

    ActivityPlaySlidePreviewBinding binding;
    PlaySlidePagerAdapter itemsPagerAdapter;
    BottomPreviewAdapter bottomPreviewAdapter;
    ArrayList<BrandModelClass.Product> arrayList = new ArrayList<>();
    Timer timer;
    boolean playBtnClicked = false;
    MediaController mediaController;
    double progress = 0 ;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlaySlidePreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        initialisation();
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled (int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected (int position) {
                if (binding.bottomLayout.getVisibility() == View.VISIBLE) {
                    bottomPreviewAdapter.notifyDataSetChanged();
                }
                switch (SupportClass.getFileExtension(arrayList.get(position).getFileName())){
                    case "pdf":
                    case "mp4":
                    case "zip":
                    case "html":{
                        binding.playBtn.setVisibility(View.VISIBLE);
                        break;
                    }
                    default:{
                        binding.playBtn.setVisibility(View.GONE);
                    }
                }

                progress = (100 / (double)arrayList.size()) * (position + 1);
                binding.progressBar.setProgress((int) progress);
            }

            @Override
            public void onPageScrollStateChanged (int state) {
            }
        });

        binding.upArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                binding.upArrow.setVisibility(View.GONE);
                binding.bottomLayout.setVisibility(View.VISIBLE);
                bottomPreviewAdapter.notifyDataSetChanged();
            }
        });

        binding.closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                binding.bottomLayout.setVisibility(View.GONE);
                binding.upArrow.setVisibility(View.VISIBLE);
            }
        });

        binding.exitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        binding.playBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {

                if (!playBtnClicked) {
                    playBtnClicked = true;
                    binding.playBtn.setImageResource(R.drawable.baseline_stop);
                    timer.cancel();
                    binding.viewPager.setVisibility(View.GONE);
                    binding.upArrow.setVisibility(View.GONE);
                    binding.bottomLayout.setVisibility(View.GONE);

                    String fileName = arrayList.get(binding.viewPager.getCurrentItem()).getFileName();
                    File file = new File(PlaySlidePreviewActivity.this.getExternalFilesDir(null) + "/Slides/", fileName);
                    if (file.exists()) {
                        String fileFormat = SupportClass.getFileExtension(fileName);
                        if (fileFormat.equals("pdf")){
                            binding.pdfView.setVisibility(View.VISIBLE);
                            binding.videoView.setVisibility(View.GONE);
                            binding.webView.setVisibility(View.GONE);
                            loadPdf(file.getAbsolutePath());
                        }else if (fileFormat.equals("mp4")){
                            binding.pdfView.setVisibility(View.GONE);
                            binding.videoView.setVisibility(View.VISIBLE);
                            binding.webView.setVisibility(View.GONE);
                            Uri uri = Uri.parse(file.getAbsolutePath());
                            binding.videoView.setVideoURI(uri);
                            binding.videoView.setMediaController(mediaController);
                            binding.videoView.start();
                        }else if (fileFormat.equals("zip")) {
                            binding.pdfView.setVisibility(View.GONE);
                            binding.videoView.setVisibility(View.GONE);
                            binding.webView.setVisibility(View.VISIBLE);

                            binding.webView.getSettings().setBuiltInZoomControls(false);
                            binding.webView.getSettings().setDisplayZoomControls(false);
                            binding.webView.getSettings().setMediaPlaybackRequiresUserGesture(false);
                            binding.webView.getSettings().setJavaScriptEnabled(true);
                            binding.webView.getSettings().setLoadWithOverviewMode(true);
                            binding.webView.getSettings().setUseWideViewPort(true);
                            binding.webView.getSettings().setPluginState(WebSettings.PluginState.ON);
                            binding.webView.getSettings().setLoadsImagesAutomatically(true);
                            binding.webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
                            binding.webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
                            binding.webView.getSettings().setAllowFileAccess(true);
                            binding.webView.setHorizontalScrollBarEnabled(false);
                            binding.webView.setVerticalScrollBarEnabled(false);
                            binding.webView.getSettings().setDomStorageEnabled(true);
                            binding.webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
                            binding.webView.getSettings().setDatabaseEnabled(true);
                            binding.webView.setInitialScale(1);
                            binding.webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

                            String filePath = SupportClass.getFileFromZip(file.getAbsolutePath(),"html");
                            if (!filePath.isEmpty()){
                                binding.webView.loadUrl("file://" + filePath);
                            }
                        }
                    }
                }else {
                    if (binding.videoView.isPlaying()) {
                        binding.videoView.stopPlayback();
                    }
                    playBtnClicked = false;
                    binding.playBtn.setImageResource(R.drawable.play_icon);
                    binding.viewPager.setVisibility(View.VISIBLE);
                    binding.pdfView.setVisibility(View.GONE);
                    binding.videoView.setVisibility(View.GONE);
                    binding.webView.setVisibility(View.GONE);
                    binding.upArrow.setVisibility(View.VISIBLE);
                    startTimer();
                }
            }
        });

    }

    public void initialisation () {
        timer = new Timer();
        timer.scheduleAtFixedRate(new SlideTimer(), 2000, 5000);
        mediaController = new MediaController(this);
        mediaController.setAnchorView(binding.videoView);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Bundle bundle = getIntent().getBundleExtra("bundle");
        if (bundle != null) {
            String data = bundle.getString("slideBundle");
            try {
                JSONArray jsonArray = new JSONArray(data);
                Type type = new TypeToken<ArrayList<BrandModelClass.Product>>() {
                }.getType();
                arrayList = new Gson().fromJson(String.valueOf(jsonArray), type);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
        }
        binding.bottomLayout.setVisibility(View.GONE);
        if (arrayList.size() > 0) {
//            progress =  100 / arrayList.size() ;

            switch (SupportClass.getFileExtension(arrayList.get(0).getFileName())){
                case "pdf":
                case "mp4":
                case "zip":
                case "html":{
                    binding.playBtn.setVisibility(View.VISIBLE);
                    break;
                }
                default:{
                    binding.playBtn.setVisibility(View.GONE);
                }
            }
        }
        populateViewPagerAdapter();
        populateBottomViewAdapter();
    }

    public void populateViewPagerAdapter () {
        itemsPagerAdapter = new PlaySlidePagerAdapter(this, arrayList);
        binding.viewPager.setAdapter(itemsPagerAdapter);
    }

    public void populateBottomViewAdapter () {
        bottomPreviewAdapter = new BottomPreviewAdapter(PlaySlidePreviewActivity.this, arrayList, binding.viewPager);
        LinearLayoutManager layoutManager = new LinearLayoutManager(PlaySlidePreviewActivity.this, LinearLayoutManager.HORIZONTAL, false);
        binding.recView.setLayoutManager(layoutManager);
        binding.recView.setAdapter(bottomPreviewAdapter);
    }

    public void startTimer () {
        timer = new Timer();
        timer.scheduleAtFixedRate(new SlideTimer(), 2000, 5000);
    }

    public void loadPdf (String fileName) {
        binding.pdfView.fromFile(new File(fileName))
                .defaultPage(0)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableAnnotationRendering(true)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();
    }

    @Override
    protected void onDestroy () {
        super.onDestroy();
        timer.cancel();
    }

    public class SlideTimer extends TimerTask {
        @Override
        public void run () {
            PlaySlidePreviewActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run () {
                    if (binding.viewPager.getCurrentItem() < arrayList.size() - 1) {
                        binding.viewPager.setCurrentItem(binding.viewPager.getCurrentItem() + 1);
                    }else {
                        timer.cancel();
                    }
                }
            });
        }
    }
}