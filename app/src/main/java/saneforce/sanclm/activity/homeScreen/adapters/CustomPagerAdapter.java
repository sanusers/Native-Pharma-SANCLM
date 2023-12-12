package saneforce.sanclm.activity.homeScreen.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import saneforce.sanclm.activity.homeScreen.fragment.CallAnalysisFragment;
import saneforce.sanclm.activity.homeScreen.fragment.E_DetailingAnalysisFragment;
import saneforce.sanclm.activity.homeScreen.fragment.SalesAnalysisFragment;

public class CustomPagerAdapter extends FragmentPagerAdapter {
    private final int NUM_PAGES = 3;

    public CustomPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new CallAnalysisFragment();
            case 1:
                return new E_DetailingAnalysisFragment();
            case 2:
                return new SalesAnalysisFragment();
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}
