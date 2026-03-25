package saneforce.sanzen.activity.presentation.playPreview;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.MediaController;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
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

import saneforce.sanzen.R;
import saneforce.sanzen.activity.presentation.SupportClass;
import saneforce.sanzen.activity.presentation.createPresentation.BrandModelClass;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.databinding.ActivityPlaySlidePreviewBinding;
import saneforce.sanzen.utility.TimeUtils;

public class PlaySlidePreviewActivity extends AppCompatActivity {
    ActivityPlaySlidePreviewBinding binding;
    PlaySlidePagerAdapter itemsPagerAdapter;
    BottomPreviewAdapter bottomPreviewAdapter;
    ArrayList<BrandModelClass.Product> arrayList = new ArrayList<>();
    Timer timer;
    boolean playBtnClicked = false;
    MediaController mediaController;
    double progress = 0;
    CommonUtilsMethods commonUtilsMethods;

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
    }

    @SuppressLint({"SetJavaScriptEnabled", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlaySlidePreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());
        commonUtilsMethods.setUpLanguage(getApplicationContext());
        initialisation();
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onPageSelected(int position) {
                if (binding.bottomLayout.getVisibility() == View.VISIBLE) {
                    bottomPreviewAdapter.notifyDataSetChanged();
                }
                switch (SupportClass.getFileExtension(arrayList.get(position).getSlideName())) {
                    case "pdf":
                    case "mp4":
                    case "avi":
                    case "zip":
                    case "htm":
                    case "html": {
                        binding.playBtn.setVisibility(View.VISIBLE);
                        break;
                    }
                    default: {
                        binding.playBtn.setVisibility(View.GONE);
                    }
                }

                progress = (100 / (double) arrayList.size()) * (position + 1);
                binding.progressBar.setProgress((int) progress);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        binding.upArrow.setOnClickListener(view -> {
            if (binding.bottomLayout.getVisibility() == View.VISIBLE) {
                binding.imgUpDown.setImageDrawable(ContextCompat.getDrawable(PlaySlidePreviewActivity.this, R.drawable.arrow_up_white));
                binding.upArrow.setVisibility(View.VISIBLE);
                binding.bottomLayout.setVisibility(View.GONE);
                binding.closeBtn.setVisibility(View.GONE);
            } else {
                binding.imgUpDown.setImageDrawable(ContextCompat.getDrawable(PlaySlidePreviewActivity.this, R.drawable.arrow_down_white));
                binding.upArrow.setVisibility(View.VISIBLE);
                binding.bottomLayout.setVisibility(View.VISIBLE);
                binding.closeBtn.setVisibility(View.GONE);
            }
            bottomPreviewAdapter.notifyDataSetChanged();
        });

        binding.upArrow.setOnClickListener(view -> {
            binding.upArrow.setVisibility(View.GONE);
            binding.bottomLayout.setVisibility(View.VISIBLE);
            bottomPreviewAdapter.notifyDataSetChanged();
        });

        binding.closeBtn.setOnClickListener(view -> {
            binding.bottomLayout.setVisibility(View.GONE);
            binding.upArrow.setVisibility(View.VISIBLE);
        });

        binding.ivPlayPauseTimer.setOnClickListener(view -> {
            if (timer != null) {
                binding.ivPlayPauseTimer.setImageResource(R.drawable.baseline_play_arrow_24);
                stopTimer();
            } else {
                binding.ivPlayPauseTimer.setImageResource(R.drawable.baseline_pause_24);
                startTimer();
            }
        });

        binding.exitBtn.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        binding.playBtn.setOnClickListener(view -> {
            if (binding.bottomLayout.getVisibility() != View.VISIBLE) {
                if (!playBtnClicked) {
                    playBtnClicked = true;
                    binding.playBtn.setImageResource(R.drawable.baseline_stop);
                    if (timer != null) {
                        timer.cancel();
                    }
                    binding.viewPager.setVisibility(View.GONE);
                    binding.upArrow.setVisibility(View.GONE);
                    binding.bottomLayout.setVisibility(View.GONE);

                    String fileName = arrayList.get(binding.viewPager.getCurrentItem()).getSlideName();
                    File file = new File(PlaySlidePreviewActivity.this.getExternalFilesDir(null) + "/Slides/", fileName);
                    if (file.exists()) {
                        String fileFormat = SupportClass.getFileExtension(fileName);
                        switch (fileFormat) {
                            case "pdf":
                                binding.pdfView.setVisibility(View.VISIBLE);
                                binding.videoView.setVisibility(View.GONE);
                                binding.webView.setVisibility(View.GONE);
                                binding.loadingView.setVisibility(View.VISIBLE);
                                binding.loadingView.startLoading();
                                loadPdf(file.getAbsolutePath());
                                break;
                            case "mp4":
                            case "avi":
                                binding.pdfView.setVisibility(View.GONE);
                                binding.videoView.setVisibility(View.VISIBLE);
                                binding.webView.setVisibility(View.GONE);
                                binding.loadingView.setVisibility(View.VISIBLE);
                                binding.loadingView.startLoading();
                                Uri uri = Uri.parse(file.getAbsolutePath());
                                binding.videoView.setVideoURI(uri);
                                binding.videoView.setMediaController(mediaController);
                                binding.videoView.setOnPreparedListener(mp -> {
                                    binding.loadingView.setVisibility(View.GONE);
                                    binding.loadingView.stopLoading();
                                    mp.start();
                                });
//                                binding.videoView.start();
                                break;
                            case "zip":
                                binding.pdfView.setVisibility(View.GONE);
                                binding.videoView.setVisibility(View.GONE);
                                binding.webView.setVisibility(View.VISIBLE);
                                binding.loadingView.setVisibility(View.VISIBLE);
                                binding.loadingView.startLoading();

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

                                String filePath = SupportClass.getFileFromZip(file.getAbsolutePath(), "html");
                                if (!filePath.isEmpty()) {
                                    binding.webView.loadUrl("file://" + filePath);
                                }
                                binding.webView.setWebViewClient(new WebViewClient() {
                                    @Override
                                    public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                        Log.v("Slides", " ---- " + url + " ---- " + view.getTitle() + " ---- " + view.getOriginalUrl());
                                        if (!url.isEmpty()) {
                                            binding.webView.loadUrl(url);
                                        }
                                        return true;
                                    }

                                    @Override
                                    public void onPageFinished(WebView view, String url) {
                                        super.onPageFinished(view, url);
                                        Log.i("webview", "onPageFinished: " + TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_22));
                                        binding.loadingView.setVisibility(View.GONE);
                                        binding.loadingView.stopLoading();
                                    }
                                });
                              break;
                        }
                    }
                    binding.playBtn.bringToFront();
                    binding.playBtn.setZ(100f);
                } else {
                    if (binding.videoView.isPlaying()) {
                        binding.videoView.stopPlayback();
                    }
                    binding.webView.loadUrl("about:blank");
                    binding.webView.clearHistory();
                    binding.webView.clearCache(false);
                    playBtnClicked = false;
                    binding.playBtn.setImageResource(R.drawable.play_icon);
                    binding.viewPager.setVisibility(View.VISIBLE);
                    binding.pdfView.setVisibility(View.GONE);
                    binding.videoView.setVisibility(View.GONE);
                    binding.webView.setVisibility(View.GONE);
                    binding.upArrow.setVisibility(View.VISIBLE);
                    binding.loadingView.setVisibility(View.GONE);
                    binding.loadingView.stopLoading();
                    binding.ivPlayPauseTimer.setImageResource(R.drawable.baseline_pause_24);
                    startTimer();
                }
            }
        });

        binding.playBtn.setOnTouchListener(new View.OnTouchListener() {
            private float dX, dY;
            private long clickStartTime;
            private static final int CLICK_THRESHOLD = 200;
            private static final int MOVE_THRESHOLD = 10;
            private float downRawX, downRawY;

            @Override
            public boolean onTouch(View view, MotionEvent event) {
                if (binding.videoView.getVisibility() == View.VISIBLE) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        view.performClick();
                    }
                    return true;
                }
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        clickStartTime = System.currentTimeMillis();
                        downRawX = event.getRawX();
                        downRawY = event.getRawY();
                        dX = view.getX() - downRawX;
                        dY = view.getY() - downRawY;
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        float moveX = event.getRawX();
                        float moveY = event.getRawY();
                        float deltaX = moveX - downRawX;
                        float deltaY = moveY - downRawY;
                        if (Math.abs(deltaX) > MOVE_THRESHOLD || Math.abs(deltaY) > MOVE_THRESHOLD) {
                            view.setX(moveX + dX);
                            view.setY(moveY + dY);
                        }
                        return true;
                    case MotionEvent.ACTION_UP:
                        long clickDuration = System.currentTimeMillis() - clickStartTime;
                        float upDeltaX = event.getRawX() - downRawX;
                        float upDeltaY = event.getRawY() - downRawY;
                        if (clickDuration < CLICK_THRESHOLD && Math.abs(upDeltaX) < MOVE_THRESHOLD && Math.abs(upDeltaY) < MOVE_THRESHOLD) {
                            view.performClick();
                            return true;
                        }
                        snapToSide(view);
                        return true;
                }
                return false;
            }
        });
    }
    private void attachButtonToBottomRight() {
        View parent = (View) binding.playBtn.getParent();
        parent.post(() -> {
            int margin = (int) getResources().getDimension(R.dimen._16sdp);
            float targetX = parent.getWidth() - binding.playBtn.getWidth() - margin;
            float targetY = parent.getHeight() - binding.playBtn.getHeight() - margin;

            binding.playBtn.animate().x(targetX).y(targetY).setDuration(200).setInterpolator(new OvershootInterpolator()).start();
        });
    }

    private void snapToSide(View view) {
        View parent = (View) view.getParent();
        int parentWidth = parent.getWidth();
        int parentHeight = parent.getHeight();
        int margin = (int) getResources().getDimension(R.dimen._16sdp);
//        float middle = parentWidth / 2f;
//        float targetX;
//        if (view.getX() + view.getWidth() / 2 >= middle) {
//            targetX = parentWidth - view.getWidth() - margin;
//        } else {
//            targetX = margin;
//        }
        float targetX = Math.max(margin, Math.min(view.getX(), parentWidth - view.getWidth() - margin));
        float targetY = Math.max(margin, Math.min(view.getY(), parentHeight - view.getHeight() - margin));
        view.animate().x(targetX).y(targetY).setDuration(200).setInterpolator(new OvershootInterpolator()).start();
    }

    public void initialisation() {
        binding.ivPlayPauseTimer.setImageResource(R.drawable.baseline_pause_24);
        startTimer();
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
                e.printStackTrace();
            }
        }
        binding.bottomLayout.setVisibility(View.GONE);
        if (arrayList.size() > 0) {
//            progress =  100 / arrayList.size() ;

            switch (SupportClass.getFileExtension(arrayList.get(0).getSlideName())) {
                case "pdf":
                case "mp4":
                case "avi":
                case "zip":
                case "htm":
                case "html": {
                    binding.playBtn.setVisibility(View.VISIBLE);
                    break;
                }
                default: {
                    binding.playBtn.setVisibility(View.GONE);
                }
            }
        }
        populateViewPagerAdapter();
        populateBottomViewAdapter();
    }

    public void populateViewPagerAdapter() {
        itemsPagerAdapter = new PlaySlidePagerAdapter(this, arrayList);
        binding.viewPager.setAdapter(itemsPagerAdapter);
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                binding.ivPlayPauseTimer.setImageResource(R.drawable.baseline_pause_24);
                startTimer();
            }

            @Override
            public void onPageSelected(int position) {
                binding.ivPlayPauseTimer.setImageResource(R.drawable.baseline_pause_24);
                startTimer();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }

    public void populateBottomViewAdapter() {
        bottomPreviewAdapter = new BottomPreviewAdapter(PlaySlidePreviewActivity.this, arrayList, binding.viewPager);
        LinearLayoutManager layoutManager = new LinearLayoutManager(PlaySlidePreviewActivity.this, LinearLayoutManager.HORIZONTAL, false);
        binding.recView.setLayoutManager(layoutManager);
        binding.recView.setAdapter(bottomPreviewAdapter);
    }

    public void startTimer() {
        if (timer != null) timer.cancel();
        timer = new Timer();
        timer.schedule(new SlideTimer(), 2000, 5000);
    }

    public void stopTimer() {
        if (timer != null) timer.cancel();
        timer = null;
    }

    public void loadPdf(String fileName) {
        binding.pdfView.fromFile(new File(fileName))
                .onRender((nbPages) -> {
                    binding.loadingView.setVisibility(View.GONE);
                    binding.loadingView.stopLoading();
                })
                .defaultPage(0)
                .enableAnnotationRendering(true)
                .scrollHandle(new DefaultScrollHandle(this))
                .enableSwipe(true)
                .swipeHorizontal(false)
                .pageSnap(true)
                .autoSpacing(false)
                .pageFling(true)
                .spacing(0)
                .load();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
    }

    public class SlideTimer extends TimerTask {
        @Override
        public void run() {
            PlaySlidePreviewActivity.this.runOnUiThread(() -> {
                if (binding.viewPager.getCurrentItem() < arrayList.size() - 1) {
                    binding.viewPager.setCurrentItem(binding.viewPager.getCurrentItem() + 1);
                } else {
                    timer.cancel();
                }
            });
        }
    }
}