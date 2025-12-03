package saneforce.sanzen.activity.forms.birthdayAnniversary;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.forms.weekoff.FormsViewpager;
import saneforce.sanzen.commonClasses.SafeClickListener;

public class BirthdayAnniversaryViewscreen extends AppCompatActivity {
    ImageView back_btn;
    TabLayout tabLayout;
    ViewPager viewPager;
    FormsViewpager formsviewpager;
    TextView heading;
    int tab_pos = 0;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_birthday_anniversary_viewscreen);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        back_btn = findViewById(R.id.iv_back);
        heading = findViewById(R.id.tag_selection);
        heading.setText(getString(R.string.birthday) + "/" + getString(R.string.anniversary));
        back_btn.setOnClickListener(new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                finish();
            }
        });

        BirthdayWishesFragment birthdaywishesFragment = new BirthdayWishesFragment();
        AnniversaryFragment anniversaryFragment = new AnniversaryFragment();

        formsviewpager  = new FormsViewpager(getSupportFragmentManager(), 0);
        formsviewpager.addFragment(birthdaywishesFragment,  getString(R.string.birthday));
        formsviewpager.addFragment(anniversaryFragment, getString(R.string.anniversary));
        viewPager.setAdapter(formsviewpager);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0);
        tabLayout.getTabAt(1);
        BadgeDrawable badgeDrawable = Objects.requireNonNull(tabLayout.getTabAt(0)).getOrCreateBadge();
        badgeDrawable.setVisible(false);
    }

}
