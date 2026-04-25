package saneforce.sanzen.activity.call.adapter.detailing;

import static saneforce.sanzen.activity.call.DCRCallActivity.arrayStore;
import static saneforce.sanzen.activity.call.adapter.detailing.PlaySlideDetailedAdapter.mandatoryProductList;
import static saneforce.sanzen.activity.call.adapter.detailing.PlaySlideDetailedAdapter.mandatoryProductList;
import static saneforce.sanzen.activity.call.adapter.detailing.PlaySlideDetailedAdapter.slideScribble;
import static saneforce.sanzen.activity.previewPresentation.PreviewActivity.SelectedPosPlay;
import static saneforce.sanzen.activity.previewPresentation.fragment.BrandMatrix.SlideBrandMatrixList;
import static saneforce.sanzen.activity.previewPresentation.fragment.CustomizedPresentationFragment.SlideCustomList;
import static saneforce.sanzen.activity.previewPresentation.fragment.HomeBrands.SlideHomeBrandList;
import static saneforce.sanzen.activity.previewPresentation.fragment.MyPresentation.SlideCustomizedList;
import static saneforce.sanzen.activity.previewPresentation.fragment.Speciality.SlideSpecialityList;
import static saneforce.sanzen.activity.previewPresentation.fragment.Therapist.SlideTherapistList;
import static saneforce.sanzen.activity.previewPresentation.fragment.WelcomePresentation.SlideWelcomeList;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.graphics.pdf.PdfRenderer;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.OvershootInterpolator;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.pojo.detailing.StoreImageTypeUrl;
import saneforce.sanzen.activity.presentation.SupportClass;
import saneforce.sanzen.activity.presentation.createPresentation.BrandModelClass;
import saneforce.sanzen.activity.previewPresentation.fragment.CustomPresentationFragment;
import saneforce.sanzen.commonClasses.CommonSharedPreference;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.databinding.ActivityPlaySlidePreviewDetailingBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class PlaySlideDetailing extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    public static ActivityPlaySlidePreviewDetailingBinding binding;
    @SuppressLint("StaticFieldLeak")
    public static PlaySlideDetailedAdapter itemsPagerAdapter;
    @SuppressLint("StaticFieldLeak")
    public static BottomPreviewDetailedAdapter bottomPreviewDetailedAdapter;
    public static ArrayList<BrandModelClass.Product> arrayList = new ArrayList<>();
    public static ArrayList<String> headingData = new ArrayList<>();
    @SuppressLint("StaticFieldLeak")
    public static Context context;
    public static String SpecialityCodePlay, MappedBrandsPlay, MappedSlidesPlay;
    BottomLayoutHeadAdapter bottomPreviewDetailedHeadAdapter;
    boolean playBtnClicked = false;
    MediaController mediaController;
    double progress = 0;
    int SelectedPos = 0;
    int scribblePos;
    int val = 0;
    CommonSharedPreference mCommonSharedPreference;
    Dialog dialogPopUp;
    String defaultTime = "00:00:00";

    public static void populateViewPagerAdapterNew(ArrayList<BrandModelClass.Product> productsList) {
        itemsPagerAdapter = new PlaySlideDetailedAdapter((PlaySlideDetailing) context, productsList, mandatoryProductList);
        binding.viewPager.setAdapter(itemsPagerAdapter);
        itemsPagerAdapter.onPageChanged(binding.viewPager.getCurrentItem());
        if (SharedPref.getSlideAutoPlay(context).equalsIgnoreCase("1")) {
            binding.playBtn.setVisibility(View.GONE);
        } else {
            binding.playBtn.setVisibility(View.VISIBLE);
        }
        itemsPagerAdapter.autoPlaySlide(0, 1);
    }

    public static void populateBottomViewAdapterNew(ArrayList<BrandModelClass.Product> productsList) {
        bottomPreviewDetailedAdapter = new BottomPreviewDetailedAdapter(context, productsList, binding.viewPager);
        arrayList = productsList;
        LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        binding.recView.setLayoutManager(layoutManager);
        binding.recView.setAdapter(bottomPreviewDetailedAdapter);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
    }

    //To Hide the bottomNavigation When popup
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            binding.getRoot().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    public String findingEndTime(int k) {
        if (checkForLastSlide(k)) {
            return defaultTime;
        } else {
            int j = k + 1;
            return mCommonSharedPreference.getValueFromPreferenceFeed("timeVal" + j);
        }
    }

    public boolean checkForLastSlide(int k) {
        return k == val - 1;
    }

    public int checkForProduct(String slidename) {
        for (int i = 0; i < arrayStore.size(); i++) {
            if (arrayStore.get(i).getSlideNam().equals(slidename)) {
                return i;
            }
        }
        return -1;
    }

    @SuppressLint({"NotifyDataSetChanged", "ClickableViewAccessibility"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlaySlidePreviewDetailingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        mCommonSharedPreference = new CommonSharedPreference(this);
        context = this;
        initialisation();

        binding.rightArrow.setOnClickListener(view -> {
            DialogPopUp();
        });

        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                itemsPagerAdapter.onPageChanged(position);
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onPageSelected(int position) {
                if (binding.bottomLayout.getVisibility() == View.VISIBLE) {
                    bottomPreviewDetailedAdapter.notifyDataSetChanged();
                }
                switch (SupportClass.getFileExtension(arrayList.get(position).getSlideName())) {
                    case "pdf":
                    case "mp4":
                    case "avi":
                    case "zip":
                    case "htm":
                    case "html": {
                        if (SharedPref.getSlideAutoPlay(context).equalsIgnoreCase("1")) {
                            binding.playBtn.setVisibility(View.GONE);
                        } else {
                            binding.playBtn.setVisibility(View.VISIBLE);
                        }
                        break;
                    }
                    default: {
                        binding.playBtn.setVisibility(View.GONE);
                    }
                }

                progress = (100 / (double) arrayList.size()) * (position + 1);
                binding.progressBar.setProgress((int) progress);
                itemsPagerAdapter.autoPlaySlide(position, 1);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        binding.upArrow.setOnClickListener(view -> {
            if (binding.bottomLayout.getVisibility() == View.VISIBLE) {
                binding.imgUpDown.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.arrow_up_white));
                binding.upArrow.setVisibility(View.VISIBLE);
                binding.bottomLayout.setVisibility(View.GONE);
                binding.closeBtn.setVisibility(View.GONE);
            } else {
                binding.imgUpDown.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.arrow_down_white));
                binding.upArrow.setVisibility(View.VISIBLE);
                binding.bottomLayout.setVisibility(View.VISIBLE);
                binding.closeBtn.setVisibility(View.GONE);
            }
            bottomPreviewDetailedAdapter.notifyDataSetChanged();
        });

        binding.closeBtn.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        binding.exitBtn.setOnClickListener(view -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

//        binding.playBtn.setOnClickListener(new SafeClickListener() {
//            @Override
//            public void onSafeClick(View view) {
        binding.playBtn.setOnClickListener(view -> {
            if (!playBtnClicked) {
                playBtnClicked = true;
                binding.playBtn.setImageResource(R.drawable.baseline_stop);
                binding.viewPager.setVisibility(View.GONE);
                binding.upArrow.setVisibility(View.GONE);
                binding.bottomLayout.setVisibility(View.GONE);

                String fileName = arrayList.get(binding.viewPager.getCurrentItem()).getSlideName();
                File file = new File(PlaySlideDetailing.this.getExternalFilesDir(null) + "/Slides/", fileName);
                if (file.exists()) {
                    String fileFormat = SupportClass.getFileExtension(fileName);
                    switch (fileFormat) {
//                        case "pdf":
//                            binding.pdfView.setVisibility(View.VISIBLE);
//                            binding.videoView.setVisibility(View.GONE);
//                            binding.webView.setVisibility(View.GONE);
//                            binding.loadingView.setVisibility(View.VISIBLE);
//                            binding.loadingView.startLoading();
//                            loadPdf(file.getAbsolutePath());
//                            break;
                        case "pdf":
                            setPdfThumbnail(file.getAbsolutePath()); // ✅先 thumbnail
                            binding.videoView.setVisibility(View.GONE);
                            binding.webView.setVisibility(View.GONE);
                            loadPdf(file.getAbsolutePath());         // ✅ background load
                            break;
                        case "mp4":
                        case "avi":
                            loadVideo(file);
//                            binding.pdfView.setVisibility(View.GONE);
//                            binding.videoView.setVisibility(View.VISIBLE);
//                            binding.webView.setVisibility(View.GONE);
////                            binding.loadingView.setVisibility(View.VISIBLE);
////                            binding.loadingView.startLoading();
//                            Uri uri = Uri.parse(file.getAbsolutePath());
//                            binding.videoView.setVideoURI(uri);
//                            binding.videoView.setMediaController(mediaController);
//                            binding.videoView.setOnPreparedListener(mp -> {
////                                binding.loadingView.setVisibility(View.GONE);
////                                binding.loadingView.stopLoading();
//                                mp.start();
//                            });
//                            binding.videoView.setZOrderOnTop(false);
//                            binding.videoView.setZOrderMediaOverlay(false);
//                            binding.videoView.start();
                            break;
                        case "zip":
                            binding.pdfView.setVisibility(View.GONE);
                            binding.videoView.setVisibility(View.GONE);
                            binding.webView.setVisibility(View.VISIBLE);
//                            binding.loadingView.setVisibility(View.VISIBLE);
//                            binding.loadingView.startLoading();

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
                            Log.v("Slides", " --2222-- " + filePath);
                            if (!filePath.isEmpty()) {
                                binding.webView.loadUrl("file://" + filePath);
                            }

                           /* binding.webView.setOnTouchListener((v, event) -> {
                                String filename = "";
                                WebView.HitTestResult hr = ((WebView) v).getHitTestResult();
                                Log.v("Slides", "getExtra = " + hr.getExtra() + "\t\t Type=" + hr.getType());
                                // Log.v("Slides", "getExtra = "+ hr.getExtra());
                                if (hr.getExtra() != null) {
                                    filename = hr.getExtra().substring(hr.getExtra().lastIndexOf("/") + 1);
                                    if (!filename.contains(".html"))
                                        storingSlide.add(new LoadBitmap("", CommonUtilsMethods.getCurrentInstance("HH:mm:ss"), hr.getType() + 100221, CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd"), filename, "", hr.getExtra(), presentBrandName, presentBrandCode));
                                }
                                Log.v("Slides", "---- " + filename + "----" + presentBrandName);
                                return false;
                            });*/

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
                try {
                    binding.webView.loadUrl("about:blank");
                    binding.webView.clearHistory();
                    binding.webView.clearCache(false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                playBtnClicked = false;
                binding.playBtn.setImageResource(R.drawable.play_icon);
                binding.viewPager.setVisibility(View.VISIBLE);
                binding.pdfView.setVisibility(View.GONE);
                binding.videoView.setVisibility(View.GONE);
                binding.loadingView.setVisibility(View.GONE);
                binding.loadingView.stopLoading();
                binding.webView.setVisibility(View.GONE);
                binding.upArrow.setVisibility(View.VISIBLE);
            }
//            }
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

    private void DialogPopUp() {
        dialogPopUp = new Dialog(context);
        dialogPopUp.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialogPopUp.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialogPopUp.setContentView(R.layout.detailing_right_popup);
        dialogPopUp.setCanceledOnTouchOutside(true);
        Window window = dialogPopUp.getWindow();
        WindowManager.LayoutParams wlp = window.getAttributes();
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);


        RelativeLayout rl_like = dialogPopUp.findViewById(R.id.rl_hand_up);
        RelativeLayout rl_dislike = dialogPopUp.findViewById(R.id.rl_hand_down);
        RelativeLayout rl_comments = dialogPopUp.findViewById(R.id.rl_comments);
        RelativeLayout rl_share = dialogPopUp.findViewById(R.id.rl_share);
        RelativeLayout rl_paint = dialogPopUp.findViewById(R.id.rl_paint);
        RelativeLayout rl_stop = dialogPopUp.findViewById(R.id.rl_stop);

        rl_like.setEnabled(false);
        rl_dislike.setEnabled(false);
        rl_comments.setEnabled(false);
        rl_share.setEnabled(false);
        rl_paint.setEnabled(false);
        rl_stop.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                if (PlaySlideDetailedAdapter.preVal) {
                    int timecount = 0;

                    for (int i = 0; i < PlaySlideDetailedAdapter.storingSlide.size(); i++) {
                        int ll = 0;
                        if (i != 0) {
                            ll = i - 1;
                            if (PlaySlideDetailedAdapter.storingSlide.get(ll).getIndexVal() == PlaySlideDetailedAdapter.storingSlide.get(i).getIndexVal()) {
                                PlaySlideDetailedAdapter.storingSlide.remove(ll);
                            }
                        }
                    }

                    for (int i = 0; i < PlaySlideDetailedAdapter.storingSlide.size(); i++) {
                        int ll = 0;
                        if (i != 0) ll = i - 1;
                        if (i == 0 || PlaySlideDetailedAdapter.storingSlide.get(ll).getIndexVal() != PlaySlideDetailedAdapter.storingSlide.get(i).getIndexVal()) {
                            mCommonSharedPreference.setValueToPreferenceFeed("timeVal" + timecount, PlaySlideDetailedAdapter.storingSlide.get(i).getTiming());
                            mCommonSharedPreference.setValueToPreferenceFeed("dateVal" + timecount, PlaySlideDetailedAdapter.storingSlide.get(i).getDateVal());
                            mCommonSharedPreference.setValueToPreferenceFeed("brd_nam" + timecount, PlaySlideDetailedAdapter.storingSlide.get(i).getBrandName());
                            mCommonSharedPreference.setValueToPreferenceFeed("brd_code" + timecount, PlaySlideDetailedAdapter.storingSlide.get(i).getBrandCode());
                            mCommonSharedPreference.setValueToPreferenceFeed("slide_nam" + timecount, PlaySlideDetailedAdapter.storingSlide.get(i).getSlideName());
                            mCommonSharedPreference.setValueToPreferenceFeed("slide_id" + timecount, PlaySlideDetailedAdapter.storingSlide.get(i).getSlideID());
                            mCommonSharedPreference.setValueToPreferenceFeed("slide_typ" + timecount, PlaySlideDetailedAdapter.storingSlide.get(i).getSlideType());
                            mCommonSharedPreference.setValueToPreferenceFeed("slide_url" + timecount, PlaySlideDetailedAdapter.storingSlide.get(i).getSlideUrl());
                            mCommonSharedPreference.setValueToPreferenceFeed("product_code" + timecount, PlaySlideDetailedAdapter.storingSlide.get(i).getProductCode());
                            mCommonSharedPreference.setValueToPreferenceFeed("timeCount", ++timecount);
                        }
                    }

                    val = mCommonSharedPreference.getValueFromPreferenceFeed("timeCount", 0);
                    Log.v("slideData", String.valueOf(val));
                    for (int i = 0; i < val; i++) {
                        String timevalue = mCommonSharedPreference.getValueFromPreferenceFeed("timeVal" + i);
                        String SlideName = mCommonSharedPreference.getValueFromPreferenceFeed("slide_nam" + i);
                        String BrandName = mCommonSharedPreference.getValueFromPreferenceFeed("brd_nam" + i);
                        String BrandCode = mCommonSharedPreference.getValueFromPreferenceFeed("brd_code" + i);
                        String slideID = mCommonSharedPreference.getValueFromPreferenceFeed("slide_id" + i);
                        String slidetyp = mCommonSharedPreference.getValueFromPreferenceFeed("slide_typ" + i);
                        String slideur = mCommonSharedPreference.getValueFromPreferenceFeed("slide_url" + i);
                        String productCode = mCommonSharedPreference.getValueFromPreferenceFeed("product_code" + i);

                        String eTime;
                        if (arrayStore.contains(new StoreImageTypeUrl(SlideName))) {
                            eTime = findingEndTime(i);
                            int index = checkForProduct(SlideName);
                            StoreImageTypeUrl mmm = arrayStore.get(index);

                            try {
                                JSONArray jj = new JSONArray(mmm.getRemTime());
                                JSONArray jk = new JSONArray();
                                JSONObject js = null;
                                for (int k = 0; k < jj.length(); k++) {
                                    js = jj.getJSONObject(k);
                                    jk.put(js);
                                }
                                js = new JSONObject();
                                js.put("sT", timevalue);
                                js.put("eT", eTime);
                                jk.put(js);
                                mmm.setRemTime(jk.toString());
                            } catch (Exception ignored) {
                            }
                        } else if (!SlideName.isEmpty()) {
                            eTime = findingEndTime(i);
                            JSONObject jsonObject = new JSONObject();
                            JSONArray jsonArray = new JSONArray();
                            try {
                                jsonObject.put("sT", timevalue);
                                jsonObject.put("eT", eTime);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            jsonArray.put(jsonObject);
                            Log.v("slideData", "----" + BrandName + "----" + SlideName);
                            boolean isAvailableScrib = false;
                            scribblePos = 0;
                            for (int j = 0; j < slideScribble.size(); j++) {
                                if (slideScribble.get(j).getSlideNam().equalsIgnoreCase(SlideName)) {
                                    isAvailableScrib = true;
                                    scribblePos = j;
                                    break;
                                }
                            }
                            if (isAvailableScrib) {
                                arrayStore.add(new StoreImageTypeUrl(slideScribble.get(scribblePos).getScribble(), slideID, SlideName, slidetyp, slideur, "0", slideScribble.get(scribblePos).getSlideComments(), jsonArray.toString(), BrandName, BrandCode, productCode, false));
                            } else {
                                arrayStore.add(new StoreImageTypeUrl("", slideID, SlideName, slidetyp, slideur, "0", "", jsonArray.toString(), BrandName, BrandCode, productCode, false));
                            }
                        }
                    }
                }
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
        params.setMargins(0, 0, 0, 0);
        wlp.gravity = Gravity.CENTER | Gravity.END;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        dialogPopUp.show();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (itemsPagerAdapter != null) {
            itemsPagerAdapter.resetTimer();
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        if (Build.VERSION.SDK_INT >= 33) {
//            if (ContextCompat.checkSelfPermission(this, READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(PlaySlideDetailing.this, READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(PlaySlideDetailing.this, READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {
//                CommonUtilsMethods.RequestPermissions(this, new String[]{READ_MEDIA_IMAGES, READ_MEDIA_AUDIO, READ_MEDIA_VIDEO}, false);
//            }
//        } else {
//            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//                CommonUtilsMethods.RequestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, false);
//            }
//        }
    }

    public void initialisation() {

        mediaController = new MediaController(this);
        mediaController.setAnchorView(binding.videoView);

        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());

        Bundle bundle = getIntent().getBundleExtra("bundle");
        if (bundle != null) {
            if (!Objects.requireNonNull(bundle.getString("position")).isEmpty()) {
                SelectedPos = Integer.parseInt(Objects.requireNonNull(bundle.getString("position")));
            } else {
                SelectedPos = 0;
            }
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

        bottomPreviewDetailedHeadAdapter = new BottomLayoutHeadAdapter(PlaySlideDetailing.this, headingData);
        LinearLayoutManager layoutManager11 = new LinearLayoutManager(PlaySlideDetailing.this, LinearLayoutManager.HORIZONTAL, false);
        binding.recViewHead.setLayoutManager(layoutManager11);
        binding.recViewHead.setAdapter(bottomPreviewDetailedHeadAdapter);

        if (arrayList != null && !arrayList.isEmpty()) {
            switch (SupportClass.getFileExtension(arrayList.get(SelectedPos).getSlideName())) {
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
        if (SharedPref.getSlideAutoPlay(context).equalsIgnoreCase("1")) {
            binding.playBtn.setVisibility(View.GONE);
        } else {
            if (arrayList != null && !arrayList.isEmpty()) {
                switch (SupportClass.getFileExtension(arrayList.get(SelectedPos).getSlideName())) {
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
            } else {
                binding.playBtn.setVisibility(View.VISIBLE);
            }
        }
        itemsPagerAdapter = new PlaySlideDetailedAdapter(this, arrayList, mandatoryProductList);
        binding.viewPager.setAdapter(itemsPagerAdapter);
        binding.viewPager.setCurrentItem(SelectedPos);
        itemsPagerAdapter.onPageChanged(binding.viewPager.getCurrentItem());
        itemsPagerAdapter.autoPlaySlide(SelectedPos, 1);
    }

    public void populateBottomViewAdapter() {
        bottomPreviewDetailedAdapter = new BottomPreviewDetailedAdapter(PlaySlideDetailing.this, arrayList, binding.viewPager);
        LinearLayoutManager layoutManager = new LinearLayoutManager(PlaySlideDetailing.this, LinearLayoutManager.HORIZONTAL, false);
        binding.recView.setLayoutManager(layoutManager);
        binding.recView.setAdapter(bottomPreviewDetailedAdapter);
    }
    private void loadVideo(File file) {
        binding.pdfView.setVisibility(View.GONE);
        binding.webView.setVisibility(View.GONE);
        binding.previewThumb.setVisibility(View.GONE);

        // ✅ Screen outside ah move pannunga - surface create aagum but user kaanmaatan
        binding.videoView.setTranslationY(-10000f);
        binding.videoView.setVisibility(View.VISIBLE);

        Uri uri = Uri.fromFile(file);
        binding.videoView.setVideoURI(uri);
        binding.videoView.setMediaController(mediaController);
        binding.videoView.setOnPreparedListener(mp -> {
            Log.e("VIDEO_DEBUG", "onPrepared called!");
            // ✅ Ready aana normal position la show pannunga
            binding.videoView.setTranslationY(0f);
            mp.start();
        });
        binding.videoView.setOnErrorListener((mp, what, extra) -> {
            Log.e("VIDEO_DEBUG", "Error! what=" + what + " extra=" + extra);
            return false;
        });
        binding.videoView.requestFocus();
    }
    private void setPdfThumbnail(String filePath) {
        try {
            ParcelFileDescriptor fd = ParcelFileDescriptor.open(
                    new File(filePath), ParcelFileDescriptor.MODE_READ_ONLY);
            PdfRenderer renderer = new PdfRenderer(fd);
            PdfRenderer.Page page = renderer.openPage(0);

            Bitmap bitmap = Bitmap.createBitmap(
                    page.getWidth(), page.getHeight(), Bitmap.Config.ARGB_8888);
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);

            binding.previewThumb.setImageBitmap(bitmap);
            binding.previewThumb.setVisibility(View.VISIBLE);

            page.close();
            renderer.close();
        } catch (Exception e) {
            Log.e("THUMB_ERROR", e.getMessage());
        }
    }
    public void loadPdf(String fileName) {
        File pdfFile = new File(fileName);


        binding.previewThumb.setVisibility(View.VISIBLE);
        binding.pdfView.setVisibility(View.INVISIBLE);

        binding.pdfView.fromFile(pdfFile)
                .defaultPage(0)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .pageSnap(true)
                .spacing(0)
                .onRender(nbPages -> {

                    binding.previewThumb.setVisibility(View.GONE);
                    binding.pdfView.setVisibility(View.VISIBLE);
                })
                .onError(throwable -> {
                    binding.previewThumb.setVisibility(View.GONE);
                    Log.e("PDF_ERROR", "Error: " + throwable.getMessage());
                })
                .load();
    }
//    public void loadPdf(String fileName) {
//        binding.pdfView.fromFile(new File(fileName))
//                .onRender((nbPages) -> {
//                    binding.loadingView.setVisibility(View.GONE);
//                    binding.loadingView.stopLoading();
//                })
//                .defaultPage(0)
//                .enableAnnotationRendering(true)
//                .scrollHandle(new DefaultScrollHandle(this))
//                .enableSwipe(true)
//                .swipeHorizontal(false)
//                .pageSnap(true)
//                .autoSpacing(false)
//                .pageFling(true)
//                .spacing(0)
//                .load();
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public static class BottomLayoutHeadAdapter extends RecyclerView.Adapter<BottomLayoutHeadAdapter.MyViewHolder> {
        Context context;
        List<String> arrayListHead;
        private RoomDB roomDB;
        private MasterDataDao masterDataDao;

        public BottomLayoutHeadAdapter(Context context, List<String> arrayListHead) {
            this.context = context;
            this.arrayListHead = arrayListHead;
            roomDB = RoomDB.getDatabase(context);
            masterDataDao = roomDB.masterDataDao();
        }

        @NonNull
        @Override
        public BottomLayoutHeadAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.presentation_btm_preview_head, parent, false);
            return new MyViewHolder(view);
        }

        @SuppressLint("NotifyDataSetChanged")
        @Override
        public void onBindViewHolder(@NonNull BottomLayoutHeadAdapter.MyViewHolder holder, int position) {

            if (SelectedPosPlay == holder.getAbsoluteAdapterPosition()) {
                holder.tv_brandName.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_purple));
            } else {
                holder.tv_brandName.setBackground(ContextCompat.getDrawable(context, R.drawable.bg_grey));
            }

            switch (arrayListHead.get(holder.getAbsoluteAdapterPosition())) {
                case "A":
                    holder.tv_brandName.setText(context.getString(R.string.welcome));
                    break;
                case "B":
                    holder.tv_brandName.setText(context.getResources().getText(R.string.all_brands));
                    break;
                case "C":
                    holder.tv_brandName.setText(context.getResources().getText(R.string.brand_matrix));
                    break;
                case "D":
                    holder.tv_brandName.setText(context.getResources().getText(R.string.speciality));
                    break;
                case "E":
                    holder.tv_brandName.setText(context.getResources().getText(R.string.therapist));
                    break;
                case "F":
                    holder.tv_brandName.setText(context.getResources().getText(R.string.my_presentation));
                    break;
                case "G":
                    holder.tv_brandName.setText(context.getResources().getText(R.string.customized_presentation));
                    break;
                case "H":
                    holder.tv_brandName.setText(context.getResources().getText(R.string.custom_presentation));
                    break;
            }


            holder.tv_brandName.setOnClickListener(view -> {
                SelectedPosPlay = holder.getAbsoluteAdapterPosition();
                itemsPagerAdapter.logCurrentPageEndIfNeeded();
                switch (arrayListHead.get(holder.getAbsoluteAdapterPosition())) {
                    case "A":
                        populateListData(SlideWelcomeList);
                        break;
                    case "B":
                        populateListData(SlideHomeBrandList);
                        break;
                    case "C":
                        populateListData(SlideBrandMatrixList);
                        break;
                    case "D":
                        populateListData(SlideSpecialityList);
                        break;
                    case "E":
                        populateListData(SlideTherapistList);
                        break;
                    case "F":
                        populateLocalSavedData(SlideCustomizedList);
                        break;
                    case "G":
                        populateLocalSavedData(SlideCustomList);
                        break;
                    case "H":
                        populateCustomSavedData();
                        break;
                }
                notifyDataSetChanged();
            });
        }

        private void populateCustomSavedData() {
            ArrayList<BrandModelClass.Product> productsList = new ArrayList<>();
            if (!CustomPresentationFragment.selectedSlideArrayList.isEmpty()) {
                productsList.addAll(CustomPresentationFragment.selectedSlideArrayList);
            }
            if (!productsList.isEmpty()) {
                binding.constraintNoData.setVisibility(View.GONE);
                binding.rightArrow.setVisibility(View.GONE);
            } else {
                binding.constraintNoData.setVisibility(View.VISIBLE);
                binding.rightArrow.setVisibility(View.VISIBLE);
            }

            populateViewPagerAdapterNew(productsList);
            populateBottomViewAdapterNew(productsList);
        }

        private void populateListData(ArrayList<BrandModelClass> brandProductArrayList) {
            ArrayList<BrandModelClass.Product> productsList = new ArrayList<>();
            for (int i = 0; i < brandProductArrayList.size(); i++) {
                for (int j = 0; j < brandProductArrayList.get(i).getProductArrayList().size(); j++) {
                    productsList.add(new BrandModelClass.Product(brandProductArrayList.get(i).getBrandCode(), brandProductArrayList.get(i).getBrandName(), brandProductArrayList.get(i).getProductArrayList().get(j).getSlideId(), brandProductArrayList.get(i).getProductArrayList().get(j).getSlideName(), brandProductArrayList.get(i).getProductArrayList().get(j).getPriority(), brandProductArrayList.get(i).getProductArrayList().get(j).isImageSelected(), brandProductArrayList.get(i).getProductArrayList().get(j).getProductCode(), brandProductArrayList.get(i).getProductArrayList().get(j).getMandatorySlide()));
                }
            }

            if (!productsList.isEmpty()) {
                binding.constraintNoData.setVisibility(View.GONE);
                binding.rightArrow.setVisibility(View.GONE);
            } else {
                binding.constraintNoData.setVisibility(View.VISIBLE);
                binding.rightArrow.setVisibility(View.VISIBLE);
            }

            populateViewPagerAdapterNew(productsList);
            populateBottomViewAdapterNew(productsList);
        }

        private void populateLocalSavedData(ArrayList<BrandModelClass.Presentation> savedPresentation) {
            try {
                ArrayList<BrandModelClass.Product> productsList = new ArrayList<>();
                for (int i = 0; i < savedPresentation.size(); i++) {
                    for (int j = 0; j < savedPresentation.get(i).getProducts().size(); j++) {
                        productsList.add(new BrandModelClass.Product(savedPresentation.get(i).getPresentationName(), savedPresentation.get(i).getProducts().get(j).getBrandName(), savedPresentation.get(i).getProducts().get(j).getBrandCode(), savedPresentation.get(i).getProducts().get(j).getSlideId(), savedPresentation.get(i).getProducts().get(j).getSlideName(), savedPresentation.get(i).getProducts().get(j).getPriority(), savedPresentation.get(i).getProducts().get(j).isImageSelected(), savedPresentation.get(i).getProducts().get(j).getMandatorySlide()));
                    }
                }

                if (productsList.size() > 0) {
                    binding.constraintNoData.setVisibility(View.GONE);
                } else {
                    binding.constraintNoData.setVisibility(View.VISIBLE);
                }
                populateViewPagerAdapterNew(productsList);
                populateBottomViewAdapterNew(productsList);
            } catch (Exception e) {
                Log.v("bottomError", "error--local---" + e);
            }
        }

        private void populateRequiredData(String Selection) {
            ArrayList<BrandModelClass> brandProductArrayList = new ArrayList<>();
            ArrayList<String> brandCodeList = new ArrayList<>();
            BrandModelClass.Product product;
            try {
                JSONArray prodSlide = masterDataDao.getMasterDataTableOrNew(Constants.PROD_SLIDE).getMasterSyncDataJsonArray();
                JSONArray brandSlide = masterDataDao.getMasterDataTableOrNew(Constants.BRAND_SLIDE).getMasterSyncDataJsonArray();

                for (int i = 0; i < brandSlide.length(); i++) {
                    JSONObject brandObject = brandSlide.getJSONObject(i);
                    String brandName = "", code = "", slideId = "", fileName = "", slidePriority = "", productDetailCode = "", mandatorySlide = "";
                    String brandCode = brandObject.optString("Product_Brd_Code");
                    String priority = brandObject.optString("Priority");


                    ArrayList<BrandModelClass.Product> productArrayList = new ArrayList<>();
                    for (int j = 0; j < prodSlide.length(); j++) {
                        JSONObject productObject = prodSlide.getJSONObject(j);
                        if (productObject.optString("Code").equalsIgnoreCase(brandCode)) {
                            switch (Selection) {
                                case "A":
                                    brandName = productObject.optString("Name");
                                    code = productObject.optString("Code");
                                    slideId = productObject.optString("SlideId");
                                    fileName = productObject.optString("FilePath");
                                    slidePriority = productObject.optString("Priority");
                                    productDetailCode = productObject.optString("Product_Detail_Code");
                                    mandatorySlide = productObject.optString("Mandatory_slide");
                                    product = new BrandModelClass.Product(code, brandName, slideId, fileName, slidePriority, false, productDetailCode, mandatorySlide);
                                    productArrayList.add(product);
                                    break;
                                case "B":
                                    if (PlaySlideDetailing.MappedBrandsPlay.contains(productObject.optString("Code")) && PlaySlideDetailing.MappedSlidesPlay.contains(productObject.optString("Product_Detail_Code"))) {
                                        brandName = productObject.optString("Name");
                                        code = productObject.optString("Code");
                                        slideId = productObject.optString("SlideId");
                                        fileName = productObject.optString("FilePath");
                                        slidePriority = productObject.optString("Priority");
                                        productDetailCode = productObject.optString("Product_Detail_Code");
                                        mandatorySlide = productObject.optString("Mandatory_slide");
                                        product = new BrandModelClass.Product(code, brandName, slideId, fileName, slidePriority, false, productDetailCode, mandatorySlide);
                                        productArrayList.add(product);
                                    }
                                    break;
                                case "C":
                                    if (productObject.optString("Speciality_Code").contains(PlaySlideDetailing.SpecialityCodePlay)) {
                                        brandName = productObject.optString("Name");
                                        code = productObject.optString("Code");
                                        slideId = productObject.optString("SlideId");
                                        fileName = productObject.optString("FilePath");
                                        slidePriority = productObject.optString("Priority");
                                        productDetailCode = productObject.optString("Product_Detail_Code");
                                        mandatorySlide = productObject.optString("Mandatory_slide");
                                        product = new BrandModelClass.Product(code, brandName, slideId, fileName, slidePriority, false, productDetailCode, mandatorySlide);
                                        productArrayList.add(product);
                                    }
                                    break;
//                                case "D":
//                                    if (productObject.optString("Speciality_Code").contains(PlaySlideDetailing.SpecialityCodePlay)) {
//                                        brandName = productObject.optString("Name");
//                                        code = productObject.optString("Code");
//                                        slideId = productObject.optString("SlideId");
//                                        fileName = productObject.optString("FilePath");
//                                        slidePriority = productObject.optString("Priority");
//                                        product = new BrandModelClass.Product(code, brandName, slideId, fileName, slidePriority, false);
//                                        productArrayList.add(product);
//                                    }
//                                    break;
                            }
                        }
                    }
                    boolean brandSelected = i == 0;
                    if (!brandCodeList.contains(brandCode) && !brandName.isEmpty()) {  //To avoid repeated of same brand
                        BrandModelClass brandModelClass = new BrandModelClass(brandName, brandCode, priority, 0, brandSelected, productArrayList);
                        brandProductArrayList.add(brandModelClass);
                        brandCodeList.add(brandCode);
                    }
                }

                ArrayList<BrandModelClass.Product> productsList = new ArrayList<>();
                for (int i = 0; i < brandProductArrayList.size(); i++) {
                    for (int j = 0; j < brandProductArrayList.get(i).getProductArrayList().size(); j++) {
                        productsList.add(new BrandModelClass.Product(brandProductArrayList.get(i).getBrandCode(), brandProductArrayList.get(i).getBrandName(), brandProductArrayList.get(i).getProductArrayList().get(j).getSlideId(), brandProductArrayList.get(i).getProductArrayList().get(j).getSlideName(), brandProductArrayList.get(i).getProductArrayList().get(j).getPriority(), brandProductArrayList.get(i).getProductArrayList().get(j).isImageSelected(), brandProductArrayList.get(i).getProductArrayList().get(j).getProductCode(), brandProductArrayList.get(i).getProductArrayList().get(j).getMandatorySlide()));
                    }
                }

                if (productsList.size() > 0) {
                    binding.constraintNoData.setVisibility(View.GONE);
                } else {
                    binding.constraintNoData.setVisibility(View.VISIBLE);
                }
                populateViewPagerAdapterNew(productsList);
                populateBottomViewAdapterNew(productsList);

            } catch (Exception e) {
                Log.v("bottomError", "error-----" + e);
            }
        }

        @Override
        public int getItemCount() {
            return arrayListHead.size();
        }

        public static class MyViewHolder extends RecyclerView.ViewHolder {
            TextView tv_brandName;

            public MyViewHolder(@NonNull View itemView) {
                super(itemView);
                tv_brandName = itemView.findViewById(R.id.brandName);
            }
        }
    }

}
