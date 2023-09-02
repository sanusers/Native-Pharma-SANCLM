package saneforce.sanclm.activity.mastersync;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import saneforce.sanclm.R;
import saneforce.sanclm.databinding.ActivityMasterSyncBinding;


public class MasterSyncActivity extends AppCompatActivity {

    ActivityMasterSyncBinding binding;


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMasterSyncBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.listedDoctor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
//                binding.listedDoctor.setCompoundDrawablesWithIntrinsicBounds( null, null, getResources().getDrawable(R.drawable.right_arrow), null);
            }
        });

        binding.chemist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {

            }
        });
    }
}