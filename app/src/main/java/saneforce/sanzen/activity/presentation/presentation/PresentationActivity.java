package saneforce.sanzen.activity.presentation.presentation;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager.widget.ViewPager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.activity.call.dcrCallSelection.adapter.TabLayoutAdapter;
import saneforce.sanzen.activity.presentation.createPresentation.BrandModelClass;
import saneforce.sanzen.activity.presentation.customerSelection.CustomerSelectionActivity;
import saneforce.sanzen.activity.presentation.customerSelection.model.CustomerDataModel;
import saneforce.sanzen.activity.presentation.presentation.adapter.PresentationAdapter;
import saneforce.sanzen.activity.presentation.presentation.adapter.SideScreenAdapter;
import saneforce.sanzen.activity.presentation.presentation.fragments.CIPPresentationFragment;
import saneforce.sanzen.activity.presentation.presentation.fragments.ChemistPresentationFragment;
import saneforce.sanzen.activity.presentation.presentation.fragments.CommonPresentationFragment;
import saneforce.sanzen.activity.presentation.presentation.fragments.DoctorPresentationFragment;
import saneforce.sanzen.activity.presentation.presentation.fragments.HospitalPresentationFragment;
import saneforce.sanzen.activity.presentation.presentation.fragments.StockistPresentationFragment;
import saneforce.sanzen.activity.presentation.presentation.fragments.UnListedDoctorPresentationFragment;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.commonClasses.UtilityClass;
import saneforce.sanzen.databinding.ActivityPresentationBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.PresentationTableDetails.PresentationDataDao;
import saneforce.sanzen.roomdatabase.PresentationTableDetails.PresentationDataTable;
import saneforce.sanzen.roomdatabase.RoomDB;
import saneforce.sanzen.storage.SharedPref;


public class PresentationActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    public static ActivityPresentationBinding binding;
    PresentationAdapter presentationAdapter;
    ArrayList<BrandModelClass.Presentation> savedPresentation = new ArrayList<>();
    CommonUtilsMethods commonUtilsMethods;
    private RoomDB roomDB;
    private MasterDataDao masterDataDao;
    private PresentationDataDao presentationDataDao;
    public static int selectedPosition = 0;
    private TabLayoutAdapter viewPagerAdapter;
    private SideScreenAdapter sideScreenAdapter;
    private String drCap, chmCap, stkCap, unlDrCap, cipCap, hosCap;

    //To Hide the bottomNavigation When popup
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if(hasFocus) {
            binding.getRoot().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            if(getWindow() != null) {
                getWindow().getDecorView().setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.viewPagerCallSelection.setCurrentItem(selectedPosition);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPresentationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        commonUtilsMethods = new CommonUtilsMethods(this);
        commonUtilsMethods.setUpLanguage(this);
        roomDB = RoomDB.getDatabase(this);
        presentationDataDao = roomDB.presentationDataDao();
        masterDataDao = roomDB.masterDataDao();
        savedPresentation = presentationDataDao.getPresentations();

        drCap = SharedPref.getDrCap(this);
        chmCap = SharedPref.getChmCap(this);
        stkCap = SharedPref.getStkCap(this);
        unlDrCap = SharedPref.getUNLcap(this);
        cipCap = SharedPref.getCipCaption(this);
        hosCap = SharedPref.getHospCaption(this);

        viewPagerAdapter = new TabLayoutAdapter(getSupportFragmentManager());
        viewPagerAdapter.add(new CommonPresentationFragment(), "Common");
        if(SharedPref.getDrNeed(context).equalsIgnoreCase("0")) {
            viewPagerAdapter.add(new DoctorPresentationFragment(this::viewSideScreen), drCap);
        }
        if(SharedPref.getChmNeed(context).equalsIgnoreCase("0")) {
            viewPagerAdapter.add(new ChemistPresentationFragment(this::viewSideScreen), chmCap);
        }
        if(SharedPref.getStkNeed(context).equalsIgnoreCase("0")) {
            viewPagerAdapter.add(new StockistPresentationFragment(this::viewSideScreen), stkCap);
        }
        if(SharedPref.getUnlNeed(context).equalsIgnoreCase("0")) {
            viewPagerAdapter.add(new UnListedDoctorPresentationFragment(this::viewSideScreen), unlDrCap);
        }
        if(SharedPref.getCipNeed(context).equalsIgnoreCase("0")) {
            viewPagerAdapter.add(new CIPPresentationFragment(this::viewSideScreen), cipCap);
        }
        if(SharedPref.getHospNeed(context).equalsIgnoreCase("0")) {
            viewPagerAdapter.add(new HospitalPresentationFragment(this::viewSideScreen), hosCap);
        }

        binding.viewPagerCallSelection.setAdapter(viewPagerAdapter);
        binding.tabLayoutCall.setupWithViewPager(binding.viewPagerCallSelection);
        binding.viewPagerCallSelection.setOffscreenPageLimit(7);

        binding.viewPagerCallSelection.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                selectedPosition = position;
            }

            @Override
            public void onPageSelected(int position) {
                selectedPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

//        populateAdapter();

        binding.backArrow.setOnClickListener(new SafeClickListener() {
                                                 @Override
                                                 public void onSafeClick(View view) {
                                                     selectedPosition = 0;
                                                     getOnBackPressedDispatcher().onBackPressed();
                                                 }
        });

//        binding.createPresentationBtn.setOnClickListener(view -> startActivity(new Intent(PresentationActivity.this, CreatePresentationActivity.class)));
    }

//    public void populateAdapter() {
//        if (savedPresentation.size() > 0) {
//            binding.constraintNoData.setVisibility(View.GONE);
//            binding.presentationRecView.setVisibility(View.VISIBLE);
//            presentationAdapter = new PresentationAdapter(this, savedPresentation, "presentation");
//            binding.presentationRecView.setLayoutManager(new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false));
//            binding.presentationRecView.setAdapter(presentationAdapter);
//        } else {
//            binding.constraintNoData.setVisibility(View.VISIBLE);
//            binding.presentationRecView.setVisibility(View.GONE);
//        }
//    }

    private void viewSideScreen(String customerType, String presentationName) {
        binding.getRoot().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        PresentationDataTable presentationDataTable = presentationDataDao.getPresentationData(presentationName);

        binding.navigationView.imgSideClose.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                closeDrawer();
            }
        });

        String selectedCap = "";
        switch (customerType){
  /*          case Constants.DOCTOR:
                selectedCap = drCap;
                break;
            case Constants.CHEMIST:
                selectedCap = chmCap;
                break;
            case Constants.STOCKIEST:
                selectedCap = stkCap;
                break;
            case Constants.UNLISTED_DOCTOR:
                selectedCap = unlDrCap;
                break;*/
            case Constants.DOCTOR_MAS:
                selectedCap = drCap;
                break;
            case Constants.CHEMIST_MAS:
                selectedCap = chmCap;
                break;
            case Constants.STOCKIEST_MAS:
                selectedCap = stkCap;
                break;
            case Constants.UNLISTED_DOCTOR_MAS:
                selectedCap = unlDrCap;
                break;
            case Constants.CIP:
                selectedCap = cipCap;
                break;
            case Constants.HOSPITAL:
                selectedCap = hosCap;
                break;
        }

        binding.navigationView.tvSideTitle.setText(String.format("Selected %s", selectedCap));
        if(SharedPref.getSfType(this).equalsIgnoreCase("2")) {
            binding.navigationView.tvSideHq.setText(getHQName(presentationDataTable.getHeadquarterCode()));
            binding.navigationView.tvSideHq.setVisibility(View.VISIBLE);
        } else {
            binding.navigationView.tvSideHq.setVisibility(View.GONE);
        }
        binding.navigationView.rvSide.setVisibility(View.VISIBLE);
        binding.drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_OPEN);
        binding.drawerLayout.setSystemUiVisibility(
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
        if(jsonArray.length() == 0) {
            commonUtilsMethods.showToastMessage(this, this.getString(R.string.no_data_found) + "  " + this.getString(R.string.do_master_sync));
        }else {
            try {
                Set<String> customerCodes1 = new HashSet<>();
                String code = "";
                for (int i = 0; i<jsonArray.length(); i++) {
                    try {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        code = jsonObject.getString("Code");
                        if(!customerCodes1.contains(code)) {
                            customerCodes1.add(code);
                            CustomerDataModel customerDataModel = new CustomerDataModel(jsonObject.getString("Name"), jsonObject.getString("Code"), jsonObject.getString("Town_Name"), jsonObject.getString("Town_Code"), "", "", "", "", "", "");
                            if(presentationDataTable.getCustomerCodes() != null && !presentationDataTable.getCustomerCodes().isEmpty() && presentationDataTable.getCustomerCodes().contains(code)) {
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
        binding.navigationView.rvSide.setAdapter(sideScreenAdapter);
        binding.navigationView.rvSide.setLayoutManager(new LinearLayoutManager(this));
        sideScreenAdapter.notifyDataSetChanged();

        binding.navigationView.btnEdit.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                Intent intent = new Intent(PresentationActivity.this, CustomerSelectionActivity.class);
                intent.putExtra(CustomerSelectionActivity.PRESENTATION_NAME, presentationName);
                intent.putExtra(CustomerSelectionActivity.CUSTOMER_TYPE, customerType);
                intent.putExtra(CustomerSelectionActivity.HEAD_QUARTER_CODE, presentationDataTable.getHeadquarterCode());
                intent.putExtra(CustomerSelectionActivity.IS_FROM, "edit");
                startActivity(intent);
                closeDrawer();
            }
        });
    }

    private String getHQName(String hqCode) {
        try {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.SUBORDINATE).getMasterSyncDataJsonArray();
            ArrayList<String> list = new ArrayList<>();
            if(jsonArray.length()>0) {
                for (int i = 0; i<jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    if(jsonObject.optString("id").equalsIgnoreCase(hqCode)) {
                        return jsonObject.optString("name");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getUnListedClassName(String unlDocClsCode) {
        try {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.SPECIALITY).getMasterSyncDataJsonArray();
            for (int i = 0; i<jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("Name");
                String code = jsonObject.getString("Code");
                if(code.equalsIgnoreCase(unlDocClsCode))
                    return name;
            }
        } catch (Exception e) {
            Log.e("Customer Selection", "getUnListedClassName: " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }

    private String getChemistCategory(String catCode) {
        try {
            JSONArray jsonArray = masterDataDao.getMasterDataTableOrNew(Constants.CATEGORY_CHEMIST).getMasterSyncDataJsonArray();
            for (int i = 0; i<jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String name = jsonObject.getString("Name");
                String code = jsonObject.getString("Code");
                if(code.equalsIgnoreCase(catCode))
                    return name;
            }
        } catch (Exception e) {
            Log.e("Customer Selection", "getChemistCategory: " + e.getMessage());
            e.printStackTrace();
        }
        return "";
    }

    private void closeDrawer() {
        binding.drawerLayout.closeDrawer(GravityCompat.END);
        binding.navigationView.etSearch.getText().clear();
        UtilityClass.hideKeyboard(this);
    }
}