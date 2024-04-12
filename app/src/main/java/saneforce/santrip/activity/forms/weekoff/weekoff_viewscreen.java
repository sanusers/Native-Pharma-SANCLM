package saneforce.santrip.activity.forms.weekoff;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import saneforce.santrip.R;

public class weekoff_viewscreen extends AppCompatActivity {

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


        back_btn.setOnClickListener(v -> {
            finish();
        });

        Holiday_fragment holidayfragment = new Holiday_fragment();
        weekoff_fragment weekofffragment = new weekoff_fragment();



        tabLayout.setupWithViewPager(viewPager);
        //create viewpager adapter
        //here we will create inner class for adapter
        formsviewpager  = new forms_viewpager(getSupportFragmentManager(), 0);        //add fragments and set the adapter
        formsviewpager.addFragment(holidayfragment,"Holiday");
        formsviewpager.addFragment(weekofffragment,"Weekly Off");
        viewPager.setAdapter(formsviewpager);        //set the icons
        tabLayout.getTabAt(0);
        tabLayout.getTabAt(1);
        BadgeDrawable badgeDrawable = Objects.requireNonNull(tabLayout.getTabAt(0)).getOrCreateBadge();
        badgeDrawable.setVisible(false);




    }

}
