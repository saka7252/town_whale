package com.esen.together.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.esen.together.fragment.BlankFragment;


public class MainTabPagerAdapter extends FragmentStatePagerAdapter {
    Fragment fragment = null;

    public MainTabPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        // Based upon the position you can call the fragment you need here
        // positions starts from 0
        switch (position) {
            case 0:
                BlankFragment tab1 = new BlankFragment();
                return tab1;

            case 1:
                BlankFragment tab2 = new BlankFragment();
                return tab2;

            case 2:
                BlankFragment tab3 = new BlankFragment();
                return tab3;

            case 3:
                BlankFragment tab4 = new BlankFragment();
                //FirstFragmentPrev tab4 = new FirstFragmentPrev();
                return tab4;

            default:
                return null;
        }
    }


    @Override
    public int getCount() {
        // Returns the number of tabs (If you need 4 tabs change it to 4)
        return 4;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // this is where you set texts to your tabs based upon the position
        // positions starts from 0
        switch (position) {
            case 0:
                return "First";

            case 1:
                return "Second";

            case 2:
                return "Third";

            case 3:
                return "Fouth";

            default:
                return null;
        }
    }
}