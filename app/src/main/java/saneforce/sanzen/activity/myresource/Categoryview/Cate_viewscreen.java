package saneforce.sanzen.activity.myresource.Categoryview;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.forms.weekoff.forms_viewpager;
import saneforce.sanzen.storage.SharedPref;


public class Cate_viewscreen extends AppCompatActivity {

    ImageView back_btn;
    TabLayout tabLayout;
    ViewPager viewPager;
    forms_viewpager formsviewpager;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cateviewscreen);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);
        back_btn = findViewById(R.id.iv_back);

        back_btn.setOnClickListener(v -> {
            finish();
        });



        Cate_Doctorview cate_Docview = new Cate_Doctorview();
        Cate_Chemistview cate_chemistview = new Cate_Chemistview();

        tabLayout.setupWithViewPager(viewPager);
        formsviewpager = new forms_viewpager(getSupportFragmentManager(), 0);
        if (SharedPref.getDrNeed(this).equalsIgnoreCase("0")){
            formsviewpager.addFragment(cate_Docview,"Doctor");
        }
        if (SharedPref.getChmNeed(this).equalsIgnoreCase("0")){
            formsviewpager.addFragment(cate_chemistview, "Chemist");
        }
        viewPager.setAdapter(formsviewpager);        //set the icons
        tabLayout.getTabAt(0);
        tabLayout.getTabAt(1);
        BadgeDrawable badgeDrawable = Objects.requireNonNull(tabLayout.getTabAt(0)).getOrCreateBadge();
        badgeDrawable.setVisible(false);



    }
}