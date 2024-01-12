package saneforce.santrip.activity.presentation.playPreview;

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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.viewpager.widget.PagerAdapter;

import com.bumptech.glide.Glide;
import com.github.barteksc.pdfviewer.BuildConfig;
import com.google.gson.JsonObject;

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
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.network.ApiInterface;
import saneforce.santrip.network.RetrofitClient;
import saneforce.santrip.storage.SharedPref;

public class PlaySlidePagerAdapter extends PagerAdapter {

    private final Context context;
    private final ArrayList<BrandModelClass.Product> productArrayList;

    public PlaySlidePagerAdapter (Context context, ArrayList<BrandModelClass.Product> productArrayList) {
        this.context = context;
        this.productArrayList = productArrayList;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View sliderLayout = inflater.inflate(R.layout.presentation_preview_item, null);

        ImageView imageView = sliderLayout.findViewById(R.id.imageView);
        RelativeLayout rl_rightView = sliderLayout.findViewById(R.id.rightArrow);
        rl_rightView.setVisibility(View.GONE);
        getFromFilePath(productArrayList.get(position).getSlideName(),imageView);
        container.addView(sliderLayout);

        return sliderLayout;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @Override
    public int getCount() {
        return productArrayList.size();
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view == object;
    }

    public void getFromFilePath(String fileName, ImageView imageView){

        File file = new File(context.getExternalFilesDir(null)+ "/Slides/", fileName);
        if (file.exists()){
            String fileFormat = SupportClass.getFileExtension(fileName);
            Bitmap bitmap = null;
            switch (fileFormat){
                case "jpg" :
                case "png" :
                case "jpeg" :
                case "mp4" :{
                    Glide.with(context).asBitmap().load(Uri.fromFile(new File(file.getAbsolutePath()))).into(imageView);
                    return;
                }
                case "pdf" :{
                    bitmap = SupportClass.pdfToBitmap(file.getAbsoluteFile());
                    Glide.with(context).asBitmap().load(bitmap).into(imageView);
                    return;
                }
                case "zip" :{
                    bitmap = BitmapFactory.decodeFile(SupportClass.getFileFromZip(file.getAbsolutePath(),"image"));
                    if (bitmap != null)
                        Glide.with(context).asBitmap().load(bitmap).into(imageView);
                    return;
                }
                case "gif" :{
                    Glide.with(context).asGif().load(new File(file.getAbsolutePath())).into(imageView);
                    return;
                }
            }
        }
    }

}