package saneforce.sanclm.Activities.Call.DcrCallSelection;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import saneforce.sanclm.Activities.Call.DcrCallSelection.Fragments.ChemistFragment;
import saneforce.sanclm.Activities.Call.DcrCallSelection.Fragments.ListedDoctorFragment;
import saneforce.sanclm.Activities.Call.DcrCallSelection.Fragments.StockiestFragment;
import saneforce.sanclm.Activities.Call.DcrCallSelection.Fragments.UnlistedDoctorFragment;


public class DCRCallSelectionTabLayoutAdapter extends FragmentPagerAdapter {
    Context mContext;
    int mTotalTabs;

    public DCRCallSelectionTabLayoutAdapter(Context context, FragmentManager fragmentManager, int totalTabs) {
        super(fragmentManager);
        mContext = context;
        mTotalTabs = totalTabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        Log.v("getfrag", "--datad--" + position);
        switch (position) {
            case 0:
                return new ListedDoctorFragment();
            case 1:
                return new ChemistFragment();
            case 2:
                return new StockiestFragment();
            case 3:
                return new UnlistedDoctorFragment();
            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return mTotalTabs;
    }

}
