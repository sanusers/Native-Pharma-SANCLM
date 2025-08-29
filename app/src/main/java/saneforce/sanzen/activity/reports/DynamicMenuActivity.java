package saneforce.sanzen.activity.reports;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;


import java.util.ArrayList;

import saneforce.sanzen.activity.reports.dayReport.adapter.DynamicAdapter;
import saneforce.sanzen.activity.reports.dayReport.model.MenuModel;
import saneforce.sanzen.databinding.ActivityDynamicMenuBinding;
import saneforce.sanzen.databinding.ActivityReportFragContainerBinding;
import saneforce.sanzen.storage.SharedPref;

public class DynamicMenuActivity extends AppCompatActivity {

    ActivityDynamicMenuBinding binding;
    TextView title;
    ArrayList<MenuModel> menuList = new ArrayList<>();
    DynamicAdapter dynamicAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDynamicMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        title = binding.title;
        title.setText(SharedPref.getDynamicOptionCaps(DynamicMenuActivity.this));


        binding.backArrow.setOnClickListener(view -> onBackPressed());

        dynamicAdapter = new DynamicAdapter( menuList, this);

        binding.menuRecycler.setLayoutManager(new LinearLayoutManager(this));
//        binding.menuRecycler.setAdapter(dynamicAdapter);


        loadMenuFromApi();
    }

    private void loadMenuFromApi() {
        // 🚀 Example: Here you’d call Retrofit API
        // For testing, I’ll add dummy data


        // notify adapter
        dynamicAdapter.notifyDataSetChanged();
    }
}
