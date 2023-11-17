package saneforce.sanclm.activity.forms.weekoff;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;


public class forms_viewpager extends FragmentPagerAdapter {
    private List<Fragment> fragments = new ArrayList<>();
    private List<String> fragmentTitles = new ArrayList<>();




    public forms_viewpager(@NonNull FragmentManager fm, int i) {
        super(fm);
    }


    public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            fragmentTitles.add(title);
        }        @NonNull
        @Override


		public Fragment getItem(int position) {
            return fragments.get(position);
        }        @Override
        public int getCount() {
            return fragments.size();
        }        //to setup title of the tab layout

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitles.get(position);
        }
    }






