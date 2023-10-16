package saneforce.sanclm.activity.homeScreen.call.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.call.adapter.jwOthers.AdapterCallCaptureImage;
import saneforce.sanclm.activity.homeScreen.call.adapter.jwOthers.AdapterCallJointWorkList;
import saneforce.sanclm.activity.homeScreen.call.pojo.CallCaptureImageList;
import saneforce.sanclm.activity.homeScreen.call.pojo.CallCommonCheckedList;


public class JWOthersFragment extends Fragment {
    public static ArrayList<CallCaptureImageList> callCaptureImageLists;
    public static ArrayList<CallCommonCheckedList> callAddedJointList;
    AdapterCallJointWorkList adapterCallJointWorkList;
    AdapterCallCaptureImage adapterCallCaptureImage;
    Uri outputFileUri;
    String filePath = "", imageName = "";
    RecyclerView rv_image_capture, rv_jointwork;
    TextView btn_add_capture, btn_add_jointwork;

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1888 && resultCode == Activity.RESULT_OK) {
            int captureSize = callCaptureImageLists.size() + 1;
          //  imageName = "img_capture_DCR_" + captureSize;
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            callCaptureImageLists.add(0,new CallCaptureImageList("", "", photo));

            adapterCallCaptureImage = new AdapterCallCaptureImage(getActivity(), callCaptureImageLists);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
            rv_image_capture.setLayoutManager(mLayoutManager);
            rv_image_capture.setItemAnimator(new DefaultItemAnimator());
            rv_image_capture.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
            rv_image_capture.setAdapter(adapterCallCaptureImage);


          /*  String finalPath = "/storage/emulated/0";
            filePath = outputFileUri.getPath();
            filePath = filePath.substring(1);
            filePath = finalPath + filePath.substring(filePath.indexOf("/"));

            Matrix mat = new Matrix();
            mat.postRotate(Integer.parseInt("270"));
            Bitmap bMapRotate = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), mat, true);*/


            // imageView.setImageBitmap(photo);

        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_jwothers, container, false);

        Log.v("fragment", "jointworks");
        rv_image_capture = v.findViewById(R.id.rv_img_capture);
        rv_jointwork = v.findViewById(R.id.rv_jointwork);
        btn_add_capture = v.findViewById(R.id.btn_add_img_capture);
        btn_add_jointwork = v.findViewById(R.id.btn_add_jw);


        DummyAdapter();


        btn_add_capture.setOnClickListener(view -> {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 5);
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    captureFile();
                } else captureFileLower();
            }
          /*  Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_REQUEST);*/
        });
        return v;
    }

    private void DummyAdapter() {
        adapterCallCaptureImage = new AdapterCallCaptureImage(getActivity(), callCaptureImageLists);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        rv_image_capture.setLayoutManager(mLayoutManager);
        rv_image_capture.setItemAnimator(new DefaultItemAnimator());
        rv_image_capture.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        rv_image_capture.setAdapter(adapterCallCaptureImage);


        adapterCallJointWorkList = new AdapterCallJointWorkList(getActivity(), callAddedJointList);
        RecyclerView.LayoutManager mLayoutManagerJW = new LinearLayoutManager(getActivity());
        rv_jointwork.setLayoutManager(mLayoutManagerJW);
        rv_jointwork.setItemAnimator(new DefaultItemAnimator());
        rv_jointwork.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        rv_jointwork.setAdapter(adapterCallJointWorkList);
    }


    public void captureFile() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
      /*   Uri outputFileUri = Uri.fromFile(new File(getExternalCacheDir().getPath(), "pickImageResult.jpeg"));
        outputFileUri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName() + ".fileprovider", new File(getContext().getExternalCacheDir().getPath(), "1234.jpeg"));

        Log.v("priniting_uri", " output " + outputFileUri.getPath());
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);*/

        startActivityForResult(intent, 1888);
    }

    public void captureFileLower() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Uri outputFileUri = Uri.fromFile(new File(getExternalCacheDir().getPath(), "pickImageResult.jpeg"));
        //outputFileUri = FileProvider.getUriForFile(FeedbackActivity.this, getApplicationContext().getPackageName() + ".fileprovider", new File(getExternalCacheDir().getPath(), "pickImageResult"+System.currentTimeMillis()+".jpeg"));
        //Log.v("priniting_uri",outputFileUri.toString()+" output "+outputFileUri.getPath()+" raw_msg "+getExternalCacheDir().getPath());
        //content://com.saneforce.sbiapplication.fileprovider/shared_video/Android/data/com.saneforce.sbiapplication/cache/pickImageResult.jpeg
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(intent, 19);
    }
}
