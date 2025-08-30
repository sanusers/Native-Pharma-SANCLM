package saneforce.sanzen.activity.reports;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.reports.dayReport.adapter.DynamicAdapter;
import saneforce.sanzen.activity.reports.dayReport.adapter.DynamicSubMenuAdapter;
import saneforce.sanzen.activity.reports.dayReport.model.SubMenuModel;

public class DynamicSubMenuActivity extends AppCompatActivity {
    ArrayList<SubMenuModel> subMenuModelArrayList = new ArrayList<>();
    RecyclerView recyclerView;
    DynamicSubMenuAdapter dynamicSubMenuAdapter;
    TextView toolbar_title;
    LinearLayout backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dynamic_submenu);

        toolbar_title = findViewById(R.id.toolbar_title);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        backArrow = findViewById(R.id.backArrow);

        backArrow.setOnClickListener(v -> {
            //show_exit_alert();
            finish();
        });

        String title = getIntent().getStringExtra("title");
        String menu_sub_details = getIntent().getStringExtra("menu_sub_details");

        toolbar_title.setText(title);
        dynamicSubMenuAdapter = new DynamicSubMenuAdapter( subMenuModelArrayList, this);

        prepare_menu_sub_details(menu_sub_details);
    }

    @SuppressLint("Range")
    private void prepare_menu_sub_details(String menu_sub_details) {

    }


}
