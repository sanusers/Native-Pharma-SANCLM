package saneforce.sanclm.activity.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import java.util.ArrayList;

import saneforce.sanclm.R;
import saneforce.sanclm.activity.homeScreen.HomeDashBoard;
import saneforce.sanclm.activity.presentation.adapter.PresentationListAdapter;
import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.databinding.ActivityPresentationBinding;


public class Presentation extends AppCompatActivity {
    ActivityPresentationBinding presentationBinding;
    CommonUtilsMethods commonUtilsMethods;

    @Override
    protected void onResume() {
        super.onResume();
    }

    ArrayList<PresentationStoredModel> presentationStoredModels = new ArrayList<>();
    PresentationListAdapter presentationListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presentationBinding = ActivityPresentationBinding.inflate(getLayoutInflater());
        setContentView(presentationBinding.getRoot());
        commonUtilsMethods = new CommonUtilsMethods(this);
        presentationBinding.ivBack.setOnClickListener(view -> startActivity(new Intent(Presentation.this, HomeDashBoard.class)));

        presentationBinding.tvCreatePresentation.setOnClickListener(view -> startActivity(new Intent(Presentation.this, CreatePresentation.class)));

        presentationStoredModels.add(new PresentationStoredModel("Presentation_1", "12 Asserts"));
        presentationStoredModels.add(new PresentationStoredModel("Presentation_2", "9 Asserts"));
        presentationStoredModels.add(new PresentationStoredModel("Presentation_3", "8 Asserts"));
        presentationStoredModels.add(new PresentationStoredModel("Presentation_4", "10 Asserts"));
        presentationStoredModels.add(new PresentationStoredModel("Presentation_5", "3 Asserts"));

        presentationListAdapter = new PresentationListAdapter(this, presentationStoredModels);
        presentationBinding.rvPresentationList.setLayoutManager(new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false));
        presentationBinding.rvPresentationList.setAdapter(presentationListAdapter);

    }


}