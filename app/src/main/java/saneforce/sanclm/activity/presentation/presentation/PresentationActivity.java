package saneforce.sanclm.activity.presentation.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;

import saneforce.sanclm.activity.homeScreen.HomeDashBoard;
import saneforce.sanclm.activity.presentation.createPresentation.BrandModelClass;
import saneforce.sanclm.activity.presentation.createPresentation.CreatePresentationActivity;
import saneforce.sanclm.databinding.ActivityPresentationBinding;
import saneforce.sanclm.storage.SQLite;


public class PresentationActivity extends AppCompatActivity {
    ActivityPresentationBinding binding;
    PresentationAdapter presentationAdapter;
    SQLite sqLite;
    ArrayList<BrandModelClass.Presentation> savedPresentation = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPresentationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        sqLite = new SQLite(this);
        savedPresentation = sqLite.getPresentationData();
        populateAdapter();

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                startActivity(new Intent(PresentationActivity.this, HomeDashBoard.class));
                finish();
            }
        });

        binding.createPresentationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                startActivity(new Intent(PresentationActivity.this, CreatePresentationActivity.class));
            }
        });


    }

    public void populateAdapter(){
        presentationAdapter = new PresentationAdapter(this, savedPresentation);
        binding.presentationRecView.setLayoutManager(new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false));
        binding.presentationRecView.setAdapter(presentationAdapter);
    }


}