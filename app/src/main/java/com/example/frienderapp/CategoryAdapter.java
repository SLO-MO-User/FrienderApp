package com.example.frienderapp;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class CategoryAdapter extends FragmentPagerAdapter {
    private String[] tabTitles = new String[]{"Friends", "Nearby", "Location History", "History Map"};

    public CategoryAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        if (position == 0) {
            return new FriendsFragment();
        } else if (position == 1) {
            return new NearbyFragment();
        } else if (position == 2){
            return new LocationHistoryFragment();
        } else {
            return new MapsFragment();
        }
    }

    @Override
    public int getCount() {
        return 4;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }
}