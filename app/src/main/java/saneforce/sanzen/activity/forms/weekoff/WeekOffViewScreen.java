package saneforce.sanzen.activity.forms.weekoff;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import saneforce.sanzen.R;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.activity.myresource.MyResource_Activity;

public class WeekOffViewScreen extends AppCompatActivity {

    ImageView back_btn;
    TabLayout tabLayout;
    ViewPager viewPager;
    forms_viewpager formsviewpager;
    int tab_pos = 0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weekoff_viewscreen);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        back_btn = findViewById(R.id.iv_back);


        back_btn.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                finish();
            }
        });

        Holiday_fragment holidayfragment = new Holiday_fragment();
        weekoff_fragment weekofffragment = new weekoff_fragment();

        tabLayout.setupWithViewPager(viewPager);
        //create viewpager adapter
        //here we will create inner class for adapter
        formsviewpager  = new forms_viewpager(getSupportFragmentManager(), 0);        //add fragments and set the adapter
        formsviewpager.addFragment(holidayfragment, getString(R.string.holiday));
        formsviewpager.addFragment(weekofffragment, getString(R.string.weekly_off));
        viewPager.setAdapter(formsviewpager);        //set the icons
        tabLayout.getTabAt(0);
        tabLayout.getTabAt(1);
        BadgeDrawable badgeDrawable = Objects.requireNonNull(tabLayout.getTabAt(0)).getOrCreateBadge();
        badgeDrawable.setVisible(false);

    }

    @Override
    protected void onResume() {
        super.onResume();
        LocalBroadcastManager.getInstance(this).registerReceiver(syncReceiver, new IntentFilter("com.saneforce.SYNC_COMPLETED"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(syncReceiver);
    }

    private final BroadcastReceiver syncReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            startActivity(new Intent(WeekOffViewScreen.this, MyResource_Activity.class));
            finishAffinity();
        }
    };

}
