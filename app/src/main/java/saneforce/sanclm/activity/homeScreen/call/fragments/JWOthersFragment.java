package saneforce.sanclm.activity.homeScreen.call.fragments;

import static saneforce.sanclm.activity.homeScreen.call.DCRCallActivity.dcrcallBinding;
import static saneforce.sanclm.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.SfCode;
import static saneforce.sanclm.activity.homeScreen.call.fragments.JointworkSelectionSide.JwList;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;

import saneforce.sanclm.activity.homeScreen.call.DCRCallActivity;
import saneforce.sanclm.activity.homeScreen.call.adapter.jwOthers.AdapterCallCaptureImage;
import saneforce.sanclm.activity.homeScreen.call.adapter.jwOthers.AdapterCallJointWorkList;
import saneforce.sanclm.activity.homeScreen.call.pojo.CallCaptureImageList;
import saneforce.sanclm.activity.homeScreen.call.pojo.CallCommonCheckedList;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.databinding.FragmentJwothersBinding;
import saneforce.sanclm.storage.SQLite;


public class JWOthersFragment extends Fragment {
    public static ArrayList<CallCaptureImageList> callCaptureImageLists;
    public static ArrayList<CallCommonCheckedList> callAddedJointList;
    @SuppressLint("StaticFieldLeak")
    public static FragmentJwothersBinding jwothersBinding;
    public static String filePath = "", imageName = "";
    @SuppressLint("StaticFieldLeak")
    public static AdapterCallCaptureImage adapterCallCaptureImage;
    public static Uri outputFileUri;
    AdapterCallJointWorkList adapterCallJointWorkList;
    SQLite sqLite;
    CommonUtilsMethods commonUtilsMethods;


    @SuppressLint("NotifyDataSetChanged")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        jwothersBinding = FragmentJwothersBinding.inflate(inflater);
        View v = jwothersBinding.getRoot();
        sqLite = new SQLite(requireContext());
        commonUtilsMethods = new CommonUtilsMethods(getActivity());

        HiddenVisibleFunction();
        SetupAdapter();


        jwothersBinding.tvFeedback.setOnClickListener(view -> {
            dcrcallBinding.fragmentSelectFbSide.setVisibility(View.VISIBLE);
        });

        jwothersBinding.btnAddJw.setOnClickListener(view -> {
            dcrcallBinding.fragmentSelectJwSide.setVisibility(View.VISIBLE);
            HideKeyboard();
        });

        jwothersBinding.tvFeedback.setOnClickListener(view -> {
            HideKeyboard();
            dcrcallBinding.fragmentSelectFbSide.setVisibility(View.VISIBLE);
        });

        jwothersBinding.btnAddImgCapture.setOnClickListener(view -> {
            if (callCaptureImageLists.size() < 2) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, 5);
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        captureFile();
                    } else captureFileLower();
                }
            } else {
                Toast.makeText(getContext(), "Not able to Add more Images", Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }

    private void HideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(jwothersBinding.btnAddJw.getWindowToken(), 0);
    }

    private void HiddenVisibleFunction() {
        if (DCRCallActivity.PobNeed.equalsIgnoreCase("0")) {
            jwothersBinding.constraintPob.setVisibility(View.VISIBLE);
        } else {
            jwothersBinding.constraintPob.setVisibility(View.GONE);
        }

        if (DCRCallActivity.OverallFeedbackNeed.equalsIgnoreCase("0")) {
            jwothersBinding.constraintFeedback.setVisibility(View.VISIBLE);
        } else {
            jwothersBinding.constraintFeedback.setVisibility(View.GONE);
        }

        if (DCRCallActivity.EventCaptureNeed.equalsIgnoreCase("0")) {
            jwothersBinding.constraintCapture.setVisibility(View.VISIBLE);
        } else {
            jwothersBinding.constraintCapture.setVisibility(View.GONE);
        }

        if (DCRCallActivity.JwNeed.equalsIgnoreCase("0")) {
            jwothersBinding.constraintJointWork.setVisibility(View.VISIBLE);
        } else {
            jwothersBinding.constraintJointWork.setVisibility(View.GONE);
        }
    }

    private void SetupAdapter() {
        adapterCallCaptureImage = new AdapterCallCaptureImage(getActivity(), callCaptureImageLists);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        jwothersBinding.rvImgCapture.setLayoutManager(mLayoutManager);
        jwothersBinding.rvImgCapture.setItemAnimator(new DefaultItemAnimator());
        jwothersBinding.rvImgCapture.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        jwothersBinding.rvImgCapture.setAdapter(adapterCallCaptureImage);

        adapterCallJointWorkList = new AdapterCallJointWorkList(getContext(), getActivity(), callAddedJointList, JwList);
        RecyclerView.LayoutManager mLayoutManagerJW = new LinearLayoutManager(getActivity());
        jwothersBinding.rvJointwork.setLayoutManager(mLayoutManagerJW);
        jwothersBinding.rvJointwork.setItemAnimator(new DefaultItemAnimator());
        jwothersBinding.rvJointwork.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
        jwothersBinding.rvJointwork.setAdapter(adapterCallJointWorkList);
    }


    public void captureFile() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        outputFileUri = FileProvider.getUriForFile(requireContext(), getActivity().getPackageName() + ".fileprovider", new File(getContext().getExternalCacheDir().getPath(), SfCode + "_" + DCRCallActivity.CallActivityCustDetails.get(0).getCode() + "_" + CommonUtilsMethods.getCurrentDateDMY().replace("-", "") + CommonUtilsMethods.getCurrentTime().replace(":", "") + ".jpeg"));
        imageName = "E_" + SfCode + DCRCallActivity.CallActivityCustDetails.get(0).getCode() + "_" + CommonUtilsMethods.getCurrentDateDMY().replace("-", "") + CommonUtilsMethods.getCurrentTime().replace(":", "") + ".jpeg";
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        //intent.putExtra(MediaStore.EXTRA_OUTPUT,picUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 1888 && resultCode == Activity.RESULT_OK) {
                String finalPath = "/storage/emulated/0";
                //   Uri imageUri = data.getData();
                //  Bitmap photo = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), imageUri);
                Bitmap photo = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), outputFileUri);
                filePath = outputFileUri.getPath();
                filePath = filePath.substring(1);
                filePath = finalPath + filePath.substring(filePath.indexOf("/"));
                String result = String.valueOf(resultCode);
                if (result.equalsIgnoreCase("-1")) {
                    callCaptureImageLists.add(0, new CallCaptureImageList("", "", photo, filePath, imageName));
                    adapterCallCaptureImage = new AdapterCallCaptureImage(getActivity(), callCaptureImageLists);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    jwothersBinding.rvImgCapture.setLayoutManager(mLayoutManager);
                    jwothersBinding.rvImgCapture.setItemAnimator(new DefaultItemAnimator());
                    jwothersBinding.rvImgCapture.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
                    jwothersBinding.rvImgCapture.setAdapter(adapterCallCaptureImage);
                }
            }
        } catch (Exception e) {
            Log.e("imagerror", "--" + e);
        }
    }
}
