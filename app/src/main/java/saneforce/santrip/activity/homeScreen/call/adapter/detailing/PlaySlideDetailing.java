package saneforce.santrip.activity.homeScreen.call.adapter.detailing;


import static android.Manifest.permission.READ_MEDIA_AUDIO;
import static android.Manifest.permission.READ_MEDIA_IMAGES;
import static android.Manifest.permission.READ_MEDIA_VIDEO;

import static saneforce.santrip.activity.previewPresentation.fragment.BrandMatrix.SlideBrandMatrixList;
import static saneforce.santrip.activity.previewPresentation.fragment.Customized.SlideCustomizedList;
import static saneforce.santrip.activity.previewPresentation.fragment.HomeBrands.SlideHomeBrandList;
import static saneforce.santrip.activity.previewPresentation.fragment.Speciality.SlideSpecialityList;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.MediaController;
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
import saneforce.santrip.activity.presentation.SupportClass;
import saneforce.santrip.activity.presentation.createPresentation.BrandModelClass;
import saneforce.santrip.commonClasses.CommonSharedPreference;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.databinding.ActivityPlaySlidePreviewDetailingBinding;
import saneforce.santrip.response.LoginResponse;
import saneforce.santrip.storage.SQLite;

public class PlaySlideDetailing extends AppCompatActivity {

    @SuppressLint("StaticFieldLeak")
    public static ActivityPlaySlidePreviewDetailingBinding binding;
    @SuppressLint("StaticFieldLeak")
    public static PlaySlideDetailedAdapter itemsPagerAdapter;
    @SuppressLint("StaticFieldLeak")
    public static BottomPreviewDetailedAdapter bottomPreviewDetailedAdapter;
    BottomLayoutHeadAdapter bottomPreviewDetailedHeadAdapter;
    public static ArrayList<BrandModelClass.Product> arrayList = new ArrayList<>();
    public static ArrayList<String> headingData = new ArrayList<>();
    boolean playBtnClicked = false;
    MediaController mediaController;
    double progress = 0;
    int SelectedPos;
    LoginResponse loginResponse;
    CommonSharedPreference mCommonSharedPreference;
    @SuppressLint("StaticFieldLeak")
    public static Context context;
    SQLite sqLite;
    public static String SfType, SfCode, SfName, DivCode, Designation, StateCode, SubDivisionCode, SpecialityCodePlay, MappedBrandsPlay, MappedSlidesPlay;

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
    }

    //To Hide the bottomNavigation When popup
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            binding.getRoot().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
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
        context = this;
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
            if ((ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) && ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                CommonUtilsMethods.RequestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE}, false);
            }
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

        bottomPreviewDetailedHeadAdapter = new BottomLayoutHeadAdapter(PlaySlideDetailing.this, headingData, sqLite);
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

    public static void populateViewPagerAdapterNew(ArrayList<BrandModelClass.Product> productsList) {
        itemsPagerAdapter = new PlaySlideDetailedAdapter((PlaySlideDetailing) context, productsList);
        binding.viewPager.setAdapter(itemsPagerAdapter);
    }

    public void populateBottomViewAdapter() {
        bottomPreviewDetailedAdapter = new BottomPreviewDetailedAdapter(PlaySlideDetailing.this, arrayList, binding.viewPager);
        LinearLayoutManager layoutManager = new LinearLayoutManager(PlaySlideDetailing.this, LinearLayoutManager.HORIZONTAL, false);
        binding.recView.setLayoutManager(layoutManager);
        binding.recView.setAdapter(bottomPreviewDetailedAdapter);
    }

    public static void populateBottomViewAdapterNew(ArrayList<BrandModelClass.Product> productsList) {
        bottomPreviewDetailedAdapter = new BottomPreviewDetailedAdapter((PlaySlideDetailing) context, productsList, binding.viewPager);
        LinearLayoutManager layoutManager = new LinearLayoutManager((PlaySlideDetailing) context, LinearLayoutManager.HORIZONTAL, false);
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


    public static class BottomLayoutHeadAdapter extends RecyclerView.Adapter<BottomLayoutHeadAdapter.MyViewHolder> {
        Context context;
        List<String> arrayListHead;
        SQLite sqLite;
        public static int SelectedPosPlay;

        public BottomLayoutHeadAdapter(Context context, List<String> arrayListHead, SQLite sqLite) {
            this.context = context;
            this.arrayListHead = arrayListHead;
            this.sqLite = sqLite;
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
                        //populateRequiredData("A");
                        populateListData(SlideHomeBrandList);
                        break;
                    case "B":
                        //populateRequiredData("B");
                        populateListData(SlideBrandMatrixList);
                        break;
                    case "C":
                        //populateRequiredData("C");
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
                    productsList.add(new BrandModelClass.Product(brandProductArrayList.get(i).getBrandCode(), brandProductArrayList.get(i).getBrandName(), brandProductArrayList.get(i).getProductArrayList().get(j).getSlideId()
                            , brandProductArrayList.get(i).getProductArrayList().get(j).getSlideName(), brandProductArrayList.get(i).getProductArrayList().get(j).getPriority(), brandProductArrayList.get(i).getProductArrayList().get(j).isImageSelected()));
                }
            }

            if (productsList.size() > 0) {
                binding.constraintNoData.setVisibility(View.GONE);
            } else {
                binding.constraintNoData.setVisibility(View.VISIBLE);
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
                        productsList.add(new BrandModelClass.Product(savedPresentation.get(i).getPresentationName(), savedPresentation.get(i).getProducts().get(j).getBrandName(), savedPresentation.get(i).getProducts().get(j).getBrandCode(), savedPresentation.get(i).getProducts().get(j).getSlideId()
                                , savedPresentation.get(i).getProducts().get(j).getSlideName(), savedPresentation.get(i).getProducts().get(j).getPriority(), savedPresentation.get(i).getProducts().get(j).isImageSelected()));
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
                JSONArray prodSlide = sqLite.getMasterSyncDataByKey(Constants.PROD_SLIDE);
                JSONArray brandSlide = sqLite.getMasterSyncDataByKey(Constants.BRAND_SLIDE);

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
                        productsList.add(new BrandModelClass.Product(brandProductArrayList.get(i).getBrandCode(), brandProductArrayList.get(i).getBrandName(), brandProductArrayList.get(i).getProductArrayList().get(j).getSlideId()
                                , brandProductArrayList.get(i).getProductArrayList().get(j).getSlideName(), brandProductArrayList.get(i).getProductArrayList().get(j).getPriority(), brandProductArrayList.get(i).getProductArrayList().get(j).isImageSelected()));
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
