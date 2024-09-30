package saneforce.sanzen.activity.standardTourPlan.addListScreen;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import saneforce.sanzen.R;
import saneforce.sanzen.databinding.ActivityAddListBinding;

public class AddListActivity extends AppCompatActivity {

    private ActivityAddListBinding activityAddListBinding;

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityAddListBinding = ActivityAddListBinding.inflate(getLayoutInflater());
        setContentView(activityAddListBinding.getRoot());

        activityAddListBinding.backArrow.setOnClickListener(v -> {
            super.onBackPressed();
        });

        activityAddListBinding.btnCancel.setOnClickListener(v -> {
            super.onBackPressed();
        });

        activityAddListBinding.btnSave.setOnClickListener( v-> {

        });
    }
}