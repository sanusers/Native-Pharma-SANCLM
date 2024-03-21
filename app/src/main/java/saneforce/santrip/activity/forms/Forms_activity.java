package saneforce.santrip.activity.forms;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

import saneforce.santrip.R;
import saneforce.santrip.activity.homeScreen.HomeDashBoard;
import saneforce.santrip.commonClasses.CommonUtilsMethods;

public class Forms_activity extends AppCompatActivity {
    ArrayList<Formsmodel_class> frmlisted_data = new ArrayList<>();
    Forms_adapter frm_adapter;
    RecyclerView forms_id;
    ImageView back_btn;
    LinearLayout backArrow;

    TabLayout tabLayout;
    CommonUtilsMethods commonUtilsMethods;


    //To Hide the bottomNavigation When popup
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            backArrow.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forms);
        forms_id = findViewById(R.id.forms_id);
        back_btn = findViewById(R.id.back_btn);
        backArrow = findViewById(R.id.backArrow);


        tabLayout = findViewById(R.id.tablelayout);
        commonUtilsMethods = new CommonUtilsMethods(getApplicationContext());
        commonUtilsMethods.setUpLanguage(getApplicationContext());

        backArrow.setOnClickListener(v -> {
            getOnBackPressedDispatcher().onBackPressed();
        });

        frmlisted_data.clear();
        frmlisted_data.add(new Formsmodel_class("Leave details", R.drawable.leavedetails));
        frmlisted_data.add(new Formsmodel_class("Next Visit", R.drawable.nextvisit));
        frmlisted_data.add(new Formsmodel_class("Holiday / Weekly off", R.drawable.vacation));
        frmlisted_data.add(new Formsmodel_class("Tour Plan View", R.drawable.calendar_clock));


        frm_adapter = new Forms_adapter(frmlisted_data, Forms_activity.this);
        forms_id.setItemAnimator(new DefaultItemAnimator());
        forms_id.setLayoutManager(new GridLayoutManager(Forms_activity.this, 4, GridLayoutManager.VERTICAL, false));
        forms_id.setAdapter(frm_adapter);

//        TabLayoutMediator mediator = new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(""));
//        mediator.attach();
//        setupCustomTab(tabLayout, 0, "Holiday", false);
//        setupCustomTab(tabLayout, 1, "Weekly Off", false);
    }


    private void setupCustomTab(TabLayout tabLayout, int tabIndex, String tabTitleText, boolean isTabTitleInvisible) {
        TabLayout.Tab tab = tabLayout.getTabAt(tabIndex);
        if (tab != null) {
            @SuppressLint("InflateParams") View customView = LayoutInflater.from(this).inflate(R.layout.customtab_item, null);
            tab.setCustomView(customView);
            TextView tabTitle = customView.findViewById(R.id.tablayname);
            if (tabIndex == 0) {
                tabTitle.setTextColor(getResources().getColor(R.color.text_dark));
            }

            tabTitle.setText(tabTitleText);
            TextView tabTitleInvisible = customView.findViewById(R.id.tv_filter_count);
            tabTitleInvisible.setVisibility(isTabTitleInvisible ? View.VISIBLE : View.GONE);
        }
    }

}