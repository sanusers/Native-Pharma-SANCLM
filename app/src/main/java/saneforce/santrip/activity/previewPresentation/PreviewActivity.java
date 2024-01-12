package saneforce.santrip.activity.previewPresentation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedDispatcher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import saneforce.santrip.activity.homeScreen.call.DCRCallActivity;
import saneforce.santrip.activity.homeScreen.call.dcrCallSelection.DcrCallTabLayoutActivity;
import saneforce.santrip.activity.presentation.createPresentation.BrandModelClass;
import saneforce.santrip.activity.presentation.createPresentation.CreatePresentationActivity;
import saneforce.santrip.activity.previewPresentation.fragment.BrandMatrix;
import saneforce.santrip.activity.previewPresentation.fragment.Customized;
import saneforce.santrip.activity.previewPresentation.fragment.HomeBrands;
import saneforce.santrip.activity.previewPresentation.fragment.Speciality;
import saneforce.santrip.activity.profile.CustomerProfile;
import saneforce.santrip.commonClasses.Constants;
import saneforce.santrip.storage.SQLite;

public class PreviewActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    public static saneforce.santrip.databinding.ActivityPreviewBinding previewBinding;
    PreviewTabAdapter viewPagerAdapter;
    SQLite sqLite;

    public static String SelectedTab = "", from_where = "", cus_name = "", SpecialityCode = "", BrandCode = "", SlideCode = "", CusType = "";

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        previewBinding = saneforce.santrip.databinding.ActivityPreviewBinding.inflate(getLayoutInflater());
        setContentView(previewBinding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        sqLite = new SQLite(getApplicationContext());

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            from_where = extra.getString("from");
            assert from_where != null;
            if (from_where.equalsIgnoreCase("call")) {
                cus_name = extra.getString("cus_name");
                SpecialityCode = extra.getString("SpecialityCode");
                BrandCode = extra.getString("MappedProdCode");
                SlideCode = extra.getString("MappedSlideCode");
                CusType = extra.getString("CusType");
                previewBinding.tagCustName.setText(cus_name);
                previewBinding.btnFinishDet.setVisibility(View.VISIBLE);
            }
        }

        getRequiredData();

        viewPagerAdapter = new PreviewTabAdapter(getSupportFragmentManager());

        if (from_where.equalsIgnoreCase("call")) {
            if (CusType.equalsIgnoreCase("1")) {
                viewPagerAdapter.add(new HomeBrands(), "Home");
                viewPagerAdapter.add(new BrandMatrix(), "Brand Matrix");
                viewPagerAdapter.add(new Speciality(), "Speciality");
                viewPagerAdapter.add(new Customized(), "Custom Presentation");
            } else {
                viewPagerAdapter.add(new HomeBrands(), "Home");
                viewPagerAdapter.add(new Speciality(), "Speciality");
                viewPagerAdapter.add(new Customized(), "Custom Presentation");
            }
        } else {
            viewPagerAdapter.add(new HomeBrands(), "Home");
            viewPagerAdapter.add(new BrandMatrix(), "Brand Matrix");
            viewPagerAdapter.add(new Speciality(), "Speciality");
            viewPagerAdapter.add(new Customized(), "Custom Presentation");
        }

        previewBinding.viewPager.setAdapter(viewPagerAdapter);
        previewBinding.tabLayout.setupWithViewPager(previewBinding.viewPager);
        previewBinding.viewPager.setOffscreenPageLimit(viewPagerAdapter.getCount());

        previewBinding.ivBack.setOnClickListener(v -> getOnBackPressedDispatcher().onBackPressed());

        previewBinding.ivBack.setOnClickListener(v -> {
            if (from_where.equalsIgnoreCase("call")) {
                Intent intent = new Intent(PreviewActivity.this, DcrCallTabLayoutActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            } else {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });

        previewBinding.btnFinishDet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(PreviewActivity.this, DCRCallActivity.class);
                intent1.putExtra("isDetailedRequired", "true");
                intent1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent1);
            }
        });

    }

    private void getRequiredData() {

    }
}
