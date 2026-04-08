package saneforce.sanzen.activity.myresource.setupdetails;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.badge.BadgeDrawable;

import java.util.Objects;

import saneforce.sanzen.R;
import saneforce.sanzen.activity.forms.weekoff.FormsViewpager;
import saneforce.sanzen.commonClasses.SafeClickListener;
import saneforce.sanzen.databinding.ActivitySetupdetailsBinding;

public class SetupDetails extends AppCompatActivity {
    FormsViewpager formsviewpager;
    ActivitySetupdetailsBinding setupdetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupdetails = ActivitySetupdetailsBinding.inflate(getLayoutInflater());
        setContentView(setupdetails.getRoot());

        setupdetails.ivBack.setOnClickListener((new SafeClickListener() {
            @Override
            public void onSafeClick(View view) {
                finish();
            }
        }));
        setupdetails.tagSelection.setText(getString(R.string.setup_details));


        App_setup appSetup = new App_setup();
        Dcr_setup dcrSetup = new Dcr_setup();
        Call_setup callSetup = new Call_setup();
        Tour_plan_setup tourPlanSetup = new Tour_plan_setup();


        setupdetails.tabLayout.setupWithViewPager(setupdetails.viewPager);
        formsviewpager = new FormsViewpager(getSupportFragmentManager(), 0);


        formsviewpager.addFragment(appSetup, getString(R.string.app_setup));
        formsviewpager.addFragment(dcrSetup, getString(R.string.dcr_setup));
        formsviewpager.addFragment(callSetup, getString(R.string.call_setup));
        formsviewpager.addFragment(tourPlanSetup, getString(R.string.tour_plan_setup));


        setupdetails.viewPager.setAdapter(formsviewpager);
        setupdetails.tabLayout.getTabAt(0);
        setupdetails.tabLayout.getTabAt(1);
        BadgeDrawable badgeDrawable = Objects.requireNonNull(setupdetails.tabLayout.getTabAt(0)).getOrCreateBadge();
        badgeDrawable.setVisible(false);

    }


}


