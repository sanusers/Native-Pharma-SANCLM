package saneforce.santrip.activity.myresource.callstatusview;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.badge.BadgeDrawable;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import saneforce.santrip.activity.forms.weekoff.forms_viewpager;
import saneforce.santrip.databinding.ActivityCallsstatusScreenviewBinding;

public class Callsstatus_screenview extends AppCompatActivity {
    forms_viewpager formsviewpager;
    public static String  currentmon="",beforefistmon="",beforesecmon="";
    ActivityCallsstatusScreenviewBinding Callsstatusscreenview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Callsstatusscreenview = ActivityCallsstatusScreenviewBinding.inflate(getLayoutInflater());
        setContentView(Callsstatusscreenview.getRoot());

        Callsstatusscreenview.ivBack.setOnClickListener(v -> {
            finish();
        });

        LocalDate currentDate = LocalDate.now();
        String current_MonthName = currentDate.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
//        System.out.println("Current month: " + current_MonthName);

        // Get the name of the month before two months
        LocalDate before_TwoMonths = currentDate.minusMonths(2);
        String beforeTwo_MonthsName = before_TwoMonths.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
//        System.out.println("Month before two months: " + beforeTwo_MonthsName);

        LocalDate before_OneMonths = currentDate.minusMonths(1);
        String beforeOne_MonthsName = before_OneMonths.getMonth().getDisplayName(TextStyle.FULL, Locale.getDefault());
//        System.out.println("Month before two months: " + beforeOne_MonthsName);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("mm");
        String CurrtDate = df.format(c.getTime());
        System.out.println("Current month in MM format: " + CurrtDate);




        Call_statusfrist_view calls_statusfram = new Call_statusfrist_view();
        Call_status_second_view calls_status_secfram = new Call_status_second_view();
        Call_status_thrid_view calls_status_thraidfram = new Call_status_thrid_view();


        Callsstatusscreenview.tabLayout.setupWithViewPager(Callsstatusscreenview.viewPager);
        //create viewpager adapter
        //here we will create inner class for adapter
        formsviewpager = new forms_viewpager(getSupportFragmentManager(), 0); //add fragments and set the adapter
//        formsviewpager.addFragment(calls_statusfram,"February");

        formsviewpager.addFragment(calls_statusfram,beforeTwo_MonthsName);
        formsviewpager.addFragment(calls_status_secfram, beforeOne_MonthsName);
        formsviewpager.addFragment(calls_status_thraidfram, current_MonthName);//


        Callsstatusscreenview.viewPager.setAdapter(formsviewpager);//set the icons
        Callsstatusscreenview.tabLayout.getTabAt(0);
        Callsstatusscreenview.tabLayout.getTabAt(1);
        BadgeDrawable badgeDrawable = Objects.requireNonNull(Callsstatusscreenview.tabLayout.getTabAt(0)).getOrCreateBadge();
        badgeDrawable.setVisible(false);


    }
}