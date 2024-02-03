package saneforce.santrip.activity.presentation.presentation;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;

import saneforce.santrip.activity.presentation.createPresentation.BrandModelClass;
import saneforce.santrip.activity.presentation.createPresentation.CreatePresentationActivity;
import saneforce.santrip.commonClasses.CommonUtilsMethods;
import saneforce.santrip.databinding.ActivityPresentationBinding;
import saneforce.santrip.storage.SQLite;


public class PresentationActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    public static ActivityPresentationBinding binding;
    PresentationAdapter presentationAdapter;
    SQLite sqLite;
    ArrayList<BrandModelClass.Presentation> savedPresentation = new ArrayList<>();
    CommonUtilsMethods commonUtilsMethods;

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
        sqLite = new SQLite(this);
        savedPresentation = sqLite.getPresentationData();
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