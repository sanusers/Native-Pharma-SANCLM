package saneforce.sanzen.activity.presentation.presentation;

import static com.gun0912.tedpermission.provider.TedPermissionProvider.context;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;

import saneforce.sanzen.activity.call.dcrCallSelection.adapter.TabLayoutAdapter;
import saneforce.sanzen.activity.call.dcrCallSelection.fragments.CIPFragment;
import saneforce.sanzen.activity.call.dcrCallSelection.fragments.ChemistFragment;
import saneforce.sanzen.activity.call.dcrCallSelection.fragments.HospitalFragment;
import saneforce.sanzen.activity.call.dcrCallSelection.fragments.ListedDoctorFragment;
import saneforce.sanzen.activity.call.dcrCallSelection.fragments.StockiestFragment;
import saneforce.sanzen.activity.call.dcrCallSelection.fragments.UnlistedDoctorFragment;
import saneforce.sanzen.activity.presentation.createPresentation.BrandModelClass;
import saneforce.sanzen.activity.presentation.createPresentation.CreatePresentationActivity;
import saneforce.sanzen.activity.presentation.presentation.fragments.CIPPresentationFragment;
import saneforce.sanzen.activity.presentation.presentation.fragments.ChemistPresentationFragment;
import saneforce.sanzen.activity.presentation.presentation.fragments.DoctorPresentationFragment;
import saneforce.sanzen.activity.presentation.presentation.fragments.HospitalPresentationFragment;
import saneforce.sanzen.activity.presentation.presentation.fragments.StockistPresentationFragment;
import saneforce.sanzen.activity.presentation.presentation.fragments.UnListedDoctorPresentationFragment;
import saneforce.sanzen.commonClasses.CommonUtilsMethods;
import saneforce.sanzen.databinding.ActivityPresentationBinding;
import saneforce.sanzen.roomdatabase.MasterTableDetails.MasterDataDao;
import saneforce.sanzen.roomdatabase.PresentationTableDetails.PresentationDataDao;
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

    TabLayoutAdapter viewPagerAdapter;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPresentationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());
        commonUtilsMethods.setUpLanguage(getApplicationContext());
        roomDB = RoomDB.getDatabase(this);
        presentationDataDao = roomDB.presentationDataDao();
        masterDataDao = roomDB.masterDataDao();
        savedPresentation = presentationDataDao.getPresentations();

        viewPagerAdapter = new TabLayoutAdapter(getSupportFragmentManager());
        if (SharedPref.getDrNeed(context).equalsIgnoreCase("0")) {
            viewPagerAdapter.add(new DoctorPresentationFragment(), SharedPref.getDrCap(context));
        }
        if (SharedPref.getChmNeed(context).equalsIgnoreCase("0")) {
            viewPagerAdapter.add(new ChemistPresentationFragment(), SharedPref.getChmCap(context));
        }
        if (SharedPref.getStkNeed(context).equalsIgnoreCase("0")) {
            viewPagerAdapter.add(new StockistPresentationFragment(), SharedPref.getStkCap(context));
        }
        if (SharedPref.getUnlNeed(context).equalsIgnoreCase("0")) {
            viewPagerAdapter.add(new UnListedDoctorPresentationFragment(), SharedPref.getUNLcap(context));
        }
        if (SharedPref.getCipNeed(context).equalsIgnoreCase("0")) {
            viewPagerAdapter.add(new CIPPresentationFragment(), SharedPref.getCipCaption(context));
        }
        if (SharedPref.getHospNeed(context).equalsIgnoreCase("0")) {
            viewPagerAdapter.add(new HospitalPresentationFragment(), SharedPref.getHospCaption(context));
        }

        binding.viewPagerCallSelection.setAdapter(viewPagerAdapter);
        binding.tabLayoutCall.setupWithViewPager(binding.viewPagerCallSelection);
        binding.viewPagerCallSelection.setOffscreenPageLimit(7);

        populateAdapter();

        binding.backArrow.setOnClickListener(view -> getOnBackPressedDispatcher().onBackPressed());

        binding.createPresentationBtn.setOnClickListener(view -> startActivity(new Intent(PresentationActivity.this, CreatePresentationActivity.class)));
    }

    public void populateAdapter() {
        if (savedPresentation.size() > 0) {
            binding.constraintNoData.setVisibility(View.GONE);
            binding.presentationRecView.setVisibility(View.VISIBLE);
            presentationAdapter = new PresentationAdapter(this, savedPresentation, "presentation");
            binding.presentationRecView.setLayoutManager(new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false));
            binding.presentationRecView.setAdapter(presentationAdapter);
        } else {
            binding.constraintNoData.setVisibility(View.VISIBLE);
            binding.presentationRecView.setVisibility(View.GONE);
        }
    }
}