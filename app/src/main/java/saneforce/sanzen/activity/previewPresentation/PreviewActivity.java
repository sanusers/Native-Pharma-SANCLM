package saneforce.sanzen.activity.previewPresentation;

import static saneforce.sanzen.activity.call.DCRCallActivity.CallActivityCustDetails;
import static saneforce.sanzen.activity.call.DCRCallActivity.arrayStore;
import static saneforce.sanzen.activity.call.adapter.detailing.PlaySlideDetailing.binding;
import static saneforce.sanzen.activity.call.adapter.detailing.PlaySlideDetailedAdapter.mandatoryProductList;
import static saneforce.sanzen.activity.call.adapter.detailing.PlaySlideDetailedAdapter.playedMandatorySlideIds;
import static saneforce.sanzen.activity.call.adapter.detailing.PlaySlideDetailing.context;
import static saneforce.sanzen.activity.call.adapter.detailing.PlaySlideDetailing.headingData;
import static saneforce.sanzen.activity.call.fragments.detailing.DetailedFragment.callDetailingLists;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.call.DCRCallActivity;
import saneforce.sanzen.activity.call.adapter.detailing.PlaySlideDetailedAdapter;
import saneforce.sanzen.activity.call.adapter.detailing.PlaySlideDetailing;
import saneforce.sanzen.activity.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.sanzen.activity.call.pojo.detailing.CallDetailingList;
import saneforce.sanzen.activity.call.pojo.detailing.StoreImageTypeUrl;
import saneforce.sanzen.activity.homeScreen.HomeDashBoard;
import saneforce.sanzen.activity.presentation.createPresentation.BrandModelClass;
import saneforce.sanzen.activity.presentation.customerSelection.model.CustomerDataModel;
import saneforce.sanzen.activity.presentation.presentation.adapter.SideScreenAdapter;
import saneforce.sanzen.activity.previewPresentation.fragment.BrandMatrix;
import saneforce.sanzen.activity.previewPresentation.fragment.CustomPresentationFragment;
import saneforce.sanzen.activity.previewPresentation.fragment.CustomPreviewFragment;
import saneforce.sanzen.activity.previewPresentation.fragment.CustomizedPresentationFragment;
import saneforce.sanzen.activity.previewPresentation.fragment.HomeBrands;
import saneforce.sanzen.activity.previewPresentation.fragment.MyPresentation;
import saneforce.sanzen.activity.previewPresentation.fragment.Speciality;
import saneforce.sanzen.activity.previewPresentation.fragment.Therapist;
import saneforce.sanzen.activity.previewPresentation.fragment.WelcomePresentation;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.roomdatabase.CallOfflineTableDetails.CallOfflineDataDao;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.PresentationTableDetails.PresentationDataDao;
import saneforce.sanzen.roomdatabase.PresentationTableDetails.PresentationDataTable;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;
import saneforce.sanzen.utility.TimeUtils;

public class PreviewActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    public static saneforce.sanzen.databinding.ActivityPreviewBinding previewBinding;
    public static String SelectedTab = "Matrix", from_where = "", cus_name = "", cus_code = "", SpecialityCode = "", SpecialityName = "", BrandCode = "", SlideCode = "", CusType = "";
    public static int SelectedPosPlay;
    PreviewTabAdapter viewPagerAdapter;
    String finalPrdNam;
    ArrayList<StoreImageTypeUrl> dummyArr = new ArrayList<>();
    String startT, endT, presentationNeed, therapticNeed, caption = "";
    CommonUtilsMethods commonUtilsMethods;
    //    CustomSetupResponse customSetupResponse;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;
    private CallOfflineDataDao callOfflineDataDao;
    private PresentationDataDao presentationDataDao;
    ProgressDialog progressDialog;
    private SideScreenAdapter sideScreenAdapter;
    private JSONObject checkInJsonObject = new JSONObject();
    public static boolean isTimerEnd = false;
    String mandatorySlide ="0";

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (isTimerEnd) {
            Log.e("TAG", "onResume: timer end");
            showDetailingTimeExceededAlert();
            isTimerEnd = false;
        }
    }

    private void showDetailingTimeExceededAlert() {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dcr_cancel_alert);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        TextView btn_yes = dialog.findViewById(R.id.btn_yes);
        TextView btn_no = dialog.findViewById(R.id.btn_no);
        TextView titte = dialog.findViewById(R.id.ed_alert_msg);
        titte.setText(getString(R.string.idle_time) + " " + SharedPref.getDetailingIdleDuration(this) + " " + getString(R.string.minutes) + " " + getString(R.string.for_detailing_has_been_exceeded));
        btn_no.setVisibility(View.GONE);
        btn_yes.setText(getString(R.string.ok));
        btn_yes.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                dialog.dismiss();
            }
        });
    }

    //To Hide the bottomNavigation When popup
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            previewBinding.getRoot().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        playedMandatorySlideIds.clear();
        previewBinding = saneforce.sanzen.databinding.ActivityPreviewBinding.inflate(getLayoutInflater());
        setContentView(previewBinding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        roomDB = RoomDB.getDatabase(this);
        masterDataDao = roomDB.masterDataDao();
        callOfflineDataDao = roomDB.callOfflineDataDao();
        presentationDataDao = roomDB.presentationDataDao();
        commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());
        commonUtilsMethods.setUpLanguage(getApplicationContext());
        callDetailingLists = new ArrayList<>();
        arrayStore = new ArrayList<>();

        getRequiredData();
        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            from_where = extra.getString("from");
            assert from_where != null;
            if (from_where.equalsIgnoreCase("call")) {
                cus_name = extra.getString("cus_name");
                cus_code = extra.getString("cus_code");
                SpecialityCode = extra.getString("SpecialityCode");
                SpecialityName = extra.getString("SpecialityName");
                BrandCode = extra.getString("MappedProdCode");
                SlideCode = extra.getString("MappedSlideCode");
                CusType = extra.getString("CusType", "");
                // 🔥 DIRECT OPEN WELCOME FOR DOCTOR
                if ("1".equalsIgnoreCase(CusType)) {

                    try {

                        JSONArray welcomeSlideArray =
                                masterDataDao.getMasterDataTableOrNew(Constants.WELCOME_SLIDE)
                                        .getMasterSyncDataJsonArray();

                        if (welcomeSlideArray != null && welcomeSlideArray.length() > 0) {

                            ArrayList<BrandModelClass.Product> productList = new ArrayList<>();

                            for (int i = 0; i < welcomeSlideArray.length(); i++) {

                                JSONObject obj = welcomeSlideArray.optJSONObject(i);

                                if (obj != null) {

                                    String fileName = obj.optString("Name", "");

                                    if (fileName.contains("/")) {
                                        fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
                                    }

                                    productList.add(new BrandModelClass.Product("", "Welcome", "", fileName, obj.optString("orderby"), false, "", ""));
                                }
                            }

                            if (!productList.isEmpty()) {

                                Intent intent = new Intent(this, PlaySlideDetailing.class);

                                String data = new Gson().toJson(productList);

                                Bundle bundle = new Bundle();
                                bundle.putString("slideBundle", data);
                                bundle.putString("position", "0");

                                intent.putExtra("bundle", bundle);
                                bundle.putBoolean("isWelcomeOnly", true);

                                startActivity(intent);
                                //finish();
                                //return;  // 🔥 STOP ACTIVITY HERE
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                switch (CusType) {
                    case "1":
                        caption = SharedPref.getDrCap(PreviewActivity.this);
                        break;
                    case "2":
                        caption = SharedPref.getChmCap(PreviewActivity.this);
                        break;
                    case "3":
                        caption = SharedPref.getStkCap(PreviewActivity.this);
                        break;
                    case "4":
                        caption = SharedPref.getUNLcap(PreviewActivity.this);
                        break;
                    case "5":
                        caption = SharedPref.getCipCaption(PreviewActivity.this);
                        break;
                    case "6":
                        caption = SharedPref.getHospCaption(PreviewActivity.this);
                        break;
                }
                if (extra.containsKey("CheckInJsonObject")) {
                    String jsonObject = extra.getString("CheckInJsonObject");
                    try {
                        checkInJsonObject = new JSONObject(jsonObject);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    checkInJsonObject = new JSONObject();
                }
                PlaySlideDetailedAdapter.timer = new HashMap<>();
                PlaySlideDetailedAdapter.pageStartTime = TimeUtils.getCurrentDateTime(TimeUtils.FORMAT_32);
                previewBinding.tagCustName.setText(cus_name);
                previewBinding.btnFinishDet.setVisibility(View.VISIBLE);
            } else {
                previewBinding.btnFinishDet.setVisibility(View.GONE);
            }
        }
        viewPagerAdapter = new PreviewTabAdapter(getSupportFragmentManager());

        if (from_where.equalsIgnoreCase("call")) {

            headingData.clear();
            viewPagerAdapter.clear();

            boolean hideWelcomeTab = getIntent().getBooleanExtra("hideWelcomeTab", false);
            Log.d("PreviewActivity", "hideWelcomeTab = " + hideWelcomeTab);

            if (from_where.equalsIgnoreCase("call")) {

                if (CusType.equalsIgnoreCase("1")) {

                    if (hideWelcomeTab) {
                        viewPagerAdapter.add(new WelcomePresentation(), getString(R.string.welcome));
                        headingData.add("A");
                    }

                    viewPagerAdapter.add(new HomeBrands(), getString(R.string.all_brands));
                    headingData.add("B");
                    viewPagerAdapter.add(new BrandMatrix(), getString(R.string.brand_matrix));
                    headingData.add("C");
                    viewPagerAdapter.add(new Speciality(), getString(R.string.speciality));
                    headingData.add("D");

                    if (therapticNeed.equalsIgnoreCase("0")) {
                        viewPagerAdapter.add(new Therapist(), getString(R.string.therapist));
                        headingData.add("E");
                    }

                    if (presentationNeed.equalsIgnoreCase("0")) {
                        viewPagerAdapter.add(new MyPresentation(), getString(R.string.my_presentation));
                        headingData.add("F");
                    }

                    viewPagerAdapter.add(new CustomizedPresentationFragment(), getString(R.string.customized_presentation));
                    headingData.add("G");
                    viewPagerAdapter.add(new CustomPresentationFragment(), getString(R.string.custom_presentation));
                    headingData.add("H");

                } else {
                    // CusType != 1
                    if (hideWelcomeTab) {
                        viewPagerAdapter.add(new WelcomePresentation(), getString(R.string.welcome));
                        headingData.add("A");
                    }

                    viewPagerAdapter.add(new HomeBrands(), getString(R.string.all_brands));
                    headingData.add("B");
                    viewPagerAdapter.add(new Speciality(), getString(R.string.speciality));
                    headingData.add("D");

                    if (therapticNeed.equalsIgnoreCase("0")) {
                        viewPagerAdapter.add(new Therapist(), getString(R.string.therapist));
                        headingData.add("E");
                    }

                    if (presentationNeed.equalsIgnoreCase("0")) {
                        viewPagerAdapter.add(new MyPresentation(), getString(R.string.my_presentation));
                        headingData.add("F");
                    }

                    viewPagerAdapter.add(new CustomizedPresentationFragment(), getString(R.string.customized_presentation));
                    headingData.add("G");
                    viewPagerAdapter.add(new CustomPresentationFragment(), getString(R.string.custom_presentation));
                    headingData.add("H");
                }

            } else {
                // from_where != call
                viewPagerAdapter.add(new HomeBrands(), getString(R.string.all_brands));
                viewPagerAdapter.add(new BrandMatrix(), getString(R.string.brand_matrix));
                viewPagerAdapter.add(new Speciality(), getString(R.string.speciality));

                if (therapticNeed.equalsIgnoreCase("0")) {
                    viewPagerAdapter.add(new Therapist(), getString(R.string.therapist));
                }

                if (presentationNeed.equalsIgnoreCase("0")) {
                    viewPagerAdapter.add(new CustomPreviewFragment(this::viewSideScreen), getString(R.string.my_presentation));
                }
            }

// 4️⃣ Set adapter fresh
            previewBinding.viewPager.setAdapter(viewPagerAdapter);
            previewBinding.tabLayout.setupWithViewPager(previewBinding.viewPager);
            previewBinding.viewPager.setOffscreenPageLimit(viewPagerAdapter.getCount());

        }

        previewBinding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                SelectedPosPlay = tab.getPosition();
                if (tab.getPosition() == 1) SelectedTab = "Matrix";
                if (tab.getPosition() == 2) SelectedTab = "Spec";
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        previewBinding.ivBack.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                if (from_where.equalsIgnoreCase("call")) {
                    Intent intent = new Intent(PreviewActivity.this, DcrCallTabLayoutActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startActivity(intent);
                    finish();
                } else {
                    getOnBackPressedDispatcher().onBackPressed();
                }
            }
        });

        previewBinding.btnFinishDet.setOnClickListener(view -> {

            Set<String> pendingSlides = new LinkedHashSet<>();

            for (BrandModelClass.Product p : PlaySlideDetailedAdapter.mandatoryProductList) {
                if (!PlaySlideDetailedAdapter.playedMandatorySlideIds.contains(p.getSlideId())) {
                    pendingSlides.add(p.getSlideName());
                }
            }
            if (mandatorySlide.equalsIgnoreCase("1")) {
                if (PlaySlideDetailedAdapter.playedMandatorySlideIds.isEmpty()) {
                    Toast.makeText(this, "Please view mandatory slides", Toast.LENGTH_LONG).show();
                    return;
                }
            }
            if (!pendingSlides.isEmpty()) {
                Toast.makeText(this, "Mandatory slides pending: " + TextUtils.join(", ", pendingSlides), Toast.LENGTH_LONG).show();
                return;
            }

            previewBinding.rlThankYou.setVisibility(View.VISIBLE);
            String DrDetCap = SharedPref.getDetDrCap(PreviewActivity.this);
            String UlDrDetCap = SharedPref.getDetUldrCap(PreviewActivity.this);
            if(CusType.equalsIgnoreCase("1") || CusType.equalsIgnoreCase("4")) {
                if (CusType.equalsIgnoreCase("1") && !DrDetCap.isEmpty() || !DrDetCap.equalsIgnoreCase("null")) {
                    previewBinding.docName.setText("Thank You\n" + DrDetCap + " " + CallActivityCustDetails.get(0).getName());
                } else if (CusType.equalsIgnoreCase("4") && !UlDrDetCap.isEmpty() || !UlDrDetCap.equalsIgnoreCase("null")){
                    previewBinding.docName.setText("Thank You\n" + UlDrDetCap + " " + CallActivityCustDetails.get(0).getName());
                }else {
                    previewBinding.docName.setText("Thank You\n" + "Dr." + " " + CallActivityCustDetails.get(0).getName());
                }
            }else{
                previewBinding.docName.setText("Thank You\n"+ " " + CallActivityCustDetails.get(0).getName());
            }
            previewBinding.btnFinishDet.setVisibility(View.GONE);
//            @Override
//            public void onSafeClick(View view) {
//            }
        });

        previewBinding.proceed.setOnClickListener(view -> {
            Collections.sort(arrayStore, new StoreImageTypeUrl.StoreImageComparator());
            String totalDuration = "";
            for (int j = 0; j < arrayStore.size(); j++) {
                if (j == 0) {
                    gettingProductStartEndTime(arrayStore.get(j).getRemTime(), j);
                    finalPrdNam = arrayStore.get(j).getBrdName();
                } else if (finalPrdNam.equalsIgnoreCase(arrayStore.get(j).getBrdName())) {
                    try {
                        JSONArray jsonArray = new JSONArray(arrayStore.get(j - 1).getRemTime());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String duration = TimeUtils.timeDurationHMS(jsonArray.optJSONObject(i).optString("sT"), jsonArray.optJSONObject(i).optString("eT"));
                            totalDuration = TimeUtils.addTime(totalDuration, duration);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    String time = gettingProductStartEndTime(arrayStore.get(j).getRemTime(), j) + " " + gettingProductTiming(arrayStore.get(j - 1).getBrdName());
                    if (time.contains("00:00:00")) {
                        time = time.replace("00:00:00", time.substring(0, 8));
                    }
                    Log.v("printing_all_time", time);
                    try {
                        JSONArray jsonArray = new JSONArray(arrayStore.get(j - 1).getRemTime());
                        for (int i = 0; i < jsonArray.length(); i++) {
                            String duration = TimeUtils.timeDurationHMS(jsonArray.optJSONObject(i).optString("sT"), jsonArray.optJSONObject(i).optString("eT"));
                            totalDuration = TimeUtils.addTime(totalDuration, duration);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    if (!time.isEmpty()) {
                        callDetailingLists.add(new CallDetailingList(arrayStore.get(j - 1).getBrdName(), arrayStore.get(j - 1).getBrdCode(), arrayStore.get(j - 1).getSlideNam(), arrayStore.get(j - 1).getSlideTyp(), arrayStore.get(j - 1).getSlideUrl(), time, time.substring(0, 8), 0, "", CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd"), totalDuration));
                    }
                    finalPrdNam = arrayStore.get(j).getBrdName();
                    totalDuration = "";
                }
            }

            if (!arrayStore.isEmpty()) {
                try {
                    JSONArray jsonArray = new JSONArray(arrayStore.get(arrayStore.size() - 1).getRemTime());
                    for (int i = 0; i < jsonArray.length(); i++) {
                        String duration = TimeUtils.timeDurationHMS(jsonArray.optJSONObject(i).optString("sT"), jsonArray.optJSONObject(i).optString("eT"));
                        totalDuration = TimeUtils.addTime(totalDuration, duration);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                String time = gettingProductStartEndTime1(arrayStore.get(arrayStore.size() - 1).getRemTime(), arrayStore.size() - 1) + " " + gettingProductTiming(arrayStore.get(arrayStore.size() - 1).getBrdName());
                if (time != null && !time.isEmpty() && !time.equalsIgnoreCase("null")&& (!time.equalsIgnoreCase(" " ))) {
                    callDetailingLists.add(new CallDetailingList(arrayStore.get(arrayStore.size() - 1).getBrdName(), arrayStore.get(arrayStore.size() - 1).getBrdCode(), arrayStore.get(arrayStore.size() - 1).getSlideNam(), arrayStore.get(arrayStore.size() - 1).getSlideTyp(), arrayStore.get(arrayStore.size() - 1).getSlideUrl(), time, time.substring(0, 8), 0, "", CommonUtilsMethods.getCurrentInstance("yyyy-MM-dd"), totalDuration));
                }
            }
            Set<String> detailedProducts = new HashSet<>();
            for (StoreImageTypeUrl storeImageTypeUrl : arrayStore) {
                String[] productCode = storeImageTypeUrl.getProductCode().split(",");
                detailedProducts.addAll(Arrays.asList(productCode));
            }
            Log.d("Slide detailed", "onCreate: " + detailedProducts.toString());
            Intent intent1 = new Intent(PreviewActivity.this, DCRCallActivity.class);
            intent1.putExtra(Constants.DETAILING_REQUIRED, "true");
            intent1.putExtra(Constants.DCR_FROM_ACTIVITY, "new");
            intent1.putExtra("DetailedProducts", detailedProducts.stream().collect(Collectors.joining(",")));
            intent1.putExtra("remainder_save", "0");
            intent1.putExtra("hq_code", "");
            intent1.putExtra("CheckInJsonObject", checkInJsonObject.toString());
//                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            callOfflineDataDao.saveOfflineCallIN(HomeDashBoard.selectedDate.toString(), CommonUtilsMethods.getCurrentInstance("hh:mm aa"), CallActivityCustDetails.get(0).getCode(), CallActivityCustDetails.get(0).getName(), CallActivityCustDetails.get(0).getType());
            startActivity(intent1);
        });

    }

    private void viewSideScreen(String customerType, String presentationName) {
        previewBinding.getRoot().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        PresentationDataTable presentationDataTable = presentationDataDao.getPresentationData(presentationName);

        previewBinding.navigationView.imgSideClose.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                closeDrawer();
            }
        });

        String selectedCap = "";
        switch (customerType) {
    /*        case Constants.DOCTOR:
                selectedCap = SharedPref.getDrCap(this);
                break;
            case Constants.CHEMIST:
                selectedCap = SharedPref.getChmCap(this);
                break;
            case Constants.STOCKIEST:
                selectedCap = SharedPref.getStkCap(this);
                break;
            case Constants.UNLISTED_DOCTOR:
                selectedCap = SharedPref.getUNLcap(this);
                break;*/
            case Constants.DOCTOR_MAS:
                selectedCap = SharedPref.getDrCap(this);
                break;
            case Constants.CHEMIST_MAS:
                selectedCap = SharedPref.getChmCap(this);
                break;
            case Constants.STOCKIEST_MAS:
                selectedCap = SharedPref.getStkCap(this);
                break;
            case Constants.UNLISTED_DOCTOR_MAS:
                selectedCap = SharedPref.getUNLcap(this);
                break;
            case Constants.CIP:
                selectedCap = SharedPref.getCipCaption(this);
                break;
            case Constants.HOSPITAL:
                selectedCap = SharedPref.getHospCaption(this);
                break;
        }

        previewBinding.navigationView.tvSideTitle.setText(String.format("Selected %s", selectedCap));
        if (SharedPref.getSfType(this).equalsIgnoreCase("2")) {
            previewBinding.navigationView.tvSideHq.setText(getHQName(presentationDataTable.getHeadquarterCode()));
            previewBinding.navigationView.tvSideHq.setVisibility(View.VISIBLE);
        } else {
            previewBinding.navigationView.tvSideHq.setVisibility(View.GONE);
        }
        previewBinding.navigationView.rvSide.setVisibility(View.VISIBLE);
        previewBinding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
        previewBinding.drawerLayout.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        ArrayList<CustomerDataModel> customerDataList = new ArrayList<>();
        JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(customerType + presentationDataTable.getHeadquarterCode()).getMasterSyncDataJsonArray();
        if (jsonArray.length() == 0) {
            commonUtilsMethods.showToastMessage(this, this.getString(R.string.no_data_found) + "  " + this.getString(R.string.do_master_sync));
        } else {
            try {
                Set<String> customerCodes1 = new HashSet<>();
                String code = "";
                for (int i = 0; i < jsonArray.length(); i++) {
                    try {
                        JSONObject jsonObject = jsonArray.optJSONObject(i);
                        code = jsonObject.optString("Code");
                        if (!customerCodes1.contains(code)) {
                            customerCodes1.add(code);
                            CustomerDataModel customerDataModel = new CustomerDataModel(jsonObject.optString("Name"), jsonObject.optString("Code"), jsonObject.optString("Town_Name"), jsonObject.optString("Town_Code"), "", "", "", "", "", "");
                            if (presentationDataTable.getCustomerCodes() != null && !presentationDataTable.getCustomerCodes().isEmpty() && presentationDataTable.getCustomerCodes().contains(code)) {
                                customerDataModel.setSelected(true);
                                customerDataList.add(customerDataModel);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        sideScreenAdapter = new SideScreenAdapter(this, customerDataList);
        previewBinding.navigationView.rvSide.setAdapter(sideScreenAdapter);
        previewBinding.navigationView.rvSide.setLayoutManager(new LinearLayoutManager(this));
        sideScreenAdapter.notifyDataSetChanged();

        previewBinding.navigationView.btnEdit.setVisibility(View.GONE);
    }

    private String getHQName(String hqCode) {
        try {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray();
            ArrayList<String> list = new ArrayList<>();
            if (jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.optJSONObject(i);
                    if (jsonObject.optString("id").equalsIgnoreCase(hqCode)) {
                        return jsonObject.optString("name");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private void closeDrawer() {
        previewBinding.drawerLayout.closeDrawer(GravityCompat.END);
        previewBinding.navigationView.etSearch.getText().clear();
        UtilityClass.hideKeyboard(this);
    }

    private void getRequiredData() {
        try {
//            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CUSTOM_SETUP).getMasterSyncDataJsonArray();
//            for (int i = 0; i < jsonArray.length(); i++) {
//                JSONObject setupData = jsonArray.optJSONObject(0);
//                customSetupResponse = new CustomSetupResponse();
//                Type typeSetup = new TypeToken<CustomSetupResponse>() {
//                }.getType();
//                customSetupResponse = new Gson().fromJson(String.valueOf(setupData), typeSetup);
//                CustomPresentationNeed = customSetupResponse.getCustomizationPrsNeed();
//                therapticNeed = customSetupResponse.getTherapaticNeed();
//            }
            presentationNeed = SharedPref.getPresentationNeed(this);
            therapticNeed = SharedPref.getTherapticPresentationNeed(this);
        } catch (Exception ignored) {
        }
    }


    public String gettingProductTiming(String BrandName) {
        String maxTime = null;
        String minTime = null;
        dummyArr.clear();
        try {
            for (int i = 0; i < arrayStore.size(); i++) {
                if (arrayStore.get(i).getBrdName().equalsIgnoreCase(BrandName)) {
                    dummyArr.add(new StoreImageTypeUrl(arrayStore.get(i).getScribble(), arrayStore.get(i).getSlideNam(), arrayStore.get(i).getSlideTyp(), arrayStore.get(i).getSlideUrl(), arrayStore.get(i).getRemTime(), arrayStore.get(i).getSlideComments(), arrayStore.get(i).getTiming(), arrayStore.get(i).getFlag()));
                }
            }
            ArrayList<String> timesMax = new ArrayList<>();
            ArrayList<String> timesMin = new ArrayList<>();
            for (int i1 = 0; i1 < dummyArr.size(); i1++) {
                StoreImageTypeUrl mm1 = dummyArr.get(i1);
                JSONArray jj = new JSONArray(mm1.getTiming());

                if (jj.length() > 0) {
                    JSONObject jsr = jj.optJSONObject(jj.length() - 1);
                    timesMax.add(jsr.optString("eT"));
                    timesMin.add(jsr.optString("sT"));
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

    public String gettingProductStartEndTime1(String jsonvalue, int i) {
        String finalTime = "";
        StoreImageTypeUrl mm, mm1, mm2;
        try {
            JSONArray json = new JSONArray(jsonvalue);
            JSONArray json2 = new JSONArray(jsonvalue);
            mm = arrayStore.get(i);
            json = new JSONArray(mm.getRemTime());
            if (!json.toString().equals("[]")) {
                JSONObject jjj = json.optJSONObject(0);
                Log.v("last_value_time", jjj.optString("sT"));
                startT = jjj.optString("sT");
                //  finalTime = startT + " " + jjj.optString("eT");
                finalTime = startT;
            }
            if (i == arrayStore.size() - 1) {
                mm1 = arrayStore.get(arrayStore.size() - 1);
                json = new JSONArray(mm1.getRemTime());
                if (!json.toString().equals("[]")) {
                    JSONObject jj = json.optJSONObject(0);
                    endT = jj.optString("eT");
                    for (int j = 0; j < i; j++) {
                        if (arrayStore.get(j).getBrdName().equals(mm1.getBrdName())) {
                            mm2 = arrayStore.get(j);
                            json2 = new JSONArray(mm2.getRemTime());
                            if (!json2.toString().equals("[]")) {
                                JSONObject jj2 = json2.optJSONObject(0);
                                startT = jj2.optString("sT");
                            }
                            break;
                        }
                    }
                    finalTime = startT;
                }
            }
            return finalTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalTime;
    }

    public String gettingProductStartEndTime(String jsonvalue, int i) {
        String finalTime = null;
        StoreImageTypeUrl mm, mm1;
        try {
            JSONArray json;
            if (i != 0) {
                mm1 = arrayStore.get(i - 1);
                json = new JSONArray(mm1.getRemTime());
                if (!json.toString().equals("[]")) {
                    JSONObject jj = json.optJSONObject(0);
                    endT = jj.optString("eT");
                }
            }
            finalTime = startT;
            mm = arrayStore.get(i);
            json = new JSONArray(mm.getRemTime());
            if (!json.toString().equals("[]")) {
                JSONObject jj = json.optJSONObject(0);
                Log.v("last_value_timemid", jj.optString("sT"));
                startT = jj.optString("sT");
            }
            if (arrayStore.size() == 1) {
                mm = arrayStore.get(i);
                json = new JSONArray(mm.getRemTime());
                if (!json.toString().equals("[]")) {
                    JSONObject jjj = json.optJSONObject(0);
                    Log.v("last_value_time", jjj.optString("sT"));
                    startT = jjj.optString("sT");
                    finalTime = startT;
                }
            }
            return finalTime;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return finalTime;
    }

}
