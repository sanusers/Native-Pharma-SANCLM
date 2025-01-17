package saneforce.sanzen.activity.Survey;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import saneforce.sanzen.databinding.ActivitySurveyBinding;

public class SurveyActivity extends AppCompatActivity {
    private ActivitySurveyBinding surveyBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        surveyBinding = ActivitySurveyBinding.inflate(getLayoutInflater());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        setContentView(surveyBinding.getRoot());
    }
}