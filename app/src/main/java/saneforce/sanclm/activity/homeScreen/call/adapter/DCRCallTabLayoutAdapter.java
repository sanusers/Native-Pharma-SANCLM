package saneforce.sanclm.activity.homeScreen.call.adapter;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import saneforce.sanclm.activity.homeScreen.call.fragments.AdditionalCallFragment;
import saneforce.sanclm.activity.homeScreen.call.fragments.DetailedFragment;
import saneforce.sanclm.activity.homeScreen.call.fragments.InputFragment;
import saneforce.sanclm.activity.homeScreen.call.fragments.JWOthersFragment;
import saneforce.sanclm.activity.homeScreen.call.fragments.ProductFragment;
import saneforce.sanclm.activity.homeScreen.call.fragments.RCPAFragment;


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
