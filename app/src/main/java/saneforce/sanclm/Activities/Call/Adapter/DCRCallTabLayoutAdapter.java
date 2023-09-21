package saneforce.sanclm.Activities.Call.Adapter;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import saneforce.sanclm.Activities.Call.Fragments.AdditionalCallFragment;
import saneforce.sanclm.Activities.Call.Fragments.DetailedFragment;
import saneforce.sanclm.Activities.Call.Fragments.InputFragment;
import saneforce.sanclm.Activities.Call.Fragments.JWOthersFragment;
import saneforce.sanclm.Activities.Call.Fragments.ProductFragment;
import saneforce.sanclm.Activities.Call.Fragments.RCPAFragment;


public class DCRCallTabLayoutAdapter extends FragmentPagerAdapter {
    Context mContext;
    int mTotalTabs;
    public DCRCallTabLayoutAdapter(Context context , FragmentManager fragmentManager , int totalTabs) {
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
                return new DetailedFragment();
            case 1:
                return new ProductFragment();
            case 2:
                return new InputFragment();
            case 3:
                return new AdditionalCallFragment();
            case 4:
                return new RCPAFragment();
            case 5:
                return new JWOthersFragment();
            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return mTotalTabs;
    }

}
