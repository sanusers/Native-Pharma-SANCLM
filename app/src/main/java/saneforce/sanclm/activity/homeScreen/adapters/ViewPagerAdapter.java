package saneforce.sanclm.activity.homeScreen.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import saneforce.sanclm.activity.homeScreen.fragment.CallAnalysisFragment;
import saneforce.sanclm.activity.homeScreen.fragment.CallsFragment;
import saneforce.sanclm.activity.homeScreen.fragment.E_DetailingAnalysisFragment;
import saneforce.sanclm.activity.homeScreen.fragment.OutboxFragment;
import saneforce.sanclm.activity.homeScreen.fragment.SalesAnalysisFragment;
import saneforce.sanclm.activity.homeScreen.fragment.worktype.WorkPlanFragment;


public class ViewPagerAdapter extends FragmentStateAdapter {
   private final int key;
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, int key) {
        super(fragmentActivity);
        this.key=key;

    }
    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                if (key == 1) {
                    return new WorkPlanFragment();
                } else {
                    return new CallAnalysisFragment();
                }

            case 1:


                if (key == 1) {
                    return new CallsFragment();
                } else {
                    return new E_DetailingAnalysisFragment();
                }


            case 2:

                if (key == 1) {
                    return new OutboxFragment();
                } else {
                    return new SalesAnalysisFragment();
                }


        }
        if (key == 1) {
            return new WorkPlanFragment();
        } else {
            return new CallAnalysisFragment();
        }

    }

    @Override
    public int getItemCount() {
        return 3;
    }

}
