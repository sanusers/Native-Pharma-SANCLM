package saneforce.sanzen.activity.call.fragments.signature;

import static saneforce.sanzen.activity.call.DCRCallActivity.isFromActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Path;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.request.transition.Transition;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import saneforce.sanzen.AWS.AWSBucketsSign;
import saneforce.sanzen.AWS.S3DownloadFiles;
import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.pojo.CallSignCaptureImageList;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.FragmentSignatureBinding;
import saneforce.sanzen.roomdatabase.CallOfflineSignTableDetails.CallOfflineSignDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class SignatureFragment1 extends Fragment {
    public SignatureCanvas signatureCanvas;
    public static ArrayList<CallSignCaptureImageList> callSignCaptureImage;
    public FragmentSignatureBinding signatureBinding;
    public Button clearButton;
    public static String imageName = "";
    public static String filePath = "";

    public String id;
    public RoomDB roomDb;
    public Context context;
    boolean delete;
    CallOfflineSignDataDao callOfflineSignDataDao;
    RoomDB roomDB;
    CommonUtilsMethods commonUtilsMethods;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        roomDB = RoomDB.getDatabase(context);
        callOfflineSignDataDao = roomDB.callOfflineSignDataDao();
        commonUtilsMethods = new CommonUtilsMethods(context);
        Log.d("SignatureFragment", "onAttach()");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("SignatureFragment", "onCreate()");
        callSignCaptureImage = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d("SignatureFragment", "onCreateView()");
        signatureBinding = FragmentSignatureBinding.inflate(inflater, container, false);
        View view = signatureBinding.getRoot();
        if (signatureCanvas == null) {
            signatureCanvas = signatureBinding.signLyt;
        }
        clearButton = view.findViewById(R.id.clr_btn);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        clearButton.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                clearSignature();
            }
        });
        if (savedInstanceState != null) {
            callSignCaptureImage = savedInstanceState.getParcelableArrayList("SIGNATURE");

            Log.d("SignatureFragment", "onCreate: Restored list size: " + (callSignCaptureImage != null ? callSignCaptureImage.size() : 0));
        } else {
            callSignCaptureImage = new ArrayList<>();
            Log.d("SignatureFragment", "onCreate: Creating new list.");
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        int position = 0;

        switch (isFromActivity) {
            case "new":
                if (imageName == null) {
                    signatureCanvas.clearCanvas();
                }
                break;
            case "edit_online":
                if ((!imageName.isEmpty()  /*|| !filePath.isEmpty()*/)) {
                    if (UtilityClass.isNetworkAvailable(context)) {
                        if (SharedPref.getS3BucketNeed(context).equalsIgnoreCase("0")) {
                            loadImageFromS3(imageName);
                        } else {
                            loadImageFromGlide(imageName);
                        }
                    }
                } else {
                    Log.d("edit_online signFrag", "onResume: " + "imageName or filePAth is empty");
                }
                break;
            case "edit_local":
                if (/*!filePath.isEmpty() &&*/ !imageName.isEmpty()) {
                    loadImageFromLocal(imageName);
                } else {
                    Log.d("Edit local SignFrag", "onResume: " + "Filepath is empty");
                }
                break;
        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        ArrayList<CallSignCaptureImageList> parcelableList = new ArrayList<>();
        if (callSignCaptureImage != null) {
            for (CallSignCaptureImageList item : callSignCaptureImage) {
                parcelableList.add(0, new CallSignCaptureImageList(item.getId(), item.getImg_Name(), item.getFilepath(), item.getSign_view(), item.isNewlyAdded()));
            }
            outState.putParcelableArrayList("Signature", parcelableList);
            Log.d("SignatureFragment", "onSaveInstanceState: Saving list size: " + callSignCaptureImage.size());
        }
    }

    public void clearSignature() {
        if (signatureCanvas != null) {
            if(callSignCaptureImage != null && !callSignCaptureImage.isEmpty()) {
                File fileDelete = new File(callSignCaptureImage.get(0).getFilepath());
                if (fileDelete.exists()) {
                    if (fileDelete.delete()) {
                        callOfflineSignDataDao.deleteSignDataByImageName(callSignCaptureImage.get(0).getImg_Name());
                        callSignCaptureImage.clear();
                    }
                }
            } else {
                callSignCaptureImage = new ArrayList<>();
            }
            signatureCanvas.clearCanvas();
            signatureCanvas.setSignaturePath(new Path());
            signatureCanvas.count = 0;
        }
    }

    public void getSignatureBitmap() {
        if (signatureCanvas != null && callSignCaptureImage != null) {
            String newFilePath = signatureCanvas.saveSignature();
            if (newFilePath != null && !newFilePath.isEmpty()) {
                Bitmap newSign = BitmapFactory.decodeFile(newFilePath);

                if (!callSignCaptureImage.isEmpty()) {
                    String originalFilePath = callSignCaptureImage.get(0).getFilepath();

                    if (originalFilePath != null && !originalFilePath.isEmpty() && !originalFilePath.equals(newFilePath)) {
                        File originalFile = new File(originalFilePath);
                        if (originalFile.exists()) {
                            delete = originalFile.delete();
                            Log.d("dao", "getSignatureBitmap: " + originalFilePath);
                            callOfflineSignDataDao.deleteOfflineSignImage(originalFilePath);
                            if (originalFile.delete()) {
                                Log.d("SignatureFlow", "Original (loaded from local) signature deleted: " + originalFilePath);
                            } else {
                                Log.e("SignatureFlow", "Failed to delete original (loaded from local) signature: " + originalFilePath);
                            }
                        }
                    }
                }
                callSignCaptureImage.clear();
                callSignCaptureImage.add(0, new CallSignCaptureImageList(id, signatureCanvas.imageName, newFilePath, newSign, true));
                Log.d("SignatureFlow", "Modified signature saved at: " + newFilePath + ", list size: " + callSignCaptureImage.size());

            } else {
                Log.d("SignatureFlow", "Modified signature save failed or returned empty path.");
            }
        } else {
            Log.d("SignatureFlow", "Canvas is NULL");
        }
    }

    public void loadImageFromS3(String fileName) {
        if (!fileName.contains("null")) {
            File file = new File(context.getExternalFilesDir(null) + "/Signature/", fileName);
            new AWSBucketsSign(context, fileName, file, 0, "", new S3DownloadFiles() {
                @Override
                public void fileDataAdd(int pos, Bitmap bitmap) {

                    if (bitmap != null && fileName != null) {
                        Log.d("S3ImageLoad", "Image successfully loaded from S3: " + fileName);
                        try (FileOutputStream fos = new FileOutputStream(file)) {
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fos);
                            Log.d("S3ImageLoad", "Image stored locally at: " + file.getAbsolutePath());
                            if (!fileName.contains("null")) {
                                callSignCaptureImage.add(0, new CallSignCaptureImageList(file.getAbsolutePath(), fileName));
                            }
                        } catch (Exception e) {
                            Log.e("S3ImageLoad", "Error saving image locally: " + e.getMessage());
                        }
                        signatureCanvas.setBackgroundBitmap(bitmap);
                    } else {
                        Log.e("S3ImageLoad", "Failed to load image from S3: " + fileName + ", bitmap is null.");

                    }

                }

                @Override
                public void onFailure(int pos) {
                    Log.e("S3ImageLoad", "Failed to load image from S3: " + fileName + ", bitmap is null.");
                    commonUtilsMethods.showToastMessage(context, context.getString(R.string.image_not_found), true);
                }
            });
        } else {
            loadImageFromLocal(imageName);
        }
    }

    public void loadImageFromGlide(String fileName) {
        if (fileName != null && !fileName.equalsIgnoreCase("null")) {
            File file = new File(context.getExternalFilesDir(null) + "/Signature/", fileName);

            String imageUrl = SharedPref.getTagImageUrl(context) + "signs/" + fileName;
            Log.d("TAG", "loadImageFromGlide: "+imageUrl);
            Glide.with(context)
                    .asBitmap()
                    .load(imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.NONE) // cache original & resized
                    .skipMemoryCache(false) // allow memory caching
                    .listener(new RequestListener<Bitmap>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                    Target<Bitmap> target, boolean isFirstResource) {
                            Log.e("GlideError", "Load failed: " + e.getMessage());
                            return false;
                        }
                        @Override
                        public boolean onResourceReady(Bitmap resource, Object model,
                                                       Target<Bitmap> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .into(new CustomTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(@NonNull Bitmap bitmap, @Nullable Transition<? super Bitmap> transition) {
                            if (bitmap != null) {
                                Log.d("GlideImageLoad", "Image successfully loaded from Glide: " + fileName);
//                                try (FileOutputStream fos = new FileOutputStream(file)) {
//                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//                                    if (!fileName.equalsIgnoreCase("null")) {
//                                        callSignCaptureImage.add(0, new CallSignCaptureImageList(id, imageName, file.getAbsolutePath(), bitmap, false));
//                                    }
//                                    Log.d("GlideImageLoad", "Image stored locally at: " + file.getAbsolutePath());
//                                } catch (Exception e) {
//                                    Log.e("GlideImageLoad", "Error saving image locally: " + e.getMessage());
//                                }
                                signatureCanvas.setBackgroundBitmap(bitmap);
                            } else {
                                Log.e("GlideImageLoad", "Failed to load image via Glide: " + fileName + ", bitmap is null.");
                            }
                        }

                        @Override
                        public void onLoadCleared(@Nullable Drawable placeholder) {
                            // Optional: handle placeholder cleanup if needed
                        }
                    });
        } else {
            loadImageFromLocal(imageName);
        }
    }

    public void loadImageFromLocal(String fileName) {
        if (/*!filePath.isEmpty() && */!imageName.isEmpty()) {
            File file = new File(context.getExternalFilesDir(null) + "/Signature/", fileName);
//            File file = new File(callSignCaptureImage.get(0).getFilepath());
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            signatureCanvas.setBackgroundBitmap(bitmap);
            if (!imageName.contains("null")) {
                callSignCaptureImage.add(0, new CallSignCaptureImageList(id, imageName, file.getAbsolutePath(), bitmap, false));
            }
            Log.d("SignatureFlow", "Loaded image from local: " + fileName);
        } else {
            Log.d("TAG", "instance initializer: file path is empty");
        }
    }
/*    public void loadImageFromLocal() {
        if (!filePath.isEmpty()) {
            File file = new File(filePath);
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            signatureCanvas.setBackgroundBitmap(bitmap);
            if (imageName != null) {
                callSignCaptureImageLists.clear();
                callSignCaptureImageLists.add(0, new CallSignCaptureImageList(id, imageName, filePath, bitmap, false));
            } else {
                callSignCaptureImageLists.get(0);
            }
            Log.d("SignatureFlow", "Loaded image from local: " + filePath + ", list size: " + callSignCaptureImageLists.size());
        }else{
            Log.d("TAG", "instance initializer: "+"the file path is empty");
        }
    }*/
}

