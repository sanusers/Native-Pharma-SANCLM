package saneforce.sanzen.activity.FAQ;

import android.os.Bundle;
import android.view.View;

import saneforce.sanzen.activity.approvals.ApprovalsActivity;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.databinding.ActivityFakeGpsBinding;
import saneforce.sanzen.databinding.ActivityFaqBinding;

public class FAQ extends ApprovalsActivity {

    ActivityFaqBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = saneforce.sanzen.databinding.ActivityFaqBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        binding.privacyWebview.loadUrl("https://san.saneforce.com/zenfaq/index.html");

        binding.backArrow.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                getOnBackPressedDispatcher().onBackPressed();
            }
        });
    }



}
