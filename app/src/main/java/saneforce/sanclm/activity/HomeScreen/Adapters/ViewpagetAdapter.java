package saneforce.sanclm.activity.HomeScreen.Adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import saneforce.sanclm.activity.HomeScreen.Fragment.CallAnalysisFragment;
import saneforce.sanclm.activity.HomeScreen.Fragment.CallsFragment;
import saneforce.sanclm.activity.HomeScreen.Fragment.E_DetailingAnalysisFragment;
import saneforce.sanclm.activity.HomeScreen.Fragment.OutboxFragment;
import saneforce.sanclm.activity.HomeScreen.Fragment.SalesAnalysisFragment;
import saneforce.sanclm.activity.HomeScreen.Fragment.WorkPlanFragment;


public class ViewpagetAdapter extends FragmentStateAdapter {
   private int key;
    public ViewpagetAdapter(@NonNull FragmentActivity fragmentActivity,int key) {
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
