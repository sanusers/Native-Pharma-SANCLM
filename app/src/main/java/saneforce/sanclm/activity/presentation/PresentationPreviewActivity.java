package saneforce.sanclm.activity.presentation;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import saneforce.sanclm.R;
import saneforce.sanclm.databinding.ActivityPresentationPreviewBinding;

public class PresentationPreviewActivity extends AppCompatActivity {

    ActivityPresentationPreviewBinding binding;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPresentationPreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


    }
}