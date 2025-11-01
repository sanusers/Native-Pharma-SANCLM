package saneforce.sanzen.activity.call.adapter.detailing;

import static saneforce.sanzen.activity.call.DCRCallActivity.arrayStore;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.PagerAdapter;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.DownsampleStrategy;
import com.github.barteksc.pdfviewer.BuildConfig;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.scroll.DefaultScrollHandle;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.pojo.detailing.LoadBitmap;
import saneforce.sanzen.activity.call.pojo.detailing.StoreImageTypeUrl;
import saneforce.sanzen.activity.presentation.SupportClass;
import saneforce.sanzen.activity.presentation.createPresentation.BrandModelClass;
import saneforce.sanzen.activity.previewPresentation.PreviewActivity;
import saneforce.sanzen.commonClasses.CommonSharedPreference;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.network.ApiInterface;
import saneforce.sanzen.network.RetrofitClient;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class PlaySlideDetailedAdapter extends PagerAdapter {

    public static ArrayList<LoadBitmap> storingSlide = new ArrayList<>();
    public static int presentSlidePos;
    public static String presentBrandName, presentBrandCode;
    public static boolean preVal = false;
    private final Context context;
    private final ArrayList<BrandModelClass.Product> productArrayList;
    ArrayList<StoreImageTypeUrl> slideDescribe = new ArrayList<>();
    public static ArrayList<StoreImageTypeUrl> slideScribble = new ArrayList<>();
    Object objsd;
    PlaySlideDetailing act;
    String slideUrl1 = null;
    Dialog dialogPopUp;
    ApiInterface apiService;
    CommonSharedPreference mCommonSharedPreference;
    int val = 0;
    String defaultTime = "00:00:00";
    int scribblePos;
    CommonUtilsMethods commonUtilsMethods;
    private Handler handler;
    private Runnable runnable;
    public static HashMap<String, ArrayList<String>> timer = new HashMap<>();
    private int currentPage = -1;
    public static String pageStartTime = TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_32);
    private HashMap<Integer, ImageView> imageViewList = new HashMap<>();
    private HashMap<Integer, PDFView> pdfViewList = new HashMap<>();
    private HashMap<Integer, VideoView> videoViewList = new HashMap<>();
    private HashMap<Integer, WebView> webViewList = new HashMap<>();
    private HashMap<Integer, LottieAnimationView> progressAnimationViewList = new HashMap<>();

    public PlaySlideDetailedAdapter(PlaySlideDetailing context, ArrayList<BrandModelClass.Product> productArrayList) {
        this.context = context;
        this.productArrayList = productArrayList;
        slideDescribe.clear();
        act = context;
        mCommonSharedPreference = new CommonSharedPreference(context);
        imageViewList = new HashMap<>();
        pdfViewList = new HashMap<>();
        videoViewList = new HashMap<>();
        webViewList = new HashMap<>();
        progressAnimationViewList = new HashMap<>();
        commonUtilsMethods = new CommonUtilsMethods(context);
        for (int i = 0; i < productArrayList.size(); i++) {
            File file = new File(context.getExternalFilesDir(null) + "/Slides/", productArrayList.get(i).getSlideName());
            if (file.exists()) {
                String fileFormat = SupportClass.getFileExtension(productArrayList.get(i).getSlideName());
                slideDescribe.add(new StoreImageTypeUrl("", productArrayList.get(i).getSlideName(), fileFormat, file.toString(), "", productArrayList.get(i).getSlideId(), productArrayList.get(i).getBrandName(), productArrayList.get(i).getBrandCode()));
            } else {
                slideDescribe.add(new StoreImageTypeUrl("", productArrayList.get(i).getSlideName(), "", "", "", productArrayList.get(i).getSlideId(), productArrayList.get(i).getBrandName(), productArrayList.get(i).getBrandCode()));
            }
        }
    }

    public String getSlideNameAt(int position) {
        if (position >= 0 && position < productArrayList.size()) {
            return productArrayList.get(position).getSlideName();
        }
        return "";
    }

    @SuppressLint("ClickableViewAccessibility")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View sliderLayout = inflater.inflate(R.layout.presentation_preview_item, null);

        ImageView imageView = sliderLayout.findViewById(R.id.imageView);
        WebView webView = sliderLayout.findViewById(R.id.webView);
        PDFView pdfView = sliderLayout.findViewById(R.id.pdfView);
        VideoView videoView = sliderLayout.findViewById(R.id.videoView);
        LottieAnimationView progressAnim = sliderLayout.findViewById(R.id.progress_anim);
        imageViewList.put(position, imageView);
        pdfViewList.put(position, pdfView);
        videoViewList.put(position, videoView);
        webViewList.put(position, webView);
        progressAnimationViewList.put(position, progressAnim);
        RelativeLayout rl_rightView = sliderLayout.findViewById(R.id.rightArrow);
        rl_rightView.setVisibility(View.VISIBLE);

        SupportClass.setThumbnail(context, productArrayList.get(position).getSlideName(), imageView);
        container.addView(sliderLayout);

        rl_rightView.setOnClickListener(view -> {
            File file = new File(context.getExternalFilesDir(null) + "/Slides/", productArrayList.get(position).getSlideName());
            String fileFormat = SupportClass.getFileExtension(productArrayList.get(position).getSlideName());
            popupScribbling(productArrayList.get(position).getSlideName(), productArrayList.get(position).getSlideId(), file.toString(), fileFormat);
        });
        resetTimer();
        sliderLayout.setOnTouchListener((view, event) -> {
            resetTimer();
            return false;
        });
        return sliderLayout;
    }

    public void autoPlaySlide(int position, int attempt) {
        if (SharedPref.getSlideAutoPlay(context).equalsIgnoreCase("1")) {
            try {
                ImageView imageView = imageViewList.get(position);
                WebView webView = webViewList.get(position);
                VideoView videoView = videoViewList.get(position);
                LottieAnimationView progressAnim = progressAnimationViewList.get(position);
                PDFView pdfView = pdfViewList.get(position);
                String fileName = productArrayList.get(position).getSlideName();
                File file = new File(context.getExternalFilesDir(null) + "/Slides/", fileName);
                if (file.exists()) {
                    String fileFormat = SupportClass.getFileExtension(fileName);
                    switch (fileFormat) {
                        case "pdf":
                            pdfView.setVisibility(View.VISIBLE);
                            videoView.setVisibility(View.GONE);
                            webView.setVisibility(View.GONE);
                            progressAnim.setVisibility(View.VISIBLE);
                            progressAnim.playAnimation();
                            pdfView.fromFile(file)
                                    .onRender((nbPages, pageWidth, pageHeight) -> {
                                        progressAnim.setVisibility(View.GONE);
                                        progressAnim.cancelAnimation();
                                    }).defaultPage(0).enableSwipe(true).swipeHorizontal(false).enableAnnotationRendering(true).scrollHandle(new DefaultScrollHandle(context)).load();
                            break;
                        case "mp4":
                        case "avi":
                            MediaController mediaController = new MediaController(context);
                            mediaController.setAnchorView(videoView);
                            pdfView.setVisibility(View.GONE);
                            videoView.setVisibility(View.VISIBLE);
                            webView.setVisibility(View.GONE);
                            progressAnim.setVisibility(View.VISIBLE);
                            progressAnim.playAnimation();
                            Uri uri = Uri.parse(file.getAbsolutePath());
                            videoView.setVideoURI(uri);
                            videoView.setMediaController(mediaController);
                            videoView.setOnPreparedListener(mp -> {
                                progressAnim.setVisibility(View.GONE);
                                mp.start();
                            });
//                            videoView.start();
                            break;
                        case "zip":
                            pdfView.setVisibility(View.GONE);
                            videoView.setVisibility(View.GONE);
                            webView.setVisibility(View.VISIBLE);
                            progressAnim.setVisibility(View.VISIBLE);
                            progressAnim.playAnimation();

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
                            webView.setInitialScale(1);
                            webView.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);

                            String filePath = SupportClass.getFileFromZip(file.getAbsolutePath(), "html");
                            Log.v("Slides", " --2222-- " + filePath);
                            if (!filePath.isEmpty()) {
                                webView.loadUrl("file://" + filePath);
                            }
                            webView.setWebViewClient(new WebViewClient() {
                                @Override
                                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                                    Log.v("Slides", " ---- " + url + " ---- " + view.getTitle() + " ---- " + view.getOriginalUrl());
                                    if (!url.isEmpty()) {
                                        webView.loadUrl(url);
                                    }
                                    return true;
                                }

                                @Override
                                public void onPageFinished(WebView view, String url) {
                                    super.onPageFinished(view, url);
                                    Log.i("webview", "onPageFinished: " + TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_22));
                                    progressAnim.setVisibility(View.GONE);
                                    progressAnim.cancelAnimation();
                                }
                            });
                            break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (attempt != 3) {
                    new Handler().postDelayed(() -> autoPlaySlide(position, attempt + 1), 500);
                }
            }
        }
    }

    public void logCurrentPageEndIfNeeded() {
        if (currentPage != -1 && !pageStartTime.isEmpty()) {
            String now = TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_32);
            String slideName = getSlideNameAt(currentPage);
            ArrayList<String> list = new ArrayList<>();
            if (timer.containsKey(slideName)) {
                list = timer.get(slideName);
            }
            list.add(pageStartTime + " $ " + now);
            timer.put(slideName, list);
            Log.d("SlideTiming", "Final slide " + slideName + "started at " + pageStartTime + " ended after " + now);
            currentPage = -1;
            pageStartTime = now;
        }
    }

    public void onPageChanged(int newPosition) {
        String now = TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_32);
        if (currentPage != -1 && !pageStartTime.isEmpty()) {
            String slideName = getSlideNameAt(currentPage);
            ArrayList<String> list = new ArrayList<>();
            if (timer.containsKey(slideName)) {
                list = timer.get(slideName);
            }
            list.add(pageStartTime + " $ " + now);
            timer.put(slideName, list);
            Log.d("SlideTiming", "Slide " + slideName + "started at " + pageStartTime + " ended after " + now);
        }
        pageStartTime = now;
        currentPage = newPosition;
    }

    public void resetTimer() {
        try {
            if (handler == null) {
                handler = new Handler(Looper.getMainLooper());
            }
            removeTimer();
            String timeLimit = SharedPref.getDetailingIdleDuration(context);
            if (!timeLimit.isEmpty()) {
                try {
                    runnable = () -> {
                        PreviewActivity.isTimerEnd = true;
                        handleStopDetailing();
                    };
                    long minutes = Long.parseLong(timeLimit);
                    if (minutes > 0) {
                        handler.postDelayed(runnable, minutes * 1000 * 60);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeTimer() {
        if (runnable != null) {
            handler.removeCallbacks(runnable);
        }
    }

    public void popupPaint(String slideName, final String path) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.popup_scribble);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        Display display = act.getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        RelativeLayout rlay = dialog.findViewById(R.id.r_lay);
        final RelativeLayout canvas_lay = dialog.findViewById(R.id.canvas_lay);
        final ImageView erase = dialog.findViewById(R.id.erase);
        final ImageView pen_black = dialog.findViewById(R.id.pen_black);
        final ImageView pen_red = dialog.findViewById(R.id.pen_red);
        final ImageView pen_green = dialog.findViewById(R.id.pen_green);
        final ImageView sq = dialog.findViewById(R.id.sq);
        final ImageView cir = dialog.findViewById(R.id.cir);
        final ImageView canva_img = dialog.findViewById(R.id.canva_img);
        final ImageView img_close = dialog.findViewById(R.id.img_close);
        final PaintView paintviews = dialog.findViewById(R.id.paintviews);
        Button submit = dialog.findViewById(R.id.submit);
        ViewGroup.LayoutParams layoutParams = rlay.getLayoutParams();
        layoutParams.width = width - 80;
        rlay.setLayoutParams(layoutParams);

        img_close.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dialog.dismiss();
            }
        });

        erase.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                paintviews.erase();
            }
        });

        submit.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                captureCanvasScreen(slideName, canvas_lay, dialog, path);
            }
        });
        sq.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                paintviews.addRectangle();
            }
        });
        cir.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                paintviews.addCircle();
            }
        });

        pen_black.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                paintviews.changePaintColor(1);
            }
        });

        pen_red.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                paintviews.changePaintColor(2);
            }
        });

        pen_green.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                paintviews.changePaintColor(3);
            }
        });

        try {
            Glide.with(context).load(new File(path)).downsample(DownsampleStrategy.FIT_CENTER).into(canva_img);
//            Drawable d = Drawable.createFromPath(path);
//            canva_img.setBackground(d);
        } catch (Exception e) {
            Log.e("PlaySlideDetailedAdapter", "popupPaint: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public void captureCanvasScreen(String slideName, View layBg, Dialog dialog, String imgpath) {
        layBg.setDrawingCacheEnabled(true);
        layBg.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        Bitmap bitmap = layBg.getDrawingCache();
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
        File file = new File(path, "EDetails/Pictures");
        if (!file.exists()) {
            file.mkdirs();
        }
        String file_path = file + "/" + "paint_" + System.currentTimeMillis() + ".png";
        String ScribbleFileName = "paint_" + System.currentTimeMillis() + ".png";
        FileOutputStream ostream;
        try {
            ostream = new FileOutputStream(file_path);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
            ostream.flush();
            ostream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        MultipartBody.Part imgg = convertImg("ScribbleImg", file_path);
        sendScribbleImg(imgg, dialog, imgpath, ScribbleFileName, slideName);
    }

    public MultipartBody.Part convertImg(String tag, String path) {
        MultipartBody.Part yy = null;
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            RequestBody requestBody = RequestBody.create(MultipartBody.FORM, file);
            yy = MultipartBody.Part.createFormData(tag, file.getName(), requestBody);
        }
        return yy;
    }

    public HashMap<String, RequestBody> field(String val) {
        HashMap<String, RequestBody> xx = new HashMap<String, RequestBody>();
        xx.put("data", createFromString(val));
        return xx;
    }

    private RequestBody createFromString(String txt) {
        return RequestBody.create(MultipartBody.FORM, txt);
    }

    public void sendScribbleImg(MultipartBody.Part img, final Dialog dialog, String path, String scribbleFileName, String SlideName) {
        dialog.dismiss();
        JSONObject jsonImage = CommonUtilsMethods.CommonObjectParameter(context);
        try {
            jsonImage.put("tableName", "uploadscribble");
            jsonImage.put("sfcode", SharedPref.getSfCode(context));
            jsonImage.put("division_code", SharedPref.getDivisionCode(context));
            jsonImage.put("Rsf", SharedPref.getHqCode(context));
            Log.v("scribbleUpload", jsonImage.toString());
        } catch (Exception ignored) {
        }


        commonUtilsMethods.showToastMessage(context, context.getString(R.string.processing));
        apiService = RetrofitClient.getRetrofit(context, SharedPref.getCallApiUrl(context));

        Call<JsonObject> callImageScrub;
        HashMap<String, RequestBody> values = field(jsonImage.toString());
        callImageScrub = apiService.SaveImg(values, img);

        callImageScrub.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(@NonNull Call<JsonObject> call, @NonNull Response<JsonObject> response) {
                assert response.body() != null;
                Log.v("scribbleUpload", "---res---" + response.body());
                if (response.isSuccessful()) {
                    try {
                        JSONObject jsonImgRes;
                        jsonImgRes = new JSONObject(response.body().toString());
                        if (jsonImgRes.getString("success").equalsIgnoreCase("true")) {
                            commonUtilsMethods.showToastMessage(context, context.getString(R.string.scribble_upload));
                            slideScribble.set(scribblePos, new StoreImageTypeUrl(slideScribble.get(scribblePos).getSlideNam(), slideScribble.get(scribblePos).getSlideid(), slideScribble.get(scribblePos).isLike(), slideScribble.get(scribblePos).isDisLike(), slideScribble.get(scribblePos).getSlideComments(), scribbleFileName));
                            dialog.dismiss();
                        }
                    } catch (Exception e) {
                        dialog.dismiss();
                    }
                } else {
                    dialog.dismiss();
                    commonUtilsMethods.showToastMessage(context, context.getString(R.string.something_wrong));
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                commonUtilsMethods.showToastMessage(context, context.getString(R.string.no_network));
                dialog.dismiss();
            }
        });
    }


    private void shareImage(File path, String fileFormat) {
        Uri contentUri = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".fileprovider", path);
        if (contentUri != null) {
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            shareIntent.setDataAndType(contentUri, context.getContentResolver().getType(contentUri));
            shareIntent.putExtra(Intent.EXTRA_STREAM, contentUri);
            switch (fileFormat) {
                case "jpg":
                case "png":
                case "gif":
                case "jpeg": {
                    shareIntent.setType("image/png");
                    return;
                }
                case "avi":
                case "mp4": {
                    shareIntent.setType("video/mp4");
                    return;
                }
                case "pdf": {
                    shareIntent.setType("application/pdf");
                    return;
                }
                case "zip": {
                    shareIntent.setType("application/zip");
                    return;
                }
            }
            context.startActivity(Intent.createChooser(shareIntent, "Choose an app"));
        }
    }

    public void commentsPopup() {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.detailing_pop_feed);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
        final EditText edt_feed = dialog.findViewById(R.id.ed_remark);
        ImageView img_close = dialog.findViewById(R.id.img_close);
        Button btnClear = dialog.findViewById(R.id.btn_clear);
        Button btnSave = dialog.findViewById(R.id.btn_save);

        btnSave.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                slideScribble.set(scribblePos, new StoreImageTypeUrl(slideScribble.get(scribblePos).getSlideNam(), slideScribble.get(scribblePos).getSlideid(), slideScribble.get(scribblePos).isLike(), slideScribble.get(scribblePos).isDisLike(), edt_feed.getText().toString(), slideScribble.get(scribblePos).getScribble()));
                dialog.dismiss();
            }
        });

        btnClear.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                edt_feed.setText("");
                edt_feed.setHint(context.getResources().getString(R.string.type_your_feedback_here));
            }
        });

        img_close.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                edt_feed.setText("");
                edt_feed.setHint(context.getResources().getString(R.string.type_your_feedback_here));
                dialog.dismiss();
            }
        });
    }

    public void popupScribbling(String slideName, String slideId, String path, String fileFormat) {
        act.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
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

        boolean isAvailable = false;
        if (!slideScribble.isEmpty()) {
            for (int i = 0; i < slideScribble.size(); i++) {
                if (slideScribble.get(i).getSlideNam().equalsIgnoreCase(slideName)) {
                    scribblePos = i;
                    isAvailable = true;
                    break;
                } else {
                    scribblePos = slideScribble.size();
                }
            }
        } else {
            scribblePos = 0;
        }

        if (isAvailable) {
            slideScribble.set(scribblePos, new StoreImageTypeUrl(slideName, slideId, false, false, slideScribble.get(scribblePos).getSlideComments(), slideScribble.get(scribblePos).getScribble()));
        } else {
            slideScribble.add(scribblePos, new StoreImageTypeUrl(slideName, slideId, false, false, "", ""));
        }


        rl_paint.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                popupPaint(slideName, path);
            }
        });

        rl_like.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (Objects.equals(rl_like.getBackground().getConstantState(), Objects.requireNonNull(ContextCompat.getDrawable(context, R.drawable.outline_green)).getConstantState())) {
                    rl_like.setBackground(ContextCompat.getDrawable(context, R.drawable.green_full));
                    rl_dislike.setBackground(ContextCompat.getDrawable(context, R.drawable.outline_pink));
                    slideScribble.set(scribblePos, new StoreImageTypeUrl(slideName, slideId, true, false, slideScribble.get(scribblePos).getSlideComments(), slideScribble.get(scribblePos).getScribble()));
                } else {
                    rl_like.setBackground(ContextCompat.getDrawable(context, R.drawable.outline_green));
                }
            }
        });

        rl_dislike.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (Objects.equals(rl_dislike.getBackground().getConstantState(), Objects.requireNonNull(ContextCompat.getDrawable(context, R.drawable.outline_pink)).getConstantState())) {
                    rl_dislike.setBackground(ContextCompat.getDrawable(context, R.drawable.pink_full));
                    rl_like.setBackground(ContextCompat.getDrawable(context, R.drawable.outline_green));
                    slideScribble.set(scribblePos, new StoreImageTypeUrl(slideName, slideId, false, true, slideScribble.get(scribblePos).getSlideComments(), slideScribble.get(scribblePos).getScribble()));
                } else {
                    rl_dislike.setBackground(ContextCompat.getDrawable(context, R.drawable.outline_pink));
                }
            }
        });

        rl_comments.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                commentsPopup();
            }
        });

        rl_share.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                final Intent shareIntent = new Intent(Intent.ACTION_SEND);
                final File photoFile = new File(String.valueOf(path));
                switch (fileFormat) {
                    case "jpg":
                    case "png":
                    case "gif":
                    case "jpeg": {
                        try {
                            shareIntent.setType("image/jpg");
                            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(photoFile));
                            context.startActivity(Intent.createChooser(shareIntent, "Share image using"));
                        } catch (Exception e) {
                            shareImage(photoFile, fileFormat);
                        }
                        return;
                    }
                    case "avi":
                    case "mp4": {
                        try {
                            ContentValues content = new ContentValues(4);
                            content.put(MediaStore.Video.VideoColumns.DATE_ADDED, System.currentTimeMillis() / 1000);
                            content.put(MediaStore.Video.Media.MIME_TYPE, "video/mp4");
                            content.put(MediaStore.Video.Media.DATA, path);

                            ContentResolver resolver = context.getContentResolver();
                            Uri uri = resolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, content);

                            shareIntent.setType("video/*");
                            shareIntent.putExtra(Intent.EXTRA_STREAM, uri);
                            context.startActivity(Intent.createChooser(shareIntent, "Share Video"));
                        } catch (Exception e) {
                            shareImage(photoFile, fileFormat);
                        }
                        return;
                    }
                    case "pdf": {
                        try {
                            shareIntent.setType("application/pdf");
                            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(photoFile));
                            context.startActivity(Intent.createChooser(shareIntent, "Share pdf using"));
                        } catch (Exception e) {
                            shareImage(photoFile, fileFormat);
                        }
                        return;
                    }
                    case "zip": {
                        try {
                            shareIntent.setType("application/zip");
                            shareIntent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(new File(path)));
                            context.startActivity(shareIntent);
                        } catch (Exception e) {
                            shareImage(photoFile, fileFormat);
                        }
                    }
                }
            }
        });

        rl_stop.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                handleStopDetailing();
            }
        });


        params.setMargins(0, 0, 0, 0);
        wlp.gravity = Gravity.CENTER | Gravity.END;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        dialogPopUp.show();
        act.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    private void handleStopDetailing() {
        if (arrayStore != null) {
            arrayStore.clear();
        }
        logCurrentPageEndIfNeeded();
        removeTimer();
        act.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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

//            for (int i = 0; i<PlaySlideDetailedAdapter.storingSlide.size(); i++) {
//                Log.v("total_printing", PlaySlideDetailedAdapter.storingSlide.get(i).getSlideName() + "size" + PlaySlideDetailedAdapter.storingSlide.size() + "slide" + PlaySlideDetailedAdapter.storingSlide.get(i).getIndexVal());
//            }

            for (int i = 0; i < PlaySlideDetailedAdapter.storingSlide.size(); i++) {
                int ll = 0;
                if (i != 0) ll = i - 1;
                if (i == 0 || PlaySlideDetailedAdapter.storingSlide.get(ll).getIndexVal() != PlaySlideDetailedAdapter.storingSlide.get(i).getIndexVal()) {
                    if (!PlaySlideDetailedAdapter.storingSlide.get(i).getBrandName().equalsIgnoreCase("Welcome")) {
                        mCommonSharedPreference.setValueToPreferenceFeed("timeVal" + timecount, PlaySlideDetailedAdapter.storingSlide.get(i).getTiming());
                        mCommonSharedPreference.setValueToPreferenceFeed("dateVal" + timecount, PlaySlideDetailedAdapter.storingSlide.get(i).getDateVal());
                        mCommonSharedPreference.setValueToPreferenceFeed("brd_nam" + timecount, PlaySlideDetailedAdapter.storingSlide.get(i).getBrandName());
                        mCommonSharedPreference.setValueToPreferenceFeed("brd_code" + timecount, PlaySlideDetailedAdapter.storingSlide.get(i).getBrandCode());
                        mCommonSharedPreference.setValueToPreferenceFeed("slide_nam" + timecount, PlaySlideDetailedAdapter.storingSlide.get(i).getSlideName());
                        mCommonSharedPreference.setValueToPreferenceFeed("slide_typ" + timecount, PlaySlideDetailedAdapter.storingSlide.get(i).getSlideType());
                        mCommonSharedPreference.setValueToPreferenceFeed("slide_url" + timecount, PlaySlideDetailedAdapter.storingSlide.get(i).getSlideUrl());
                        mCommonSharedPreference.setValueToPreferenceFeed("timeCount", ++timecount);
                    }
                }
            }

            val = mCommonSharedPreference.getValueFromPreferenceFeed("timeCount", 0);
            Log.v("slideData", String.valueOf(val));
            for (int i = 0; i < val; i++) {
                String timevalue = mCommonSharedPreference.getValueFromPreferenceFeed("timeVal" + i);
                String SlideName = mCommonSharedPreference.getValueFromPreferenceFeed("slide_nam" + i);
                String BrandName = mCommonSharedPreference.getValueFromPreferenceFeed("brd_nam" + i);
                String BrandCode = mCommonSharedPreference.getValueFromPreferenceFeed("brd_code" + i);
                String slidetyp = mCommonSharedPreference.getValueFromPreferenceFeed("slide_typ" + i);
                String slideur = mCommonSharedPreference.getValueFromPreferenceFeed("slide_url" + i);

                if (!BrandName.equalsIgnoreCase("Welcome")) {
                    String eTime;
                    if (arrayStore != null && !arrayStore.isEmpty() && arrayStore.contains(new StoreImageTypeUrl(SlideName))) {
//                        eTime = findingEndTime(i);
//                        int index = checkForProduct(SlideName);
//                        StoreImageTypeUrl mmm = arrayStore.get(index);

//                        try {
//                            JSONArray jj = new JSONArray(mmm.getRemTime());
//                            JSONArray jk = new JSONArray();
//                            JSONObject js = null;
//                            for (int k = 0; k<jj.length(); k++) {
//                                js = jj.getJSONObject(k);
//                                jk.put(js);
//                            }
//                            js = new JSONObject();
//                            js.put("sT", timevalue);
//                            js.put("eT", eTime);
//                            jk.put(js);
//                            mmm.setRemTime(jk.toString());
//                        } catch (Exception ignored) {
//                        }
                    } else if (!SlideName.isEmpty()) {
//                        eTime = findingEndTime(i);
                        JSONObject jsonObject = new JSONObject();
                        JSONArray jsonArray = new JSONArray();
//                        try {
//                            jsonObject.put("sT", timevalue);
//                            jsonObject.put("eT", eTime);
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//                        }
//                        jsonArray.put(jsonObject);
                        Set<String> timing = new LinkedHashSet<>();
                        if (timer.get(SlideName) != null && !timer.get(SlideName).isEmpty()) {
                            timing.addAll(timer.get(SlideName));
                        }
                        if (!timing.isEmpty()) {
                            for (String time : timing) {
                                try {
                                    Log.v("SlideTiming", "handleStopDetailing => " + SlideName + " -> " + time);
                                    jsonObject = new JSONObject();
                                    String[] timeSplit = time.split(" \\$ ");
                                    jsonObject.put("sT", timeSplit[0]);
                                    jsonObject.put("eT", timeSplit[1]);
                                    jsonArray.put(jsonObject);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                        Log.v("slideData", "----" + BrandName + "----" + SlideName + " -> " + jsonArray);
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
                            arrayStore.add(new StoreImageTypeUrl(slideScribble.get(scribblePos).getScribble(), SlideName, slidetyp, slideur, "0", slideScribble.get(scribblePos).getSlideComments(), jsonArray.toString(), BrandName, BrandCode, false));
                        } else {
                            arrayStore.add(new StoreImageTypeUrl("", SlideName, slidetyp, slideur, "0", "", jsonArray.toString(), BrandName, BrandCode, false));
                        }
                    }
                }
            }
        }
        slideScribble.clear();
        act.getOnBackPressedDispatcher().onBackPressed();
    }

    public int checkForProduct(String slidename) {
        for (int i = 0; i < arrayStore.size(); i++) {
            if (arrayStore.get(i).getSlideNam().equals(slidename)) {
                return i;
            }
        }
        return -1;
    }


    public String findingEndTime(int k) {
        if (checkForLastSlide(k)) {
            return CommonUtilsMethods.getCurrentInstance("HH:mm:ss");
        } else {
            int j = k + 1;
            return mCommonSharedPreference.getValueFromPreferenceFeed("timeVal" + j);
        }
    }

    public boolean checkForLastSlide(int k) {
        return k == val - 1;
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
        StoreImageTypeUrl mm = slideDescribe.get(position);
        presentSlidePos = position;
        presentBrandName = mm.getBrdName();
        presentBrandCode = mm.getBrdCode();
        objsd = object;
        preVal = true;
      /*  Log.v("Slides", "----" + mm.getSlideTyp() + "---- " + mm.getSlideNam() + " --- " + mm.getSlideUrl());
        if (mm.getSlideTyp().equalsIgnoreCase("zip")) {
            String fileName = mm.getSlideNam();
            File file = new File(context.getExternalFilesDir(null) + "/Slides/", fileName);
            if (file.exists()) {
                String fileFormat = SupportClass.getFileExtension(fileName);
                String filePath = SupportClass.getFileFromZip(file.getAbsolutePath(), "html");
            }
        }*/
        if (!mm.getBrdName().equalsIgnoreCase("Welcome")) {
            Log.i("TAG slide", "setPrimaryItem: " + mm.getSlideNam() + " --> " + CommonUtilsMethods.getCurrentInstance("HH:mm:ss"));
            storingSlide.add(new LoadBitmap(mm.getScribble(), CommonUtilsMethods.getCurrentInstance("HH:mm:ss"), position, CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd"), mm.getSlideNam(), mm.getSlideTyp(), mm.getSlideUrl(), mm.getBrdName(), mm.getBrdCode()));
        }
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View) object);
    }

    @Override
    public int getCount() {
        return productArrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

}
