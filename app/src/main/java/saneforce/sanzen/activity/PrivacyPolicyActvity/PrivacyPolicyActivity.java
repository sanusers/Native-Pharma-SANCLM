package saneforce.sanzen.activity.PrivacyPolicyActvity;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.login.LoginActivity;
import saneforce.sanzen.commonClasses.Constants;
import saneforce.sanzen.databinding.ActivityPrivacypolicyBinding;
import saneforce.sanzen.databinding.ActivityTourPlanBinding;
import saneforce.sanzen.storage.SharedPref;

public class PrivacyPolicyActivity extends AppCompatActivity {



    ActivityPrivacypolicyBinding binding;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityPrivacypolicyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        binding.submitPrivacy.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.bg_grey)));
        binding.privacyWebview.loadUrl("https://sansfe.info/sanzen_privacy.html");
        binding.submitPrivacy.setEnabled(false);
        binding.privacyCheckBox.setOnClickListener(view -> {
            boolean checked = ((CheckBox) view).isChecked();
            if (checked) {
                binding.submitPrivacy.setEnabled(true);
                binding.submitPrivacy.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.black_45)));
            } else {
                binding.submitPrivacy.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.bg_grey)));
                binding.submitPrivacy.setEnabled(false);
            }
        });

        binding.submitPrivacy.setOnClickListener(view -> {
            SharedPref.setPolicyStaus(PrivacyPolicyActivity.this,true);
            Intent intent = new Intent(PrivacyPolicyActivity.this, LoginActivity.class);
            intent.putExtra(Constants.NAVIGATE_FROM, "Setting");
            startActivity(intent);
            finish();
        });
    }
}
