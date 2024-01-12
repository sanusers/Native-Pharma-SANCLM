package saneforce.santrip.activity.homeScreen.call.adapter.detailing;


import static android.Manifest.permission.READ_MEDIA_AUDIO;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.READ_MEDIA_VIDEO;
import static androidx.core.content.ContextCompat.startActivity;
import static saneforce.santrip.activity.homeScreen.call.DCRCallActivity.arrayStore;
import static saneforce.santrip.activity.homeScreen.call.fragments.DetailedFragment.callDetailingLists;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
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
import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Stream;

import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.call.pojo.detailing.CallDetailingList;
import saneforce.santrip.activity.homeScreen.call.pojo.detailing.StoreImageTypeUrl;
import saneforce.santrip.activity.presentation.SupportClass;
import saneforce.santrip.activity.presentation.createPresentation.BrandModelClass;
import saneforce.santrip.commonClasses.CommonSharedPreference;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.databinding.ActivityPlaySlidePreviewDetailingBinding;
import saneforce.santrip.response.LoginResponse;
import saneforce.santrip.storage.SQLite;

public class PlaySlideDetailing extends AppCompatActivity {

    ActivityPlaySlidePreviewDetailingBinding binding;
    PlaySlideDetailedAdapter itemsPagerAdapter;
    BottomPreviewDetailedAdapter bottomPreviewDetailedAdapter;
    ArrayList<BrandModelClass.Product> arrayList = new ArrayList<>();
    boolean playBtnClicked = false;
    MediaController mediaController;
    double progress = 0;
    String startT, endT;
    int SelectedPos;
    int val = 0;
    String defaultTime = "00:00:00";
    LoginResponse loginResponse;
    CommonSharedPreference mCommonSharedPreference;
    ArrayList<StoreImageTypeUrl> dummyarr = new ArrayList<>();
    String finalPrdNam;
    SQLite sqLite;
    public static String SfType, SfCode, SfName, DivCode, Designation, StateCode, SubDivisionCode;

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPlaySlidePreviewDetailingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        mCommonSharedPreference = new CommonSharedPreference(this);
        sqLite = new SQLite(this);
        initialisation();

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
            binding.upArrow.setVisibility(View.GONE);
            binding.bottomLayout.setVisibility(View.VISIBLE);
            binding.closeBtn.setVisibility(View.GONE);
            bottomPreviewDetailedAdapter.notifyDataSetChanged();
        });

        binding.closeBtn.setOnClickListener(view -> {
            callDetailingLists = new ArrayList<>();
            arrayStore.clear();

            getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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
                    Log.v("total_printing", PlaySlideDetailedAdapter.storingSlide.get(i).getSlideName() + "size" + PlaySlideDetailedAdapter.storingSlide.size() + "slide" + PlaySlideDetailedAdapter.storingSlide.get(i).getIndexVal());
                }

                for (int i = 0; i < PlaySlideDetailedAdapter.storingSlide.size(); i++) {
                    int ll = 0;
                    if (i != 0)
                        ll = i - 1;
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
                    String prdScribble = mCommonSharedPreference.getValueFromPreferenceFeed("slide_scribble" + i);
                    String BrandName = mCommonSharedPreference.getValueFromPreferenceFeed("brd_nam" + i);
                    String BrandCode = mCommonSharedPreference.getValueFromPreferenceFeed("brd_code" + i);
                    String slidetyp = mCommonSharedPreference.getValueFromPreferenceFeed("slide_typ" + i);
                    String slideur = mCommonSharedPreference.getValueFromPreferenceFeed("slide_url" + i);
                    Log.v("slideData", BrandName + " time_val " + SlideName + "----" + prdScribble);
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
                            Log.v("slideData", "---000----" + "----" + BrandName + "----" + SlideName);
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
                        Log.v("slideData", jsonArray.toString() + "----" + BrandName + "----" + SlideName);
                        arrayStore.add(new StoreImageTypeUrl("", SlideName, slidetyp, slideur, "0", "", jsonArray.toString(), BrandName, BrandCode, false));
                    }
                }

                Collections.sort(arrayStore, new StoreImageTypeUrl.StoreImageComparator());

                for (int j = 0; j < arrayStore.size(); j++) {
                    if (j == 0) {

                        gettingProductStartEndTime(arrayStore.get(j).getRemTime(), j);
                        finalPrdNam = arrayStore.get(j).getBrdName();
                    } else if (finalPrdNam.equalsIgnoreCase(arrayStore.get(j).getBrdName())) {
                    } else {
                        String time = gettingProductStartEndTime(arrayStore.get(j).getRemTime(), j) + " " + gettingProductTiming(arrayStore.get(j - 1).getBrdName());
                        Log.v("printing_all_time", time);
                        callDetailingLists.add(new CallDetailingList(arrayStore.get(j - 1).getBrdName(), arrayStore.get(j - 1).getBrdCode(), arrayStore.get(j - 1).getSlideNam(), arrayStore.get(j - 1).getSlideTyp(), arrayStore.get(j - 1).getSlideUrl(), time, 0, "", CommonUtilsMethods.getCurrentDate()));
                        finalPrdNam = arrayStore.get(j).getBrdName();
                    }
                }

                if (arrayStore.size() > 0) {
                    String time = gettingProductStartEndTime1(arrayStore.get(arrayStore.size() - 1).getRemTime(), arrayStore.size() - 1) + " " + gettingProductTiming(arrayStore.get(arrayStore.size() - 1).getBrdName());
                    callDetailingLists.add(new CallDetailingList(arrayStore.get(arrayStore.size() - 1).getBrdName(), arrayStore.get(arrayStore.size() - 1).getBrdCode(), arrayStore.get(arrayStore.size() - 1).getSlideNam(), arrayStore.get(arrayStore.size() - 1).getSlideTyp(), arrayStore.get(arrayStore.size() - 1).getSlideUrl(), time, 0, "", CommonUtilsMethods.getCurrentDate()));
                }
            }
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
                            if (!filePath.isEmpty()) {
                                binding.webView.loadUrl("file://" + filePath);
                            }
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

    public String gettingProductStartEndTime1(String jsonvalue, int i) {
        String finalTime = null;
        StoreImageTypeUrl mm, mm1, mm2;
        try {
            JSONArray json = new JSONArray(jsonvalue);
            JSONArray json2 = new JSONArray(jsonvalue);
            mm = arrayStore.get(i);
            json = new JSONArray(mm.getRemTime());
            JSONObject jjj = json.getJSONObject(0);
            Log.v("last_value_time", jjj.getString("sT"));
            startT = jjj.getString("sT");
            //  finalTime = startT + " " + jjj.getString("eT");
            finalTime = startT;
            if (i == arrayStore.size() - 1) {
                mm1 = arrayStore.get(arrayStore.size() - 1);
                json = new JSONArray(mm1.getRemTime());
                JSONObject jj = json.getJSONObject(0);
                endT = jj.getString("eT");
                for (int j = 0; j < i; j++) {
                    if (arrayStore.get(j).getBrdName().equals(mm1.getBrdName())) {
                        mm2 = arrayStore.get(j);
                        json2 = new JSONArray(mm2.getRemTime());
                        JSONObject jj2 = json2.getJSONObject(0);
                        startT = jj2.getString("sT");
                        break;
                    }
                }
                finalTime = startT;
            }
            return finalTime;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return finalTime;
    }


    public String gettingProductTiming(String BrandName) {
        String maxTime = null;
        String minTime = null;
        dummyarr.clear();
        try {
            for (int i = 0; i < arrayStore.size(); i++) {
                if (arrayStore.get(i).getBrdName().equalsIgnoreCase(BrandName)) {
                    dummyarr.add(new StoreImageTypeUrl(arrayStore.get(i).getScribble(), arrayStore.get(i).getSlideNam(), arrayStore.get(i).getSlideTyp(), arrayStore.get(i).getSlideUrl(), arrayStore.get(i).getRemTime(), arrayStore.get(i).getSlideComments(), arrayStore.get(i).getTiming()));
                }
            }
            ArrayList<String> timesMax = new ArrayList<>();
            ArrayList<String> timesMin = new ArrayList<>();
            for (int i1 = 0; i1 < dummyarr.size(); i1++) {
                StoreImageTypeUrl mm1 = dummyarr.get(i1);
                JSONArray jj = new JSONArray(mm1.getTiming());

                if (jj.length() > 0) {
                    JSONObject jsr = jj.getJSONObject(jj.length() - 1);
                    timesMax.add(jsr.getString("eT"));
                    timesMin.add(jsr.getString("sT"));
                }
            }
            String timesMaxnew = timesMax.toString().replace("[", "").replace("]", "");
            String timesMinnew = timesMin.toString().replace("[", "").replace("]", "");

            String[] allTimesMax = timesMaxnew.replaceAll(" ", "").split(",");
            String[] allTimesMin = timesMinnew.replaceAll(" ", "").split(",");

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                maxTime = Stream.of(allTimesMax).max(String::compareTo).get();
                minTime = Stream.of(allTimesMin).min(String::compareTo).get();
            }

            return maxTime;
        } catch (Exception ignored) {
        }
        return maxTime;
    }

    public String gettingProductStartEndTime(String jsonvalue, int i) {
        String finalTime = null;
        StoreImageTypeUrl mm, mm1;
        try {
            JSONArray json;
            if (i != 0) {

                mm1 = arrayStore.get(i - 1);
                json = new JSONArray(mm1.getRemTime());
                JSONObject jj = json.getJSONObject(0);
                endT = jj.getString("eT");
            }
            finalTime = startT;
            mm = arrayStore.get(i);
            json = new JSONArray(mm.getRemTime());
            JSONObject jj = json.getJSONObject(0);
            Log.v("last_value_timemid", jj.getString("sT"));
            startT = jj.getString("sT");
            if (arrayStore.size() == 1) {
                mm = arrayStore.get(i);
                json = new JSONArray(mm.getRemTime());
                JSONObject jjj = json.getJSONObject(0);
                Log.v("last_value_time", jjj.getString("sT"));
                startT = jjj.getString("sT");
                finalTime = startT;
            }
            return finalTime;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return finalTime;
    }

    public int checkForProduct(String slidename) {
        for (int i = 0; i < arrayStore.size(); i++) {
            if (arrayStore.get(i).getSlideNam().equals(slidename)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= 33) {
            if (ContextCompat.checkSelfPermission(this, READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(PlaySlideDetailing.this, READ_MEDIA_AUDIO)
                    != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(PlaySlideDetailing.this, READ_MEDIA_VIDEO)
                    != PackageManager.PERMISSION_GRANTED) {
                CommonUtilsMethods.RequestPermissions(this, new String[]{READ_MEDIA_IMAGES, READ_MEDIA_AUDIO, READ_MEDIA_VIDEO}, false);
            }
        } else {
            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            } else {
                CommonUtilsMethods.RequestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, false);
            }
        }
    }

    public boolean checkForLastSlide(int k) {
        return k == val - 1;
    }

    public String findingEndTime(int k) {
        if (checkForLastSlide(k)) {
            return defaultTime;
        } else {
            int j = k + 1;
            return mCommonSharedPreference.getValueFromPreferenceFeed("timeVal" + j);
        }
    }

    public void initialisation() {
        loginResponse = new LoginResponse();
        loginResponse = sqLite.getLoginData();

        SfType = loginResponse.getSf_type();
        SfCode = loginResponse.getSF_Code();
        SfName = loginResponse.getSF_Name();
        DivCode = loginResponse.getDivision_Code();
        SubDivisionCode = loginResponse.getSubdivision_code();
        Designation = loginResponse.getDesig();
        StateCode = loginResponse.getState_Code();

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
        binding.pdfView.fromFile(new File(fileName))
                .defaultPage(0)
                .enableSwipe(true)
                .swipeHorizontal(false)
                .enableAnnotationRendering(true)
                .scrollHandle(new DefaultScrollHandle(this))
                .load();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }



}