package saneforce.sanclm.activity.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;

import saneforce.sanclm.activity.homeScreen.HomeDashBoard;
import saneforce.sanclm.activity.presentation.adapter.PresentationAdapter;
import saneforce.sanclm.activity.presentation.createPresentation.CreatePresentationActivity;
import saneforce.sanclm.activity.presentation.playPreview.PlaySlidePreviewActivity;
import saneforce.sanclm.activity.presentation.preview.PresentationPreviewActivity;
import saneforce.sanclm.databinding.ActivityPresentationBinding;


public class PresentationActivity extends AppCompatActivity {
    ActivityPresentationBinding binding;
    ArrayList<PresentationStoredModel> presentationStoredModels = new ArrayList<>();
    PresentationAdapter presentationAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPresentationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        binding.backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                startActivity(new Intent(PresentationActivity.this, HomeDashBoard.class));
            }
        });

        binding.createPresentationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                startActivity(new Intent(PresentationActivity.this, CreatePresentationActivity.class));
            }
        });


        presentationStoredModels.add(new PresentationStoredModel("Presentation_1", "12 Asserts"));
        presentationStoredModels.add(new PresentationStoredModel("Presentation_2", "9 Asserts"));
        presentationStoredModels.add(new PresentationStoredModel("Presentation_3", "8 Asserts"));
        presentationStoredModels.add(new PresentationStoredModel("Presentation_4", "10 Asserts"));
        presentationStoredModels.add(new PresentationStoredModel("Presentation_5", "3 Asserts"));

        presentationAdapter = new PresentationAdapter(this, presentationStoredModels);
        binding.presentationRecView.setLayoutManager(new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false));
        binding.presentationRecView.setAdapter(presentationAdapter);

    }


}