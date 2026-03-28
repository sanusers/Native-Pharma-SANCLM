package saneforce.sanzen.activity.call.fragments.jwOthers;

import static android.Manifest.permission.CAMERA;
import static saneforce.sanzen.activity.call.DCRCallActivity.CapPob;
import static saneforce.sanzen.activity.call.DCRCallActivity.SfCode;
import static saneforce.sanzen.activity.call.DCRCallActivity.TodayPlanSfCode;
import static saneforce.sanzen.activity.call.DCRCallActivity.dcrCallBinding;
import static saneforce.sanzen.activity.call.DCRCallActivity.isFromActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.DCRCallActivity;
import saneforce.sanzen.activity.call.adapter.jwOthers.AdapterCallCaptureImage;
import saneforce.sanzen.activity.call.adapter.jwOthers.AdapterCallJointWorkList;
import saneforce.sanzen.activity.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.sanzen.activity.call.pojo.CallCaptureImageList;
import saneforce.sanzen.activity.call.pojo.CallCommonCheckedList;
import saneforce.sanzen.activity.camera.CameraActivity;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.activity.map.custSelection.CustList;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.databinding.FragmentJwothersBinding;
import saneforce.sanzen.databinding.PopupUnlistedJointworkNameBinding;
import saneforce.sanzen.roomdatabase.DCRDocDataTableDetails.DCRDocDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;

public class JWOthersFragment extends Fragment {
    public static ArrayList<CallCaptureImageList> callCaptureImageLists;
    @SuppressLint("StaticFieldLeak")
    public static FragmentJwothersBinding jwOthersBinding;
    public static String filePath = "", imageName = "", editRemarks, editPob, editFeedback;
    @SuppressLint("StaticFieldLeak")
    public static AdapterCallCaptureImage adapterCallCaptureImage;
    public static Uri outputFileUri;
    @SuppressLint("StaticFieldLeak")
    public static AdapterCallJointWorkList adapterCallJointWorkList;
    public static ArrayList<CallCommonCheckedList> callAddedJointList;
    public static ArrayList<CallCommonCheckedList> unlistedJointList = new ArrayList<>();
    public static AdapterCallJointWorkList adapterUnlistedJointWork;
    public static ArrayList<String> JWKCodeList = new ArrayList<>();
    Gson gson;
    CommonUtilsMethods commonUtilsMethods;
    private String destinationFilePath;
    private RoomDB roomDB;
    private DCRDocDataDao dcrDocDataDao;
    private MasterDataDao masterDataDao;
    String unlisted_jointWork = "0";
    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
        @Override
        public void onActivityResult(ActivityResult result) {
            try {
                if (result.getResultCode() == Activity.RESULT_OK) {
//                    String finalPath = "/storage/emulated/0";
//                    Bitmap photo = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), outputFileUri);
//                    filePath = outputFileUri.getPath();
//                    filePath = Objects.requireNonNull(filePath).substring(1);
//                    filePath = finalPath + filePath.substring(filePath.indexOf("/"));
                    Bitmap photo = BitmapFactory.decodeFile(destinationFilePath);
                    callCaptureImageLists.add(0, new CallCaptureImageList("", "", photo, destinationFilePath, imageName, true, true));
                    adapterCallCaptureImage = new AdapterCallCaptureImage(getActivity(), callCaptureImageLists);
                    RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                    jwOthersBinding.rvImgCapture.setLayoutManager(mLayoutManager);
                    jwOthersBinding.rvImgCapture.setItemAnimator(new DefaultItemAnimator());
                    jwOthersBinding.rvImgCapture.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));
                    jwOthersBinding.rvImgCapture.setAdapter(adapterCallCaptureImage);
                } else if (result.getResultCode() == Activity.RESULT_CANCELED) {
                    Log.d("Camera", "onActivityResult: Canceled");
                }
            } catch (Exception e) {
                Log.e("Camera", "onActivityResult: " + e.getMessage());
                e.printStackTrace();
            }
        }
    });

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("EDITREMARKS", editRemarks);
        outState.putString("EDITPOB", editPob);
        outState.putString("EDITFEEDBACK", editFeedback);
        for (int i = 0; i < callCaptureImageLists.size(); i++) {
            CallCaptureImageList callCaptureImageList = callCaptureImageLists.get(i);
            callCaptureImageList.setImg_view(null);
            callCaptureImageLists.set(i, callCaptureImageList);
        }
        outState.putParcelableArrayList("CAPTURE", callCaptureImageLists);
        outState.putParcelableArrayList("JOINTWORK", callAddedJointList);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        jwOthersBinding = FragmentJwothersBinding.inflate(inflater);
        View view = jwOthersBinding.getRoot();
        roomDB = RoomDB.getDatabase(requireContext());
        dcrDocDataDao = roomDB.dcrDocDataDao();
        masterDataDao = roomDB.masterDataDao();
        commonUtilsMethods = new CommonUtilsMethods(requireContext());
        commonUtilsMethods.setUpLanguage(requireContext());
        gson = new Gson();
        String unlCaption = SharedPref.getUNLcap(requireContext());
        if (unlCaption.isEmpty()) {
            jwOthersBinding.tagJointwork2.setText(getString(R.string.unlisted_jointwork));
        } else {
            jwOthersBinding.tagJointwork2.setText(unlCaption + " " + getString(R.string.joint_work));
        }
        if ("0".equalsIgnoreCase(unlisted_jointWork)) {
            jwOthersBinding.constraintNewSection.setVisibility(View.VISIBLE);
        } else {
            jwOthersBinding.constraintNewSection.setVisibility(View.GONE);
        }

        if (savedInstanceState != null) {
            editRemarks = savedInstanceState.getString("EDITREMARKS");
            editPob = savedInstanceState.getString("EDITPOB");
            editFeedback = savedInstanceState.getString("EDITFEEDBACK");
            callCaptureImageLists = savedInstanceState.getParcelableArrayList("CAPTURE");
            callAddedJointList = savedInstanceState.getParcelableArrayList("JOINTWORK");
            if (editFeedback != null && !editFeedback.isEmpty()) {
                jwOthersBinding.tvFeedback.setHint("");
                jwOthersBinding.tvFeedback.setText(editFeedback);
            }
            if (editPob != null && !editPob.isEmpty()) {
                jwOthersBinding.edPob.setText(editPob);
            }
            if (editRemarks != null && !editRemarks.isEmpty()) {
                jwOthersBinding.edRemarks.setText(editRemarks);
            }
            if (callCaptureImageLists != null && !callCaptureImageLists.isEmpty()) {
                for (int i = 0; i < callCaptureImageLists.size(); i++) {
                    CallCaptureImageList callCaptureImageList = callCaptureImageLists.get(i);
                    Bitmap photo = BitmapFactory.decodeFile(callCaptureImageList.getFilePath());
                    callCaptureImageList.setImg_view(photo);
                    callCaptureImageLists.set(i, callCaptureImageList);
                }
//                adapterCallCaptureImage = new AdapterCallCaptureImage(getActivity(), callCaptureImageLists);
//                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
//                jwOthersBinding.rvImgCapture.setLayoutManager(mLayoutManager);
//                jwOthersBinding.rvImgCapture.setItemAnimator(new DefaultItemAnimator());
//                jwOthersBinding.rvImgCapture.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));
//                jwOthersBinding.rvImgCapture.setAdapter(adapterCallCaptureImage);
            }
            Log.e("JW", "onCreateView: " + editRemarks + " -> " + editPob + " -> " + editFeedback);
        }
        HiddenVisibleFunction();
        SetupAdapter();

        if (HomeDashBoard.selectedDate.toString().equalsIgnoreCase(SharedPref.getJWKDATE(requireContext()))) {
            if (isFromActivity.equalsIgnoreCase("new") && SharedPref.getJwAutoSelectionNeed(requireContext()).equalsIgnoreCase("0")) {
                Log.v("Testing", "new");
                String getjwkcode = SharedPref.getJWKCODE(requireContext());
                if (!getjwkcode.equalsIgnoreCase("")) {
                    Type type = new TypeToken<List<String>>() {
                    }.getType();
                    JWKCodeList = gson.fromJson(getjwkcode, type);

                    Map<String, List<String>> jcMap = SharedPref.getJCMap(requireContext());
                    if (jcMap == null) {
                        jcMap = new HashMap<>();
                    }
                    jcMap.put(TodayPlanSfCode, JWKCodeList);
                    SharedPref.setJWKCODE(requireContext(), JWKCodeList, HomeDashBoard.selectedDate.toString());
                    SharedPref.saveJCMap(requireContext(), jcMap, HomeDashBoard.selectedDate.toString());
                    if (jcMap.containsKey(DcrCallTabLayoutActivity.TodayPlanSfCode)) {
                        JWKCodeList = (ArrayList<String>) jcMap.get(DcrCallTabLayoutActivity.TodayPlanSfCode);
                    } else {
                        JWKCodeList = new ArrayList<>();
                    }

                    try {
                        if (DCRCallActivity.save_valid.equals("1")) {
                            JSONArray jsonArray = dcrDocDataDao.getDCRDocData(DCRCallActivity.hqcode).getDCRDocDataJSONArray();

                            Log.d("jw_data", jsonArray.toString() + "====" + TodayPlanSfCode);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                if (JWKCodeList.contains(jsonObject.getString("Code"))) {
                                    callAddedJointList.add(new CallCommonCheckedList(jsonObject.getString("Name"), jsonObject.getString("Code"), true));
                                }
                            }
                        } else {
                            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.JOINT_WORK + TodayPlanSfCode).getMasterSyncDataJsonArray();
                            Log.d("jw_data", jsonArray.toString() + "====" + TodayPlanSfCode);
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                if (JWKCodeList.contains(jsonObject.getString("Code"))) {
                                    callAddedJointList.add(new CallCommonCheckedList(jsonObject.getString("Name"), jsonObject.getString("Code"), true));
                                }
                            }
                        }

                    } catch (Exception e) {
                        Log.v("Testing", "issue" + e.getMessage());
                    }
                }
            }
        } else {
            JWKCodeList.clear();
            SharedPref.setJWKCODE(requireContext(), JWKCodeList, "");
            SharedPref.saveJCMap(requireContext(), new HashMap<>(), "");
            Log.v("Testing", "OLD");
        }

        jwOthersBinding.tvFeedback.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dcrCallBinding.fragmentSelectFbSide.setVisibility(View.VISIBLE);
            }
        });

        jwOthersBinding.btnAddJw.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dcrCallBinding.fragmentSelectJwSide.setVisibility(View.VISIBLE);
                HideKeyboard();
            }
        });
        jwOthersBinding.btnAddJw2.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View v) {
                PopupUnlistedJointworkNameBinding popupBinding = PopupUnlistedJointworkNameBinding.inflate(LayoutInflater.from(requireContext()));
                AlertDialog alertDialog = new AlertDialog.Builder(requireContext()).setView(popupBinding.getRoot()).create();
                alertDialog.setCancelable(false);
                alertDialog.setCanceledOnTouchOutside(false);

                popupBinding.tvHead2.setText(unlCaption + " " + getString(R.string.joint_work));
                popupBinding.unlistedJointwork.setHint(getString(R.string.type) +  " "  + unlCaption + " " + getString(R.string.joint_work_name));
//                String unlCaption = SharedPref.getUNLcap(requireContext());
//                if (unlCaption.isEmpty()) {
//                    jwOthersBinding.tagJointwork2.setText(getString(R.string.unlisted_jointwork));
//                } else {
//                    jwOthersBinding.tagJointwork2.setText(unlCaption +getString(R.string.joint_work));
//                }
//                // Head text
//                if (unlCaption.isEmpty()) {
//                    popupBinding.tvHead2.setText(getString(R.string.unlisted_jointwork));
//                } else {
//                    popupBinding.tvHead2.setText(unlCaption + getString(R.string.joint_work));
//                }
//
//                // joint work hint
//                if (unlCaption.isEmpty()) {
//                    popupBinding.unlistedJointwork.setHint(getString(R.string.type_unlisted_name));
//                    popupBinding.unlistedJointwork.setText("");
//                } else {
//                    popupBinding.unlistedJointwork.setHint(getString(R.string.type)  + unlCaption + getString(R.string.joint_work_name));
//                    popupBinding.unlistedJointwork.setText("");
//                }

                // Main Fragment Tag


                final String[] selectedSpeciality = {""};
                List<String> specialtyList = new ArrayList<>();
                try {
                    JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.SPECIALITY)
                            .getMasterSyncDataJsonArray();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);
                        String specialty = obj.optString("Name", "").trim();
                        if (!specialty.isEmpty()) {
                            specialtyList.add(specialty);
                        }
                    }
                } catch (Exception e) {
                    Log.e("JW", "Speciality fetch error: " + e.getMessage());
                }

                // Speciality button click
                popupBinding.secondConstraint.setOnClickListener(view -> {
                    InputMethodManager imm = (InputMethodManager) requireContext()
                            .getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    popupBinding.unlistedJointwork.clearFocus();

                    if (specialtyList.isEmpty()) {
                        commonUtilsMethods.showToastMessage(requireContext(), "No speciality found");
                        return;
                    }

                    // Toggle
                    if (popupBinding.lvSpeciality.getVisibility() == View.VISIBLE) {
                        popupBinding.lvSpeciality.setVisibility(View.GONE);
                        return;
                    }

                    // Adapter set
                    android.widget.ArrayAdapter<String> adapter = new android.widget.ArrayAdapter<>(
                            requireContext(),
                            R.layout.popup_unlisted_jointwork_speciality,
                            R.id.tv_speciality,
                            specialtyList
                    );
                    popupBinding.lvSpeciality.setAdapter(adapter);
                    popupBinding.lvSpeciality.setVisibility(View.VISIBLE);

                    // Item click
                    popupBinding.lvSpeciality.setOnItemClickListener((parent, v2, position, id) -> {
                        selectedSpeciality[0] = specialtyList.get(position);
                        popupBinding.secondConstraint.setText(selectedSpeciality[0]);
                        popupBinding.lvSpeciality.setVisibility(View.GONE);
                    });
                });

                // Cancel
                popupBinding.btnCancel.setOnClickListener(view -> alertDialog.dismiss());

                // Save
                popupBinding.btnSave.setOnClickListener(view -> {
                    String name = popupBinding.unlistedJointwork.getText().toString().trim();
                    if (name.isEmpty()) {
                        commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.enter_unlisted_jointwork));
                        return;
                    }
                    if (selectedSpeciality[0].isEmpty()) {
                        commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.select_speciality));
                        return;
                    }
                    unlistedJointList.add(new CallCommonCheckedList(name, selectedSpeciality[0], true));
                    if (adapterUnlistedJointWork != null) {
                        adapterUnlistedJointWork.notifyDataSetChanged();
                    }
                    alertDialog.dismiss();
                });

                alertDialog.show();
                if (alertDialog.getWindow() != null) {
                    alertDialog.getWindow().setSoftInputMode(
                            android.view.WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
                    );
                }
            }
        });
//        jwOthersBinding.btnAddJw2.setOnClickListener(new SafeClickListener() {
//            @Override
//            public void onSafeClick(View v) {
//                PopupUnlistedJointworkNameBinding popupBinding = PopupUnlistedJointworkNameBinding.inflate(LayoutInflater.from(requireContext()));
//                AlertDialog alertDialog = new AlertDialog.Builder(requireContext()).setView(popupBinding.getRoot()).create();
//                alertDialog.setCancelable(false);
//                alertDialog.setCanceledOnTouchOutside(false);
//                String unlCaption = SharedPref.getUNLcap(requireContext());
//                //for  popup heading
//                // Head text (TextView) - Idhuku setText correct
//                if (unlCaption.isEmpty()) {
//                    popupBinding.tvHead2.setText("Unlisted JointWork");
//                } else {
//                    popupBinding.tvHead2.setText(unlCaption + " JointWork");
//                }
//
//                // joint work type text
//                if (unlCaption.isEmpty()) {
//                    popupBinding.unlistedJointwork.setHint("Type Unlisted Jointwork Name");
//                    popupBinding.unlistedJointwork.setText("");
//                } else {
//
//                    popupBinding.unlistedJointwork.setHint("Type " + unlCaption + " Jointwork Name");
//                    popupBinding.unlistedJointwork.setText("");
//                }
//
//                // Main Fragment Tag (TextView)
//                if (unlCaption.isEmpty()) {
//                    jwOthersBinding.tagJointwork2.setText("Unlisted JointWork");
//                } else {
//                    jwOthersBinding.tagJointwork2.setText(unlCaption + " JointWork");
//                }
//                final String[] selectedSpeciality = {""};
//                List<String> specialtyList = new ArrayList<>();
//                try {
//                    JSONArray jsonArray;
//                    if (DCRCallActivity.save_valid.equals("1")) {
//                        jsonArray = dcrDocDataDao.getDCRDocData(DCRCallActivity.hqcode).getDCRDocDataJSONArray();
//                    } else {
//                        jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.DOCTOR_MAS + TodayPlanSfCode).getMasterSyncDataJsonArray();
//                    }
//                    java.util.LinkedHashSet<String> seen = new java.util.LinkedHashSet<>();
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        JSONObject obj = jsonArray.getJSONObject(i);
//                        String specialty = obj.optString("Specialty", "").trim();
//                        if (!specialty.isEmpty()) {
//                            seen.add(specialty);
//                        }
//                    }
//                    specialtyList.addAll(seen);
//
//                } catch (Exception e) {
//                    Log.e("JW", "Speciality fetch error: " + e.getMessage());
//                }
//
//                popupBinding.secondConstraint.setOnClickListener(view -> {
//                    InputMethodManager imm = (InputMethodManager) requireContext()
//                            .getSystemService(Context.INPUT_METHOD_SERVICE);
//                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
//
//                    if (specialtyList.isEmpty()) {
//                        commonUtilsMethods.showToastMessage(requireContext(), "No speciality found");
//                        return;
//                    }
//
//                    android.widget.ScrollView scrollView = new android.widget.ScrollView(requireContext());
//                    android.widget.LinearLayout innerLayout = new android.widget.LinearLayout(requireContext());
//                    innerLayout.setOrientation(android.widget.LinearLayout.VERTICAL);
//
//                    AlertDialog specialityDialog = new AlertDialog.Builder(requireContext())
//                            .setView(scrollView)
//                            .create();
//
//                    for (String specialty : specialtyList) {
//
//                        View itemView = LayoutInflater.from(requireContext())
//                                .inflate(R.layout.popup_unlisted_jointwork_speciality, innerLayout, false);
//
//                        androidx.appcompat.widget.AppCompatTextView tv = itemView.findViewById(R.id.tv_speciality);
//                        tv.setText(specialty);
//                        itemView.setOnClickListener(click -> {
//                            selectedSpeciality[0] = specialty;
//                            popupBinding.secondConstraint.setText(specialty);
//                            specialityDialog.dismiss();
//                        });
//
//                        innerLayout.addView(itemView);
//                    }
//
//                    scrollView.addView(innerLayout);
//                    specialityDialog.show();
//
//                    if (specialityDialog.getWindow() != null) {
//                        specialityDialog.getWindow().setLayout(
//                                (int) (getResources().getDisplayMetrics().widthPixels * 0.45),
//                                (int) (getResources().getDisplayMetrics().heightPixels * 0.4)
//                        );
//                    }
//                });
////                popupBinding.secondConstraint.setOnClickListener(view -> {
////                    // Keyboard hide
////                    InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
////                    imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
////
////                    if (specialtyList.isEmpty()) {
////                        commonUtilsMethods.showToastMessage(requireContext(), "No speciality found");
////                        return;
////                    }
////                    String[] specialtyArray = specialtyList.toArray(new String[0]);
////                    new AlertDialog.Builder(requireContext())
////                            .setTitle("Select Speciality")
////                            .setItems(specialtyArray, (dialog, which) -> {
////                                selectedSpeciality[0] = specialtyArray[which];
////                                popupBinding.secondConstraint.setText(selectedSpeciality[0]);
////                                dialog.dismiss();
////                            })
////                            .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
////                            .create()
////                            .show();
////                });
//
//                // Cancel
//                popupBinding.btnCancel.setOnClickListener(view -> alertDialog.dismiss());
//
//                // Save
//                popupBinding.btnSave.setOnClickListener(view -> {
//                    String name = popupBinding.unlistedJointwork.getText().toString().trim();
//                    if (name.isEmpty()) {
//                        commonUtilsMethods.showToastMessage(requireContext(), "Enter the unlisted jointwork");
//                        return;
//                    }
//                    if (selectedSpeciality[0].isEmpty()) {
//                        commonUtilsMethods.showToastMessage(requireContext(), "Select a speciality");
//                        return;
//                    }
//                    unlistedJointList.add(new CallCommonCheckedList(name, selectedSpeciality[0], true));
//                    if (adapterUnlistedJointWork != null) {
//                        adapterUnlistedJointWork.notifyDataSetChanged();
//                    }
//                    alertDialog.dismiss();
//                });
//
//                alertDialog.show();
//            }
//        });
//        jwOthersBinding.btnAddJw2.setOnClickListener(new SafeClickListener() {
//            @Override
//            public void onSafeClick(View v) {
//                PopupUnlistedJointworkNameBinding popupBinding = PopupUnlistedJointworkNameBinding.inflate(LayoutInflater.from(requireContext()));
//                AlertDialog alertDialog = new AlertDialog.Builder(requireContext()).setView(popupBinding.getRoot()).create();
//                alertDialog.setCancelable(false);
//                alertDialog.setCanceledOnTouchOutside(false);
//                popupBinding.btnSave.setOnClickListener(view -> {
//                    String name = popupBinding.unlistedJointwork.getText().toString().trim();
//                    if (!name.isEmpty()) {
//                        unlistedJointList.add(new CallCommonCheckedList(name, "0", true));
//                        if (adapterUnlistedJointWork != null) {
//                            adapterUnlistedJointWork.notifyDataSetChanged();
//                        }
//                        alertDialog.dismiss();
//                    }else{
//                        commonUtilsMethods.showToastMessage(requireContext(), "Enter the unlisted jointwork");
//                    }
//                });
//                alertDialog.show();
//            }
//        });

        jwOthersBinding.tvFeedback.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                HideKeyboard();
                dcrCallBinding.fragmentSelectFbSide.setVisibility(View.VISIBLE);
            }
        });

        jwOthersBinding.btnAddImgCapture.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (callCaptureImageLists.size() < 2) {
                    if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        requestMultiplePermissionsLauncher.launch(new String[]{Manifest.permission.CAMERA,});
                    } else {
                        captureFile();
                    }
                } else {
                    commonUtilsMethods.showToastMessage(requireContext(), getString(R.string.no_add_more_images));
                }
            }
        });
        return view;
    }

    private void HideKeyboard() {
        InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(jwOthersBinding.btnAddJw.getWindowToken(), 0);
    }

    private void HiddenVisibleFunction() {
        jwOthersBinding.tagPob.setText(CapPob);
        jwOthersBinding.edRemarks.setFilters(new InputFilter[]{CommonUtilsMethods.FilterSpaceEditText(jwOthersBinding.edRemarks, 200)});
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
//        if (DCRCallActivity.save_valid.equals("1")) {
//            jwOthersBinding.constraintCapture.setVisibility(View.GONE);
//        }else{
//            jwOthersBinding.constraintCapture.setVisibility(View.VISIBLE);
//        }


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

        adapterUnlistedJointWork = new AdapterCallJointWorkList(getContext(), getActivity(), unlistedJointList, JointWorkSelectionSide.JwList);
        RecyclerView.LayoutManager mLayoutManagerJW2 = new LinearLayoutManager(getActivity());
        jwOthersBinding.rvJointwork2.setLayoutManager(mLayoutManagerJW2);
        jwOthersBinding.rvJointwork2.setItemAnimator(new DefaultItemAnimator());
        jwOthersBinding.rvJointwork2.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));
        jwOthersBinding.rvJointwork2.setAdapter(adapterUnlistedJointWork);

        adapterCallJointWorkList = new AdapterCallJointWorkList(getContext(), getActivity(), callAddedJointList, JointWorkSelectionSide.JwList);
        RecyclerView.LayoutManager mLayoutManagerJW = new LinearLayoutManager(getActivity());
        jwOthersBinding.rvJointwork.setLayoutManager(mLayoutManagerJW);
        jwOthersBinding.rvJointwork.setItemAnimator(new DefaultItemAnimator());
        jwOthersBinding.rvJointwork.addItemDecoration(new DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL));
        jwOthersBinding.rvJointwork.setAdapter(adapterCallJointWorkList);

        if (isFromActivity.equalsIgnoreCase("edit_local") || isFromActivity.equalsIgnoreCase("edit_online")) {
            jwOthersBinding.edRemarks.setText(editRemarks);
            jwOthersBinding.edPob.setText(editPob);
            jwOthersBinding.tvFeedback.setText(editFeedback);
        }
    }

    public void captureFile() {
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        outputFileUri = FileProvider.getUriForFile(requireContext(), requireContext().getPackageName() + ".fileprovider", new File(Objects.requireNonNull(requireContext().getExternalCacheDir()).getPath(), SfCode + "_" + DCRCallActivity.CallActivityCustDetails.get(0).getCode() + "_" + CommonUtilsMethods.getCurrentInstance("dd-MM-yyyy").replace("-", "") + CommonUtilsMethods.getCurrentInstance("HHmmss") + ".jpeg"));
//        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
//        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        imageName = /*SfCode + "_" +*/ DCRCallActivity.CallActivityCustDetails.get(0).getName() + "_" + CommonUtilsMethods.getCurrentInstance("dd-MM-yyyy").replace("-", "") + CommonUtilsMethods.getCurrentInstance("HHmmss") + ".jpeg";
        Intent intent = new Intent(requireActivity(), CameraActivity.class);
        File file = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            file = new File(requireContext().getExternalFilesDir(null) + "/JWOthersImages/");
        } else {
            Log.e("File Creation", "captureFile: No media mounted");
        }
        if (file != null && !file.exists()) {
            if (!file.mkdirs()) {
                Log.e("File Creation", "Directory Creation Failed.");
            }
        }
        File destinationFile = new File(file, imageName);
        try {
            if (!destinationFile.createNewFile()) {
                Log.e("File Creation", "Destination File Creation Failed.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        destinationFilePath = destinationFile.getAbsolutePath();
        intent.putExtra("FILE_PATH", destinationFilePath);
        intent.putExtra("FROM", "JWOthersFragment");
        intent.putExtra("L_FLAG", SharedPref.getGeoChk(requireContext()).equalsIgnoreCase("0"));
        intent.putExtra("CAMERA_MODE", "BACK");
        try {
            CustList custList = DCRCallActivity.CallActivityCustDetails.get(0);
            StringBuilder customerData = new StringBuilder();
            customerData.append(custList.getName());
            if (custList.getQualification() != null && !custList.getQualification().isEmpty()) {
                customerData.append(" - ");
                customerData.append(custList.getQualification());
            }
            if (custList.getSpecialist() != null && !custList.getSpecialist().isEmpty()) {
                customerData.append(" - ");
                customerData.append(custList.getSpecialist());
            }
            if (custList.getclass() != null && !custList.getclass().isEmpty()) {
                customerData.append(" - ");
                customerData.append(custList.getclass());
            }
            if (custList.getCategory() != null && !custList.getCategory().isEmpty()) {
                customerData.append(" - ");
                customerData.append(custList.getCategory());
            }
            if (custList.getTown_name() != null && !custList.getTown_name().isEmpty()) {
                customerData.append(" - ");
                customerData.append(custList.getTown_name());
            }
            intent.putExtra("CUSTOMER_DATA", customerData.toString());
            String caption = "";
            switch (custList.getType()) {
                case "1":
                    caption = SharedPref.getDrCap(requireContext());
                    if (caption.isEmpty()) {
                        caption = "Listed Doctor";
                    }
                    break;
                case "2":
                    caption = SharedPref.getChmCap(requireContext());
                    if (caption.isEmpty()) {
                        caption = "Chemist";
                    }
                    break;
                case "3":
                    caption = SharedPref.getStkCap(requireContext());
                    if (caption.isEmpty()) {
                        caption = "Stockist";
                    }
                    break;
                case "4":
                    caption = SharedPref.getUNLcap(requireContext());
                    if (caption.isEmpty()) {
                        caption = "UnListed Doctor";
                    }
                    break;
                case "5":
                    caption = SharedPref.getCipCaption(requireContext());
                    if (caption.isEmpty()) {
                        caption = "CIP";
                    }
                    break;
                case "6":
                    caption = SharedPref.getHospCaption(requireContext());
                    if (caption.isEmpty()) {
                        caption = "Hospital";
                    }
                    break;
            }
            intent.putExtra("CUSTOMER_CAPTION", caption + " : ");
        } catch (Exception e) {
            e.printStackTrace();
        }
        someActivityResultLauncher.launch(intent);
    }


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

    public boolean CheckCameraPermission() {
        int Camera = ContextCompat.checkSelfPermission(requireContext(), CAMERA);
        return Camera != PackageManager.PERMISSION_GRANTED;
    }

    private void RequestCameraPermission() {
        ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.CAMERA}, 102);
    }

    private final ActivityResultLauncher<String[]> requestMultiplePermissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean cameraPermission = result.getOrDefault(Manifest.permission.CAMERA, false);
                if (Boolean.TRUE.equals(cameraPermission)) {
                    captureFile();
                } else {
                    CommonUtilsMethods.RequestGPSPermission(requireActivity(), "Camera");
                }
            });
}
