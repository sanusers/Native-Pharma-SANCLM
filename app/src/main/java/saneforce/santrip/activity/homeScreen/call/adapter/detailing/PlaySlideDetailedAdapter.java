package saneforce.santrip.activity.homeScreen.call.adapter.detailing;

import static saneforce.santrip.activity.homeScreen.call.DCRCallActivity.arrayStore;
import static saneforce.santrip.activity.homeScreen.call.adapter.detailing.PlaySlideDetailing.Designation;
import static saneforce.santrip.activity.homeScreen.call.adapter.detailing.PlaySlideDetailing.DivCode;
import static saneforce.santrip.activity.homeScreen.call.adapter.detailing.PlaySlideDetailing.SfCode;
import static saneforce.santrip.activity.homeScreen.call.adapter.detailing.PlaySlideDetailing.SfType;
import static saneforce.santrip.activity.homeScreen.call.adapter.detailing.PlaySlideDetailing.StateCode;
import static saneforce.santrip.activity.homeScreen.call.adapter.detailing.PlaySlideDetailing.SubDivisionCode;
import static saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.TodayPlanSfCode;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.github.barteksc.pdfviewer.BuildConfig;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.call.pojo.detailing.LoadBitmap;
import saneforce.santrip.activity.homeScreen.call.pojo.detailing.StoreImageTypeUrl;
import saneforce.santrip.activity.presentation.SupportClass;
import saneforce.santrip.activity.presentation.createPresentation.BrandModelClass;
import saneforce.santrip.commonClasses.CommonSharedPreference;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;
import saneforce.santrip.storage.SharedPref;

public class PlaySlideDetailedAdapter extends PagerAdapter {

    public static ArrayList<LoadBitmap> storingSlide = new ArrayList<>();
    public static int presentSlidePos;
    public static boolean preVal = false;
    private final Context context;
    private final ArrayList<BrandModelClass.Product> productArrayList;
    ArrayList<StoreImageTypeUrl> slideDescribe = new ArrayList<>();
    ArrayList<StoreImageTypeUrl> slideScribble = new ArrayList<>();
    Object objsd;
    ImageView imageView;
    String startT, endT;
    PlaySlideDetailing act;
    String slideUrl1 = null;
    Dialog dialogPopUp;
    ApiInterface apiService;
    CommonSharedPreference mCommonSharedPreference;
    String finalPrdNam;
    ArrayList<StoreImageTypeUrl> dummyarr = new ArrayList<>();
    int val = 0;
    String defaultTime = "00:00:00";
    int scribblePos;
    CommonUtilsMethods commonUtilsMethods;

    public PlaySlideDetailedAdapter(PlaySlideDetailing context, ArrayList<BrandModelClass.Product> productArrayList) {
        this.context = context;
        this.productArrayList = productArrayList;
        slideDescribe.clear();
        act = context;
        mCommonSharedPreference = new CommonSharedPreference(context);
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

    @SuppressLint("ClickableViewAccessibility")
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View sliderLayout = inflater.inflate(R.layout.presentation_preview_item, null);

        imageView = sliderLayout.findViewById(R.id.imageView);
        RelativeLayout rl_rightView = sliderLayout.findViewById(R.id.rightArrow);
        rl_rightView.setVisibility(View.VISIBLE);

        getFromFilePath(productArrayList.get(position).getSlideName(), imageView);
        container.addView(sliderLayout);


        rl_rightView.setOnClickListener(v -> {
            File file = new File(context.getExternalFilesDir(null) + "/Slides/", productArrayList.get(position).getSlideName());
            String fileFormat = SupportClass.getFileExtension(productArrayList.get(position).getSlideName());
            popupScribbling(productArrayList.get(position).getSlideName(), productArrayList.get(position).getSlideId(), file.toString(), fileFormat);
        });

        return sliderLayout;
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

        img_close.setOnClickListener(view -> dialog.dismiss());

        erase.setOnClickListener(view -> {
            paintviews.erase();
        });

        submit.setOnClickListener(view -> captureCanvasScreen(slideName, canvas_lay, dialog, path));
        sq.setOnClickListener(view -> paintviews.addRectangle());
        cir.setOnClickListener(view -> paintviews.addCircle());

        pen_black.setOnClickListener(view -> {
            paintviews.changePaintColor(1);
        });

        pen_red.setOnClickListener(view -> {
            paintviews.changePaintColor(2);
        });

        pen_green.setOnClickListener(view -> {
            paintviews.changePaintColor(3);
        });

        Drawable d = Drawable.createFromPath(path);
        canva_img.setBackground(d);
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
        JSONObject jsonImage = new JSONObject();
        try {
            jsonImage.put("tableName", "uploadscribble");
            jsonImage.put("sfcode", SfCode);
            jsonImage.put("division_code", DivCode);
            jsonImage.put("Rsf", TodayPlanSfCode);
            jsonImage.put("sf_type", SfType);
            jsonImage.put("Designation", Designation);
            jsonImage.put("state_code", StateCode);
            jsonImage.put("subdivision_code", SubDivisionCode);
            Log.v("scribbleUpload", jsonImage.toString());
        } catch (Exception ignored) {
        }


        commonUtilsMethods.ShowToast(context, context.getString(R.string.processing), 100);
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
                            commonUtilsMethods.ShowToast(context, context.getString(R.string.scribble_upload), 100);
                            slideScribble.set(scribblePos, new StoreImageTypeUrl(slideScribble.get(scribblePos).getSlideNam(), slideScribble.get(scribblePos).getSlideid(), slideScribble.get(scribblePos).isLike(), slideScribble.get(scribblePos).isDisLike(), slideScribble.get(scribblePos).getSlideComments(), scribbleFileName));
                            dialog.dismiss();
                        }
                    } catch (Exception e) {
                        dialog.dismiss();
                    }
                } else {
                    dialog.dismiss();
                    commonUtilsMethods.ShowToast(context, context.getString(R.string.something_wrong), 100);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                commonUtilsMethods.ShowToast(context, context.getString(R.string.toast_response_failed), 100);
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

        btnSave.setOnClickListener(v -> {
            slideScribble.set(scribblePos, new StoreImageTypeUrl(slideScribble.get(scribblePos).getSlideNam(), slideScribble.get(scribblePos).getSlideid(), slideScribble.get(scribblePos).isLike(), slideScribble.get(scribblePos).isDisLike(), edt_feed.getText().toString(), slideScribble.get(scribblePos).getScribble()));
            dialog.dismiss();
        });


        btnClear.setOnClickListener(v -> {
            edt_feed.setText("");
            edt_feed.setHint(context.getResources().getString(R.string.type_your_feedback_here));
        });

        img_close.setOnClickListener(view -> {
            edt_feed.setText("");
            edt_feed.setHint(context.getResources().getString(R.string.type_your_feedback_here));
            dialog.dismiss();
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
        if (slideScribble.size() > 0) {
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


        rl_paint.setOnClickListener(view -> popupPaint(slideName, path));

        rl_like.setOnClickListener(v -> {
            if (Objects.equals(rl_like.getBackground().getConstantState(), Objects.requireNonNull(ContextCompat.getDrawable(context, R.drawable.outline_green)).getConstantState())) {
                rl_like.setBackground(ContextCompat.getDrawable(context, R.drawable.green_full));
                rl_dislike.setBackground(ContextCompat.getDrawable(context, R.drawable.outline_pink));
                slideScribble.set(scribblePos, new StoreImageTypeUrl(slideName, slideId, true, false, slideScribble.get(scribblePos).getSlideComments(), slideScribble.get(scribblePos).getScribble()));
            } else {
                rl_like.setBackground(ContextCompat.getDrawable(context, R.drawable.outline_green));
            }
        });

        rl_dislike.setOnClickListener(v -> {
            if (Objects.equals(rl_dislike.getBackground().getConstantState(), Objects.requireNonNull(ContextCompat.getDrawable(context, R.drawable.outline_pink)).getConstantState())) {
                rl_dislike.setBackground(ContextCompat.getDrawable(context, R.drawable.pink_full));
                rl_like.setBackground(ContextCompat.getDrawable(context, R.drawable.outline_green));
                slideScribble.set(scribblePos, new StoreImageTypeUrl(slideName, slideId, false, true, slideScribble.get(scribblePos).getSlideComments(), slideScribble.get(scribblePos).getScribble()));
            } else {
                rl_dislike.setBackground(ContextCompat.getDrawable(context, R.drawable.outline_pink));
            }
        });

        rl_comments.setOnClickListener(v -> commentsPopup());

        rl_share.setOnClickListener(v -> {
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
                case "mp4": {
                    try {
                        ContentValues content = new ContentValues(4);
                        content.put(MediaStore.Video.VideoColumns.DATE_ADDED,
                                System.currentTimeMillis() / 1000);
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
        });

        rl_stop.setOnClickListener(v -> {
            act.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
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
               /* Collections.sort(arrayStore, new StoreImageTypeUrl.StoreImageComparator());

                for (int j = 0; j < arrayStore.size(); j++) {
                    if (j == 0) {

                        gettingProductStartEndTime(arrayStore.get(j).getRemTime(), j);
                        finalPrdNam = arrayStore.get(j).getBrdName();
                    } else if (finalPrdNam.equalsIgnoreCase(arrayStore.get(j).getBrdName())) {
                    } else {
                        String time = gettingProductStartEndTime(arrayStore.get(j).getRemTime(), j) + " " + gettingProductTiming(arrayStore.get(j - 1).getBrdName());
                        Log.v("printing_all_time", time + "----" + time.substring(0, 9));
                        callDetailingLists.add(new CallDetailingList(arrayStore.get(j - 1).getBrdName(), arrayStore.get(j - 1).getBrdCode(), arrayStore.get(j - 1).getSlideNam(), arrayStore.get(j - 1).getSlideTyp(), arrayStore.get(j - 1).getSlideUrl(), time, time.substring(0, 9), 0, "", CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd")));
                        finalPrdNam = arrayStore.get(j).getBrdName();
                    }
                }

                if (arrayStore.size() > 0) {
                    String time = gettingProductStartEndTime1(arrayStore.get(arrayStore.size() - 1).getRemTime(), arrayStore.size() - 1) + " " + gettingProductTiming(arrayStore.get(arrayStore.size() - 1).getBrdName());
                    callDetailingLists.add(new CallDetailingList(arrayStore.get(arrayStore.size() - 1).getBrdName(), arrayStore.get(arrayStore.size() - 1).getBrdCode(), arrayStore.get(arrayStore.size() - 1).getSlideNam(), arrayStore.get(arrayStore.size() - 1).getSlideTyp(), arrayStore.get(arrayStore.size() - 1).getSlideUrl(), time, time.substring(0, 9), 0, "", CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd")));
                }*/
            }
            act.getOnBackPressedDispatcher().onBackPressed();
        });


        params.setMargins(0, 0, 0, 0);
        wlp.gravity = Gravity.CENTER | Gravity.END;
        wlp.flags &= ~WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        window.setAttributes(wlp);
        dialogPopUp.show();
        act.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
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
            return defaultTime;
        } else {
            int j = k + 1;
            return mCommonSharedPreference.getValueFromPreferenceFeed("timeVal" + j);
        }
    }

    public boolean checkForLastSlide(int k) {
        return k == val - 1;
    }

    private Uri getImageToShare(Bitmap bitmap) {
        File imagefolder = new File(context.getCacheDir(), "images");
        Uri uri = null;
        try {
            imagefolder.mkdirs();
            File file = new File(imagefolder, "shared_image.png");
            FileOutputStream outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, outputStream);
            outputStream.flush();
            outputStream.close();
            uri = FileProvider.getUriForFile(context, "saneforce.santrip.fileprovider", file);
        } catch (Exception ignored) {

        }
        return uri;
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        super.setPrimaryItem(container, position, object);
        StoreImageTypeUrl mm = slideDescribe.get(position);
        presentSlidePos = position;
        objsd = object;
        preVal = true;
        storingSlide.add(new LoadBitmap(mm.getScribble(), CommonUtilsMethods.getCurrentInstance("HH:mm:ss"), position, CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd"), mm.getSlideNam(), mm.getSlideTyp(), mm.getSlideUrl(), mm.getBrdName(), mm.getBrdCode()));
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

    public void getFromFilePath(String fileName, ImageView imageView) {

        File file = new File(context.getExternalFilesDir(null) + "/Slides/", fileName);
        if (file.exists()) {
            String fileFormat = SupportClass.getFileExtension(fileName);
            Bitmap bitmap = null;
            switch (fileFormat) {
                case "jpg":
                case "png":
                case "jpeg":
                case "mp4": {
                    Glide.with(context).asBitmap().load(Uri.fromFile(new File(file.getAbsolutePath()))).into(imageView);
                    return;
                }
                case "pdf": {
                    bitmap = SupportClass.pdfToBitmap(file.getAbsoluteFile());
                    Glide.with(context).asBitmap().load(bitmap).into(imageView);
                    slideUrl1 = file.toString();
                    return;
                }
                case "zip": {
                    bitmap = BitmapFactory.decodeFile(SupportClass.getFileFromZip(file.getAbsolutePath(), "image"));
                    if (bitmap != null)
                        Glide.with(context).asBitmap().load(bitmap).into(imageView);
                    return;
                }
                case "gif": {
                    Glide.with(context).asGif().load(new File(file.getAbsolutePath())).into(imageView);
                }
            }
        }
    }
}
