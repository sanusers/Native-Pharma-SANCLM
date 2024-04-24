package saneforce.santrip.activity.call.adapter.detailing;


import static android.Manifest.permission.READ_MEDIA_AUDIO;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.READ_MEDIA_VIDEO;
import static saneforce.santrip.activity.call.DCRCallActivity.arrayStore;
import static saneforce.santrip.activity.call.adapter.detailing.PlaySlideDetailedAdapter.slideScribble;
import static saneforce.santrip.activity.previewPresentation.PreviewActivity.SelectedPosPlay;
import static saneforce.santrip.activity.previewPresentation.fragment.BrandMatrix.SlideBrandMatrixList;
import static saneforce.santrip.activity.previewPresentation.fragment.Customized.SlideCustomizedList;
import static saneforce.santrip.activity.previewPresentation.fragment.HomeBrands.SlideHomeBrandList;
import static saneforce.santrip.activity.previewPresentation.fragment.Speciality.SlideSpecialityList;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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

import saneforce.santrip.R;
import saneforce.santrip.activity.call.pojo.detailing.StoreImageTypeUrl;
import saneforce.santrip.activity.presentation.SupportClass;
import saneforce.santrip.activity.presentation.createPresentation.BrandModelClass;
import saneforce.santrip.commonClasses.CommonSharedPreference;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.databinding.ActivityPlaySlidePreviewDetailingBinding;
import saneforce.santrip.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.santrip.roomdatabase.RoomDB;
import saneforce.santrip.storage.SQLite;

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
    int SelectedPos;

    int scribblePos;
    int val = 0;
    CommonSharedPreference mCommonSharedPreference;
//    SQLite sqLite;
    Dialog dialogPopUp;
    String defaultTime = "00:00:00";


    public static void populateViewPagerAdapterNew(ArrayList<BrandModelClass.Product> productsList) {
        itemsPagerAdapter = new PlaySlideDetailedAdapter((PlaySlideDetailing) context, productsList);
        binding.viewPager.setAdapter(itemsPagerAdapter);
    }

    public static void populateBottomViewAdapterNew(ArrayList<BrandModelClass.Product> productsList) {
        bottomPreviewDetailedAdapter = new BottomPreviewDetailedAdapter(context, productsList, binding.viewPager);
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
//        sqLite = new SQLite(this);
        context = this;
        initialisation();


        binding.rightArrow.setOnClickListener(v -> {
            DialogPopUp();
        });
        binding.viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
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
                    case "zip":
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


        binding.exitBtn.setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());

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
                        case "pdf":
                            binding.pdfView.setVisibility(View.VISIBLE);
                            binding.videoView.setVisibility(View.GONE);
                            binding.webView.setVisibility(View.GONE);
                            loadPdf(file.getAbsolutePath());
                            break;
                        case "mp4":
                            binding.pdfView.setVisibility(View.GONE);
                            binding.videoView.setVisibility(View.VISIBLE);
                            binding.webView.setVisibility(View.GONE);
                            Uri uri = Uri.parse(file.getAbsolutePath());
                            binding.videoView.setVideoURI(uri);
                            binding.videoView.setMediaController(mediaController);
                            binding.videoView.start();
                            break;
                        case "zip":
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
                            });


                            break;
                    }
                }
            } else {
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
            }
        });
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
        rl_stop.setOnClickListener(v1 -> {
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
                        mCommonSharedPreference.setValueToPreferenceFeed("slide_typ" + timecount, PlaySlideDetailedAdapter.storingSlide.get(i).getSlideType());
                        mCommonSharedPreference.setValueToPreferenceFeed("slide_url" + timecount, PlaySlideDetailedAdapter.storingSlide.get(i).getSlideUrl());
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
                    String slidetyp = mCommonSharedPreference.getValueFromPreferenceFeed("slide_typ" + i);
                    String slideur = mCommonSharedPreference.getValueFromPreferenceFeed("slide_url" + i);

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
                            arrayStore.add(new StoreImageTypeUrl(slideScribble.get(scribblePos).getScribble(), SlideName, slidetyp, slideur, "0", slideScribble.get(scribblePos).getSlideComments(), jsonArray.toString(), BrandName, BrandCode, false));
                        } else {
                            arrayStore.add(new StoreImageTypeUrl("", SlideName, slidetyp, slideur, "0", "", jsonArray.toString(), BrandName, BrandCode, false));
                        }
                    }
                }
            }
            getOnBackPressedDispatcher().onBackPressed();
        });
        params.setMargins(0, 0, 0, 0);
        wlp.gravity = Gravity.CENTER | Gravity.END;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        dialogPopUp.show();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(PlaySlideDetailing.this, READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(PlaySlideDetailing.this, READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {
                CommonUtilsMethods.RequestPermissions(this, new String[]{READ_MEDIA_IMAGES, READ_MEDIA_AUDIO, READ_MEDIA_VIDEO}, false);
            }
        } else {
            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                CommonUtilsMethods.RequestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, false);
            }
        }
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

        if (arrayList.size() > 0) {
            switch (SupportClass.getFileExtension(arrayList.get(SelectedPos).getSlideName())) {
                case "pdf":
                case "mp4":
                case "zip":
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
        itemsPagerAdapter = new PlaySlideDetailedAdapter(this, arrayList);
        binding.viewPager.setAdapter(itemsPagerAdapter);
        binding.viewPager.setCurrentItem(SelectedPos);
    }

    public void populateBottomViewAdapter() {
        bottomPreviewDetailedAdapter = new BottomPreviewDetailedAdapter(PlaySlideDetailing.this, arrayList, binding.viewPager);
        LinearLayoutManager layoutManager = new LinearLayoutManager(PlaySlideDetailing.this, LinearLayoutManager.HORIZONTAL, false);
        binding.recView.setLayoutManager(layoutManager);
        binding.recView.setAdapter(bottomPreviewDetailedAdapter);
    }

    public void loadPdf(String fileName) {
        binding.pdfView.fromFile(new File(fileName)).defaultPage(0).enableSwipe(true).swipeHorizontal(false).enableAnnotationRendering(true).scrollHandle(new DefaultScrollHandle(this)).load();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public static class BottomLayoutHeadAdapter extends RecyclerView.Adapter<BottomLayoutHeadAdapter.MyViewHolder> {
        Context context;
        List<String> arrayListHead;
//        SQLite sqLite;
        private RoomDB roomDB;
        private MasterDataDao masterDataDao;

        public BottomLayoutHeadAdapter(Context context, List<String> arrayListHead) {
            this.context = context;
            this.arrayListHead = arrayListHead;
//            this.sqLite = sqLite;
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
                    holder.tv_brandName.setText(context.getResources().getText(R.string.all));
                    break;
                case "B":
                    holder.tv_brandName.setText(context.getResources().getText(R.string.brand_matrix));
                    break;
                case "C":
                    holder.tv_brandName.setText(context.getResources().getText(R.string.speciality));
                    break;
                case "D":
                    holder.tv_brandName.setText(context.getResources().getText(R.string.custom_presentation));
                    break;
            }


            holder.tv_brandName.setOnClickListener(v -> {
                SelectedPosPlay = holder.getAbsoluteAdapterPosition();
                switch (arrayListHead.get(holder.getAbsoluteAdapterPosition())) {
                    case "A":
                        populateListData(SlideHomeBrandList);
                        break;
                    case "B":
                        populateListData(SlideBrandMatrixList);
                        break;
                    case "C":
                        populateListData(SlideSpecialityList);
                        break;
                    case "D":
                        populateLocalSavedData(SlideCustomizedList);
                        break;
                }
                notifyDataSetChanged();
            });
        }

        private void populateListData(ArrayList<BrandModelClass> brandProductArrayList) {

            ArrayList<BrandModelClass.Product> productsList = new ArrayList<>();
            for (int i = 0; i < brandProductArrayList.size(); i++) {
                for (int j = 0; j < brandProductArrayList.get(i).getProductArrayList().size(); j++) {
                    productsList.add(new BrandModelClass.Product(brandProductArrayList.get(i).getBrandCode(), brandProductArrayList.get(i).getBrandName(), brandProductArrayList.get(i).getProductArrayList().get(j).getSlideId(), brandProductArrayList.get(i).getProductArrayList().get(j).getSlideName(), brandProductArrayList.get(i).getProductArrayList().get(j).getPriority(), brandProductArrayList.get(i).getProductArrayList().get(j).isImageSelected()));
                }
            }

            if (productsList.size() > 0) {
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
                // ArrayList<BrandModelClass.Presentation> savedPresentation = sqLite.getPresentationData();
                ArrayList<BrandModelClass.Product> productsList = new ArrayList<>();
                for (int i = 0; i < savedPresentation.size(); i++) {
                    for (int j = 0; j < savedPresentation.get(i).getProducts().size(); j++) {
                        productsList.add(new BrandModelClass.Product(savedPresentation.get(i).getPresentationName(), savedPresentation.get(i).getProducts().get(j).getBrandName(), savedPresentation.get(i).getProducts().get(j).getBrandCode(), savedPresentation.get(i).getProducts().get(j).getSlideId(), savedPresentation.get(i).getProducts().get(j).getSlideName(), savedPresentation.get(i).getProducts().get(j).getPriority(), savedPresentation.get(i).getProducts().get(j).isImageSelected()));
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
//                JSONArray prodSlide = sqLite.getMasterSyncDataByKey(Constants.PROD_SLIDE);
//                JSONArray brandSlide = sqLite.getMasterSyncDataByKey(Constants.BRAND_SLIDE);
                JSONArray prodSlide = masterDataDao.getMasterDataTableOrNew(Constants.PROD_SLIDE).getMasterSyncDataJsonArray();
                JSONArray brandSlide = masterDataDao.getMasterDataTableOrNew(Constants.BRAND_SLIDE).getMasterSyncDataJsonArray();

                for (int i = 0; i < brandSlide.length(); i++) {
                    JSONObject brandObject = brandSlide.getJSONObject(i);
                    String brandName = "", code = "", slideId = "", fileName = "", slidePriority = "";
                    String brandCode = brandObject.getString("Product_Brd_Code");
                    String priority = brandObject.getString("Priority");


                    ArrayList<BrandModelClass.Product> productArrayList = new ArrayList<>();
                    for (int j = 0; j < prodSlide.length(); j++) {
                        JSONObject productObject = prodSlide.getJSONObject(j);
                        if (productObject.getString("Code").equalsIgnoreCase(brandCode)) {
                            switch (Selection) {
                                case "A":
                                    brandName = productObject.getString("Name");
                                    code = productObject.getString("Code");
                                    slideId = productObject.getString("SlideId");
                                    fileName = productObject.getString("FilePath");
                                    slidePriority = productObject.getString("Priority");
                                    product = new BrandModelClass.Product(code, brandName, slideId, fileName, slidePriority, false);
                                    productArrayList.add(product);
                                    break;
                                case "B":
                                    if (PlaySlideDetailing.MappedBrandsPlay.contains(productObject.getString("Code")) && PlaySlideDetailing.MappedSlidesPlay.contains(productObject.getString("Product_Detail_Code"))) {
                                        brandName = productObject.getString("Name");
                                        code = productObject.getString("Code");
                                        slideId = productObject.getString("SlideId");
                                        fileName = productObject.getString("FilePath");
                                        slidePriority = productObject.getString("Priority");
                                        product = new BrandModelClass.Product(code, brandName, slideId, fileName, slidePriority, false);
                                        productArrayList.add(product);
                                    }
                                    break;
                                case "C":
                                    if (productObject.getString("Speciality_Code").contains(PlaySlideDetailing.SpecialityCodePlay)) {
                                        brandName = productObject.getString("Name");
                                        code = productObject.getString("Code");
                                        slideId = productObject.getString("SlideId");
                                        fileName = productObject.getString("FilePath");
                                        slidePriority = productObject.getString("Priority");
                                        product = new BrandModelClass.Product(code, brandName, slideId, fileName, slidePriority, false);
                                        productArrayList.add(product);
                                    }
                                    break;
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
                        productsList.add(new BrandModelClass.Product(brandProductArrayList.get(i).getBrandCode(), brandProductArrayList.get(i).getBrandName(), brandProductArrayList.get(i).getProductArrayList().get(j).getSlideId(), brandProductArrayList.get(i).getProductArrayList().get(j).getSlideName(), brandProductArrayList.get(i).getProductArrayList().get(j).getPriority(), brandProductArrayList.get(i).getProductArrayList().get(j).isImageSelected()));
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
