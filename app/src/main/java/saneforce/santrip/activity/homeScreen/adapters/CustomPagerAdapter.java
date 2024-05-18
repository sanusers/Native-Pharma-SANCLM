package saneforce.santrip.activity.homeScreen.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import saneforce.santrip.activity.homeScreen.fragment.CallAnalysisFragment;
import saneforce.santrip.activity.homeScreen.fragment.E_DetailingAnalysisFragment;
import saneforce.santrip.activity.homeScreen.fragment.SalesAnalysisFragment;

public class CustomPagerAdapter extends FragmentPagerAdapter {
    private final int NUM_PAGES = 1;

    public CustomPagerAdapter(FragmentManager fm) {
        super(fm,BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @Override
    public Fragment getItem(int position) {

                return new CallAnalysisFragment();
          /*  case 1:
                return new E_DetailingAnalysisFragment();
            case 2:
                return new SalesAnalysisFragment();*/

    }

    @Override
    public int getCount() {
        return NUM_PAGES;
    }
}
