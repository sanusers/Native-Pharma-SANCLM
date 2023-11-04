package saneforce.sanclm.activity.presentation;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import saneforce.sanclm.commonClasses.CommonUtilsMethods;
import saneforce.sanclm.databinding.ActivityCreatePresentationBinding;


public class CreatePresentation extends AppCompatActivity {
    private ActivityCreatePresentationBinding createPresentationBinding;
    CommonUtilsMethods commonUtilsMethods;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createPresentationBinding = ActivityCreatePresentationBinding.inflate(getLayoutInflater());
        setContentView(createPresentationBinding.getRoot());
        commonUtilsMethods = new CommonUtilsMethods(this);
       // commonUtilsMethods.FullScreencall();
        createPresentationBinding.ivBack.setOnClickListener(view -> startActivity(new Intent(CreatePresentation.this,Presentation.class)));
    }
}