package saneforce.santrip.activity.homeScreen.call.fragments;

import static saneforce.santrip.activity.homeScreen.call.DCRCallActivity.CapPob;
import static saneforce.santrip.activity.homeScreen.call.DCRCallActivity.dcrCallBinding;
import static saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity.SfCode;
import static saneforce.santrip.activity.homeScreen.call.fragments.JointWorkSelectionSide.JwList;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
import java.util.Objects;

import saneforce.santrip.activity.homeScreen.call.DCRCallActivity;
import saneforce.santrip.activity.homeScreen.call.adapter.jwOthers.AdapterCallCaptureImage;
import saneforce.santrip.activity.homeScreen.call.adapter.jwOthers.AdapterCallJointWorkList;
import saneforce.santrip.activity.homeScreen.call.pojo.CallCaptureImageList;
import saneforce.santrip.activity.homeScreen.call.pojo.CallCommonCheckedList;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.databinding.FragmentJwothersBinding;
import saneforce.santrip.storage.SQLite;


public class JWOthersFragment extends Fragment {
    public static ArrayList<CallCaptureImageList>   callCaptureImageLists;
    @SuppressLint("StaticFieldLeak")
    public static FragmentJwothersBinding jwOthersBinding;
    public static String filePath = "", imageName = "";
    @SuppressLint("StaticFieldLeak")
    public static AdapterCallCaptureImage adapterCallCaptureImage;
    public static Uri outputFileUri;
    @SuppressLint("StaticFieldLeak")
    public static AdapterCallJointWorkList adapterCallJointWorkList;
    public static ArrayList<CallCommonCheckedList> callAddedJointList;
    SQLite sqLite;
    CommonUtilsMethods commonUtilsMethods;


    @SuppressLint("NotifyDataSetChanged")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        jwOthersBinding = FragmentJwothersBinding.inflate(inflater);
        View v = jwOthersBinding.getRoot();
        sqLite = new SQLite(requireContext());
        commonUtilsMethods = new CommonUtilsMethods(getActivity());

        HiddenVisibleFunction();
        SetupAdapter();


        jwOthersBinding.tvFeedback.setOnClickListener(view -> dcrCallBinding.fragmentSelectFbSide.setVisibility(View.VISIBLE));

        jwOthersBinding.btnAddJw.setOnClickListener(view -> {
            dcrCallBinding.fragmentSelectJwSide.setVisibility(View.VISIBLE);
            HideKeyboard();
        });

        jwOthersBinding.tvFeedback.setOnClickListener(view -> {
            HideKeyboard();
            dcrCallBinding.fragmentSelectFbSide.setVisibility(View.VISIBLE);
        });

        jwOthersBinding.btnAddImgCapture.setOnClickListener(view -> {
            if (callCaptureImageLists.size() < 2) {
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, 5);
                } else {
                   // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        captureFile();
                   // } else captureFileLower();
                }
            } else {
                Toast.makeText(getContext(), "Not able to Add more Images", Toast.LENGTH_SHORT).show();
            }
        });
        return v;
    }

    private void HideKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(jwOthersBinding.btnAddJw.getWindowToken(), 0);
    }

    private void HiddenVisibleFunction() {
        jwOthersBinding.tagPob.setText(CapPob);
        if (DCRCallActivity.PobNeed.equalsIgnoreCase("0")) {
            jwOthersBinding.constraintPob.setVisibility(View.VISIBLE);
        } else {
            jwOthersBinding.constraintPob.setVisibility(View.GONE);
        }

        if (DCRCallActivity.OverallFeedbackNeed.equalsIgnoreCase("0")) {
            jwOthersBinding.constraintFeedback.setVisibility(View.VISIBLE);
        } else {
            jwOthersBinding.constraintFeedback.setVisibility(View.GONE);
        }

        if (jwOthersBinding.constraintFeedback.getVisibility() == View.VISIBLE && jwOthersBinding.constraintPob.getVisibility() == View.GONE) {
            jwOthersBinding.viewInPobFb.setVisibility(View.GONE);
        } else if (jwOthersBinding.constraintFeedback.getVisibility() == View.GONE && jwOthersBinding.constraintPob.getVisibility() == View.VISIBLE) {
            jwOthersBinding.viewInPobFb.setVisibility(View.GONE);
        }

        if (!DCRCallActivity.PobNeed.equalsIgnoreCase("0") && !DCRCallActivity.OverallFeedbackNeed.equalsIgnoreCase("0")) {
            jwOthersBinding.constraintTopFirst.setVisibility(View.GONE);
        } else {
            jwOthersBinding.constraintTopFirst.setVisibility(View.VISIBLE);
        }

        if (DCRCallActivity.EventCaptureNeed.equalsIgnoreCase("0")) {
            jwOthersBinding.constraintCapture.setVisibility(View.VISIBLE);
        } else {
            jwOthersBinding.constraintCapture.setVisibility(View.GONE);
        }

        if (DCRCallActivity.JwNeed.equalsIgnoreCase("0")) {
            jwOthersBinding.constraintJointWork.setVisibility(View.VISIBLE);
        } else {
            jwOthersBinding.constraintJointWork.setVisibility(View.GONE);
        }
    }

    private void SetupAdapter() {
        adapterCallCaptureImage = new AdapterCallCaptureImage(getActivity(), callCaptureImageLists);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
        jwOthersBinding.rvImgCapture.setLayoutManager(mLayoutManager);
        jwOthersBinding.rvImgCapture.setItemAnimator(new DefaultItemAnimator());
        jwOthersBinding.rvImgCapture.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));
        jwOthersBinding.rvImgCapture.setAdapter(adapterCallCaptureImage);

        adapterCallJointWorkList = new AdapterCallJointWorkList(getContext(), getActivity(), callAddedJointList, JwList);
        RecyclerView.LayoutManager mLayoutManagerJW = new LinearLayoutManager(getActivity());
        jwOthersBinding.rvJointwork.setLayoutManager(mLayoutManagerJW);
        jwOthersBinding.rvJointwork.setItemAnimator(new DefaultItemAnimator());
        jwOthersBinding.rvJointwork.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));
        jwOthersBinding.rvJointwork.setAdapter(adapterCallJointWorkList);
    }


    public void captureFile() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        outputFileUri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".fileprovider", new File(Objects.requireNonNull(requireContext().getExternalCacheDir()).getPath(), SfCode + "_" + DCRCallActivity.CallActivityCustDetails.get(0).getCode() + "_" + CommonUtilsMethods.getCurrentDateDMY().replace("-", "") + CommonUtilsMethods.getCurrentTime().replace(":", "") + ".jpeg"));
        imageName = "E_" + SfCode + DCRCallActivity.CallActivityCustDetails.get(0).getCode() + "_" + CommonUtilsMethods.getCurrentDateDMY().replace("-", "") + CommonUtilsMethods.getCurrentTime().replace(":", "") + ".jpeg";
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        someActivityResultLauncher.launch(intent);
       // startActivityForResult(intent, 1888);
    }

    public void captureFileLower() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Uri outputFileUri = Uri.fromFile(new File(getExternalCacheDir().getPath(), "pickImageResult.jpeg"));
        //outputFileUri = FileProvider.getUriForFile(FeedbackActivity.this, getApplicationContext().getPackageName() + ".fileprovider", new File(getExternalCacheDir().getPath(), "pickImageResult"+System.currentTimeMillis()+".jpeg"));
        //Log.v("Printing_uri",outputFileUri.toString()+" output "+outputFileUri.getPath()+" raw_msg "+getExternalCacheDir().getPath());
        //intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
      //  startActivityForResult(intent, 19);
    }

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    try {
                        if (result.getResultCode() == Activity.RESULT_OK) {
                            String finalPath = "/storage/emulated/0";
                           Bitmap photo = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), outputFileUri);
                            filePath = outputFileUri.getPath();
                            filePath = Objects.requireNonNull(filePath).substring(1);
                            filePath = finalPath + filePath.substring(filePath.indexOf("/"));

                            callCaptureImageLists.add(0, new CallCaptureImageList("", "", photo, filePath, imageName));
                            adapterCallCaptureImage = new AdapterCallCaptureImage(getActivity(), callCaptureImageLists);
                            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                            jwOthersBinding.rvImgCapture.setLayoutManager(mLayoutManager);
                            jwOthersBinding.rvImgCapture.setItemAnimator(new DefaultItemAnimator());
                            jwOthersBinding.rvImgCapture.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));
                            jwOthersBinding.rvImgCapture.setAdapter(adapterCallCaptureImage);
                        }
                    } catch (Exception ignored) {

                    }
                }
            });

   /* @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        try {
            if (requestCode == 1888 && resultCode == Activity.RESULT_OK) {
                String finalPath = "/storage/emulated/0";
                //   Uri imageUri = data.getData();
                //  Bitmap photo = MediaStore.Images.Media.getBitmap(this.getActivity().getContentResolver(), imageUri);
                Bitmap photo = MediaStore.Images.Media.getBitmap(this.requireContext().getContentResolver(), outputFileUri);
                filePath = outputFileUri.getPath();
                filePath = filePath.substring(1);
                filePath = finalPath + filePath.substring(filePath.indexOf("/"));

                callCaptureImageLists.add(0, new CallCaptureImageList("", "", photo, filePath, imageName));
                adapterCallCaptureImage = new AdapterCallCaptureImage(getActivity(), callCaptureImageLists);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                jwOthersBinding.rvImgCapture.setLayoutManager(mLayoutManager);
                jwOthersBinding.rvImgCapture.setItemAnimator(new DefaultItemAnimator());
                jwOthersBinding.rvImgCapture.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));
                jwOthersBinding.rvImgCapture.setAdapter(adapterCallCaptureImage);
            }
        } catch (Exception e) {
            Log.e("imgError", "--" + e);
        }
    }*/
}
